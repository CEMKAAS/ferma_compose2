package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.table.DomainAnimalCount
import kotlinx.coroutines.flow.Flow

interface AnimalCountRepository {

    suspend fun insertAnimalCountTable(animalCountTable: DomainAnimalCount): Long
    suspend fun updateAnimalCountTable(animalCountTable: DomainAnimalCount)
    suspend fun deleteAnimalCountTable(animalCountTable: DomainAnimalCount)
    fun getCountAnimalLimit(id: Int): Flow<DomainAnimalCount>
    fun getCountAnimal(id: Int): Flow<List<DomainAnimalCount>>
}