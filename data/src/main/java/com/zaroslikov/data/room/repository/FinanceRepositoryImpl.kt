package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.FinanceDao
import com.zaroslikov.data.room.mapper.dto.finance.toDomainIncomeExpenses
import com.zaroslikov.domain.models.dto.finance.DomainIncomeExpenses
import com.zaroslikov.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FinanceRepositoryImpl @Inject constructor(private val financeDao: FinanceDao) :
    FinanceRepository {
    override fun getCurrentBalance(id: Long): Flow<Double> {
        return financeDao.getCurrentBalance(id)
    }

    override fun getIncomeExpensesCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainIncomeExpenses>> {
        return financeDao.getIncomeExpensesCurrentMonth(id, dateBegin, dateEnd)
            .map { it -> it.map { it.toDomainIncomeExpenses() } }
    }
}