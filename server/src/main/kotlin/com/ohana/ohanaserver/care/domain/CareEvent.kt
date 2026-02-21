package com.ohana.ohanaserver.care.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.*

@Schema(description = "돌봄 이벤트 기록")
@Entity
@Table(name = "care_event")
class CareEvent(
    @Id
    @Schema(description = "돌봄 이벤트 고유 ID")
    val id: UUID = UUID.randomUUID(),

    @Schema(description = "소속된 그룹 ID")
    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Schema(description = "돌봄 대상 ID")
    @Column(name = "subject_id", nullable = false)
    val subjectId: UUID,

    @Schema(description = "돌봄 이벤트 타입")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: CareEventType,

    @Schema(description = "이벤트 발생 시간")
    @Column(name = "occurred_at", nullable = false)
    val occurredAt: OffsetDateTime,

    @Schema(description = "이벤트 상세 정보 (JSON)")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    val payload: Map<String, Any?>? = null,

    @Schema(description = "기록한 사용자 ID")
    @Column(name = "created_by_user_id", nullable = false)
    val createdByUserId: UUID,

    @Schema(description = "멱등성 키")
    @Column(name = "idempotency_key", nullable = false)
    val idempotencyKey: UUID,

    @Schema(description = "생성일")
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)

@Schema(description = "돌봄 이벤트 타입 (DIAPER_PEE: 소변, DIAPER_POO: 대변, BATH: 목욕, TEMP: 체온, SLEEP: 수면)")
enum class CareEventType {
    DIAPER_PEE, // 소변
    DIAPER_POO, // 대변
    BATH,       // 목욕
    TEMP,       // 체온
    SLEEP,      // 수면
}
