package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.NoteDao
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.mapper.table.toNoteMap
import com.zaroslikov.domain.models.DomainNoteTable
import com.zaroslikov.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(private val noteDao: NoteDao) : NoteRepository {
    override fun getAllNote(id: Long): Flow<List<DomainNoteTable>> {
        return noteDao.getAllNote(id).map { it -> it.map { it.toDomainMap() } }
    }

    override fun getNote(id: Long): Flow<DomainNoteTable> {
        return noteDao.getNote(id).map { it.toDomainMap() }
    }

    override suspend fun insertNote(item: DomainNoteTable) {
        return noteDao.insertNote(item.toNoteMap())
    }

    override suspend fun updateNote(item: DomainNoteTable) {
        return noteDao.updateNote(item.toNoteMap())
    }

    override suspend fun deleteNote(item: DomainNoteTable) {
        return noteDao.deleteNote(item.toNoteMap())
    }

    override suspend fun deleteNoteById(id: Long) {
        return noteDao.deleteNoteById(id)
    }

}