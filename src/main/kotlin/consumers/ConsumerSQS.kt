package br.com.backoff.exponencial.consumers

import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.model.Message

@Component
class ConsumerSQS(
    private val backoff: ExponentialBackoffAdapter
) {

    @Value("\${aws.sqs.queue.url.test-name}")
    lateinit var queueUrl: String

    @SqsListener("\${aws.sqs.queue.test-name}")
    fun consume(
        messageBody: Message
    ) {
        try {
            logger.info("[ConsumerSQS] Received message body={}", messageBody)
            val body = messageBody.body()
            body.toInt() // Simulate processing that may fail
        } catch (e: Exception) {
            logger.error("[ConsumerSQS] Error processing message", e)

            val receiptHandle = messageBody.receiptHandle()
            val receiveCount = messageBody.attributesAsStrings()["ApproximateReceiveCount"]
                ?.toInt()

            backoff.applyBackoff(
                queueUrl = queueUrl,
                receiptHandle = receiptHandle,
                receiveCount = requireNotNull(receiveCount)
            )

            throw e
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ConsumerSQS::class.java)
    }
}