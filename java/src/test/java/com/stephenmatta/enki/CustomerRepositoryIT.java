package com.stephenmatta.enki;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.stephenmatta.enki.customers.Customer;
import com.stephenmatta.enki.customers.CustomerService;
import com.stephenmatta.enki.customers.Pet;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@SpringBootTest(classes = EnkiApplication.class)
@WebAppConfiguration
@ActiveProfiles({"local", "test"})
@Slf4j
public class CustomerRepositoryIT {

    @Autowired
    DynamoDbClient dynamoDb;

    @Autowired
    CustomerService service;

    private final Faker faker = new Faker();

    @Test
    public void initializeTest() {
        List<Customer> result = service.findAll();
        assertThat(result.size(), is(0));
    }

    @Test
    public void test() {
        var customer = new Customer();
        customer.setId(UUID.randomUUID().toString());
        customer.setFirstName(faker.name().firstName());
        customer.setLastName(faker.name().lastName());
        customer.setEmail(faker.internet().emailAddress());

        var pets = IntStream.range(0, 5).mapToObj(i -> {
            Pet pet = new Pet();

            pet.setId(UUID.randomUUID().toString());

            pet.setName(faker.funnyName().name());
            pet.setAnimalId(faker.numerify("A######"));

            pet.setType(faker.animal().name().toLowerCase());
            pet.setBreed(faker.animal().genus().toLowerCase());
            pet.setMicrochip(faker.number().randomNumber(15, false));

            pet.setBirthdate(faker.timeAndDate().birthday(1, 30));
            pet.setSpayedOrNeutered(faker.bool().bool());
            pet.setVaccineExpirationDate(getRandomDateWithinOneYear());
            pet.setDocumentURL(faker.internet().url());

            return pet;
        }).toList();
        customer.setPets(pets);

        log.debug("{}", pets);

        service.save(customer);

        List<Customer> result = service.findAll();
        result.forEach(c -> log.debug(c.toString()));

        assertThat(result.size(), is(greaterThan(0)));
    }

    @Test
    public void validationTest() {
        // todo: convenience method to get a pre-validated customer, then make it invalid
        var customer = new Customer();
        customer.setLastName("");
        customer.setEmail(faker.internet().emailAddress());
        assertThrows(
            ConstraintViolationException.class,
            () -> service.save(customer),
            "Expected save to throw a ConstraintViolationException, but it didn't"
        );

        customer.setLastName(faker.name().lastName());

        // todo: convenience method to get a pre-validated pet, then make it invalid
        var pet = new Pet();
        customer.setPets(List.of(pet));
        assertThrows(
            ConstraintViolationException.class,
            () -> service.save(customer),
            "Expected save to throw a ConstraintViolationException, but it didn't"
        );
    }

    private boolean tableExists(String tableName) {
        ListTablesRequest listTablesRequest = ListTablesRequest.builder().build();
        ListTablesResponse listTablesResponse = dynamoDb.listTables(listTablesRequest);
        List<String> tableNames = listTablesResponse.tableNames();
        return tableNames.contains(tableName);
    }

    @BeforeEach
    public void setup() {
        if (!tableExists("customer")) {
            dynamoDb.createTable(builder -> builder
                .tableName("customer")
                .keySchema(KeySchemaElement.builder()
                    .attributeName("id")
                    .keyType(KeyType.HASH)
                    .build())
                .attributeDefinitions(AttributeDefinition.builder()
                    .attributeName("id")
                    .attributeType(ScalarAttributeType.S)
                    .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
            );
        }
    }

    @AfterEach
    public void cleanup() {
        dynamoDb.deleteTable(builder -> builder.tableName("customer"));
    }

    private LocalDate getRandomDateWithinOneYear() {
        var now = new Date();

        // Calculate the date one year ago
        var calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.YEAR, -1);
        var oneYearAgo = calendar.toInstant();

        // Calculate the date one year from now
        calendar.setTime(now);
        calendar.add(Calendar.YEAR, 1);
        var oneYearFromNow = calendar.toInstant();

        var instant = faker.timeAndDate().between(oneYearAgo, oneYearFromNow);
        return instant.atZone(ZoneId.of("UTC")).toLocalDate();
    }
}
