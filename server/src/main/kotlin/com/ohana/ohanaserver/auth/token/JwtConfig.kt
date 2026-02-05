package com.ohana.ohanaserver.auth.token

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtConfig(@Value("\${ohana.jwt.secret}") private val secret: String) {
    @Bean
    fun jwtDecoder(): JwtDecoder {
        // HS256 decoder for tokens issued by JwtProvider.
        val key = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
        return NimbusJwtDecoder.withSecretKey(key).build()
    }

}
