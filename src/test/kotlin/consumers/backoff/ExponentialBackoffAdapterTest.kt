package br.com.backoff.exponencial.consumers.backoff

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityRequest
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse
import java.util.function.Consumer

@ExtendWith(MockitoExtension::class)
class ExponentialBackoffAdapterTest {

    @InjectMocks
    private lateinit var exponentialBackoffAdapter: ExponentialBackoffAdapter

    @Mock
    private lateinit var sqsClient: SqsClient

    @Mock
    private lateinit var backoffPolicy: ExponentialBackoffPolicy

    @Test
    fun `should calculate the timeout, obtain the queueURL, and change the visibility`() {
        // arrange
        val queueName = "check-payment-queue"
        val endpointQueue = "http://sqs.sa-east-1.localhost.localstack.cloud:4566/000000000000/check-payment-queue"
        val receiveCount = 6

        whenever(backoffPolicy.calculateVisibilityTimeout(receiveCount)).thenReturn(15)
        whenever(sqsClient.getQueueUrl(any<Consumer<GetQueueUrlRequest.Builder>>()))
            .thenReturn(GetQueueUrlResponse.builder().queueUrl(endpointQueue).build())

        // act
        exponentialBackoffAdapter.applyBackoff(queueName, receiptHandle = "test-receipt-handle", receiveCount)

        // assert
        verify(backoffPolicy).calculateVisibilityTimeout(receiveCount)
        verify(sqsClient).changeMessageVisibility(any<ChangeMessageVisibilityRequest>())
    }

    @Test
    fun `should not propagate an exception if the backoffPolicy fails`() {
        // arrange
        val queueName = "check-payment-queue"
        val receiveCount = 6

        whenever(backoffPolicy.calculateVisibilityTimeout(receiveCount)).thenThrow(RuntimeException::class.java)

        // act
        exponentialBackoffAdapter.applyBackoff(queueName, receiptHandle = "test-receipt-handle", receiveCount)

        // assert
        verify(backoffPolicy).calculateVisibilityTimeout(receiveCount)
        verify(sqsClient, never()).getQueueUrl(any<Consumer<GetQueueUrlRequest.Builder>>())
        verify(sqsClient, never()).changeMessageVisibility(any<ChangeMessageVisibilityRequest>())
    }

    @Test
    fun `should not propagate an exception if getQueueUrl fails`() {
        // arrange
        val queueName = "check-payment-queue"
        val receiveCount = 6

        whenever(backoffPolicy.calculateVisibilityTimeout(receiveCount)).thenReturn(15)
        whenever(sqsClient.getQueueUrl(any<Consumer<GetQueueUrlRequest.Builder>>()))
            .thenThrow(RuntimeException::class.java)

        // act
        exponentialBackoffAdapter.applyBackoff(queueName, receiptHandle = "test-receipt-handle", receiveCount)

        // assert
        verify(backoffPolicy).calculateVisibilityTimeout(receiveCount)
        verify(sqsClient).getQueueUrl(any<Consumer<GetQueueUrlRequest.Builder>>())
        verify(sqsClient, never()).changeMessageVisibility(any<ChangeMessageVisibilityRequest>())
    }

    @Test
    fun `should not propagate an exception if changeMessageVisibility fails`() {
        // arrange
        val queueName = "check-payment-queue"
        val endpointQueue = "http://sqs.sa-east-1.localhost.localstack.cloud:4566/000000000000/check-payment-queue"
        val receiveCount = 6

        whenever(backoffPolicy.calculateVisibilityTimeout(receiveCount)).thenReturn(15)
        whenever(sqsClient.getQueueUrl(any<Consumer<GetQueueUrlRequest.Builder>>()))
            .thenReturn(GetQueueUrlResponse.builder().queueUrl(endpointQueue).build())
        whenever(sqsClient.changeMessageVisibility(any<ChangeMessageVisibilityRequest>()))
            .thenThrow(RuntimeException::class.java)

        // act
        exponentialBackoffAdapter.applyBackoff(queueName, receiptHandle = "test-receipt-handle", receiveCount)

        // assert
        verify(backoffPolicy).calculateVisibilityTimeout(receiveCount)
        verify(sqsClient).getQueueUrl(any<Consumer<GetQueueUrlRequest.Builder>>())
        verify(sqsClient).changeMessageVisibility(any<ChangeMessageVisibilityRequest>())
    }
}