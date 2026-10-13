package com.zaroslikov.fermacompose2.ui.project.finance.traceBoard

import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class TraceBoardIntent : BaseIntent {
    data class Loaded(
        val graph: TraceGraph,
        val layout: TraceBoardLayout,
        val collapsedHubs: Set<String>
    ) : TraceBoardIntent()

    /** Тап по хабу: свернуть или развернуть его операции. */
    data class HubToggled(val id: String) : TraceBoardIntent()
    data class NodeSelected(val node: TraceNode) : TraceBoardIntent()
    data object DetailsDismissed : TraceBoardIntent()
}
