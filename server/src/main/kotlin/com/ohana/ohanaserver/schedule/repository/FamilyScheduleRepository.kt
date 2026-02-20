package com.ohana.ohanaserver.schedule.repository

import com.ohana.ohanaserver.schedule.domain.FamilySchedule
import com.ohana.ohanaserver.schedule.domain.ScheduleStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FamilyScheduleRepository : JpaRepository<FamilySchedule, UUID> {
    // 나에게 온 요청 중 아직 대기 중(PENDING)인 것들 찾기 (앱 알림 뱃지용)
    fun findAllByAssigneeIdAndStatusOrderByCreatedAtDesc(assigneeId: UUID, status: ScheduleStatus): List<FamilySchedule>
}
