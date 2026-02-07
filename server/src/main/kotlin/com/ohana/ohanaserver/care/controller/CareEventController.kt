package com.ohana.ohanaserver.care.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.care.domain.CareEvent
import com.ohana.ohanaserver.care.domain.CareEventType
import com.ohana.ohanaserver.care.service.CareEventService
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

    // GET /care-events?subjectId=...&date=2026-02-06
    @GetMapping
    fun list(
        @RequestParam subjectId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): List<CareEvent> {
        val userId = SecurityUtil.currentUserId()
        return careEventService.listByDate(userId, subjectId, date)
    }

    // GET /care-events/latest?subjectId=...&type=BATH
    @GetMapping("/latest")
    fun latest(
        @RequestParam subjectId: UUID,
        @RequestParam type: CareEventType
    ): CareEvent? {
        val userId = SecurityUtil.currentUserId()
        return careEventService.latest(userId, subjectId, type)
    }
}
