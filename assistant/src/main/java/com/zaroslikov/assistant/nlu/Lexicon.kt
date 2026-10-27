package com.zaroslikov.assistant.nlu

import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.Suffix

/**
 * Продукт/товар из встроенного словаря: [lemma] — именительный падеж для записи в БД,
 * [stems] — начала словоформ, [defaultSuffix] — единица по умолчанию, [expenseCategory] —
 * категория для расходов (корма, ветеринария…).
 */
data class LexiconProduct(
    val lemma: String,
    val stems: List<String>,
    val defaultSuffix: Suffix,
    val expenseCategory: String? = null
)

/** Вид животного по слову: [sex] — если слово задаёт пол (`true` самец), [displayType] — тип для синонимов («Цыплята»). */
data class SpeciesMatch(
    val species: AnimalSpecies,
    val sex: Boolean?,
    val displayType: String? = null,
    val isYoung: Boolean = false
)

/** Встроенные словари фермерских продуктов, животных и категорий расходов. */
object Lexicon {

    const val CATEGORY_FEED = "Корма"
    const val CATEGORY_VET = "Ветеринария"
    const val CATEGORY_UTILITIES = "Коммунальные услуги"
    const val CATEGORY_FUEL = "Топливо"
    const val CATEGORY_TOOLS = "Инвентарь"
    const val CATEGORY_REPAIR = "Ремонт и стройка"
    const val CATEGORY_TRANSPORT = "Транспорт"
    const val CATEGORY_SERVICES = "Услуги"
    const val CATEGORY_PACKAGING = "Упаковка"
    const val CATEGORY_SEEDS = "Семена и рассада"

    private val PRODUCTS: List<LexiconProduct> = listOf(
        // Продукция хозяйства
        LexiconProduct("Молоко", listOf("молок", "молоч"), Suffix.LITERS),
        LexiconProduct("Козье молоко", listOf("козь"), Suffix.LITERS),
        LexiconProduct("Яйца", listOf("яйц", "яиц", "яич"), Suffix.PIECES),
        LexiconProduct("Мясо", listOf("мяс"), Suffix.KILOGRAM),
        LexiconProduct("Творог", listOf("творог", "творож"), Suffix.KILOGRAM),
        LexiconProduct("Сыр", listOf("сыр"), Suffix.KILOGRAM),
        LexiconProduct("Сметана", listOf("сметан"), Suffix.KILOGRAM),
        LexiconProduct("Сливки", listOf("сливк", "сливок"), Suffix.LITERS),
        LexiconProduct("Масло", listOf("масл"), Suffix.KILOGRAM),
        LexiconProduct("Кефир", listOf("кефир"), Suffix.LITERS),
        LexiconProduct("Йогурт", listOf("йогурт"), Suffix.LITERS),
        LexiconProduct("Брынза", listOf("брынз"), Suffix.KILOGRAM),
        LexiconProduct("Мёд", listOf("мед", "медом", "меда"), Suffix.KILOGRAM),
        LexiconProduct("Воск", listOf("воск"), Suffix.KILOGRAM),
        LexiconProduct("Прополис", listOf("прополис"), Suffix.GRAM),
        LexiconProduct("Пыльца", listOf("пыльц"), Suffix.GRAM),
        LexiconProduct("Перга", listOf("перг"), Suffix.GRAM),
        LexiconProduct("Шерсть", listOf("шерст"), Suffix.KILOGRAM),
        LexiconProduct("Пух", listOf("пух"), Suffix.GRAM),
        LexiconProduct("Перо", listOf("пер", "перь"), Suffix.KILOGRAM),
        LexiconProduct("Навоз", listOf("навоз"), Suffix.KILOGRAM),
        LexiconProduct("Сало", listOf("сал"), Suffix.KILOGRAM),
        LexiconProduct("Колбаса", listOf("колбас"), Suffix.KILOGRAM),
        LexiconProduct("Свинина", listOf("свинин"), Suffix.KILOGRAM),
        LexiconProduct("Говядина", listOf("говядин"), Suffix.KILOGRAM),
        LexiconProduct("Баранина", listOf("баранин"), Suffix.KILOGRAM),
        LexiconProduct("Крольчатина", listOf("крольчатин"), Suffix.KILOGRAM),
        LexiconProduct("Курятина", listOf("курятин"), Suffix.KILOGRAM),
        LexiconProduct("Рыба", listOf("рыб"), Suffix.KILOGRAM),
        LexiconProduct("Грибы", listOf("гриб"), Suffix.KILOGRAM),
        LexiconProduct("Ягоды", listOf("ягод"), Suffix.KILOGRAM),
        LexiconProduct("Клубника", listOf("клубник"), Suffix.KILOGRAM),
        LexiconProduct("Малина", listOf("малин"), Suffix.KILOGRAM),
        LexiconProduct("Смородина", listOf("смородин"), Suffix.KILOGRAM),
        LexiconProduct("Вишня", listOf("вишн", "вишен"), Suffix.KILOGRAM),
        LexiconProduct("Яблоки", listOf("яблок"), Suffix.KILOGRAM),
        LexiconProduct("Груши", listOf("груш"), Suffix.KILOGRAM),
        LexiconProduct("Картофель", listOf("картофел", "картошк"), Suffix.KILOGRAM),
        LexiconProduct("Морковь", listOf("морков"), Suffix.KILOGRAM),
        LexiconProduct("Свёкла", listOf("свекл"), Suffix.KILOGRAM),
        LexiconProduct("Капуста", listOf("капуст"), Suffix.KILOGRAM),
        LexiconProduct("Тыква", listOf("тыкв"), Suffix.KILOGRAM),
        LexiconProduct("Кабачки", listOf("кабачк", "кабачок"), Suffix.KILOGRAM),
        LexiconProduct("Огурцы", listOf("огурц", "огурец"), Suffix.KILOGRAM),
        LexiconProduct("Помидоры", listOf("помидор", "томат"), Suffix.KILOGRAM),
        LexiconProduct("Лук", listOf("лук"), Suffix.KILOGRAM),
        LexiconProduct("Чеснок", listOf("чеснок", "чесноч"), Suffix.KILOGRAM),
        LexiconProduct("Зелень", listOf("зелен"), Suffix.KILOGRAM),
        LexiconProduct("Сено", listOf("сен"), Suffix.KILOGRAM, CATEGORY_FEED),
        LexiconProduct("Солома", listOf("солом"), Suffix.KILOGRAM, CATEGORY_FEED),
        LexiconProduct("Дрова", listOf("дров"), Suffix.CUBIC_METERS, CATEGORY_FUEL),
        LexiconProduct("Рассада", listOf("рассад"), Suffix.PIECES, CATEGORY_SEEDS),
        LexiconProduct("Саженцы", listOf("саженц", "саженец"), Suffix.PIECES, CATEGORY_SEEDS),
        LexiconProduct("Семена", listOf("семен", "семян"), Suffix.KILOGRAM, CATEGORY_SEEDS),
        // Корма и расходники
        LexiconProduct("Комбикорм", listOf("комбикорм"), Suffix.KILOGRAM, CATEGORY_FEED),
        LexiconProduct("Корм", listOf("корм"), Suffix.KILOGRAM, CATEGORY_FEED),
        LexiconProduct("Зерно", listOf("зерн"), Suffix.KILOGRAM, CATEGORY_FEED),
        LexiconProduct("Пшеница", listOf("пшениц"), Suffix.KILOGRAM, CATEGORY_FEED),
        LexiconProduct("Овёс", listOf("овес", "овс"), Suffix.KILOGRAM, CATEGORY_FEED),
        LexiconProduct("Ячмень", listOf("ячмен"), Suffix.KILOGRAM, CATEGORY_FEED),
        LexiconProduct("Кукуруза", listOf("кукуруз"), Suffix.KILOGRAM, CATEGORY_FEED),
        LexiconProduct("Отруби", listOf("отруб"), Suffix.KILOGRAM, CATEGORY_FEED),
        LexiconProduct("Жмых", listOf("жмых"), Suffix.KILOGRAM, CATEGORY_FEED),
        LexiconProduct("Премикс", listOf("премикс"), Suffix.KILOGRAM, CATEGORY_FEED),
        LexiconProduct("Соль", listOf("соль", "соли"), Suffix.KILOGRAM, CATEGORY_FEED),
        LexiconProduct("Мел", listOf("мел"), Suffix.KILOGRAM, CATEGORY_FEED),
        LexiconProduct("Ракушка", listOf("ракушк"), Suffix.KILOGRAM, CATEGORY_FEED),
        LexiconProduct("Опилки", listOf("опилк", "опилок"), Suffix.KILOGRAM, CATEGORY_TOOLS),
        LexiconProduct("Ветеринар", listOf("ветеринар", "ветврач"), Suffix.PIECES, CATEGORY_VET),
        LexiconProduct("Вакцина", listOf("вакцин", "привив"), Suffix.PIECES, CATEGORY_VET),
        LexiconProduct("Лекарства", listOf("лекарств", "препарат", "таблетк"), Suffix.PIECES, CATEGORY_VET),
        LexiconProduct("Витамины", listOf("витамин"), Suffix.PIECES, CATEGORY_VET),
        LexiconProduct("Антибиотик", listOf("антибиотик"), Suffix.PIECES, CATEGORY_VET),
        LexiconProduct("Электричество", listOf("электричеств", "электроэнерг", "свет"), Suffix.UNITS, CATEGORY_UTILITIES),
        LexiconProduct("Вода", listOf("вод"), Suffix.CUBIC_METERS, CATEGORY_UTILITIES),
        LexiconProduct("Газ", listOf("газ"), Suffix.CUBIC_METERS, CATEGORY_UTILITIES),
        LexiconProduct("Интернет", listOf("интернет"), Suffix.UNITS, CATEGORY_UTILITIES),
        LexiconProduct("Бензин", listOf("бензин"), Suffix.LITERS, CATEGORY_FUEL),
        LexiconProduct("Дизель", listOf("дизел", "солярк", "дт"), Suffix.LITERS, CATEGORY_FUEL),
        LexiconProduct("Уголь", listOf("угол", "угл"), Suffix.KILOGRAM, CATEGORY_FUEL),
        LexiconProduct("Инструмент", listOf("инструмент"), Suffix.PIECES, CATEGORY_TOOLS),
        LexiconProduct("Инвентарь", listOf("инвентар"), Suffix.PIECES, CATEGORY_TOOLS),
        LexiconProduct("Ведро", listOf("ведр", "ведер"), Suffix.PIECES, CATEGORY_TOOLS),
        LexiconProduct("Поилка", listOf("поилк", "поилок"), Suffix.PIECES, CATEGORY_TOOLS),
        LexiconProduct("Кормушка", listOf("кормушк", "кормушек"), Suffix.PIECES, CATEGORY_TOOLS),
        LexiconProduct("Клетка", listOf("клетк", "клеток"), Suffix.PIECES, CATEGORY_TOOLS),
        LexiconProduct("Сетка", listOf("сетк", "сеток"), Suffix.METERS, CATEGORY_REPAIR),
        LexiconProduct("Доски", listOf("доск", "досок"), Suffix.PIECES, CATEGORY_REPAIR),
        LexiconProduct("Гвозди", listOf("гвозд"), Suffix.KILOGRAM, CATEGORY_REPAIR),
        LexiconProduct("Цемент", listOf("цемент"), Suffix.KILOGRAM, CATEGORY_REPAIR),
        LexiconProduct("Ремонт", listOf("ремонт"), Suffix.UNITS, CATEGORY_REPAIR),
        LexiconProduct("Доставка", listOf("доставк"), Suffix.UNITS, CATEGORY_TRANSPORT),
        LexiconProduct("Транспорт", listOf("транспорт", "перевозк"), Suffix.UNITS, CATEGORY_TRANSPORT),
        LexiconProduct("Аренда", listOf("аренд"), Suffix.UNITS, CATEGORY_SERVICES),
        LexiconProduct("Упаковка", listOf("упаковк", "тара", "тары", "тару", "пакет", "бутылк", "банк"), Suffix.PIECES, CATEGORY_PACKAGING)
    )

    /** Слово → продукт словаря; «молока» → «Молоко». */
    fun product(word: String): LexiconProduct? {
        val w = normalizeRu(word)
        if (w.length < 3) return null
        // Сначала точные/более длинные стемы, чтобы «комбикорм» не стал «кормом»
        return PRODUCTS
            .flatMap { p -> p.stems.map { it to p } }
            .filter { (stem, _) -> matchesStem(w, stem) }
            .maxByOrNull { (stem, _) -> stem.length }
            ?.second
    }

    /** Слово начинается со стема, а «хвост» — не длиннее окончания (3 буквы). */
    fun matchesStem(word: String, stem: String): Boolean =
        word.startsWith(stem) && word.length - stem.length <= 3

    // ----- Животные -----

    private data class SpeciesForms(val match: SpeciesMatch, val forms: Set<String>)

    private fun forms(vararg f: String) = f.toSet()

    private val SPECIES: List<SpeciesForms> = listOf(
        SpeciesForms(SpeciesMatch(AnimalSpecies.COW, false), forms("корова", "коровы", "корову", "корове", "коровой", "коров", "коровам", "коровами", "коровка", "коровку", "бурёнка", "буренка", "буренку")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.COW, true), forms("бык", "быка", "быку", "быком", "быки", "быков", "бычок", "бычка", "бычку", "бычки", "бычков")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.COW, false, "Тёлка"), forms("телка", "телку", "телки", "телок", "тёлка", "тёлку")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.COW, null, "Телёнок", isYoung = true), forms("теленок", "теленка", "телята", "телят", "телятам", "телёнок", "телёнка")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.COW, null, "КРС"), forms("крс")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.GOAT, false), forms("коза", "козы", "козу", "козе", "козой", "коз", "козам", "козами", "козочка", "козочку")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.GOAT, true), forms("козел", "козла", "козлу", "козлом", "козлы", "козлов", "козёл", "козлик", "козлика")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.GOAT, null, "Козлёнок", isYoung = true), forms("козленок", "козленка", "козлята", "козлят", "козлёнок", "козлёнка")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.SHEEP, false), forms("овца", "овцы", "овцу", "овце", "овцой", "овец", "овцам", "овцами", "овечка", "овечку", "овечки")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.SHEEP, true), forms("баран", "барана", "барану", "бараном", "бараны", "баранов")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.SHEEP, null, "Ягнёнок", isYoung = true), forms("ягненок", "ягненка", "ягнята", "ягнят", "ягнёнок", "ягнёнка")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.PIG, false), forms("свинья", "свиньи", "свинью", "свинье", "свиньей", "свиней", "свиньям", "свиньями", "свиноматка", "свиноматку", "свиноматки", "свинка", "свинку")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.PIG, true), forms("хряк", "хряка", "хряку", "хряком", "хряки", "хряков", "боров", "борова")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.PIG, null, "Поросёнок", isYoung = true), forms("поросенок", "поросенка", "поросята", "поросят", "поросятам", "поросёнок", "поросёнка")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.RABBIT, null), forms("кролик", "кролика", "кролику", "кроликом", "кролики", "кроликов", "кроликам", "кроликами")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.RABBIT, false), forms("крольчиха", "крольчихи", "крольчиху", "крольчихе", "крольчих")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.RABBIT, null, "Крольчата", isYoung = true), forms("крольчонок", "крольчонка", "крольчата", "крольчат")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.HORSE, false), forms("лошадь", "лошади", "лошадей", "лошадью", "лошадям", "кобыла", "кобылу", "кобылы", "кобыле", "кобыл")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.HORSE, true), forms("конь", "коня", "коню", "конем", "кони", "коней", "жеребец", "жеребца", "жеребцу", "жеребцы", "жеребцов")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.HORSE, null, "Жеребёнок", isYoung = true), forms("жеребенок", "жеребенка", "жеребята", "жеребят", "жеребёнок")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.CHICKEN, false), forms("курица", "курицы", "курицу", "курице", "курицей", "куриц", "куры", "кур", "курам", "курами", "курочка", "курочку", "курочки", "курочек", "несушка", "несушку", "несушки", "несушек")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.CHICKEN, true), forms("петух", "петуха", "петуху", "петухом", "петухи", "петухов", "петушок", "петушка")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.CHICKEN, null, "Цыплята", isYoung = true), forms("цыпленок", "цыпленка", "цыплята", "цыплят", "цыплятам", "цыплёнок", "цыплёнка", "бройлер", "бройлера", "бройлеры", "бройлеров")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.TURKEY, true), forms("индюк", "индюка", "индюку", "индюком", "индюки", "индюков")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.TURKEY, false), forms("индейка", "индейки", "индейку", "индеек", "индюшка", "индюшку", "индюшки", "индюшек")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.TURKEY, null, "Индюшата", isYoung = true), forms("индюшонок", "индюшата", "индюшат")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.DUCK, false), forms("утка", "утки", "утку", "утке", "уткой", "уток", "уткам", "утками")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.DUCK, true), forms("селезень", "селезня", "селезню", "селезни", "селезней")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.DUCK, null, "Утята", isYoung = true), forms("утенок", "утенка", "утята", "утят", "утёнок", "утёнка")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.GOOSE, null), forms("гусь", "гуся", "гусю", "гусем", "гуси", "гусей", "гусям", "гусями")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.GOOSE, false), forms("гусыня", "гусыни", "гусыню", "гусынь")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.GOOSE, null, "Гусята", isYoung = true), forms("гусенок", "гусенка", "гусята", "гусят", "гусёнок")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.QUAIL, null), forms("перепел", "перепела", "перепелу", "перепелов", "перепелка", "перепелку", "перепелки", "перепелок", "перепёлка", "перепёлку", "перепёлки")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.QUAIL, null, "Перепелята", isYoung = true), forms("перепелята", "перепелят")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.PHEASANT, null), forms("фазан", "фазана", "фазану", "фазаны", "фазанов")),
        SpeciesForms(SpeciesMatch(AnimalSpecies.MUSCOVY_DUCK, null), forms("индоутка", "индоутки", "индоутку", "индоуток"))
    )

    /** Вид по слову («корову» → корова, самка). Слово уже нормализовано. */
    fun species(word: String): SpeciesMatch? {
        val w = normalizeRu(word)
        return SPECIES.firstOrNull { w in it.forms }?.match
    }

    fun isSpeciesWord(word: String): Boolean = species(word) != null

    // ----- Покупатели -----

    /** Грубое приведение фамилии/имени из дательного или родительного падежа: «иванову» → «Иванов», «марине» → «Марина». */
    fun buyerNominative(word: String): String {
        val w = normalizeRu(word)
        val nominative = when {
            w.endsWith("ову") || w.endsWith("еву") || w.endsWith("ину") || w.endsWith("ыну") -> w.dropLast(1)
            w.endsWith("ова") || w.endsWith("ева") || w.endsWith("ина") || w.endsWith("ына") -> w // женская фамилия или род. падеж — оставляем
            w.endsWith("ому") || w.endsWith("ему") -> w.dropLast(3) + if (w.endsWith("ому")) "ый" else "ий"
            w.endsWith("ского") -> w.dropLast(3) + "ий"
            w.endsWith("ко") || w.endsWith("енко") -> w
            w.endsWith("е") && w.length > 4 -> w.dropLast(1) + "а" // марине → марина
            w.endsWith("у") && w.length > 4 -> w.dropLast(1) + "а" // марину → марина
            w.endsWith("ю") && w.length > 4 -> w.dropLast(1) + "я" // наташю? редкое
            else -> w
        }
        return nominative.capitalizeRu()
    }

    /** Имя животного из винительного падежа: «зорьку» → «Зорька», «борьку» → «Борька», «машу» → «Маша». */
    fun animalNameNominative(word: String): String {
        val w = normalizeRu(word)
        val nominative = when {
            w.endsWith("ьку") || w.endsWith("шку") || w.endsWith("чку") || w.endsWith("нку") || w.endsWith("лку") -> w.dropLast(1) + "а"
            w.endsWith("у") && w.length > 3 -> w.dropLast(1) + "а"
            w.endsWith("ю") && w.length > 3 -> w.dropLast(1) + "я"
            else -> w
        }
        return nominative.capitalizeRu()
    }

    // ----- Категории расходов по ключевым словам -----

    private val CATEGORY_WORDS: Map<String, List<String>> = mapOf(
        CATEGORY_FEED to listOf("корм", "комбикорм", "зерн", "сен", "отруб", "кукуруз", "овес", "овс", "ячмен", "пшениц", "премикс", "жмых", "дроблен", "силос"),
        CATEGORY_VET to listOf("ветеринар", "ветврач", "вакцин", "лекарств", "витамин", "антибиотик", "привив", "лечен", "осмотр", "препарат"),
        CATEGORY_UTILITIES to listOf("электричеств", "электроэнерг", "свет", "вод", "газ", "интернет", "коммунал"),
        CATEGORY_FUEL to listOf("бензин", "дизел", "солярк", "дров", "угол", "топлив"),
        CATEGORY_TOOLS to listOf("инструмент", "инвентар", "ведр", "лопат", "вил", "поилк", "кормушк", "клетк", "опилк"),
        CATEGORY_REPAIR to listOf("ремонт", "доск", "гвозд", "цемент", "шифер", "профнастил", "сетк", "стройк", "строительн", "кирпич"),
        CATEGORY_TRANSPORT to listOf("доставк", "транспорт", "перевозк", "такси"),
        CATEGORY_SERVICES to listOf("аренд", "услуг", "налог", "страхов", "зарплат", "работник"),
        CATEGORY_PACKAGING to listOf("упаковк", "тар", "пакет", "бутылк", "банк", "этикетк"),
        CATEGORY_SEEDS to listOf("семен", "семян", "рассад", "саженц", "удобрен")
    )

    /** Категория расхода по словам фразы; null — не определена. */
    fun expenseCategory(words: List<String>): String? {
        for ((category, stems) in CATEGORY_WORDS) {
            if (words.any { w -> stems.any { matchesStem(w, it) } }) return category
        }
        return null
    }
}
