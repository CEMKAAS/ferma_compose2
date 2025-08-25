package com.zaroslikov.data.room.table.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "add_table",
    foreignKeys = [ForeignKey(
        entity = ProjectTable::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("idPT")]
)
data class AddTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    @ColumnInfo(name = "title")
    val title: String, // название
    @ColumnInfo(name = "count")
    val count: Double, // Кол-во
    @ColumnInfo(name = "day")
    val day: Int,  // день
    @ColumnInfo(name = "month")
    val mount: Int, // месяц
    @ColumnInfo(name = "year")
    val year: Int, // время
    @ColumnInfo(name = "price")
    val price: Double,
    @ColumnInfo(name = "count_suffix")
    val countSuffix: String,
    val category: String,
    @ColumnInfo(name = "animal_id")
    val animalId: Long?,
    val note: String,
    @ColumnInfo(name = "idPT")
    val idPT: Long,
)
