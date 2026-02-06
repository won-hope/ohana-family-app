package com.ohana.ohanaserver.subject.repository

import com.ohana.ohanaserver.subject.domain.Subject
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SubjectRepository : JpaRepository<Subject, UUID> {
    fun findAllByGroupId(groupId: UUID): List<Subject>
}
