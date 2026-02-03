package com.ohana.ohanaserver.group.domain

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "group_member")
class GroupMember(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: GroupRole,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)

enum class GroupRole {
    OWNER, MEMBER
}
