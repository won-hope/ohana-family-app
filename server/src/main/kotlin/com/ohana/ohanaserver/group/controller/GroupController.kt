package com.ohana.ohanaserver.group.controller

import com.ohana.ohanaserver.auth.domain.User
import com.ohana.ohanaserver.auth.repository.UserRepository
import com.ohana.ohanaserver.group.domain.Group
import com.ohana.ohanaserver.group.domain.GroupMember
import com.ohana.ohanaserver.group.domain.GroupRole
import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import com.ohana.ohanaserver.group.repository.GroupRepository
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/groups")
class GroupController(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val groupMemberRepository: GroupMemberRepository
) {

    data class CreateGroupRequest(val name: String)

    @GetMapping("/ping")
    fun ping(): Map<String, String> = mapOf("ok" to "true")

    @PostMapping
    fun createGroup(@RequestBody req: CreateGroupRequest): Group {
        // ✅ 인증 붙기 전까지 임시 유저 생성(고정 sub)
        val user = userRepository.findByGoogleSub("dev-user") ?: userRepository.save(
            User(
                googleSub = "dev-user",
                email = "dev@ohana.local",
                name = "Dev User"
            )
        )

        val group = groupRepository.save(
            Group(
                ownerUserId = user.id,
                name = req.name
            )
        )

        groupMemberRepository.save(
            GroupMember(
                groupId = group.id,
                userId = user.id,
                role = GroupRole.OWNER
            )
        )

        return group
    }
}
