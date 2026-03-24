package com.zaroslikov.data.room.dto

import androidx.room.ColumnInfo


data class AnimalWithCountDto(
    val id: Long,
    val name: String,
    val type: String,
    val date: String,
    val dateFactory: String?,
    @ColumnInfo(name = "is_group")
    val isGroup: Boolean,
    val sex: Boolean,
    val count: String?,
    val suffix: String?
)
