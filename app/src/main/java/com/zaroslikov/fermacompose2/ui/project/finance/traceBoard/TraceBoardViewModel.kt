package com.zaroslikov.fermacompose2.ui.project.finance.traceBoard

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.repository.ProductTraceRepository
import com.zaroslikov.domain.repository.RecyclingRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.supportFun.todayIso
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TraceBoardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productTraceRepository: ProductTraceRepository,
    private val settingsRepository: SettingsRepository,
    private val recyclingRepository: RecyclingRepository,
    resourceProvider: ResourceProvider
) : BaseViewModel2<TraceBoardState, TraceBoardIntent, TraceBoardReduce>(
    TraceBoardState(), TraceBoardReduce()
) {
    private val idPT: Long = checkNotNull(savedStateHandle[TraceBoardDestination.itemIdArg])
    private val title: String = checkNotNull(savedStateHandle[TraceBoardDestination.itemIdArgTwo])
    private val baseSuffix: Suffix =
        Suffix.valueOf(checkNotNull(savedStateHandle[TraceBoardDestination.itemIdArgTree]))

    private val graphBuilder = TraceGraphBuilder(resourceProvider)

    /**
     * Хабы, которые пользователь переключил. Итоговая свёрнутость = «по умолчанию» XOR эти
     * переключения, поэтому обновление данных не сворачивает то, что пользователь развернул,
     * а сам поток данных не пишет в это состояние.
     */
    private val toggledHubs = MutableStateFlow<Set<String>>(emptySet())

    init {
        updateState { it.copy(idPT = idPT, title = title, baseSuffix = baseSuffix) }
        loadData()
    }

    fun onIntent(intent: TraceBoardIntent) {
        when (intent) {
            is TraceBoardIntent.HubToggled -> toggledHubs.update { set ->
                if (intent.id in set) set - intent.id else set + intent.id
            }

            else -> sendIntent(intent)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            // Созревшие переработки завершаем до показа, иначе доска покажет «зреет до» прошедшей даты.
            // Сбой здесь не должен ронять экран — доска и так покажет актуальные данные
            try {
                recyclingRepository.completeDueRecycling(todayIso())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w("TraceBoard", "completeDueRecycling", e)
            }

            // Граф перестраивается только при смене данных или настроек
            val graphFlow = combine(
                productTraceRepository.getProductTrace(idPT, title),
                settingsRepository.getSettings(idPT)
            ) { trace, settings -> graphBuilder.build(trace, idPT, title, baseSuffix, settings) }

            // Переключение хаба пересчитывает только раскладку
            combine(graphFlow, toggledHubs) { graph, toggled ->
                val defaults = graph.nodes
                    .filter { it.kind in TraceBoardLayoutEngine.COLLAPSIBLE && it.childCount > COLLAPSE_THRESHOLD }
                    .map { it.id }
                    .toSet()
                val collapsed = (defaults - toggled) + (toggled - defaults)
                TraceBoardIntent.Loaded(
                    graph = graph,
                    layout = TraceBoardLayoutEngine.layout(graph, collapsed),
                    collapsedHubs = collapsed
                )
            }
                // Построение графа и раскладка — вне главного потока
                .flowOn(Dispatchers.Default)
                .collect { sendIntent(it) }
        }
    }

    private companion object {
        /** Хаб с большим числом операций по умолчанию свёрнут. */
        const val COLLAPSE_THRESHOLD = 20
    }
}
