package com.zaroslikov.fermacompose2.ui.project.finance.traceBoard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val ANIMATION_MS = 350

/**
 * Узел на экране во время анимации. Координаты — в dp относительно центра продукта:
 * продукт всегда в (0,0), поэтому он не двигается, а остальные карточки плавно
 * переезжают к нему или от него (вид при смене раскладки сдвигается за продуктом).
 */
@Stable
class AnimatedNode(placed: PlacedNode, start: Offset, startAlpha: Float) {
    var placed: PlacedNode = placed
        internal set
    val position = Animatable(start, Offset.VectorConverter)
    val alpha = Animatable(startAlpha)
    var isLeaving = false
        internal set
}

/**
 * Плавная смена раскладки доски: при раскрытии хаба или категории дети выезжают
 * из родителя и проявляются, при сворачивании — въезжают в него и гаснут,
 * остальные карточки и нити переезжают на новые места.
 */
@Stable
class TraceBoardAnimator {
    /** Видимые и уходящие узлы по id; порядок вставки = порядок отрисовки. */
    val nodes = mutableStateMapOf<String, AnimatedNode>()

    /** Нити текущей раскладки и нити уходящих узлов — пока те видны. */
    var edges: List<TraceEdge> by mutableStateOf(emptyList())
        private set

    private var isFirst = true

    /** Положение узла относительно центра продукта, dp. */
    private fun PlacedNode.relative(product: PlacedNode) =
        Offset(x - product.centerX, y - product.centerY)

    /**
     * Принять новую раскладку. [parents] — родитель каждого узла (хаб для категории и
     * операции и т. д.): из него выезжают новые узлы и в него уходят скрытые.
     */
    fun update(layout: TraceBoardLayout, parents: Map<String, String>, scope: CoroutineScope) {
        val product = layout.product
        val targets = layout.nodes.associateBy { it.node.id }

        if (isFirst) {
            // Первая раскладка — без анимации
            isFirst = false
            layout.nodes.forEach { nodes[it.node.id] = AnimatedNode(it, it.relative(product), 1f) }
            edges = layout.edges.map { it.edge }
            return
        }

        val spec = tween<Offset>(ANIMATION_MS, easing = FastOutSlowInEasing)
        val alphaSpec = tween<Float>(ANIMATION_MS, easing = FastOutSlowInEasing)

        /** Ближайший предок, который есть в [present] (для выезда/въезда). */
        fun ancestorIn(id: String, present: (String) -> Boolean): String? {
            var current = parents[id]
            while (current != null && !present(current)) current = parents[current]
            return current
        }

        // Приходящие и остающиеся узлы
        layout.nodes.forEach { placed ->
            val id = placed.node.id
            val target = placed.relative(product)
            val existing = nodes[id]
            if (existing != null) {
                existing.placed = placed
                existing.isLeaving = false
                scope.launch { existing.position.animateTo(target, spec) }
                scope.launch { existing.alpha.animateTo(1f, alphaSpec) }
            } else {
                // Новый узел стартует из того места, где сейчас его раскрытый предок
                val from = ancestorIn(id) { nodes[it]?.isLeaving == false }
                    ?.let { nodes[it]?.position?.value }
                    ?: target
                val node = AnimatedNode(placed, from, 0f)
                nodes[id] = node
                scope.launch { node.position.animateTo(target, spec) }
                scope.launch { node.alpha.animateTo(1f, alphaSpec) }
            }
        }

        // Уходящие узлы въезжают в свёрнутого предка и гаснут
        val leavingEdges = edges.filter { it.from !in targets || it.to !in targets }
        nodes.values.filter { it.placed.node.id !in targets && !it.isLeaving }.forEach { node ->
            val id = node.placed.node.id
            node.isLeaving = true
            val into = ancestorIn(id) { it in targets }?.let { targets.getValue(it).relative(product) }
                ?: node.position.value
            scope.launch { node.position.animateTo(into, spec) }
            scope.launch {
                node.alpha.animateTo(0f, alphaSpec)
                // Если за время анимации узел снова появился — не удаляем
                if (nodes[id]?.isLeaving == true) {
                    nodes.remove(id)
                    edges = edges.filter { it.from in nodes && it.to in nodes }
                }
            }
        }
        edges = layout.edges.map { it.edge } + leavingEdges
    }

    /**
     * Левый верхний угол узла в dp доски: относительное положение + центр продукта.
     * Центр берётся из той же раскладки, по которой сдвигается вид, — иначе на кадре
     * смены раскладки карточки на миг съезжали бы.
     */
    fun boardTopLeft(node: AnimatedNode, productCenter: Offset): Offset = node.position.value + productCenter
}
