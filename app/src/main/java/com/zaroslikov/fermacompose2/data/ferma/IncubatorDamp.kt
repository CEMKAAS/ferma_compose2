package com.zaroslikov.fermacompose2.data.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable


@Entity(
    tableName = "МyINCUBATORTEMPDAMP",
    foreignKeys = [ForeignKey(
        entity = ProjectTable::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class IncubatorDamp(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
////    @ColumnInfo(name = "DAYDAMP1")
//    val day1: String, // название
//    @ColumnInfo(name = "DAYDAMP2")
//    val day2: String, // название
//    @ColumnInfo(name = "DAYDAMP3")
//    val day3: String, // название
//    @ColumnInfo(name = "DAYDAMP4")
//    val day4: String, // название
//    @ColumnInfo(name = "DAYDAMP5")
//    val day5: String, // название
//    @ColumnInfo(name = "DAYDAMP6")
//    val day6: String, // название
//    @ColumnInfo(name = "DAYDAMP7")
//    val day7: String, // название
//    @ColumnInfo(name = "DAYDAMP8")
//    val day8: String, // название
//    @ColumnInfo(name = "DAYDAMP9")
//    val day9: String, // название
//    @ColumnInfo(name = "DAYDAMP10")
//    val day10: String, // название
//    @ColumnInfo(name = "DAYDAMP11")
//    val day11: String, // название
//    @ColumnInfo(name = "DAYDAMP12")
//    val day12: String, // название
//    @ColumnInfo(name = "DAYDAMP13")
//    val day13: String, // название
//    @ColumnInfo(name = "DAYDAMP14")
//    val day14: String, // название
//    @ColumnInfo(name = "DAYDAMP15")
//    val day15: String, // название
//    @ColumnInfo(name = "DAYDAMP16")
//    val day16: String, // название
//    @ColumnInfo(name = "DAYDAMP17")
//    val day17: String, // название
//    @ColumnInfo(name = "DAYDAMP18")
//    val day18: String, // название
//    @ColumnInfo(name = "DAYDAMP19")
//    val day19: String, // название
//    @ColumnInfo(name = "DAYDAMP20")
//    val day20: String, // название
//    @ColumnInfo(name = "DAYDAMP21")
//    val day21: String, // название
//    @ColumnInfo(name = "DAYDAMP22")
//    val day22: String, // название
//    @ColumnInfo(name = "DAYDAMP23")
//    val day23: String, // название
//    @ColumnInfo(name = "DAYDAMP24")
//    val day24: String, // название
//    @ColumnInfo(name = "DAYDAMP25")
//    val day25: String, // название
//    @ColumnInfo(name = "DAYDAMP26")
//    val day26: String, // название
//    @ColumnInfo(name = "DAYDAMP27")
//    val day27: String, // название
//    @ColumnInfo(name = "DAYDAMP28")
//    val day28: String, // название
//    @ColumnInfo(name = "DAYDAMP29")
//    val day29: String, // название
//    @ColumnInfo(name = "DAYDAMP30")
//    val day30: String, // название
    //    @ColumnInfo(name = "DAYTEMP1")
    val day1: String, // название
//    @ColumnInfo(name = "DAYTEMP2")
    val day2: String, // название
//    @ColumnInfo(name = "DAYTEMP3")
    val day3: String, // название
//    @ColumnInfo(name = "DAYTEMP4")
    val day4: String, // название
//    @ColumnInfo(name = "DAYTEMP5")
    val day5: String, // название
//    @ColumnInfo(name = "DAYTEMP6")
    val day6: String, // название
//    @ColumnInfo(name = "DAYTEMP7")
    val day7: String, // название
//    @ColumnInfo(name = "DAYTEMP8")
    val day8: String, // название
//    @ColumnInfo(name = "DAYTEMP9")
    val day9: String, // название
//    @ColumnInfo(name = "DAYTEMP10")
    val day10: String, // название
//    @ColumnInfo(name = "DAYTEMP11")
    val day11: String, // название
//    @ColumnInfo(name = "DAYTEMP12")
    val day12: String, // название
//    @ColumnInfo(name = "DAYTEMP13")
    val day13: String, // название
//    @ColumnInfo(name = "DAYTEMP14")
    val day14: String, // название
//    @ColumnInfo(name = "DAYTEMP15")
    val day15: String, // название
//    @ColumnInfo(name = "DAYTEMP16")
    val day16: String, // название
//    @ColumnInfo(name = "DAYTEMP17")
    val day17: String, // название
//    @ColumnInfo(name = "DAYTEMP18")
    val day18: String, // название
//    @ColumnInfo(name = "DAYTEMP19")
    val day19: String, // название
//    @ColumnInfo(name = "DAYTEMP20")
    val day20: String, // название
//    @ColumnInfo(name = "DAYTEMP21")
    val day21: String, // название
//    @ColumnInfo(name = "DAYTEMP22")
    val day22: String, // название
//    @ColumnInfo(name = "DAYTEMP23")
    val day23: String, // название
//    @ColumnInfo(name = "DAYTEMP24")
    val day24: String, // название
//    @ColumnInfo(name = "DAYTEMP25")
    val day25: String, // название
//    @ColumnInfo(name = "DAYTEMP26")
    val day26: String, // название
//    @ColumnInfo(name = "DAYTEMP27")
    val day27: String, // название
//    @ColumnInfo(name = "DAYTEMP28")
    val day28: String, // название
//    @ColumnInfo(name = "DAYTEMP29")
    val day29: String, // название
//    @ColumnInfo(name = "DAYTEMP30")
    val day30: String, // название
    @ColumnInfo(name = "idPT")
    val idPT : Int
    )
