package com.zaroslikov.data.room.table.animal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "animal_vaccination_table",
    foreignKeys = [ForeignKey(
        entity = AnimalTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("animal_id"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("animal_id")]
)
data class AnimalVaccinationTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vaccination: String,
    @ColumnInfo(name = "count_vaccination")
    val countVaccination: Int,
    val date: String,
    @ColumnInfo(name = "next_vaccination")
    val nextVaccination: String?,
    @ColumnInfo(name = "animal_id")
    val animalId: Long,
    val note: String
)
