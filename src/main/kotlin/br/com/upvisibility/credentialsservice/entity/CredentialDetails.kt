package br.com.upvisibility.credentialsservice.entity

import java.time.Instant

sealed interface CredentialDetails {

    data class ApiKeyDetails(
        val apiKey: String
    ) : CredentialDetails

    data class OAuth2Details(
        val accessToken: String,
        val refreshToken: String,
        val expiresIn: Instant?
    ) : CredentialDetails

    data class BasicAuthDetails(
        val username: String,
        val password: String
    ) : CredentialDetails
}