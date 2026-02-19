package com.ohana.ohanaserver.health.service

import com.ohana.ohanaserver.google.service.GoogleCalendarService
import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import com.ohana.ohanaserver.health.domain.*
import com.ohana.ohanaserver.health.repository.VaccinationRepository
import com.ohana.ohanaserver.subject.repository.SubjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
class VaccinationService(
    private val vaccinationRepository: VaccinationRepository,
    private val subjectRepository: SubjectRepository,
    private val groupMemberRepository: GroupMemberRepository,
    private val googleCalendarService: GoogleCalendarService
) {
    private fun requireGroupId(userId: UUID): UUID =
        groupMemberRepository.findFirstByUserIdOrderByCreatedAtAsc(userId)?.groupId
            ?: throw IllegalStateException("그룹 없음")

    // 1. 도현이 생일 기준 평생 일정 자동 생성
    @Transactional
    fun initSchedule(userId: UUID, subjectId: UUID) {
        val groupId = requireGroupId(userId)
        val subject = subjectRepository.findById(subjectId).orElseThrow()
        val birthDate = subject.birthDate ?: LocalDate.now()

        VaccinationSchedule.values().forEach { sched ->
            if (!vaccinationRepository.existsBySubjectIdAndVaccineTypeAndDoseNumber(
                    subjectId, sched.vaccine, sched.dose
                )
            ) {
                val plannedDate = birthDate.plusMonths(sched.monthOffset)
                
                // A. 캘린더 이벤트 생성
                val title = "${subject.name} - ${sched.vaccine.koName} ${sched.dose}차"
                val desc = "오하나 예방접종 알림\n권장 시기: 생후 ${sched.monthOffset}개월"
                val eventId = googleCalendarService.createEvent(groupId, title, plannedDate, desc)

                // B. DB 저장 (eventId 포함)
                vaccinationRepository.save(
                    Vaccination(
                        groupId = groupId,
                        subjectId = subjectId,
                        vaccineType = sched.vaccine,
                        doseNumber = sched.dose,
                        scheduledDate = plannedDate,
                        googleEventId = eventId,
                        createdByUserId = userId
                    )
                )
            }
        }
    }

    // 2. 접종 완료 체크 (기존에 맞은 것 체크용)
    @Transactional
    fun markAsCompleted(userId: UUID, vaccinationId: UUID, date: LocalDate, hospital: String?) {
        val vac = vaccinationRepository.findById(vaccinationId).orElseThrow()
        vac.inoculatedDate = date
        vac.hospitalName = hospital
        vac.updatedAt = java.time.OffsetDateTime.now()

        // 📅 캘린더 제목 변경 (💉 -> ✅)
        vac.googleEventId?.let { eventId ->
            val subject = subjectRepository.findById(vac.subjectId).get()
            val newTitle = "✅ [완료] ${subject.name} - ${vac.vaccineType} ${vac.doseNumber}차"
            googleCalendarService.updateEventTitle(vac.groupId, eventId, newTitle)
        }
    }

    // 3. 홈 화면 알림용 (지연되었거나, 2주 내로 다가온 주사들)
    fun getAlerts(userId: UUID, subjectId: UUID): List<Vaccination> {
        val twoWeeksLater = LocalDate.now().plusDays(14)
        // 안 맞은 것 중, (예정일 < 오늘+14일) 인 것들 조회
        return vaccinationRepository.findAllBySubjectIdAndInoculatedDateIsNullAndScheduledDateBeforeOrderByScheduledDateAsc(
            subjectId, twoWeeksLater
        )
    }
    
    // 4. 전체 리스트 (캘린더 뷰용)
    fun list(userId: UUID, subjectId: UUID): List<Vaccination> {
        return vaccinationRepository.findAllBySubjectIdOrderByScheduledDateAsc(subjectId)
    }
}
