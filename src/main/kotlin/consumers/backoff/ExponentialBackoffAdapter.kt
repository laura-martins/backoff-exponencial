package br.com.backoff.exponencial.consumers.backoff

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityRequest

@Component
class ExponentialBackoffAdapter(
    private val sqsAsyncClient: SqsClient,
    private val backoffPolicy: ExponentialBackoffPolicy
) {

    fun applyBackoff(
        queueName: String,
        receiptHandle: String,
        receiveCount: Int
    ) {
        val visibilityTimeout = backoffPolicy.calculateVisibilityTimeout(receiveCount)

        try {
            val queueUrl = sqsAsyncClient.getQueueUrl { it.queueName(queueName) }.queueUrl()

            val req = ChangeMessageVisibilityRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .visibilityTimeout(visibilityTimeout)
                .build()

            sqsAsyncClient.changeMessageVisibility(req)

            logger.warn("SQS retry applied | queue=$queueUrl | attempt=$receiveCount | visibilityTimeout=${visibilityTimeout}s")
        } catch (ex: Exception) {
            logger.error("Failed to change message visibility", ex)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ExponentialBackoffAdapter::class.java)
    }
}
