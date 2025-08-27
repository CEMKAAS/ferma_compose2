package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.table.DomainAnimalWeight
import kotlinx.coroutines.flow.Flow

interface AnimalWeightRepository {
    suspend fun insertAnimalWeightTable(animalWeightTable: DomainAnimalWeight)
    suspend fun updateAnimalWeightTable(animalWeightTable:  DomainAnimalWeight)
    suspend fun deleteAnimalWeightTable(animalWeightTable:  DomainAnimalWeight)
    fun getWeightAnimalLimit(id: Int): Flow< DomainAnimalWeight>
    fun getWeightAnimal(id: Int): Flow<List< DomainAnimalWeight>>
}