package br.com.backoff.exponencial.consumers.backoff

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ExponentialBackoffPolicyTest {

    @InjectMocks
    private lateinit var exponentialBackoffPolicy: ExponentialBackoffPolicy

    @ParameterizedTest(name = "attempt={0} -> visibilityTimeout={1}s")
    @CsvSource(
        "1, 30",
        "2, 60",
        "3, 120",
        "4, 240",
        "5, 480",
        "6, 900",
        "7, 900",
        "8, 900",
        "9, 900",
        "10, 900"
    )
    fun `should calculate visibility timeout for attempts 1-10`(attempt: Int, expectedSeconds: Int) {
        val visibilityTimeout = exponentialBackoffPolicy.calculateVisibilityTimeout(attempt)
        assertEquals(expectedSeconds, visibilityTimeout)
    }

    @Test
    fun `should throw IllegalArgumentException for attempt less than 1`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            exponentialBackoffPolicy.calculateVisibilityTimeout(0)
        }
        assertEquals("Attempt must be >= 1", exception.message)
    }
}