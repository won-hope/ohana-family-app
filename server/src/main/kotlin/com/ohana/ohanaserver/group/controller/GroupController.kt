package com.ohana.ohanaserver.group.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.group.domain.Group
import com.ohana.ohanaserver.group.domain.GroupMember
import com.ohana.ohanaserver.group.domain.GroupRole
import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import com.ohana.ohanaserver.group.repository.GroupRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/groups")
class GroupController(
    private val groupRepository: GroupRepository,
    private val groupMemberRepository: GroupMemberRepository
) {

    data class CreateGroupRequest(val name: String)

    @GetMapping("/ping")
    fun ping(): Map<String, String> = mapOf("ok" to "true")

    @PostMapping
    fun createGroup(@RequestBody req: CreateGroupRequest): Group {
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
}
