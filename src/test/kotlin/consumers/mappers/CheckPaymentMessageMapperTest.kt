package br.com.backoff.exponencial.consumers.mappers

import br.com.backoff.exponencial.consumers.messages.CheckPaymentMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class CheckPaymentMessageMapperTest {

    @Test
    fun `should map Payment to CheckPaymentMessage`() {
        // arrange
        val uuid = UUID.randomUUID().toString()
        val message = CheckPaymentMessage(paymentId = uuid)

        // act
        val payment = message.toDomain()

        // assert
        assertEquals(uuid, payment.id)
    }
}