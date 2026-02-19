package com.ohana.ohanaserver.health.repository

import com.ohana.ohanaserver.health.domain.Vaccination
import com.ohana.ohanaserver.health.domain.VaccineType
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.UUID

interface VaccinationRepository : JpaRepository<Vaccination, UUID> {
    
    // 전체 일정 (날짜순) - 캘린더 화면용
    fun findAllBySubjectIdOrderByScheduledDateAsc(subjectId: UUID): List<Vaccination>
    
    // 알림용 (아직 안 맞았고, 날짜가 임박했거나 지난 것들)
    fun findAllBySubjectIdAndInoculatedDateIsNullAndScheduledDateBeforeOrderByScheduledDateAsc(
        subjectId: UUID, 
        limitDate: LocalDate // 오늘 + 14일
    ): List<Vaccination>

    // 중복 방지용
    fun existsBySubjectIdAndVaccineTypeAndDoseNumber(subjectId: UUID, type: VaccineType, dose: Int): Boolean
}
