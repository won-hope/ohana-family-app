package com.ohana.ohanaserver.notification.repository

import com.ohana.ohanaserver.notification.domain.UserDevice
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserDeviceRepository : JpaRepository<UserDevice, UUID>
