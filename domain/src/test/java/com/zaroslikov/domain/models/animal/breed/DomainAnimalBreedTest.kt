package com.zaroslikov.domain.models.animal.breed

import com.zaroslikov.domain.models.enums.AnimalSpecies
import org.junit.Assert.assertEquals
import org.junit.Test

class DomainAnimalBreedTest {

    private val breeds = listOf(
        DomainAnimalBreed("Фландр", AnimalSpecies.RABBIT, "Кролик"),
        DomainAnimalBreed("Бабочка", AnimalSpecies.RABBIT, "Крольчиха"),
        DomainAnimalBreed("фландр", AnimalSpecies.RABBIT, "Крольчиха"),
        DomainAnimalBreed("Хайсекс", AnimalSpecies.CHICKEN, "Курица"),
        DomainAnimalBreed("Тибетская", AnimalSpecies.UNKNOWN, "Як"),
        DomainAnimalBreed("Чужая", AnimalSpecies.UNKNOWN, "Лама"),
    )

    @Test
    fun `породы только своего вида, независимо от пола в названии`() {
        assertEquals(listOf("Бабочка", "Фландр"), DomainAnimalBreed.suggestions(breeds, "Крольчиха"))
        assertEquals(listOf("Хайсекс"), DomainAnimalBreed.suggestions(breeds, "Курица"))
    }

    @Test
    fun `свой тип сравнивается по названию`() {
        assertEquals(listOf("Тибетская"), DomainAnimalBreed.suggestions(breeds, " як "))
    }

    @Test
    fun `без типа подсказок нет`() {
        assertEquals(emptyList<String>(), DomainAnimalBreed.suggestions(breeds, "  "))
    }
}
