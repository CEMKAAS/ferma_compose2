package com.zaroslikov.data.room.table.animal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix

@Entity(
    tableName = "animal_count_table",
    foreignKeys = [ForeignKey(
        entity = AnimalTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("animal_id"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("animal_id")]
)
data class AnimalCountTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val count: String,
    val suffix: Suffix,
    val date: String,
    val note: String,
    val version: AnimalCountVersion? = null,//0-SaleTable 1-ExpensesTable 2-WriteOffSop // 3-WriteOffUtil 4-AddTable null - Старые не считаются
    @ColumnInfo(name = "animal_id")
    val animalId: Long
)
