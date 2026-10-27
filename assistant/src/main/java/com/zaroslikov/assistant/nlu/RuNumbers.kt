package com.zaroslikov.assistant.nlu

/** Числительные словами: «двадцать пять», «полтора», «две тысячи триста». */
object RuNumbers {

    private val UNITS = mapOf(
        "ноль" to 0.0, "нуль" to 0.0,
        "один" to 1.0, "одна" to 1.0, "одно" to 1.0, "одну" to 1.0, "одного" to 1.0, "одной" to 1.0,
        "два" to 2.0, "две" to 2.0, "двух" to 2.0, "пару" to 2.0, "пара" to 2.0,
        "три" to 3.0, "трех" to 3.0, "четыре" to 4.0, "четырех" to 4.0,
        "пять" to 5.0, "пяти" to 5.0, "шесть" to 6.0, "шести" to 6.0, "семь" to 7.0, "семи" to 7.0,
        "восемь" to 8.0, "восьми" to 8.0, "девять" to 9.0, "девяти" to 9.0,
        "десять" to 10.0, "десяти" to 10.0, "одиннадцать" to 11.0, "двенадцать" to 12.0, "тринадцать" to 13.0,
        "четырнадцать" to 14.0, "пятнадцать" to 15.0, "шестнадцать" to 16.0, "семнадцать" to 17.0,
        "восемнадцать" to 18.0, "девятнадцать" to 19.0,
        "полтора" to 1.5, "полторы" to 1.5
    )
    private val TENS = mapOf(
        "двадцать" to 20.0, "двадцати" to 20.0, "тридцать" to 30.0, "тридцати" to 30.0,
        "сорок" to 40.0, "сорока" to 40.0, "пятьдесят" to 50.0, "пятидесяти" to 50.0,
        "шестьдесят" to 60.0, "семьдесят" to 70.0, "восемьдесят" to 80.0, "девяносто" to 90.0
    )
    private val HUNDREDS = mapOf(
        "сто" to 100.0, "ста" to 100.0, "двести" to 200.0, "триста" to 300.0, "четыреста" to 400.0,
        "пятьсот" to 500.0, "шестьсот" to 600.0, "семьсот" to 700.0, "восемьсот" to 800.0, "девятьсот" to 900.0
    )
    private val THOUSANDS = setOf("тысяча", "тысячи", "тысяч", "тысячу", "тыс")
    private val MILLIONS = setOf("миллион", "миллиона", "миллионов")
    private val HALF = setOf("половина", "половиной", "половину")

    fun isNumberWord(word: String): Boolean =
        word in UNITS || word in TENS || word in HUNDREDS || word in THOUSANDS || word in MILLIONS

    data class Parsed(val value: Double, val consumed: Int)

    /** Читает цепочку числительных начиная с [start]; [isNumberWord] для `words[start]` уже true. */
    fun parseWords(words: List<String>, start: Int): Parsed {
        var total = 0.0
        var current = 0.0
        var i = start
        var seenSmall = false
        while (i < words.size) {
            val w = words[i]
            when {
                w in UNITS -> {
                    if (seenSmall && (UNITS[w]!! >= 10 || current % 10 != 0.0)) break
                    current += UNITS[w]!!; seenSmall = true
                }

                w in TENS -> {
                    if (seenSmall && current % 100 != 0.0) break
                    current += TENS[w]!!; seenSmall = true
                }

                w in HUNDREDS -> {
                    if (seenSmall && current % 1000 != 0.0 && current >= 100) break
                    current += HUNDREDS[w]!!; seenSmall = true
                }

                w in THOUSANDS -> {
                    total += (if (current == 0.0) 1.0 else current) * 1000
                    current = 0.0; seenSmall = false
                }

                w in MILLIONS -> {
                    total += (if (current == 0.0) 1.0 else current) * 1_000_000
                    current = 0.0; seenSmall = false
                }

                w == "с" && i + 1 < words.size && words[i + 1] in HALF -> {
                    current += 0.5; i += 2; continue
                }

                else -> break
            }
            i++
        }
        return Parsed(total + current, i - start)
    }
}
