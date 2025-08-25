package com.zaroslikov.data.room.table.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.animal.AnimalCountTable

@Entity(
    tableName = "write_off_table",
    foreignKeys = [ForeignKey(
        entity = ProjectTable::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = AnimalCountTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("animal_count_id"),
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index("idPT"), Index("animal_count_id")]
)
data class WriteOffTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    @ColumnInfo(name = "title")
    val title: String, // название
    @ColumnInfo(name = "count")
    val count: Double, // Кол-во
    @ColumnInfo(name = "count_suffix")
    val countSuffix: String,
    @ColumnInfo(name = "price")
    val price: Double?,
    @ColumnInfo(name = "price_all")
    val priceAll: Double?,
    @ColumnInfo(name = "day")
    val day: Int,  // день
    @ColumnInfo(name = "month")
    val mount: Int, // месяц
    @ColumnInfo(name = "year")
    val year: Int, // время
    @ColumnInfo(name = "status")
    val status: Boolean, //0-на собственные 1-утиль
    val note: String,
    @ColumnInfo(name = "idPT")
    val idPT: Long,
    @ColumnInfo(name = "animal_count_id")
    val animalCountId: Long? = null
)