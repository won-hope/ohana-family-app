package com.ohana.ohanaserver.google.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class GoogleOAuthService(
    @Value("\${ohana.google.oauth.client-id}") private val clientId: String,
    @Value("\${ohana.google.oauth.client-secret}") private val clientSecret: String,
    @Value("\${ohana.google.oauth.redirect-uri}") private val redirectUri: String
) {
    private val webClient = WebClient.create("https://oauth2.googleapis.com")

    data class TokenResponse(val access_token: String, val refresh_token: String?)

    fun buildConsentUrl(state: String): String {
        return "https://accounts.google.com/o/oauth2/v2/auth" +
            "?client_id=$clientId&redirect_uri=$redirectUri" +
            "&response_type=code&scope=https://www.googleapis.com/auth/spreadsheets" +
            "&access_type=offline&prompt=consent&state=$state"
    }

    fun exchangeCode(code: String): TokenResponse {
        return webClient.post().uri("/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue("code=$code&client_id=$clientId&client_secret=$clientSecret&redirect_uri=$redirectUri&grant_type=authorization_code")
            .retrieve().bodyToMono(TokenResponse::class.java).block()!!
    }

    fun refreshAccessToken(refreshToken: String): String {
        return webClient.post().uri("/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue("refresh_token=$refreshToken&client_id=$clientId&client_secret=$clientSecret&grant_type=refresh_token")
            .retrieve().bodyToMono(TokenResponse::class.java).block()!!.access_token
    }
}
