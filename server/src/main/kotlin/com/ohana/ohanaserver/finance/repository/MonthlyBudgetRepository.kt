package com.ohana.ohanaserver.finance.repository

import com.ohana.ohanaserver.finance.domain.MonthlyBudget
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MonthlyBudgetRepository : JpaRepository<MonthlyBudget, UUID> {
    fun findByGroupIdAndYearMonth(groupId: UUID, yearMonth: String): MonthlyBudget?
}
