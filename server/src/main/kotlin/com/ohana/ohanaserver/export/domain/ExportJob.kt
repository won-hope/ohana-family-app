package com.ohana.ohanaserver.export.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

@Schema(description = "데이터 내보내기 작업 정보")
@Entity
@Table(name = "export_job")
class ExportJob(
    @Id
    @Schema(description = "작업 고유 ID")
    val id: UUID = UUID.randomUUID(),

    @Schema(description = "소속된 그룹 ID")
    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Schema(description = "작업 타입")
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    val jobType: ExportJobType,

    @Schema(description = "작업 대상 날짜")
    @Column(name = "job_date", nullable = false)
    val jobDate: LocalDate,

    @Schema(description = "작업 상태")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ExportJobStatus = ExportJobStatus.PENDING,

    @Schema(description = "시도 횟수")
    @Column(name = "try_count", nullable = false)
    var tryCount: Int = 0,

    @Schema(description = "마지막 에러 메시지")
    @Column(name = "last_error")
    var lastError: String? = null,

    @Schema(description = "작업 시작 시간")
    @Column(name = "started_at")
    var startedAt: OffsetDateTime? = null,

    @Schema(description = "작업 종료 시간")
    @Column(name = "finished_at")
    var finishedAt: OffsetDateTime? = null,

    @Schema(description = "업데이트 시간")
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now(),
)

@Schema(description = "내보내기 작업 타입 (FEEDING_SHEETS: 수유 기록 시트)")
enum class ExportJobType { FEEDING_SHEETS }

@Schema(description = "내보내기 작업 상태 (PENDING: 대기, RUNNING: 실행중, SUCCESS: 성공, FAILED: 실패)")
enum class ExportJobStatus { PENDING, RUNNING, SUCCESS, FAILED }