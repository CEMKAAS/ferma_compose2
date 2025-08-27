package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.AddDao
import com.zaroslikov.data.room.mapper.dto.toBrieflyAddDomain
import com.zaroslikov.data.room.mapper.dto.toTitleAndSuffixDomain
import com.zaroslikov.data.room.mapper.toAddDomainMap
import com.zaroslikov.data.room.mapper.toAddRoomMap
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.dto.add.BrieflyAddDomain
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.repository.AddRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AddRepositoryImpl(private val addDao: AddDao) : AddRepository {
    override fun getItem(id: Int): Flow<DomainAddTable> {
        return addDao.getItem(id).map { it.toAddDomainMap() }
    }

    override fun getAllItems(id: Int): Flow<List<DomainAddTable>> {
        return addDao.getAllItems(id).map { it -> it.map { it.toAddDomainMap() } }
    }

    override fun getItemAdd(id: Long): Flow<DomainAddTable> {
        return addDao.getItemAdd(id).map { it.toAddDomainMap() }
    }

    override fun getBrieflyItemAdd(id: Int): Flow<List<BrieflyAddDomain>> {
        return addDao.getBrieflyItemAdd(id).map { it -> it.map { it.toBrieflyAddDomain() } }
    }

    override fun getBrieflyDetailsItemAdd(
        id: Long,
        name: String
    ): Flow<List<DomainAddTable>> {
        return addDao.getBrieflyDetailsItemAdd(id, name)
            .map { it -> it.map { it.toAddDomainMap() } }
    }

    override fun getItemsTitleAddList(id: Long): Flow<List<TitleAndSuffixDomain>> {
        return addDao.getItemsTitleAddList(id).map { it -> it.map { it.toTitleAndSuffixDomain() } }
    }

    override fun getItemsCategoryAddList(id: Long): Flow<List<String>> {
        return addDao.getItemsCategoryAddList(id)
    }

    override fun getAnimalById(id: Long): Flow<String> {
        return addDao.getAnimalById(id)
    }

    override suspend fun insert(item: DomainAddTable) {
        return addDao.insert(item.toAddRoomMap())
    }

    override suspend fun update(item: DomainAddTable) {
        return addDao.update(item.toAddRoomMap())
    }

    override suspend fun deleteAddById(id: Long) {
        return addDao.deleteAddById(id)
    }
}