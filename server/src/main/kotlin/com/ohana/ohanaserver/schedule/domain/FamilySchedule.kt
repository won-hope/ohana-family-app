package com.ohana.ohanaserver.schedule.domain

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

enum class ScheduleStatus {
    PENDING,   // 상대방의 수락 대기 중
    ACCEPTED,  // 수락 완료 (캘린더 등록됨)
    REJECTED   // 거절 (시간 안 됨 등)
}

@Entity
@Table(name = "family_schedule")
class FamilySchedule(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Column(name = "creator_id", nullable = false)
    val creatorId: UUID,

    @Column(name = "assignee_id", nullable = false)
    val assigneeId: UUID,

    @Column(nullable = false)
    val title: String,

    val description: String? = null,

    @Column(name = "start_time", nullable = false)
    val startTime: OffsetDateTime,

    @Column(name = "end_time", nullable = false)
    val endTime: OffsetDateTime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ScheduleStatus = ScheduleStatus.PENDING,

    @Column(name = "google_event_id")
    var googleEventId: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
)
