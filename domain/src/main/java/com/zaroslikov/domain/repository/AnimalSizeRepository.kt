package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.table.DomainAnimalSize
import kotlinx.coroutines.flow.Flow

interface AnimalSizeRepository {

    suspend fun insertAnimalSizeTable(animalSizeTable: DomainAnimalSize)
    suspend fun updateAnimalSizeTable(animalSizeTable: DomainAnimalSize)
    suspend fun deleteAnimalSizeTable(animalSizeTable: DomainAnimalSize)
    suspend fun deleteAnimalSizeTableById(id: Long)
    fun getSizeAnimalLimit(id: Long): Flow<DomainAnimalSize?>
    fun getSizeAnimal(id: Long): Flow<List<DomainAnimalSize>>
}