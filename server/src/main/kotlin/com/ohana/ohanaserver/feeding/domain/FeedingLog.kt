package com.ohana.ohanaserver.feeding.domain

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "feeding_log")
class FeedingLog(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Column(name = "subject_id", nullable = false)
    val subjectId: UUID,

    @Column(name = "fed_at", nullable = false)
    val fedAt: OffsetDateTime,

    @Column(name = "amount_ml")
    val amountMl: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val method: FeedingMethod,

    val note: String? = null,

    @Column(name = "duration_seconds")
    val durationSeconds: Int? = null,

    @Column(name = "created_by", nullable = false)
    val createdBy: UUID,

    @Column(name = "idempotency_key", nullable = false)
    val idempotencyKey: UUID,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "exported_at")
    var exportedAt: OffsetDateTime? = null,
)

enum class FeedingMethod {
    BOTTLE, BREAST, MIXED, SOLIDS
}
