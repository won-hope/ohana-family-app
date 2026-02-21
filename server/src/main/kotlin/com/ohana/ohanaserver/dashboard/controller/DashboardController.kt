package com.ohana.ohanaserver.dashboard.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.dashboard.dto.DailySummaryResponse
import com.ohana.ohanaserver.dashboard.dto.TimelineItem
import com.ohana.ohanaserver.dashboard.service.DashboardService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.UUID

@Tag(name = "대시보드", description = "통합 조회 API (타임라인, 요약 등)")
@RestController
@RequestMapping("/subjects")
class DashboardController(
    private val dashboardService: DashboardService
) {
    @Operation(summary = "일별 타임라인 조회", description = "특정 날짜의 모든 기록(수유, 돌봄 등)을 시간순으로 통합 조회합니다.")
    @GetMapping("/{subjectId}/timeline")
    fun getTimeline(
        @PathVariable subjectId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): List<TimelineItem> {
        val userId = SecurityUtil.currentUserId()
        return dashboardService.getTimeline(userId, subjectId, date)
    }

    @Operation(summary = "일별 요약 조회", description = "특정 날짜의 기록들을 요약하여 조회합니다. (총 수유량, 배변 횟수 등)")
    @GetMapping("/{subjectId}/summary")
    fun getSummary(
        @PathVariable subjectId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): DailySummaryResponse {
        val userId = SecurityUtil.currentUserId()
        return dashboardService.getSummary(userId, subjectId, date)
    }
}
