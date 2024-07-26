package com.stephenmatta.enki.customers;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
@Getter
@Setter
@ToString
public class Pet {

    @NotBlank
    private String id;

    private String animalId;

    private Long microchip;

    private Boolean spayedOrNeutered;

    private String name;

    private String type;

    private String breed;

    private LocalDate birthdate;

    private LocalDate vaccineExpirationDate;

    private String documentURL;
}
