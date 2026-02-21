package com.ohana.ohanaserver.finance.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "가계부 거래 내역")
@Entity
@Table(name = "ledger_transaction")
class LedgerTransaction(
    @Id
    @Schema(description = "거래 내역 고유 ID")
    val id: UUID = UUID.randomUUID(),

    @Schema(description = "소속된 그룹 ID")
    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Schema(description = "거래 타입")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    val transactionType: TransactionType,

    @Schema(description = "금액 (원)")
    @Column(nullable = false)
    val amount: Long,

    @Schema(description = "거래일")
    @Column(name = "transaction_date", nullable = false)
    val transactionDate: LocalDate,

    @Schema(description = "카테고리 (예: 식비, 육아용품)")
    @Column(nullable = false)
    val category: String,

    @Schema(description = "결제 수단 (예: 신용카드, 현금)")
    @Column(name = "payment_method")
    val paymentMethod: String? = null,

    @Schema(description = "메모")
    val memo: String? = null,

    @Schema(description = "기록한 사용자 ID")
    @Column(name = "created_by_user_id", nullable = false)
    val createdByUserId: UUID,

    @Schema(description = "생성일")
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Schema(description = "업데이트 시간")
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
)

@Schema(description = "거래 타입 (INCOME: 수입, EXPENSE: 지출, TRANSFER: 이체)")
enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER
}
