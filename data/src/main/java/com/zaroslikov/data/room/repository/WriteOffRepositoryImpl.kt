package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.WriteOffDao
import com.zaroslikov.data.room.mapper.dto.write_off.toBrieflyWriteOffDomain
import com.zaroslikov.data.room.mapper.dto.write_off.toTitleWriteOffDomain
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.mapper.table.toRoomMap
import com.zaroslikov.domain.models.dto.write_off.BrieflyWriteOffDomain
import com.zaroslikov.domain.models.dto.write_off.TitleWriteOffDomain
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.repository.WriteOffRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WriteOffRepositoryImpl(private val writeOffDao: WriteOffDao) : WriteOffRepository {

    override fun getAllWriteOffItems(id: Long): Flow<List<DomainWriteOffTable>> {
        return writeOffDao.getAllWriteOffItems(id).map { it -> it.map { it.toDomainMap() } }
    }

    override fun getItemWriteOff(id: Long): Flow<DomainWriteOffTable> {
        return writeOffDao.getItemWriteOff(id).map { it.toDomainMap() }
    }

    override fun getItemWriteOffIdAnimalCount(id: Long): Flow<DomainWriteOffTable> {
        return writeOffDao.getItemWriteOffIdAnimalCount(id).map { it.toDomainMap() }
    }

    override fun getBrieflyItemWriteOff(id: Long): Flow<List<BrieflyWriteOffDomain>> {
        return writeOffDao.getBrieflyItemWriteOff(id)
            .map { it -> it.map { it.toBrieflyWriteOffDomain() } }
    }

    override fun getBrieflyDetailsItemWriteOff(
        id: Long,
        name: String
    ): Flow<List<DomainWriteOffTable>> {
        return writeOffDao.getBrieflyDetailsItemWriteOff(id, name)
            .map { it -> it.map { it.toDomainMap() } }
    }

    override fun getItemsWriteOffList(id: Long): Flow<List<TitleWriteOffDomain>> {
        return writeOffDao.getItemsWriteOffList(id)
            .map { it -> it.map { it.toTitleWriteOffDomain() } }
    }

    override suspend fun insertWriteOff(item: DomainWriteOffTable) {
        return writeOffDao.insertWriteOff(item.toRoomMap())
    }

    override suspend fun updateWriteOff(item: DomainWriteOffTable) {
        return writeOffDao.updateWriteOff(item.toRoomMap())
    }

    override suspend fun deleteWriteOff(id: Long) {
        return writeOffDao.deleteWriteOff(id)
    }

}