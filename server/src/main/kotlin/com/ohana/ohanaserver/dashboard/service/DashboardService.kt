package com.ohana.ohanaserver.dashboard.service

import com.ohana.ohanaserver.care.domain.CareEventType
import com.ohana.ohanaserver.care.repository.CareEventRepository
import com.ohana.ohanaserver.dashboard.dto.*
import com.ohana.ohanaserver.feeding.repository.FeedingLogRepository
import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import com.ohana.ohanaserver.subject.repository.SubjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.UUID

@Service
@Transactional(readOnly = true)
class DashboardService(
    private val groupMemberRepository: GroupMemberRepository,
    private val subjectRepository: SubjectRepository,
    private val feedingLogRepository: FeedingLogRepository,
    private val careEventRepository: CareEventRepository
) {
    private val zone = ZoneId.of("Asia/Seoul")

    // 보안 검증: 내 그룹의 Subject인가?
    private fun verifySubject(userId: UUID, subjectId: UUID) {
        val groupMember = groupMemberRepository.findFirstByUserIdOrderByCreatedAtAsc(userId)
            ?: throw IllegalStateException("No group found")

        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { NoSuchElementException("Subject not found") }
        if (subject.groupId != groupMember.groupId) {
            throw IllegalAccessException("Forbidden subject")
        }
    }

    // 1. 타임라인 (통합 조회)
    fun getTimeline(userId: UUID, subjectId: UUID, date: LocalDate): List<TimelineItem> {
        verifySubject(userId, subjectId)

        val from = date.atStartOfDay(zone).toOffsetDateTime()
        val to = date.plusDays(1).atStartOfDay(zone).toOffsetDateTime()

        // 수유 기록 -> TimelineItem 변환
        val feedings = feedingLogRepository.findBySubjectAndRange(subjectId, from, to)
            .map {
                TimelineItem(
                    id = it.id,
                    subjectId = it.subjectId,
                    occurredAt = it.fedAt,
                    category = TimelineCategory.FEEDING,
                    feeding = FeedingItem(
                        amountMl = it.amountMl,
                        method = it.method,
                        note = it.note
                    )
                )
            }

        // 케어 기록 -> TimelineItem 변환
        val cares = careEventRepository.findBySubjectAndRange(subjectId, from, to)
            .map {
                TimelineItem(
                    id = it.id,
                    subjectId = it.subjectId,
                    occurredAt = it.occurredAt,
                    category = TimelineCategory.CARE,
                    care = CareItem(
                        type = it.type,
                        payload = toCarePayload(it.type, it.payload)
                    )
                )
            }

        // 합쳐서 최신순(내림차순) 정렬
        return (feedings + cares).sortedByDescending { it.occurredAt }
    }

    // 2. 데일리 요약
    fun getSummary(userId: UUID, subjectId: UUID, date: LocalDate): DailySummaryResponse {
        verifySubject(userId, subjectId)

        val from = date.atStartOfDay(zone).toOffsetDateTime()
        val to = date.plusDays(1).atStartOfDay(zone).toOffsetDateTime()

        // 데이터 조회
        val feedings = feedingLogRepository.findBySubjectAndRange(subjectId, from, to)
        val cares = careEventRepository.findBySubjectAndRange(subjectId, from, to)

        // 수유 통계
        val lastFeeding = feedings.maxByOrNull { it.fedAt }
        val totalAmount = feedings.sumOf { it.amountMl ?: 0 }

        // 케어 통계
        val peeCount = cares.count { it.type == CareEventType.DIAPER_PEE }
        val pooCount = cares.count { it.type == CareEventType.DIAPER_POO }
        val bathDone = cares.any { it.type == CareEventType.BATH }

        // 체온 (payload에서 "celsius" 꺼내기)
        val latestTempEvent = cares.filter { it.type == CareEventType.TEMP }
            .maxByOrNull { it.occurredAt }

        val latestTemp = latestTempEvent?.payload?.get("celsius")?.toString()?.toDoubleOrNull()

        return DailySummaryResponse(
            date = date.toString(),
            subjectId = subjectId,
            lastFeedingAt = lastFeeding?.fedAt,
            totalFeedingAmountMl = totalAmount,
            feedingCount = feedings.size,
            peeCount = peeCount,
            pooCount = pooCount,
            bathDone = bathDone,
            latestTemp = latestTemp,
            latestTempAt = latestTempEvent?.occurredAt
        )
    }

    private fun toCarePayload(type: CareEventType, payload: Map<String, Any?>?): CarePayload? {
        if (payload.isNullOrEmpty()) return null
        return when (type) {
            CareEventType.DIAPER_PEE,
            CareEventType.DIAPER_POO -> DiaperPayload(
                color = payload["color"] as? String,
                amount = payload["amount"] as? String,
                memo = payload["memo"] as? String
            )
            CareEventType.BATH -> BathPayload(
                memo = payload["memo"] as? String
            )
            CareEventType.TEMP -> TempPayload(
                celsius = (payload["celsius"] as? Number)?.toDouble(),
                memo = payload["memo"] as? String
            )
            CareEventType.SLEEP -> SleepPayload(
                startedAt = parseOffsetDateTime(payload["startedAt"]),
                endedAt = parseOffsetDateTime(payload["endedAt"]),
                durationMinutes = (payload["durationMinutes"] as? Number)?.toInt(),
                memo = payload["memo"] as? String
            )
        }
    }

    private fun parseOffsetDateTime(value: Any?): OffsetDateTime? =
        when (value) {
            is OffsetDateTime -> value
            is String -> runCatching { OffsetDateTime.parse(value) }.getOrNull()
            else -> null
        }
}
