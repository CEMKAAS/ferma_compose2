/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaroslikov.fermacompose2.data.ferma

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "Project")
data class ProjectTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
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
//    val picture: ByteArray, // Изображение
    @ColumnInfo(name = "TIMEPUSH1")
    var time1: String,
    @ColumnInfo(name = "TIMEPUSH2")
    var time2: String,
    @ColumnInfo(name = "TIMEPUSH3")
    var time3: String,

    val mode: Int //Инкубатор = 0, Хозяйство = 1
)





