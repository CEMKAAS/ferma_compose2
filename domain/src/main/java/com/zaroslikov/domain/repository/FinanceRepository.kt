package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.dto.finance.DomainIncomeExpenses
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun getCurrentBalance(id: Long): Flow<Double>
    fun getIncomeExpensesCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainIncomeExpenses>>
}