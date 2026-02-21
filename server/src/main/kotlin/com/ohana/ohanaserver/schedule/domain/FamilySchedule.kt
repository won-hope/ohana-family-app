package com.ohana.ohanaserver.schedule.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "가족 공유 일정")
@Entity
@Table(name = "family_schedule")
class FamilySchedule(
    @Id
    @Schema(description = "일정 고유 ID")
    val id: UUID = UUID.randomUUID(),

    @Schema(description = "소속된 그룹 ID")
    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Schema(description = "일정 생성자 ID")
    @Column(name = "creator_id", nullable = false)
    val creatorId: UUID,

    @Schema(description = "일정 공유 대상 ID")
    @Column(name = "assignee_id", nullable = false)
    val assigneeId: UUID,

    @Schema(description = "일정 제목")
    @Column(nullable = false)
    val title: String,

    @Schema(description = "일정 상세 설명")
    val description: String? = null,

    @Schema(description = "시작 시간")
    @Column(name = "start_time", nullable = false)
    val startTime: OffsetDateTime,

    @Schema(description = "종료 시간")
    @Column(name = "end_time", nullable = false)
    val endTime: OffsetDateTime,

    @Schema(description = "일정 상태")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ScheduleStatus = ScheduleStatus.PENDING,

    @Schema(description = "구글 캘린더 이벤트 ID")
    @Column(name = "google_event_id")
    var googleEventId: String? = null,

    @Schema(description = "생성일")
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Schema(description = "업데이트 시간")
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

@Schema(description = "일정 상태 (PENDING: 대기, ACCEPTED: 수락, REJECTED: 거절)")
enum class ScheduleStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}
