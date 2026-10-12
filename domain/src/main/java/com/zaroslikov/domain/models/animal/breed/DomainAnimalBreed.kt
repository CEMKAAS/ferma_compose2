package com.zaroslikov.domain.models.animal.breed

import com.zaroslikov.domain.models.enums.AnimalSpecies

/** Порода, уже встречающаяся у неархивного животного проекта, вместе с его видом и типом. */
data class DomainAnimalBreed(
    val breed: String,
    val species: AnimalSpecies,
    val type: String
) {
    companion object {
        /**
         * Подсказки пород для вводимого [type]: только породы того же вида
         * (у кроликов свои, у куриц свои). У своего типа ([AnimalSpecies.UNKNOWN]) вида нет —
         * сравниваем сам тип без учёта регистра. Пустой тип — подсказок нет.
         */
        fun suggestions(breeds: List<DomainAnimalBreed>, type: String): List<String> {
            val typeKey = type.trim()
            if (typeKey.isEmpty()) return emptyList()
            val species = AnimalSpecies.resolve(typeKey)
            return breeds
                .filter {
                    if (species == AnimalSpecies.UNKNOWN)
                        it.species == AnimalSpecies.UNKNOWN && it.type.trim().equals(typeKey, ignoreCase = true)
                    else it.species == species
                }
                .map { it.breed.trim() }
                .filter { it.isNotEmpty() }
                .distinctBy { it.lowercase() }
                .sortedBy { it.lowercase() }
        }
    }
}
