package com.zaroslikov.fermacompose2.data.animal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable

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
    val id: Int = 0,
    val count: String,
    val date: String,
    val idAnimal: Int
)
