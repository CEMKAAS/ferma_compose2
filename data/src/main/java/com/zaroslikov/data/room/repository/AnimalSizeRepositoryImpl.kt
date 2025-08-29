package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.AnimalSizeDao
import com.zaroslikov.data.room.mapper.table.toAnimalSizeTable
import com.zaroslikov.data.room.mapper.table.toDomainAnimalSize
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.repository.AnimalSizeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnimalSizeRepositoryImpl @Inject constructor(private val animalSizeDao: AnimalSizeDao) :
    AnimalSizeRepository {
    override suspend fun insertAnimalSizeTable(animalSizeTable: DomainAnimalSize) {
        return animalSizeDao.insertAnimalSizeTable(animalSizeTable.toAnimalSizeTable())
    }

    override suspend fun updateAnimalSizeTable(animalSizeTable: DomainAnimalSize) {
        return animalSizeDao.updateAnimalSizeTable(animalSizeTable.toAnimalSizeTable())
    }

    override suspend fun deleteAnimalSizeTable(animalSizeTable: DomainAnimalSize) {
        return animalSizeDao.deleteAnimalSizeTable(animalSizeTable.toAnimalSizeTable())
    }

    override fun getSizeAnimalLimit(id: Long): Flow<DomainAnimalSize> {
        return animalSizeDao.getSizeAnimalLimit(id).map { it.toDomainAnimalSize() }
    }

    override fun getSizeAnimal(id: Long): Flow<List<DomainAnimalSize>> {
        return animalSizeDao.getSizeAnimal(id).map { it -> it.map { it.toDomainAnimalSize() } }
    }

}