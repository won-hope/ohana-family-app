package com.ohana.ohanaserver.health.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "vaccination")
class Vaccination(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Column(name = "subject_id", nullable = false)
    val subjectId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "vaccine_type", nullable = false)
    val vaccineType: VaccineType,

    @Column(name = "dose_number", nullable = false)
    val doseNumber: Int,

    @Column(name = "scheduled_date", nullable = false)
    var scheduledDate: LocalDate,

    @Column(name = "inoculated_date")
    var inoculatedDate: LocalDate? = null, // 완료일 (Not Null이면 완료)

    @Column(name = "hospital_name")
    var hospitalName: String? = null,

    @Column(name = "google_event_id")
    var googleEventId: String? = null,

    @Column(name = "created_by_user_id", nullable = false)
    val createdByUserId: UUID,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
) {
    // 📢 알림 상태 로직 (핵심!)
    // 홈 화면에서 "빨간색/노란색/초록색" 구분할 때 씀
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
