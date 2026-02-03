package com.ohana.ohanaserver.group.repository

import com.ohana.ohanaserver.group.domain.Group
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GroupRepository : JpaRepository<Group, UUID>
