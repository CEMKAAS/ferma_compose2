package com.zaroslikov.assistant.nlu

/**
 * Русский стеммер по алгоритму Snowball (Портер для русского): отрезает окончания
 * существительных, прилагательных, глаголов и причастий. Нужен, чтобы «молока», «молоком»
 * и «молоко» давали один стем «молок». Ошибки стеммера страхует лексикон и словари форм.
 */
object RuStemmer {

    private const val VOWELS = "аеиоуыэюя"

    private val PERFECTIVE_GERUND_1 = listOf("вшись", "вши", "в")
    private val PERFECTIVE_GERUND_2 = listOf("ившись", "ывшись", "ивши", "ывши", "ив", "ыв")
    private val ADJECTIVE = listOf(
        "ими", "ыми", "его", "ого", "ему", "ому",
        "ее", "ие", "ые", "ое", "ей", "ий", "ый", "ой", "ем", "им", "ым", "ом", "их", "ых", "ую", "юю", "ая", "яя", "ою", "ею"
    )
    private val PARTICIPLE_1 = listOf("ем", "нн", "вш", "ющ", "щ")
    private val PARTICIPLE_2 = listOf("ивш", "ывш", "ующ")
    private val REFLEXIVE = listOf("ся", "сь")
    private val VERB_1 = listOf(
        "ете", "йте", "ешь", "нно", "ла", "на", "ли", "ем", "ло", "но", "ет", "ют", "ны", "ть", "й", "л", "н"
    )
    private val VERB_2 = listOf(
        "ейте", "уйте", "ила", "ыла", "ена", "ите", "или", "ыли", "ило", "ыло", "ено", "ует", "уют", "ены", "ить", "ыть", "ишь",
        "ей", "уй", "ил", "ыл", "им", "ым", "ен", "ят", "ит", "ыт", "ую", "ю"
    )
    private val NOUN = listOf(
        "иями", "ями", "ами", "ией", "иям", "ием", "иях",
        "ев", "ов", "ие", "ье", "еи", "ии", "ей", "ой", "ий", "ям", "ем", "ам", "ом", "ах", "ях", "ию", "ью", "ия", "ья",
        "а", "е", "и", "й", "о", "у", "ы", "ь", "ю", "я"
    )
    private val DERIVATIONAL = listOf("ость", "ост")
    private val SUPERLATIVE = listOf("ейше", "ейш")

    fun stem(word: String): String {
        val w = normalizeRu(word)
        if (w.length <= 3 || w.any { it !in 'а'..'я' && it != '-' }) return w

        val rvStart = w.indexOfFirst { it in VOWELS }.let { if (it < 0) return w else it + 1 }
        if (rvStart >= w.length) return w
        val r1Start = run {
            var i = rvStart
            // R1: после первой согласной, следующей за гласной
            while (i < w.length && w[i] in VOWELS) i++
            if (i < w.length) i + 1 else w.length
        }
        val r2Start = run {
            var i = r1Start
            while (i < w.length && w[i] !in VOWELS) i++
            while (i < w.length && w[i] in VOWELS) i++
            if (i < w.length) i + 1 else w.length
        }

        var s = w
        // Шаг 1
        var removed = removeEnding(s, rvStart, PERFECTIVE_GERUND_1, requirePrecedingAorYa = true)
            ?: removeEnding(s, rvStart, PERFECTIVE_GERUND_2)
        if (removed != null) s = removed
        else {
            removeEnding(s, rvStart, REFLEXIVE)?.let { s = it }
            val adjectival = removeAdjectival(s, rvStart)
            if (adjectival != null) s = adjectival
            else {
                removed = removeEnding(s, rvStart, VERB_1, requirePrecedingAorYa = true)
                    ?: removeEnding(s, rvStart, VERB_2)
                if (removed != null) s = removed
                else removeEnding(s, rvStart, NOUN)?.let { s = it }
            }
        }
        // Шаг 2
        if (s.endsWith("и") && s.length - 1 >= rvStart) s = s.dropLast(1)
        // Шаг 3
        removeEnding(s, r2Start, DERIVATIONAL)?.let { s = it }
        // Шаг 4
        when {
            s.endsWith("нн") -> s = s.dropLast(1)
            else -> {
                val sup = removeEnding(s, rvStart, SUPERLATIVE)
                if (sup != null) {
                    s = sup
                    if (s.endsWith("нн")) s = s.dropLast(1)
                } else if (s.endsWith("ь")) s = s.dropLast(1)
            }
        }
        return s
    }

    private fun removeAdjectival(word: String, rvStart: Int): String? {
        val withoutAdjective = removeEnding(word, rvStart, ADJECTIVE) ?: return null
        return removeEnding(withoutAdjective, rvStart, PARTICIPLE_1, requirePrecedingAorYa = true)
            ?: removeEnding(withoutAdjective, rvStart, PARTICIPLE_2)
            ?: withoutAdjective
    }

    /** Отрезает самое длинное окончание из [endings], лежащее целиком в области от [regionStart]. */
    private fun removeEnding(
        word: String,
        regionStart: Int,
        endings: List<String>,
        requirePrecedingAorYa: Boolean = false
    ): String? {
        for (ending in endings) {
            if (!word.endsWith(ending)) continue
            val cut = word.length - ending.length
            if (cut < regionStart) continue
            if (requirePrecedingAorYa) {
                if (cut == 0) continue
                val prev = word[cut - 1]
                if (prev != 'а' && prev != 'я') continue
            }
            return word.substring(0, cut)
        }
        return null
    }
}
