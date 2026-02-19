package com.ohana.ohanaserver.health.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.health.domain.GrowthRecord
import com.ohana.ohanaserver.health.service.GrowthService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/health/growth")
class GrowthController(
    private val growthService: GrowthService
) {
    data class RecordRequest(
        val subjectId: UUID,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        val date: LocalDate,
        val height: Double?,
        val weight: Double?,
        val head: Double?
    )

    @PostMapping
    fun record(@RequestBody req: RecordRequest): GrowthRecord {
        val userId = SecurityUtil.currentUserId()
        return growthService.record(
            userId, req.subjectId, req.date, req.height, req.weight, req.head
        )
    }

    // 홈 화면용 요약 문구만 달라고 할 때
    @GetMapping("/latest-summary")
    fun getLatestSummary(@RequestParam subjectId: UUID): Map<String, String?> {
        val userId = SecurityUtil.currentUserId()
        val summary = growthService.getLatestSummaryString(userId, subjectId)
        return mapOf("summary" to summary)
    }
}
