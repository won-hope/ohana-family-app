package com.ohana.ohanaserver.medical.repository

import com.ohana.ohanaserver.medical.domain.MedicalRecord
import com.ohana.ohanaserver.medical.domain.RecordType
import org.springframework.data.jpa.repository.JpaRepository
import java.time.OffsetDateTime
import java.util.UUID

interface MedicalRecordRepository : JpaRepository<MedicalRecord, UUID> {
    
    // 📈 프론트엔드 체온 차트용: 특정 기간의 체온 기록만 시간순(오름차순)으로 조회
    fun findAllBySubjectIdAndRecordTypeAndRecordTimeBetweenOrderByRecordTimeAsc(
        subjectId: UUID,
        recordType: RecordType,
        startTime: OffsetDateTime,
        endTime: OffsetDateTime
    ): List<MedicalRecord>
    
    // 일반 리스트 조회용 (최신순)
    fun findAllBySubjectIdOrderByRecordTimeDesc(subjectId: UUID): List<MedicalRecord>
}
