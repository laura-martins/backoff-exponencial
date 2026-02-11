package consumers.backoff

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityRequest
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse

class ExponentialBackoffAdapterTest {

    @Test
    fun `applyBackoff deve chamar changeMessageVisibility com visibilityTimeout calculado`() {
        // Mocks
        val sqsClient = mock(SqsClient::class.java)
        val backoffPolicy = mock(ExponentialBackoffPolicy::class.java)

        // Dados do teste
        val queueName = "minha-fila"
        val receiptHandle = "rh-123"
        val receiveCount = 3
        val expectedVisibility = 45
        val expectedQueueUrl = "https://sqs.mock/minha-fila"

        // Stubs
        `when`(backoffPolicy.calculateVisibilityTimeout(receiveCount)).thenReturn(expectedVisibility)
        val getQueueUrlResponse = GetQueueUrlResponse.builder().queueUrl(expectedQueueUrl).build()
        `when`(sqsClient.getQueueUrl(any(GetQueueUrlRequest::class.java))).thenReturn(getQueueUrlResponse)

        // Instancia e execução
        val adapter = ExponentialBackoffAdapter(sqsClient, backoffPolicy)
        adapter.applyBackoff(queueName, receiptHandle, receiveCount)

        // Captura e verificações
        val captor = ArgumentCaptor.forClass(ChangeMessageVisibilityRequest::class.java)
        verify(sqsClient, times(1)).changeMessageVisibility(captor.capture())

        val request = captor.value
        assertEquals(expectedQueueUrl, request.queueUrl())
        assertEquals(receiptHandle, request.receiptHandle())
        assertEquals(expectedVisibility, request.visibilityTimeout())
    }
}