package com.ohana.ohanaserver.export.repository

import com.ohana.ohanaserver.export.domain.ExportJob
import com.ohana.ohanaserver.export.domain.ExportJobType
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.*

interface ExportJobRepository : JpaRepository<ExportJob, UUID> {
    fun findByGroupIdAndJobTypeAndJobDate(
        groupId: UUID,
        jobType: ExportJobType,
        jobDate: LocalDate
    ): ExportJob?
}