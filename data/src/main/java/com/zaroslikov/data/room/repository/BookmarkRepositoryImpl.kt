package com.zaroslikov.data.room.repository

import android.R
import com.zaroslikov.data.room.dao.BookmarkDao
import com.zaroslikov.data.room.mapper.dto.incubator.toDomainCountRejectedCount
import com.zaroslikov.data.room.mapper.dto.incubator.toDomainFinanceAllIncubator
import com.zaroslikov.data.room.mapper.dto.incubator.toDomainFinanceIncubatorHistory
import com.zaroslikov.data.room.mapper.dto.incubator.toDomainFinanceIncubatorMain
import com.zaroslikov.data.room.mapper.dto.incubator.toDomainTypeEggCount
import com.zaroslikov.data.room.mapper.dto.incubator.toTitleCountDto
import com.zaroslikov.data.room.mapper.table.toBookmarkTable
import com.zaroslikov.data.room.mapper.table.toDomainBookmark
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.domain.models.dto.incubator.DomainCountRejectedCount
import com.zaroslikov.domain.models.dto.incubator.DomainFinanceAllIncubator
import com.zaroslikov.domain.models.dto.incubator.DomainFinanceIncubatorHistory
import com.zaroslikov.domain.models.dto.incubator.DomainFinanceIncubatorMain
import com.zaroslikov.domain.models.dto.incubator.DomainTitleCount
import com.zaroslikov.domain.models.dto.incubator.DomainTypeEggCount
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.domain.models.table.DomainBookmark
import com.zaroslikov.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

class BookmarkRepositoryImpl @Inject constructor(private val bookmarkDao: BookmarkDao) :
    BookmarkRepository {
    override fun getAllBookmarkTableForExport(): Flow<List<DomainBookmark>> {
        return bookmarkDao.getAllBookmarkIncubatorTableForExport()
            .map { it -> it.map { it.toDomainBookmark() } }
    }

    override suspend fun clearAndInsertBookmarkTableForImport(domainBookmark: List<DomainBookmark>) {
        return bookmarkDao.clearAndInsertBookmarkTableForImport(domainBookmark.map { it.toBookmarkTable() })
    }

    override fun getBookmark(id: Long): Flow<DomainBookmark> {
        return bookmarkDao.getBookmark(id).map { it.toDomainBookmark() }
    }

    override fun getBookmarkForWork(id: Long): DomainBookmark? {
        return bookmarkDao.getBookmarkForWork(id)?.toDomainBookmark()
    }

    override fun getAllBookmark(id: Long): Flow<List<DomainBookmark>> {
        return bookmarkDao.getAllBookmark(id).map { it -> it.map { it.toDomainBookmark() } }
    }

    override fun getBreedBookmark(type: TypeEgg): Flow<List<String>> {
        return bookmarkDao.getBreedBookmark(type)
    }

    override fun getBreedStatisticList(typeEgg: TypeEgg, idPT: Long): Flow<List<DomainTitleCount>> {
        return bookmarkDao.getBreedStatisticList(type = typeEgg, idPT = idPT)
            .map { it -> it.map { it.toTitleCountDto() } }
    }

    override fun getTypeStatisticList(idPT: Long): Flow<List<DomainTypeEggCount>> {
        return bookmarkDao.getTypeStatisticList(idPT = idPT)
            .map { it -> it.map { it.toDomainTypeEggCount() } }
    }

    override fun getCountAndRejectedCountAll(idPT: Long): Flow<DomainCountRejectedCount> {
        return bookmarkDao.getCountAndRejectedCountAll(idPT)
            .map { it.toDomainCountRejectedCount() }
    }

    override fun getBookmarkList(type: TypeEgg, idPT: Long): Flow<List<DomainBookmark>> {
        return bookmarkDao.getBookmarkList(type = type, idPT = idPT)
            .map { it -> it.map { it.toDomainBookmark() } }
    }

    override fun getBookmarkListByIdPT(idPT: Long): Flow<List<DomainBookmark>> {
        return bookmarkDao.getBookmarkListByIdPT(idPT)
            .map { it -> it.map { it.toDomainBookmark() } }
    }

    override fun getActivityBookmark(idPT: Long): Flow<DomainBookmark?> {
        return bookmarkDao.getActivityBookmark(idPT = idPT).map { it?.toDomainBookmark() }
    }

    override fun getActivityBookmarkByIdPT(idPT: Long): Flow<DomainBookmark?> {
        return bookmarkDao.getActivityBookmarkByIdPT(idPT).map { it?.toDomainBookmark() }
    }

    override suspend fun insert(item: DomainBookmark): Long {
        return bookmarkDao.insert(item.toBookmarkTable())
    }

    override suspend fun update(item: DomainBookmark) {
        return bookmarkDao.update(item.toBookmarkTable())
    }

    override suspend fun deleteBookmarkById(id: Long) {
        return bookmarkDao.deleteBookmarkById(id)
    }

    override suspend fun deleteBookmark(id: DomainBookmark) {
        return bookmarkDao.deleteBookmark(id.toBookmarkTable())
    }

    override fun getFinanceIncubator(idPT: Long): Flow<DomainFinanceIncubatorMain?> {
        return bookmarkDao.getFinanceIncubator(idPT).map { it?.toDomainFinanceIncubatorMain() }
    }

    override fun getFinanceIncubatorAllProject(currencySuffix: Suffix): Flow<DomainFinanceAllIncubator> {
        return bookmarkDao.getFinanceIncubatorAllProject(currencySuffix = currencySuffix)
            .map { it.toDomainFinanceAllIncubator() }
    }

    override fun getFinanceIncubatorList(idPT: Long): Flow<List<DomainFinanceIncubatorHistory>> {
        return bookmarkDao.getFinanceIncubatorList(idPT)
            .map { it -> it.map { it.toDomainFinanceIncubatorHistory() } }
    }
}