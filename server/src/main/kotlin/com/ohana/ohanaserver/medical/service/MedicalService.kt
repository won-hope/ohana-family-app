package com.ohana.ohanaserver.medical.service

import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import com.ohana.ohanaserver.medical.domain.MedicalRecord
import com.ohana.ohanaserver.medical.domain.RecordType
import com.ohana.ohanaserver.medical.repository.MedicalRecordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Service
class MedicalService(
    private val medicalRecordRepository: MedicalRecordRepository,
    private val groupMemberRepository: GroupMemberRepository
) {
    private fun requireGroupId(userId: UUID): UUID =
        groupMemberRepository.findFirstByUserIdOrderByCreatedAtAsc(userId)?.groupId
            ?: throw IllegalStateException("그룹을 찾을 수 없습니다.")

    // 1. 체온 기록하기
    @Transactional
    fun recordTemperature(
        userId: UUID, subjectId: UUID, time: OffsetDateTime, temp: Double, memo: String?
    ): MedicalRecord {
        return medicalRecordRepository.save(
            MedicalRecord(
                groupId = requireGroupId(userId),
                subjectId = subjectId,
                recordType = RecordType.TEMPERATURE,
                recordTime = time,
                temperature = BigDecimal.valueOf(temp),
                memo = memo,
                createdByUserId = userId
            )
        )
    }

    // 2. 투약 기록하기
    @Transactional
    fun recordMedication(
        userId: UUID, subjectId: UUID, time: OffsetDateTime, 
        name: String, amount: Double, memo: String?
    ): MedicalRecord {
        return medicalRecordRepository.save(
            MedicalRecord(
                groupId = requireGroupId(userId),
                subjectId = subjectId,
                recordType = RecordType.MEDICATION,
                recordTime = time,
                medicationName = name,
                amountMl = BigDecimal.valueOf(amount),
                memo = memo,
                createdByUserId = userId
            )
        )
    }

    // 3. 체온 차트 데이터 내려주기
    fun getTemperatureChartData(
        subjectId: UUID, start: OffsetDateTime, end: OffsetDateTime
    ): List<MedicalRecord> {
        return medicalRecordRepository.findAllBySubjectIdAndRecordTypeAndRecordTimeBetweenOrderByRecordTimeAsc(
            subjectId, RecordType.TEMPERATURE, start, end
        )
    }
}
