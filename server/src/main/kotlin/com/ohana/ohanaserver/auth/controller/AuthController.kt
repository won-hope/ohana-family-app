package com.ohana.ohanaserver.auth.controller

import com.ohana.ohanaserver.auth.service.GoogleIdTokenVerifier
import com.ohana.ohanaserver.auth.domain.User
import com.ohana.ohanaserver.auth.repository.UserRepository
import com.ohana.ohanaserver.auth.token.JwtProvider
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val verifier: GoogleIdTokenVerifier,
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
) {
    data class GoogleLoginRequest(@field:NotBlank val idToken: String)
    data class GoogleLoginResponse(val accessToken: String)

    // Exchanges a Google ID token for an app access token.
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
