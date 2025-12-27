package br.com.backoff.exponencial.configurations

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
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

    @Value("\${aws.sqs.accessKey}")
    lateinit var accessKey: String

    @Value("\${aws.sqs.secretKey}")
    lateinit var secretKey: String

    @Bean
    fun awsCredentialsProvider(): AwsCredentialsProvider =
        StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        )

    @Bean
    fun sqsAsyncClient(credentialsProvider: AwsCredentialsProvider): SqsAsyncClient =
        SqsAsyncClient.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)
            .endpointOverride(URI.create(endpoint))
            .build()

    @Bean
    fun sqsClient(credentialsProvider: AwsCredentialsProvider): SqsClient =
        SqsClient.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)
            .endpointOverride(URI.create(endpoint))
            .build()
}