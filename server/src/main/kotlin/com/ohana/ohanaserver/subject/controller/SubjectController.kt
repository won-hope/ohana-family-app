package com.ohana.ohanaserver.subject.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.subject.domain.Subject
import com.ohana.ohanaserver.subject.domain.SubjectType
import com.ohana.ohanaserver.subject.service.SubjectService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.UUID

@Tag(name = "관리 대상", description = "관리 대상(아이, 반려동물) 관련 API")
@RestController
@RequestMapping("/subjects")
class SubjectController(
    private val subjectService: SubjectService
) {
    data class CreateSubjectRequest(
        @field:NotNull val type: SubjectType,
        @field:NotBlank
        @field:Size(max = 50)
        val name: String,
        @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        val birthDate: LocalDate? = null,
        @field:Size(max = 500)
        val notes: String? = null
    )

    @Operation(summary = "관리 대상 생성", description = "새로운 아이 또는 반려동물을 등록합니다.")
    @PostMapping
    fun create(@RequestBody @Valid req: CreateSubjectRequest): Subject {
        val userId = SecurityUtil.currentUserId()
        return subjectService.create(userId, req.type, req.name, req.birthDate, req.notes)
    }

    @Operation(summary = "관리 대상 목록 조회", description = "내가 속한 그룹의 모든 관리 대상 목록을 조회합니다.")
    @GetMapping
    fun list(): List<Subject> {
        val userId = SecurityUtil.currentUserId()
        return subjectService.list(userId)
    }

    @Operation(summary = "특정 관리 대상 조회", description = "ID로 특정 관리 대상을 조회합니다.")
    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): Subject {
        val userId = SecurityUtil.currentUserId()
        return subjectService.get(userId, id)
    }
}
