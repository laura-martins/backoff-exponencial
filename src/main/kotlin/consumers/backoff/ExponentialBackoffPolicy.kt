package br.com.backoff.exponencial.consumers.backoff

import org.springframework.stereotype.Component

@Component
class ExponentialBackoffPolicy(
    private val baseVisibilityTimeout: Long = 30,
    private val maxVisibilityTimeout: Long = 15 * 60
) {

    fun calculateVisibilityTimeout(attempt: Int): Int {
        require(attempt >= 1) { "Attempt must be >= 1" }

        val exponentialDelay = baseVisibilityTimeout * (1L shl (attempt - 1))
        return minOf(exponentialDelay, maxVisibilityTimeout).toInt()
    }
}
