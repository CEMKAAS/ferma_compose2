package com.zaroslikov.data.room.dto.incubator

import androidx.room.ColumnInfo

data class CountRejectedCountDto(
    val count: Int,
    @ColumnInfo("rejected_count")
    val rejectedCount: Int
)
