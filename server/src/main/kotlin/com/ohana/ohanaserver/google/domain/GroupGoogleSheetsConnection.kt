package com.ohana.ohanaserver.google.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "그룹과 구글 계정 연동 정보")
@Entity
@Table(name = "group_google_sheets_connection")
class GroupGoogleSheetsConnection(
    @Id
    @Schema(description = "연동 정보 고유 ID")
    val id: UUID = UUID.randomUUID(),

    @Schema(description = "연동된 그룹 ID")
    @Column(name = "group_id", nullable = false, unique = true)
    val groupId: UUID,

    @Schema(description = "연동된 구글 계정의 고유 식별자 (sub)")
    @Column(name = "google_sub", nullable = false)
    val googleSub: String,

    @Schema(description = "생성된 구글 스프레드시트 ID")
    @Column(name = "spreadsheet_id", nullable = false)
    val spreadsheetId: String,

    @Schema(description = "데이터가 기록될 시트 이름")
    @Column(name = "sheet_name", nullable = false)
    val sheetName: String = "feeding_logs",

    @Schema(description = "암호화된 리프레시 토큰")
    @Column(name = "refresh_token_encrypted", nullable = false)
    var refreshTokenEncrypted: String,

    @Schema(description = "요청한 권한 범위 (스코프)")
    @Column(nullable = false)
    val scopes: String,

    @Schema(description = "연동 시작일")
    @Column(name = "connected_at", nullable = false)
    val connectedAt: OffsetDateTime = OffsetDateTime.now(),

    @Schema(description = "업데이트 시간")
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
)
