package com.ohana.ohanaserver.auth.domain

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "app_user")
class User(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "google_sub", nullable = false, unique = true)
    val googleSub: String,

    @Column(nullable = false)
    val email: String,

    val name: String? = null,

    @Column(name = "picture_url")
    val pictureUrl: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)
