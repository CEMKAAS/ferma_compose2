package com.zaroslikov.data.room.table.incubator

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TypeEgg

@Entity(
    tableName = "bookmark_incubator",
    foreignKeys = [ForeignKey(
        entity = IncubatorTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("idPT")]
)
data class BookmarkTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    val title: String = "",
    val type: TypeEgg,
    val breed: String?,
    val count: Int,
    @ColumnInfo(name = "rejected_count")
    val rejectedCount: Int,
    @ColumnInfo(name = "start_date")
    val startDate: String,
    @ColumnInfo(name = "end_date")
    val endDate: String,
    @ColumnInfo(name = "is_early_completion_status")
    val isEarlyCompletionStatus: Boolean = false, //false - завершена, true - прервана
    val time: String,
    val price: Double?,
    @ColumnInfo(name = "price_all")
    val priceAll: Double?,
    @ColumnInfo(name = "price_suffix")
    val priceSuffix: Suffix?,
    @ColumnInfo(name = "chick_price")
    val chickPrice: Double?,
    val note: String,
    @ColumnInfo(name = "is_auto_rotation")
    val isAutoRotation: Boolean = false,
    @ColumnInfo(name = "is_auto_ventilation")
    val isAutoVentilation: Boolean = false,
    @ColumnInfo(name = "is_activity_bookmark")
    val isActivityBookmark: Boolean,
    @ColumnInfo(name = "idPT")
    val idPT: Long
)
