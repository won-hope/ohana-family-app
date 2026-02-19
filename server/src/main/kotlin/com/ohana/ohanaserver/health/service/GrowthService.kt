package com.ohana.ohanaserver.health.service

import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import com.ohana.ohanaserver.health.domain.GrowthRecord
import com.ohana.ohanaserver.health.domain.GrowthStandard
import com.ohana.ohanaserver.health.repository.GrowthRecordRepository
import com.ohana.ohanaserver.subject.repository.SubjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class GrowthService(
    private val growthRecordRepository: GrowthRecordRepository,
    private val subjectRepository: SubjectRepository,
    private val groupMemberRepository: GroupMemberRepository
) {
    @Transactional
    fun record(
        userId: UUID, subjectId: UUID, date: LocalDate,
        height: Double?, weight: Double?, head: Double?
    ): GrowthRecord {
        val groupMember = groupMemberRepository.findFirstByUserIdOrderByCreatedAtAsc(userId)
            ?: throw IllegalStateException("아직 그룹이 없습니다.")
        val subject = subjectRepository.findById(subjectId).orElseThrow()

        // 1. 생후 개월 수 계산 (표준 비교용)
        val birthDate = subject.birthDate ?: LocalDate.now()
        val months = Period.between(birthDate, date).toTotalMonths().toInt()

        // 2. 체중 분석 (예: "상위 3% (우량아)")
        val desc = if (weight != null) {
            GrowthStandard.analyzeWeight(months, weight)
        } else null

        // 3. 저장
        return growthRecordRepository.save(
            GrowthRecord(
                groupId = groupMember.groupId,
                subjectId = subjectId,
                measuredDate = date,
                height = height?.toBigDecimal(),
                weight = weight?.toBigDecimal(),
                headCircumference = head?.toBigDecimal(),
                summaryDesc = desc,
                createdByUserId = userId
            )
        )
    }

    // ✅ 홈 화면용 한 줄 요약 만들기 ("생후 102일 / 6.5kg (상위 15%)")
    fun getLatestSummaryString(userId: UUID, subjectId: UUID): String? {
        val subject = subjectRepository.findById(subjectId).orElseThrow()
        val record = growthRecordRepository.findTop1BySubjectIdOrderByMeasuredDateDesc(subjectId) 
            ?: return null

        val birthDate = subject.birthDate ?: return null
        // 생후 D+일 계산
        val daysAlive = ChronoUnit.DAYS.between(birthDate, record.measuredDate) + 1 // 태어난 날이 1일

        // 문자열 조합
        val sb = StringBuilder()
        sb.append("생후 ${daysAlive}일")
        
        if (record.weight != null) {
            sb.append(" / ${record.weight}kg")
            if (record.summaryDesc != null) {
                sb.append(" (${record.summaryDesc})")
            }
        } else if (record.height != null) {
            sb.append(" / ${record.height}cm")
        }

        return sb.toString()
    }
}
