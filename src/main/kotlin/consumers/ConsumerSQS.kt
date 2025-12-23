package br.com.backoff.exponencial.consumers

import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ConsumerSQS {
    private val log = LoggerFactory.getLogger(ConsumerSQS::class.java)

    @SqsListener("\${aws.sqs.queue.test-name}")
    fun consume(messageBody: String) {
        try {
            log.info("[ConsumerSQS] Received message body={}", messageBody)
        } catch (e: Exception) {
            log.error("[ConsumerSQS] Error processing message", e)
            throw e
        }
    }
}