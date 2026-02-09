package com.ohana.ohanaserver.export.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "export_job")
class ExportJob(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    val jobType: ExportJobType,

    @Column(name = "job_date", nullable = false)
    val jobDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ExportJobStatus = ExportJobStatus.PENDING,

    @Column(name = "try_count", nullable = false)
    var tryCount: Int = 0,

    @Column(name = "last_error")
    var lastError: String? = null,

    @Column(name = "started_at")
    var startedAt: OffsetDateTime? = null,

    @Column(name = "finished_at")
    var finishedAt: OffsetDateTime? = null,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now(),
)

enum class ExportJobType { FEEDING_SHEETS }
enum class ExportJobStatus { PENDING, RUNNING, SUCCESS, FAILED }