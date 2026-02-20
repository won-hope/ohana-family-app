package com.ohana.ohanaserver.finance.repository

import com.ohana.ohanaserver.finance.domain.LedgerTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.util.UUID

interface LedgerTransactionRepository : JpaRepository<LedgerTransaction, UUID> {
    
    // 특정 달의 전체 내역 조회 (날짜 내림차순)
    fun findAllByGroupIdAndTransactionDateBetweenOrderByTransactionDateDesc(
        groupId: UUID,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<LedgerTransaction>

    // 위젯용: 특정 기간 지출 합계 (EXPENSE 타입만)
    @Query("SELECT SUM(l.amount) FROM LedgerTransaction l WHERE l.groupId = :groupId AND l.transactionType = 'EXPENSE' AND l.transactionDate BETWEEN :startDate AND :endDate")
    fun sumExpenseByGroupAndDateRange(
        @Param("groupId") groupId: UUID,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): Long?
}
