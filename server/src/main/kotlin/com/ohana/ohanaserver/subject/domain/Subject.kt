package com.ohana.ohanaserver.subject.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "subject")
class Subject(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: SubjectType,

    @Column(nullable = false)
    val name: String,

    @Column(name = "birth_date")
    val birthDate: LocalDate? = null,

    val notes: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)

enum class SubjectType {
    BABY, PET
}
