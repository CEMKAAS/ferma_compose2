package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.BookmarkDao
import com.zaroslikov.data.room.mapper.table.toBookmarkTable
import com.zaroslikov.data.room.mapper.table.toDomainBookmark
import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.domain.models.table.DomainBookmark
import com.zaroslikov.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

class BookmarkRepositoryImpl @Inject constructor(private val bookmarkDao: BookmarkDao) :
    BookmarkRepository {
    override fun getBookmark(id: Long): Flow<DomainBookmark> {
        return bookmarkDao.getBookmark(id).map { it.toDomainBookmark() }
    }

    override fun getAllBookmark(id: Long): Flow<List<DomainBookmark>> {
        return bookmarkDao.getAllBookmark(id).map { it -> it.map { it.toDomainBookmark() } }
    }

    override fun getBreedBookmark(type: TypeEgg): Flow<List<String>> {
        return bookmarkDao.getBreedBookmark(type)
    }

    override fun getBookmarkList(type: TypeEgg): Flow<List<DomainBookmark>> {
        return bookmarkDao.getBookmarkList(type = type)
            .map { it -> it.map { it.toDomainBookmark() } }
    }

    override fun getActivityBookmark(idPT: Long): Flow<DomainBookmark?> {
        return bookmarkDao.getActivityBookmark(idPT = idPT).map { it?.toDomainBookmark() }
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
}