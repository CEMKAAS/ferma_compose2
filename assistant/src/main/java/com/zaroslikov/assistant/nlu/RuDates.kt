package com.zaroslikov.assistant.nlu

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/** Период для аналитики. [isAllTime] — «за всё время»: границы условные. */
data class Period(
    val begin: LocalDate,
    val end: LocalDate,
    /** Подпись для ответа: «за октябрь», «за последние 7 дней», «сегодня». */
    val label: String,
    val isAllTime: Boolean = false
) {
    val days: Long get() = java.time.temporal.ChronoUnit.DAYS.between(begin, end) + 1

    companion object {
        fun allTime(today: LocalDate) = Period(LocalDate.of(2000, 1, 1), today, "за всё время", isAllTime = true)
        fun currentMonth(today: LocalDate) = Period(today.withDayOfMonth(1), today, "за ${monthLabel(today.monthValue)}")
        fun day(date: LocalDate, label: String) = Period(date, date, label)
        fun lastDays(today: LocalDate, days: Int) =
            Period(today.minusDays(days - 1L), today, "за последние $days " + pluralDays(days))

        private fun pluralDays(n: Int): String {
            val mod10 = n % 10
            val mod100 = n % 100
            return when {
                mod10 == 1 && mod100 != 11 -> "день"
                mod10 in 2..4 && mod100 !in 12..14 -> "дня"
                else -> "дней"
            }
        }
        fun month(year: Int, month: Int, today: LocalDate): Period {
            val first = LocalDate.of(year, month, 1)
            val last = first.with(TemporalAdjusters.lastDayOfMonth())
            val label = if (year == today.year) "за ${monthLabel(month)}" else "за ${monthLabel(month)} $year"
            return Period(first, minOf(last, today), label)
        }
    }
}

/** Результат разбора: что нашли и какие индексы токенов заняты. */
data class Found<T>(val value: T, val used: Set<Int>)

object RuDates {

    private val MONTH_STEMS = listOf(
        "январ", "феврал", "март", "апрел", "ма", "июн", "июл", "август", "сентябр", "октябр", "ноябр", "декабр"
    )

    /** Номер месяца по слову («октября», «в октябре»); «май/мая/мае» — отдельно, чтобы не ловить «мало». */
    fun monthOf(word: String): Int? {
        if (word in setOf("май", "мая", "мае", "маю")) return 5
        MONTH_STEMS.forEachIndexed { index, stem ->
            if (index != 4 && word.startsWith(stem) && word.length - stem.length <= 2) return index + 1
        }
        return null
    }

    private val WEEKDAYS = mapOf(
        "понедельник" to DayOfWeek.MONDAY, "вторник" to DayOfWeek.TUESDAY, "среда" to DayOfWeek.WEDNESDAY, "среду" to DayOfWeek.WEDNESDAY,
        "четверг" to DayOfWeek.THURSDAY, "пятниц" to DayOfWeek.FRIDAY, "суббот" to DayOfWeek.SATURDAY, "воскресен" to DayOfWeek.SUNDAY
    )

    /**
     * Дата события: «вчера», «позавчера», «3 дня назад», «15 октября», «15.10[.2026]», «в понедельник».
     * Без указания — null (вызывающий подставит сегодня).
     */
    fun extractDate(tokens: List<Token>, today: LocalDate): Found<LocalDate>? {
        tokens.forEachIndexed { i, t ->
            when (t.text) {
                "сегодня" -> return Found(today, setOf(i))
                "вчера" -> return Found(today.minusDays(1), setOf(i))
                "позавчера" -> return Found(today.minusDays(2), setOf(i))
            }
            if (t.isDate) parseDateToken(t.text, today)?.let { return Found(it, setOf(i)) }
            // «15 октября»
            if (t.isNumber && t.number!! in 1.0..31.0 && i + 1 < tokens.size) {
                val month = monthOf(tokens[i + 1].text)
                if (month != null) {
                    val day = t.number.toInt()
                    var year = today.year
                    var used = setOf(i, i + 1)
                    if (i + 2 < tokens.size && tokens[i + 2].isNumber && tokens[i + 2].number!! in 2000.0..2100.0) {
                        year = tokens[i + 2].number!!.toInt(); used = used + (i + 2)
                    }
                    val date = safeDate(year, month, day) ?: return@forEachIndexed
                    // «15 октября», когда октябрь ещё впереди — прошлый год
                    return Found(if (date.isAfter(today) && used.size == 2) date.minusYears(1) else date, used)
                }
            }
            // «3 дня назад», «неделю назад»
            if (i + 1 < tokens.size && tokens[i + 1].text == "назад") {
                if (t.text.startsWith("недел")) return Found(today.minusWeeks(1), setOf(i, i + 1))
                if (t.text.startsWith("месяц")) return Found(today.minusMonths(1), setOf(i, i + 1))
            }
            if (t.isNumber && i + 2 < tokens.size && tokens[i + 2].text == "назад") {
                val unit = tokens[i + 1].text
                val n = t.number!!.toLong()
                // «999999 дней назад» — не дата события, а ошибка распознавания: игнорируем
                val date = when {
                    n !in 0..MAX_DAYS_BACK -> null
                    unit.startsWith("дн") -> today.minusDays(n)
                    unit.startsWith("недел") && n <= MAX_DAYS_BACK / 7 -> today.minusWeeks(n)
                    unit.startsWith("месяц") && n <= MAX_DAYS_BACK / 30 -> today.minusMonths(n)
                    else -> null
                }
                if (date != null && isPlausible(date, today)) return Found(date, setOf(i, i + 1, i + 2))
            }
            // «в понедельник» — ближайший прошедший
            WEEKDAYS.entries.firstOrNull { t.text.startsWith(it.key) }?.let { (_, dow) ->
                val date = today.with(TemporalAdjusters.previousOrSame(dow))
                val used = if (i > 0 && tokens[i - 1].text == "в") setOf(i - 1, i) else setOf(i)
                return Found(date, used)
            }
        }
        return null
    }

    /**
     * Период для вопросов: «за неделю», «за месяц», «за прошлый месяц», «за год», «за всё время»,
     * «за октябрь», «с 1 по 15 октября», «за 10 дней», «сегодня», «вчера».
     */
    fun extractPeriod(tokens: List<Token>, today: LocalDate): Found<Period>? {
        val texts = tokens.map { it.text }
        fun has(vararg words: String) = texts.indices.filter { i -> words.any { texts[i].startsWith(it) } }

        // «с 1 по 15 октября» / «с 1 октября по 15 октября»
        val from = texts.indexOf("с")
        val to = texts.indexOf("по")
        if (from >= 0 && to > from) {
            val d1 = tokens.getOrNull(from + 1)
            val d2 = tokens.getOrNull(to + 1)
            if (d1?.isNumber == true && d2?.isNumber == true) {
                val month2 = tokens.getOrNull(to + 2)?.text?.let(::monthOf)
                val month1 = tokens.getOrNull(from + 2)?.text?.let(::monthOf) ?: month2 ?: today.monthValue
                val m2 = month2 ?: month1
                // Месяц ещё впереди — речь о прошлом годе
                val year = if (m2 > today.monthValue) today.year - 1 else today.year
                val begin = safeDate(year, month1, d1.number!!.toInt())
                val end = safeDate(year, m2, d2.number!!.toInt())
                if (begin != null && end != null && !end.isBefore(begin)) {
                    val used = mutableSetOf(from, from + 1, to, to + 1)
                    if (month2 != null) used += to + 2
                    if (tokens.getOrNull(from + 2)?.text?.let(::monthOf) != null) used += from + 2
                    return Found(Period(begin, end, "с ${begin.dayOfMonth} по ${end.dayOfMonth} ${monthLabel(m2)}"), used)
                }
            }
        }

        // «всего»/«вообще» — за всё время, но только если явного периода нет
        var fallback: Found<Period>? = null
        texts.forEachIndexed { i, w ->
            val prevIsZa = i > 0 && texts[i - 1] in setOf("за", "в", "на")
            val usedWithZa = if (prevIsZa) setOf(i - 1, i) else setOf(i)
            val prevIsPast = i > 0 && texts[i - 1].startsWith("прошл") || i > 0 && texts[i - 1].startsWith("предыдущ") || i > 0 && texts[i - 1].startsWith("минувш")
            val prevIsThis = i > 0 && (texts[i - 1].startsWith("этот") || texts[i - 1].startsWith("этом") || texts[i - 1].startsWith("текущ") || texts[i - 1].startsWith("нынешн"))
            val usedWithAdj = (if (prevIsPast || prevIsThis) setOf(i - 1, i) else setOf(i)) +
                    (if (i > 1 && (prevIsPast || prevIsThis) && texts[i - 2] in setOf("за", "в")) setOf(i - 2) else emptySet()) +
                    (if (prevIsZa) setOf(i - 1) else emptySet())
            when {
                w == "сегодня" -> return Found(Period.day(today, "сегодня"), setOf(i))
                w == "вчера" -> return Found(Period.day(today.minusDays(1), "вчера"), setOf(i))
                w.startsWith("недел") -> {
                    val period = if (prevIsPast) {
                        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1)
                        Period(monday, monday.plusDays(6), "за прошлую неделю")
                    } else Period.lastDays(today, 7).copy(label = "за неделю")
                    return Found(period, usedWithAdj)
                }

                w.startsWith("месяц") -> {
                    val period = if (prevIsPast) {
                        val prev = today.minusMonths(1)
                        Period.month(prev.year, prev.monthValue, today).copy(label = "за прошлый месяц")
                    } else Period.currentMonth(today).copy(label = if (prevIsThis) "за этот месяц" else "за месяц")
                    return Found(period, usedWithAdj)
                }

                w == "год" || w == "году" || w == "года" -> {
                    // «за 2025 год»
                    val explicitYear = tokens.getOrNull(i - 1)?.number?.toInt()?.takeIf { it in 2000..2100 }
                    val period = when {
                        explicitYear != null && explicitYear != today.year ->
                            Period(LocalDate.of(explicitYear, 1, 1), LocalDate.of(explicitYear, 12, 31), "за $explicitYear год")
                        explicitYear != null -> Period(LocalDate.of(explicitYear, 1, 1), today, "за $explicitYear год")
                        prevIsPast -> {
                            val y = today.year - 1
                            Period(LocalDate.of(y, 1, 1), LocalDate.of(y, 12, 31), "за прошлый год")
                        }
                        else -> Period(LocalDate.of(today.year, 1, 1), today, "за год")
                    }
                    val used = if (explicitYear != null) usedWithAdj + (i - 1) + (if (i > 1 && texts[i - 2] == "за") setOf(i - 2) else emptySet()) else usedWithAdj
                    return Found(period, used)
                }

                w == "квартал" -> {
                    val startMonth = (today.monthValue - 1) / 3 * 3 + 1
                    return Found(Period(LocalDate.of(today.year, startMonth, 1), today, "за квартал"), usedWithAdj)
                }

                (w == "все" || w == "всё") && texts.getOrNull(i + 1) == "время" -> {
                    val used = usedWithZa + (i + 1)
                    return Found(Period.allTime(today), used)
                }

                w == "всего" || w == "вообще" -> fallback = Found(Period.allTime(today), setOf(i))
            }
            // «за октябрь», «в октябре»
            val month = monthOf(w)
            if (month != null && !(i > 0 && tokens[i - 1].isNumber && tokens[i - 1].number!! <= 31)) {
                val year = if (month > today.monthValue) today.year - 1 else today.year
                return Found(Period.month(year, month, today), usedWithZa)
            }
            // «за 10 дней», «последние 3 дня»
            if (tokens[i].isNumber && i + 1 < texts.size && texts[i + 1].startsWith("дн") && texts.getOrNull(i + 2) != "назад" &&
                tokens[i].number!! in 1.0..MAX_DAYS_BACK.toDouble()
            ) {
                val n = tokens[i].number!!.toInt()
                var used = setOf(i, i + 1)
                if (i > 0 && (texts[i - 1].startsWith("последн") || texts[i - 1] == "за")) used = used + (i - 1)
                if (i > 1 && texts[i - 1].startsWith("последн") && texts[i - 2] == "за") used = used + (i - 2)
                return Found(Period.lastDays(today, n), used)
            }
        }
        return fallback
    }

    private fun parseDateToken(text: String, today: LocalDate): LocalDate? {
        val parts = text.split('.')
        val day = parts.getOrNull(0)?.toIntOrNull() ?: return null
        val month = parts.getOrNull(1)?.toIntOrNull() ?: return null
        var year = parts.getOrNull(2)?.toIntOrNull() ?: today.year
        if (year < 100) year += 2000
        return safeDate(year, month, day)
    }

    /** Не раньше 2000 года и не дальше года вперёд — остальное считаем ошибкой распознавания. */
    private const val MAX_DAYS_BACK = 3650L

    private fun isPlausible(date: LocalDate, today: LocalDate): Boolean =
        !date.isBefore(LocalDate.of(2000, 1, 1)) && !date.isAfter(today.plusYears(1))

    private fun safeDate(year: Int, month: Int, day: Int): LocalDate? =
        try {
            LocalDate.of(year, month, day).takeIf { it.year in 2000..2100 }
        } catch (e: Exception) {
            null
        }

    /** «dd.MM.yyyy» для показа. */
    fun display(date: LocalDate): String = "%02d.%02d.%d".format(date.dayOfMonth, date.monthValue, date.year)

    /** «yyyy-MM-dd» для SQL-диапазонов. */
    fun iso(date: LocalDate): String = date.toString()

    fun dayLabel(date: LocalDate, today: LocalDate): String = when (date) {
        today -> "сегодня"
        today.minusDays(1) -> "вчера"
        else -> display(date)
    }
}

/** Название месяца в родительном падеже без предлога: 10 → «октябрь». */
fun monthLabel(month: Int): String = listOf(
    "январь", "февраль", "март", "апрель", "май", "июнь", "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"
).getOrElse(month - 1) { "" }
