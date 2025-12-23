package br.com.backoff.exponencial.exceptions

class SqsPublishException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)