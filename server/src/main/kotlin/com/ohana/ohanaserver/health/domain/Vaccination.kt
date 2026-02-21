package com.ohana.ohanaserver.health.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "예방접종 기록")
@Entity
@Table(name = "vaccination")
class Vaccination(
    @Id
    @Schema(description = "예방접종 기록 고유 ID")
    val id: UUID = UUID.randomUUID(),

    @Schema(description = "소속된 그룹 ID")
    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Schema(description = "접종 대상 ID")
    @Column(name = "subject_id", nullable = false)
    val subjectId: UUID,

    @Schema(description = "백신 타입")
    @Enumerated(EnumType.STRING)
    @Column(name = "vaccine_type", nullable = false)
    val vaccineType: VaccineType,

    @Schema(description = "접종 회차")
    @Column(name = "dose_number", nullable = false)
    val doseNumber: Int,

    @Schema(description = "권장 접종일")
    @Column(name = "scheduled_date", nullable = false)
    var scheduledDate: LocalDate,

    @Schema(description = "실제 접종일")
    @Column(name = "inoculated_date")
    var inoculatedDate: LocalDate? = null,

    @Schema(description = "접종 병원")
    @Column(name = "hospital_name")
    var hospitalName: String? = null,

    @Schema(description = "구글 캘린더 이벤트 ID")
    @Column(name = "google_event_id")
    var googleEventId: String? = null,

    @Schema(description = "기록한 사용자 ID")
    @Column(name = "created_by_user_id", nullable = false)
    val createdByUserId: UUID,

    @Schema(description = "생성일")
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Schema(description = "업데이트 시간")
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
) {
    @Schema(hidden = true)
    fun getStatus(today: LocalDate): String {
        return when {
            inoculatedDate != null -> "COMPLETED" // ✅ 완료
            today.isAfter(scheduledDate) -> "OVERDUE" // 🚨 지연 (빨리 맞아야 함!)
            // 2주(14일) 안으로 다가오면 "임박" 알림
            !today.isAfter(scheduledDate) && today.plusDays(14).isAfter(scheduledDate) -> "UPCOMING" 
            else -> "FUTURE" // 먼 미래
        }
    }
}
