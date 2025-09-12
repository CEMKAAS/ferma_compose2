package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.AnimalWeightDao
import com.zaroslikov.data.room.mapper.table.toAnimalWeightTable
import com.zaroslikov.data.room.mapper.table.toDomainAnimalWeight
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.domain.repository.AnimalWeightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnimalWeightRepositoryImpl @Inject constructor(private val animalWeightDao: AnimalWeightDao) :
    AnimalWeightRepository {

    override suspend fun insertAnimalWeightTable(animalWeightTable: DomainAnimalWeight) {
        return animalWeightDao.insertAnimalWeightTable(animalWeightTable.toAnimalWeightTable())
    }

    override suspend fun updateAnimalWeightTable(animalWeightTable: DomainAnimalWeight) {
        return animalWeightDao.updateAnimalWeightTable(animalWeightTable.toAnimalWeightTable())
    }

    override suspend fun deleteAnimalWeightTable(animalWeightTable: DomainAnimalWeight) {
        return animalWeightDao.deleteAnimalWeightTable(animalWeightTable.toAnimalWeightTable())
    }

    override fun getWeightAnimalLimit(id: Long): Flow<DomainAnimalWeight?> {
        return animalWeightDao.getWeightAnimalLimit(id).map { it?.toDomainAnimalWeight() }
    }

    override fun getWeightAnimal(id: Long): Flow<List<DomainAnimalWeight>> {
        return animalWeightDao.getWeightAnimal(id)
            .map { it -> it.map { it.toDomainAnimalWeight() } }
    }
}