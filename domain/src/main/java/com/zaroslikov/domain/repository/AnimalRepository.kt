package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.domain.models.dto.animal.AnimalForAddDomain
import kotlinx.coroutines.flow.Flow

interface AnimalRepository {
    fun getAllAnimal(id: Long): Flow<List<DomainAnimalWithCount>>
    fun getAnimal(id: Long): Flow<DomainAnimalTable>
    fun getTypeAnimal(id: Long): Flow<List<String>>
    fun getItemsAnimalAddList(id: Long): Flow<List<AnimalForAddDomain>>
    suspend fun insertAnimalTable(animalTable: DomainAnimalTable): Long
    suspend fun updateAnimalTable(animalTable: DomainAnimalTable)
    suspend fun deleteAnimalTable(id: Long)

   /* fun getExpensesAnimalAllList(id: Long): Flow<List<Fin>>//0 = price  //maybe
    fun getProductLisCategoryExpensesAnimalCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>>  //maybe*/
}