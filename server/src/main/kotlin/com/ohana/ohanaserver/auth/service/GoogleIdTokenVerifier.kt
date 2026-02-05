package com.ohana.ohanaserver.auth.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.stereotype.Component

@Component
class GoogleIdTokenVerifier(
    @Value("\${ohana.google.client-id}") private val googleClientId: String
) {
    private val decoder: JwtDecoder =
        JwtDecoders.fromIssuerLocation("https://accounts.google.com")
    fun verify(idToken: String): Jwt {
        val jwt = decoder.decode(idToken)

        // ✅ aud 체크 (내 OAuth Client ID가 맞는지)
        val audiences = jwt.audience.orEmpty()
        require(audiences.contains(googleClientId)) {
            "Invalid audience. aud=$audiences"
        }

        // 이메일/서브는 여기서 꺼내면 됨
        return jwt
    }
}
