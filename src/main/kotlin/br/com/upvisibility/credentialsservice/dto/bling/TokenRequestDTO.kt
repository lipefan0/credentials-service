package br.com.upvisibility.credentialsservice.dto.bling

data class TokenRequestDTO(
    val grant_type: String = "authorization_code",
    val code: String
)