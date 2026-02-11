package consumers.backoff

import br.com.backoff.exponencial.consumers.backoff.ExponentialBackoffAdapter
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ExponentialBackoffAdapterTest {

    @InjectMocks
    private lateinit var exponentialBackoffAdapter: ExponentialBackoffAdapter
}