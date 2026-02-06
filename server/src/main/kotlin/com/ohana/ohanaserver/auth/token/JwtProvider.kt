package com.ohana.ohanaserver.auth.token

import com.nimbusds.jose.jwk.source.ImmutableSecret
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class JwtProvider(
    @Value("\${ohana.jwt.secret}") secret: String,
    @Value("\${ohana.jwt.issuer}") private val issuer: String,
    @Value("\${ohana.jwt.access-token-minutes}") private val ttlMinutes: Long,
) {
    private val encoder: JwtEncoder = NimbusJwtEncoder(ImmutableSecret(secret.toByteArray()))

    fun issueAccessToken(userId: UUID, groupId: UUID? = null): String {
        val now = Instant.now()
        val claimsBuilder = JwtClaimsSet.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(ttlMinutes * 60))
            .subject(userId.toString())

        if (groupId != null) {
            // Optional group context for downstream authorization checks.
            claimsBuilder.claim("gid", groupId.toString())
        }

        val claims = claimsBuilder.build()

        val header = JwsHeader.with(MacAlgorithm.HS256).build()
        return encoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue
    }
}
