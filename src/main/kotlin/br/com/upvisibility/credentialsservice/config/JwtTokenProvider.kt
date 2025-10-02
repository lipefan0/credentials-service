package br.com.upvisibility.credentialsservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.Base64

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val secret: String
) {
    private val key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))

    fun getEmailFromToken(token: String): String {
        return getClaimsFromToken(token).subject
    }

    fun getClaimsFromToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            return true
        } catch (e: Exception) {
            println(e.message)
        }
        return false
    }
}
