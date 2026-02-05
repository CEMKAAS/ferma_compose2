package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.domain.models.table.DomainBookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getBookmark(id: Long): Flow<DomainBookmark>
    fun getAllBookmark(id: Long): Flow<List<DomainBookmark>>
    fun getBreedBookmark(type: TypeEgg): Flow<List<String>>
    fun getBookmarkList(type: TypeEgg): Flow<List<DomainBookmark>>
    fun getActivityBookmark(idPT: Long): Flow<DomainBookmark?>
    suspend fun insert(item: DomainBookmark) : Long
    suspend fun update(item: DomainBookmark)
    suspend fun deleteBookmarkById(id: Long)
    suspend fun deleteBookmark(id: DomainBookmark)
}