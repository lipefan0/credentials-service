package br.com.upvisibility.credentialsservice.client

import br.com.upvisibility.credentialsservice.dto.bling.EmpresaBlingResponseDTO
import br.com.upvisibility.credentialsservice.dto.bling.RefreshTokenRequestDTO
import br.com.upvisibility.credentialsservice.dto.bling.TokenRequestDTO
import br.com.upvisibility.credentialsservice.dto.bling.TokenResponseDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class ClientBling(
    private val webClientBuilder: WebClient.Builder,
    @Value("\${bling.api.url}") private val blingApiUrl: String,
    @Value("\${bling.api.client-id}") private val clientId: String,
    @Value("\${bling.api.client-secret}") private val clientSecret: String
) {

    private val webClient = webClientBuilder.baseUrl(blingApiUrl).build()

    private val credentials = java.util.Base64.getEncoder()
        .encodeToString("$clientId:$clientSecret".toByteArray())

    suspend fun generateToken(code: String): TokenResponseDTO {
        return webClient.post()
            .uri("/oauth/token")
            .header("Authorization", "Basic $credentials")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters
                    .fromFormData("grant_type", "authorization_code")
                    .with("code", code)
            )
            .retrieve()
            .awaitBody<TokenResponseDTO>()
    }

    suspend fun refreshToken(refreshToken: String): TokenResponseDTO {
        return webClient.post()
            .uri("/oauth/token")
            .header("Authorization", "Basic $credentials")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters
                    .fromFormData("grant_type", "refresh_token")
                    .with("refresh_token", refreshToken)
            )
            .retrieve()
            .awaitBody<TokenResponseDTO>()
    }

    suspend fun getEmpresaDetails(accessToken: String): EmpresaBlingResponseDTO {
        return webClient.get()
            .uri("/empresas/me/dados-basicos")
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .awaitBody<EmpresaBlingResponseDTO>()
    }

}