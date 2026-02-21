package com.ohana.ohanaserver.group.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.group.domain.Group
import com.ohana.ohanaserver.group.domain.GroupMember
import com.ohana.ohanaserver.group.domain.GroupRole
import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import com.ohana.ohanaserver.group.repository.GroupRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.web.bind.annotation.*

@Tag(name = "그룹", description = "가족 그룹 생성 및 조회 API")
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

    @Operation(summary = "그룹 생성", description = "새로운 가족 그룹을 생성합니다. 생성한 사용자는 자동으로 소유자(OWNER)가 됩니다.")
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

    @Operation(summary = "내 그룹 목록 조회", description = "내가 속한 모든 그룹의 목록을 조회합니다.")
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
