package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.animal.breed.DomainAnimalBreed
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.domain.models.dto.animal.AnimalForAddDomain
import com.zaroslikov.domain.models.table.DomainAnimalHerd
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.models.table.app.DomainAppSettings
import kotlinx.coroutines.flow.Flow

interface AnimalRepository {
    fun getAllAnimalTableForExport(): Flow<List<DomainAnimalTable>>
    /** [herds] — сборные группы бэкапа (уже импортированные): по ним проверяется herdId. */
    suspend fun clearAndInsertAnimalTableForImport(domainAnimal: List<DomainAnimalTable>, herds: List<DomainAnimalHerd>)
    fun getAllAnimalWithGroups(id: Long): Flow<List<DomainAnimalWithCount>>
    fun getAnimalWithGroup(id: Long): Flow<DomainAnimalWithCount?>
    fun getAnimalNames(idPT: Long): Flow<List<String>>
    fun getAnimal(id: Long): Flow<DomainAnimalTable>
    fun getTypeAnimal(id: Long): Flow<List<String>>

    /** Породы неархивных животных проекта с видом — подсказки фильтруются по виду. */
    fun getBreedAnimal(id: Long): Flow<List<DomainAnimalBreed>>
    fun getItemsAnimalAddList(id: Long): Flow<List<AnimalForAddDomain>>
    suspend fun insertAnimalTable(animalTable: DomainAnimalTable): Long
    suspend fun updateAnimalTable(animalTable: DomainAnimalTable)
    suspend fun deleteAnimalTable(id: Long)

    suspend fun updateArchiveAnimalTableById(isArchive: Boolean, id: Long)

   /* fun getExpensesAnimalAllList(id: Long): Flow<List<Fin>>//0 = price  //maybe
    fun getProductLisCategoryExpensesAnimalCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>>  //maybe*/
}