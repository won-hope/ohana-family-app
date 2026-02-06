package com.ohana.ohanaserver.feeding.repository

import com.ohana.ohanaserver.feeding.domain.FeedingLog
import org.springframework.data.jpa.repository.JpaRepository
import java.time.OffsetDateTime
import java.util.UUID

interface FeedingLogRepository : JpaRepository<FeedingLog, UUID> {
    fun findAllByGroupIdAndFedAtBetweenOrderByFedAtAsc(
        groupId: UUID,
        from: OffsetDateTime,
        to: OffsetDateTime
    ): List<FeedingLog>

    fun findAllBySubjectIdOrderByFedAtDesc(subjectId: UUID): List<FeedingLog>

    fun findBySubjectIdAndIdempotencyKey(subjectId: UUID, idempotencyKey: UUID): FeedingLog?
}
