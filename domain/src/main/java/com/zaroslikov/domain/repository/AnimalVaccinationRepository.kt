package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import kotlinx.coroutines.flow.Flow

interface AnimalVaccinationRepository {
    suspend fun insertAnimalVaccinationTable(animalVaccinationTable: DomainAnimalVaccination): Long
    suspend fun updateAnimalVaccinationTable(animalVaccinationTable: DomainAnimalVaccination)
    suspend fun deleteAnimalVaccinationTable(animalVaccinationTable: DomainAnimalVaccination)
    fun getVaccinationAnimalLimit(id: Long): Flow<DomainAnimalVaccination?>
    fun getVaccinationAnimal(id: Long): Flow<List<DomainAnimalVaccination>>
}