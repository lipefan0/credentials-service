package br.com.upvisibility.credentialsservice.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
class CredentialAlreadyExistsException(message: String) : RuntimeException(message)
