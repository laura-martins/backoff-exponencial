package br.com.backoff.exponencial.configurations

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import java.net.URI

@Configuration
@Profile("local")
class ConfigurationSQS {

    @Value("\${aws.region}")
    lateinit var region: String

    @Value("\${aws.sqs.endpoint:}")
    lateinit var endpoint: String

    @Value("\${aws.sqs.accessKey}")
    lateinit var accessKey: String

    @Value("\${aws.sqs.secretKey}")
    lateinit var secretKey: String

    private fun credentialsProvider() =
        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))

    @Bean
    fun sqsClient(): SqsClient {
        val builder = SqsClient.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider())

        builder.endpointOverride(URI.create(endpoint))

        return builder.build()
    }
}