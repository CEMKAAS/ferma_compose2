package com.zaroslikov.fermacompose2.data.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable

@Entity(
    tableName = "MyFermaWRITEOFF",
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
    val id: Int = 0,
    @ColumnInfo(name = "titleWRITEOFF")
    val title: String, // название
    @ColumnInfo(name = "discWRITEOFF")
    val count: Double, // Кол-во
    @ColumnInfo(name = "DAY")
    val day: Int,  // день
    @ColumnInfo(name = "MOUNT")
    val mount: Int, // месяц
    @ColumnInfo(name = "YEAR")
    val year: Int, // время
    @ColumnInfo(name = "statusWRITEOFF")
    var status: Boolean, //0-на собственные 1-утиль
    val priceAll: Double,
    var suffix: String,
    val note: String,
    @ColumnInfo(name = "idPT")
    val idPT: Int,
    @ColumnInfo(name = "animal_count_id")
    val animalCountId: Long? = null
)