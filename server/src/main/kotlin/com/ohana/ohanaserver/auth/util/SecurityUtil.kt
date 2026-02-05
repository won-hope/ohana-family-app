package com.ohana.ohanaserver.auth.util

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import java.util.UUID


object SecurityUtil {
    fun currentUserId(): UUID {
        return currentUserIdOrNull()
            ?: throw IllegalStateException("No authenticated Jwt principal")
    }

    fun currentUserIdOrNull(): UUID? {
        // Returns null when no JWT is present (e.g., anonymous request).
        val auth = SecurityContextHolder.getContext().authentication ?: return null
        val jwt = auth.principal as? Jwt ?: return null
        return UUID.fromString(jwt.subject)
    }

}
