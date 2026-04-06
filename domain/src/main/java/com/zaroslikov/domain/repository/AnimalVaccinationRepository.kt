package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.dto.animal.AnimalVaccinationExpensesDomain
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import kotlinx.coroutines.flow.Flow

interface AnimalVaccinationRepository {
    fun getAllAnimalVaccinationTableForExport(): Flow<List<DomainAnimalVaccination>>
    suspend fun clearAndInsertAnimalVaccinationTableForImport(domainAnimalVaccination: List<DomainAnimalVaccination>)
    suspend fun insertAnimalVaccinationTable(animalVaccinationTable: DomainAnimalVaccination): Long
    suspend fun updateAnimalVaccinationTable(animalVaccinationTable: DomainAnimalVaccination)
    suspend fun deleteAnimalVaccinationTable(animalVaccinationTable: DomainAnimalVaccination)
    suspend fun deleteAnimalVaccinationTableById(id: Long)
    fun getVaccinationAnimalLimit(id: Long): Flow<DomainAnimalVaccination?>
    fun getVaccinationAnimal(id: Long): Flow<List<DomainAnimalVaccination>>
    fun getVaccinationExpensesAnimal(id: Long): Flow<List<AnimalVaccinationExpensesDomain>>
    fun getTitleVaccinationAnimalList(id: Long): Flow<List<String>>
}