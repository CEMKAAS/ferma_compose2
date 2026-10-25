package com.zaroslikov.data.room.table.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.domain.models.enums.Suffix

/**
 * Готовая продукция партии переработки. В add_table (add_id) попадает только при завершении,
 * до этого её нет ни на складе, ни в учёте. Удаляется каскадом вместе с партией.
 */
@Entity(
    tableName = "recycling_product_table",
    foreignKeys = [ForeignKey(
        entity = RecyclingTable::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("recycling_id"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("recycling_id"), Index("idPT")]
)
data class RecyclingProductTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    @ColumnInfo(name = "recycling_id")
    val recyclingId: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "count")
    val count: Double,
    @ColumnInfo(name = "count_suffix")
    val countSuffix: Suffix,
    @ColumnInfo(name = "category")
    val category: String?,
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
    @ColumnInfo(name = "idPT")
    val idPT: Long
)
