package br.com.backoff.exponencial.producers

import br.com.backoff.exponencial.exceptions.SqsPublishException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

@Component
class ProducerSQS(
    private val sqsClient: SqsClient
) {

    @Value("\${aws.sqs.queue.test-name}")
    lateinit var queueName: String

    fun send(messageBody: String) {
        val request = SendMessageRequest.builder()
            .queueUrl(queueName)
            .messageBody(messageBody)
            .build()

        try {
            sqsClient.sendMessage(request)
        } catch (e: SdkClientException) {
            throw SqsPublishException("[ProducerSQS] Failed to publish message to queue: $queueName", e)
        } catch (e: Exception) {
            throw SqsPublishException("[ProducerSQS] Unexpected error publishing message to queue: $queueName", e)
        }
    }
}