package com.ohana.ohanaserver.medical.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.medical.domain.MedicalRecord
import com.ohana.ohanaserver.medical.service.MedicalService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import java.util.UUID

@Tag(name = "의료", description = "체온/투약 기록 관련 API")
@RestController
@RequestMapping("/medical")
class MedicalController(
    private val medicalService: MedicalService
) {
    data class TempRequest(
        val subjectId: UUID,
        val recordTime: OffsetDateTime,
        val temperature: Double,
        val memo: String?
    )

    data class MedRequest(
        val subjectId: UUID,
        val recordTime: OffsetDateTime,
        val medicationName: String,
        val amountMl: Double,
        val memo: String?
    )

    @Operation(summary = "체온 기록", description = "체온 측정 결과를 기록합니다.")
    @PostMapping("/temperatures")
    fun recordTemperature(@RequestBody req: TempRequest): MedicalRecord {
        val userId = SecurityUtil.currentUserId()
        return medicalService.recordTemperature(
            userId, req.subjectId, req.recordTime, req.temperature, req.memo
        )
    }

    @Operation(summary = "투약 기록", description = "해열제 등 약 투약 기록을 남깁니다.")
    @PostMapping("/medications")
    fun recordMedication(@RequestBody req: MedRequest): MedicalRecord {
        val userId = SecurityUtil.currentUserId()
        return medicalService.recordMedication(
            userId, req.subjectId, req.recordTime, req.medicationName, req.amountMl, req.memo
        )
    }

    @Operation(summary = "체온 그래프 데이터 조회", description = "특정 기간의 체온 기록을 차트용으로 조회합니다.")
    @GetMapping("/subjects/{subjectId}/temperatures/chart")
    fun getTemperatureChart(
        @PathVariable subjectId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) start: OffsetDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) end: OffsetDateTime
    ): List<MedicalRecord> {
        return medicalService.getTemperatureChartData(subjectId, start, end)
    }
}
