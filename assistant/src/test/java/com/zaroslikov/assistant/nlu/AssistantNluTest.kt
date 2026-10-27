package com.zaroslikov.assistant.nlu

import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.Suffix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class AssistantNluTest {

    private val today = LocalDate.of(2026, 10, 27)
    private val nlu = AssistantNlu()
    private val ctx = NluContext(
        productTitles = listOf(
            KnownProduct("Молоко", Suffix.LITERS),
            KnownProduct("Козье молоко", Suffix.LITERS),
            KnownProduct("Яйца", Suffix.PIECES),
            KnownProduct("Комбикорм", Suffix.KILOGRAM, isExpense = true)
        ),
        animals = listOf(KnownAnimal(1, "Зорька", "Корова", false), KnownAnimal(2, "Борька", "Бык", false)),
        buyers = listOf("Иванов", "Магазин у дома"),
        expenseCategories = listOf("Корма", "Ветеринария"),
        today = today
    )

    private fun parse(text: String, pending: ParsedCommand.Write? = null) = nlu.parse(text, ctx, pending)

    // ---------- Продукция ----------

    @Test
    fun `production with liters and lexicon title`() {
        val cmd = parse("добавь 10 литров молока") as ParsedCommand.AddProduction
        assertEquals("Молоко", cmd.title)
        assertEquals(10.0, cmd.count!!, 0.001)
        assertEquals(Suffix.LITERS, cmd.suffix)
        assertEquals(today, cmd.date)
        assertNull(cmd.missingSlot())
    }

    @Test
    fun `production with animal and yesterday`() {
        val cmd = parse("надоила вчера 12 л от зорьки") as ParsedCommand.AddProduction
        // Слова «молоко» нет, но глагол «надоила» и корова Зорька подразумевают молоко
        assertEquals("Молоко", cmd.title)
        assertEquals(1L, cmd.animal?.id)
        assertEquals(today.minusDays(1), cmd.date)
        assertEquals(Suffix.LITERS, cmd.suffix)
    }

    @Test
    fun `production from animal resolves known animal`() {
        val cmd = parse("надоила вчера 12 литров молока от зорьки") as ParsedCommand.AddProduction
        assertEquals("Молоко", cmd.title)
        assertEquals(1L, cmd.animal?.id)
        assertEquals(today.minusDays(1), cmd.date)
        assertEquals(12.0, cmd.count!!, 0.001)
    }

    @Test
    fun `production eggs with word numbers and tens`() {
        val cmd = parse("собрал два десятка яиц") as ParsedCommand.AddProduction
        assertEquals("Яйца", cmd.title)
        assertEquals(20.0, cmd.count!!, 0.001)
        assertEquals(Suffix.PIECES, cmd.suffix)
    }

    @Test
    fun `production without verb defaults to production`() {
        val cmd = parse("30 яиц") as ParsedCommand.AddProduction
        assertEquals("Яйца", cmd.title)
        assertEquals(30.0, cmd.count!!, 0.001)
    }

    @Test
    fun `production missing count asks`() {
        val cmd = parse("запиши молоко") as ParsedCommand.AddProduction
        assertEquals("Молоко", cmd.title)
        assertEquals(MissingSlot.COUNT, cmd.missingSlot())
    }

    @Test
    fun `slot fill completes pending production`() {
        val pending = parse("запиши молоко") as ParsedCommand.AddProduction
        val filled = parse("15 литров", pending) as ParsedCommand.AddProduction
        assertEquals("Молоко", filled.title)
        assertEquals(15.0, filled.count!!, 0.001)
        assertNull(filled.missingSlot())
    }

    @Test
    fun `unknown product is kept raw and capitalized`() {
        val cmd = parse("добавь 5 кг щавеля") as ParsedCommand.AddProduction
        assertEquals("Щавеля", cmd.title)
        assertEquals(false, cmd.titleResolved)
        assertEquals(Suffix.KILOGRAM, cmd.suffix)
    }

    @Test
    fun `multiword known title wins`() {
        val cmd = parse("добавь 3 литра козьего молока") as ParsedCommand.AddProduction
        assertEquals("Козье молоко", cmd.title)
    }

    @Test
    fun `date with month name`() {
        val cmd = parse("добавь 10 литров молока 15 октября") as ParsedCommand.AddProduction
        assertEquals(LocalDate.of(2026, 10, 15), cmd.date)
    }

    @Test
    fun `half liter`() {
        val cmd = parse("добавь пол-литра сливок") as ParsedCommand.AddProduction
        assertEquals(0.5, cmd.count!!, 0.001)
        assertEquals(Suffix.LITERS, cmd.suffix)
        assertEquals("Сливки", cmd.title)
    }

    // ---------- Продажа ----------

    @Test
    fun `sale with per unit price and buyer`() {
        val cmd = parse("продал 10 литров молока по 80 рублей иванову") as ParsedCommand.AddSale
        assertEquals("Молоко", cmd.title)
        assertEquals(10.0, cmd.count!!, 0.001)
        assertEquals(80.0, cmd.pricePerUnit!!, 0.001)
        assertNull(cmd.priceTotal)
        assertEquals("Иванов", cmd.buyer)
        assertNull(cmd.missingSlot())
    }

    @Test
    fun `sale with total price`() {
        val cmd = parse("продала 30 яиц за 300") as ParsedCommand.AddSale
        assertEquals("Яйца", cmd.title)
        assertEquals(30.0, cmd.count!!, 0.001)
        assertEquals(300.0, cmd.priceTotal!!, 0.001)
        assertNull(cmd.pricePerUnit)
    }

    @Test
    fun `sale price per unit via currency and unit`() {
        val cmd = parse("продал 5 литров молока 90 рублей за литр") as ParsedCommand.AddSale
        assertEquals(90.0, cmd.pricePerUnit!!, 0.001)
        assertEquals(5.0, cmd.count!!, 0.001)
    }

    @Test
    fun `sale without price asks`() {
        val cmd = parse("продал 10 литров молока") as ParsedCommand.AddSale
        assertEquals(MissingSlot.PRICE, cmd.missingSlot())
        val filled = parse("по 85", cmd) as ParsedCommand.AddSale
        assertEquals(85.0, filled.pricePerUnit!!, 0.001)
        assertNull(filled.missingSlot())
    }

    @Test
    fun `sale price fill with bare number is price not count`() {
        val cmd = parse("продал 10 литров молока") as ParsedCommand.AddSale
        val filled = parse("800", cmd) as ParsedCommand.AddSale
        assertEquals(10.0, filled.count!!, 0.001)
        assertEquals(800.0, filled.pricePerUnit!!, 0.001)
    }

    @Test
    fun `sale with two bare numbers`() {
        val cmd = parse("продал молоко 12 1200") as ParsedCommand.AddSale
        assertEquals(12.0, cmd.count!!, 0.001)
        assertEquals(1200.0, cmd.priceTotal!!, 0.001)
    }

    @Test
    fun `sale thousands with space`() {
        val cmd = parse("продал 2 кг меда за 1 500 рублей") as ParsedCommand.AddSale
        assertEquals("Мёд", cmd.title)
        assertEquals(1500.0, cmd.priceTotal!!, 0.001)
        assertEquals(2.0, cmd.count!!, 0.001)
    }

    @Test
    fun `sale known buyer fuzzy`() {
        val cmd = parse("продал 10 яиц по 10 рублей покупатель магазин") as ParsedCommand.AddSale
        assertEquals("Магазин у дома", cmd.buyer)
    }

    // ---------- Расходы ----------

    @Test
    fun `expense with quantity and total`() {
        val cmd = parse("купил комбикорм 40 кг за 1200 рублей") as ParsedCommand.AddExpense
        assertEquals("Комбикорм", cmd.title)
        assertEquals(40.0, cmd.count!!, 0.001)
        assertEquals(1200.0, cmd.priceTotal!!, 0.001)
        assertEquals("Корма", cmd.category)
        assertNull(cmd.missingSlot())
    }

    @Test
    fun `expense with only amount`() {
        val cmd = parse("потратил 500 на ветеринара") as ParsedCommand.AddExpense
        assertEquals("Ветеринар", cmd.title)
        assertEquals(500.0, cmd.priceTotal!!, 0.001)
        assertEquals(1.0, cmd.count!!, 0.001)
        assertEquals("Ветеринария", cmd.category)
    }

    @Test
    fun `expense electricity`() {
        val cmd = parse("оплатил электричество 3000") as ParsedCommand.AddExpense
        assertEquals("Электричество", cmd.title)
        assertEquals(3000.0, cmd.priceTotal!!, 0.001)
        assertEquals("Коммунальные услуги", cmd.category)
    }

    @Test
    fun `expense without price asks`() {
        val cmd = parse("купил 2 мешка комбикорма") as ParsedCommand.AddExpense
        assertEquals(MissingSlot.PRICE, cmd.missingSlot())
        val filled = parse("за 2400", cmd) as ParsedCommand.AddExpense
        assertEquals(2400.0, filled.priceTotal!!, 0.001)
    }

    @Test
    fun `expense product with price and no verb`() {
        val cmd = parse("комбикорм 40 кг 1200 рублей") as ParsedCommand.AddExpense
        assertEquals("Комбикорм", cmd.title)
        assertEquals(1200.0, cmd.priceTotal!!, 0.001)
    }

    // ---------- Животные ----------

    @Test
    fun `animal cow with name`() {
        val cmd = parse("добавь корову зорьку") as ParsedCommand.AddAnimal
        assertEquals(AnimalSpecies.COW, cmd.species)
        assertEquals("Корова", cmd.type)
        assertEquals("Зорька", cmd.name)
        assertEquals(false, cmd.sex)
        assertEquals(1, cmd.count)
        assertNull(cmd.missingSlot())
    }

    @Test
    fun `animal bull is male`() {
        val cmd = parse("новый бык борька родился 10 мая") as ParsedCommand.AddAnimal
        assertEquals("Бык", cmd.type)
        assertEquals(true, cmd.sex)
        assertEquals("Борька", cmd.name)
        assertEquals(LocalDate.of(2026, 5, 10), cmd.date)
    }

    @Test
    fun `animal group of chickens`() {
        val cmd = parse("добавь 20 кур") as ParsedCommand.AddAnimal
        assertEquals(AnimalSpecies.CHICKEN, cmd.species)
        assertEquals(20, cmd.count)
        assertTrue(cmd.isGroup)
        assertEquals("Курицы", cmd.type)
        assertNull(cmd.missingSlot())
    }

    @Test
    fun `animal purchase with price`() {
        val cmd = parse("купил корову за 50000") as ParsedCommand.AddAnimal
        assertEquals(AnimalSpecies.COW, cmd.species)
        assertEquals(50000.0, cmd.priceTotal!!, 0.001)
        assertEquals(MissingSlot.ANIMAL_NAME, cmd.missingSlot())
        val filled = parse("Марта", cmd) as ParsedCommand.AddAnimal
        assertEquals("Марта", filled.name)
    }

    @Test
    fun `feed for cows is expense not animal`() {
        val cmd = parse("купил корм для коров 40 кг за 1000")
        assertTrue(cmd is ParsedCommand.AddExpense)
    }

    // ---------- Аналитика ----------

    @Test
    fun `analysis production for month`() {
        val cmd = parse("сколько молока за месяц") as ParsedCommand.Analysis
        assertEquals(AnalysisKind.PRODUCTION, cmd.kind)
        assertEquals("Молоко", cmd.product)
        assertEquals(LocalDate.of(2026, 10, 1), cmd.period.begin)
        assertEquals(today, cmd.period.end)
        assertTrue(cmd.periodExplicit)
    }

    @Test
    fun `analysis sales of product`() {
        val cmd = parse("сколько молока продал за неделю") as ParsedCommand.Analysis
        assertEquals(AnalysisKind.SALES, cmd.kind)
        assertEquals("Молоко", cmd.product)
        assertEquals(7, cmd.period.days)
    }

    @Test
    fun `analysis revenue previous month`() {
        val cmd = parse("выручка за прошлый месяц") as ParsedCommand.Analysis
        assertEquals(AnalysisKind.FINANCE, cmd.kind)
        assertEquals(LocalDate.of(2026, 9, 1), cmd.period.begin)
        assertEquals(LocalDate.of(2026, 9, 30), cmd.period.end)
    }

    @Test
    fun `analysis stock`() {
        val cmd = parse("сколько яиц на складе") as ParsedCommand.Analysis
        assertEquals(AnalysisKind.STOCK, cmd.kind)
        assertEquals("Яйца", cmd.product)
    }

    @Test
    fun `analysis animals by species`() {
        val cmd = parse("сколько у меня коров") as ParsedCommand.Analysis
        assertEquals(AnalysisKind.ANIMALS, cmd.kind)
        assertEquals(AnimalSpecies.COW, cmd.species)
    }

    @Test
    fun `analysis summary for product`() {
        val cmd = parse("анализ по молоку за год") as ParsedCommand.Analysis
        assertEquals(AnalysisKind.PRODUCT_SUMMARY, cmd.kind)
        assertEquals("Молоко", cmd.product)
        assertEquals(LocalDate.of(2026, 1, 1), cmd.period.begin)
    }

    @Test
    fun `analysis expenses all time`() {
        val cmd = parse("расходы за всё время") as ParsedCommand.Analysis
        assertEquals(AnalysisKind.EXPENSES, cmd.kind)
        assertTrue(cmd.period.isAllTime)
    }

    @Test
    fun `analysis for named month`() {
        val cmd = parse("сколько яиц собрали в сентябре") as ParsedCommand.Analysis
        assertEquals(AnalysisKind.PRODUCTION, cmd.kind)
        assertEquals(LocalDate.of(2026, 9, 1), cmd.period.begin)
        assertEquals(LocalDate.of(2026, 9, 30), cmd.period.end)
    }

    @Test
    fun `analysis default period is current month and not explicit`() {
        val cmd = parse("сколько молока") as ParsedCommand.Analysis
        assertEquals(false, cmd.periodExplicit)
        assertEquals(LocalDate.of(2026, 10, 1), cmd.period.begin)
    }

    // ---------- Служебные ----------

    @Test
    fun `confirm and cancel`() {
        assertEquals(ParsedCommand.Confirm, parse("да"))
        assertEquals(ParsedCommand.Confirm, parse("да, всё верно"))
        assertEquals(ParsedCommand.Cancel, parse("нет"))
        assertEquals(ParsedCommand.Cancel, parse("отмена"))
        assertEquals(ParsedCommand.Cancel, parse("не надо"))
    }

    @Test
    fun `help`() {
        assertEquals(ParsedCommand.Help, parse("что ты умеешь"))
        assertEquals(ParsedCommand.Help, parse("привет"))
    }

    @Test
    fun `unknown`() {
        assertTrue(parse("какая сегодня погода") is ParsedCommand.Analysis || parse("бла бла") is ParsedCommand.Unknown)
        assertTrue(parse("бла бла") is ParsedCommand.Unknown)
    }

    // ---------- Правки по ревью ----------

    @Test
    fun `selling or losing an animal is unsupported, not a new animal`() {
        assertTrue(parse("продал корову за 50000") is ParsedCommand.Unsupported)
        assertTrue(parse("умерла корова зорька") is ParsedCommand.Unsupported)
    }

    @Test
    fun `animal purchase price without preposition`() {
        val group = parse("купил 20 кур 5000") as ParsedCommand.AddAnimal
        assertEquals(20, group.count)
        assertEquals(5000.0, group.priceTotal!!, 0.001)
        val single = parse("купил корову 50000") as ParsedCommand.AddAnimal
        assertEquals(1, single.count)
        assertEquals(50000.0, single.priceTotal!!, 0.001)
    }

    @Test
    fun `sale inherits product origin of a known product`() {
        val known = parse("продал 10 литров молока по 80") as ParsedCommand.AddSale
        assertEquals(com.zaroslikov.domain.models.enums.ProductOrigin.ADD, known.productOrigin)
        val unknown = parse("продал 2 кг щавеля по 100") as ParsedCommand.AddSale
        assertNull(unknown.productOrigin)
    }

    @Test
    fun `money received for a product is a sale not production`() {
        val cmd = parse("получил 500 рублей за молоко") as ParsedCommand.AddSale
        assertEquals("Молоко", cmd.title)
        assertEquals(500.0, cmd.priceTotal!!, 0.001)
        assertEquals(MissingSlot.COUNT, cmd.missingSlot())
    }

    @Test
    fun `explicit year and total do not override an explicit month`() {
        val year = parse("выручка за 2025 год") as ParsedCommand.Analysis
        assertEquals(LocalDate.of(2025, 1, 1), year.period.begin)
        assertEquals(LocalDate.of(2025, 12, 31), year.period.end)
        val month = parse("сколько всего молока за месяц") as ParsedCommand.Analysis
        assertEquals(LocalDate.of(2026, 10, 1), month.period.begin)
        assertEquals(false, month.period.isAllTime)
    }

    @Test
    fun `thousands merge only in money context`() {
        assertEquals(listOf(3.0, 400.0), tokenizeRu("продал 3 400").mapNotNull { it.number })
        assertEquals(listOf(1500.0), tokenizeRu("за 1 500 рублей").mapNotNull { it.number })
    }

    @Test
    fun `weak words alone do not confirm`() {
        assertTrue(parse("так") !is ParsedCommand.Confirm)
        assertEquals(ParsedCommand.Confirm, parse("так точно"))
    }

    // ---------- Низкоуровневые ----------

    @Test
    fun `tokenizer splits glued units and parses word numbers`() {
        val tokens = tokenizeRu("Продал 10л молока за двадцать пять рублей")
        assertEquals(listOf("продал", "10", "л", "молока", "за", "двадцать пять", "рублей"), tokens.map { it.text })
        assertEquals(25.0, tokens[5].number!!, 0.001)
    }

    @Test
    fun `stemmer unifies forms`() {
        assertEquals(RuStemmer.stem("молоко"), RuStemmer.stem("молока"))
        assertEquals(RuStemmer.stem("молоком"), RuStemmer.stem("молоку"))
        assertEquals(RuStemmer.stem("коровы"), RuStemmer.stem("корову"))
        assertNotNull(RuStemmer.stem("яйца"))
    }

    @Test
    fun `entity resolver fuzzy`() {
        assertTrue(EntityResolver.wordScore("зорьки", "Зорька") >= 0.9)
        assertTrue(EntityResolver.wordScore("иванову", "Иванов") >= 0.75)
        assertEquals(0.0, EntityResolver.wordScore("молоко", "яйца"), 0.001)
    }

    @Test
    fun `numbers words`() {
        assertEquals(1500.0, tokenizeRu("полторы тысячи")[0].number!!, 0.001)
        assertEquals(2300.0, tokenizeRu("две тысячи триста")[0].number!!, 0.001)
        assertEquals(2.5, tokenizeRu("2,5")[0].number!!, 0.001)
        assertEquals(2.5, tokenizeRu("два с половиной")[0].number!!, 0.001)
    }

    @Test
    fun `dates`() {
        val d = RuDates.extractDate(tokenizeRu("3 дня назад"), today)!!
        assertEquals(today.minusDays(3), d.value)
        val iso = RuDates.extractDate(tokenizeRu("15.10.2026"), today)!!
        assertEquals(LocalDate.of(2026, 10, 15), iso.value)
        val range = RuDates.extractPeriod(tokenizeRu("с 1 по 15 октября"), today)!!
        assertEquals(LocalDate.of(2026, 10, 1), range.value.begin)
        assertEquals(LocalDate.of(2026, 10, 15), range.value.end)
    }
}
