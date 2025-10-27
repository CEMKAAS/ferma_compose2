package com.zaroslikov.data.room.dto.add

import androidx.compose.runtime.Composable
import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.Suffix


data class BrieflyAddDto(
    val title: String,
    val count :Double,
    val suffix: Suffix,
    @ColumnInfo(name = "row_count")
    val rowCount:Long
)