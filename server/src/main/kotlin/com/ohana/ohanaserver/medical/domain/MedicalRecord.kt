package com.ohana.ohanaserver.medical.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

enum class RecordType {
    TEMPERATURE, // 체온 측정
    MEDICATION   // 해열제 등 투약
}

@Entity
@Table(name = "medical_record")
class MedicalRecord(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Column(name = "subject_id", nullable = false)
    val subjectId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false)
    val recordType: RecordType,

    @Column(name = "record_time", nullable = false)
    val recordTime: OffsetDateTime, // 언제 열을 쟀는지/약을 먹였는지

    @Column(precision = 3, scale = 1)
    val temperature: BigDecimal? = null,

    @Column(name = "medication_name")
    val medicationName: String? = null, // 예: "챔프 시럽(빨강)"

    @Column(name = "amount_ml", precision = 4, scale = 1)
    val amountMl: BigDecimal? = null, // 예: 2.5ml

    val memo: String? = null,

    @Column(name = "created_by_user_id", nullable = false)
    val createdByUserId: UUID,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)
