package com.ohana.ohanaserver.medical.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "의료 기록 (체온, 투약)")
@Entity
@Table(name = "medical_record")
class MedicalRecord(
    @Id
    @Schema(description = "의료 기록 고유 ID")
    val id: UUID = UUID.randomUUID(),

    @Schema(description = "소속된 그룹 ID")
    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Schema(description = "측정 대상 ID")
    @Column(name = "subject_id", nullable = false)
    val subjectId: UUID,

    @Schema(description = "기록 타입")
    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false)
    val recordType: RecordType,

    @Schema(description = "측정/투약 시간")
    @Column(name = "record_time", nullable = false)
    val recordTime: OffsetDateTime,

    @Schema(description = "체온 (℃)")
    @Column(precision = 3, scale = 1)
    val temperature: BigDecimal? = null,

    @Schema(description = "약 이름")
    @Column(name = "medication_name")
    val medicationName: String? = null,

    @Schema(description = "투약량 (ml)")
    @Column(name = "amount_ml", precision = 4, scale = 1)
    val amountMl: BigDecimal? = null,

    @Schema(description = "메모")
    val memo: String? = null,

    @Schema(description = "기록한 사용자 ID")
    @Column(name = "created_by_user_id", nullable = false)
    val createdByUserId: UUID,

    @Schema(description = "생성일")
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)

@Schema(description = "의료 기록 타입 (TEMPERATURE: 체온, MEDICATION: 투약)")
enum class RecordType {
    TEMPERATURE,
    MEDICATION
}
