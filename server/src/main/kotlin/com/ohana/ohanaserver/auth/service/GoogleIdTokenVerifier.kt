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
        JwtDecoders.fromOidcIssuerLocation("https://accounts.google.com")

    fun verify(idToken: String): Jwt {
        val jwt = decoder.decode(idToken)

        // Ensure the token was issued for our OAuth client.
        val audiences = jwt.audience.orEmpty()
        require(audiences.contains(googleClientId)) {
            "Invalid audience. aud=$audiences"
        }

        // Caller can read email/sub/etc from the returned Jwt.
        return jwt
    }
}
