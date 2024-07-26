package com.stephenmatta.enki.customers;

import java.util.List;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
class CustomerRepository {

    private final DynamoDbTable<Customer> customerTable;

    CustomerRepository(DynamoDbEnhancedClient dynamoDB) {
        this.customerTable = dynamoDB.table("customer", TableSchema.fromBean(Customer.class));
    }

    public List<Customer> findAll() {
        return customerTable.scan().items().stream().toList();
    }

    public void save(Customer customer) {
        customerTable.putItem(customer);
    }
}
