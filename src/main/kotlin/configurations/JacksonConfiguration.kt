package br.com.backoff.exponencial.configurations

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MappingJackson2MessageConverter

@Configuration
class JacksonConfig {

    @Bean
    fun objectMapper(): ObjectMapper =
        jacksonObjectMapper().registerKotlinModule()

    @Bean
    fun mappingJackson2MessageConverter(objectMapper: ObjectMapper): MappingJackson2MessageConverter {
        val converter = MappingJackson2MessageConverter()
        converter.objectMapper = objectMapper
        return converter
    }
}