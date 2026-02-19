package com.ohana.ohanaserver.health.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "growth_record")
class GrowthRecord(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Column(name = "subject_id", nullable = false)
    val subjectId: UUID,

    @Column(name = "measured_date", nullable = false)
    val measuredDate: LocalDate,

    @Column(precision = 5, scale = 1)
    val height: BigDecimal? = null,

    @Column(precision = 5, scale = 2)
    val weight: BigDecimal? = null,
    
    @Column(name = "head_circumference", precision = 5, scale = 1)
    val headCircumference: BigDecimal? = null,

    @Column(name = "summary_desc")
    val summaryDesc: String? = null, // "상위 15%" 저장

    @Column(name = "created_by_user_id", nullable = false)
    val createdByUserId: UUID,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
)
