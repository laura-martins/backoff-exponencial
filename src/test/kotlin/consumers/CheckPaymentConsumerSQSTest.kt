package br.com.backoff.exponencial.consumers

import br.com.backoff.exponencial.consumers.backoff.ExponentialBackoffAdapter
import br.com.backoff.exponencial.consumers.messages.CheckPaymentMessage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.springframework.messaging.Message
import org.springframework.messaging.support.GenericMessage

@ExtendWith(MockitoExtension::class)
class CheckPaymentConsumerSQSTest {

    @InjectMocks
    private lateinit var checkPaymentConsumerSQS: CheckPaymentConsumerSQS

    @Mock
    private lateinit var exponentialBackoffAdapter: ExponentialBackoffAdapter

    @Test
    fun `should process message successfully when payload is valid`() {
        // arrange
        val payload = CheckPaymentMessage(paymentId = "123e4567-e89b-12d3-a456-426614174000")
        val headers = emptyMap<String, Any>()
        val messageBody: Message<CheckPaymentMessage> = GenericMessage(payload, headers)

        // act
        checkPaymentConsumerSQS.consume(messageBody)

        // assert
        verify(exponentialBackoffAdapter, never()).applyBackoff(any(), any(), any())
    }

    @Test
    fun `should apply backoff when an exception is thrown during message processing`() {
        // arrange
        checkPaymentConsumerSQS.queueName = "test-queue"
        val receiptHandle = "test-receipt-handle"
        val receiveCount = 3

        val payload = CheckPaymentMessage(paymentId = "not-a-uuid")
        val headers = mapOf(
            "Sqs_ReceiptHandle" to receiptHandle,
            "Sqs_Msa_ApproximateReceiveCount" to receiveCount
        )
        val messageBody: Message<CheckPaymentMessage> = GenericMessage(payload, headers)

        // act
        assertThrows<IllegalArgumentException> {
            checkPaymentConsumerSQS.consume(messageBody)
        }

        // assert
        verify(exponentialBackoffAdapter).applyBackoff(
            queueName = "test-queue",
            receiptHandle = receiptHandle,
            receiveCount = receiveCount
        )
    }
}