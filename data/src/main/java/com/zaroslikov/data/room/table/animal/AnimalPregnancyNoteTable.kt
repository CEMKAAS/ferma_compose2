package com.zaroslikov.data.room.table.animal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Заметка к беременности за день. Удаляется вместе с беременностью.
 * Схема совпадает с MIGRATION_11_12 (колонки, FK, индекс index_animal_pregnancy_note_table_pregnancy_id).
 */
@Entity(
    tableName = "animal_pregnancy_note_table",
    foreignKeys = [
        ForeignKey(
            entity = AnimalPregnancyTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("pregnancy_id"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pregnancy_id")]
)
data class AnimalPregnancyNoteTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "pregnancy_id")
    val pregnancyId: Long,
    /** dd.MM.yyyy */
    val date: String,
    /** HH:mm; null — время не указано. */
    val time: String? = null,
    val note: String
)
