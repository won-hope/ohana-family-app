package com.ohana.ohanaserver.group.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.group.domain.Group
import com.ohana.ohanaserver.group.domain.GroupMember
import com.ohana.ohanaserver.group.domain.GroupRole
import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import com.ohana.ohanaserver.group.repository.GroupRepository
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/groups")
class GroupController(
    private val groupRepository: GroupRepository,
    private val groupMemberRepository: GroupMemberRepository
) {

    data class CreateGroupRequest(
        @field:NotBlank
        @field:Size(max = 50)
        val name: String
    )

    @GetMapping("/ping")
    fun ping(): Map<String, String> = mapOf("ok" to "true")

    @PostMapping
    fun createGroup(@RequestBody @Valid req: CreateGroupRequest): Group {
        val userId = SecurityUtil.currentUserId()
        val group = groupRepository.save(
            Group(
                ownerUserId = userId,
                name = req.name
            )
        )

        groupMemberRepository.save(
            GroupMember(
                groupId = group.id,
                userId = userId,
                role = GroupRole.OWNER
            )
        )

        return group
    }

    @GetMapping
    fun listGroups(): List<Group> {
        val userId = SecurityUtil.currentUserId()
        val memberships = groupMemberRepository.findAllByUserId(userId)
            .sortedBy { it.createdAt }
        val groupsById = groupRepository.findAllById(memberships.map { it.groupId })
            .associateBy { it.id }

        return memberships.mapNotNull { groupsById[it.groupId] }
    }
}
