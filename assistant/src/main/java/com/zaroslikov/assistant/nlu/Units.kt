package com.zaroslikov.assistant.nlu

import com.zaroslikov.domain.models.enums.Suffix

/** Единица измерения из слова: «литров» → LITERS; «десяток» → PIECES ×10. */
data class UnitMatch(val suffix: Suffix, val multiplier: Double = 1.0)

object Units {

    private val FORMS: Map<String, UnitMatch> = buildMap {
        fun put(unit: UnitMatch, vararg forms: String) = forms.forEach { put(it, unit) }
        put(UnitMatch(Suffix.LITERS), "л", "литр", "литра", "литров", "литры", "литре", "литрах", "литром", "литрами")
        put(UnitMatch(Suffix.MILLILITRES), "мл", "миллилитр", "миллилитра", "миллилитров")
        put(UnitMatch(Suffix.KILOGRAM), "кг", "килограмм", "килограмма", "килограммов", "килограммы", "кило", "килограммами")
        put(UnitMatch(Suffix.GRAM), "г", "гр", "грамм", "грамма", "граммов", "граммы")
        put(UnitMatch(Suffix.TONS), "т", "тонна", "тонны", "тонн", "тонну")
        put(UnitMatch(Suffix.PIECES), "шт", "штук", "штука", "штуки", "штуку", "штуками", "штучки", "штучек", "штучка")
        put(UnitMatch(Suffix.PIECES, 10.0), "десяток", "десятка", "десятков", "десятки")
        put(UnitMatch(Suffix.PIECES, 12.0), "дюжина", "дюжины", "дюжину", "дюжин")
        put(UnitMatch(Suffix.HEADS), "гол", "голов", "голова", "головы", "голову")
        put(UnitMatch(Suffix.UNITS), "ед", "единиц", "единица", "единицы", "единицу")
        put(UnitMatch(Suffix.CUBIC_METERS), "куб", "кубов", "куба", "кубометр", "кубометра", "кубометров", "м3")
        put(UnitMatch(Suffix.METERS), "м", "метр", "метра", "метров")
        put(UnitMatch(Suffix.CENTIMETERS), "см", "сантиметр", "сантиметра", "сантиметров")
        put(UnitMatch(Suffix.MILLIMETERS), "мм", "миллиметр", "миллиметра", "миллиметров")
    }

    /** Односимвольные/короткие сокращения — только сразу после числа, иначе «г» в «г. Москва» станет граммами. */
    private val SHORT = setOf("л", "г", "гр", "т", "м", "кг", "мл", "шт", "см", "мм", "ед", "гол", "куб", "м3")

    private val CURRENCY: Map<String, Suffix> = buildMap {
        fun put(s: Suffix, vararg forms: String) = forms.forEach { put(it, s) }
        put(Suffix.RUBLE, "р", "руб", "рубль", "рубля", "рублей", "рублям", "рублями", "рублях", "рубликов", "рублика")
        put(Suffix.TENGE, "тенге", "тг")
        put(Suffix.DOLLAR, "доллар", "доллара", "долларов", "бакс", "баксов", "usd")
        put(Suffix.EURO, "евро", "eur")
    }

    fun unit(word: String, afterNumber: Boolean): UnitMatch? {
        val match = FORMS[word] ?: return null
        return if (word in SHORT && !afterNumber) null else match
    }

    fun isUnitWord(word: String): Boolean = word in FORMS

    fun currency(word: String): Suffix? = CURRENCY[word]

    fun isCurrencyWord(word: String): Boolean = word in CURRENCY

    /** Подпись единицы для ответов помощника. */
    fun label(suffix: Suffix): String = when (suffix) {
        Suffix.PIECES -> "шт"
        Suffix.HEADS -> "гол"
        Suffix.UNITS -> "ед"
        Suffix.GRAM -> "г"
        Suffix.KILOGRAM -> "кг"
        Suffix.TONS -> "т"
        Suffix.MILLILITRES -> "мл"
        Suffix.LITERS -> "л"
        Suffix.CUBIC_METERS -> "м³"
        Suffix.MILLIMETERS -> "мм"
        Suffix.CENTIMETERS -> "см"
        Suffix.METERS -> "м"
        Suffix.RUBLE -> "₽"
        Suffix.TENGE -> "₸"
        Suffix.DOLLAR -> "$"
        Suffix.EURO -> "€"
        Suffix.GRAM_DAY -> "г/день"
        Suffix.KILOGRAM_DAY -> "кг/день"
        Suffix.TONS_DAY -> "т/день"
        Suffix.KILOGRAM_TO_CUBIC_METERS -> "кг/м³"
        Suffix.KILOGRAM_TO_LITERS -> "кг/л"
        Suffix.NO -> ""
    }
}
