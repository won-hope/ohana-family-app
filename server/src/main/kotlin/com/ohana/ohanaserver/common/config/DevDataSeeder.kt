package com.ohana.ohanaserver.common.config

import com.ohana.ohanaserver.auth.domain.User
import com.ohana.ohanaserver.auth.repository.UserRepository
import com.ohana.ohanaserver.group.domain.Group
import com.ohana.ohanaserver.group.domain.GroupMember
import com.ohana.ohanaserver.group.domain.GroupRole
import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import com.ohana.ohanaserver.group.repository.GroupRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("dev")
@ConditionalOnProperty(prefix = "ohana.dev.auth", name = ["enabled"], havingValue = "true")
class DevDataSeeder {

    @Bean
    fun devSeedData(
        userRepository: UserRepository,
        groupRepository: GroupRepository,
        groupMemberRepository: GroupMemberRepository,
    ) = CommandLineRunner {
        val user = userRepository.findByGoogleSub("dev-user")
            ?: userRepository.save(
                User(
                    googleSub = "dev-user",
                    email = "dev@ohana.local",
                    name = "Dev User",
                    pictureUrl = null
                )
            )

        val existingGroup = groupRepository.findAll()
            .firstOrNull { it.ownerUserId == user.id && it.name == "Dev Group" }

        if (existingGroup == null) {
            val group = groupRepository.save(
                Group(
                    ownerUserId = user.id,
                    name = "Dev Group"
                )
            )
            groupMemberRepository.save(
                GroupMember(
                    groupId = group.id,
                    userId = user.id,
                    role = GroupRole.OWNER
                )
            )
        }
    }
}
