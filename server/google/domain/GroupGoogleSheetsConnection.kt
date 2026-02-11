package com.ohana.ohanaserver.google.domain

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "group_google_sheets_connection")
class GroupGoogleSheetsConnection(
    @Id val id: UUID = UUID.randomUUID(),
    @Column(name = "group_id") val groupId: UUID,
    @Column(name = "google_sub") val googleSub: String,
    @Column(name = "spreadsheet_id") val spreadsheetId: String,
    @Column(name = "sheet_name") val sheetName: String,
    @Column(name = "refresh_token_encrypted") var refreshTokenEncrypted: String,
    val scopes: String,
    @Column(name = "updated_at") var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

// Repository
interface GroupGoogleSheetsConnectionRepository : org.springframework.data.jpa.repository.JpaRepository<GroupGoogleSheetsConnection, UUID> {
    fun findByGroupId(groupId: UUID): GroupGoogleSheetsConnection?
}
