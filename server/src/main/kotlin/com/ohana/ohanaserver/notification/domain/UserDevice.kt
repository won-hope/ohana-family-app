package com.ohana.ohanaserver.notification.domain

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "user_device")
class UserDevice(
    @Id @Column(name = "user_id") val userId: UUID,
    @Column(name = "fcm_token", nullable = false) var fcmToken: String,
    @Column(name = "updated_at", nullable = false) var updatedAt: OffsetDateTime = OffsetDateTime.now()
) {
    fun updateToken(newToken: String) {
        this.fcmToken = newToken
        this.updatedAt = OffsetDateTime.now()
    }
}
