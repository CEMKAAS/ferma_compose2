package com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.card

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListUi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HerdStatsTest {
    private val cow = AnimalListUi(id = 1, sex = false, totalCount = 1.0)
    private val bull = AnimalListUi(id = 2, sex = true, totalCount = 1.0)
    private val calves = AnimalListUi(id = 3, isGroup = true, sex = null, totalCount = 4.0)

    private fun w(id: Long, animal: Long, weight: String, date: String, suffix: Suffix = Suffix.KILOGRAM) =
        DomainAnimalWeight(id = id, weight = weight, suffix = suffix, date = date, idAnimal = animal)

    @Test fun `берётся последнее взвешивание по дате`() {
        val r = herdWeight(
            listOf(cow),
            listOf(w(1, 1, "400", "10.05.2026"), w(2, 1, "450", "01.06.2026"), w(3, 1, "300", "01.01.2026")),
            Suffix.KILOGRAM
        )!!
        assertEquals(450.0, r.total, 0.001)
    }

    @Test fun `вес обезличенной группы — на голову, умножается на поголовье`() {
        val r = herdWeight(
            listOf(cow, calves),
            listOf(w(1, 1, "500", "01.06.2026"), w(2, 3, "100", "01.06.2026")),
            Suffix.KILOGRAM
        )!!
        assertEquals(900.0, r.total, 0.001)      // 500 + 100 × 4
        assertEquals(180.0, r.average, 0.001)    // 900 / 5 голов
        assertEquals(2, r.weighed)
    }

    @Test fun `граммы переводятся в единицы настроек, невзвешенные не учитываются`() {
        val r = herdWeight(listOf(cow, bull), listOf(w(1, 1, "450000", "01.06.2026", Suffix.GRAM)), Suffix.KILOGRAM)!!
        assertEquals(450.0, r.average, 0.001)
        assertEquals(1, r.weighed)
        assertEquals(2, r.members)
    }

    @Test fun `запятая в весе читается`() =
        assertEquals(12.5, herdWeight(listOf(cow), listOf(w(1, 1, "12,5", "01.06.2026")), Suffix.KILOGRAM)!!.total, 0.001)

    @Test fun `никто не взвешен — null`() = assertNull(herdWeight(listOf(cow), emptyList(), Suffix.KILOGRAM))
}
