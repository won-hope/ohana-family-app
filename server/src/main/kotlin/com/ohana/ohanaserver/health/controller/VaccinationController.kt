package com.ohana.ohanaserver.health.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.health.domain.Vaccination
import com.ohana.ohanaserver.health.service.VaccinationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.UUID

@Tag(name = "건강 (예방접종)", description = "예방접종 일정 관련 API")
@RestController
@RequestMapping("/health/vaccinations")
class VaccinationController(
    private val vaccinationService: VaccinationService
) {
    @Operation(summary = "예방접종 일정 자동 생성", description = "아이의 생년월일을 기준으로 국가 필수 예방접종 전체 일정을 생성합니다. (최초 1회 호출)")
    @PostMapping("/{subjectId}/init")
    fun init(@PathVariable subjectId: UUID) {
        val userId = SecurityUtil.currentUserId()
        vaccinationService.initSchedule(userId, subjectId)
    }

    @Operation(summary = "예방접종 알림 목록 조회", description = "접종일이 지났거나 2주 내로 다가온 미접종 항목 목록을 조회합니다.")
    @GetMapping("/{subjectId}/alerts")
    fun alerts(@PathVariable subjectId: UUID): List<Vaccination> {
        val userId = SecurityUtil.currentUserId()
        return vaccinationService.getAlerts(userId, subjectId)
    }

    @Operation(summary = "전체 예방접종 일정 조회", description = "캘린더 등에 표시할 전체 예방접종 일정 목록을 조회합니다.")
    @GetMapping("/{subjectId}")
    fun list(@PathVariable subjectId: UUID): List<Vaccination> {
        val userId = SecurityUtil.currentUserId()
        return vaccinationService.list(userId, subjectId)
    }

    @Operation(summary = "예방접종 완료 처리", description = "특정 예방접종 항목을 완료 처리합니다.")
    @PostMapping("/{id}/complete")
    fun complete(@PathVariable id: UUID, @RequestBody req: CompleteRequest) {
        val userId = SecurityUtil.currentUserId()
        vaccinationService.markAsCompleted(userId, id, req.date, req.hospital)
    }

    data class CompleteRequest(val date: LocalDate, val hospital: String?)
}
