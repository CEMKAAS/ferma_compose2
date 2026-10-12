package com.zaroslikov.data.room.table.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.domain.models.enums.Suffix

/**
 * Переработка сырья в продукцию. Сырьё — строки write_off_table с recycling_id = _id.
 * Продукция попадает в add_table (add_id) только при завершении, до этого её нет
 * ни на складе, ни в учёте.
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
    @ColumnInfo(name = "title")
    val title: String, // продукция
    @ColumnInfo(name = "count")
    val count: Double,
    @ColumnInfo(name = "count_suffix")
    val countSuffix: Suffix,
    @ColumnInfo(name = "category")
    val category: String?,
    @ColumnInfo(name = "day")
    val day: Int, // дата переработки
    @ColumnInfo(name = "month")
    val month: Int,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "ready_day")
    val readyDay: Int?, // дата готовности; null — готово сразу
    @ColumnInfo(name = "ready_month")
    val readyMonth: Int?,
    @ColumnInfo(name = "ready_year")
    val readyYear: Int?,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean,
    @ColumnInfo(name = "add_id")
    val addId: Long?, // строка add_table с полученной продукцией
    @ColumnInfo(name = "note")
    val note: String,
    @ColumnInfo(name = "idPT")
    val idPT: Long
)
