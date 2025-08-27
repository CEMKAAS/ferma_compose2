package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.AnimalDao
import com.zaroslikov.data.room.mapper.AnimaMapper.dto.toDomain
import com.zaroslikov.data.room.mapper.dto.animal.toAnimalForAddDomain
import com.zaroslikov.data.room.mapper.table.toRoomMap
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.domain.models.dto.animal.AnimalForAddDomain
import com.zaroslikov.domain.repository.AnimalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AnimalRepositoryImpl(private val animalDao: AnimalDao) : AnimalRepository {
    override fun getAllAnimal(id: Long): Flow<List<DomainAnimalWithCount>> {
        return animalDao.getAllAnimal(id).map { list -> list.map { it.toDomain() } }
    }

    override fun getAnimal(id: Long): Flow<DomainAnimalTable> {
        return animalDao.getAnimal(id).map { it.toDomain() }
    }

    override fun getTypeAnimal(id: Long): Flow<List<String>> {
        return animalDao.getTypeAnimal(id)
    }

    override fun getItemsAnimalAddList(id: Long): Flow<List<AnimalForAddDomain>> {
        return animalDao.getItemsAnimalAddList(id).map { it->it.map { it.toAnimalForAddDomain() } }
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

}