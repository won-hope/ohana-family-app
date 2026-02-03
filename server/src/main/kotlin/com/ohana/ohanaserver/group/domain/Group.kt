package com.ohana.ohanaserver.group.domain

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "app_group")
class Group(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "owner_user_id", nullable = false)
    val ownerUserId: UUID,

    @Column(nullable = false)
    val name: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)
