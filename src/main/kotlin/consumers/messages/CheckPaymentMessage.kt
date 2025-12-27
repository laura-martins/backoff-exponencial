package br.com.backoff.exponencial.consumers.messages

data class CheckPaymentMessage (
    val paymentId: String
)