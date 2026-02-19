package com.ohana.ohanaserver.health.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.health.domain.Vaccination
import com.ohana.ohanaserver.health.service.VaccinationService
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/health/vaccinations")
class VaccinationController(
    private val vaccinationService: VaccinationService
) {
    // 1. 일정 자동 생성 (최초 1회)
    @PostMapping("/{subjectId}/init")
    fun init(@PathVariable subjectId: UUID) {
        val userId = SecurityUtil.currentUserId()
        vaccinationService.initSchedule(userId, subjectId)
    }

    // 2. 홈 화면 알림 (🚨 빨간불 들어올 리스트)
    @GetMapping("/{subjectId}/alerts")
    fun alerts(@PathVariable subjectId: UUID): List<Vaccination> {
        val userId = SecurityUtil.currentUserId()
        return vaccinationService.getAlerts(userId, subjectId)
    }

    // 3. 전체 일정표 (캘린더)
    @GetMapping("/{subjectId}")
    fun list(@PathVariable subjectId: UUID): List<Vaccination> {
        val userId = SecurityUtil.currentUserId()
        return vaccinationService.list(userId, subjectId)
    }

    // 4. 접종 완료 체크 ("맞았어요!")
    @PostMapping("/{id}/complete")
    fun complete(@PathVariable id: UUID, @RequestBody req: CompleteRequest) {
        val userId = SecurityUtil.currentUserId()
        vaccinationService.markAsCompleted(userId, id, req.date, req.hospital)
    }

    data class CompleteRequest(val date: LocalDate, val hospital: String?)
}
