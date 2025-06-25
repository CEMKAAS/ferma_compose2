package com.zaroslikov.fermacompose2.data.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable

@Entity(
    tableName = "MyFermaSale",
    foreignKeys = [ForeignKey(
        entity = ProjectTable::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = AnimalTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("animalId"),
            onDelete = ForeignKey.CASCADE
        ), ForeignKey(
            entity = AnimalCountTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("animal_count_id"),
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index("idPT"), Index("animalId"), Index("animal_count_id")]
)
data class SaleTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    @ColumnInfo(name = "titleSale")
    val title: String, // название
    @ColumnInfo(name = "discSale")
    val count: Double, // Кол-во
    @ColumnInfo(name = "DAY")
    val day: Int,  // день
    @ColumnInfo(name = "MOUNT")
    val mount: Int, // месяц
    @ColumnInfo(name = "YEAR")
    val year: Int, // время
    @ColumnInfo(name = "PRICE")
    val priceAll: Double,
    var suffix: String,
    var category: String,
    val buyer: String,
    val note: String,
    @ColumnInfo(name = "idPT")
    val idPT: Long,
    @ColumnInfo(name = "animalId")
    val animalId: Long? = null,
    @ColumnInfo(name = "animal_count_id")
    val animalCountId: Long? = null
)
