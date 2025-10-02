package br.com.upvisibility.credentialsservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationWebFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    @Value("\${api.keys.bling-service}") private val blingServiceApiKey: String,
    @Value("\${api.keys.shopify-service}") private val shopifyServiceApiKey: String
) : WebFilter {

    // Vamos guardar nossa API Key de serviço aqui por enquanto
    // O ideal é que isso venha de um arquivo de configuração (application.yml)


    private val validServiceApiKeys = listOf(blingServiceApiKey, shopifyServiceApiKey)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val path = request.path.pathWithinApplication().value()

        // Callback público (recebe code + state)
        if (path == "/credentials/bling" && request.queryParams.containsKey("code")) {
            return chain.filter(exchange)
        }

        // Rotas internas de serviço autenticadas por API Key
        if (path.startsWith("/credentials/internal/")) {
            val apiKey = request.headers.getFirst("X-API-Key")
            if (apiKey != null && apiKey in validServiceApiKeys) {
                return chain.filter(exchange)
            }
            return unauthorized(exchange)
        }

        // Demais rotas /credentials/** exigem JWT de usuário
        if (path.startsWith("/credentials/")) {
            val token = getTokenFromRequest(request)
            if (token != null && jwtTokenProvider.validateToken(token)) {
                return chain.filter(exchange)
            }
            return unauthorized(exchange)
        }

        return chain.filter(exchange)
    }

    private fun getTokenFromRequest(request: ServerHttpRequest): String? {
        val bearer = request.headers.getFirst("Authorization") ?: return null
        return if (bearer.startsWith("Bearer ")) bearer.substring(7) else null
    }

    private fun unauthorized(exchange: ServerWebExchange): Mono<Void> {
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        return exchange.response.setComplete()
    }
}
