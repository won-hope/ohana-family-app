package com.ohana.ohanaserver.finance.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "월별 예산")
@Entity
@Table(name = "monthly_budget")
class MonthlyBudget(
    @Id
    @Schema(description = "월별 예산 고유 ID")
    val id: UUID = UUID.randomUUID(),

    @Schema(description = "소속된 그룹 ID")
    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Schema(description = "대상 연월 (YYYY-MM)")
    @Column(name = "year_month", nullable = false)
    val yearMonth: String,

    @Schema(description = "목표 예산 금액")
    @Column(name = "target_amount", nullable = false)
    var targetAmount: Long,

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
