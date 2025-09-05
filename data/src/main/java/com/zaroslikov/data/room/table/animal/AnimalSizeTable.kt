package com.zaroslikov.data.room.table.animal


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "AnimalSizeTable",
    foreignKeys = [ForeignKey(
        entity = AnimalTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idAnimal"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class AnimalSizeTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val size: String,
    val suffix: String,
    val date: String,
    val idAnimal: Long,
    val note: String
)
