package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.AnimalVaccinationDao
import com.zaroslikov.data.room.mapper.table.toAnimalVaccinationTable
import com.zaroslikov.data.room.mapper.table.toDomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.repository.AnimalVaccinationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AnimalVaccinationRepositoryImpl(private val animalVaccinationDao: AnimalVaccinationDao) :
    AnimalVaccinationRepository {
    override suspend fun insertAnimalVaccinationTable(animalVaccinationTable: DomainAnimalVaccination): Long {
        return animalVaccinationDao.insertAnimalVaccinationTable(animalVaccinationTable.toAnimalVaccinationTable())
    }

    override suspend fun updateAnimalVaccinationTable(animalVaccinationTable: DomainAnimalVaccination) {
        return animalVaccinationDao.updateAnimalVaccinationTable(animalVaccinationTable.toAnimalVaccinationTable())
    }

    override suspend fun deleteAnimalVaccinationTable(animalVaccinationTable: DomainAnimalVaccination) {
        return animalVaccinationDao.deleteAnimalVaccinationTable(animalVaccinationTable.toAnimalVaccinationTable())
    }

    override fun getVaccinationAnimalLimit(id: Int): Flow<DomainAnimalVaccination> {
        return animalVaccinationDao.getVaccinationAnimalLimit(id)
            .map { it.toDomainAnimalVaccination() }
    }

    override fun getVaccinationAnimal(id: Int): Flow<List<DomainAnimalVaccination>> {
        return animalVaccinationDao.getVaccinationAnimal(id)
            .map { it -> it.map { it.toDomainAnimalVaccination() } }
    }
}