package com.zaroslikov.domain.models.animal.group

import org.junit.Assert.assertEquals
import org.junit.Test

class AnimalAutoNameTest {
    @Test fun emptyStartsFromOne() =
        assertEquals(listOf("Кролик 1", "Кролик 2"), AnimalAutoName.generate("Кролик", emptyList(), 2))

    @Test fun continuesAfterMax() =
        assertEquals(listOf("Кролик 6"), AnimalAutoName.generate("Кролик", listOf("Кролик 1", "кролик 5", "Кролик 2"), 1))

    @Test fun ignoresOtherNames() =
        assertEquals(listOf("Коза 1"), AnimalAutoName.generate("Коза", listOf("Коза Мила", "Коза 1 б", "Овца 9"), 1))

    @Test fun escapesRegexChars() =
        assertEquals(listOf("a.b 4"), AnimalAutoName.generate("a.b", listOf("a.b 3", "axb 10"), 1))

    @Test fun zeroCount() = assertEquals(emptyList<String>(), AnimalAutoName.generate("x", emptyList(), 0))
}
