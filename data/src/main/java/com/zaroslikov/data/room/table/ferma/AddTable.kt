package com.zaroslikov.data.room.table.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.animal.AnimalCountTable
import com.zaroslikov.data.room.table.animal.AnimalTable
import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.domain.models.enums.Suffix


@Entity(
    tableName = "add_table",
    foreignKeys = [
        ForeignKey(
            entity = ProjectTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("idPT"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AnimalTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("animal_id"),
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = AnimalCountTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("animal_count_id"),
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index("idPT"), Index("animal_id"), Index("animal_count_id")]
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
    val countSuffix: Suffix,
    @ColumnInfo(name = "price_suffix")
    val priceSuffix: Suffix,
    val category: String,
    @ColumnInfo(name = "animal_id")
    val animalId: Long?,
    val note: String,
    @ColumnInfo(name = "idPT")
    val idPT: Long,
    @ColumnInfo(name = "animal_count_id")
    val animalCountId: Long? = null
)
