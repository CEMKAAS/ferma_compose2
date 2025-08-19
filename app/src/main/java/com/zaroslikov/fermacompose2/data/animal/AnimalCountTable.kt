package com.zaroslikov.fermacompose2.data.animal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "AnimalCountTable",
    foreignKeys = [ForeignKey(
        entity = AnimalTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idAnimal"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class AnimalCountTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val count: String,
    val suffix: String,
    val date: String,
    val note: String,
    val version: Int? = null,//0-SaleTable 1-ExpensesTable 2-WriteOffSop // 3-WriteOffUtil 4-AddTable null - Старые не считаются
    val idAnimal: Long
)
