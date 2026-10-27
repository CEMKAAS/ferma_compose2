package com.zaroslikov.assistant.engine

import com.zaroslikov.assistant.model.AnalysisCard
import com.zaroslikov.assistant.model.CardRow
import com.zaroslikov.assistant.nlu.AnalysisKind
import com.zaroslikov.assistant.nlu.ParsedCommand
import com.zaroslikov.assistant.nlu.RuDates
import com.zaroslikov.assistant.nlu.Units
import com.zaroslikov.assistant.util.assistantFormat
import com.zaroslikov.assistant.util.moneyLabel
import com.zaroslikov.assistant.util.plural
import com.zaroslikov.assistant.util.quantityLabel
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import java.time.LocalDate
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.FinanceRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** Ответ аналитики: текст + карточка. */
data class AnalysisReply(val text: String, val card: AnalysisCard?)

/** Отвечает на вопросы «сколько…» запросами к domain-репозиториям. */
class AnalyticsService @Inject constructor(
    private val addRepository: AddRepository,
    private val saleRepository: SaleRepository,
    private val expensesRepository: ExpensesRepository,
    private val writeOffRepository: WriteOffRepository,
    private val financeRepository: FinanceRepository,
    private val warehouseRepository: WarehouseRepository,
    private val animalRepository: AnimalRepository
) {

    suspend fun answer(q: ParsedCommand.Analysis, ctx: ProjectContext): AnalysisReply = when (q.kind) {
        AnalysisKind.PRODUCTION -> production(q, ctx)
        AnalysisKind.SALES -> sales(q, ctx)
        AnalysisKind.EXPENSES -> expenses(q, ctx)
        AnalysisKind.FINANCE -> finance(q, ctx)
        AnalysisKind.STOCK -> stock(q, ctx)
        AnalysisKind.ANIMALS -> animals(q, ctx)
        AnalysisKind.PRODUCT_SUMMARY -> summary(q, ctx)
    }

    // ---------- Продукция ----------

    private suspend fun production(q: ParsedCommand.Analysis, ctx: ProjectContext): AnalysisReply {
        val period = q.period
        val begin = RuDates.iso(period.begin)
        val end = RuDates.iso(period.end)
        if (q.product == null) {
            // По всем продуктам: суммируем диапазоны по каждому названию
            val titles = addRepository.getBrieflyItemAdd(ctx.idPT).firstOr(emptyList())
            val rows = titles.mapNotNull { brief ->
                val list = addRepository.getAnalysisAddRangeList(ctx.idPT, brief.title, begin, end).firstOr(emptyList())
                if (list.isEmpty()) null else CardRow(brief.title, quantityLabel(list.sumOf { it.count }, list.first().suffix))
            }
            if (rows.isEmpty()) return AnalysisReply("${period.label.capitalizeFirst()} записей о продукции нет.", null)
            return AnalysisReply(
                "Вот что получено ${period.label}:",
                AnalysisCard(title = "Продукция", subtitle = period.label.capitalizeFirst(), rows = rows)
            )
        }
        val list = addRepository.getAnalysisAddRangeList(ctx.idPT, q.product, begin, end).firstOr(emptyList())
        if (list.isEmpty()) {
            val allTime = if (period.isAllTime) null else addRepository.getAnalysisAddAllTime(ctx.idPT, q.product).firstOr(null)
            val hint = if (allTime != null && allTime.count > 0) " За всё время — ${quantityLabel(allTime.count, allTime.suffix)}." else ""
            return AnalysisReply("${period.label.capitalizeFirst()} записей по «${q.product}» нет.$hint", null)
        }
        val suffix = list.first().suffix
        val total = list.sumOf { it.count }
        val perDay = total / period.days.coerceAtLeast(1)
        val rows = mutableListOf(
            CardRow("Получено", quantityLabel(total, suffix)),
            CardRow("Записей", list.size.toString())
        )
        if (!period.isAllTime && period.days > 1) rows += CardRow("В среднем в день", quantityLabel(perDay, suffix))
        if (list.size > 1) rows += CardRow("Максимум за раз", quantityLabel(list.maxOf { it.count }, suffix))
        val periodNote = if (q.periodExplicit) "" else " (период не указан — взял текущий месяц)"
        return AnalysisReply(
            "${q.product}: ${period.label} получено ${quantityLabel(total, suffix)}$periodNote.",
            AnalysisCard(title = q.product, subtitle = "Продукция ${period.label}", rows = rows)
        )
    }

    // ---------- Продажи ----------

    private suspend fun sales(q: ParsedCommand.Analysis, ctx: ProjectContext): AnalysisReply {
        val period = q.period
        val begin = RuDates.iso(period.begin)
        val end = RuDates.iso(period.end)
        if (q.product == null) {
            val income = saleRepository.getIncomeMount(ctx.idPT, begin, end).firstOr(0.0)
            val byProduct = saleRepository.getProductListCategoryIncomeCurrentMonth(ctx.idPT, begin, end).firstOr(emptyList())
            if (income == 0.0 && byProduct.isEmpty()) return AnalysisReply("${period.label.capitalizeFirst()} продаж не было.", null)
            return AnalysisReply(
                "Выручка ${period.label} — ${moneyLabel(income, ctx.currency)}.",
                AnalysisCard(
                    title = "Продажи",
                    subtitle = period.label.capitalizeFirst(),
                    rows = listOf(CardRow("Выручка", moneyLabel(income, ctx.currency))),
                    listTitle = "По продуктам",
                    list = byProduct.sortedByDescending { it.price }.take(8).map { CardRow(it.title, moneyLabel(it.price, ctx.currency)) }
                )
            )
        }
        val list = saleRepository.getAnalysisSaleRangeList(ctx.idPT, q.product, begin, end).firstOr(emptyList())
        if (list.isEmpty()) return AnalysisReply("${period.label.capitalizeFirst()} продаж «${q.product}» не было.", null)
        val suffix = list.first().suffix
        val count = list.sumOf { it.count }
        val income = list.sumOf { it.price }
        val buyers = saleRepository.getAnalysisSaleBuyerRangeList(ctx.idPT, q.product, begin, end).firstOr(emptyList())
            .groupBy { it.buyer?.trim()?.ifEmpty { null } ?: "Без покупателя" }
            .map { (buyer, rows) -> buyer to rows.sumOf { it.price } }
            .sortedByDescending { it.second }
            .map { (buyer, sum) -> CardRow(buyer, moneyLabel(sum, ctx.currency)) }
        val rows = mutableListOf(
            CardRow("Продано", quantityLabel(count, suffix)),
            CardRow("Выручка", moneyLabel(income, ctx.currency)),
            CardRow("Продаж", list.size.toString())
        )
        if (count > 0) rows += CardRow("Средняя цена", "${moneyLabel(income / count, ctx.currency)} за ${Units.label(suffix)}")
        return AnalysisReply(
            "${q.product}: ${period.label} продано ${quantityLabel(count, suffix)} на ${moneyLabel(income, ctx.currency)}.",
            AnalysisCard(
                title = q.product,
                subtitle = "Продажи ${period.label}",
                rows = rows,
                listTitle = if (buyers.size > 1 || buyers.firstOrNull()?.label != "Без покупателя") "Покупатели" else null,
                list = if (buyers.size > 1 || buyers.firstOrNull()?.label != "Без покупателя") buyers.take(6) else emptyList()
            )
        )
    }

    // ---------- Расходы ----------

    private suspend fun expenses(q: ParsedCommand.Analysis, ctx: ProjectContext): AnalysisReply {
        val period = q.period
        val begin = RuDates.iso(period.begin)
        val end = RuDates.iso(period.end)
        val total = expensesRepository.getExpensesMount(ctx.idPT, begin, end).firstOr(0.0)
        val byCategory = expensesRepository.getCategoryExpensesCurrentMonth(ctx.idPT, begin, end).firstOr(emptyList())
        val byProduct = expensesRepository.getProductLisCategoryExpensesCurrentMonth(ctx.idPT, begin, end).firstOr(emptyList())
        if (total == 0.0) return AnalysisReply("${period.label.capitalizeFirst()} расходов не было.", null)
        val productRows = if (q.product != null) byProduct.filter { it.title.equals(q.product, ignoreCase = true) } else byProduct
        val list = if (q.product == null && byCategory.isNotEmpty())
            byCategory.sortedByDescending { it.price }.take(8).map { CardRow(it.category?.ifBlank { null } ?: "Без категории", moneyLabel(it.price, ctx.currency)) }
        else productRows.sortedByDescending { it.price }.take(8).map { CardRow(it.title, moneyLabel(it.price, ctx.currency)) }
        val rows = mutableListOf(CardRow("Расходы", moneyLabel(total, ctx.currency)))
        if (q.product != null) {
            val productTotal = productRows.sumOf { it.price }
            rows.add(0, CardRow(q.product, moneyLabel(productTotal, ctx.currency)))
        }
        return AnalysisReply(
            if (q.product != null) "На «${q.product}» ${period.label} потрачено ${moneyLabel(productRows.sumOf { it.price }, ctx.currency)}; все расходы — ${moneyLabel(total, ctx.currency)}."
            else "Расходы ${period.label} — ${moneyLabel(total, ctx.currency)}.",
            AnalysisCard(
                title = "Расходы",
                subtitle = period.label.capitalizeFirst(),
                rows = rows,
                listTitle = if (list.isNotEmpty()) (if (q.product == null && byCategory.isNotEmpty()) "По категориям" else "По статьям") else null,
                list = list
            )
        )
    }

    // ---------- Финансы ----------

    private suspend fun finance(q: ParsedCommand.Analysis, ctx: ProjectContext): AnalysisReply {
        val period = q.period
        val begin = RuDates.iso(period.begin)
        val end = RuDates.iso(period.end)
        val income = saleRepository.getIncomeMount(ctx.idPT, begin, end).firstOr(0.0)
        val expenses = expensesRepository.getExpensesMount(ctx.idPT, begin, end).firstOr(0.0)
        val ownNeed = writeOffRepository.getOwnNeedMonth(ctx.idPT, begin, end).firstOr(0.0)
        val scrap = writeOffRepository.getScrapMonth(ctx.idPT, begin, end).firstOr(0.0)
        val balance = financeRepository.getCurrentBalance(ctx.idPT).firstOr(0.0)
        val profit = income - expenses
        val rows = mutableListOf(
            CardRow("Выручка", moneyLabel(income, ctx.currency)),
            CardRow("Расходы", moneyLabel(expenses, ctx.currency)),
            CardRow(if (profit >= 0) "Прибыль" else "Убыток", moneyLabel(kotlin.math.abs(profit), ctx.currency))
        )
        if (ownNeed > 0) rows += CardRow("Собственные нужды", moneyLabel(ownNeed, ctx.currency))
        if (scrap > 0) rows += CardRow("Утилизация", moneyLabel(scrap, ctx.currency))
        rows += CardRow("Баланс за всё время", moneyLabel(balance, ctx.currency))
        val text = if (income == 0.0 && expenses == 0.0) "${period.label.capitalizeFirst()} движений денег не было. Баланс — ${moneyLabel(balance, ctx.currency)}."
        else "${period.label.capitalizeFirst()}: выручка ${moneyLabel(income, ctx.currency)}, расходы ${moneyLabel(expenses, ctx.currency)}, " +
                (if (profit >= 0) "прибыль " else "убыток ") + moneyLabel(kotlin.math.abs(profit), ctx.currency) + "."
        return AnalysisReply(text, AnalysisCard(title = "Финансы", subtitle = period.label.capitalizeFirst(), rows = rows))
    }

    // ---------- Склад ----------

    private suspend fun stock(q: ParsedCommand.Analysis, ctx: ProjectContext): AnalysisReply {
        val balance = warehouseRepository.getCurrentBalanceWarehouse(ctx.idPT).firstOr(emptyList())
        if (q.product != null) {
            val rows = balance.filter { it.title.equals(q.product, ignoreCase = true) }
            if (rows.isEmpty()) return AnalysisReply("«${q.product}» на складе сейчас нет.", null)
            val text = rows.joinToString(", ") { quantityLabel(it.count, it.suffix) }
            return AnalysisReply(
                "На складе ${q.product}: $text.",
                AnalysisCard(title = q.product, subtitle = "Остаток на складе", rows = rows.map { CardRow("Остаток", quantityLabel(it.count, it.suffix)) })
            )
        }
        val positive = balance.filter { it.count > 0 }
        if (positive.isEmpty()) return AnalysisReply("Склад пуст: остатков продукции нет.", null)
        return AnalysisReply(
            "На складе ${positive.size} ${plural(positive.size.toLong(), "позиция", "позиции", "позиций")}:",
            AnalysisCard(title = "Склад", subtitle = "Текущие остатки", rows = positive.sortedByDescending { it.count }.take(12).map { CardRow(it.title, quantityLabel(it.count, it.suffix)) })
        )
    }

    // ---------- Животные ----------

    private suspend fun animals(q: ParsedCommand.Analysis, ctx: ProjectContext): AnalysisReply {
        val all = animalRepository.getAllAnimalWithGroups(ctx.idPT).firstOr(emptyList()).filter { !it.isArchive }
        if (all.isEmpty()) return AnalysisReply("В хозяйстве пока нет животных.", null)
        val filtered = if (q.species != null) all.filter { AnimalSpecies.resolve(it.type) == q.species } else all
        if (filtered.isEmpty()) return AnalysisReply("Животных вида «${q.species?.commonName}» в хозяйстве нет.", null)
        val heads = filtered.sumOf { if (it.group) it.totalCount else maxOf(it.totalCount, 1.0) }
        val byType = filtered.groupBy { it.type }
            .map { (type, list) -> type to list.sumOf { if (it.group) it.totalCount else maxOf(it.totalCount, 1.0) } }
            .sortedByDescending { it.second }
            .map { (type, heads) -> CardRow(type, heads.assistantFormat() + " гол") }
        val headsLabel = "${heads.assistantFormat()} ${plural(heads.toLong(), "голова", "головы", "голов")}"
        val text = if (q.species != null) "${q.species.groupName}: $headsLabel (${filtered.size} ${plural(filtered.size.toLong(), "запись", "записи", "записей")})."
        else "Всего в хозяйстве $headsLabel, ${filtered.size} ${plural(filtered.size.toLong(), "животное и группа", "животных и группы", "животных и групп")}."
        return AnalysisReply(text, AnalysisCard(title = "Поголовье", subtitle = q.species?.groupName ?: "Все животные", rows = byType))
    }

    // ---------- Сводка по продукту ----------

    private suspend fun summary(q: ParsedCommand.Analysis, ctx: ProjectContext): AnalysisReply {
        val product = requireNotNull(q.product)
        val period = q.period
        val begin = RuDates.iso(period.begin)
        val end = RuDates.iso(period.end)
        val produced = addRepository.getAnalysisAddRangeList(ctx.idPT, product, begin, end).firstOr(emptyList())
        val sold = saleRepository.getAnalysisSaleRangeList(ctx.idPT, product, begin, end).firstOr(emptyList())
        val stock = warehouseRepository.getCurrentBalanceWarehouse(ctx.idPT).firstOr(emptyList()).filter { it.title.equals(product, ignoreCase = true) }
        // Списания за период: собственные нужды и утилизация (строки фильтруем по дате в Kotlin)
        val inPeriod = { w: DomainWriteOffTable ->
            runCatching { LocalDate.of(w.year, w.month, w.day) }.getOrNull()
                ?.let { !it.isBefore(period.begin) && !it.isAfter(period.end) } == true
        }
        val ownNeed = writeOffRepository.getBrieflyDetailsItemWriteOffOwnNeed(ctx.idPT, product).firstOr(emptyList()).filter(inPeriod)
        val scrap = writeOffRepository.getBrieflyDetailsItemWriteOffScrap(ctx.idPT, product).firstOr(emptyList()).filter(inPeriod)
        val suffix = produced.firstOrNull()?.suffix ?: sold.firstOrNull()?.suffix ?: stock.firstOrNull()?.suffix ?: Suffix.PIECES
        if (produced.isEmpty() && sold.isEmpty() && stock.isEmpty()) {
            return AnalysisReply("${period.label.capitalizeFirst()} по «$product» нет ни производства, ни продаж.", null)
        }
        val producedTotal = produced.sumOf { it.count }
        val soldCount = sold.sumOf { it.count }
        val income = sold.sumOf { it.price }
        val rows = mutableListOf<CardRow>()
        rows += CardRow("Получено", quantityLabel(producedTotal, suffix))
        rows += CardRow("Продано", quantityLabel(soldCount, sold.firstOrNull()?.suffix ?: suffix))
        rows += CardRow("Выручка", moneyLabel(income, ctx.currency))
        if (soldCount > 0) rows += CardRow("Средняя цена", "${moneyLabel(income / soldCount, ctx.currency)} за ${Units.label(sold.first().suffix)}")
        if (producedTotal > 0) rows += CardRow("Продано от полученного", "${(soldCount / producedTotal * 100).assistantFormat()} %")
        stock.forEach { rows += CardRow("Остаток на складе", quantityLabel(it.count, it.suffix)) }
        if (ownNeed.isNotEmpty()) rows += CardRow("На собственные нужды", quantityLabel(ownNeed.sumOf { it.count }, ownNeed.first().countSuffix))
        if (scrap.isNotEmpty()) rows += CardRow("Утилизировано", quantityLabel(scrap.sumOf { it.count }, scrap.first().countSuffix))
        return AnalysisReply(
            "$product ${period.label}: получено ${quantityLabel(producedTotal, suffix)}, продано ${quantityLabel(soldCount, suffix)} на ${moneyLabel(income, ctx.currency)}.",
            AnalysisCard(title = product, subtitle = "Сводка ${period.label}", rows = rows)
        )
    }

    private suspend fun <T> Flow<T>.firstOr(default: T): T =
        try {
            catch { emit(default) }.first()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            default
        }
}

private fun String.capitalizeFirst(): String = if (isEmpty()) this else substring(0, 1).uppercase() + substring(1)
