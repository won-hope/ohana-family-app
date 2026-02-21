package com.ohana.ohanaserver.group.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "그룹 멤버 정보")
@Entity
@Table(name = "group_member")
class GroupMember(
    @Id
    @Schema(description = "그룹 멤버 고유 ID")
    val id: UUID = UUID.randomUUID(),

    @Schema(description = "소속된 그룹 ID")
    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Schema(description = "사용자 ID")
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Schema(description = "그룹 내 역할")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: GroupRole,

    @Schema(description = "가입일")
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)

@Schema(description = "그룹 멤버 역할 (OWNER: 소유자, MEMBER: 멤버)")
enum class GroupRole {
    OWNER, MEMBER
}
