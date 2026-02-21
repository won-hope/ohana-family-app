package com.ohana.ohanaserver.auth.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "사용자 정보")
@Entity
@Table(name = "app_user")
class User(
    @Id
    @Schema(description = "사용자 고유 ID")
    val id: UUID = UUID.randomUUID(),

    @Schema(description = "Google 고유 식별자 (sub)")
    @Column(name = "google_sub", nullable = false, unique = true)
    val googleSub: String,

    @Schema(description = "이메일 주소")
    @Column(nullable = false)
    val email: String,

    @Schema(description = "사용자 이름")
    val name: String? = null,

    @Schema(description = "프로필 사진 URL")
    @Column(name = "picture_url")
    val pictureUrl: String? = null,

    @Schema(description = "가입일")
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)
