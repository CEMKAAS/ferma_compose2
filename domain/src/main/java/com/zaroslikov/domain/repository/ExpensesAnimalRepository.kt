package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.DomainExpensesAnimal
import com.zaroslikov.domain.models.DomainExpensesTable
import kotlinx.coroutines.flow.Flow


interface ExpensesAnimalRepository {
    fun getAllExpensesAnimalTableForExport(): Flow<List<DomainExpensesAnimal>>

    suspend fun clearAndInsertExpensesAnimalTableForImport(domainExpensesAnimal: List<DomainExpensesAnimal>)
    suspend fun getItemExpensesAnimal(id: Long): List<Long>
    suspend fun insertExpensesAnimal(item: DomainExpensesAnimal)
    suspend fun updateExpensesAnimal(item: DomainExpensesAnimal)
    suspend fun deleteExpensesAnimal(item: DomainExpensesAnimal)
    suspend fun deleteExpensesAnimalById(id: Long)
}