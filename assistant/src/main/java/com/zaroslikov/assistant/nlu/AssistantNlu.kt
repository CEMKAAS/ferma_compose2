package com.zaroslikov.assistant.nlu

/**
 * Фасад компактной локальной модели понимания команд: текст → [ParsedCommand].
 * Работает полностью офлайн и мгновенно; знание о проекте передаётся через [NluContext].
 */
class AssistantNlu {

    /**
     * Разбирает фразу. Если есть незавершённая команда [pending] (помощник задал уточняющий вопрос),
     * короткий ответ без глагола трактуется как заполнение недостающих полей.
     */
    fun parse(text: String, ctx: NluContext, pending: ParsedCommand.Write? = null): ParsedCommand {
        val tokens = tokenizeRu(text)
        if (tokens.isEmpty()) return ParsedCommand.Unknown(text)
        val intent = IntentClassifier.classify(tokens)

        if (intent == IntentType.CONFIRM) return ParsedCommand.Confirm
        if (intent == IntentType.CANCEL) return ParsedCommand.Cancel
        if (intent != IntentType.ANALYSIS && intent != IntentType.HELP && IntentClassifier.isAnimalUnsupported(tokens)) {
            return ParsedCommand.Unsupported(
                "Продажу, забой и падёж животных я пока не записываю — это делается из карточки животного, чтобы правильно уменьшить поголовье."
            )
        }

        if (pending != null && isSlotFill(tokens, intent)) {
            return merge(pending, tokens, ctx)
        }

        val extractor = SlotExtractor(tokens, ctx)
        return when (intent) {
            IntentType.ADD_PRODUCTION -> extractor.production()
            IntentType.ADD_SALE -> extractor.sale()
            IntentType.ADD_EXPENSE -> extractor.expense()
            IntentType.ADD_ANIMAL -> extractor.animal(intentHasPurchase = tokens.any { it.text.startsWith("купи") })
            IntentType.ANALYSIS -> extractor.analysis()
            IntentType.HELP -> ParsedCommand.Help
            IntentType.CONFIRM -> ParsedCommand.Confirm
            IntentType.CANCEL -> ParsedCommand.Cancel
            IntentType.UNKNOWN -> ParsedCommand.Unknown(text)
        }
    }

    /** Ответ на уточнение: нет глагола записи/вопроса, фраза короткая. */
    private fun isSlotFill(tokens: List<Token>, intent: IntentType): Boolean {
        if (intent == IntentType.ANALYSIS || intent == IntentType.HELP) return false
        val hasVerb = tokens.any { t ->
            IntentWords.startsWithAny(t.text, IntentWords.PRODUCTION) ||
                    IntentWords.startsWithAny(t.text, IntentWords.SALE) ||
                    IntentWords.startsWithAny(t.text, IntentWords.EXPENSE)
        }
        return !hasVerb && tokens.size <= 6
    }

    /** Дополняет незавершённую команду слотами из короткого ответа. */
    private fun merge(pending: ParsedCommand.Write, tokens: List<Token>, ctx: NluContext): ParsedCommand {
        val extractor = SlotExtractor(tokens, ctx)
        return when (pending) {
            is ParsedCommand.AddProduction -> {
                val fill = extractor.production()
                pending.copy(
                    title = pending.title ?: fill.title,
                    count = pending.count ?: fill.count,
                    suffix = if (pending.count == null) fill.suffix ?: pending.suffix else pending.suffix ?: fill.suffix,
                    animal = pending.animal ?: fill.animal,
                    note = pending.note ?: fill.note,
                    titleResolved = pending.titleResolved || (pending.title == null && fill.titleResolved)
                )
            }

            is ParsedCommand.AddSale -> {
                val fill = extractor.sale()
                val priceMissing = pending.pricePerUnit == null && pending.priceTotal == null
                // На вопрос «по какой цене?» одно число — это цена, а не количество
                val fillPerUnit = fill.pricePerUnit ?: if (priceMissing && pending.count != null && fill.pricePerUnit == null && fill.priceTotal == null) fill.count else null
                pending.copy(
                    title = pending.title ?: fill.title,
                    count = pending.count ?: fill.count,
                    suffix = if (pending.count == null) fill.suffix ?: pending.suffix else pending.suffix ?: fill.suffix,
                    pricePerUnit = if (priceMissing) fillPerUnit else pending.pricePerUnit,
                    priceTotal = if (priceMissing) fill.priceTotal else pending.priceTotal,
                    buyer = pending.buyer ?: fill.buyer,
                    note = pending.note ?: fill.note,
                    titleResolved = pending.titleResolved || (pending.title == null && fill.titleResolved)
                )
            }

            is ParsedCommand.AddExpense -> {
                val fill = extractor.expense()
                val priceMissing = pending.pricePerUnit == null && pending.priceTotal == null
                pending.copy(
                    title = pending.title ?: fill.title,
                    count = pending.count ?: fill.count,
                    suffix = pending.suffix ?: fill.suffix,
                    pricePerUnit = if (priceMissing) fill.pricePerUnit else pending.pricePerUnit,
                    priceTotal = if (priceMissing) fill.priceTotal else pending.priceTotal,
                    category = pending.category ?: fill.category,
                    note = pending.note ?: fill.note,
                    titleResolved = pending.titleResolved || (pending.title == null && fill.titleResolved)
                )
            }

            is ParsedCommand.AddAnimal -> {
                val fill = extractor.animal(intentHasPurchase = false)
                // На вопрос «как назвать?» ответ — имя, даже если слово не похоже на кличку
                val rawName = tokens.firstOrNull { !it.isNumber && !StopWords.isStop(it.text) && !Lexicon.isSpeciesWord(it.text) }?.text
                pending.copy(
                    name = pending.name ?: fill.name ?: rawName?.let { Lexicon.animalNameNominative(it) },
                    type = pending.type ?: fill.type,
                    species = if (pending.type == null) fill.species else pending.species,
                    sex = pending.sex ?: fill.sex,
                    note = pending.note ?: fill.note
                )
            }
        }
    }
}
