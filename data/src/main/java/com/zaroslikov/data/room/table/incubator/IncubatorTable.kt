package com.zaroslikov.data.room.table.incubator

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.domain.models.enums.Suffix

@Entity(
    tableName = "incubator_table",
    foreignKeys = [
        ForeignKey(
            entity = ProjectTable::class,
            parentColumns = arrayOf("_id"),
            childColumns = arrayOf("idPT"),
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index("idPT")]
)
data class IncubatorTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "capacity")
    val capacity: Int = 0,
    @ColumnInfo(name = "price")
    val price: Double? = null,
    @ColumnInfo(name = "note")
    val note: String = "",
    @ColumnInfo(name = "auto_rotation")
    val isAutoRotation: Boolean = false,
    @ColumnInfo(name = "auto_ventilation")
    val isAutoVentilation: Boolean = false,
    @ColumnInfo(name = "currency")
    val currencySuffix: Suffix = Suffix.RUBLE,
    val idPT: Long = 0
)
