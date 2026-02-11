package br.com.backoff.exponencial.consumers

import br.com.backoff.exponencial.consumers.backoff.ExponentialBackoffAdapter
import br.com.backoff.exponencial.consumers.mappers.toDomain
import br.com.backoff.exponencial.consumers.messages.CheckPaymentMessage
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.Message
import org.springframework.stereotype.Component
import java.util.UUID

// Realizar teste unitário
@Component
class CheckPaymentConsumerSQS(
    private val backoff: ExponentialBackoffAdapter
) {
    @Value("\${aws.sqs.queue.check-payment-name}")
    lateinit var queueName: String

    @SqsListener("\${aws.sqs.queue.check-payment-name}")
    fun consume(messageBody: Message<CheckPaymentMessage>) {
        try {
            logger.info("[ConsumerSQS] Received message headers={} payload={}", messageBody.headers, messageBody.payload)

            val body = messageBody.payload.toDomain()
            UUID.fromString(body.id)

        } catch (ex: Exception) {
            logger.error("[ConsumerSQS] Error processing message", ex)

            backoff.applyBackoff(
                queueName = queueName,
                receiptHandle = messageBody.headers[HEADER_RECEIPT_HANDLE].toString(),
                receiveCount = messageBody.headers[HEADER_RECEIVE_COUNT].toString().toInt()
            )

            throw ex
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CheckPaymentConsumerSQS::class.java)
        private const val HEADER_RECEIVE_COUNT = "Sqs_Msa_ApproximateReceiveCount"
        private const val HEADER_RECEIPT_HANDLE = "Sqs_ReceiptHandle"
    }
}