package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.AnimalCountDao
import com.zaroslikov.data.room.mapper.dto.animal.toDomainAnimalCountPrice
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.mapper.table.toRoomMap
import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.repository.AnimalCountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnimalCountRepositoryImpl @Inject constructor(private val animalCountDao: AnimalCountDao) :
    AnimalCountRepository {
    override fun getAllAnimalCountTableForExport(): Flow<List<DomainAnimalCount>> {
        return animalCountDao.getAllAnimalCountTableForExport()
            .map { it -> it.map { it.toDomainMap() } }
    }

    override suspend fun clearAndInsertAnimalCountTableForImport(domainAnimalCount: List<DomainAnimalCount>) {
        return animalCountDao.clearAndInsertAnimalCountTableForImport(domainAnimalCount.map { it.toRoomMap() })
    }

    override suspend fun insertAnimalCountTable(animalCountTable: DomainAnimalCount): Long {
        return animalCountDao.insertAnimalCountTable(animalCountTable.toRoomMap())
    }

    override suspend fun updateAnimalCountTable(animalCountTable: DomainAnimalCount) {
        return animalCountDao.updateAnimalCountTable(animalCountTable.toRoomMap())
    }

    override suspend fun deleteAnimalCountTable(id: Long) {
        return animalCountDao.deleteAnimalCountTable(id)
    }

    override fun getCountAnimalLimit(id: Long): Flow<DomainAnimalCount?> {
        return animalCountDao.getCountAnimalLimit(id).map { it?.toDomainMap() }
    }

    override fun getCountAnimal(id: Long): Flow<List<DomainAnimalCountPrice>> {
        return animalCountDao.getCountAnimal(id)
            .map { it -> it.map { it.toDomainAnimalCountPrice() } }
    }

}