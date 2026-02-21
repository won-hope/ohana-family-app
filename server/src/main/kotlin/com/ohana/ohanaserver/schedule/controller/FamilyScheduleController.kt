package com.ohana.ohanaserver.schedule.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.schedule.domain.FamilySchedule
import com.ohana.ohanaserver.schedule.service.FamilyScheduleService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import java.util.UUID

@Tag(name = "일정", description = "가족 공유 일정 관련 API")
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

    @Operation(summary = "일정 제안", description = "다른 가족 구성원에게 일정을 제안합니다.")
    @PostMapping
    fun propose(@RequestBody req: ProposeRequest): FamilySchedule {
        val userId = SecurityUtil.currentUserId()
        return scheduleService.proposeSchedule(
            userId, req.assigneeId, req.title, req.description, req.startTime, req.endTime
        )
    }

    @Operation(summary = "대기중인 일정 조회", description = "나에게 제안된 후 아직 수락/거절하지 않은 일정 목록을 조회합니다.")
    @GetMapping("/pending")
    fun getPending(): List<FamilySchedule> {
        val userId = SecurityUtil.currentUserId()
        return scheduleService.getPendingRequests(userId)
    }

    @Operation(summary = "일정 수락", description = "제안받은 일정을 수락하고 구글 캘린더에 등록합니다.")
    @PostMapping("/{id}/accept")
    fun accept(@PathVariable id: UUID) {
        val userId = SecurityUtil.currentUserId()
        scheduleService.acceptSchedule(userId, id)
    }
}
