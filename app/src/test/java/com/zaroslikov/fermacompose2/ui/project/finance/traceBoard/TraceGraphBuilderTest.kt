package com.zaroslikov.fermacompose2.ui.project.finance.traceBoard

import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.trace.DomainProductTrace
import com.zaroslikov.domain.models.dto.trace.DomainTraceProduction
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainRecycling
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.project.finance.traceBoard.TraceGraphBuilder.Companion.PRODUCT_ID
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TraceGraphBuilderTest {

    /** Читаемые строки для нужных id, остальные — «str:<id>». */
    private class FakeResourceProvider : ResourceProvider {
        private val known = mapOf(
            R.string.trace_board_produced_s to "Произведено: %s",
            R.string.trace_board_stock_s to "Остаток: %s",
            Suffix.KILOGRAM.toResId() to "кг",
            Suffix.GRAM.toResId() to "г",
            Suffix.PIECES.toResId() to "шт",
            Suffix.RUBLE.toResId() to "₽"
        )

        override fun getString(id: Int): String = known[id] ?: "str:$id"

        override fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any): String =
            "plural:$id:$quantity"
    }

    private val builder = TraceGraphBuilder(FakeResourceProvider())
    private val settings = DomainSettings(weightSuffix = Suffix.KILOGRAM)
    private val idPT = 1L
    private val title = "Молоко"

    private fun build(trace: DomainProductTrace, baseSuffix: Suffix = Suffix.KILOGRAM) =
        builder.build(trace, idPT, title, baseSuffix, settings)

    private fun production(
        id: Long,
        count: Double,
        animalId: Long? = null,
        suffix: Suffix = Suffix.KILOGRAM,
        day: Int = 1
    ) = DomainTraceProduction(
        id = id, count = count, countSuffix = suffix, day = day, month = 1, year = 2026,
        price = 0.0, priceSuffix = Suffix.RUBLE, animalId = animalId,
        animalName = animalId?.let { "Животное $it" }, animalType = animalId?.let { "Корова" },
        animalIsGroup = animalId?.let { false }, animalIcon = null, animalImagePath = null, note = ""
    )

    private fun sale(id: Long, count: Double, price: Double, priceAll: Double? = null, buyer: String? = null) =
        DomainSaleTable(
            id = id, title = title, count = count, countSuffix = Suffix.KILOGRAM, price = price,
            priceAll = priceAll, productOrigin = ProductOrigin.ADD, day = 2, month = 1, year = 2026,
            category = null, buyer = buyer, note = "", idPT = idPT
        )

    private fun writeOff(
        id: Long,
        count: Double,
        status: Boolean = true,
        recyclingId: Long? = null,
        origin: ProductOrigin? = ProductOrigin.ADD,
        writeOffTitle: String = title
    ) = DomainWriteOffTable(
        id = id, title = writeOffTitle, count = count, countSuffix = Suffix.KILOGRAM,
        productOrigin = origin, day = 3, month = 1, year = 2026, status = status, note = "",
        idPT = idPT, recyclingId = recyclingId
    )

    private fun recycling(
        id: Long,
        recyclingTitle: String,
        isCompleted: Boolean = false,
        addId: Long? = null,
        ready: Boolean = false
    ) = DomainRecycling(
        id = id, title = recyclingTitle, count = 2.0, countSuffix = Suffix.KILOGRAM,
        day = 4, month = 1, year = 2026,
        readyDay = if (ready) 20 else null, readyMonth = if (ready) 1 else null,
        readyYear = if (ready) 2026 else null,
        isCompleted = isCompleted, addId = addId, idPT = idPT
    )

    private fun TraceGraph.node(id: String): TraceNode =
        nodesById[id] ?: throw AssertionError("Нет узла $id; есть ${nodesById.keys}")

    private fun TraceGraph.hasEdge(from: String, to: String) = edges.any { it.from == from && it.to == to }

    @Test
    fun `категории — карточка на категорию и «Без категории» для остальных`() {
        val trace = DomainProductTrace(
            production = listOf(production(1, 10.0)),
            writeOffs = listOf(
                writeOff(21, 1.0).copy(category = "Детям", price = 5.0, priceAll = 5.0),
                writeOff(22, 2.0).copy(category = " Детям ", priceAll = 7.0),
                writeOff(23, 0.5)
            )
        )
        val graph = build(trace)
        val hubKids = graph.children.getValue("hub:own")
        assertEquals(2, hubKids.size)

        val kids = graph.node(hubKids[0])
        assertEquals(TraceNodeKind.CATEGORY, kids.kind)
        assertEquals("Детям", kids.title)
        assertEquals(listOf("op:own:21", "op:own:22"), graph.children.getValue(kids.id))
        assertTrue(graph.hasEdge("hub:own", kids.id))
        assertTrue(graph.hasEdge(kids.id, "op:own:21"))
        assertFalse(graph.hasEdge("hub:own", "op:own:21"))

        // Карточка без категории — последней
        val none = graph.node(hubKids[1])
        assertEquals("str:${R.string.support_text_no_category}", none.title)
        assertEquals(listOf("op:own:23"), graph.children.getValue(none.id))
        // У хаба по-прежнему учтены все операции — для порога сворачивания
        assertEquals(3, graph.node("hub:own").childCount)
    }

    @Test
    fun `без категорий карточки категорий не создаются`() {
        val graph = build(salesAndWriteOffsTrace())
        assertTrue(graph.nodes.none { it.kind == TraceNodeKind.CATEGORY })
        assertEquals(listOf("op:own:21"), graph.children.getValue("hub:own"))
    }

    // ---------- 1. Источники-животные ----------

    @Test
    fun `производство двух животных и без животного даёт три источника со связями на продукт`() {
        val graph = build(
            DomainProductTrace(
                production = listOf(
                    production(1, 2.0, animalId = 10),
                    production(2, 3.0, animalId = 20),
                    production(3, 4.0, animalId = null)
                )
            )
        )

        val sources = graph.nodes.filter { it.kind == TraceNodeKind.SOURCE }.map { it.id }.toSet()
        assertEquals(setOf("src:animal:10", "src:animal:20", "src:none"), sources)
        sources.forEach { assertTrue("Нет нити $it → product", graph.hasEdge(it, PRODUCT_ID)) }
    }

    @Test
    fun `произведено продукта считает все строки производства`() {
        val graph = build(
            DomainProductTrace(
                production = listOf(
                    production(1, 2.0, animalId = 10),
                    production(2, 3.0, animalId = 20),
                    production(3, 4.0, animalId = null)
                )
            )
        )

        assertEquals("Произведено: 9 кг", graph.product.value)
    }

    @Test
    fun `строки одного животного объединяются в один источник`() {
        val graph = build(
            DomainProductTrace(
                production = listOf(production(1, 2.0, animalId = 10), production(2, 3.0, animalId = 10))
            )
        )

        val node = graph.node("src:animal:10")
        assertEquals("5 кг", node.value)
        assertEquals(1, graph.nodes.count { it.kind == TraceNodeKind.SOURCE })
    }

    // ---------- 2. Хабы продаж, собственных нужд и утилизации ----------

    private fun salesAndWriteOffsTrace() = DomainProductTrace(
        production = listOf(production(1, 10.0)),
        sales = listOf(
            sale(11, 2.0, price = 100.0, priceAll = 250.0, buyer = "Иван"),
            sale(12, 1.0, price = 50.0, priceAll = null, buyer = null)
        ),
        writeOffs = listOf(
            writeOff(21, 1.0, status = true),
            writeOff(22, 0.5, status = false)
        )
    )

    @Test
    fun `продажи собираются в хаб с операцией на каждую продажу`() {
        val graph = build(salesAndWriteOffsTrace())

        val hub = graph.node("hub:sale")
        assertEquals(TraceNodeKind.HUB, hub.kind)
        assertEquals(2, hub.childCount)
        assertEquals(setOf("op:sale:11", "op:sale:12"), graph.children["hub:sale"]?.toSet())
        assertTrue(graph.hasEdge(PRODUCT_ID, "hub:sale"))
        assertTrue(graph.hasEdge("hub:sale", "op:sale:11"))
        assertTrue(graph.hasEdge("hub:sale", "op:sale:12"))
    }

    @Test
    fun `сумма хаба продаж берёт priceAll, а без него price`() {
        val graph = build(salesAndWriteOffsTrace())

        assertEquals("300 ₽", graph.node("hub:sale").subtitle)
        // Количество и сумма — отдельными строками
        assertEquals("1 кг", graph.node("op:sale:12").value)
        assertEquals("50 ₽", graph.node("op:sale:12").subtitle)
    }

    @Test
    fun `продажа без покупателя подписана заглушкой`() {
        val graph = build(salesAndWriteOffsTrace())

        assertEquals("Иван", graph.node("op:sale:11").title)
        assertEquals("str:${R.string.trace_board_no_buyer}", graph.node("op:sale:12").title)
    }

    @Test
    fun `списание со статусом true уходит в собственные нужды, false — в утилизацию`() {
        val graph = build(salesAndWriteOffsTrace())

        assertEquals(1, graph.node("hub:own").childCount)
        assertEquals(listOf("op:own:21"), graph.children["hub:own"])
        assertEquals(1, graph.node("hub:disposal").childCount)
        assertEquals(listOf("op:disposal:22"), graph.children["hub:disposal"])
        assertTrue(graph.hasEdge(PRODUCT_ID, "hub:own"))
        assertTrue(graph.hasEdge("hub:own", "op:own:21"))
        assertTrue(graph.hasEdge(PRODUCT_ID, "hub:disposal"))
        assertTrue(graph.hasEdge("hub:disposal", "op:disposal:22"))
    }

    @Test
    fun `остаток равен произведённому минус продажи и списания`() {
        val graph = build(salesAndWriteOffsTrace())

        // 10 − (2 + 1) − (1 + 0,5) = 5,5
        assertEquals("Остаток: 5,5 кг", graph.product.subtitle)
    }

    @Test
    fun `без операций хабы не создаются`() {
        val graph = build(DomainProductTrace(production = listOf(production(1, 1.0))))

        assertTrue(graph.nodes.none { it.kind == TraceNodeKind.HUB })
    }

    // ---------- 3. Продукт — сырьё переработки ----------

    private fun rawMaterialTrace() = DomainProductTrace(
        production = listOf(production(1, 10.0)),
        writeOffs = listOf(
            writeOff(31, 3.0, status = true, recyclingId = 5),
            writeOff(32, 1.0, status = false, recyclingId = 5)
        ),
        recyclingUsingProduct = listOf(recycling(5, "Сыр", isCompleted = false, ready = true))
    )

    @Test
    fun `сырьё переработки даёт хаб переработки с операцией партии`() {
        val graph = build(rawMaterialTrace())

        assertEquals(listOf("op:rec:5"), graph.children["hub:rec"])
        assertEquals(1, graph.node("hub:rec").childCount)
        assertTrue(graph.hasEdge(PRODUCT_ID, "hub:rec"))
        assertTrue(graph.hasEdge("hub:rec", "op:rec:5"))
    }

    @Test
    fun `партия переработки ведёт к результату с переходом на доску результата`() {
        val graph = build(rawMaterialTrace())

        val result = graph.node("res:rec:5")
        assertEquals(TraceNodeKind.RESULT, result.kind)
        assertEquals(Triple(idPT, "Сыр", Suffix.KILOGRAM), result.targetProduct)
        assertTrue(graph.hasEdge("op:rec:5", "res:rec:5"))
        assertEquals(listOf("res:rec:5"), graph.children["op:rec:5"])
    }

    @Test
    fun `сырьё переработки не попадает в собственные нужды и утилизацию`() {
        val graph = build(rawMaterialTrace())

        assertNull(graph.nodesById["hub:own"])
        assertNull(graph.nodesById["hub:disposal"])
    }

    @Test
    fun `сырьё переработки вычитается из остатка`() {
        val graph = build(rawMaterialTrace())

        // 10 − 3 − 1 = 6
        assertEquals("Остаток: 6 кг", graph.product.subtitle)
    }

    // ---------- 4. Сырьё удалённой партии ----------

    @Test
    fun `сырьё без партии становится операцией-сиротой без результата`() {
        val graph = build(
            DomainProductTrace(
                production = listOf(production(1, 10.0)),
                writeOffs = listOf(writeOff(41, 2.0, recyclingId = 99))
            )
        )

        val ops = graph.children["hub:rec"].orEmpty()
        assertEquals(1, ops.size)
        val orphan = graph.node(ops.single())
        assertTrue(orphan.id.startsWith("op:rec:orphan:"))
        assertEquals(TraceNodeKind.OPERATION, orphan.kind)
        assertNull(graph.children[orphan.id])
        assertTrue(graph.nodes.none { it.kind == TraceNodeKind.RESULT })
    }

    // ---------- 5. Продукт — результат переработки ----------

    private fun producedByRecyclingTrace() = DomainProductTrace(
        production = listOf(production(100, 2.0, animalId = null)),
        recyclingProducingProduct = listOf(recycling(7, title, isCompleted = true, addId = 100)),
        producingIngredients = listOf(
            writeOff(51, 5.0, recyclingId = 7, origin = ProductOrigin.ADD, writeOffTitle = "Сливки"),
            writeOff(52, 0.1, recyclingId = 7, origin = ProductOrigin.EXPENSES, writeOffTitle = "Закваска")
        )
    )

    @Test
    fun `переработка-источник связывает ингредиенты с продуктом`() {
        val graph = build(producedByRecyclingTrace())

        val source = graph.node("src:rec:7")
        assertEquals(TraceNodeKind.SOURCE, source.kind)
        assertEquals(listOf("ing:51", "ing:52"), graph.children["src:rec:7"])
        assertTrue(graph.hasEdge("ing:51", "src:rec:7"))
        assertTrue(graph.hasEdge("ing:52", "src:rec:7"))
        assertTrue(graph.hasEdge("src:rec:7", PRODUCT_ID))
    }

    @Test
    fun `строка производства переработки не создаёт источник без животного`() {
        val graph = build(producedByRecyclingTrace())

        assertNull(graph.nodesById["src:none"])
    }

    @Test
    fun `переход на доску есть только у ингредиента собственной продукции`() {
        val graph = build(producedByRecyclingTrace())

        assertEquals(Triple(idPT, "Сливки", Suffix.KILOGRAM), graph.node("ing:51").targetProduct)
        assertNull(graph.node("ing:52").targetProduct)
    }

    @Test
    fun `незавершённая переработка-источник остаётся источником и не меняет произведённое`() {
        val trace = DomainProductTrace(
            production = listOf(production(1, 3.0, animalId = 10)),
            recyclingProducingProduct = listOf(recycling(8, title, isCompleted = false, addId = null, ready = true))
        )
        val graph = build(trace)

        assertEquals(TraceNodeKind.SOURCE, graph.node("src:rec:8").kind)
        assertEquals("Произведено: 3 кг", graph.product.value)
    }

    // ---------- 6. Переработка продукта в самого себя ----------

    @Test
    fun `переработка продукта в себя даёт уникальные id и источник и операцию`() {
        val graph = build(
            DomainProductTrace(
                production = listOf(production(200, 2.0, animalId = null)),
                writeOffs = listOf(writeOff(61, 3.0, recyclingId = 5)),
                recyclingUsingProduct = listOf(recycling(5, title, isCompleted = true, addId = 200)),
                recyclingProducingProduct = listOf(recycling(5, title, isCompleted = true, addId = 200)),
                producingIngredients = listOf(writeOff(61, 3.0, recyclingId = 5))
            )
        )

        val ids = (graph.nodes + graph.product).map { it.id }
        assertEquals("Дубли id: ${ids.groupBy { it }.filter { it.value.size > 1 }.keys}", ids.size, ids.toSet().size)
        assertNotNull(graph.nodesById["src:rec:5"])
        assertNotNull(graph.nodesById["op:rec:5"])
    }

    // ---------- 7. Единицы ----------

    @Test
    fun `граммы приводятся к килограммам в итоге продукта`() {
        val graph = build(
            DomainProductTrace(
                production = listOf(production(1, 1500.0, suffix = Suffix.GRAM), production(2, 1.0))
            )
        )

        assertEquals("Произведено: 2,5 кг", graph.product.value)
    }

    @Test
    fun `штуки при базе в килограммах не входят в итог продукта`() {
        val graph = build(
            DomainProductTrace(
                production = listOf(production(1, 2.0), production(2, 3.0, suffix = Suffix.PIECES))
            )
        )

        assertEquals("Произведено: 2 кг", graph.product.value)
    }

    @Test
    fun `штуки показываются на карточке источника отдельным слагаемым`() {
        val graph = build(
            DomainProductTrace(
                production = listOf(production(1, 2.0), production(2, 3.0, suffix = Suffix.PIECES))
            )
        )

        assertEquals("2 кг + 3 шт", graph.node("src:none").value)
        assertFalse(graph.product.value.contains("шт"))
    }
}
