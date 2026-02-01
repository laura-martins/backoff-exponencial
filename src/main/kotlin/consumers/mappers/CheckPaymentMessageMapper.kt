package br.com.backoff.exponencial.consumers.mappers

import br.com.backoff.exponencial.consumers.messages.CheckPaymentMessage
import br.com.backoff.exponencial.domains.Payment

// Realizar teste unitário
fun CheckPaymentMessage.toDomain(): Payment {
    return Payment(
        id = this.paymentId
    )
}