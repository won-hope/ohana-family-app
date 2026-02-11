package com.ohana.ohanaserver.google.controller

import com.google.api.services.sheets.v4.model.*
import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.common.crypto.TokenCrypto
import com.ohana.ohanaserver.google.domain.GroupGoogleSheetsConnection
import com.ohana.ohanaserver.google.repository.GroupGoogleSheetsConnectionRepository
import com.ohana.ohanaserver.google.service.GoogleOAuthService
import com.ohana.ohanaserver.google.sheets.SheetsClientFactory
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/google/sheets/connect")
class GoogleSheetsConnectController(
    private val oauthService: GoogleOAuthService,
    private val tokenCrypto: TokenCrypto,
    private val connRepo: GroupGoogleSheetsConnectionRepository,
    private val sheetsFactory: SheetsClientFactory
) {
    @PostMapping("/start")
    fun start(): Map<String, String> {
        val groupId = SecurityUtil.currentGroupId() // 가정: SecurityUtil에서 그룹ID 추출
        return mapOf("url" to oauthService.buildConsentUrl(groupId.toString()))
    }

    @GetMapping("/callback")
    fun callback(@RequestParam code: String, @RequestParam state: String) {
        val groupId = UUID.fromString(state)
        val tokenRes = oauthService.exchangeCode(code)
        
        // 1. 시트 생성
        val sheets = sheetsFactory.create(tokenRes.access_token)
        val spreadsheet = sheets.spreadsheets().create(Spreadsheet()
            .setProperties(SpreadsheetProperties().setTitle("OHANA Logs")))
            .execute()

        // 2. 헤더 추가
        sheets.spreadsheets().values().append(spreadsheet.spreadsheetId, "Sheet1!A1", 
            ValueRange().setValues(listOf(listOf("Time", "Type", "Amount", "Memo"))))
            .setValueInputOption("RAW").execute()

        // 3. DB 저장 (Refresh Token 암호화)
        val encryptedToken = tokenCrypto.encrypt(tokenRes.refresh_token!!)
        connRepo.save(GroupGoogleSheetsConnection(
            groupId = groupId, 
            googleSub = "me", 
            spreadsheetId = spreadsheet.spreadsheetId,
            sheetName = "Sheet1",
            refreshTokenEncrypted = encryptedToken,
            scopes = "spreadsheets"
        ))
    }
}
