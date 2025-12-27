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
    @Value("\${aws.sqs.queue.test-name}")
    lateinit var queueName: String

    @SqsListener("\${aws.sqs.queue.test-name}")
    fun consume(messageBody: Message<CheckPaymentMessage>) {
        try {
            logger.info("[ConsumerSQS] Received message headers={} payload={}", messageBody.headers, messageBody.payload)

            val body = messageBody.payload.toDomain()

            // simula falha
            body.id.toInt()

        } catch (ex: Exception) {
            logger.error("[ConsumerSQS] Error processing message", ex)

            val receiveCount = messageBody.headers[HEADER_RECEIVE_COUNT].toString().toInt()
            val receiptHandle = messageBody.headers[HEADER_RECEIPT_HANDLE].toString()

            backoff.applyBackoff(
                queueName = queueName,
                receiptHandle = receiptHandle,
                receiveCount = receiveCount
            )

            throw ex
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ConsumerSQS::class.java)
        private const val HEADER_RECEIVE_COUNT = "Sqs_Msa_ApproximateReceiveCount"
        private const val HEADER_RECEIPT_HANDLE = "Sqs_ReceiptHandle"
    }
}