package com.ohana.ohanaserver.google.repository

import com.ohana.ohanaserver.google.domain.GroupGoogleSheetsConnection
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GroupGoogleSheetsConnectionRepository : JpaRepository<GroupGoogleSheetsConnection, UUID>
