package com.zaroslikov.data.room.dto.animal

import com.zaroslikov.domain.models.enums.Suffix

/** Запись продукции члена сборной группы (add_table с animal_id). */
data class HerdProductDto(
    val animalId: Long,
    val title: String,
    val count: Double,
    val suffix: Suffix,
    val price: Double
)
