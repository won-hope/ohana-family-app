package com.ohana.ohanaserver.feeding.repository

import com.ohana.ohanaserver.feeding.domain.FeedingLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.OffsetDateTime
import java.util.*

interface FeedingLogRepository : JpaRepository<FeedingLog, UUID> {
    fun findAllByGroupIdAndFedAtBetweenOrderByFedAtAsc(
        groupId: UUID,
        from: OffsetDateTime,
        to: OffsetDateTime
    ): List<FeedingLog>

    fun findAllBySubjectIdOrderByFedAtDesc(subjectId: UUID): List<FeedingLog>

    fun findBySubjectIdAndIdempotencyKey(subjectId: UUID, idempotencyKey: UUID): FeedingLog?

    @Query(
        """
        select f from FeedingLog f
        where f.subjectId = :subjectId
          and f.fedAt >= :from
          and f.fedAt < :to
        order by f.fedAt desc
    """
    )
    fun findBySubjectAndRange(
        @Param("subjectId") subjectId: UUID,
        @Param("from") from: OffsetDateTime,
        @Param("to") to: OffsetDateTime
    ): List<FeedingLog>

    // ✅ [Step 4 추가] 배치용: 특정 기간 내 미전송 기록 조회
    @Query(
        """
        select f from FeedingLog f
        where f.groupId = :groupId
          and f.fedAt >= :from
          and f.fedAt < :to
          and f.exportedAt is null
        order by f.fedAt asc
    """
    )
    fun findUnexportedInRange(
        @Param("groupId") groupId: UUID,
        @Param("from") from: OffsetDateTime,
        @Param("to") to: OffsetDateTime
    ): List<FeedingLog>

}
