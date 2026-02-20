package com.ohana.ohanaserver.medical.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.medical.domain.MedicalRecord
import com.ohana.ohanaserver.medical.service.MedicalService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import java.util.UUID

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

    // 🌡️ 체온 기록
    @PostMapping("/temperatures")
    fun recordTemperature(@RequestBody req: TempRequest): MedicalRecord {
        val userId = SecurityUtil.currentUserId()
        return medicalService.recordTemperature(
            userId, req.subjectId, req.recordTime, req.temperature, req.memo
        )
    }

    // 💊 투약 기록
    @PostMapping("/medications")
    fun recordMedication(@RequestBody req: MedRequest): MedicalRecord {
        val userId = SecurityUtil.currentUserId()
        return medicalService.recordMedication(
            userId, req.subjectId, req.recordTime, req.medicationName, req.amountMl, req.memo
        )
    }

    // 📈 그래프 차트 데이터 조회 (GET /medical/subjects/{id}/temperatures/chart?start=...&end=...)
    @GetMapping("/subjects/{subjectId}/temperatures/chart")
    fun getTemperatureChart(
        @PathVariable subjectId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) start: OffsetDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) end: OffsetDateTime
    ): List<MedicalRecord> {
        return medicalService.getTemperatureChartData(subjectId, start, end)
    }
}
