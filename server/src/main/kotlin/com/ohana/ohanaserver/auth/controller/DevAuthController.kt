package com.ohana.ohanaserver.auth.controller

import com.ohana.ohanaserver.auth.domain.User
import com.ohana.ohanaserver.auth.repository.UserRepository
import com.ohana.ohanaserver.auth.token.JwtProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Profile("dev")
@ConditionalOnProperty(prefix = "ohana.dev.auth", name = ["enabled"], havingValue = "true")
class DevAuthController(
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
) {
    data class TokenResponse(val accessToken: String)

    // Dev-only login to unblock local API testing without Google ID tokens.
    @PostMapping("/dev")
    fun devLogin(): TokenResponse {
        val user = userRepository.findByGoogleSub("dev-user")
            ?: userRepository.save(
                User(
                    googleSub = "dev-user",
                    email = "dev@ohana.local",
                    name = "Dev User",
                    pictureUrl = null
                )
            )

        val token = jwtProvider.issueAccessToken(user.id)
        return TokenResponse(token)
    }
}
