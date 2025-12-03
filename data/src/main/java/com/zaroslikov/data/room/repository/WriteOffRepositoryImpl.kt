package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.WriteOffDao
import com.zaroslikov.data.room.mapper.dto.shared.toDomainCategoryPrice
import com.zaroslikov.data.room.mapper.dto.shared.toDomainCountSuffix
import com.zaroslikov.data.room.mapper.dto.shared.toDomainTitleSuffixCategory
import com.zaroslikov.data.room.mapper.dto.shared.toDomainTitleSuffixPrice
import com.zaroslikov.data.room.mapper.dto.write_off.toBrieflyWriteOffDomain
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.mapper.table.toRoomMap
import com.zaroslikov.domain.models.dto.shared.DomainCategoryPrice
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice
import com.zaroslikov.domain.models.dto.write_off.BrieflyWriteOffDomain
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.repository.WriteOffRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WriteOffRepositoryImpl @Inject constructor(private val writeOffDao: WriteOffDao) :
    WriteOffRepository {

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

    override fun getItemsWriteOffList(id: Long): Flow<List<DomainTitleSuffixCategory>> {
        return writeOffDao.getItemsWriteOffList(id)
            .map { it -> it.map { it.toDomainTitleSuffixCategory() } }
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

    override fun getOwnNeedAllList(id: Long): Flow<List<DomainTitleSuffixPrice>> {
        return writeOffDao.getOwnNeedAllList(id)
            .map { it -> it.map { it.toDomainTitleSuffixPrice() } }
    }

    override fun getScrapAllList(id: Long): Flow<List<DomainTitleSuffixPrice>> {
        return writeOffDao.getScrapAllList(id)
            .map { it -> it.map { it.toDomainTitleSuffixPrice() } }
    }

    override fun getOwnNeedAllCategoryAllList(id: Long): Flow<List<DomainCategoryPrice>> {
        return writeOffDao.getOwnNeedAllCategoryAllList(id)
            .map { it -> it.map { it.toDomainCategoryPrice() } }
    }

    override fun getScrapAllCategoryAllList(id: Long): Flow<List<DomainCategoryPrice>> {
        return writeOffDao.getScrapAllCategoryAllList(id)
            .map { it -> it.map { it.toDomainCategoryPrice() } }

    }

    override fun getOwnNeed(id: Long): Flow<Double> {
        return writeOffDao.getOwnNeed(id)
    }

    override fun getScrap(id: Long): Flow<Double> {
        return writeOffDao.getScrap(id)
    }

    override fun getOwnNeedMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> {
        return writeOffDao.getOwnNeedMonth(id, dateBegin, dateEnd)
    }

    override fun getScrapMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> {
        return writeOffDao.getScrapMonth(id, dateBegin, dateEnd)
    }

    override fun getAnalysisWriteOffAllTime(
        id: Long,
        name: String
    ): Flow<DomainCountSuffix> {
        return writeOffDao.getAnalysisWriteOffAllTime(id, name).map { it.toDomainCountSuffix() }
    }

    override fun getAnalysisWriteOffOwnNeedsAllTime(
        id: Long,
        name: String
    ): Flow<DomainCountSuffix> {
        return writeOffDao.getAnalysisWriteOffOwnNeedsAllTime(id, name)
            .map { it.toDomainCountSuffix() }
    }

    override fun getAnalysisWriteOffScrapAllTime(
        id: Long,
        name: String
    ): Flow<DomainCountSuffix> {
        return writeOffDao.getAnalysisWriteOffScrapAllTime(id, name)
            .map { it.toDomainCountSuffix() }
    }

    override fun getAnalysisWriteOffOwnNeedsMoneyAllTime(
        id: Long,
        name: String
    ): Flow<Double> {
        return writeOffDao.getAnalysisWriteOffOwnNeedsMoneyAllTime(id, name)
    }

    override fun getAnalysisWriteOffScrapMoneyAllTime(
        id: Long,
        name: String
    ): Flow<Double> {
        return writeOffDao.getAnalysisWriteOffScrapMoneyAllTime(id, name)
    }

    override fun getAnalysisWriteOffAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<DomainCountSuffix> {
        return writeOffDao.getAnalysisWriteOffAllTimeRange(id, name, dateBegin, dateEnd)
            .map { it.toDomainCountSuffix() }
    }

    override fun getAnalysisWriteOffOwnNeedsAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<DomainCountSuffix> {
        return writeOffDao.getAnalysisWriteOffOwnNeedsAllTimeRange(id, name, dateBegin, dateEnd)
            .map { it.toDomainCountSuffix() }
    }

    override fun getAnalysisWriteOffScrapAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<DomainCountSuffix> {
        return writeOffDao.getAnalysisWriteOffScrapAllTimeRange(id, name, dateBegin, dateEnd)
            .map { it.toDomainCountSuffix() }
    }

    override fun getAnalysisWriteOffOwnNeedsMoneyAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> {
        return writeOffDao.getAnalysisWriteOffOwnNeedsMoneyAllTimeRange(
            id,
            name,
            dateBegin,
            dateEnd
        )
    }

    override fun getAnalysisWriteOffScrapMoneyAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> {
        return writeOffDao.getAnalysisWriteOffScrapMoneyAllTimeRange(id, name, dateBegin, dateEnd)
    }

    override fun getAnalysisWriteOffOwnNeedsNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> {
        return writeOffDao.getAnalysisWriteOffOwnNeedsNewYearProject(id, dateBegin, dateEnd)
    }

    override fun getAnalysisWriteOffScrapNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> {
        return writeOffDao.getAnalysisWriteOffScrapNewYearProject(id, dateBegin, dateEnd)
    }

    override fun getAnalysisWriteOffOwnNeedsNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> {
        return writeOffDao.getAnalysisWriteOffOwnNeedsNewYear(dateBegin, dateEnd)
    }

    override fun getAnalysisWriteOffScrapNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double> {
        return writeOffDao.getAnalysisWriteOffScrapNewYear(dateBegin, dateEnd)
    }

}