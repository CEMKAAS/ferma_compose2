package com.zaroslikov.data.room.table.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_table")
data class ProjectTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    val title: String,
    val date: String,
    @ColumnInfo(name = "date_end")
    val dateEnd: String,
    val mode: Boolean, //Инкубатор = 0, Хозяйство = 1
    val archive: Boolean,  //не архив = 0, Архив = 1
)