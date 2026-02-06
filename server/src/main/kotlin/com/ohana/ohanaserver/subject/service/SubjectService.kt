package com.ohana.ohanaserver.subject.service

import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import com.ohana.ohanaserver.subject.domain.Subject
import com.ohana.ohanaserver.subject.domain.SubjectType
import com.ohana.ohanaserver.subject.repository.SubjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
@Transactional(readOnly = true)
class SubjectService(
    private val groupMemberRepository: GroupMemberRepository,
    private val subjectRepository: SubjectRepository
) {

    @Transactional
    fun create(
        userId: UUID,
        type: SubjectType,
        name: String,
        birthDate: LocalDate?,
        notes: String?
    ): Subject {
        val groupId = groupMemberRepository
            .findFirstByUserIdOrderByCreatedAtAsc(userId)
            ?.groupId
            ?: throw IllegalStateException("아직 그룹이 없습니다. 그룹을 먼저 생성해주세요.")

        return subjectRepository.save(
            Subject(
                groupId = groupId,
                type = type,
                name = name,
                birthDate = birthDate,
                notes = notes
            )
        )
    }

    fun list(userId: UUID): List<Subject> {
        val groupId = groupMemberRepository
            .findFirstByUserIdOrderByCreatedAtAsc(userId)
            ?.groupId
            ?: throw IllegalStateException("아직 그룹이 없습니다.")

        return subjectRepository.findAllByGroupId(groupId)
    }

    fun get(userId: UUID, subjectId: UUID): Subject {
        val groupId = groupMemberRepository
            .findFirstByUserIdOrderByCreatedAtAsc(userId)
            ?.groupId
            ?: throw IllegalStateException("아직 그룹이 없습니다.")

        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { NoSuchElementException("Subject not found") }

        if (subject.groupId != groupId) {
            throw IllegalAccessException("Forbidden: 본인 그룹의 Subject만 조회할 수 있습니다.")
        }

        return subject
    }
}
