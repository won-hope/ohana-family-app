package com.ohana.ohanaserver.group.repository

import com.ohana.ohanaserver.group.domain.GroupMember
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GroupMemberRepository : JpaRepository<GroupMember, UUID> {
    fun findFirstByUserIdOrderByCreatedAtAsc(userId: UUID): GroupMember?
    fun findAllByUserId(userId: UUID): List<GroupMember>
}
