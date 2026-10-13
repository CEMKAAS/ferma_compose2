package com.zaroslikov.fermacompose2.ui.project.finance.traceBoard

/** Размер карточки узла, dp. */
data class NodeSize(val width: Float, val height: Float)

/** Узел с координатами на доске (dp, начало — левый верхний угол). */
data class PlacedNode(
    val node: TraceNode,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {
    val centerX: Float get() = x + width / 2f
    val centerY: Float get() = y + height / 2f
    val right: Float get() = x + width
    val bottom: Float get() = y + height
}

/** Нить: от правой середины узла-источника (x1, y1) к левой середине узла-приёмника (x2, y2). */
data class PlacedEdge(
    val edge: TraceEdge,
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float
)

/** Готовая раскладка доски; [width]/[height] — размер всей доски с полями. */
data class TraceBoardLayout(
    val nodes: List<PlacedNode>,
    val edges: List<PlacedEdge>,
    val width: Float,
    val height: Float,
    val product: PlacedNode
)

/**
 * Послойная раскладка доски слева направо:
 * INGREDIENT (−2) → SOURCE (−1) → PRODUCT (0) → HUB (1) → OPERATION (2) → RESULT (3).
 * Чистый Kotlin, детерминирована, без рекурсии.
 */
object TraceBoardLayoutEngine {
    const val COLUMN_GAP = 72f
    const val ROW_GAP = 16f
    const val GROUP_GAP = 40f
    const val MARGIN = 48f

    fun sizeOf(node: TraceNode): NodeSize = when (node.kind) {
        TraceNodeKind.PRODUCT -> NodeSize(240f, 112f)
        TraceNodeKind.HUB -> NodeSize(200f, 104f)
        TraceNodeKind.CATEGORY -> NodeSize(200f, 104f)
        TraceNodeKind.SOURCE -> NodeSize(200f, 96f)
        TraceNodeKind.OPERATION -> NodeSize(200f, 100f)
        TraceNodeKind.RESULT -> NodeSize(200f, 96f)
        TraceNodeKind.INGREDIENT -> NodeSize(200f, 80f)
    }

    /** Узлы, которые можно свернуть нажатием: их дети и все потомки скрываются. */
    val COLLAPSIBLE = setOf(TraceNodeKind.HUB, TraceNodeKind.CATEGORY)

    private const val CATEGORY_COLUMN = 2
    private const val OPERATION_COLUMN = 3
    private const val RESULT_COLUMN = 4

    /** Колонка категорий пуста, если у хабов нет категорий, — тогда она не занимает места. */
    fun columnOf(node: TraceNode): Int = when (node.kind) {
        TraceNodeKind.INGREDIENT -> -2
        TraceNodeKind.SOURCE -> -1
        TraceNodeKind.PRODUCT -> 0
        TraceNodeKind.HUB -> 1
        TraceNodeKind.CATEGORY -> CATEGORY_COLUMN
        TraceNodeKind.OPERATION -> OPERATION_COLUMN
        TraceNodeKind.RESULT -> RESULT_COLUMN
    }

    fun layout(graph: TraceGraph, collapsedHubs: Set<String>): TraceBoardLayout {
        val byId = graph.nodesById
        val hidden = hiddenIds(graph, collapsedHubs)
        val visible = graph.nodes.filter { it.id !in hidden && it.id != graph.product.id }
        val visibleIds = visible.mapTo(HashSet()) { it.id }

        // Верхняя координата y каждого узла (до нормализации)
        val top = HashMap<String, Float>()
        fun h(id: String) = sizeOf(byId.getValue(id)).height
        fun bottomOf(id: String) = top.getValue(id) + h(id)
        fun centerOf(id: String) = top.getValue(id) + h(id) / 2f

        fun kidsOf(id: String, column: Int): List<String> =
            graph.children[id].orEmpty()
                .filter { it in visibleIds && columnOf(byId.getValue(it)) == column }
                .distinct()

        fun stackHeight(ids: List<String>): Float =
            ids.sumOf { h(it).toDouble() }.toFloat() + ROW_GAP * (ids.size - 1).coerceAtLeast(0)

        /** Укладывает [ids] столбиком начиная с [start]; возвращает низ стопки. */
        fun stack(ids: List<String>, start: Float): Float {
            var y = start
            ids.forEachIndexed { i, id ->
                if (i > 0) y += ROW_GAP
                top[id] = y
                y += h(id)
            }
            return y
        }

        /** Сдвигает вниз узлы колонки (в порядке [ids]), чтобы они не перекрывались. */
        fun sweep(ids: List<String>) {
            var prevBottom = Float.NEGATIVE_INFINITY
            for (id in ids) {
                val y = maxOf(top.getValue(id), prevBottom + ROW_GAP)
                top[id] = y
                prevBottom = y + h(id)
            }
        }

        val byColumn = visible.groupBy { columnOf(it) }
        fun columnIds(column: Int) = byColumn[column].orEmpty().map { it.id }

        // --- Колонки 1 (хабы), 2 (категории) и 3 (операции).
        // У хаба дети — либо категории (а у них операции), либо сразу операции.
        // Сначала столбиком укладываются операции хаба: внутри категории через ROW_GAP,
        // между категориями через GROUP_GAP. Категория центрируется на своих операциях,
        // хаб — на всех операциях своей группы.
        val hubs = columnIds(1)
        val opsOrder = ArrayList<String>()
        val categoriesOrder = ArrayList<String>()
        var hubBottom = Float.NEGATIVE_INFINITY
        var opBottom = Float.NEGATIVE_INFINITY

        /** Сегменты операций хаба: категория со своими операциями или одна операция без категории. */
        fun segmentsOf(hubId: String): List<Pair<String?, List<String>>> {
            if (hubId in collapsedHubs) return emptyList()
            val direct = graph.children[hubId].orEmpty().filter { it in visibleIds && it !in top }.distinct()
            return direct.mapNotNull { id ->
                when (columnOf(byId.getValue(id))) {
                    CATEGORY_COLUMN -> id to kidsOf(id, OPERATION_COLUMN).filter { it !in top }
                    OPERATION_COLUMN -> null to listOf(id)
                    else -> null
                }
            }
        }

        for (hubId in hubs) {
            val hubH = h(hubId)
            val segments = segmentsOf(hubId)
            // Высота стопки операций хаба с учётом промежутков между категориями
            val groupH = segments.foldIndexed(0f) { i, acc, (category, ops) ->
                val prev = segments.getOrNull(i - 1)
                val gap = when {
                    i == 0 -> 0f
                    category != null || prev?.first != null -> GROUP_GAP
                    else -> ROW_GAP
                }
                acc + gap + stackHeight(ops).coerceAtLeast(category?.let(::h) ?: 0f)
            }
            if (segments.isEmpty()) {
                top[hubId] = if (hubBottom.isInfinite()) 0f else hubBottom + ROW_GAP
            } else {
                // Группа ниже предыдущей группы и так, чтобы центрированный хаб не налез на предыдущий хаб
                val byOps = if (opBottom.isInfinite()) Float.NEGATIVE_INFINITY else opBottom + GROUP_GAP
                val byHubs = if (hubBottom.isInfinite()) Float.NEGATIVE_INFINITY
                else hubBottom + ROW_GAP + hubH / 2f - groupH / 2f
                var groupTop = maxOf(byOps, byHubs)
                if (groupTop.isInfinite()) groupTop = 0f

                var y = groupTop
                segments.forEachIndexed { i, (category, ops) ->
                    val prev = segments.getOrNull(i - 1)
                    if (i > 0) y += if (category != null || prev?.first != null) GROUP_GAP else ROW_GAP
                    val segmentH = stackHeight(ops).coerceAtLeast(category?.let(::h) ?: 0f)
                    // Операции центрируются в сегменте (категория может быть выше одной операции)
                    stack(ops, y + (segmentH - stackHeight(ops)) / 2f)
                    opsOrder += ops
                    if (category != null) {
                        top[category] = y + segmentH / 2f - h(category) / 2f
                        categoriesOrder += category
                    }
                    y += segmentH
                }
                opBottom = y
                top[hubId] = groupTop + groupH / 2f - hubH / 2f
            }
            hubBottom = bottomOf(hubId)
        }
        sweep(hubs)
        sweep(categoriesOrder)
        // Категории и операции без хаба — в конец колонки
        val orphanCategories = columnIds(CATEGORY_COLUMN).filter { it !in top }
        if (orphanCategories.isNotEmpty()) {
            val lastBottom = categoriesOrder.maxOfOrNull { bottomOf(it) }
            stack(orphanCategories, if (lastBottom == null) 0f else lastBottom + GROUP_GAP)
        }
        val orphanOps = columnIds(OPERATION_COLUMN).filter { it !in top }
        if (orphanOps.isNotEmpty()) {
            opBottom = stack(orphanOps, if (opBottom.isInfinite()) 0f else opBottom + GROUP_GAP)
            opsOrder += orphanOps
        }

        // --- Колонка 4 (результаты): центрируются на родительской операции.
        val resultsOrder = ArrayList<String>()
        for (opId in opsOrder) {
            val kids = kidsOf(opId, RESULT_COLUMN).filter { it !in top }
            if (kids.isEmpty()) continue
            stack(kids, centerOf(opId) - stackHeight(kids) / 2f)
            resultsOrder += kids
        }
        val orphanResults = columnIds(RESULT_COLUMN).filter { it !in top }
        if (orphanResults.isNotEmpty()) {
            val lastBottom = resultsOrder.maxOfOrNull { bottomOf(it) }
            stack(orphanResults, if (lastBottom == null) 0f else lastBottom + GROUP_GAP)
            resultsOrder += orphanResults
        }
        sweep(resultsOrder.sortedBy { top.getValue(it) })

        // --- Колонка −1 (источники) и −2 (ингредиенты).
        val sources = columnIds(-1)
        stack(sources, 0f)
        val ingredientsOrder = ArrayList<String>()
        for (srcId in sources) {
            val kids = kidsOf(srcId, -2).filter { it !in top }
            if (kids.isEmpty()) continue
            stack(kids, centerOf(srcId) - stackHeight(kids) / 2f)
            ingredientsOrder += kids
        }
        val orphanIngredients = columnIds(-2).filter { it !in top }
        if (orphanIngredients.isNotEmpty()) {
            val lastBottom = ingredientsOrder.maxOfOrNull { bottomOf(it) }
            stack(orphanIngredients, if (lastBottom == null) 0f else lastBottom + GROUP_GAP)
            ingredientsOrder += orphanIngredients
        }
        sweep(ingredientsOrder)
        // Стопка ингредиентов выше колонки источников — центрируем источники на ней
        if (sources.isNotEmpty() && ingredientsOrder.isNotEmpty()) {
            val srcTop = top.getValue(sources.first())
            val srcBottom = bottomOf(sources.last())
            val ingTop = ingredientsOrder.minOf { top.getValue(it) }
            val ingBottom = ingredientsOrder.maxOf { bottomOf(it) }
            if (ingBottom - ingTop > srcBottom - srcTop) {
                val shift = (ingTop + ingBottom) / 2f - (srcTop + srcBottom) / 2f
                sources.forEach { top[it] = top.getValue(it) + shift }
            }
        }

        // --- Колонка 0 (продукт).
        val productId = graph.product.id
        val productH = sizeOf(graph.product).height
        val left = sources + ingredientsOrder
        fun spanCenter(ids: List<String>): Float? = if (ids.isEmpty()) null
        else (ids.minOf { top.getValue(it) } + ids.maxOf { bottomOf(it) }) / 2f
        val productCenter = spanCenter(hubs) ?: spanCenter(left) ?: (MARGIN + productH / 2f)
        top[productId] = productCenter - productH / 2f
        // Левая группа (−1/−2) центрируется на продукте
        spanCenter(left)?.let { leftCenter ->
            val shift = productCenter - leftCenter
            left.forEach { top[it] = top.getValue(it) + shift }
        }

        // --- Нормализация: верхний узел ровно на MARGIN.
        val minY = top.values.min()
        val dy = MARGIN - minY

        // --- Координаты x колонок: пустые колонки пропускаются.
        val allNodes = listOf(graph.product) + visible
        val columnWidth = sortedMapOf<Int, Float>()
        for (node in allNodes) {
            val c = columnOf(node)
            columnWidth[c] = maxOf(columnWidth[c] ?: 0f, sizeOf(node).width)
        }
        val columnX = HashMap<Int, Float>()
        var x = MARGIN
        for ((c, w) in columnWidth) {
            columnX[c] = x
            x += w + COLUMN_GAP
        }

        val placed = LinkedHashMap<String, PlacedNode>()
        for (node in allNodes) {
            val size = sizeOf(node)
            val c = columnOf(node)
            placed[node.id] = PlacedNode(
                node = node,
                x = columnX.getValue(c) + (columnWidth.getValue(c) - size.width) / 2f,
                y = top.getValue(node.id) + dy,
                width = size.width,
                height = size.height
            )
        }

        val edges = graph.edges.mapNotNull { edge ->
            val from = placed[edge.from] ?: return@mapNotNull null
            val to = placed[edge.to] ?: return@mapNotNull null
            PlacedEdge(edge, from.right, from.centerY, to.x, to.centerY)
        }

        val nodes = placed.values.toList()
        return TraceBoardLayout(
            nodes = nodes,
            edges = edges,
            width = nodes.maxOf { it.right } + MARGIN,
            height = nodes.maxOf { it.bottom } + MARGIN,
            product = placed.getValue(productId)
        )
    }

    /** Дети свёрнутых хабов и все их потомки (обход в ширину). */
    private fun hiddenIds(graph: TraceGraph, collapsedHubs: Set<String>): Set<String> {
        // Сворачиваются только хабы и категории; прочие id (например, источники) игнорируются
        val collapsed = collapsedHubs.filterTo(HashSet()) { graph.nodesById[it]?.kind in COLLAPSIBLE }
        val hidden = HashSet<String>()
        val queue = ArrayDeque<String>()
        for (id in collapsed) {
            graph.children[id].orEmpty().forEach { if (hidden.add(it)) queue.addLast(it) }
        }
        while (queue.isNotEmpty()) {
            graph.children[queue.removeFirst()].orEmpty().forEach {
                if (hidden.add(it)) queue.addLast(it)
            }
        }
        // Свёрнутый узел скрыт, только если свёрнут кто-то выше (категория в свёрнутом хабе)
        hidden.remove(graph.product.id)
        return hidden
    }
}
