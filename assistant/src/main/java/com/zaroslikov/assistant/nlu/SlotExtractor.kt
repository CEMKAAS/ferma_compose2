package com.zaroslikov.assistant.nlu

import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.Suffix
import java.time.LocalDate

/** Служебные слова, которые не могут быть названием продукта/именем. */
internal object StopWords {
    val PREPOSITIONS = setOf(
        "в", "во", "на", "за", "по", "от", "у", "с", "со", "из", "для", "к", "ко", "о", "об", "обо", "про", "при", "до", "без",
        "под", "над", "через", "около", "после", "перед", "между", "из-за"
    )
    val FILLERS = setOf(
        "я", "мы", "он", "она", "они", "мне", "нам", "меня", "тебя", "нас", "вас", "них", "его", "её", "ее", "ему", "ей", "ним", "ней",
        "мой", "моя", "мои", "моё", "мое", "это", "этот", "эту", "эти", "ещё", "еще",
        "тоже", "также", "пожалуйста", "давай", "надо", "нужно", "хочу", "хотел", "хотела", "можно", "вот", "ну", "так", "типа",
        "значит", "короче", "то", "что", "чтобы", "бы", "же", "ли", "и", "а", "но", "или", "все", "всё", "всего", "итого", "там",
        "тут", "здесь", "уже", "просто", "вроде", "примерно", "где-то", "около", "сейчас", "теперь", "потом", "затем", "снова",
        "опять", "ещё", "общем", "общей", "общая", "общий", "сумма", "сумму", "суммой", "сумме", "цена", "цену", "ценой", "цене",
        "стоимость", "стоимостью", "стоимости", "количество", "количестве", "количеством", "кол-во", "штук", "рублей", "деньги",
        "денег", "запись", "записи", "продукцию", "продукции", "продукция", "продукт", "продукта", "товар", "товара", "товары",
        "расход", "расходы", "покупку", "покупка", "продажу", "продажа", "продажи", "животное", "животного", "животных", "себе",
        "наш", "наша", "наше", "наши", "свой", "своя", "свои", "свою", "сегодняшний", "новый", "новую", "новое", "нового", "новая",
        "одного", "одну", "один", "одна", "одно", "каждый", "каждая", "лишь", "только", "именно", "ровно", "почти", "как", "будто",
        "кг", "л", "шт", "год", "месяц", "неделя", "день", "дня", "дней", "назад"
    )
    val ANIMAL_PREPOSITIONS = setOf("от", "у", "с", "со")
    val NOTE_MARKERS = listOf("примечани", "заметк", "коммент", "пометк")
    val CATEGORY_MARKERS = listOf("категори")
    val BUYER_MARKERS = listOf("покупател", "клиент", "заказчик")
    val FOR = setOf("для")

    fun isVerbLike(word: String): Boolean =
        IntentWords.startsWithAny(word, IntentWords.PRODUCTION) ||
                IntentWords.startsWithAny(word, IntentWords.SALE) ||
                IntentWords.startsWithAny(word, IntentWords.EXPENSE) ||
                IntentWords.startsWithAny(word, IntentWords.ANALYSIS_STRONG) ||
                IntentWords.startsWithAny(word, IntentWords.ANALYSIS_NOUNS) ||
                word.startsWith("родил") || word.startsWith("появил") || word.startsWith("завел") ||
                word.startsWith("помощ") || word.startsWith("умееш")

    fun isStop(word: String): Boolean = word in PREPOSITIONS || word in FILLERS || isVerbLike(word)
}

/** Извлечение слотов из токенов для заданного интента. */
class SlotExtractor(
    private val tokens: List<Token>,
    private val ctx: NluContext
) {
    private val texts = tokens.map { it.text }
    private val used = mutableSetOf<Int>()

    private data class Money(val value: Double, val perUnit: Boolean, val currency: Suffix?)

    // ---------- Публичные разборы ----------

    fun production(): ParsedCommand.AddProduction {
        val date = date()
        extractNote()
        val animal = animal()
        val quantity = quantity()
        val count = quantity?.first ?: looseNumber()
        val title = title(TitleScope.PRODUCTION) ?: impliedProduct(animal)
        return ParsedCommand.AddProduction(
            title = title?.title,
            count = count,
            suffix = quantity?.second ?: title?.suffix,
            date = date,
            animal = animal,
            note = note,
            titleResolved = title?.resolved == true
        )
    }

    fun sale(): ParsedCommand.AddSale {
        val date = date()
        extractNote()
        val money = money()
        val quantity = quantity()
        val buyer = buyer()
        var count = quantity?.first
        var total = if (money?.perUnit == false) money.value else null
        var perUnit = if (money?.perUnit == true) money.value else null
        if (count == null) {
            val loose = looseNumbers()
            when {
                money != null && loose.isNotEmpty() -> count = loose[0]
                loose.size >= 2 -> { count = loose[0]; total = loose[1] }
                loose.size == 1 -> count = loose[0]
            }
        } else if (money == null) {
            looseNumbers().firstOrNull()?.let { total = it }
        }
        val title = title(TitleScope.SALE)
        return ParsedCommand.AddSale(
            title = title?.title,
            count = count,
            suffix = quantity?.second ?: title?.suffix,
            pricePerUnit = perUnit,
            priceTotal = total,
            buyer = buyer,
            date = date,
            note = note,
            titleResolved = title?.resolved == true,
            productOrigin = title?.origin
        )
    }

    fun expense(): ParsedCommand.AddExpense {
        val date = date()
        extractNote()
        val money = money()
        val quantity = quantity()
        val explicitCategory = explicitCategory()
        var count = quantity?.first
        var total = if (money?.perUnit == false) money.value else null
        var perUnit = if (money?.perUnit == true) money.value else null
        val looseIdx = looseNumberIndices()
        val loose = looseIdx.map { tokens[it].number!! }
        if (count == null) {
            when {
                money != null && loose.isNotEmpty() -> count = loose[0]
                loose.size >= 2 -> { count = loose[0]; total = loose[1] }
                // «купил 2 мешка комбикорма» — число перед существительным это количество, а не сумма
                loose.size == 1 -> if (numberBeforeNoun(looseIdx[0])) count = loose[0] else total = loose[0]
            }
        } else if (money == null && loose.isNotEmpty()) total = loose[0]
        val title = title(TitleScope.EXPENSE)
        val category = explicitCategory
            ?: title?.lexicon?.expenseCategory
            ?: Lexicon.expenseCategory(texts)
            ?: knownCategory()
        return ParsedCommand.AddExpense(
            title = title?.title,
            count = count ?: 1.0,
            suffix = quantity?.second ?: title?.suffix ?: Suffix.PIECES,
            pricePerUnit = perUnit,
            priceTotal = total,
            category = category,
            date = date,
            note = note,
            titleResolved = title?.resolved == true
        )
    }

    fun animal(intentHasPurchase: Boolean): ParsedCommand.AddAnimal {
        val date = date()
        extractNote()
        val money = money()
        var species: SpeciesMatch? = null
        texts.forEachIndexed { i, w ->
            if (species == null && i !in used) Lexicon.species(w)?.let { species = it; used += i }
        }
        val quantity = quantity()
        val looseIdx = looseNumberIndices()
        val loose = looseIdx.map { tokens[it].number!! }
        // «купил корову 50000»: число без единицы не перед существительным при покупке — это цена, а не поголовье
        val looseIsPrice = quantity == null && intentHasPurchase && money == null && loose.size == 1 && !numberBeforeNoun(looseIdx[0])
        val count = (quantity?.first ?: loose.firstOrNull()?.takeIf { !looseIsPrice })?.toInt()?.coerceAtLeast(1) ?: 1
        val isGroup = count > 1
        val match = species
        val rawName = remainingWords().firstOrNull { it.length >= 2 }
        val name = when {
            isGroup -> null
            rawName != null -> Lexicon.animalNameNominative(rawName)
            else -> null
        }
        val sex = match?.sex
        val type = when {
            match == null -> remainingWords().getOrNull(if (rawName != null) 1 else 0)?.capitalizeRu()
            match.displayType != null -> if (isGroup && !match.isYoung) match.species.groupName else match.displayType
            isGroup -> match.species.groupName
            sex != null -> match.species.nameFor(sex)
            else -> match.species.commonName
        }
        val price = when {
            money != null -> money.value * (if (money.perUnit) count else 1)
            looseIsPrice -> loose[0]
            intentHasPurchase -> loose.getOrNull(if (quantity == null) 1 else 0)
            else -> null
        }
        return ParsedCommand.AddAnimal(
            name = name,
            type = type,
            species = match?.species ?: AnimalSpecies.UNKNOWN,
            sex = sex,
            count = count,
            date = date,
            priceTotal = price,
            note = note
        )
    }

    fun analysis(): ParsedCommand.Analysis {
        val period = RuDates.extractPeriod(tokens, ctx.today)
        period?.used?.let { used += it }
        var speciesMatch: SpeciesMatch? = null
        texts.forEachIndexed { i, w -> if (speciesMatch == null) Lexicon.species(w)?.let { speciesMatch = it; used += i } }

        val has = { prefixes: List<String> -> texts.any { w -> prefixes.any { w.startsWith(it) } } }
        val stock = has(listOf("остат", "осталос", "склад", "запас", "наличи", "хранит"))
        val finance = has(listOf("выручк", "доход", "заработ", "прибыл", "убыт", "баланс", "финанс", "денег", "деньги", "касс"))
        val expenses = has(listOf("расход", "потрат", "затрат", "трат", "купил", "покупк", "потрач"))
        val sales = has(listOf("прода", "реализ", "сдал"))
        val animals = speciesMatch != null || has(listOf("животн", "поголов", "скот", "стад", "птиц"))
        val summary = has(listOf("анализ", "статистик", "отчет", "сводк", "итог", "динамик", "покаж"))

        val title = title(TitleScope.ANY)
        val product = title?.title
        val kind = when {
            stock -> AnalysisKind.STOCK
            animals && product == null -> AnalysisKind.ANIMALS
            sales -> AnalysisKind.SALES
            expenses -> AnalysisKind.EXPENSES
            finance && product != null -> AnalysisKind.SALES
            finance -> AnalysisKind.FINANCE
            summary && product != null -> AnalysisKind.PRODUCT_SUMMARY
            summary -> AnalysisKind.FINANCE
            else -> AnalysisKind.PRODUCTION
        }
        return ParsedCommand.Analysis(
            kind = kind,
            product = product,
            period = period?.value ?: Period.currentMonth(ctx.today),
            species = speciesMatch?.species,
            periodExplicit = period != null
        )
    }

    // ---------- Слоты ----------

    private var note: String? = null

    private fun date(): LocalDate {
        val found = RuDates.extractDate(tokens, ctx.today) ?: return ctx.today
        used += found.used
        return found.value
    }

    private fun extractNote() {
        texts.forEachIndexed { i, w ->
            if (i !in used && StopWords.NOTE_MARKERS.any { w.startsWith(it) } && i + 1 < texts.size) {
                note = texts.subList(i + 1, texts.size).joinToString(" ").capitalizeRu()
                used += (i until texts.size)
                return
            }
        }
    }

    /** Деньги: число + валюта, «по N», «за N», «N рублей за литр». */
    private fun money(): Money? {
        var best: Money? = null
        var bestIdx = -1
        tokens.forEachIndexed { i, t ->
            if (!t.isNumber || i in used || t.isDate) return@forEachIndexed
            val next = texts.getOrNull(i + 1)
            val prev = if (i > 0 && (i - 1) !in used) texts[i - 1] else null
            val currency = next?.let { Units.currency(it) }
            val perUnitByPrev = prev == "по" || prev?.startsWith("цен") == true
            val totalByPrev = prev == "за" || prev == "на" || prev?.startsWith("стоим") == true || prev?.startsWith("сумм") == true || prev?.startsWith("общ") == true
            if (currency == null && !perUnitByPrev && !totalByPrev) return@forEachIndexed
            // «80 рублей за литр» — цена за единицу
            val afterCurrency = if (currency != null) i + 2 else i + 1
            val perUnitByUnit = texts.getOrNull(afterCurrency) == "за" &&
                    texts.getOrNull(afterCurrency + 1)?.let { Units.isUnitWord(it) || it in setOf("штуку", "штука", "единицу", "голову", "десяток", "литр", "кило", "килограмм") } == true
            val perUnit = perUnitByPrev || perUnitByUnit
            // Число с единицей измерения сразу после — это количество, не деньги
            if (currency == null && next != null && Units.unit(next, afterNumber = true) != null) return@forEachIndexed
            if (best == null || (currency != null && best?.currency == null)) {
                // Кандидат с валютой вытесняет кандидата без неё — освобождаем его индекс
                if (bestIdx >= 0) used -= bestIdx
                best = Money(t.number!!, perUnit, currency)
                bestIdx = i
                used += i
                if (prev != null && (perUnitByPrev || totalByPrev)) used += i - 1
                if (currency != null) used += i + 1
                if (perUnitByUnit) { used += afterCurrency; used += afterCurrency + 1 }
            }
        }
        return best
    }

    /** Количество: число + единица, «пол-литра», «два десятка». */
    private fun quantity(): Pair<Double, Suffix>? {
        tokens.forEachIndexed { i, t ->
            if (i in used) return@forEachIndexed
            if (t.isNumber && !t.isDate) {
                val next = texts.getOrNull(i + 1)
                val unit = next?.takeIf { (i + 1) !in used }?.let { Units.unit(it, afterNumber = true) }
                if (unit != null) {
                    used += i; used += i + 1
                    return t.number!! * unit.multiplier to unit.suffix
                }
            }
            // «пол-литра», «полкило», «поллитра»
            val w = t.text.removePrefix("пол").removePrefix("-")
            if (t.text.startsWith("пол") && w.length >= 2 && w != t.text) {
                val unit = Units.unit(w, afterNumber = true) ?: Units.unit(w.removeSuffix("а"), afterNumber = true)
                    ?: if (w.startsWith("кил")) UnitMatch(Suffix.KILOGRAM) else if (w.startsWith("тонн")) UnitMatch(Suffix.TONS) else null
                if (unit != null) {
                    used += i
                    return 0.5 * unit.multiplier to unit.suffix
                }
            }
        }
        return null
    }

    private fun looseNumberIndices(): List<Int> {
        val result = ArrayList<Int>()
        tokens.forEachIndexed { i, t ->
            if (t.isNumber && !t.isDate && i !in used) {
                result += i
                used += i
            }
        }
        return result
    }

    private fun looseNumbers(): List<Double> = looseNumberIndices().map { tokens[it].number!! }

    private fun looseNumber(): Double? = looseNumbers().firstOrNull()

    /** За числом идёт обычное слово («2 мешка», «3 коробки») — число считает предметы. */
    private fun numberBeforeNoun(index: Int): Boolean {
        val next = texts.getOrNull(index + 1) ?: return false
        if ((index + 1) in used || StopWords.isStop(next) || Units.isCurrencyWord(next) || Units.isUnitWord(next)) return false
        if (Lexicon.isSpeciesWord(next)) return true
        if (Lexicon.product(next) != null) return false
        return next.length >= 3 && tokens[index + 1].number == null
    }

    /** Продукт, который подразумевает глагол или животное: «надоила 12 л от Зорьки» → Молоко. */
    private fun impliedProduct(animal: KnownAnimal?): TitleMatch? {
        val lemma = when {
            texts.any { it.startsWith("надои") || it.startsWith("подои") || it.startsWith("доил") || it.startsWith("надой") } -> "Молоко"
            texts.any { it.startsWith("снесл") } -> "Яйца"
            texts.any { it.startsWith("настриг") } -> "Шерсть"
            texts.any { it.startsWith("накос") } -> "Сено"
            animal != null -> when (AnimalSpecies.resolve(animal.type)) {
                AnimalSpecies.COW, AnimalSpecies.GOAT, AnimalSpecies.SHEEP, AnimalSpecies.HORSE -> "Молоко"
                AnimalSpecies.CHICKEN, AnimalSpecies.QUAIL, AnimalSpecies.DUCK, AnimalSpecies.GOOSE,
                AnimalSpecies.TURKEY, AnimalSpecies.PHEASANT, AnimalSpecies.MUSCOVY_DUCK -> "Яйца"
                else -> null
            }
            else -> null
        } ?: return null
        val known = ctx.productTitles.firstOrNull { normalizeRu(it.title) == normalizeRu(lemma) }
        val lexicon = Lexicon.product(lemma)
        return TitleMatch(known?.title ?: lemma, known?.suffix ?: lexicon?.defaultSuffix, resolved = true, lexicon = lexicon, origin = known?.origin)
    }

    private fun animal(): KnownAnimal? {
        if (ctx.animals.isEmpty()) return null
        // «от Зорьки», «у Зорьки»
        texts.forEachIndexed { i, w ->
            if (w in StopWords.ANIMAL_PREPOSITIONS && i + 1 < texts.size && (i + 1) !in used) {
                val match = EntityResolver.bestWord(texts[i + 1], ctx.animals, 0.6) { it.name }
                if (match != null) {
                    used += i; used += i + 1
                    return match.value
                }
            }
        }
        // Имя животного где угодно во фразе — только при высокой уверенности
        texts.forEachIndexed { i, w ->
            if (i in used || StopWords.isStop(w) || tokens[i].isNumber) return@forEachIndexed
            val match = EntityResolver.bestWord(w, ctx.animals, 0.85) { it.name }
            if (match != null && Lexicon.product(w) == null) {
                used += i
                return match.value
            }
        }
        return null
    }

    private fun buyer(): String? {
        texts.forEachIndexed { i, w ->
            val marker = StopWords.BUYER_MARKERS.any { w.startsWith(it) } || w in StopWords.FOR
            if (marker && i + 1 < texts.size && (i + 1) !in used && !tokens[i + 1].isNumber) {
                val raw = texts[i + 1]
                used += i; used += i + 1
                val known = EntityResolver.bestWord(raw, ctx.buyers, 0.7) { it }
                return known?.value ?: Lexicon.buyerNominative(raw)
            }
        }
        texts.forEachIndexed { i, w ->
            if (i in used || StopWords.isStop(w) || tokens[i].isNumber || Units.isUnitWord(w) || Units.isCurrencyWord(w)) return@forEachIndexed
            if (Lexicon.product(w) != null || Lexicon.isSpeciesWord(w)) return@forEachIndexed
            if (EntityResolver.bestWord(w, ctx.productTitles, 0.75) { it.title } != null) return@forEachIndexed
            val known = EntityResolver.bestWord(w, ctx.buyers, 0.8) { it }
            if (known != null) {
                used += i
                return known.value
            }
        }
        // Фамилия в дательном падеже: «Иванову», «Петровой»
        texts.forEachIndexed { i, w ->
            if (i in used || StopWords.isStop(w) || tokens[i].isNumber || Units.isUnitWord(w)) return@forEachIndexed
            if (EntityResolver.bestWord(w, ctx.productTitles, 0.75) { it.title } != null) return@forEachIndexed
            if (w.length >= 6 && (w.endsWith("ову") || w.endsWith("еву") || w.endsWith("ину") || w.endsWith("ыну") || w.endsWith("овой") || w.endsWith("евой") || w.endsWith("иной"))) {
                if (Lexicon.product(w) == null && !Lexicon.isSpeciesWord(w)) {
                    used += i
                    return Lexicon.buyerNominative(w)
                }
            }
        }
        return null
    }

    private fun explicitCategory(): String? {
        texts.forEachIndexed { i, w ->
            if (StopWords.CATEGORY_MARKERS.any { w.startsWith(it) } && i + 1 < texts.size && (i + 1) !in used) {
                val raw = texts[i + 1]
                used += i; used += i + 1
                val known = EntityResolver.bestWord(raw, ctx.expenseCategories, 0.7) { it }
                return known?.value ?: raw.capitalizeRu()
            }
        }
        return null
    }

    private fun knownCategory(): String? {
        if (ctx.expenseCategories.isEmpty()) return null
        texts.forEachIndexed { i, w ->
            if (i in used || StopWords.isStop(w) || tokens[i].isNumber) return@forEachIndexed
            EntityResolver.bestWord(w, ctx.expenseCategories, 0.85) { it }?.let { return it.value }
        }
        return null
    }

    // ---------- Название ----------

    enum class TitleScope { PRODUCTION, SALE, EXPENSE, ANY }

    data class TitleMatch(
        val title: String,
        val suffix: Suffix?,
        val resolved: Boolean,
        val lexicon: LexiconProduct? = null,
        /** Происхождение известного продукта проекта; null — продукта в проекте нет. */
        val origin: com.zaroslikov.domain.models.enums.ProductOrigin? = null
    )

    private fun remainingWords(): List<String> =
        texts.filterIndexed { i, w ->
            i !in used && !tokens[i].isNumber && !tokens[i].isDate && !StopWords.isStop(w) &&
                    !Units.isUnitWord(w) && !Units.isCurrencyWord(w) && w.length >= 2
        }

    private fun title(scope: TitleScope): TitleMatch? {
        val words = remainingWords()
            .filter { scope == TitleScope.ANY || !Lexicon.isSpeciesWord(it) || Lexicon.product(it) != null }
        if (words.isEmpty()) return null

        // 1. Известные продукты проекта — по всей фразе, затем по словам
        val candidates = when (scope) {
            TitleScope.EXPENSE -> ctx.productTitles.filter { it.isExpense } + ctx.productTitles.filter { !it.isExpense }
            TitleScope.PRODUCTION -> ctx.productTitles.filter { !it.isExpense } + ctx.productTitles.filter { it.isExpense }
            else -> ctx.productTitles
        }
        EntityResolver.best(words, candidates, 0.75) { it.title }?.let {
            return TitleMatch(it.value.title, it.value.suffix, resolved = true, origin = it.value.origin)
        }
        val bestByWord = words.mapNotNull { w -> EntityResolver.bestWord(w, candidates, 0.75) { it.title }?.let { m -> w to m } }
            .maxByOrNull { it.second.score }
        // Слово из фразы совпало с продуктом проекта — берём его, остальные слова считаем уточнением
        if (bestByWord != null && (words.size == 1 || bestByWord.second.score >= 0.9)) {
            return TitleMatch(bestByWord.second.value.title, bestByWord.second.value.suffix, resolved = true, origin = bestByWord.second.value.origin)
        }

        // 2. Встроенный лексикон: «молока» → «Молоко»; из вложенных берём самый длинный («Козье молоко»)
        val lexiconHits = words.mapNotNull { Lexicon.product(it) }.distinct()
        if (lexiconHits.isNotEmpty()) {
            val lemma = lexiconHits.maxByOrNull { hit -> hit.lemma.length }!!
            val preferred = lexiconHits.firstOrNull { hit -> lexiconHits.any { other -> other != hit && hit.lemma.contains(other.lemma, ignoreCase = true) } } ?: lemma
            // Если лексикон знает слово, а в проекте есть продукт с таким названием — суффикс берём из проекта
            val known = ctx.productTitles.firstOrNull { normalizeRu(it.title) == normalizeRu(preferred.lemma) }
            return TitleMatch(known?.title ?: preferred.lemma, known?.suffix ?: preferred.defaultSuffix, resolved = true, lexicon = preferred, origin = known?.origin)
        }

        // 3. Сырые слова пользователя
        return TitleMatch(words.joinToString(" ").capitalizeRu(), null, resolved = false)
    }
}
