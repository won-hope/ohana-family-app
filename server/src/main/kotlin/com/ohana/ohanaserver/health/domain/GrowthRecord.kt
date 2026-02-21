package com.ohana.ohanaserver.health.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "성장 기록 (키, 몸무게, 머리둘레)")
@Entity
@Table(name = "growth_record")
class GrowthRecord(
    @Id
    @Schema(description = "성장 기록 고유 ID")
    val id: UUID = UUID.randomUUID(),

    @Schema(description = "소속된 그룹 ID")
    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Schema(description = "측정 대상 ID")
    @Column(name = "subject_id", nullable = false)
    val subjectId: UUID,

    @Schema(description = "측정일")
    @Column(name = "measured_date", nullable = false)
    val measuredDate: LocalDate,

    @Schema(description = "키 (cm)")
    @Column(precision = 5, scale = 1)
    val height: BigDecimal? = null,

    @Schema(description = "몸무게 (kg)")
    @Column(precision = 5, scale = 2)
    val weight: BigDecimal? = null,
    
    @Schema(description = "머리둘레 (cm)")
    @Column(name = "head_circumference", precision = 5, scale = 1)
    val headCircumference: BigDecimal? = null,

    @Schema(description = "분석 요약 문구 (예: 상위 15%)")
    @Column(name = "summary_desc")
    val summaryDesc: String? = null,

    @Schema(description = "기록한 사용자 ID")
    @Column(name = "created_by_user_id", nullable = false)
    val createdByUserId: UUID,

    @Schema(description = "생성일")
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Schema(description = "업데이트 시간")
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
)
