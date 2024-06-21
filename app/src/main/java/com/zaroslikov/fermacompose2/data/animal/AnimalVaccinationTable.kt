package com.zaroslikov.fermacompose2.data.animal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "AnimalVaccinationTable",
    foreignKeys = [ForeignKey(
        entity = AnimalTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idAnimal"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class AnimalVaccinationTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val vaccination: String,
    val date: String,
    val nextVaccination: String,
    val idAnimal: Int
)
