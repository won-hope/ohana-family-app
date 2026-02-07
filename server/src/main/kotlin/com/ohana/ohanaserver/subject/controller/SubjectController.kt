package com.ohana.ohanaserver.subject.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.subject.domain.Subject
import com.ohana.ohanaserver.subject.domain.SubjectType
import com.ohana.ohanaserver.subject.service.SubjectService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.UUID

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

    @GetMapping("/ping")
    fun ping(): Map<String, String> = mapOf("ok" to "true")

    @PostMapping
    fun create(@RequestBody @Valid req: CreateSubjectRequest): Subject {
        val userId = SecurityUtil.currentUserId()
        return subjectService.create(userId, req.type, req.name, req.birthDate, req.notes)
    }

    @GetMapping
    fun list(): List<Subject> {
        val userId = SecurityUtil.currentUserId()
        return subjectService.list(userId)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): Subject {
        val userId = SecurityUtil.currentUserId()
        return subjectService.get(userId, id)
    }
}
