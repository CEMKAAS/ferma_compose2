package com.zaroslikov.data.room.table.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.project.ProjectTable

/**
 * Партия переработки. Сырьё — строки write_off_table с recycling_id = _id,
 * готовая продукция — строки recycling_product_table (у каждой своя дата готовности).
 */
@Entity(
    tableName = "recycling_table",
    foreignKeys = [ForeignKey(
        entity = ProjectTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("idPT")]
)
data class RecyclingTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    @ColumnInfo(name = "day")
    val day: Int, // дата переработки
    @ColumnInfo(name = "month")
    val month: Int,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "note")
    val note: String,
    @ColumnInfo(name = "idPT")
    val idPT: Long
)
