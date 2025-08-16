package com.zaroslikov.fermacompose2.data.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable

@Entity(
    tableName = "sale_table",
    foreignKeys = [ForeignKey(
        entity = ProjectTable::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = AnimalTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("animal_id"),
            onDelete = ForeignKey.CASCADE
        ), ForeignKey(
            entity = AnimalCountTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("animal_count_id"),
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index("idPT"), Index("animal_id"), Index("animal_count_id")]
)
data class SaleTable(
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
    val price: Double,
    @ColumnInfo(name = "price_all")
    val priceAll: Double?,
    @ColumnInfo(name = "day")
    val day: Int,  // день
    @ColumnInfo(name = "month")
    val mount: Int, // месяц
    @ColumnInfo(name = "year")
    val year: Int, // время
    val category: String,
    val buyer: String?,
    val note: String,
    @ColumnInfo(name = "idPT")
    val idPT: Long,
    @ColumnInfo(name = "animal_id")
    val animalId: Long? = null,
    @ColumnInfo(name = "animal_count_id")
    val animalCountId: Long? = null
)
