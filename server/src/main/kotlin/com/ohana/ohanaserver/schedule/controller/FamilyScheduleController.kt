package com.ohana.ohanaserver.schedule.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.schedule.domain.FamilySchedule
import com.ohana.ohanaserver.schedule.service.FamilyScheduleService
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import java.util.UUID

@RestController
@RequestMapping("/schedules")
class FamilyScheduleController(
    private val scheduleService: FamilyScheduleService
) {
    data class ProposeRequest(
        val assigneeId: UUID,
        val title: String,
        val description: String?,
        val startTime: OffsetDateTime,
        val endTime: OffsetDateTime
    )

    // 1. 일정 요청 (A가 호출)
    @PostMapping
    fun propose(@RequestBody req: ProposeRequest): FamilySchedule {
        val userId = SecurityUtil.currentUserId()
        return scheduleService.proposeSchedule(
            userId, req.assigneeId, req.title, req.description, req.startTime, req.endTime
        )
    }

    // 2. 나한테 온 알림 목록 (B가 홈 화면에서 확인)
    @GetMapping("/pending")
    fun getPending(): List<FamilySchedule> {
        val userId = SecurityUtil.currentUserId()
        return scheduleService.getPendingRequests(userId)
    }

    // 3. 수락하여 캘린더에 동시 추가 (B가 '추가' 버튼 클릭)
    @PostMapping("/{id}/accept")
    fun accept(@PathVariable id: UUID) {
        val userId = SecurityUtil.currentUserId()
        scheduleService.acceptSchedule(userId, id)
    }
}
