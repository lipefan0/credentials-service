package br.com.upvisibility.credentialsservice.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(CredentialAlreadyExistsException::class)
    fun handleCredentialAlreadyExists(ex: CredentialAlreadyExistsException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            message = ex.message ?: "Credential already exists",
            timestamp = System.currentTimeMillis()
        )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    data class ErrorResponse(
        val status: Int,
        val message: String,
        val timestamp: Long
    )
}
