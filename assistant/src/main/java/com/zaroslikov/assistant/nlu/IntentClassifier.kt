package com.zaroslikov.assistant.nlu

/** Тип команды до извлечения слотов. */
enum class IntentType { ADD_PRODUCTION, ADD_SALE, ADD_EXPENSE, ADD_ANIMAL, ANALYSIS, HELP, CONFIRM, CANCEL, UNKNOWN }

/** Словари признаков интентов. Все списки — начала слов (ё уже заменена на е). */
object IntentWords {
    val PRODUCTION = listOf(
        "добав", "занес", "запиш", "запис", "внес", "получ", "собра", "собер", "надои", "надой", "подои",
        "снесл", "принес", "произв", "настриг", "накос", "накопа", "нарва", "выраст", "оприход", "учт", "учет", "зафикс"
    )
    val SALE = listOf("прода", "реализ", "сдал", "сбыл", "отгруз", "выруч")
    val SALE_CONTEXT = listOf("покупател", "клиент", "заказчик")
    val EXPENSE = listOf(
        "купи", "купл", "приобре", "потрат", "потрач", "затрат", "заплат", "оплат", "покуп", "истрат", "уплат", "расход", "трат", "ушло", "отдал"
    )
    val ANIMAL = listOf("животн", "скотин", "поголов", "завел", "завес", "появил", "родил", "отел", "окот", "опорос", "вылуп", "нов", "прибав", "ещ", "еще")
    val ANALYSIS_STRONG = listOf(
        "сколько", "скольк", "покаж", "анализ", "статистик", "отчет", "выручк", "доход", "прибыл", "убыт", "баланс",
        "остат", "осталос", "склад", "запас", "сводк", "динамик", "средн", "итог", "сравн", "наличи", "финанс", "касс"
    )
    val ANALYSIS_NOUNS = listOf("продаж", "расходы", "расходов", "затраты", "затрат", "надой", "надои", "производств", "сбор")
    val HELP = listOf("помощ", "помог", "умееш", "можеш", "привет", "здравств", "добрый", "команд", "инструкц", "подскаж", "как", "что")
    val CONFIRM = setOf(
        "да", "верно", "подтверждаю", "ок", "окей", "ok", "записывай", "записать", "запиши", "сохрани", "сохранить", "давай", "точно",
        "ага", "угу", "подтверди", "подтвердить", "согласен", "согласна", "правильно", "все", "всё", "так", "хорошо", "конечно", "пиши", "сохраняй"
    )

    /** Слова-связки («всё», «так»), которые сами по себе подтверждением не считаются. */
    val CONFIRM_WEAK = setOf("все", "всё", "так", "ok")

    /** Продажа, забой и падёж животных через помощник не записываются — это делается из карточки животного. */
    val ANIMAL_UNSUPPORTED = listOf("умер", "пал", "пала", "пали", "падеж", "сдох", "зарез", "забил", "забо", "издох", "погиб")
    val CANCEL = setOf(
        "нет", "отмена", "отмени", "отменить", "не", "надо", "нужно", "стоп", "неверно", "неправильно", "отбой", "записывай", "забудь", "сброс", "сбрось"
    )
    val CANCEL_PHRASES = listOf("не надо", "не нужно", "не записывай", "не так", "не то", "не верно", "не правильно", "не сохраняй")

    /** Фраза отмены как пара соседних токенов — без ложных срабатываний внутри слов («сено томаты»). */
    fun hasCancelPhrase(texts: List<String>): Boolean =
        texts.zipWithNext().any { (a, b) -> CANCEL_PHRASES.contains("$a $b") }

    fun startsWithAny(word: String, prefixes: List<String>): Boolean = prefixes.any { word.startsWith(it) }
}

/** Классификатор интентов: баллы по словам и структуре фразы (есть ли число с единицей, цена, вид животного…). */
object IntentClassifier {

    data class Features(
        val hasQuantity: Boolean,
        val hasPrice: Boolean,
        val hasSpecies: Boolean,
        val hasProductWord: Boolean,
        val hasUnitWord: Boolean,
        val hasNumber: Boolean
    )

    fun features(tokens: List<Token>): Features {
        var hasQuantity = false
        var hasPrice = false
        tokens.forEachIndexed { i, t ->
            if (t.isNumber) {
                val next = tokens.getOrNull(i + 1)?.text
                if (next != null && Units.unit(next, afterNumber = true) != null) hasQuantity = true
                if (next != null && Units.isCurrencyWord(next)) hasPrice = true
                val prev = tokens.getOrNull(i - 1)?.text
                if (prev == "по" || prev == "за" || prev?.startsWith("цен") == true || prev?.startsWith("стоим") == true) hasPrice = true
            }
        }
        val texts = tokens.map { it.text }
        return Features(
            hasQuantity = hasQuantity,
            hasPrice = hasPrice,
            hasSpecies = texts.any { Lexicon.isSpeciesWord(it) },
            hasProductWord = texts.any { Lexicon.product(it) != null },
            hasUnitWord = texts.any { Units.isUnitWord(it) },
            // «за 2025 год» — не данные для записи, а период
            hasNumber = tokens.withIndex().any { (i, t) ->
                t.isNumber && !(t.number!! in 2000.0..2100.0 && texts.getOrNull(i + 1)?.startsWith("год") == true)
            }
        )
    }

    /** Продажа/падёж животного: понятно, но не поддерживается. */
    fun isAnimalUnsupported(tokens: List<Token>): Boolean {
        val texts = tokens.map { it.text }
        val species = texts.any { Lexicon.isSpeciesWord(it) }
        val product = texts.any { Lexicon.product(it) != null }
        if (!species || product) return false
        return texts.any { w -> IntentWords.startsWithAny(w, IntentWords.SALE) || IntentWords.startsWithAny(w, IntentWords.ANIMAL_UNSUPPORTED) }
    }

    fun classify(tokens: List<Token>): IntentType {
        if (tokens.isEmpty()) return IntentType.UNKNOWN
        val texts = tokens.map { it.text }

        // Короткие ответы «да» / «нет» / «отмена»
        if (texts.size <= 4) {
            if (IntentWords.hasCancelPhrase(texts) || texts.first() in setOf("отмена", "отмени", "отбой", "стоп", "забудь")) {
                return IntentType.CANCEL
            }
            if (texts.all { it in IntentWords.CONFIRM } && texts.any { it !in IntentWords.CONFIRM_WEAK }) return IntentType.CONFIRM
            if (texts.all { it in IntentWords.CANCEL } && texts.any { it in setOf("нет", "не", "отмена", "неверно", "неправильно", "стоп", "отбой") }) {
                return IntentType.CANCEL
            }
        }

        val f = features(tokens)
        var production = 0
        var sale = 0
        var expense = 0
        var animal = 0
        var analysis = 0
        var help = 0

        for (w in texts) {
            if (IntentWords.startsWithAny(w, IntentWords.PRODUCTION)) production += 3
            if (IntentWords.startsWithAny(w, IntentWords.SALE)) sale += 3
            if (IntentWords.startsWithAny(w, IntentWords.SALE_CONTEXT)) sale += 2
            if (IntentWords.startsWithAny(w, IntentWords.EXPENSE)) expense += 3
            if (IntentWords.startsWithAny(w, IntentWords.ANIMAL)) animal += 2
            if (IntentWords.startsWithAny(w, IntentWords.ANALYSIS_STRONG)) analysis += 4
            if (IntentWords.startsWithAny(w, IntentWords.ANALYSIS_NOUNS)) analysis += 2
            if (IntentWords.startsWithAny(w, IntentWords.HELP)) help += 2
        }
        // «выручка» — и аналитика, и слово из SALE; без числа это вопрос
        if (texts.any { it.startsWith("выруч") } && !f.hasNumber) sale -= 3
        // «получил 500 рублей за молоко» — это выручка, а не 500 единиц продукции
        if (f.hasPrice && production > 0 && sale == 0 && expense == 0 &&
            texts.any { it.startsWith("получ") || it.startsWith("принес") || it.startsWith("заработ") }
        ) {
            sale += 4; production -= 3
        }

        // Вопросительная форма без данных для записи — аналитика
        val isQuestion = texts.any { it.startsWith("сколько") || it.startsWith("скольк") || it.startsWith("покаж") }
        if (isQuestion) analysis += 4
        if (!f.hasNumber && !f.hasQuantity && analysis > 0) analysis += 2
        // Существительное «продажи/расходы» без количества и цены — вопрос, а не запись
        if (f.hasQuantity || f.hasPrice) analysis -= 2 else if (analysis > 0) { sale -= 1; expense -= 1 }

        // Животное: вид назван, продукта и единиц объёма нет. Продажа и падёж животных — не наш случай
        if (f.hasSpecies && !f.hasProductWord && sale == 0 && texts.none { IntentWords.startsWithAny(it, IntentWords.ANIMAL_UNSUPPORTED) }) {
            val onlyHeadUnits = texts.none { w -> Units.isUnitWord(w) && Units.unit(w, true)?.suffix?.let { it != com.zaroslikov.domain.models.enums.Suffix.PIECES && it != com.zaroslikov.domain.models.enums.Suffix.HEADS } == true }
            if (onlyHeadUnits) animal += 4
            if (texts.any { it.startsWith("добав") || it.startsWith("нов") || it.startsWith("купи") || it.startsWith("завел") || it.startsWith("родил") }) animal += 2
        } else animal = 0

        // Запись без глагола: «10 литров молока» — продукция; с ценой — продажа или расход
        if (production == 0 && sale == 0 && expense == 0 && (f.hasQuantity || f.hasNumber) && analysis <= 0) {
            if (f.hasPrice) {
                val expenseProduct = texts.mapNotNull { Lexicon.product(it) }.any { it.expenseCategory != null }
                if (expenseProduct) expense += 2 else sale += 2
            } else production += 2
        }

        val scores = listOf(
            IntentType.ANALYSIS to analysis,
            IntentType.ADD_ANIMAL to animal,
            IntentType.ADD_SALE to sale,
            IntentType.ADD_EXPENSE to expense,
            IntentType.ADD_PRODUCTION to production,
            IntentType.HELP to help
        )
        val best = scores.maxByOrNull { it.second } ?: return IntentType.UNKNOWN
        if (best.second <= 0) return IntentType.UNKNOWN
        // «Помощь» выигрывает только без данных
        if (best.first == IntentType.HELP && (f.hasNumber || f.hasProductWord)) {
            val next = scores.filter { it.first != IntentType.HELP }.maxByOrNull { it.second }
            if (next != null && next.second > 0) return next.first
        }
        return best.first
    }
}
