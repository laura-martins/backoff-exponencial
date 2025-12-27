package br.com.backoff.exponencial.consumers

import br.com.backoff.exponencial.consumers.backoff.ExponentialBackoffAdapter
import br.com.backoff.exponencial.consumers.mappers.toDomain
import br.com.backoff.exponencial.consumers.messages.CheckPaymentMessage
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class ConsumerSQS(
    private val backoff: ExponentialBackoffAdapter
) {

    @Value("\${aws.sqs.queue.url.test-name}")
    lateinit var queueName: String

    @SqsListener("\${aws.sqs.queue.test-name}")
    fun consume(
        messageBody: Message<CheckPaymentMessage>
    ) {
        logger.info("[ConsumerSQS] Received message body={}", messageBody)

        try {
            val body = messageBody.payload.toDomain()
            body.id.toInt() // Simulate processing that may fail
        } catch (e: Exception) {
            logger.error("[ConsumerSQS] Error processing message", e)

            val receiptHandle = messageBody.headers["ReceiptHandle"] as String
            val receiveCount = messageBody.headers["ApproximateReceiveCount"] as Int

            backoff.applyBackoff(
                queueName = queueName,
                receiptHandle = receiptHandle,
                receiveCount = receiveCount
            )

            throw e
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ConsumerSQS::class.java)
    }
}