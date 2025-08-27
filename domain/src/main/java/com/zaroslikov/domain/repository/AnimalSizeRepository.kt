package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.table.DomainAnimalSize
import kotlinx.coroutines.flow.Flow

interface AnimalSizeRepository {

    suspend fun insertAnimalSizeTable(animalSizeTable: DomainAnimalSize)
    suspend fun updateAnimalSizeTable(animalSizeTable: DomainAnimalSize)
    suspend fun deleteAnimalSizeTable(animalSizeTable: DomainAnimalSize)
    fun getSizeAnimalLimit(id: Int): Flow<DomainAnimalSize>
    fun getSizeAnimal(id: Int): Flow<List<DomainAnimalSize>>
}