package com.ohana.ohanaserver.care.domain

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "care_event")
class CareEvent(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Column(name = "subject_id", nullable = false)
    val subjectId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: CareEventType,

    @Column(name = "occurred_at", nullable = false)
    val occurredAt: OffsetDateTime,

    // ✅ Map을 DB의 JSONB 컬럼과 자동 매핑
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    val payload: Map<String, Any?>? = null,

    @Column(name = "created_by_user_id", nullable = false)
    val createdByUserId: UUID,

    @Column(name = "idempotency_key", nullable = false)
    val idempotencyKey: UUID,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)

enum class CareEventType {
    DIAPER_PEE, // 소변
    DIAPER_POO, // 대변
    BATH,       // 목욕
    TEMP,       // 체온
    SLEEP,      // 수면
    // 나중에 VOMIT(구토), MEDICINE(투약) 등 여기만 추가하면 됨!
}