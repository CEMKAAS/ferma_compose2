package com.zaroslikov.data.room.table.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.project.ProjectTable


@Entity(
    tableName = "ExpensesAnimalTable",
    foreignKeys = [ForeignKey(
        entity = ProjectTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    )], indices = [Index("idPT")] // TODO добавить в миграцию
)
data class ExpensesAnimalTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    val idExpenses: Long,
    val idAnimal: Long,
    val percentExpenses: Double,
    val idPT: Long
)
