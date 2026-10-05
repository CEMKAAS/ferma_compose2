package com.zaroslikov.fermacompose2.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Птенцы, пришедшие из приложения «Инкубатор» после вывода, — из них открывается
 * заполненная форма новой группы животных.
 *
 * [type] — вид как его пишет «Инкубатор» («Курицы», «Утки», …; у своего вида — любое
 * название). Встроенные виды совпадают с нашими `type_egg_*` дословно, так что тип
 * группы берётся как есть. [name] — название закладки, [breed] — порода или пусто,
 * [date] — дата вывода «дд.ММ.гггг».
 */
data class IncubatorChicks(
    val type: String,
    val name: String,
    val breed: String,
    val count: Int,
    val date: String,
)

/**
 * Ссылка `myferma://animal/add?v=1&source=incubator&type=…&name=…&breed=…&count=…&date=…`.
 *
 * Это договор с «Инкубатором» (`ru.zaroslikov.incubator`, его сторона — `FarmLink`):
 * менять разбор можно только вместе с ним. Разбор — чистая функция над параметрами, без
 * `android.net.Uri`, который в JVM-тестах заглушка; её пришпиливает `IncubatorChicksLinkTest`.
 *
 * Отказ — `null`, и тогда приложение просто открывается как обычно: ссылку могли
 * набрать руками или прислать из будущей версии договора.
 * - схема, хост и путь — только наши (схема `myferma` у нас ещё и у QR-шаблонов);
 * - `v` — версия договора: отсутствующая читается как 1, любая другая отклоняется, потому
 *   что поднимают её только со сменой смысла полей;
 * - `count` — целое от 1 до [MAX_COUNT];
 * - `type` обязателен, `name` без названия берётся из вида;
 * - `date` проверяется строго: список животных разбирает дату через `LocalDate.parse`
 *   и упал бы на кривой строке, поэтому непонятная дата заменяется сегодняшней [today].
 */
object IncubatorChicksLink {
    const val SCHEME = "myferma"
    const val HOST = "animal"
    const val PATH = "/add"
    const val VERSION = 1
    const val MAX_COUNT = 100_000
    private const val MAX_TEXT = 100

    fun parse(
        scheme: String?,
        host: String?,
        path: String?,
        today: String,
        param: (String) -> String?,
    ): IncubatorChicks? {
        if (!scheme.equals(SCHEME, ignoreCase = true)) return null
        if (!host.equals(HOST, ignoreCase = true)) return null
        if (path?.trimEnd('/') != PATH) return null

        val version = param("v")?.trim()
        if (version != null && version.toIntOrNull() != VERSION) return null

        val count = param("count")?.trim()?.toIntOrNull() ?: return null
        if (count !in 1..MAX_COUNT) return null

        val type = param("type").clean() ?: return null
        val name = param("name").clean() ?: type
        val breed = param("breed").clean().orEmpty()
        val date = param("date")?.trim()?.takeIf(::isValidDate) ?: today

        return IncubatorChicks(type = type, name = name, breed = breed, count = count, date = date)
    }

    private fun String?.clean(): String? = this?.trim()?.take(MAX_TEXT)?.takeIf { it.isNotEmpty() }

    internal fun isValidDate(text: String): Boolean {
        if (!Regex("""\d{2}\.\d{2}\.\d{4}""").matches(text)) return false
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).apply { isLenient = false }
        return try {
            format.parse(text) != null
        } catch (_: ParseException) {
            false
        }
    }
}
