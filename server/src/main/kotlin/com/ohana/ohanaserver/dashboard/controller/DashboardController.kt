package com.ohana.ohanaserver.dashboard.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.dashboard.dto.DailySummaryResponse
import com.ohana.ohanaserver.dashboard.dto.TimelineItem
import com.ohana.ohanaserver.dashboard.service.DashboardService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/subjects")
class DashboardController(
    private val dashboardService: DashboardService
) {
    // 통합 타임라인
    @GetMapping("/{subjectId}/timeline")
    fun getTimeline(
        @PathVariable subjectId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): List<TimelineItem> {
        val userId = SecurityUtil.currentUserId()
        return dashboardService.getTimeline(userId, subjectId, date)
    }

    // 데일리 요약 (홈 위젯용)
    @GetMapping("/{subjectId}/summary")
    fun getSummary(
        @PathVariable subjectId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): DailySummaryResponse {
        val userId = SecurityUtil.currentUserId()
        return dashboardService.getSummary(userId, subjectId, date)
    }
}
