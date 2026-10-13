package com.zaroslikov.fermacompose2.ui.project.finance.traceBoard

import com.zaroslikov.fermacompose2.base.reduce.BaseReducer

class TraceBoardReduce : BaseReducer<TraceBoardState, TraceBoardIntent>() {
    override fun reducer(state: TraceBoardState, intent: TraceBoardIntent): TraceBoardState {
        return when (intent) {
            is TraceBoardIntent.Loaded -> state.copy(
                graph = intent.graph,
                layout = intent.layout,
                collapsedHubs = intent.collapsedHubs,
                isEmpty = intent.graph.nodes.isEmpty(),
                // Узел мог исчезнуть или измениться после обновления данных
                selectedNode = state.selectedNode?.let { intent.graph.nodesById[it.id] },
                isLoading = false
            )

            // Раскладку пересчитывает ViewModel, новое состояние придёт через Loaded
            is TraceBoardIntent.HubToggled -> state

            is TraceBoardIntent.NodeSelected -> state.copy(selectedNode = intent.node)
            TraceBoardIntent.DetailsDismissed -> state.copy(selectedNode = null)
        }
    }
}
