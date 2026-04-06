package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.app.DomainAppSettings
import kotlinx.coroutines.flow.Flow

interface AnimalCountRepository {
    fun getAllAnimalCountTableForExport(): Flow<List<DomainAnimalCount>>

    suspend fun clearAndInsertAnimalCountTableForImport(domainAnimalCount: List<DomainAnimalCount>)
    suspend fun insertAnimalCountTable(animalCountTable: DomainAnimalCount): Long
    suspend fun updateAnimalCountTable(animalCountTable: DomainAnimalCount)
    suspend fun deleteAnimalCountTable(id: Long)
    fun getCountAnimalLimit(id: Long): Flow<DomainAnimalCount?>
    fun getCountAnimal(id: Long): Flow<List<DomainAnimalCountPrice>>
}