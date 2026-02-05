package com.zaroslikov.data.room.table.incubator

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.project.ProjectTable
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
    val date: String,
    val time: String,
    val price: Double?,
    val autoPrice: Boolean = false,
    val note: String,
    @ColumnInfo(name = "auto_rotation")
    val isAutoRotation: Boolean = false,
    @ColumnInfo(name = "auto_ventilation")
    val isAutoVentilation: Boolean = false,
    @ColumnInfo(name = "is_activity_bookmark")
    val isActivityBookmark: Boolean,
    @ColumnInfo(name = "idPT")
    val idPT: Long
)
