package com.zaroslikov.fermacompose2.ui.project.finance.traceBoard

import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.trace.DomainProductTrace
import com.zaroslikov.domain.models.dto.trace.DomainTraceProduction
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainRecycling
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.conversation22
import com.zaroslikov.fermacompose2.supportFun.conversation4
import com.zaroslikov.fermacompose2.supportFun.dateBuilder
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.utils.ResourceProvider

/**
 * Строит граф доски продукции из всех операций продукта.
 *
 * Итоги (произведено, остаток, суммы хабов) считаются в базовой единице [Suffix]:
 * учитываются только строки, чья единица приводится к ней (как в анализе продукта);
 * остальные показываются на своих карточках как есть.
 */
class TraceGraphBuilder(private val resourceProvider: ResourceProvider) {

    fun build(
        trace: DomainProductTrace,
        idPT: Long,
        title: String,
        baseSuffix: Suffix,
        settings: DomainSettings
    ): TraceGraph {
        val units = Units(baseSuffix, settings)
        val nodes = mutableListOf<TraceNode>()
        val edges = mutableListOf<TraceEdge>()
        val children = mutableMapOf<String, List<String>>()

        // ---------- Источники ----------
        val recyclingAddIds = trace.recyclingProducingProduct.mapNotNull { it.addId }.toSet()

        trace.production
            .mapNotNull { row -> row.animalId?.let { it to row } }
            .groupBy({ it.first }, { it.second })
            .forEach { (animalId, rows) ->
                val node = animalSource(animalId, rows, units)
                nodes += node
                edges += TraceEdge(node.id, PRODUCT_ID, TraceCategory.PRODUCTION)
            }

        val noAnimal = trace.production.filter { it.animalId == null && it.id !in recyclingAddIds }
        if (noAnimal.isNotEmpty()) {
            val node = noAnimalSource(noAnimal, units)
            nodes += node
            edges += TraceEdge(node.id, PRODUCT_ID, TraceCategory.PRODUCTION)
        }

        trace.recyclingProducingProduct.sortedByDescending { dateKey(it.day, it.month, it.year) }
            .forEach { recycling ->
                val sourceId = "src:rec:${recycling.id}"
                nodes += recyclingSource(sourceId, recycling)
                edges += TraceEdge(sourceId, PRODUCT_ID, TraceCategory.RECYCLING)

                val ingredients = trace.producingIngredients.filter { it.recyclingId == recycling.id }
                val ingredientIds = ingredients.map { ingredient ->
                    val node = ingredientNode(ingredient, idPT, units)
                    nodes += node
                    edges += TraceEdge(node.id, sourceId, TraceCategory.INGREDIENT)
                    node.id
                }
                if (ingredientIds.isNotEmpty()) children[sourceId] = ingredientIds
            }

        // ---------- Хабы и операции ----------
        addSaleHub(trace.sales, units, nodes, edges, children)

        val plainWriteOffs = trace.writeOffs.filter { it.recyclingId == null }
        addWriteOffHub(
            hubId = "hub:own",
            opPrefix = "op:own:",
            category = TraceCategory.OWN_NEEDS,
            hubTitle = getString(R.string.trace_board_hub_own_needs),
            rows = plainWriteOffs.filter { it.status },
            units = units, nodes = nodes, edges = edges, children = children
        )
        addWriteOffHub(
            hubId = "hub:disposal",
            opPrefix = "op:disposal:",
            category = TraceCategory.DISPOSAL,
            hubTitle = getString(R.string.trace_board_hub_disposal),
            rows = plainWriteOffs.filter { !it.status },
            units = units, nodes = nodes, edges = edges, children = children
        )
        addRecyclingHub(trace, idPT, units, nodes, edges, children)

        // ---------- Итоги и продукт ----------
        val produced = units.total(trace.production.map { it.count to it.countSuffix })
        val sold = units.total(trace.sales.map { it.count to it.countSuffix })
        val ownNeeds = units.total(plainWriteOffs.filter { it.status }.map { it.count to it.countSuffix })
        val disposal = units.total(plainWriteOffs.filter { !it.status }.map { it.count to it.countSuffix })
        val recycled = units.total(
            trace.writeOffs.filter { it.recyclingId != null }.map { it.count to it.countSuffix }
        )
        val stock = produced - sold - ownNeeds - disposal - recycled
        val summary = listOf(
            // Порядок сводки: остаток, затем откуда пришло и куда ушло
            TraceSummaryItem(TraceCategory.STOCK, units.amount(stock)),
            TraceSummaryItem(TraceCategory.PRODUCTION, units.amount(produced)),
            TraceSummaryItem(TraceCategory.SALE, units.amount(sold)),
            TraceSummaryItem(TraceCategory.OWN_NEEDS, units.amount(ownNeeds)),
            TraceSummaryItem(TraceCategory.DISPOSAL, units.amount(disposal)),
            TraceSummaryItem(TraceCategory.RECYCLING, units.amount(recycled))
        )
        val product = TraceNode(
            id = PRODUCT_ID,
            kind = TraceNodeKind.PRODUCT,
            category = TraceCategory.PRODUCT,
            title = title,
            value = format(R.string.trace_board_produced_s, units.amount(produced)),
            subtitle = format(R.string.trace_board_stock_s, units.amount(stock)),
            details = listOf(
                TraceDetail(getString(R.string.trace_board_legend_production), units.amount(produced)),
                TraceDetail(getString(R.string.analysis_screen_in_stock), units.amount(stock))
            )
        )

        return TraceGraph(
            product = product,
            nodes = nodes,
            edges = edges,
            children = children,
            summary = summary
        )
    }

    // ================= Источники =================

    private fun animalSource(animalId: Long, rows: List<DomainTraceProduction>, units: Units): TraceNode {
        val first = rows.first()
        // Тип · порода (если указана) · «группа»
        val subtitle = listOfNotNull(
            first.animalType,
            first.animalBreed,
            if (first.animalIsGroup == true) getString(R.string.trace_board_source_group) else null
        ).filter { it.isNotBlank() }.joinToString(" · ")
        val total = units.mixedAmount(rows.map { it.count to it.countSuffix })
        val records = plural(R.plurals.trace_board_records, rows.size)
        return TraceNode(
            id = "src:animal:$animalId",
            kind = TraceNodeKind.SOURCE,
            category = TraceCategory.PRODUCTION,
            // animal_id есть, а животного нет — его удалили (LEFT JOIN вернул null)
            title = first.animalName ?: getString(R.string.trace_board_source_animal_deleted),
            subtitle = subtitle.ifBlank { null },
            value = total,
            caption = records,
            icon = first.animalIcon ?: AppIcon.PETS,
            imagePath = first.animalImagePath,
            animalKind = animalKindOf(first),
            details = productionDetails(rows, total, records, first.animalName)
        )
    }

    /**
     * Цвет значка как в списке животных: самец — синий, самка — розовый, обезличенная группа —
     * бирюзовый; животное из сборной группы — оранжевый; удалённое — серый.
     */
    private fun animalKindOf(row: DomainTraceProduction): TraceAnimalKind = when {
        row.animalName == null -> TraceAnimalKind.UNKNOWN
        row.animalHerdId != null -> TraceAnimalKind.HERD
        row.animalIsGroup == true -> TraceAnimalKind.GROUP
        row.animalSex == true -> TraceAnimalKind.MALE
        row.animalSex == false -> TraceAnimalKind.FEMALE
        else -> TraceAnimalKind.UNKNOWN
    }

    private fun noAnimalSource(rows: List<DomainTraceProduction>, units: Units): TraceNode {
        val total = units.mixedAmount(rows.map { it.count to it.countSuffix })
        val records = plural(R.plurals.trace_board_records, rows.size)
        return TraceNode(
            id = "src:none",
            kind = TraceNodeKind.SOURCE,
            category = TraceCategory.PRODUCTION,
            title = getString(R.string.trace_board_source_no_animal),
            value = total,
            caption = records,
            icon = AppIcon.PETS,
            animalKind = TraceAnimalKind.UNKNOWN,
            details = productionDetails(rows, total, records, null)
        )
    }

    /** Итог и последние записи производства источника. */
    private fun productionDetails(
        rows: List<DomainTraceProduction>,
        total: String,
        records: String,
        animalName: String?
    ): List<TraceDetail> = buildList {
        animalName?.let { add(TraceDetail(getString(R.string.trace_board_detail_animal), it)) }
        add(TraceDetail(getString(R.string.trace_board_detail_count), total))
        add(TraceDetail(getString(R.string.trace_board_legend_production), records))
        rows.sortedByDescending { dateKey(it.day, it.month, it.year) }.take(MAX_DETAIL_ROWS).forEach {
            add(
                TraceDetail(
                    dateBuilder(it.day, it.month, it.year),
                    amountOf(it.count, it.countSuffix) + it.note.takeIf { n -> n.isNotBlank() }
                        ?.let { n -> " · $n" }.orEmpty()
                )
            )
        }
    }

    private fun recyclingSource(id: String, recycling: DomainRecycling): TraceNode {
        val status = readyStatus(recycling)
        val date = dateBuilder(recycling.day, recycling.month, recycling.year)
        return TraceNode(
            id = id,
            kind = TraceNodeKind.SOURCE,
            category = TraceCategory.RECYCLING,
            title = getString(R.string.trace_board_hub_recycling),
            subtitle = date,
            value = amountOf(recycling.count, recycling.countSuffix),
            caption = status,
            details = recyclingDetails(recycling, status)
        )
    }

    private fun ingredientNode(ingredient: DomainWriteOffTable, idPT: Long, units: Units): TraceNode {
        // Открыть доску можно только у собственной продукции хозяйства
        val target = if (ingredient.productOrigin == ProductOrigin.ADD) {
            Triple(idPT, ingredient.title, ingredient.countSuffix.conversation4(units.settings))
        } else null
        return TraceNode(
            id = "ing:${ingredient.id}",
            record = TraceRecord.WriteOff(ingredient),
            kind = TraceNodeKind.INGREDIENT,
            category = TraceCategory.INGREDIENT,
            title = ingredient.title,
            value = amountOf(ingredient.count, ingredient.countSuffix),
            caption = dateBuilder(ingredient.day, ingredient.month, ingredient.year),
            targetProduct = target,
            details = writeOffDetails(ingredient, units)
        )
    }

    // ================= Хабы =================

    private fun addSaleHub(
        sales: List<DomainSaleTable>,
        units: Units,
        nodes: MutableList<TraceNode>,
        edges: MutableList<TraceEdge>,
        children: MutableMap<String, List<String>>
    ) {
        if (sales.isEmpty()) return
        val hubId = "hub:sale"
        val ops = sales.sortedByDescending { dateKey(it.day, it.month, it.year) }.map { sale ->
            val money = sale.priceAll ?: sale.price
            TraceNode(
                id = "op:sale:${sale.id}",
                record = TraceRecord.Sale(sale),
                kind = TraceNodeKind.OPERATION,
                category = TraceCategory.SALE,
                title = sale.buyer?.takeIf { it.isNotBlank() } ?: getString(R.string.trace_board_no_buyer),
                value = amountOf(sale.count, sale.countSuffix),
                subtitle = units.money(money),
                caption = dateBuilder(sale.day, sale.month, sale.year),
                details = buildList {
                    add(TraceDetail(getString(R.string.trace_board_detail_date), dateBuilder(sale.day, sale.month, sale.year)))
                    add(TraceDetail(getString(R.string.trace_board_detail_count), amountOf(sale.count, sale.countSuffix)))
                    if (sale.priceAll != null) {
                        add(TraceDetail(getString(R.string.trace_board_detail_price), units.money(sale.price)))
                    }
                    add(TraceDetail(getString(R.string.trace_board_detail_sum), units.money(money)))
                    add(
                        TraceDetail(
                            getString(R.string.trace_board_detail_buyer),
                            sale.buyer?.takeIf { it.isNotBlank() } ?: getString(R.string.trace_board_no_buyer)
                        )
                    )
                    sale.category?.takeIf { it.isNotBlank() }?.let {
                        add(TraceDetail(getString(R.string.trace_board_detail_category), it))
                    }
                    noteDetail(sale.note)?.let(::add)
                }
            )
        }
        val totalMoney = sales.sumOf { it.priceAll ?: it.price }
        nodes += TraceNode(
            id = hubId,
            kind = TraceNodeKind.HUB,
            category = TraceCategory.SALE,
            title = getString(R.string.trace_board_hub_sale),
            subtitle = units.money(totalMoney),
            value = units.amount(units.total(sales.map { it.count to it.countSuffix })),
            caption = plural(R.plurals.trace_board_operations, ops.size),
            childCount = ops.size
        )
        attachHub(hubId, TraceCategory.SALE, ops, nodes, edges, children, units)
    }

    private fun addWriteOffHub(
        hubId: String,
        opPrefix: String,
        category: TraceCategory,
        hubTitle: String,
        rows: List<DomainWriteOffTable>,
        units: Units,
        nodes: MutableList<TraceNode>,
        edges: MutableList<TraceEdge>,
        children: MutableMap<String, List<String>>
    ) {
        if (rows.isEmpty()) return
        val ops = rows.sortedByDescending { dateKey(it.day, it.month, it.year) }.map { row ->
            val money = row.priceAll ?: row.price
            TraceNode(
                id = "$opPrefix${row.id}",
                record = TraceRecord.WriteOff(row),
                kind = TraceNodeKind.OPERATION,
                category = category,
                // Категория показана своей карточкой — сама операция подписана просто «Списание»
                title = getString(R.string.trace_board_operation_write_off),
                value = amountOf(row.count, row.countSuffix),
                subtitle = money?.let { units.money(it) },
                caption = dateBuilder(row.day, row.month, row.year),
                details = writeOffDetails(row, units)
            )
        }
        nodes += TraceNode(
            id = hubId,
            kind = TraceNodeKind.HUB,
            category = category,
            title = hubTitle,
            // Сумма в рублях — как у хаба продаж; строки без цены дают 0
            subtitle = units.money(rows.sumOf { it.priceAll ?: it.price ?: 0.0 }),
            value = units.amount(units.total(rows.map { it.count to it.countSuffix })),
            caption = plural(R.plurals.trace_board_operations, ops.size),
            childCount = ops.size
        )
        attachHub(hubId, category, ops, nodes, edges, children, units)
    }

    private fun addRecyclingHub(
        trace: DomainProductTrace,
        idPT: Long,
        units: Units,
        nodes: MutableList<TraceNode>,
        edges: MutableList<TraceEdge>,
        children: MutableMap<String, List<String>>
    ) {
        val raw = trace.writeOffs.filter { it.recyclingId != null }
        if (raw.isEmpty()) return
        val hubId = "hub:rec"
        val batches = trace.recyclingUsingProduct.associateBy { it.id }
        // Сырьё известных партий группируем по партии; сырьё с удалённой партией — по строке
        val (known, orphans) = raw.partition { it.recyclingId in batches }
        val rawByBatch = known.groupBy { it.recyclingId ?: 0L }
        val batchIds = rawByBatch.keys.sortedByDescending { id ->
            batches[id]?.let { dateKey(it.day, it.month, it.year) } ?: 0
        }
        val ops = mutableListOf<TraceNode>()
        val results = mutableListOf<TraceNode>()
        batchIds.forEach { recyclingId ->
            val recycling = batches[recyclingId] ?: return@forEach
            val rows = rawByBatch[recyclingId].orEmpty()
            val rawAmount = units.mixedAmount(rows.map { it.count to it.countSuffix })
            val opId = "op:rec:$recyclingId"
            val status = readyStatus(recycling)
            val resultId = "res:rec:$recyclingId"
            ops += TraceNode(
                id = opId,
                kind = TraceNodeKind.OPERATION,
                category = TraceCategory.RECYCLING,
                title = format(
                    R.string.trace_board_batch_from_s,
                    dateBuilder(recycling.day, recycling.month, recycling.year)
                ),
                value = rawAmount,
                caption = status,
                details = buildList {
                    add(TraceDetail(getString(R.string.trace_board_legend_ingredient), rawAmount))
                    addAll(recyclingDetails(recycling, status))
                }
            )
            results += TraceNode(
                id = resultId,
                kind = TraceNodeKind.RESULT,
                category = TraceCategory.RECYCLING_RESULT,
                title = recycling.title,
                value = amountOf(recycling.count, recycling.countSuffix),
                caption = status,
                targetProduct = Triple(idPT, recycling.title, recycling.countSuffix.conversation4(units.settings)),
                details = recyclingDetails(recycling, status)
            )
            edges += TraceEdge(opId, resultId, TraceCategory.RECYCLING_RESULT)
            children[opId] = listOf(resultId)
        }
        orphans.sortedByDescending { dateKey(it.day, it.month, it.year) }.forEach { row ->
            ops += TraceNode(
                id = "op:rec:orphan:${row.id}",
                kind = TraceNodeKind.OPERATION,
                category = TraceCategory.RECYCLING,
                title = getString(R.string.trace_board_batch_deleted),
                value = amountOf(row.count, row.countSuffix),
                caption = dateBuilder(row.day, row.month, row.year),
                details = writeOffDetails(row, units)
            )
        }
        val batchCount = rawByBatch.size + orphans.mapNotNull { it.recyclingId }.distinct().size

        nodes += TraceNode(
            id = hubId,
            kind = TraceNodeKind.HUB,
            category = TraceCategory.RECYCLING,
            title = getString(R.string.trace_board_hub_recycling),
            subtitle = plural(R.plurals.trace_board_batches, batchCount),
            value = units.amount(units.total(raw.map { it.count to it.countSuffix })),
            caption = plural(R.plurals.trace_board_operations, ops.size),
            childCount = ops.size
        )
        attachHub(hubId, TraceCategory.RECYCLING, ops, nodes, edges, children)
        nodes += results
    }

    private fun attachHub(
        hubId: String,
        category: TraceCategory,
        ops: List<TraceNode>,
        nodes: MutableList<TraceNode>,
        edges: MutableList<TraceEdge>,
        children: MutableMap<String, List<String>>,
        units: Units? = null
    ) {
        edges += TraceEdge(PRODUCT_ID, hubId, category)

        // Категории: только если хоть у одной операции хаба она указана.
        // Операции без категории тогда собираются в карточку «Без категории» в конце.
        val categoryOf = { op: TraceNode -> op.record?.categoryName?.trim()?.takeIf { it.isNotEmpty() } }
        if (units == null || ops.none { categoryOf(it) != null }) {
            nodes += ops
            ops.forEach { edges += TraceEdge(hubId, it.id, category) }
            children[hubId] = ops.map { it.id }
            return
        }

        val groups = ops.groupBy(categoryOf).entries
            .sortedBy { (name, _) -> name == null } // «Без категории» — последней, порядок остальных сохраняется
        val categoryIds = groups.mapIndexed { index, (name, groupOps) ->
            val categoryId = "cat:$hubId:$index"
            val records = groupOps.mapNotNull { it.record }
            val total = units.amount(units.total(records.map { it.count to it.countSuffix }))
            val money = units.money(records.sumOf { it.money })
            val operations = plural(R.plurals.trace_board_operations, groupOps.size)
            nodes += TraceNode(
                id = categoryId,
                kind = TraceNodeKind.CATEGORY,
                category = category,
                title = name ?: getString(R.string.support_text_no_category),
                value = total,
                subtitle = money,
                caption = operations,
                childCount = groupOps.size,
                details = listOf(
                    TraceDetail(getString(R.string.trace_board_detail_count), total),
                    TraceDetail(getString(R.string.trace_board_detail_sum), money),
                    TraceDetail(getString(R.string.trace_board_legend_operations), operations)
                )
            )
            nodes += groupOps
            edges += TraceEdge(hubId, categoryId, category)
            groupOps.forEach { edges += TraceEdge(categoryId, it.id, category) }
            children[categoryId] = groupOps.map { it.id }
            categoryId
        }
        children[hubId] = categoryIds
    }

    /** Категория, количество и сумма исходной записи операции — для карточек категорий. */
    private val TraceRecord.categoryName: String?
        get() = when (this) {
            is TraceRecord.Sale -> sale.category
            is TraceRecord.WriteOff -> writeOff.category
        }
    private val TraceRecord.count: Double
        get() = when (this) {
            is TraceRecord.Sale -> sale.count
            is TraceRecord.WriteOff -> writeOff.count
        }
    private val TraceRecord.countSuffix: Suffix
        get() = when (this) {
            is TraceRecord.Sale -> sale.countSuffix
            is TraceRecord.WriteOff -> writeOff.countSuffix
        }
    private val TraceRecord.money: Double
        get() = when (this) {
            is TraceRecord.Sale -> sale.priceAll ?: sale.price
            is TraceRecord.WriteOff -> writeOff.priceAll ?: writeOff.price ?: 0.0
        }

    // ================= Детали =================

    private fun writeOffDetails(row: DomainWriteOffTable, units: Units): List<TraceDetail> = buildList {
        add(TraceDetail(getString(R.string.trace_board_detail_date), dateBuilder(row.day, row.month, row.year)))
        add(TraceDetail(getString(R.string.trace_board_detail_count), amountOf(row.count, row.countSuffix)))
        val price = row.price
        if (row.priceAll != null && price != null) {
            add(TraceDetail(getString(R.string.trace_board_detail_price), units.money(price)))
        }
        (row.priceAll ?: row.price)?.let {
            add(TraceDetail(getString(R.string.trace_board_detail_sum), units.money(it)))
        }
        row.category?.takeIf { it.isNotBlank() }?.let {
            add(TraceDetail(getString(R.string.trace_board_detail_category), it))
        }
        noteDetail(row.note)?.let(::add)
    }

    private fun recyclingDetails(recycling: DomainRecycling, status: String): List<TraceDetail> = buildList {
        add(
            TraceDetail(
                getString(R.string.trace_board_detail_date),
                dateBuilder(recycling.day, recycling.month, recycling.year)
            )
        )
        add(TraceDetail(getString(R.string.trace_board_legend_result), recycling.title))
        add(TraceDetail(getString(R.string.trace_board_detail_count), amountOf(recycling.count, recycling.countSuffix)))
        add(TraceDetail(getString(R.string.trace_board_detail_status), status))
        recycling.category?.takeIf { it.isNotBlank() }?.let {
            add(TraceDetail(getString(R.string.trace_board_detail_category), it))
        }
        noteDetail(recycling.note)?.let(::add)
    }

    private fun noteDetail(note: String): TraceDetail? =
        note.takeIf { it.isNotBlank() }?.let { TraceDetail(getString(R.string.trace_board_detail_note), it) }

    // ================= Форматирование =================

    private fun readyStatus(recycling: DomainRecycling): String {
        if (recycling.isCompleted) return getString(R.string.trace_board_ready)
        val day = recycling.readyDay
        val month = recycling.readyMonth
        val year = recycling.readyYear
        return if (day != null && month != null && year != null) {
            format(R.string.trace_board_ready_until_s, dateBuilder(day, month, year))
        } else getString(R.string.trace_board_ripening)
    }

    private fun amountOf(count: Double, suffix: Suffix) = "${count.formatNumber()} ${getString(suffix.toResId())}"

    private fun getString(id: Int) = resourceProvider.getString(id)

    private fun format(id: Int, arg: String) = getString(id).format(arg)

    private fun plural(id: Int, count: Int) = resourceProvider.getQuantityString(id, count, count)

    /** Пересчёт в базовую единицу продукта. */
    private inner class Units(val baseSuffix: Suffix, val settings: DomainSettings) {

        fun isConvertible(suffix: Suffix) = suffix == baseSuffix || suffix.conversation4(settings) == baseSuffix

        fun convert(count: Double, suffix: Suffix) =
            if (suffix == baseSuffix) count else count.conversation22(suffix, baseSuffix, settings)

        /** Сумма только приводимых к базовой единице строк. */
        fun total(rows: List<Pair<Double, Suffix>>): Double =
            rows.filter { isConvertible(it.second) }.sumOf { convert(it.first, it.second) }

        fun amount(value: Double) = amountOf(value, baseSuffix)

        fun money(value: Double) = "${value.formatNumber()} ${getString(settings.currencySuffix.toResId())}"

        /** Приводимые строки — одной суммой в базовой единице, остальные — по своим единицам. */
        fun mixedAmount(rows: List<Pair<Double, Suffix>>): String {
            val (convertible, other) = rows.partition { isConvertible(it.second) }
            val parts = mutableListOf<String>()
            if (convertible.isNotEmpty()) parts += amount(total(convertible))
            other.groupBy { it.second }.forEach { (suffix, list) ->
                parts += amountOf(list.sumOf { it.first }, suffix)
            }
            return parts.joinToString(" + ")
        }
    }

    private fun dateKey(day: Int, month: Int, year: Int) = year * 10_000 + month * 100 + day

    companion object {
        const val PRODUCT_ID = "product"
        private const val MAX_DETAIL_ROWS = 10
    }
}
