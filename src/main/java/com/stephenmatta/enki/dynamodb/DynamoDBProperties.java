package com.stephenmatta.enki.dynamodb;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("enki.dynamodb")
public record DynamoDBProperties(String accessKey,
                                 String secretKey,
                                 String region) {

}
