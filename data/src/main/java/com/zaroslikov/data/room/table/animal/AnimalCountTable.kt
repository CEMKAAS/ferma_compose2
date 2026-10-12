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
    foreignKeys = [
        ForeignKey(
            entity = AnimalTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("animal_id"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AnimalHerdTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("herd_id"),
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("animal_id"), Index("herd_id")]
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
    val animalId: Long,
    /**
     * Сборная группа, в составе которой сделана запись (MIGRATION_12_13): по таким записям
     * строится история поголовья группы. NULL — запись к сборной группе не относится.
     */
    @ColumnInfo(name = "herd_id")
    val herdId: Long? = null,
    /**
     * Обезличенная группа: сколько из [count] самок и самцов (MIGRATION_14_15). На столько же
     * меняются счётчики самок и самцов группы; остальное — пол не известен.
     */
    @ColumnInfo(name = "females_count", defaultValue = "0")
    val femalesCount: Int = 0,
    @ColumnInfo(name = "males_count", defaultValue = "0")
    val malesCount: Int = 0
)
