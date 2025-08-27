package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.ExpensesDao
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDomain
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.data.room.mapper.dto.animal.toAnimalExpensesDomain
import com.zaroslikov.data.room.mapper.dto.expenses.toBrieflyExpensesDomain
import com.zaroslikov.data.room.mapper.dto.toTitleAndSuffixDomain
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.mapper.toExpensesRoomMap
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.repository.ExpensesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpensesRepositoryImpl(val expensesDao: ExpensesDao) : ExpensesRepository {

    override fun getAllExpensesItems(id: Int): Flow<List<DomainExpensesTable>> {
        return expensesDao.getAllExpensesItems(id).map { it -> it.map { it.toDomainMap() } }
    }

    override fun getItemExpenses(id: Int): Flow<DomainExpensesTable> {
        return expensesDao.getItemExpenses(id).map { it.toDomainMap() }
    }

    override fun getItemExpensesIdAnimalCount(id: Int): Flow<DomainExpensesTable> {
        return expensesDao.getItemExpensesIdAnimalCount(id).map { it.toDomainMap() }
    }

    override fun getItemExpensesForVaccination(id: Long): Flow<DomainExpensesTable> {
        return expensesDao.getItemExpensesForVaccination(id).map { it.toDomainMap() }
    }

    override fun getBrieflyItemExpenses(id: Int): Flow<List<BrieflyExpensesDomain>> {
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

    override fun getItemsTitleExpensesList(id: Int): Flow<List<TitleAndSuffixDomain>> {
        return expensesDao.getItemsTitleExpensesList(id)
            .map { it -> it.map { it.toTitleAndSuffixDomain() } }
    }

    override fun getItemsCategoryExpensesList(id: Int): Flow<List<String>> {
        return expensesDao.getItemsCategoryExpensesList(id)
    }

    override suspend fun getItemsAnimalExpensesList2(
        id: Int,
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

}