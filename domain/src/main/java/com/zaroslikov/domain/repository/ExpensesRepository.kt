package com.zaroslikov.domain.repository

import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.shared.DomainCategoryPrice
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainIncubatorParameters
import kotlinx.coroutines.flow.Flow
import java.util.Currency

interface ExpensesRepository {
    fun getAllExpensesTableForExport(): Flow<List<DomainExpensesTable>>
    suspend fun clearAndInsertExpensesTableForImport(domainExpensesTable: List<DomainExpensesTable>)
    fun getAllExpensesItems(id: Long): Flow<List<DomainExpensesTable>>
    fun getItemExpenses(id: Long): Flow<DomainExpensesTable>
    fun getItemExpensesIdAnimalCount(id: Long): Flow<DomainExpensesTable>
    fun getItemExpensesForVaccination(id: Long): Flow<DomainExpensesTable?>
    fun getBrieflyItemExpenses(id: Long): Flow<List<BrieflyExpensesDomain>>
    fun getBrieflyDetailsItemExpenses(id: Long, name: String): Flow<List<DomainExpensesTable>>
    fun getItemsTitleExpensesList(id: Long): Flow<List<TitleAndSuffixDomain>>
    fun getItemsCategoryExpensesList(id: Long): Flow<List<String>>
    fun getItemsAnimalExpensesList2(
        id: Long,
        idExpenses: Long
    ): Flow<List<AnimalExpensesDomain>>

    suspend fun insertExpenses(item: DomainExpensesTable): Long
    suspend fun updateExpenses(item: DomainExpensesTable)
    suspend fun deleteExpenses(item: DomainExpensesTable)
    suspend fun deleteExpensesById(id: Long)

    fun getExpenses(id: Long): Flow<Double> //Finance
    fun getExpensesAllProject(currencySuffix: Suffix) : Flow<Double>
    fun getExpensesMountFin(
        id: Long,
        mount: Int,
        year: Int,
    ): Flow<Double>

    fun getExpensesMount(id: Long, dateBegin: String, dateEnd: String): Flow<Double>
    fun getExpensesAllList(id: Long): Flow<List<DomainTitleSuffixPrice>>
    fun getExpensesCategoryAllList(id: Long): Flow<List<DomainCategoryPrice>>  //maybe
    fun getCategoryExpensesCurrentMonth(
        id: Long, dateBegin: String, dateEnd: String
    ): Flow<List<DomainCategoryPrice>> //maybe

    fun getProductLisCategoryExpensesCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainTitleSuffixPrice>> //maybe

    fun getCurrentFoodWarehouse(id: Long): Flow<List<DomainExpensesTable>>
    suspend fun updateFoodOnWriteOffWarehouse(id: Long)
    fun getAnalysisExpensesNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    /* fun getAnalysisExpensesProductNewYearProject(
         id: Long,
         dateBegin: String,
         dateEnd: String
     ): Flow<List<AnalysisSaleBuyerAllTime>>

     fun getAnalysisExpensesNewYear(
         dateBegin: String,
         dateEnd: String
     ): Flow<Double>

     fun getAnalysisExpensesProductNewYear(
         dateBegin: String,
         dateEnd: String
     ): Flow<List<AnalysisSaleBuyerAllTime>>*/

}