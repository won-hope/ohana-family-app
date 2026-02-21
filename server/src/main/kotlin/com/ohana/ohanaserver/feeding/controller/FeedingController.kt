package com.ohana.ohanaserver.feeding.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.feeding.domain.FeedingLog
import com.ohana.ohanaserver.feeding.domain.FeedingMethod
import com.ohana.ohanaserver.feeding.service.FeedingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import java.util.UUID

@Tag(name = "수유", description = "수유 기록 관련 API")
@RestController
@RequestMapping("/feedings")
class FeedingController(
    private val feedingService: FeedingService
) {
    data class CreateFeedingRequest(
        @field:NotNull val subjectId: UUID,
        @field:NotNull val idempotencyKey: UUID,
        @field:NotNull val fedAt: OffsetDateTime,
        @field:Positive val amountMl: Int? = null,
        @field:NotNull val method: FeedingMethod,
        @field:Size(max = 500) val note: String? = null,
        @field:Positive val durationSeconds: Int? = null
    )

    @Operation(summary = "수유 기록 생성", description = "새로운 수유 기록을 등록합니다.")
    @PostMapping
    fun create(@RequestBody @Valid req: CreateFeedingRequest): FeedingLog {
        val userId = SecurityUtil.currentUserId()
        return feedingService.create(
            userId = userId,
            subjectId = req.subjectId,
            idempotencyKey = req.idempotencyKey,
            fedAt = req.fedAt,
            amountMl = req.amountMl,
            method = req.method,
            note = req.note,
            durationSeconds = req.durationSeconds
        )
    }

    @Operation(summary = "대상별 수유 기록 조회", description = "특정 대상(아이/반려동물)의 수유 기록 목록을 조회합니다.")
    @GetMapping("/subject/{subjectId}")
    fun list(@PathVariable subjectId: UUID): List<FeedingLog> {
        val userId = SecurityUtil.currentUserId()
        return feedingService.listBySubject(userId, subjectId)
    }
}
