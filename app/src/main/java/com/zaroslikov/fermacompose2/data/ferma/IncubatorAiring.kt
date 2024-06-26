package com.zaroslikov.fermacompose2.data.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable


@Entity(
    tableName = "МyINCUBATORAIRING",
    foreignKeys = [ForeignKey(
        entity = ProjectTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class IncubatorAiring(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    @ColumnInfo(name = "DAYAIRING1")
    val day1: String, // название
    @ColumnInfo(name = "DAYAIRING2")
    val day2: String, // название
    @ColumnInfo(name = "DAYAIRING3")
    val day3: String, // название
    @ColumnInfo(name = "DAYAIRING4")
    val day4: String, // название
    @ColumnInfo(name = "DAYAIRING5")
    val day5: String, // название
    @ColumnInfo(name = "DAYAIRING6")
    val day6: String, // название
    @ColumnInfo(name = "DAYAIRING7")
    val day7: String, // название
    @ColumnInfo(name = "DAYAIRING8")
    val day8: String, // название
    @ColumnInfo(name = "DAYAIRING9")
    val day9: String, // название
    @ColumnInfo(name = "DAYAIRING10")
    val day10: String, // название
    @ColumnInfo(name = "DAYAIRING11")
    val day11: String, // название
    @ColumnInfo(name = "DAYAIRING12")
    val day12: String, // название
    @ColumnInfo(name = "DAYAIRING13")
    val day13: String, // название
    @ColumnInfo(name = "DAYAIRING14")
    val day14: String, // название
    @ColumnInfo(name = "DAYAIRING15")
    val day15: String, // название
    @ColumnInfo(name = "DAYAIRING16")
    val day16: String, // название
    @ColumnInfo(name = "DAYAIRING17")
    val day17: String, // название
    @ColumnInfo(name = "DAYAIRING18")
    val day18: String, // название
    @ColumnInfo(name = "DAYAIRING19")
    val day19: String, // название
    @ColumnInfo(name = "DAYAIRING20")
    val day20: String, // название
    @ColumnInfo(name = "DAYAIRING21")
    val day21: String, // название
    @ColumnInfo(name = "DAYAIRING22")
    val day22: String, // название
    @ColumnInfo(name = "DAYAIRING23")
    val day23: String, // название
    @ColumnInfo(name = "DAYAIRING24")
    val day24: String, // название
    @ColumnInfo(name = "DAYAIRING25")
    val day25: String, // название
    @ColumnInfo(name = "DAYAIRING26")
    val day26: String, // название
    @ColumnInfo(name = "DAYAIRING27")
    val day27: String, // название
    @ColumnInfo(name = "DAYAIRING28")
    val day28: String, // название
    @ColumnInfo(name = "DAYAIRING29")
    val day29: String, // название
    @ColumnInfo(name = "DAYAIRING30")
    val day30: String, // название
    @ColumnInfo(name = "idPT")
    val idPT : Int
    )
