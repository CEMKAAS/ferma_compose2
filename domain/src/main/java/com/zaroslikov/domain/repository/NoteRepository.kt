package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.DomainNoteTable
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNote(id: Long): Flow<List<DomainNoteTable>>
    fun getNote(id: Long): Flow<DomainNoteTable>
    suspend fun insertNote(item: DomainNoteTable)
    suspend fun updateNote(item: DomainNoteTable)
    suspend fun deleteNote(item: DomainNoteTable)
}