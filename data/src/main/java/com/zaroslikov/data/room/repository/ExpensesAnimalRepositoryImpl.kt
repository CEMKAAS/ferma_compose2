package com.zaroslikov.data.room.repository


import com.zaroslikov.data.room.dao.ExpensesAnimalDao
import com.zaroslikov.data.room.mapper.toExpensesAnimalTable
import com.zaroslikov.domain.models.DomainExpensesAnimal
import com.zaroslikov.domain.repository.ExpensesAnimalRepository
import javax.inject.Inject

class ExpensesAnimalRepositoryImpl @Inject constructor(private val expensesAnimalDao: ExpensesAnimalDao) :
    ExpensesAnimalRepository {

    override suspend fun getItemExpensesAnimal(id: Long): List<Long> {
        return expensesAnimalDao.getItemExpensesAnimal(id)
    }

    override suspend fun insertExpensesAnimal(item: DomainExpensesAnimal) {
        return expensesAnimalDao.insertExpensesAnimal(item.toExpensesAnimalTable())
    }

    override suspend fun updateExpensesAnimal(item: DomainExpensesAnimal) {
        return expensesAnimalDao.updateExpensesAnimal(item.toExpensesAnimalTable())
    }

    override suspend fun deleteExpensesAnimal(item: DomainExpensesAnimal) {
        return expensesAnimalDao.deleteExpensesAnimal(item.toExpensesAnimalTable())
    }
}