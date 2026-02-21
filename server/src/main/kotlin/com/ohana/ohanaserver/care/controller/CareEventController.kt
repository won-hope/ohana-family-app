package com.ohana.ohanaserver.care.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.care.domain.CareEvent
import com.ohana.ohanaserver.care.domain.CareEventType
import com.ohana.ohanaserver.care.service.CareEventService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotNull
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Tag(name = "돌봄", description = "돌봄 이벤트(기저귀, 목욕, 체온 등) 관련 API")
@RestController
@RequestMapping("/care-events")
class CareEventController(
    private val careEventService: CareEventService
) {
    data class CreateCareEventRequest(
        @field:NotNull val subjectId: UUID,
        @field:NotNull val type: CareEventType,
        @field:NotNull val occurredAt: OffsetDateTime,
        @field:NotNull val idempotencyKey: UUID,
        val payload: Map<String, Any?>? = null
    )

    @Operation(summary = "돌봄 이벤트 생성", description = "새로운 돌봄 이벤트를 기록합니다.")
    @PostMapping
    fun create(@RequestBody @Valid req: CreateCareEventRequest): CareEvent {
        val userId = SecurityUtil.currentUserId()
        return careEventService.create(
            userId = userId,
            subjectId = req.subjectId,
            type = req.type,
            occurredAt = req.occurredAt,
            idempotencyKey = req.idempotencyKey,
            payload = req.payload
        )
    }

    @Operation(summary = "일별 돌봄 이벤트 조회", description = "특정 날짜의 돌봄 이벤트 목록을 조회합니다.")
    @GetMapping
    fun list(
        @RequestParam subjectId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): List<CareEvent> {
        val userId = SecurityUtil.currentUserId()
        return careEventService.listByDate(userId, subjectId, date)
    }

    @Operation(summary = "타입별 최근 돌봄 이벤트 조회", description = "특정 타입의 가장 최근 돌봄 이벤트를 1건 조회합니다.")
    @GetMapping("/latest")
    fun latest(
        @RequestParam subjectId: UUID,
        @RequestParam type: CareEventType
    ): CareEvent? {
        val userId = SecurityUtil.currentUserId()
        return careEventService.latest(userId, subjectId, type)
    }
}
