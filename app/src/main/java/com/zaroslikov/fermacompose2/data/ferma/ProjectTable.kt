package com.zaroslikov.fermacompose2.data.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "МyINCUBATOR")
data class ProjectTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    @ColumnInfo(name = "NAME")
    val titleProject: String, // название
    @ColumnInfo(name = "TYPE")
    val type: String, // Кол-во
    @ColumnInfo(name = "DATA")
    val data: String,  // Дата начала проекта
    @ColumnInfo(name = "EGGALL")
    val eggAll: String, // месяц
    @ColumnInfo(name = "EGGALLEND")
    val eggAllEND: String, // время
    @ColumnInfo(name = "AIRING")
    val airing: String,
    @ColumnInfo(name = "OVERTURN")
    val over: String,
    @ColumnInfo(name = "ARHIVE")
    var arhive: String,  //не архив = 0, Архив = 1
    @ColumnInfo(name = "DATAEND")
    val dateEnd: String,  // Конец проекта
    @ColumnInfo(name = "TIMEPUSH1")
    var time1: String,
    @ColumnInfo(name = "TIMEPUSH2")
    var time2: String,
    @ColumnInfo(name = "TIMEPUSH3")
    var time3: String,
    val mode: Int, //Инкубатор = 0, Хозяйство = 1
    val imageData: ByteArray, // Изображение
)





