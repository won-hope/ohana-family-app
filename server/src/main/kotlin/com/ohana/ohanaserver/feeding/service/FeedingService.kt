package com.ohana.ohanaserver.feeding.service

import com.ohana.ohanaserver.feeding.domain.FeedingLog
import com.ohana.ohanaserver.feeding.domain.FeedingMethod
import com.ohana.ohanaserver.feeding.repository.FeedingLogRepository
import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import com.ohana.ohanaserver.subject.repository.SubjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
@Transactional(readOnly = true)
class FeedingService(
    private val groupMemberRepository: GroupMemberRepository,
    private val subjectRepository: SubjectRepository,
    private val feedingLogRepository: FeedingLogRepository
) {
    private fun groupIdOf(userId: UUID): UUID =
        groupMemberRepository.findFirstByUserIdOrderByCreatedAtAsc(userId)?.groupId
            ?: throw IllegalStateException("아직 그룹이 없습니다.")

    @Transactional
    fun create(
        userId: UUID,
        subjectId: UUID,
        idempotencyKey: UUID,
        fedAt: OffsetDateTime,
        amountMl: Int?,
        method: FeedingMethod,
        note: String?
    ): FeedingLog {
        val groupId = groupIdOf(userId)

        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { NoSuchElementException("Subject not found") }

        if (subject.groupId != groupId) throw IllegalAccessException("Forbidden subject")

        feedingLogRepository.findBySubjectIdAndIdempotencyKey(subjectId, idempotencyKey)
            ?.let { return it }

        return feedingLogRepository.save(
            FeedingLog(
                groupId = groupId,
                subjectId = subjectId,
                fedAt = fedAt,
                amountMl = amountMl,
                method = method,
                note = note,
                createdBy = userId,
                idempotencyKey = idempotencyKey
            )
        )
    }

    fun listBySubject(userId: UUID, subjectId: UUID): List<FeedingLog> {
        val groupId = groupIdOf(userId)

        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { NoSuchElementException("Subject not found") }

        if (subject.groupId != groupId) throw IllegalAccessException("Forbidden subject")

        return feedingLogRepository.findAllBySubjectIdOrderByFedAtDesc(subjectId)
    }
}
