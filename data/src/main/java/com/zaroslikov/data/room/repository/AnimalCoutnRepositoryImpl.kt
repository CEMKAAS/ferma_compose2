package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.AnimalCountDao
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.mapper.table.toRoomMap
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.repository.AnimalCountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AnimalCountRepositoryImpl(private val animalCountDao: AnimalCountDao) :
    AnimalCountRepository {
    override suspend fun insertAnimalCountTable(animalCountTable: DomainAnimalCount): Long {
        return animalCountDao.insertAnimalCountTable(animalCountTable.toRoomMap())
    }

    override suspend fun updateAnimalCountTable(animalCountTable: DomainAnimalCount) {
        return animalCountDao.updateAnimalCountTable(animalCountTable.toRoomMap())
    }

    override suspend fun deleteAnimalCountTable(animalCountTable: DomainAnimalCount) {
        return animalCountDao.deleteAnimalCountTable(animalCountTable.toRoomMap())
    }

    override fun getCountAnimalLimit(id: Int): Flow<DomainAnimalCount> {
        return animalCountDao.getCountAnimalLimit(id).map { it.toDomainMap() }
    }

    override fun getCountAnimal(id: Int): Flow<List<DomainAnimalCount>> {
        return animalCountDao.getCountAnimal(id).map { it -> it.map { it.toDomainMap() } }
    }

}