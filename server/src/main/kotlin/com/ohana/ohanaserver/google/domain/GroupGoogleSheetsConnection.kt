package com.ohana.ohanaserver.google.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "group_google_sheets_connection")
class GroupGoogleSheetsConnection(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "group_id", nullable = false, unique = true)
    val groupId: UUID,

    @Column(name = "google_sub", nullable = false)
    val googleSub: String,

    @Column(name = "spreadsheet_id", nullable = false)
    val spreadsheetId: String,

    @Column(name = "sheet_name", nullable = false)
    val sheetName: String = "feeding_logs",

    @Column(name = "refresh_token", nullable = false)
    var refreshTokenEncrypted: String,

    @Column(nullable = false)
    val scopes: String,

    @Column(name = "connected_at", nullable = false)
    val connectedAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
)
