package br.com.backoff.exponencial.configurations

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import java.net.URI

@Configuration
class ConfigurationSQS(
    @get:Value("\${aws.region:us-east-1}")
    private val region: String,

    @get:Value("\${aws.sqs.endpoint:}")
    private val endpoint: String
) {

    @Bean
    fun sqsClient(): SqsClient {
        val builder = SqsClient.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())

        builder.endpointOverride(URI.create(endpoint))

        return builder.build()
    }
}