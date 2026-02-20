package com.ohana.ohanaserver.finance.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.finance.domain.LedgerTransaction
import com.ohana.ohanaserver.finance.domain.MonthlyBudget
import com.ohana.ohanaserver.finance.domain.TransactionType
import com.ohana.ohanaserver.finance.service.LedgerService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/finance/ledgers")
class LedgerController(
    private val ledgerService: LedgerService
) {
    data class RecordRequest(
        val type: TransactionType,
        val amount: Long,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        val date: LocalDate,
        val category: String,
        val paymentMethod: String?,
        val memo: String?
    )

    // 가계부 입력 (3초 컷)
    @PostMapping
    fun record(@RequestBody req: RecordRequest): LedgerTransaction {
        val userId = SecurityUtil.currentUserId()
        return ledgerService.recordTransaction(
            userId, req.type, req.amount, req.date, req.category, req.paymentMethod, req.memo
        )
    }

    // 월간 가계부 조회 (2026년 2월)
    // GET /finance/ledgers/monthly?year=2026&month=2
    @GetMapping("/monthly")
    fun getMonthlySummary(
        @RequestParam year: Int,
        @RequestParam month: Int
    ): LedgerService.MonthlySummary {
        val userId = SecurityUtil.currentUserId()
        return ledgerService.getMonthlySummary(userId, year, month)
    }

    // 예산 설정
    @PostMapping("/budget")
    fun setBudget(@RequestBody req: SetBudgetRequest): MonthlyBudget {
        val userId = SecurityUtil.currentUserId()
        return ledgerService.setMonthlyBudget(userId, req.year, req.month, req.amount)
    }

    data class SetBudgetRequest(val year: Int, val month: Int, val amount: Long)

    // 위젯용 요약 조회
    @GetMapping("/widget")
    fun getWidgetSummary(
        @RequestParam year: Int,
        @RequestParam month: Int
    ): LedgerService.WidgetBudgetSummary {
        val userId = SecurityUtil.currentUserId()
        return ledgerService.getBudgetSummaryForWidget(userId, year, month)
    }
}
