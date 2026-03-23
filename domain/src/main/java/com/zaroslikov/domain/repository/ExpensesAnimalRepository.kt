package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.DomainExpensesAnimal


interface ExpensesAnimalRepository {
    suspend fun getItemExpensesAnimal(id: Long): List<Long>
    suspend fun insertExpensesAnimal(item: DomainExpensesAnimal)
    suspend fun updateExpensesAnimal(item: DomainExpensesAnimal)
    suspend fun deleteExpensesAnimal(item: DomainExpensesAnimal)
    suspend fun deleteExpensesAnimalById(id: Long)
}