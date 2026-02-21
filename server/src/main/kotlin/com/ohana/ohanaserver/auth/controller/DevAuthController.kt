package com.ohana.ohanaserver.auth.controller

import com.ohana.ohanaserver.auth.domain.User
import com.ohana.ohanaserver.auth.repository.UserRepository
import com.ohana.ohanaserver.auth.token.JwtProvider
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "인증 (개발용)", description = "개발 환경 전용 로그인 API")
@RestController
@RequestMapping("/auth")
@Profile("dev")
@ConditionalOnProperty(prefix = "ohana.dev.auth", name = ["enabled"], havingValue = "true")
class DevAuthController(
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
) {
    data class TokenResponse(val accessToken: String)

    @Operation(summary = "개발용 로그인", description = "개발 환경에서 테스트용 액세스 토큰을 발급받습니다.")
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
