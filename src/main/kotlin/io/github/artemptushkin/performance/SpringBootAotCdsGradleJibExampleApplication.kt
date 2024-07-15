package io.github.artemptushkin.performance

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootAotCdsGradleJibExampleApplication

fun main(args: Array<String>) {
    runApplication<SpringBootAotCdsGradleJibExampleApplication>(*args)
}
