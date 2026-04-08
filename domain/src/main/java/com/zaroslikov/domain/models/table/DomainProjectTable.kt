package com.zaroslikov.domain.models.table

import kotlinx.serialization.Serializable

@Serializable
data class DomainProjectTable(
    val id: Long = 0,
    val title: String = "",
    val date: String = "",
    val dateEnd: String = "",
    val mode: Boolean = true, //Инкубатор = 0, Хозяйство = 1
    val archive: Boolean = false,  //не архив = 0, Архив = 1
    val imagePath: String? = null,
    val currentIcon: Int? = null
)
