package com.zaroslikov.domain.repository

import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import kotlinx.coroutines.flow.Flow

interface ExpensesRepository {
    fun getAllExpensesItems(id: Int): Flow<List<DomainExpensesTable>>
    fun getItemExpenses(id: Int): Flow<DomainExpensesTable>
    fun getItemExpensesIdAnimalCount(id: Int): Flow<DomainExpensesTable>
    fun getItemExpensesForVaccination(id: Long): Flow<DomainExpensesTable>
    fun getBrieflyItemExpenses(id: Int): Flow<List<BrieflyExpensesDomain>>
    fun getBrieflyDetailsItemExpenses(id: Long, name: String): Flow<List<DomainExpensesTable>>
    fun getItemsTitleExpensesList(id: Int): Flow<List<TitleAndSuffixDomain>>
    fun getItemsCategoryExpensesList(id: Int): Flow<List<String>>
    suspend fun getItemsAnimalExpensesList2(id: Int, idExpenses: Long): Flow<List<AnimalExpensesDomain>>
    suspend fun insertExpenses(item: DomainExpensesTable): Long
    suspend fun updateExpenses(item: DomainExpensesTable)
    suspend fun deleteExpenses(item: DomainExpensesTable)
}