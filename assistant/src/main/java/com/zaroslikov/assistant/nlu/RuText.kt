package com.zaroslikov.assistant.nlu

/**
 * Токен нормализованной фразы. [number] заполнен у числовых токенов (цифрами или словами,
 * уже слитых в одно значение). [isDate] — токен вида `15.10.2026`.
 */
data class Token(
    val text: String,
    val number: Double? = null,
    val isDate: Boolean = false
) {
    val isNumber: Boolean get() = number != null
}

/** Приводит текст к виду для сравнения: нижний регистр, «ё» → «е», пробелы по краям. */
fun normalizeRu(value: String): String = value.trim().lowercase().replace('ё', 'е')

// Дата — только с годом: «15.10.2026»; «2.5» без года считается дробью 2,5
private val DATE_TOKEN = Regex("""^\d{1,2}\.\d{1,2}\.\d{2,4}$""")
private val DECIMAL_TOKEN = Regex("""^\d+(?:[.,]\d+)?$""")
private val NUMBER_WITH_UNIT = Regex("""^(\d+(?:[.,]\d+)?)([а-яa-z]+\.?)$""")
private val INTEGER_TOKEN = Regex("""^\d+$""")
private val MONEY_PREPOSITIONS = setOf("за", "на", "по", "сумма", "сумму", "суммой", "стоимостью", "цена", "ценой", "цену")

/**
 * Разбивает фразу на токены: убирает пунктуацию, отделяет «10л» → «10», «л», сливает
 * числительные словами («двадцать пять» → 25) и тысячи с пробелом («1 500» → 1500).
 */
fun tokenizeRu(text: String): List<Token> {
    val prepared = normalizeRu(text)
        .replace('₽', ' ')
        .replace("руб.", " руб ")
        .replace(Regex("""[!?;:()"«»„“”'+*/\\\[\]{}<>=_%№|]"""), " ")
        // точка/запятая внутри числа или даты остаются, остальные — разделители
        .replace(Regex("""(?<!\d)[.,]|[.,](?!\d)"""), " ")
    val raw = prepared.split(Regex("""\s+""")).filter { it.isNotBlank() }

    val split = ArrayList<String>()
    for (word in raw) {
        val m = NUMBER_WITH_UNIT.matchEntire(word)
        if (m != null && !DATE_TOKEN.matches(word)) {
            split += m.groupValues[1]
            split += m.groupValues[2].trimEnd('.')
        } else split += word.trimEnd('.')
    }

    val tokens = ArrayList<Token>()
    var i = 0
    while (i < split.size) {
        val word = split[i]
        when {
            DATE_TOKEN.matches(word) -> {
                tokens += Token(word, isDate = true); i++
            }

            DECIMAL_TOKEN.matches(word) -> {
                var value = word.replace(',', '.').toDouble()
                var consumed = 1
                // «за 1 500 рублей» — тысячи, разделённые пробелом: короткое целое и ровно три цифры за ним,
                // но только рядом с валютой или словом о цене — иначе «продал 3 400» потеряет количество
                val moneyContext = (i + 2 < split.size && Units.isCurrencyWord(split[i + 2])) ||
                        (i > 0 && split[i - 1] in MONEY_PREPOSITIONS)
                if (INTEGER_TOKEN.matches(word) && word.length <= 3 && moneyContext &&
                    i + 1 < split.size && split[i + 1].length == 3 && INTEGER_TOKEN.matches(split[i + 1])
                ) {
                    value = value * 1000 + split[i + 1].toInt()
                    consumed = 2
                }
                // «2 с половиной»
                if (i + consumed + 1 < split.size && split[i + consumed] == "с" && split[i + consumed + 1] == "половиной") {
                    value += 0.5
                    consumed += 2
                }
                tokens += Token(word, number = value)
                i += consumed
            }

            RuNumbers.isNumberWord(word) -> {
                val parsed = RuNumbers.parseWords(split, i)
                tokens += Token(split.subList(i, i + parsed.consumed).joinToString(" "), number = parsed.value)
                i += parsed.consumed
            }

            else -> {
                tokens += Token(word); i++
            }
        }
    }
    return tokens
}

/** Расстояние Левенштейна — для нечёткого сравнения названий. */
fun levenshtein(a: String, b: String): Int {
    if (a == b) return 0
    if (a.isEmpty()) return b.length
    if (b.isEmpty()) return a.length
    var prev = IntArray(b.length + 1) { it }
    var cur = IntArray(b.length + 1)
    for (i in 1..a.length) {
        cur[0] = i
        for (j in 1..b.length) {
            val cost = if (a[i - 1] == b[j - 1]) 0 else 1
            cur[j] = minOf(cur[j - 1] + 1, prev[j] + 1, prev[j - 1] + cost)
        }
        val t = prev; prev = cur; cur = t
    }
    return prev[b.length]
}

/** Первая буква заглавная, остальное как есть. */
fun String.capitalizeRu(): String =
    if (isEmpty()) this else substring(0, 1).uppercase() + substring(1)
