package com.ohana.ohanaserver.export.sheets

import com.google.api.services.sheets.v4.model.ValueRange
import com.ohana.ohanaserver.common.crypto.TokenCrypto
import com.ohana.ohanaserver.google.repository.GroupGoogleSheetsConnectionRepository
import com.ohana.ohanaserver.google.service.GoogleOAuthService
import com.ohana.ohanaserver.google.sheets.SheetsClientFactory
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class GoogleSheetsAppender(
    private val connRepo: GroupGoogleSheetsConnectionRepository,
    private val tokenCrypto: TokenCrypto,
    private val oauthService: GoogleOAuthService,
    private val sheetsFactory: SheetsClientFactory
) {
    fun appendRows(groupId: UUID, rows: List<List<Any>>) {
        val conn = connRepo.findByGroupId(groupId) ?: return // 연결 없으면 스킵

        // 1. 토큰 복호화 및 갱신 (가장 중요)
        val refreshToken = tokenCrypto.decrypt(conn.refreshTokenEncrypted)
        val accessToken = oauthService.refreshAccessToken(refreshToken)

        // 2. 시트 API 호출
        val sheets = sheetsFactory.create(accessToken)
        sheets.spreadsheets().values()
            .append(conn.spreadsheetId, "${conn.sheetName}!A1", ValueRange().setValues(rows))
            .setValueInputOption("RAW")
            .setInsertDataOption("INSERT_ROWS") // 기존 데이터 밀어내지 않고 추가
            .execute()
    }
}
