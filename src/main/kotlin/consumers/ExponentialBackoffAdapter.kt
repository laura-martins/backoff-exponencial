package br.com.backoff.exponencial.consumers

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityRequest

@Component
class ExponentialBackoffAdapter(
    private val sqsAsyncClient: SqsAsyncClient,
    private val backoffPolicy: ExponentialBackoffPolicy
) {

    fun applyBackoff(
        queueUrl: String,
        receiptHandle: String,
        receiveCount: Int
    ) {
        val visibilityTimeout = backoffPolicy.calculateVisibilityTimeout(receiveCount)

        try {
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
