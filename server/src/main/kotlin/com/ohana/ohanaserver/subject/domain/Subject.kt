package com.ohana.ohanaserver.subject.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "관리 대상(아이, 반려동물) 정보")
@Entity
@Table(name = "subject")
class Subject(
    @Id
    @Schema(description = "관리 대상 고유 ID")
    val id: UUID = UUID.randomUUID(),

    @Schema(description = "소속된 그룹 ID")
    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Schema(description = "관리 대상 타입")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: SubjectType,

    @Schema(description = "이름")
    @Column(nullable = false)
    val name: String,

    @Schema(description = "생년월일")
    @Column(name = "birth_date")
    val birthDate: LocalDate? = null,

    @Schema(description = "메모")
    val notes: String? = null,

    @Schema(description = "생성일")
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)

@Schema(description = "관리 대상 타입 (BABY: 아이, PET: 반려동물)")
enum class SubjectType {
    BABY, PET
}
