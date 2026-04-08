package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.AnimalDao
import com.zaroslikov.data.room.mapper.AnimaMapper.dto.toDomain
import com.zaroslikov.data.room.mapper.dto.animal.toAnimalForAddDomain
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.mapper.table.toRoomMap
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.domain.models.dto.animal.AnimalForAddDomain
import com.zaroslikov.domain.repository.AnimalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnimalRepositoryImpl @Inject constructor(private val animalDao: AnimalDao) :
    AnimalRepository {
    override fun getAllAnimalTableForExport(): Flow<List<DomainAnimalTable>> {
        return animalDao.getAllAnimalTableForExport()
            .map { it -> it.map { it.toDomainMap() } }
    }

    override suspend fun clearAndInsertAnimalTableForImport(domainAnimal: List<DomainAnimalTable>) {
        return animalDao.clearAndInsertAnimalTableForImport(domainAnimal.map { it.toRoomMap() })
    }

    override fun getAllAnimal(id: Long): Flow<List<DomainAnimalWithCount>> {
        return animalDao.getAllAnimal(id).map { list -> list.map { it.toDomain() } }
    }

    override fun getAnimal(id: Long): Flow<DomainAnimalTable> {
        return animalDao.getAnimal(id).map { it.toDomainMap() }
    }

    override fun getTypeAnimal(id: Long): Flow<List<String>> {
        return animalDao.getTypeAnimal(id)
    }

    override fun getItemsAnimalAddList(id: Long): Flow<List<AnimalForAddDomain>> {
        return animalDao.getItemsAnimalAddList(id)
            .map { it -> it.map { it.toAnimalForAddDomain() } }
    }

    override suspend fun insertAnimalTable(animalTable: DomainAnimalTable): Long {
        return animalDao.insertAnimalTable(animalTable.toRoomMap())
    }

    override suspend fun updateAnimalTable(animalTable: DomainAnimalTable) {
        return animalDao.updateAnimalTable(animalTable.toRoomMap())
    }

    override suspend fun deleteAnimalTable(id: Long) {
        return animalDao.deleteAnimalTable(id)
    }

    override suspend fun updateArchiveAnimalTableById(isArchive: Boolean, id: Long) {
        return animalDao.updateArchiveAnimalTableById(isArchive = isArchive, id = id)
    }

    /* override fun getExpensesAnimalAllList(id: Long): Flow<List<Fin>> {
         return animalDao.getExpensesAnimalAllList(id)
     }

     override fun getProductLisCategoryExpensesAnimalCurrentMonth(
         id: Long,
         dateBegin: String,
         dateEnd: String
     ): Flow<List<Fin>> {
         return animalDao.getProductLisCategoryExpensesAnimalCurrentMonth(id, dateBegin, dateEnd)
     }
 */
}