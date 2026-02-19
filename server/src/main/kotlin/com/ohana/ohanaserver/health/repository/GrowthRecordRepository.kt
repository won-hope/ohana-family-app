package com.ohana.ohanaserver.health.repository

import com.ohana.ohanaserver.health.domain.GrowthRecord
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GrowthRecordRepository : JpaRepository<GrowthRecord, UUID> {
    // 최신 기록 1건 가져오기 (날짜 내림차순)
    fun findTop1BySubjectIdOrderByMeasuredDateDesc(subjectId: UUID): GrowthRecord?
}
