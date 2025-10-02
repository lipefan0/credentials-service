package br.com.upvisibility.credentialsservice.controller

import br.com.upvisibility.credentialsservice.config.JwtTokenProvider
import br.com.upvisibility.credentialsservice.entity.CredentialsEntity
import br.com.upvisibility.credentialsservice.service.CredentialService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/credentials")
class CredentialController(
    private val credentialService: CredentialService,
    private val jwt: JwtTokenProvider,
    @Value("\${bling.api.client-id}") private val blingClientId: String
) {

    @GetMapping("/bling/authorize")
    fun generateBlingAuthorizeUrl(@RequestHeader("Authorization") authHeader: String): ResponseEntity<Map<String, String>> {
        val token = authHeader.replace("Bearer ", "")

        if (!jwt.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Token inválido"))
        }

        val authorizeUrl = "https://www.bling.com.br/Api/v3/oauth/authorize" +
                "?response_type=code" +
                "&client_id=$blingClientId" +
                "&state=$token"

        return ResponseEntity.ok(mapOf("authorize_url" to authorizeUrl))
    }

    @GetMapping("/bling")
    suspend fun createBlingCredentials(
        @RequestParam code: String,
        @RequestParam state: String
    ): ResponseEntity<CredentialsEntity> {
        if (!jwt.validateToken(state)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val userId = jwt.getClaimsFromToken(state).get("userId", String::class.java)
        val credentials = credentialService.createBlingCredentials(userId, code)
        return ResponseEntity.status(HttpStatus.CREATED).body(credentials)
    }

    @GetMapping("/bling/status")
    suspend fun getBlingCredentialsStatus(@RequestHeader("Authorization") authHeader: String): ResponseEntity<Map<String, Any>> {
        val token = authHeader.replace("Bearer ", "")

        if (!jwt.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Token inválido"))
        }

        val userId = jwt.getClaimsFromToken(token).get("userId", String::class.java)
        val hasCredentials = credentialService.userHasBlingCredentials(userId)

        return ResponseEntity.ok(mapOf("has_credentials" to hasCredentials))
    }

    @GetMapping("/internal/bling/{externalId}")
    suspend fun getBlingCredentialsByExternalId(@PathVariable externalId: String): ResponseEntity<CredentialsEntity> {
        val credentials = credentialService.findCredentialsByExternalId(externalId, "bling")
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        return ResponseEntity.ok(credentials)
    }
}
