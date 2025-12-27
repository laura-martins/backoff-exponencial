package br.com.backoff.exponencial.consumers.backoff

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityRequest

@Component
class ExponentialBackoffAdapter(
    private val sqsClient: SqsClient,
    private val backoffPolicy: ExponentialBackoffPolicy
) {

    fun applyBackoff(
        queueName: String,
        receiptHandle: String,
        receiveCount: Int
    ) {
        if (!backoffPolicy.shouldApplyBackoff(receiveCount)) {
            logger.warn("Max receive attempts reached ({}). Skipping backoff — message will be moved to DLQ.", receiveCount)
            return
        }

        try {
            val visibilityTimeout = backoffPolicy.calculateVisibilityTimeout(receiveCount)

            val queueUrl = sqsClient.getQueueUrl { it.queueName(queueName) }.queueUrl()

            val request = ChangeMessageVisibilityRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .visibilityTimeout(visibilityTimeout)
                .build()

            sqsClient.changeMessageVisibility(request)

            logger.warn(
                "Backoff applied | attempt={} | visibilityTimeout={}s | queue={}",
                receiveCount,
                visibilityTimeout,
                queueUrl
            )

        } catch (ex: Exception) {
            logger.error("Failed to apply SQS visibility timeout", ex)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ExponentialBackoffAdapter::class.java)
    }
}
