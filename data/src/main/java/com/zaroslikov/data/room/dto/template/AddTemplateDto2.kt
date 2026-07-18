package com.zaroslikov.data.room.dto.template

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.Suffix

data class AddTemplateDto2(
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    @ColumnInfo(name = "name_template")
    val nameTemplate: String,
    val title: String? = "",
    val count: Double? = 0.0,
    @ColumnInfo(name = "count_suffix")
    val countSuffix: Suffix? = Suffix.KILOGRAM,
    val price: Double? = 0.0,
    val category: String?,
    @ColumnInfo(name = "animal_id")
    val animalId: Long? = null,
    @ColumnInfo(name = "animal_name")
    val nameAnimal: String? = null,
    val note: String? = "",
    val idPT: Long = 0,
    val pin: Boolean = false,
    @ColumnInfo(name = "is_multi_project_template")
    val isMultiProject: Boolean = false
)