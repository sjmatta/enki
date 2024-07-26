package com.stephenmatta.enki.customers;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@Data
public class Customer {

    @NotBlank
    @Getter(onMethod_ = @DynamoDbPartitionKey)
    private String id;

    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String email;

    @Valid
    private List<Pet> pets;
}
