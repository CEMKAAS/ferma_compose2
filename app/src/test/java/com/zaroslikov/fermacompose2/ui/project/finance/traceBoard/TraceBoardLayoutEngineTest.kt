package com.zaroslikov.fermacompose2.ui.project.finance.traceBoard

import com.zaroslikov.fermacompose2.ui.project.finance.traceBoard.TraceBoardLayoutEngine.MARGIN
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TraceBoardLayoutEngineTest {

    private val eps = 0.001f

    private fun node(id: String, kind: TraceNodeKind, category: TraceCategory = TraceCategory.PRODUCTION) =
        TraceNode(id = id, kind = kind, category = category, title = id, value = "1")

    private val product = node("product", TraceNodeKind.PRODUCT, TraceCategory.PRODUCT)

    /**
     * Полный граф: 2 источника (src1 — переработка с 2 ингредиентами), 3 хаба с 3/1/2 операциями,
     * одна операция-переработка (hub3op1) с результатом.
     */
    private fun fullGraph(): TraceGraph {
        val nodes = ArrayList<TraceNode>()
        val edges = ArrayList<TraceEdge>()
        val children = LinkedHashMap<String, List<String>>()

        listOf("src1", "src2").forEach {
            nodes += node(it, TraceNodeKind.SOURCE)
            edges += TraceEdge(it, product.id, TraceCategory.PRODUCTION)
        }
        listOf("ing1", "ing2").forEach {
            nodes += node(it, TraceNodeKind.INGREDIENT, TraceCategory.INGREDIENT)
            edges += TraceEdge(it, "src1", TraceCategory.INGREDIENT)
        }
        children["src1"] = listOf("ing1", "ing2")

        listOf("hub1" to 3, "hub2" to 1, "hub3" to 2).forEach { (hub, count) ->
            nodes += node(hub, TraceNodeKind.HUB, TraceCategory.SALE)
            edges += TraceEdge(product.id, hub, TraceCategory.SALE)
            val ops = (1..count).map { "${hub}op$it" }
            ops.forEach {
                nodes += node(it, TraceNodeKind.OPERATION, TraceCategory.SALE)
                edges += TraceEdge(hub, it, TraceCategory.SALE)
            }
            children[hub] = ops
        }
        nodes += node("res1", TraceNodeKind.RESULT, TraceCategory.RECYCLING_RESULT)
        edges += TraceEdge("hub3op1", "res1", TraceCategory.RECYCLING_RESULT)
        children["hub3op1"] = listOf("res1")

        return TraceGraph(product, nodes, edges, children)
    }

    /** Хаб с двумя категориями (2 и 1 операция) и хаб без категорий с одной операцией. */
    private fun categoryGraph(): TraceGraph {
        val nodes = ArrayList<TraceNode>()
        val edges = ArrayList<TraceEdge>()
        val children = LinkedHashMap<String, List<String>>()
        nodes += node("hubA", TraceNodeKind.HUB, TraceCategory.OWN_NEEDS)
        edges += TraceEdge(product.id, "hubA", TraceCategory.OWN_NEEDS)
        listOf("catKids" to 2, "catNone" to 1).forEach { (cat, count) ->
            nodes += node(cat, TraceNodeKind.CATEGORY, TraceCategory.OWN_NEEDS)
            edges += TraceEdge("hubA", cat, TraceCategory.OWN_NEEDS)
            val ops = (1..count).map { "${cat}op$it" }
            ops.forEach {
                nodes += node(it, TraceNodeKind.OPERATION, TraceCategory.OWN_NEEDS)
                edges += TraceEdge(cat, it, TraceCategory.OWN_NEEDS)
            }
            children[cat] = ops
        }
        children["hubA"] = listOf("catKids", "catNone")
        nodes += node("hubB", TraceNodeKind.HUB, TraceCategory.SALE)
        nodes += node("hubBop1", TraceNodeKind.OPERATION, TraceCategory.SALE)
        edges += TraceEdge(product.id, "hubB", TraceCategory.SALE)
        edges += TraceEdge("hubB", "hubBop1", TraceCategory.SALE)
        children["hubB"] = listOf("hubBop1")
        return TraceGraph(product, nodes, edges, children)
    }

    @Test
    fun `категории — между хабом и операциями, центрированы на своих операциях`() {
        val graph = categoryGraph()
        val layout = TraceBoardLayoutEngine.layout(graph, emptySet())
        assertEquals(graph.nodes.size + 1, layout.nodes.size)
        assertBoardInvariants(layout)
        val byId = layout.byId()
        val hub = byId.getValue("hubA")
        listOf("catKids", "catNone").forEach { catId ->
            val cat = byId.getValue(catId)
            val ops = graph.children.getValue(catId).map { byId.getValue(it) }
            assertTrue(cat.x >= hub.right)
            ops.forEach { assertTrue(it.x >= cat.right) }
            assertEquals((ops.minOf { it.y } + ops.maxOf { it.bottom }) / 2f, cat.centerY, eps)
        }
        // Операция хаба без категорий стоит в колонке операций, а не категорий
        assertEquals(byId.getValue("catKidsop1").x, byId.getValue("hubBop1").x, eps)
    }

    @Test
    fun `свёрнутый хаб скрывает категории и их операции`() {
        val graph = categoryGraph()
        val ids = TraceBoardLayoutEngine.layout(graph, setOf("hubA")).nodes.map { it.node.id }.toSet()
        assertTrue("hubA" in ids)
        listOf("catKids", "catNone", "catKidsop1", "catKidsop2", "catNoneop1").forEach {
            assertFalse(it in ids)
        }
        assertTrue("hubBop1" in ids)
    }

    @Test
    fun `свёрнутая категория скрывает только свои операции`() {
        val graph = categoryGraph()
        val layout = TraceBoardLayoutEngine.layout(graph, setOf("catKids"))
        val ids = layout.nodes.map { it.node.id }.toSet()
        assertTrue("catKids" in ids)
        assertFalse("catKidsop1" in ids)
        assertFalse("catKidsop2" in ids)
        assertTrue("catNoneop1" in ids)
        assertBoardInvariants(layout)
        // Категория внутри свёрнутого хаба остаётся скрытой, даже если сама свёрнута
        val both = TraceBoardLayoutEngine.layout(graph, setOf("hubA", "catKids")).nodes.map { it.node.id }
        assertFalse("catKids" in both)
    }

    private fun overlaps(a: PlacedNode, b: PlacedNode): Boolean =
        a.x < b.right - eps && b.x < a.right - eps && a.y < b.bottom - eps && b.y < a.bottom - eps

    private fun TraceBoardLayout.byId() = nodes.associateBy { it.node.id }

    private fun assertBoardInvariants(layout: TraceBoardLayout) {
        layout.nodes.forEach {
            assertTrue("x < MARGIN у ${it.node.id}", it.x >= MARGIN - eps)
            assertTrue("y < MARGIN у ${it.node.id}", it.y >= MARGIN - eps)
        }
        assertEquals(layout.nodes.maxOf { it.right } + MARGIN, layout.width, eps)
        assertEquals(layout.nodes.maxOf { it.bottom } + MARGIN, layout.height, eps)
        // Никакие два узла не пересекаются
        for (i in layout.nodes.indices) for (j in i + 1 until layout.nodes.size) {
            val a = layout.nodes[i]
            val b = layout.nodes[j]
            assertFalse("пересечение ${a.node.id} и ${b.node.id}", overlaps(a, b))
        }
        // Концы нитей лежат на границах узлов
        val byId = layout.byId()
        layout.edges.forEach { e ->
            val from = byId.getValue(e.edge.from)
            val to = byId.getValue(e.edge.to)
            assertEquals(from.right, e.x1, eps)
            assertEquals(from.centerY, e.y1, eps)
            assertEquals(to.x, e.x2, eps)
            assertEquals(to.centerY, e.y2, eps)
        }
    }

    @Test
    fun `только продукт — один узел без нитей`() {
        val layout = TraceBoardLayoutEngine.layout(TraceGraph(product, emptyList(), emptyList(), emptyMap()), emptySet())
        assertEquals(1, layout.nodes.size)
        assertTrue(layout.edges.isEmpty())
        assertEquals(product, layout.product.node)
        assertTrue(layout.width > 0f && layout.height > 0f)
        assertEquals(MARGIN, layout.product.x, eps)
        assertEquals(MARGIN, layout.product.y, eps)
        assertBoardInvariants(layout)
    }

    @Test
    fun `полный граф — колонки, отсутствие пересечений и нити`() {
        val graph = fullGraph()
        val layout = TraceBoardLayoutEngine.layout(graph, emptySet())
        assertEquals(graph.nodes.size + 1, layout.nodes.size)
        assertEquals(graph.edges.size, layout.edges.size)
        assertBoardInvariants(layout)

        // Колонки строго возрастают по x слева направо и внутри колонки x одинаков
        val columns = layout.nodes.groupBy { TraceBoardLayoutEngine.columnOf(it.node) }.toSortedMap()
        // Колонка категорий (2) пуста и пропускается
        assertEquals(listOf(-2, -1, 0, 1, 3, 4), columns.keys.toList())
        var prevRight = Float.NEGATIVE_INFINITY
        for ((_, list) in columns) {
            val xs = list.map { it.x }.distinct()
            assertEquals(1, xs.size)
            assertTrue(xs.single() > prevRight)
            prevRight = list.maxOf { it.right }
        }

        val byId = layout.byId()
        // Результат правее операции и по вертикали пересекается с её интервалом
        graph.nodes.filter { it.kind == TraceNodeKind.RESULT }.forEach { res ->
            val parentId = graph.children.entries.single { res.id in it.value }.key
            val r = byId.getValue(res.id)
            val op = byId.getValue(parentId)
            assertTrue(r.x >= op.right)
            assertTrue(r.y < op.bottom && op.y < r.bottom)
        }
        // Хаб центрирован на своих операциях
        listOf("hub1", "hub2", "hub3").forEach { hub ->
            val ops = graph.children.getValue(hub).map { byId.getValue(it) }
            val center = (ops.minOf { it.y } + ops.maxOf { it.bottom }) / 2f
            assertEquals(center, byId.getValue(hub).centerY, eps)
        }
        // Операции идут в порядке хабов, между группами — GROUP_GAP
        assertEquals(
            byId.getValue("hub1op3").bottom + TraceBoardLayoutEngine.GROUP_GAP,
            byId.getValue("hub2op1").y, eps
        )
        // Продукт центрирован на колонке хабов
        val hubs = columns.getValue(1)
        assertEquals((hubs.minOf { it.y } + hubs.maxOf { it.bottom }) / 2f, layout.product.centerY, eps)
        // Левая группа (источники + ингредиенты) центрирована на продукте
        val leftGroup = columns.getValue(-1) + columns.getValue(-2)
        assertEquals(
            layout.product.centerY,
            (leftGroup.minOf { it.y } + leftGroup.maxOf { it.bottom }) / 2f, eps
        )
    }

    @Test
    fun `свёрнутый хаб скрывает операции и их результаты`() {
        val graph = fullGraph()
        val layout = TraceBoardLayoutEngine.layout(graph, setOf("hub3"))
        val byId = layout.byId()

        assertNotNull(byId["hub3"])
        listOf("hub3op1", "hub3op2", "res1").forEach { assertNull(byId[it]) }
        assertTrue(layout.edges.none { it.edge.from in setOf("hub3op1", "hub3op2") || it.edge.to in setOf("hub3op1", "hub3op2", "res1") })
        assertTrue(layout.edges.any { it.edge.to == "hub3" })
        listOf("hub1op1", "hub1op2", "hub1op3", "hub2op1").forEach { assertNotNull(byId[it]) }
        // Колонка результатов пропала — пустая колонка не занимает места
        assertTrue(layout.nodes.none { it.node.kind == TraceNodeKind.RESULT })
        assertEquals(byId.getValue("hub1op1").right + MARGIN, layout.width, eps)
        assertBoardInvariants(layout)
    }

    @Test
    fun `все хабы свёрнуты — без пересечений`() {
        val layout = TraceBoardLayoutEngine.layout(fullGraph(), setOf("hub1", "hub2", "hub3"))
        assertTrue(layout.nodes.none { it.node.kind == TraceNodeKind.OPERATION || it.node.kind == TraceNodeKind.RESULT })
        assertEquals(3, layout.nodes.count { it.node.kind == TraceNodeKind.HUB })
        assertBoardInvariants(layout)
    }

    @Test
    fun `высокая стопка ингредиентов — источники центрируются на ней`() {
        val ingredients = (1..6).map { node("ing$it", TraceNodeKind.INGREDIENT, TraceCategory.INGREDIENT) }
        val graph = TraceGraph(
            product = product,
            nodes = listOf(node("src", TraceNodeKind.SOURCE)) + ingredients,
            edges = listOf(TraceEdge("src", product.id, TraceCategory.PRODUCTION)) +
                ingredients.map { TraceEdge(it.id, "src", TraceCategory.INGREDIENT) },
            children = mapOf("src" to ingredients.map { it.id })
        )
        val layout = TraceBoardLayoutEngine.layout(graph, emptySet())
        val byId = layout.byId()
        val ing = ingredients.map { byId.getValue(it.id) }
        val ingCenter = (ing.minOf { it.y } + ing.maxOf { it.bottom }) / 2f
        assertEquals(ingCenter, byId.getValue("src").centerY, eps)
        assertEquals(ingCenter, layout.product.centerY, eps)
        assertBoardInvariants(layout)
    }

    /** Самопереработка: продукт и сырьё, и результат переработки одновременно. */
    private fun selfRecyclingGraph() = TraceGraph(
        product = product,
        nodes = listOf(
            node("src:rec:5", TraceNodeKind.SOURCE, TraceCategory.RECYCLING),
            node("ing:9", TraceNodeKind.INGREDIENT, TraceCategory.INGREDIENT),
            node("hub:rec", TraceNodeKind.HUB, TraceCategory.RECYCLING),
            node("op:rec:5", TraceNodeKind.OPERATION, TraceCategory.RECYCLING),
            node("res:rec:5", TraceNodeKind.RESULT, TraceCategory.RECYCLING_RESULT)
        ),
        edges = listOf(
            TraceEdge("ing:9", "src:rec:5", TraceCategory.INGREDIENT),
            TraceEdge("src:rec:5", product.id, TraceCategory.RECYCLING),
            TraceEdge(product.id, "hub:rec", TraceCategory.RECYCLING),
            TraceEdge("hub:rec", "op:rec:5", TraceCategory.RECYCLING),
            TraceEdge("op:rec:5", "res:rec:5", TraceCategory.RECYCLING_RESULT)
        ),
        children = mapOf(
            "src:rec:5" to listOf("ing:9"),
            "hub:rec" to listOf("op:rec:5"),
            "op:rec:5" to listOf("res:rec:5")
        )
    )

    @Test
    fun `самопереработка — каждый узел ровно один раз`() {
        val graph = selfRecyclingGraph()
        val layout = TraceBoardLayoutEngine.layout(graph, emptySet())
        val ids = layout.nodes.map { it.node.id }
        assertEquals(ids.size, ids.toSet().size)
        assertEquals((graph.nodes + product).map { it.id }.toSet(), ids.toSet())
        assertEquals(graph.edges.size, layout.edges.size)
        val placedEdges = layout.edges.map { it.edge.from to it.edge.to }.toSet()
        listOf(
            "ing:9" to "src:rec:5", "src:rec:5" to product.id, product.id to "hub:rec",
            "hub:rec" to "op:rec:5", "op:rec:5" to "res:rec:5"
        ).forEach { assertTrue("нет нити $it", it in placedEdges) }
        val byId = layout.byId()
        assertTrue(byId.getValue("src:rec:5").right <= layout.product.x)
        assertTrue(byId.getValue("res:rec:5").x >= byId.getValue("op:rec:5").right)
        assertBoardInvariants(layout)
    }

    @Test
    fun `в наборе свёрнутых учитываются только хабы`() {
        val graph = selfRecyclingGraph()
        // Источник «свернуть» нельзя — его ингредиент остаётся
        val layout = TraceBoardLayoutEngine.layout(graph, setOf("src:rec:5", "op:rec:5", "unknown"))
        assertEquals(TraceBoardLayoutEngine.layout(graph, emptySet()), layout)
        // Свёрнутый хаб скрывает операцию и результат, но не ингредиент источника
        val collapsed = TraceBoardLayoutEngine.layout(graph, setOf("hub:rec", "src:rec:5")).byId()
        assertNotNull(collapsed["ing:9"])
        assertNotNull(collapsed["hub:rec"])
        assertNull(collapsed["op:rec:5"])
        assertNull(collapsed["res:rec:5"])
    }

    @Test
    fun `хаб с 21 операцией — без пересечений, доска растёт`() {
        val ops = (1..21).map { node("op:sale:$it", TraceNodeKind.OPERATION, TraceCategory.SALE) }
        val graph = TraceGraph(
            product = product,
            nodes = listOf(node("hub:sale", TraceNodeKind.HUB, TraceCategory.SALE)) + ops,
            edges = listOf(TraceEdge(product.id, "hub:sale", TraceCategory.SALE)) +
                ops.map { TraceEdge("hub:sale", it.id, TraceCategory.SALE) },
            children = mapOf("hub:sale" to ops.map { it.id })
        )
        val expanded = TraceBoardLayoutEngine.layout(graph, emptySet())
        assertBoardInvariants(expanded)
        val opH = TraceBoardLayoutEngine.sizeOf(ops.first()).height
        val stackH = 21 * opH + 20 * TraceBoardLayoutEngine.ROW_GAP
        assertEquals(stackH + 2 * MARGIN, expanded.height, eps)
        val collapsed = TraceBoardLayoutEngine.layout(graph, setOf("hub:sale"))
        assertTrue(expanded.height > collapsed.height)
        assertEquals(expanded.product.centerY, expanded.byId().getValue("hub:sale").centerY, eps)
    }

    @Test
    fun `раскладка детерминирована`() {
        val graph = fullGraph()
        assertEquals(
            TraceBoardLayoutEngine.layout(graph, setOf("hub2")),
            TraceBoardLayoutEngine.layout(graph, setOf("hub2"))
        )
        assertEquals(
            TraceBoardLayoutEngine.layout(graph, emptySet()),
            TraceBoardLayoutEngine.layout(fullGraph(), emptySet())
        )
    }
}
