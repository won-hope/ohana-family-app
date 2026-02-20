package com.ohana.ohanaserver.finance.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

enum class TransactionType {
    INCOME,   // 수입
    EXPENSE,  // 지출
    TRANSFER  // 이체 (통장 간 이동 등)
}

@Entity
@Table(name = "ledger_transaction")
class LedgerTransaction(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "group_id", nullable = false)
    val groupId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    val transactionType: TransactionType,

    @Column(nullable = false)
    val amount: Long,

    @Column(name = "transaction_date", nullable = false)
    val transactionDate: LocalDate,

    @Column(nullable = false)
    val category: String,

    @Column(name = "payment_method")
    val paymentMethod: String? = null,

    val memo: String? = null,

    @Column(name = "created_by_user_id", nullable = false)
    val createdByUserId: UUID,

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
)
