package com.stephenmatta.enki.dynamodb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDBConfig {

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDb) {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDb)
            .build();
    }
}
