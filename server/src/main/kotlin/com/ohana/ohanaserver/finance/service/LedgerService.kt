package com.ohana.ohanaserver.finance.service

import com.ohana.ohanaserver.finance.domain.LedgerTransaction
import com.ohana.ohanaserver.finance.domain.MonthlyBudget
import com.ohana.ohanaserver.finance.domain.TransactionType
import com.ohana.ohanaserver.finance.repository.LedgerTransactionRepository
import com.ohana.ohanaserver.finance.repository.MonthlyBudgetRepository
import com.ohana.ohanaserver.group.repository.GroupMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

@Service
class LedgerService(
    private val ledgerRepository: LedgerTransactionRepository,
    private val monthlyBudgetRepository: MonthlyBudgetRepository,
    private val groupMemberRepository: GroupMemberRepository
) {
    private fun requireGroupId(userId: UUID): UUID =
        groupMemberRepository.findFirstByUserIdOrderByCreatedAtAsc(userId)?.groupId
            ?: throw IllegalStateException("가족 그룹이 없습니다.")

    // 1. 내역 쓰기
    @Transactional
    fun recordTransaction(
        userId: UUID,
        type: TransactionType,
        amount: Long,
        date: LocalDate,
        category: String,
        paymentMethod: String?,
        memo: String?
    ): LedgerTransaction {
        val groupId = requireGroupId(userId)
        
        return ledgerRepository.save(
            LedgerTransaction(
                groupId = groupId,
                transactionType = type,
                amount = amount,
                transactionDate = date,
                category = category,
                paymentMethod = paymentMethod,
                memo = memo,
                createdByUserId = userId
            )
        )
    }

    // 2. 예산 설정 (Upsert)
    @Transactional
    fun setMonthlyBudget(userId: UUID, year: Int, month: Int, amount: Long): MonthlyBudget {
        val groupId = requireGroupId(userId)
        val yearMonthStr = String.format("%04d-%02d", year, month)

        val budget = monthlyBudgetRepository.findByGroupIdAndYearMonth(groupId, yearMonthStr)
            ?: MonthlyBudget(
                groupId = groupId,
                yearMonth = yearMonthStr,
                targetAmount = amount,
                createdByUserId = userId
            )
        
        budget.targetAmount = amount
        budget.updatedAt = java.time.OffsetDateTime.now()
        
        return monthlyBudgetRepository.save(budget)
    }

    // 통계 결과 DTO
    data class MonthlySummary(
        val totalIncome: Long,
        val totalExpense: Long,
        val balance: Long, // 수입 - 지출
        val transactions: List<LedgerTransaction> // 상세 내역
    )

    // 3. 이번 달(또는 특정 달) 요약 및 리스트 한방에 가져오기
    fun getMonthlySummary(userId: UUID, year: Int, month: Int): MonthlySummary {
        val groupId = requireGroupId(userId)
        val yearMonth = YearMonth.of(year, month)
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()

        val transactions = ledgerRepository.findAllByGroupIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            groupId, startDate, endDate
        )

        val totalIncome = transactions.filter { it.transactionType == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.transactionType == TransactionType.EXPENSE }.sumOf { it.amount }
        val balance = totalIncome - totalExpense

        return MonthlySummary(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            balance = balance,
            transactions = transactions
        )
    }

    // 위젯 전용 DTO
    data class WidgetBudgetSummary(
        val yearMonth: String,       // "2026-02"
        val targetAmount: Long,      // 목표 금액 (예: 1,000,000)
        val totalExpense: Long,      // 현재까지 쓴 돈 (예: 300,000)
        val remainingAmount: Long,   // 남은 돈 (예: 700,000)
        val safeToSpendToday: Long   // 💡 오늘 얼마까지 써도 안전한지 (보너스 기능!)
    )

    // 4. 위젯 및 홈 화면 상단용 "차감 요약"
    fun getBudgetSummaryForWidget(userId: UUID, year: Int, month: Int): WidgetBudgetSummary {
        val groupId = requireGroupId(userId)
        val yearMonthStr = String.format("%04d-%02d", year, month)
        
        // 1. 이번 달 목표 생활비 조회 (없으면 기본값 0)
        val budget = monthlyBudgetRepository.findByGroupIdAndYearMonth(groupId, yearMonthStr)
        val targetAmount = budget?.targetAmount ?: 0L

        // 2. 이번 달 지출 총합 조회 (DB에서 바로 SUM 때려서 속도 극대화)
        val startDate = YearMonth.of(year, month).atDay(1)
        val endDate = YearMonth.of(year, month).atEndOfMonth()
        val totalExpense = ledgerRepository.sumExpenseByGroupAndDateRange(groupId, startDate, endDate) ?: 0L

        // 3. 차감 계산 (남은 돈)
        val remainingAmount = targetAmount - totalExpense

        // 4. (보너스) 이번 달 남은 일수로 나눠서 "오늘 하루 쓸 수 있는 돈" 계산
        val daysInMonth = endDate.dayOfMonth
        val today = LocalDate.now().dayOfMonth
        val remainingDays = (daysInMonth - today + 1).coerceAtLeast(1)
        val safeToSpendToday = if (remainingAmount > 0) remainingAmount / remainingDays else 0L

        return WidgetBudgetSummary(
            yearMonth = yearMonthStr,
            targetAmount = targetAmount,
            totalExpense = totalExpense,
            remainingAmount = remainingAmount,
            safeToSpendToday = safeToSpendToday
        )
    }
}
