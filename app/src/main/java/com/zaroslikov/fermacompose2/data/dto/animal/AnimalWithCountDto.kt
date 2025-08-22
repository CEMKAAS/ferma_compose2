package com.zaroslikov.fermacompose2.data.dto.animal


data class AnimalWithCountDto(
    val id: Long,
    val name: String,
    val type: String,
    val date: String,
    val dateFactory: String?,
    val group: Boolean,
    val sex: Boolean,
    val count: String?,
    val suffix: String?
)
