package br.com.upvisibility.credentialsservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CredentialsServiceApplication

fun main(args: Array<String>) {
    runApplication<CredentialsServiceApplication>(*args)
}
