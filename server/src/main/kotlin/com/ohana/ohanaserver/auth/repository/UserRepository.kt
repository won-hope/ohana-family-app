package com.ohana.ohanaserver.auth.repository

import com.ohana.ohanaserver.auth.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByGoogleSub(googleSub: String): User?
}
