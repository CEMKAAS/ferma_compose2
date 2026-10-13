package com.zaroslikov.fermacompose2.ui.project.finance.traceBoard

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

/**
 * Доска продукции: граф «откуда пришёл продукт и куда ушёл» и его раскладка.
 *
 * @property collapsedHubs хабы, чьи операции скрыты
 * @property selectedNode узел, детали которого открыты в нижнем листе
 * @property isEmpty у продукта нет ни источников, ни операций — на доске только он сам
 */
data class TraceBoardState(
    val idPT: Long = 0,
    val title: String = "",
    val baseSuffix: Suffix = Suffix.PIECES,
    val graph: TraceGraph? = null,
    val layout: TraceBoardLayout? = null,
    val collapsedHubs: Set<String> = emptySet(),
    val selectedNode: TraceNode? = null,
    val isEmpty: Boolean = false,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null
) : BaseState
