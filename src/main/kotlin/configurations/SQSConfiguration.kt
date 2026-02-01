package br.com.backoff.exponencial.configurations

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.SqsClient
import java.net.URI

@Configuration
@Profile("local")
class SQSConfiguration {

    @Value("\${aws.region}")
    lateinit var region: String

    @Value("\${aws.sqs.endpoint}")
    lateinit var endpoint: String

    @Bean
    fun sqsAsyncClient(): SqsAsyncClient =
        SqsAsyncClient.builder()
            .region(Region.of(region))
            .endpointOverride(URI.create(endpoint))
            .build()

    @Bean
    fun sqsClient(): SqsClient =
        SqsClient.builder()
            .region(Region.of(region))
            .endpointOverride(URI.create(endpoint))
            .build()
}