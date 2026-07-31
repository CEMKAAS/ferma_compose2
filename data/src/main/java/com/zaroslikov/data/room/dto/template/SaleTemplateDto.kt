package com.zaroslikov.data.room.dto.template

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.Suffix

data class SaleTemplateDto(
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    @ColumnInfo(name = "name_template")
    val nameTemplate: String,
    val title: String? = "",
    val count: Double? = 0.0,
    @ColumnInfo(name = "count_suffix")
    val countSuffix: Suffix? = Suffix.KILOGRAM,
    val price: Double? = 0.0,
    @ColumnInfo(name = "price_all")
    val priceAll: Double? = null,
    @ColumnInfo(name = "price_suffix")
    val priceSuffix: Suffix? = null,
    val category: String?,
    val buyer: String? = null,
    val note: String? = "",
    val idPT: Long = 0,
    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean = false,
    @ColumnInfo(name = "is_multi_project_template")
    val isMultiProject: Boolean = false
)