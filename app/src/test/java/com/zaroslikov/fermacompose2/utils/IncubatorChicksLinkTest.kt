package com.zaroslikov.fermacompose2.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Разбор ссылки из «Инкубатора». Это договор с другим приложением (его сторона —
 * `FarmLink` и `FarmLinkTest` там), и менять его можно только вместе с ним.
 */
class IncubatorChicksLinkTest {

    private val today = "29.09.2026"

    private val full = mapOf(
        "v" to "1",
        "source" to "incubator",
        "type" to "Курицы",
        "name" to "Весенняя партия — Хайсекс",
        "breed" to "Хайсекс",
        "count" to "22",
        "date" to "28.09.2026",
    )

    private fun parse(
        params: Map<String, String?> = full,
        scheme: String? = "myferma",
        host: String? = "animal",
        path: String? = "/add",
    ) = IncubatorChicksLink.parse(scheme, host, path, today) { params[it] }

    @Test
    fun `полная ссылка читается целиком`() {
        assertEquals(
            IncubatorChicks(
                type = "Курицы",
                name = "Весенняя партия — Хайсекс",
                breed = "Хайсекс",
                count = 22,
                date = "28.09.2026",
            ),
            parse(),
        )
    }

    @Test
    fun `чужая схема, хост или путь — не наша ссылка`() {
        assertNull(parse(scheme = "https"))
        assertNull(parse(host = "template"))
        assertNull(parse(path = "/edit"))
        assertNull(parse(path = null))
    }

    @Test
    fun `регистр схемы и хоста и завершающий слеш не мешают`() {
        assertEquals(22, parse(scheme = "MyFerma", host = "ANIMAL", path = "/add/")?.count)
    }

    @Test
    fun `другая версия договора отклоняется, отсутствующая читается как первая`() {
        assertNull(parse(full + ("v" to "2")))
        assertEquals(22, parse(full - "v")?.count)
    }

    @Test
    fun `число птенцов обязательно, целое и больше нуля`() {
        assertNull(parse(full - "count"))
        assertNull(parse(full + ("count" to "0")))
        assertNull(parse(full + ("count" to "-3")))
        assertNull(parse(full + ("count" to "2.5")))
        assertNull(parse(full + ("count" to "abc")))
        assertNull(parse(full + ("count" to "100001")))
    }

    @Test
    fun `без вида отказ, без названия — название из вида, без породы — пусто`() {
        assertNull(parse(full + ("type" to "  ")))
        assertEquals("Курицы", parse(full - "name")?.name)
        assertEquals("", parse(full - "breed")?.breed)
    }

    @Test
    fun `непонятная дата заменяется сегодняшней`() {
        assertEquals(today, parse(full - "date")?.date)
        assertEquals(today, parse(full + ("date" to "32.13.2026"))?.date)
        assertEquals(today, parse(full + ("date" to "5.8.2026"))?.date)
        assertEquals(today, parse(full + ("date" to "2026-09-28"))?.date)
        assertEquals("29.02.2024", parse(full + ("date" to "29.02.2024"))?.date)
        assertEquals(today, parse(full + ("date" to "29.02.2026"))?.date)
    }

    @Test
    fun `пробелы по краям срезаются, длинный текст обрезается`() {
        val c = parse(full + ("name" to "  Весенняя  ") + ("breed" to "x".repeat(300)))!!
        assertEquals("Весенняя", c.name)
        assertEquals(100, c.breed.length)
    }
}
