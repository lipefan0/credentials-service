package br.com.upvisibility.credentialsservice.controller

import br.com.upvisibility.credentialsservice.entity.CredentialsEntity
import br.com.upvisibility.credentialsservice.service.CredentialService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/credentials")
class CredentialController(
    private val credentialService: CredentialService
) {

    @GetMapping("/bling")
    suspend fun createBlingCredentials(@RequestParam code: String): ResponseEntity<CredentialsEntity> {
        // For now, let's assume a fixed userId. In a real application, this would come from the authenticated user.
        val userId = "test-user"
        val credentials = credentialService.createBlingCredentials(userId, code)
        return ResponseEntity.status(HttpStatus.CREATED).body(credentials)
    }
}
