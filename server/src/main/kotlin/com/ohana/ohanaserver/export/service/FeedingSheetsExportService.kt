package com.ohana.ohanaserver.export.service

import com.ohana.ohanaserver.export.domain.*
import com.ohana.ohanaserver.export.repository.ExportJobRepository
import com.ohana.ohanaserver.export.sheets.SheetsAppender
import com.ohana.ohanaserver.feeding.repository.FeedingLogRepository
import com.ohana.ohanaserver.group.repository.GroupRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*

@Service
class FeedingSheetsExportService(
    private val exportJobRepository: ExportJobRepository,
    private val groupRepository: GroupRepository,
    private val feedingLogRepository: FeedingLogRepository,
    private val sheetsAppender: SheetsAppender
) {
    private val zone = ZoneId.of("Asia/Seoul")

    // 전체 그룹 순회하며 배치 실행
    fun exportAllGroupsForDate(date: LocalDate) {
        val groups = groupRepository.findAll()
        groups.forEach { group ->
            try {
                exportOneGroupForDate(group.id, date)
            } catch (e: Exception) {
                // 한 그룹 실패해도 다른 그룹은 계속 진행
                e.printStackTrace()
            }
        }
    }

    // 단일 그룹 실행 로직
    fun exportOneGroupForDate(groupId: UUID, date: LocalDate) {
        // 1. Job 생성 또는 가져오기 (이미 성공했으면 스킵)
        val job = ensureJob(groupId, date)
        if (job.status == ExportJobStatus.SUCCESS || job.status == ExportJobStatus.RUNNING) return

        markRunning(job)

        try {
            val from = date.atStartOfDay(zone).toOffsetDateTime()
            val to = date.plusDays(1).atStartOfDay(zone).toOffsetDateTime()

            // 2. 보낼 데이터 조회
            val logs = feedingLogRepository.findUnexportedInRange(groupId, from, to)

            if (logs.isEmpty()) {
                markSuccess(job) // 보낼 게 없으면 바로 성공 처리
                return
            }

            // 3. 데이터 변환
            val rows: List<List<Any>> = logs.map { log ->
                listOf(
                    log.fedAt.toString(),
                    log.subjectId.toString(),
                    (log.amountMl ?: ""),
                    (log.method.name),
                    (log.note ?: ""),
                    (log.durationSeconds ?: ""),
                    log.createdBy.toString()
                )
            }

            // 4. 외부 연동 (지금은 로그만)
            sheetsAppender.appendRows(groupId, rows)

            // 5. 성공 처리 (exportedAt 업데이트)
            markExportedAndSuccess(job, logs.map { it.id })

        } catch (e: Exception) {
            markFailed(job, e)
        }
    }

    // --- 트랜잭션 분리 메서드들 ---

    @Transactional
    fun ensureJob(groupId: UUID, date: LocalDate): ExportJob {
        return exportJobRepository.findByGroupIdAndJobTypeAndJobDate(
            groupId,
            ExportJobType.FEEDING_SHEETS,
            date
        )
            ?: exportJobRepository.save(
                ExportJob(
                    groupId = groupId,
                    jobType = ExportJobType.FEEDING_SHEETS,
                    jobDate = date,
                    status = ExportJobStatus.PENDING
                )
            )
    }

    @Transactional
    fun markRunning(job: ExportJob) {
        job.status = ExportJobStatus.RUNNING
        job.startedAt = OffsetDateTime.now()
        job.tryCount += 1
        job.updatedAt = OffsetDateTime.now()
        exportJobRepository.save(job)
    }

    @Transactional
    fun markSuccess(job: ExportJob) {
        job.status = ExportJobStatus.SUCCESS
        job.finishedAt = OffsetDateTime.now()
        job.lastError = null
        job.updatedAt = OffsetDateTime.now()
        exportJobRepository.save(job)
    }

    @Transactional
    fun markFailed(job: ExportJob, e: Exception) {
        job.status = ExportJobStatus.FAILED
        job.finishedAt = OffsetDateTime.now()
        job.lastError = (e.message ?: e.javaClass.name).take(2000)
        job.updatedAt = OffsetDateTime.now()
        exportJobRepository.save(job)
    }

    @Transactional
    fun markExportedAndSuccess(job: ExportJob, feedingIds: List<UUID>) {
        val now = OffsetDateTime.now()
        val logs = feedingLogRepository.findAllById(feedingIds)
        logs.forEach { it.exportedAt = now } // Entity에 exportedAt 필드 있어야 함! (V4 확인)
        feedingLogRepository.saveAll(logs)
        markSuccess(job) // Job 상태도 SUCCESS로 변경
    }
}
