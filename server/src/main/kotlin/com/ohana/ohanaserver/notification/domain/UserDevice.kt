package com.ohana.ohanaserver.notification.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "사용자 기기 정보 (FCM 푸시용)")
@Entity
@Table(name = "user_device")
class UserDevice(
    @Id
    @Schema(description = "사용자 ID")
    @Column(name = "user_id")
    val userId: UUID,

    @Schema(description = "FCM 푸시 토큰")
    @Column(name = "fcm_token", nullable = false)
    var fcmToken: String,

    @Schema(description = "업데이트 시간")
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
) {
    fun updateToken(newToken: String) {
        this.fcmToken = newToken
        this.updatedAt = OffsetDateTime.now()
    }
}
