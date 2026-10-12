package com.zaroslikov.data.room.converter

import androidx.room.TypeConverter
import com.zaroslikov.domain.models.enums.AnimalSpecies

class AnimalSpeciesConverter {
    @TypeConverter
    fun fromAnimalSpecies(species: AnimalSpecies): Int = species.code

    @TypeConverter
    fun toAnimalSpecies(value: Int): AnimalSpecies = AnimalSpecies.fromCode(value)
}
