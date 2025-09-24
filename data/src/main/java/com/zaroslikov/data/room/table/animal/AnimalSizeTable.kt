package com.zaroslikov.data.room.table.animal


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.domain.models.enums.Suffix


@Entity(
    tableName = "animal_size_table",
    foreignKeys = [ForeignKey(
        entity = AnimalTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("animal_id"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("animal_id")]
)
data class AnimalSizeTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val size: String,
    val suffix: Suffix,
    val date: String,
    @ColumnInfo(name = "animal_id")
    val animalId: Long,
    val note: String
)
