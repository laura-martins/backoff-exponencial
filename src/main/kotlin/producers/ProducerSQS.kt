package br.com.backoff.exponencial.producers

import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import software.amazon.awssdk.core.exception.SdkException

@Component
class ProducerSQS(
    private val sqsClient: SqsClient
) {

    fun send(queueUrl: String, messageBody: String) {
        val request = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody(messageBody)
            .build()

        try {
            sqsClient.sendMessage(request)
        } catch (e: SdkException) {
            throw SqsPublishException("Falha ao publicar mensagem na fila: $queueUrl", e)
        } catch (e: Exception) {
            throw SqsPublishException("Erro inesperado ao publicar mensagem na fila: $queueUrl", e)
        }
    }
}