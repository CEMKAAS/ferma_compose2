package com.zaroslikov.fermacompose2.data.incubator

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable


@Entity(
    tableName = "МyINCUBATOROVER",
    foreignKeys = [ForeignKey(
        entity = ProjectTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class IncubatorOver(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val day1: String, // название
    val day2: String, // название
    val day3: String, // название
    val day4: String, // название
    val day5: String, // название
    val day6: String, // название
    val day7: String, // название
    val day8: String, // название
    val day9: String, // название
    val day10: String, // название
    val day11: String, // название
    val day12: String, // название
    val day13: String, // название
    val day14: String, // название
    val day15: String, // название
    val day16: String, // название
    val day17: String, // название
    val day18: String, // название
    val day19: String, // название
    val day20: String, // название
    val day21: String, // название
    val day22: String, // название
    val day23: String, // название
    val day24: String, // название
    val day25: String, // название
    val day26: String, // название
    val day27: String, // название
    val day28: String, // название
    val day29: String, // название
    val day30: String, // название
    @ColumnInfo(name = "idPT")
    val idPT : Int
    )
