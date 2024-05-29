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
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */
@Entity(
    tableName = "MyFerma",
    foreignKeys = [ForeignKey(
        entity = ProjectTable::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idPT"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class AddTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "Title")
    val title: String, // название

    @ColumnInfo(name = "Count")
    val count: Double, // Кол-во
    val day: Int,  // день
    val mount: Int, // месяц
    val year: Int, // время
    val priceAll: String,
    @ColumnInfo(name = "idPT")
    val idPT: Int,
    var suffix: String,
    var category: String,
    var animal: String,
)
