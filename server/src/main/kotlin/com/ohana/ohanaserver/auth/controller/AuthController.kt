package com.ohana.ohanaserver.auth.controller

import com.ohana.ohanaserver.auth.service.GoogleIdTokenVerifier
import com.ohana.ohanaserver.auth.domain.User
import com.ohana.ohanaserver.auth.repository.UserRepository
import com.ohana.ohanaserver.auth.token.JwtProvider
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.*

@Tag(name = "인증", description = "사용자 인증 및 로그인 API")
@RestController
@RequestMapping("/auth")
class AuthController(
    private val verifier: GoogleIdTokenVerifier,
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
) {
    data class GoogleLoginRequest(@field:NotBlank val idToken: String)
    data class GoogleLoginResponse(val accessToken: String)

    @Operation(summary = "구글 로그인", description = "Google ID 토큰을 사용하여 로그인하고 앱의 액세스 토큰을 발급받습니다.")
    @PostMapping("/google")
    fun login(@RequestBody @Valid req: GoogleLoginRequest): GoogleLoginResponse {
        val jwt = verifier.verify(req.idToken)

        val googleSub = jwt.subject
        val email = jwt.getClaimAsString("email") ?: ""
        val name = jwt.getClaimAsString("name")
        val picture = jwt.getClaimAsString("picture")

        val user = userRepository.findByGoogleSub(googleSub)
            ?: userRepository.save(
                User(
                    googleSub = googleSub,
                    email = email,
                    name = name,
                    pictureUrl = picture
                )
            )

        val accessToken = jwtProvider.issueAccessToken(user.id)
        return GoogleLoginResponse(accessToken)
    }
}
