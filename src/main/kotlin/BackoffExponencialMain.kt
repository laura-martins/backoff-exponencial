package br.com.backoff.exponencial

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BackoffExponencialApplication

fun main(args: Array<String>) {
    runApplication<BackoffExponencialApplication>(*args)
}