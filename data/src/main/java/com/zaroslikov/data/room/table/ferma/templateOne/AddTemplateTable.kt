package com.zaroslikov.data.room.table.ferma.templateOne

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.animal.AnimalTable
import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.domain.models.enums.Suffix

@Entity(
    tableName = "add_template_table",
    foreignKeys = [
        ForeignKey(
            entity = ProjectTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("idPT"),
            onDelete = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = AnimalTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("animal_id"),
            onDelete = ForeignKey.Companion.SET_NULL
        )],
    indices = [Index("idPT"), Index("animal_id")]
)
data class AddTemplateTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    @ColumnInfo(name = "name_template")
    val nameTemplate: String,
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "count")
    val count: Double?,
    @ColumnInfo(name = "count_suffix")
    val countSuffix: Suffix?,
    @ColumnInfo(name = "price")
    val price: Double?,
    @ColumnInfo(name = "price_suffix")
    val priceSuffix: Suffix?,
    val category: String?,
    @ColumnInfo(name = "animal_id")
    val animalId: Long?,
    val note: String?,
    @ColumnInfo(name = "idPT")
    val idPT: Long
)