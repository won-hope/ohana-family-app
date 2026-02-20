package com.ohana.ohanaserver.google.service

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.SpreadsheetProperties
import com.google.api.services.sheets.v4.model.ValueRange
import com.ohana.ohanaserver.common.crypto.TokenCrypto
import com.ohana.ohanaserver.google.domain.GroupGoogleSheetsConnection
import com.ohana.ohanaserver.google.repository.GroupGoogleSheetsConnectionRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

@Service
class GoogleOAuthService(
    @Value("\${ohana.google.client-id}") private val clientId: String,
    @Value("\${ohana.google.client-secret}") private val clientSecret: String, // yml 추가 필요
    @Value("\${ohana.google.redirect-uri}") private val redirectUri: String,   // yml 추가 필요
    private val connRepo: GroupGoogleSheetsConnectionRepository,
    private val tokenCrypto: TokenCrypto
) {

    private val webClient = WebClient.create()
    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val transport = GoogleNetHttpTransport.newTrustedTransport()

    // 1. 동의 URL 생성
    fun buildConsentUrl(state: String): String {
        val scopes = listOf(
            "https://www.googleapis.com/auth/spreadsheets", // 기존 (시트)
            "https://www.googleapis.com/auth/calendar"      // ✅ 신규 (캘린더) 추가!
        ).joinToString(" ")

        val scopeEncoded = URLEncoder.encode(scopes, StandardCharsets.UTF_8)
        val redirectEncoded = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)

        return "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=$clientId" +
                "&redirect_uri=$redirectEncoded" +
                "&response_type=code" +
                "&scope=$scopeEncoded" +
                "&access_type=offline" + // Refresh Token 필수!
                "&prompt=consent" +
                "&state=$state"
    }

    // 2. 콜백 처리 (토큰 교환 -> 시트 생성 -> DB 저장)
    fun processCallback(code: String, groupId: UUID): String {
        // A. 토큰 교환
        // URL Decode code if needed (sometimes it comes encoded)
        val decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8)
        val tokenRes = exchangeCode(decodedCode)
        val refreshToken = tokenRes.refresh_token
            ?: throw IllegalStateException("No refresh token! (prompt=consent 확인)")

        // B. 시트 생성
        val sheets = createSheetsClient(tokenRes.access_token)
        val spreadsheet = sheets.spreadsheets().create(
            Spreadsheet().setProperties(SpreadsheetProperties().setTitle("OHANA Family Log"))
        ).execute()

        val spreadsheetId = spreadsheet.spreadsheetId
        
        // 👇 [수정] 시트 이름을 동적으로 가져오기
        val firstSheetName = spreadsheet.sheets[0].properties.title

        // C. 헤더 추가
        val header = ValueRange().setValues(listOf(listOf("Time", "Subject", "Amount", "Method", "Note", "Who")))
        sheets.spreadsheets().values()
            .append(spreadsheetId, "$firstSheetName!A1", header)
            .setValueInputOption("RAW")
            .execute()

        // D. DB 저장
        connRepo.save(
            GroupGoogleSheetsConnection(
                groupId = groupId,
                googleSub = "unknown",
                spreadsheetId = spreadsheetId,
                sheetName = firstSheetName, // 👇 DB에도 진짜 이름으로 저장!
                refreshTokenEncrypted = tokenCrypto.encrypt(refreshToken),
                scopes = "spreadsheets calendar"
            )
        )

        return spreadsheet.spreadsheetUrl
    }

    // --- Helper Methods ---

    data class TokenResponse(val access_token: String, val refresh_token: String?)

    private fun exchangeCode(code: String): TokenResponse {
        return webClient.post()
            .uri("https://oauth2.googleapis.com/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(
                "code=$code&client_id=$clientId&client_secret=$clientSecret&redirect_uri=$redirectUri&grant_type=authorization_code"
            )
            .retrieve()
            .bodyToMono(TokenResponse::class.java)
            .block()!!
    }

    fun refreshAccessToken(refreshToken: String): TokenResponse {
        return webClient.post()
            .uri("https://oauth2.googleapis.com/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(
                "client_id=$clientId&client_secret=$clientSecret&refresh_token=$refreshToken&grant_type=refresh_token"
            )
            .retrieve()
            .bodyToMono(TokenResponse::class.java)
            .block()!!
    }

    private fun createSheetsClient(accessToken: String): Sheets {
        return Sheets.Builder(transport, jsonFactory) { req ->
            req.headers.setAuthorization("Bearer $accessToken")
        }.setApplicationName("Ohana").build()
    }
}
