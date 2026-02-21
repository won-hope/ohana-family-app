package com.ohana.ohanaserver.feeding.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Schema(description = "수유 기록")
@Entity
@Table(name = "feeding_log")
class FeedingLog(
    @Id
    @Schema(description = "수유 기록 고유 ID")
    val id: UUID = UUID.randomUUID(),

    @Schema(description = "소속된 그룹 ID")
    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Schema(description = "수유 대상 ID")
    @Column(name = "subject_id", nullable = false)
    val subjectId: UUID,

    @Schema(description = "수유 시간")
    @Column(name = "fed_at", nullable = false)
    val fedAt: OffsetDateTime,

    @Schema(description = "수유량 (ml)")
    @Column(name = "amount_ml")
    val amountMl: Int? = null,

    @Schema(description = "수유 방법")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val method: FeedingMethod,

    @Schema(description = "메모")
    val note: String? = null,

    @Schema(description = "수유 소요 시간 (초)")
    @Column(name = "duration_seconds")
    val durationSeconds: Int? = null,

    @Schema(description = "기록한 사용자 ID")
    @Column(name = "created_by", nullable = false)
    val createdBy: UUID,

    @Schema(description = "멱등성 키")
    @Column(name = "idempotency_key", nullable = false)
    val idempotencyKey: UUID,

    @Schema(description = "생성일")
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Schema(description = "구글 시트 내보내기 완료 시간")
    @Column(name = "exported_at")
    var exportedAt: OffsetDateTime? = null,
)

@Schema(description = "수유 방법 (BOTTLE: 젖병, BREAST: 모유, MIXED: 혼합, SOLIDS: 이유식)")
enum class FeedingMethod {
    BOTTLE, BREAST, MIXED, SOLIDS
}
