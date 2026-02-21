package com.ohana.ohanaserver.group.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "가족 그룹 정보")
@Entity
@Table(name = "app_group")
class Group(
    @Id
    @Schema(description = "그룹 고유 ID")
    val id: UUID = UUID.randomUUID(),

    @Schema(description = "그룹 소유자(최초 생성자)의 사용자 ID")
    @Column(name = "owner_user_id", nullable = false)
    val ownerUserId: UUID,

    @Schema(description = "그룹 이름")
    @Column(nullable = false)
    val name: String,

    @Schema(description = "생성일")
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)
