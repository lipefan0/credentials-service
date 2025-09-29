package br.com.upvisibility.credentialsservice.dto.bling

data class TokenResponseDTO(
    val access_token: String,
    val token_type: String,
    val expires_in: Long,
    val refresh_token: String,
    val scope: String
)
