package com.zaroslikov.fermacompose2.ui.project.finance.traceBoard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class TraceBoardReduceTest {

    private val reducer = TraceBoardReduce()

    private fun node(id: String, kind: TraceNodeKind = TraceNodeKind.SOURCE, value: String = "1") =
        TraceNode(id = id, kind = kind, category = TraceCategory.PRODUCTION, title = id, value = value)

    private val product = node("product", TraceNodeKind.PRODUCT)

    private fun graph(vararg nodes: TraceNode) = TraceGraph(
        product = product,
        nodes = nodes.toList(),
        edges = nodes.map { TraceEdge(it.id, product.id, TraceCategory.PRODUCTION) },
        children = emptyMap()
    )

    private fun loaded(graph: TraceGraph, collapsed: Set<String> = emptySet()) =
        TraceBoardIntent.Loaded(graph, TraceBoardLayoutEngine.layout(graph, collapsed), collapsed)

    @Test
    fun `Loaded кладёт граф, раскладку и свёрнутые хабы и снимает загрузку`() {
        val g = graph(node("a"))
        val intent = loaded(g, setOf("hub:sale"))

        val state = reducer.reducer(TraceBoardState(), intent)

        assertSame(g, state.graph)
        assertSame(intent.layout, state.layout)
        assertEquals(setOf("hub:sale"), state.collapsedHubs)
        assertFalse(state.isLoading)
        assertFalse(state.isEmpty)
    }

    @Test
    fun `Loaded с графом без узлов помечает доску пустой`() {
        val state = reducer.reducer(TraceBoardState(), loaded(graph()))

        assertTrue(state.isEmpty)
    }

    @Test
    fun `NodeSelected открывает детали узла`() {
        val a = node("a")

        val state = reducer.reducer(TraceBoardState(), TraceBoardIntent.NodeSelected(a))

        assertEquals(a, state.selectedNode)
    }

    @Test
    fun `DetailsDismissed закрывает детали`() {
        val state = reducer.reducer(
            TraceBoardState(selectedNode = node("a")),
            TraceBoardIntent.DetailsDismissed
        )

        assertNull(state.selectedNode)
    }

    @Test
    fun `Loaded обновляет выбранный узел на версию из нового графа`() {
        val old = node("a", value = "1 кг")
        val fresh = node("a", value = "5 кг")

        val state = reducer.reducer(TraceBoardState(selectedNode = old), loaded(graph(fresh)))

        assertEquals(fresh, state.selectedNode)
    }

    @Test
    fun `Loaded сбрасывает выбранный узел, если его нет в новом графе`() {
        val state = reducer.reducer(
            TraceBoardState(selectedNode = node("gone")),
            loaded(graph(node("b")))
        )

        assertNull(state.selectedNode)
    }

    @Test
    fun `HubToggled не меняет состояние`() {
        val before = TraceBoardState(collapsedHubs = setOf("hub:sale"))

        val after = reducer.reducer(before, TraceBoardIntent.HubToggled("hub:sale"))

        assertEquals(before, after)
    }
}
