package com.ohana.ohanaserver.care.repository

import com.ohana.ohanaserver.care.domain.CareEvent
import com.ohana.ohanaserver.care.domain.CareEventType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.OffsetDateTime
import java.util.*

interface CareEventRepository : JpaRepository<CareEvent, UUID> {

    // 멱등성 체크용
    fun findBySubjectIdAndIdempotencyKey(subjectId: UUID, idempotencyKey: UUID): CareEvent?

    // 하루치 타임라인 조회 (시간순 정렬)
    @Query(
        """
        select e from CareEvent e
        where e.subjectId = :subjectId
          and e.occurredAt >= :from
          and e.occurredAt < :to
        order by e.occurredAt desc
    """
    )
    fun findBySubjectAndRange(
        @Param("subjectId") subjectId: UUID,
        @Param("from") from: OffsetDateTime,
        @Param("to") to: OffsetDateTime
    ): List<CareEvent>

    // 특정 이벤트의 최신 기록 (예: 마지막 목욕)
    fun findTop1BySubjectIdAndTypeOrderByOccurredAtDesc(
        subjectId: UUID,
        type: CareEventType
    ): CareEvent?
}