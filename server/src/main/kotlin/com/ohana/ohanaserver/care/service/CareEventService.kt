package com.ohana.ohanaserver.care.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.ohana.ohanaserver.care.domain.CareEvent
import com.ohana.ohanaserver.care.domain.CareEventType
import com.ohana.ohanaserver.care.repository.CareEventRepository
import com.ohana.ohanaserver.common.exception.FieldError
import com.ohana.ohanaserver.common.exception.ValidationException
import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import com.ohana.ohanaserver.inventory.domain.ItemType
import com.ohana.ohanaserver.inventory.service.InventoryService
import com.ohana.ohanaserver.subject.repository.SubjectRepository
import jakarta.validation.Validator
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CareEventService(
    private val groupMemberRepository: GroupMemberRepository,
    private val subjectRepository: SubjectRepository,
    private val careEventRepository: CareEventRepository,
    private val objectMapper: ObjectMapper,
    private val validator: Validator,
    private val inventoryService: InventoryService
) {
    private val zone = ZoneId.of("Asia/Seoul")

    private fun requireGroupId(userId: UUID): UUID =
        groupMemberRepository.findFirstByUserIdOrderByCreatedAtAsc(userId)?.groupId
            ?: throw IllegalStateException("아직 그룹이 없습니다.")

    @Transactional
    fun create(
        userId: UUID,
        subjectId: UUID,
        type: CareEventType,
        occurredAt: OffsetDateTime,
        idempotencyKey: UUID,
        payload: Map<String, Any?>?
    ): CareEvent {
        val groupId = requireGroupId(userId)

        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { NoSuchElementException("Subject not found") }

        if (subject.groupId != groupId) throw IllegalAccessException("Forbidden subject")

        validatePayload(type, payload)

        // ✅ 멱등성 체크 (중복 요청이면 기존 것 반환)
        careEventRepository.findBySubjectIdAndIdempotencyKey(subjectId, idempotencyKey)
            ?.let { return it }

        val savedEvent = careEventRepository.save(
            CareEvent(
                groupId = groupId,
                subjectId = subjectId,
                type = type,
                occurredAt = occurredAt,
                payload = payload,
                createdByUserId = userId,
                idempotencyKey = idempotencyKey
            )
        )

        // 💡 기저귀 자동 차감 연동!
        if (type == CareEventType.DIAPER_PEE || type == CareEventType.DIAPER_POO) {
            try {
                inventoryService.decreaseStock(
                    groupId, 
                    ItemType.DIAPER, 
                    1
                )
            } catch (e: Exception) {
                // 재고 차감에 실패해도 육아 기록은 정상 저장되어야 하므로 예외 처리
                println("재고 차감 에러: ${e.message}")
            }
        }

        return savedEvent
    }

    fun listByDate(userId: UUID, subjectId: UUID, date: LocalDate): List<CareEvent> {
        val groupId = requireGroupId(userId)
        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { NoSuchElementException("Subject not found") }
        if (subject.groupId != groupId) throw IllegalAccessException("Forbidden subject")

        // KST 기준 하루 범위 계산 (00:00 ~ 다음날 00:00)
        val from = date.atStartOfDay(zone).toOffsetDateTime()
        val to = date.plusDays(1).atStartOfDay(zone).toOffsetDateTime()

        return careEventRepository.findBySubjectAndRange(subjectId, from, to)
    }

    fun latest(userId: UUID, subjectId: UUID, type: CareEventType): CareEvent? {
        val groupId = requireGroupId(userId)
        val subject = subjectRepository.findById(subjectId)
            .orElseThrow { NoSuchElementException("Subject not found") }
        if (subject.groupId != groupId) throw IllegalAccessException("Forbidden subject")
        return careEventRepository.findTop1BySubjectIdAndTypeOrderByOccurredAtDesc(subjectId, type)
    }

    private fun validatePayload(type: CareEventType, payload: Map<String, Any?>?) {
        if (payload.isNullOrEmpty()) return

        val allowedKeys = when (type) {
            CareEventType.DIAPER_PEE,
            CareEventType.DIAPER_POO -> setOf("color", "amount", "memo")
            CareEventType.BATH -> setOf("memo")
            CareEventType.TEMP -> setOf("celsius", "memo")
            CareEventType.SLEEP -> setOf("startedAt", "endedAt", "durationMinutes", "memo")
        }

        val invalidKeys = payload.keys - allowedKeys
        if (invalidKeys.isNotEmpty()) {
            throw ValidationException(
                listOf(FieldError("payload", "Invalid keys: ${invalidKeys.joinToString(", ")}"))
            )
        }

        when (type) {
            CareEventType.DIAPER_PEE,
            CareEventType.DIAPER_POO -> validateDto<DiaperPayload>(payload)
            CareEventType.BATH -> validateDto<BathPayload>(payload)
            CareEventType.TEMP -> validateDto<TempPayload>(payload)
            CareEventType.SLEEP -> validateDto<SleepPayload>(payload)
        }
    }

    private inline fun <reified T : Any> validateDto(payload: Map<String, Any?>) {
        val dto = try {
            objectMapper.convertValue(payload, object : TypeReference<T>() {})
        } catch (ex: IllegalArgumentException) {
            throw ValidationException(listOf(FieldError("payload", "Invalid payload format")))
        }

        val violations = validator.validate(dto)
        if (violations.isNotEmpty()) {
            val errors = violations.map {
                FieldError("payload.${it.propertyPath}", it.message)
            }
            throw ValidationException(errors)
        }

        if (dto is SleepPayload) {
            validateSleepBounds(dto)
        }
    }

    data class DiaperPayload(
        @field:Size(max = 100) val color: String? = null,
        @field:Size(max = 100) val amount: String? = null,
        @field:Size(max = 500) val memo: String? = null
    )

    data class BathPayload(
        @field:Size(max = 500) val memo: String? = null
    )

    data class TempPayload(
        @field:DecimalMin("-10.0")
        @field:DecimalMax("45.0")
        val celsius: Double? = null,
        @field:Size(max = 500) val memo: String? = null
    )

    data class SleepPayload(
        val startedAt: OffsetDateTime? = null,
        val endedAt: OffsetDateTime? = null,
        @field:Positive val durationMinutes: Int? = null,
        @field:Size(max = 500) val memo: String? = null
    )

    private fun validateSleepBounds(payload: SleepPayload) {
        val start = payload.startedAt
        val end = payload.endedAt
        if (start != null && end != null && end.isBefore(start)) {
            throw ValidationException(
                listOf(FieldError("payload.endedAt", "endedAt must be after startedAt"))
            )
        }
    }
}
