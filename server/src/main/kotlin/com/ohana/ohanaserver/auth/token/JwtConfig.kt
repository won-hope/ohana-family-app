package com.ohana.ohanaserver.auth.token

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtConfig(
    @Value("\${ohana.jwt.secret}") private val secret: String,
    private val environment: Environment
) {
    init {
        val isDev = environment.activeProfiles.any { it == "dev" }
        if (!isDev && secret.startsWith("change-me")) {
            throw IllegalStateException("JWT secret must be set via OHANA_JWT_SECRET in non-dev environments.")
        }
        if (secret.length < 32) {
            throw IllegalStateException("JWT secret must be at least 32 characters.")
        }
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        // HS256 decoder for tokens issued by JwtProvider.
        val key = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
        return NimbusJwtDecoder.withSecretKey(key).build()
    }

}
