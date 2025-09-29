package br.com.upvisibility.credentialsservice.dto.bling

data class RefreshTokenRequestDTO(
    val grant_type: String = "refresh_token",
    val refresh_token: String
)
