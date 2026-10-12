package com.zaroslikov.data.room.dto.animal

import com.zaroslikov.domain.models.enums.AnimalSpecies

/** Порода неархивного животного с его видом и типом — для выпадающего списка пород. */
data class AnimalBreedDto(
    val breed: String,
    val species: AnimalSpecies,
    val type: String
)
