package com.zaroslikov.data.room.table.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.project.ProjectTable

@Entity(
    tableName = "recycling_interruption_table",
    foreignKeys = [
        ForeignKey(
            entity = ProjectTable::class,
            parentColumns = ["id"],
            childColumns = ["idPT"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RecyclingTable::class,
            parentColumns = ["_id"],
            childColumns = ["recycling_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("idPT"), Index("recycling_id")]
)
data class RecyclingInterruptionTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    val idPT: Long,
    @ColumnInfo(name = "recycling_id")
    val recyclingId: Long?,
    /** Сбрасывается при возобновлении партии, само прерывание остаётся в истории. */
    @ColumnInfo(name = "ended_current_cycle")
    val endedCurrentCycle: Boolean
)
