package com.zaroslikov.data.room.table.incubator

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.data.room.table.project.ProjectTable

@Entity(
    tableName = "MyIncubator",
    foreignKeys = [ForeignKey(
        entity = BookmarkTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("idPT")]
)
data class Incubator(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    val day: Int,
    val temp: String,
    val damp: String,
    val over: String,
    val airing: String,
    @ColumnInfo("temp_fact")
    val tempFact: String,
    @ColumnInfo("damp_fact")
    val dampFact: String,
    @ColumnInfo("over_fact")
    val overFact: String,
    @ColumnInfo("airing_fact")
    val airingFact: String,
    val note: String,
    @ColumnInfo(name = "idPT")
    val idPT: Long
)