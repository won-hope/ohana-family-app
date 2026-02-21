package com.ohana.ohanaserver.health.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.health.domain.GrowthRecord
import com.ohana.ohanaserver.health.service.GrowthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.UUID

@Tag(name = "건강 (성장)", description = "성장(키, 몸무게 등) 기록 관련 API")
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

    @Operation(summary = "성장 기록 등록", description = "특정 날짜의 키, 몸무게, 머리둘레를 기록합니다.")
    @PostMapping
    fun record(@RequestBody req: RecordRequest): GrowthRecord {
        val userId = SecurityUtil.currentUserId()
        return growthService.record(
            userId, req.subjectId, req.date, req.height, req.weight, req.head
        )
    }

    @Operation(summary = "최신 성장 요약 조회", description = "가장 최근의 성장 기록을 바탕으로 요약 문구를 생성하여 반환합니다.")
    @GetMapping("/latest-summary")
    fun getLatestSummary(@RequestParam subjectId: UUID): Map<String, String?> {
        val userId = SecurityUtil.currentUserId()
        val summary = growthService.getLatestSummaryString(userId, subjectId)
        return mapOf("summary" to summary)
    }
}
