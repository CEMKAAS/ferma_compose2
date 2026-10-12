package com.zaroslikov.data.room.dao

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimalCountSqlTest {

    private fun String.codes(): Set<Int> = split(",").map { it.trim().toInt() }.toSet()

    @Test
    fun `приход — все версии со знаком плюс`() {
        assertEquals(
            AnimalCountVersion.entries.filter { it.sign > 0 }.map { it.code }.toSet(),
            ANIMAL_COUNT_PLUS.codes()
        )
    }

    @Test
    fun `расход — все версии со знаком минус`() {
        assertEquals(
            AnimalCountVersion.entries.filter { it.sign < 0 }.map { it.code }.toSet(),
            ANIMAL_COUNT_MINUS.codes()
        )
    }

    @Test
    fun `каждая версия со знаком учтена ровно один раз`() {
        val plus = ANIMAL_COUNT_PLUS.codes()
        val minus = ANIMAL_COUNT_MINUS.codes()
        assertTrue((plus intersect minus).isEmpty())
        assertEquals(AnimalCountVersion.entries.filter { it.sign != 0 }.map { it.code }.toSet(), plus + minus)
    }

    @Test
    fun `вход и выход из сборной группы не меняют поголовье животного`() {
        val signed = ANIMAL_COUNT_PLUS.codes() + ANIMAL_COUNT_MINUS.codes()
        assertTrue(AnimalCountVersion.HERD_JOIN.code !in signed)
        assertTrue(AnimalCountVersion.HERD_LEAVE.code !in signed)
    }

    @Test
    fun `выражение со знаком использует оба списка`() {
        assertTrue(ANIMAL_SIGNED_COUNT.contains("version IN ($ANIMAL_COUNT_PLUS) THEN count"))
        assertTrue(ANIMAL_SIGNED_COUNT.contains("version IN ($ANIMAL_COUNT_MINUS) THEN -count"))
    }

    @Test
    fun `суффикс по умолчанию — штуки`() {
        assertEquals(Suffix.PIECES.code, SUFFIX_PIECES_CODE)
    }

    @Test
    fun `CTE own ограничен областью запроса`() {
        assertTrue(ANIMAL_WITH_HERD_SELECT_BY_PROJECT.contains("SELECT id FROM animal_table WHERE idPT = :idPT"))
        assertTrue(ANIMAL_WITH_HERD_SELECT_BY_HERD.contains("SELECT id FROM animal_table WHERE herd_id = :herdId"))
        assertTrue(ANIMAL_WITH_HERD_SELECT_BY_ANIMAL.contains("c.animal_id = :id"))
        assertTrue(ANIMAL_OWN_CTE_SINGLE.contains("c.animal_id = :animalId"))
        listOf(ANIMAL_OWN_CTE_BY_PROJECT, ANIMAL_OWN_CTE_BY_HERD, ANIMAL_OWN_CTE_BY_ANIMAL, ANIMAL_OWN_CTE_SINGLE)
            .forEach { assertTrue(it.contains("GROUP BY c.animal_id")) }
    }

    @Test
    fun `название сборной группы берётся из её таблицы`() {
        assertTrue(ANIMAL_WITH_HERD_COLUMNS.contains("LEFT JOIN animal_herd_table h ON h.id = an.herd_id"))
        assertTrue(!ANIMAL_WITH_HERD_COLUMNS.contains("group_id"))
    }

    @Test
    fun `последняя запись — тот же порядок, что в own`() {
        assertTrue(ANIMAL_OWN_SELECT.contains("ORDER BY $ANIMAL_COUNT_LAST_FIRST_ORDER"))
    }

    @Test
    fun `количество в transfer-записи без дробной части`() {
        assertEquals("3", 3.0.toCountString())
        assertEquals("2.5", 2.5.toCountString())
    }
}
