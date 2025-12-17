package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.dto.finance.DomainIncomeExpenses
import com.zaroslikov.domain.models.dto.finance.DomainTransaction
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun getCurrentBalance(id: Long): Flow<Double>
    fun getIncomeExpensesCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainIncomeExpenses>>

    fun getAnalysisTransactionList(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainTransaction>>
}