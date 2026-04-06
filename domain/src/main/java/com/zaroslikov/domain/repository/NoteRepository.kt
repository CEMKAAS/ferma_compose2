package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.DomainNoteTable
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.models.table.profile.DomainProfileTable
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNoteTableForExport(): Flow<List<DomainNoteTable>>
    suspend fun clearAndInsertNoteTableForImport(domainNoteTable: List<DomainNoteTable>)
    fun getAllNote(id: Long): Flow<List<DomainNoteTable>>
    fun getNote(id: Long): Flow<DomainNoteTable>
    suspend fun insertNote(item: DomainNoteTable)
    suspend fun updateNote(item: DomainNoteTable)
    suspend fun deleteNote(item: DomainNoteTable)
    suspend fun deleteNoteById(id: Long)
}