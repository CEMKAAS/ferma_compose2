package com.zaroslikov.fermacompose2.data.animal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "AnimalWeightTable",
    foreignKeys = [ForeignKey(
        entity = AnimalTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idAnimal"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class AnimalWeightTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val weight: String,
    val date: String,
    val idAnimal: Int
)
