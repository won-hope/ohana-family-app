package com.ohana.ohanaserver.finance.controller

import com.ohana.ohanaserver.auth.util.SecurityUtil
import com.ohana.ohanaserver.finance.domain.LedgerTransaction
import com.ohana.ohanaserver.finance.domain.MonthlyBudget
import com.ohana.ohanaserver.finance.domain.TransactionType
import com.ohana.ohanaserver.finance.service.LedgerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Tag(name = "가계부", description = "가족 공유 가계부 관련 API")
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

    @Operation(summary = "가계부 내역 기록", description = "수입/지출/이체 내역을 기록합니다.")
    @PostMapping
    fun record(@RequestBody req: RecordRequest): LedgerTransaction {
        val userId = SecurityUtil.currentUserId()
        return ledgerService.recordTransaction(
            userId, req.type, req.amount, req.date, req.category, req.paymentMethod, req.memo
        )
    }

    @Operation(summary = "월간 가계부 조회", description = "특정 연월의 가계부 요약(총수입, 총지출) 및 전체 내역을 조회합니다.")
    @GetMapping("/monthly")
    fun getMonthlySummary(
        @RequestParam year: Int,
        @RequestParam month: Int
    ): LedgerService.MonthlySummary {
        val userId = SecurityUtil.currentUserId()
        return ledgerService.getMonthlySummary(userId, year, month)
    }

    @Operation(summary = "월별 예산 설정", description = "특정 연월의 목표 예산을 설정합니다.")
    @PostMapping("/budget")
    fun setBudget(@RequestBody req: SetBudgetRequest): MonthlyBudget {
        val userId = SecurityUtil.currentUserId()
        return ledgerService.setMonthlyBudget(userId, req.year, req.month, req.amount)
    }

    data class SetBudgetRequest(val year: Int, val month: Int, val amount: Long)

    @Operation(summary = "위젯용 예산 요약 조회", description = "위젯을 위한 가벼운 예산 요약 정보를 조회합니다.")
    @GetMapping("/widget")
    fun getWidgetSummary(
        @RequestParam year: Int,
        @RequestParam month: Int
    ): LedgerService.WidgetBudgetSummary {
        val userId = SecurityUtil.currentUserId()
        return ledgerService.getBudgetSummaryForWidget(userId, year, month)
    }
}
