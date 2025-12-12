package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.ExpensesDao
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.data.room.mapper.dto.animal.toAnimalExpensesDomain
import com.zaroslikov.data.room.mapper.dto.expenses.toBrieflyExpensesDomain
import com.zaroslikov.data.room.mapper.dto.shared.toDomainCategoryPrice
import com.zaroslikov.data.room.mapper.dto.shared.toDomainTitleSuffixPrice
import com.zaroslikov.data.room.mapper.dto.toTitleAndSuffixDomain
import com.zaroslikov.data.room.mapper.toDomainMap
import com.zaroslikov.data.room.mapper.toExpensesRoomMap
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.shared.DomainCategoryPrice
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice
import com.zaroslikov.domain.repository.ExpensesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpensesRepositoryImpl @Inject constructor(private val expensesDao: ExpensesDao) :
    ExpensesRepository {

    override fun getAllExpensesItems(id: Long): Flow<List<DomainExpensesTable>> {
        return expensesDao.getAllExpensesItems(id).map { it -> it.map { it.toDomainMap() } }
    }

    override fun getItemExpenses(id: Long): Flow<DomainExpensesTable> {
        return expensesDao.getItemExpenses(id).map { it.toDomainMap() }
    }

    override fun getItemExpensesIdAnimalCount(id: Long): Flow<DomainExpensesTable> {
        return expensesDao.getItemExpensesIdAnimalCount(id).map { it.toDomainMap() }
    }

    override fun getItemExpensesForVaccination(id: Long): Flow<DomainExpensesTable?> {
        return expensesDao.getItemExpensesForVaccination(id).map { it?.toDomainMap() }
    }

    override fun getBrieflyItemExpenses(id: Long): Flow<List<BrieflyExpensesDomain>> {
        return expensesDao.getBrieflyItemExpenses(id)
            .map { it -> it.map { it.toBrieflyExpensesDomain() } }
    }

    override fun getBrieflyDetailsItemExpenses(
        id: Long,
        name: String
    ): Flow<List<DomainExpensesTable>> {
        return expensesDao.getBrieflyDetailsItemExpenses(id, name)
            .map { it -> it.map { it.toDomainMap() } }
    }

    override fun getItemsTitleExpensesList(id: Long): Flow<List<TitleAndSuffixDomain>> {
        return expensesDao.getItemsTitleExpensesList(id)
            .map { it -> it.map { it.toTitleAndSuffixDomain() } }
    }

    override fun getItemsCategoryExpensesList(id: Long): Flow<List<String>> {
        return expensesDao.getItemsCategoryExpensesList(id)
    }

    override fun getItemsAnimalExpensesList2(
        id: Long,
        idExpenses: Long
    ): Flow<List<AnimalExpensesDomain>> {
        return expensesDao.getItemsAnimalExpensesList2(id, idExpenses)
            .map { it -> it.map { it.toAnimalExpensesDomain() } }
    }

    override suspend fun insertExpenses(item: DomainExpensesTable): Long {
        return expensesDao.insertExpenses(item.toExpensesRoomMap())
    }

    override suspend fun updateExpenses(item: DomainExpensesTable) {
        return expensesDao.updateExpenses(item.toExpensesRoomMap())
    }

    override suspend fun deleteExpenses(item: DomainExpensesTable) {
        return expensesDao.deleteExpenses(item.toExpensesRoomMap())
    }

    override suspend fun deleteExpensesById(id: Long) {
        return expensesDao.deleteExpensesById(id)
    }

    override fun getExpenses(id: Long): Flow<Double> {
        return expensesDao.getExpenses(id)
    }

    override fun getExpensesMountFin(
        id: Long,
        mount: Int,
        year: Int
    ): Flow<Double> {
        return expensesDao.getExpensesMountFin(id, mount, year)
    }

    override fun getExpensesMount(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> {
        return expensesDao.getExpensesMount(id, dateBegin, dateEnd)
    }

    override fun getExpensesAllList(id: Long): Flow<List<DomainTitleSuffixPrice>> {
        return expensesDao.getExpensesAllList(id)
            .map { it -> it.map { it.toDomainTitleSuffixPrice() } }
    }

    override fun getExpensesCategoryAllList(id: Long): Flow<List<DomainCategoryPrice>> {
        return expensesDao.getExpensesCategoryAllList(id)
            .map { it -> it.map { it.toDomainCategoryPrice() } }
    }

    override fun getCategoryExpensesCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainCategoryPrice>> {
        return expensesDao.getCategoryExpensesCurrentMonth(id, dateBegin, dateEnd)
            .map { it -> it.map { it.toDomainCategoryPrice() } }
    }

    override fun getProductLisCategoryExpensesCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String,
    ): Flow<List<DomainTitleSuffixPrice>> {
        return expensesDao.getProductLisCategoryExpensesCurrentMonth(
            id,
            dateBegin,
            dateEnd
        ).map { it -> it.map { it.toDomainTitleSuffixPrice() } }
    }

    override fun getCurrentFoodWarehouse(id: Long): Flow<List<DomainExpensesTable>> {
        return expensesDao.getCurrentFoodWarehouse(id).map { it -> it.map { it.toDomainMap() } }
    }

    override fun getAnalysisExpensesNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> {
        return expensesDao.getAnalysisExpensesNewYearProject(id, dateBegin, dateEnd)
    }

    /*override fun getAnalysisExpensesProductNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>> {
        return expensesDao.getAnalysisExpensesProductNewYearProject(id, dateBegin, dateEnd)
    }

    override fun getAnalysisExpensesNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> {
        return expensesDao.getAnalysisExpensesNewYear(dateBegin, dateEnd)
    }

    override fun getAnalysisExpensesProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>> {
        return expensesDao.getAnalysisExpensesProductNewYear(dateBegin, dateEnd)
    }*/

}