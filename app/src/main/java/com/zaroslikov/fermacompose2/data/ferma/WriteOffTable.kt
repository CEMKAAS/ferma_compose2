package com.zaroslikov.fermacompose2.data.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "MyFermaWRITEOFF",
    foreignKeys = [ForeignKey(
        entity = ProjectTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class WriteOffTable (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "Title")
    val title: String, // название

    @ColumnInfo(name = "Count")
    val count: Double, // Кол-во
    val day: Int,  // день
    val mount: Int, // месяц
    val year: Int, // время
    var status: Int,
    val priceAll: Double,
    var suffix: String,

    @ColumnInfo(name = "idPT")
    val idPT: Int,
)