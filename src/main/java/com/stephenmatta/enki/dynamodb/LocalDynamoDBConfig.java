package com.stephenmatta.enki.dynamodb;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

import jakarta.annotation.PreDestroy;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
@EnableConfigurationProperties(DynamoDBProperties.class)
@Profile("local")
@Slf4j
class LocalDynamoDBConfig {

    private final String accessKey;
    private final String secretKey;
    private final String region;

    private LocalStackContainer localstack;

    LocalDynamoDBConfig(DynamoDBProperties properties) {
        this.accessKey = properties.accessKey();
        this.secretKey = properties.secretKey();
        this.region = properties.region();
    }

    @PreDestroy
    public void stopLocalStack() {
        if (localstack != null) {
            localstack.stop();
        }
    }

    @Bean
    public DynamoDbClient dynamoDbClient() throws Exception {
        var endpoint = startLocalDynamoDB();
        var credentials = AwsBasicCredentials.create(accessKey, secretKey);
        var credentialsProvider = StaticCredentialsProvider.create(credentials);

        return DynamoDbClient.builder()
            .httpClientBuilder(ApacheHttpClient.builder())
            .endpointOverride(endpoint)
            .credentialsProvider(credentialsProvider)
            .region(Region.of(region))
            .build();
    }

    private URI startLocalDynamoDB() throws Exception {
        localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3"))
            .withServices(DYNAMODB);
        localstack.start();
        return localstack.getEndpointOverride(DYNAMODB);
    }
}
