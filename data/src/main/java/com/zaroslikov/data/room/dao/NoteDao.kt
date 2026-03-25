package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.table.ferma.NoteTable
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query(
        "SELECT * from note_table" +
                " Where idPT=:id" +
                " ORDER BY strftime('%Y-%m-%d', substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) DESC"
    )
    fun getAllNote(id: Long): Flow<List<NoteTable>>

    @Query(
        "SELECT * from note_table Where _id=:id"
    )
    fun getNote(id: Long): Flow<NoteTable>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertNote(item: NoteTable)

    @Update
    suspend fun updateNote(item: NoteTable)

    @Delete
    suspend fun deleteNote(item: NoteTable)

    @Query("DELETE FROM note_table WHERE _id = :id")
    suspend fun deleteNoteById(id: Long)
}