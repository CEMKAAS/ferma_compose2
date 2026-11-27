package com.zaroslikov.fermacompose2.ui.warehouse

import com.zaroslikov.domain.repository.RecyclingRepository
import com.zaroslikov.fermacompose2.supportFun.nowIso
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.shared.DomainTitleCountSuffix
import com.zaroslikov.domain.models.enums.WarehouseCard
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.NoteRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.supportFun.conversation3
import com.zaroslikov.fermacompose2.supportFun.conversation4
import com.zaroslikov.fermacompose2.supportFun.dateTodayArray
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.CountOfWarehouse
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.FoodListUi
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.LoadDataWarehouse
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseCardLayout
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseCalendarPreviewSource
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseDestination
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseIntent
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseItem
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseReduce
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseState
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.calculateWarehouseFeed
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.warehouseCalendarDates
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import com.zaroslikov.todo.domain.repository.TodoTaskRepository
import com.zaroslikov.todo.domain.model.TodoSmartList
import com.zaroslikov.todo.domain.model.todoSmartCount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.map

internal data class WarehouseRecordCounts(
    val counts: Map<WarehouseCard, Int> = emptyMap(),
    val tasksToday: Int? = null,
)

@HiltViewModel
class WarehouseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val warehouseRepository: WarehouseRepository,
    private val expensesRepository: ExpensesRepository,
    private val settingsRepository: SettingsRepository,
    private val resourceProvider: ResourceProvider,
    private val projectRepository: ProjectRepository,
    private val yandexMetricRepository: YandexMetricRepository,
    private val recyclingRepository: RecyclingRepository,
    noteRepository: NoteRepository,
    todoTaskRepository: TodoTaskRepository,
    calendarPreviewSource: WarehouseCalendarPreviewSource
) : BaseViewModel2<WarehouseState, WarehouseIntent, WarehouseReduce>(
    WarehouseState(),
    WarehouseReduce()
) {

    private val itemId: Long = checkNotNull(savedStateHandle[WarehouseDestination.itemIdArg])
    private var cardSaveJob: Job? = null

    val calendarPreview = calendarPreviewSource.observe(itemId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    internal val recordCounts = combine(
        recyclingRepository.getRecyclingList(itemId),
        todoTaskRepository.getProjectTasks(itemId),
        noteRepository.getAllNote(itemId),
        warehouseCalendarDates(),
    ) { recycling, tasks, notes, today ->
        WarehouseRecordCounts(
            counts = mapOf(
                WarehouseCard.RECYCLING to recycling.count { batch -> batch.products.any { !it.isCompleted } },
                WarehouseCard.TASKS to tasks.size,
                WarehouseCard.NOTES to notes.size,
            ),
            tasksToday = todoSmartCount(tasks, TodoSmartList.TODAY, today.atStartOfDay()),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WarehouseRecordCounts())

    init {
        loadData()
    }

    suspend fun refreshRecycling() {
        try {
            recyclingRepository.completeDueRecycling(nowIso())
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.e("WarehouseViewModel", "Не удалось обновить партии переработки", error)
        }
    }

    fun onIntent(intent: WarehouseIntent) {
        sendIntent(intent)
        when (intent) {
            is WarehouseIntent.ChangeCards -> saveCards(intent.layout)
            WarehouseIntent.FoodOnWriteOffChange -> foodOnWriteOffChange()
            else -> Unit
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val isArchive = projectRepository.getIsArchiveProject(itemId).first()
            combine(
                warehouseRepository.getCurrentBalanceWarehouse(itemId),
                expensesRepository.getCurrentFoodWarehouse(itemId).distinctUntilChanged(),
                warehouseRepository.getCurrentExpensesWarehouse(itemId),
                settingsRepository.getSettings(itemId),
                warehouseCalendarDates(),
            ) { products, foodList, expenses, settings, today ->
                LoadDataWarehouse(
                    productList = build(products, settings),
                    feed = calculateWarehouseFeed(foodList, today),
                    expensesList = build(expenses, settings),
                    settings = settings,
                )
            }.collect { newState ->
                updateState { state ->
                    val selectedFeed = state.currentFoodOnWriteOff?.let { current ->
                        newState.feed.rows.find { it.id == current.id }
                    }
                    state.copy(
                        isLoading = false,
                        productList = newState.productList,
                        expensesList = newState.expensesList,
                        foodList = newState.feed.rows,
                        feedSummary = newState.feed.summary,
                        currentFoodOnWriteOff = selectedFeed,
                        isOpenWarningWriteOffAlterDialog = state.isOpenWarningWriteOffAlterDialog && selectedFeed != null,
                        idPT = itemId,
                        isArchive = isArchive,
                        settings = newState.settings
                    )
                }
                for (food in newState.feed.exhausted) {
                    if (expensesRepository.hideExhaustedFeed(food.id, itemId)) {
                        showMessage(resourceProvider.getString(R.string.warehouse_feed_exhausted).format(food.title))
                    }
                }
            }
        }
    }

    private fun foodOnWriteOffChange() {
        val currentFoodOnWriteOff = getState().currentFoodOnWriteOff ?: return
        sendIntent(WarehouseIntent.OpenWarningWriteOffAlterDialogClicked(null))
        viewModelScope.launch {
            try {
                if (expensesRepository.writeOffFood(currentFoodOnWriteOff.id, currentFoodOnWriteOff.toUi())) {
                    showMessage(resourceProvider.getString(R.string.warehouse_screen_write_off))
                    yandexMetricRepository.metricalWriteOffFood(currentFoodOnWriteOff)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("WarehouseViewModel", "Не удалось списать корм", error)
                showMessage(resourceProvider.getString(R.string.warehouse_feed_write_off_error))
            }
        }
    }

    private fun saveCards(layout: WarehouseCardLayout) {
        val current = getState()
        if (current.isLoading || layout == current.visibleLayout) return
        updateState { it.copy(pendingCardCodes = layout.toCodes()) }
        if (cardSaveJob?.isActive == true) return
        cardSaveJob = viewModelScope.launch {
            while (true) {
                val codes = getState().pendingCardCodes ?: break
                try {
                    settingsRepository.updateWarehouseCardCodes(itemId, codes)
                    withTimeoutOrNull(2_000L) {
                        state.first { it.settings.warehouseCardCodes == codes }
                    }
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    Log.e("WarehouseViewModel", "Не удалось сохранить карточки", error)
                    showMessage(resourceProvider.getString(R.string.warehouse_cards_save_error))
                }
                updateState {
                    if (it.pendingCardCodes == codes) it.copy(pendingCardCodes = null) else it
                }
            }
        }
    }

    private fun build(
        productList: List<DomainTitleCountSuffix>,
        settings: DomainSettings
    ): List<WarehouseItem> {
        return productList
            .groupBy { it.title to it.suffix.conversation4(settings) }
            .map { (title, items) ->
                val totalCount = items.sumOf {
                    it.count.conversation3(it.suffix, settings)
                }
                val countOfWarehouse = when {
                    totalCount > 0 -> CountOfWarehouse.Have
                    totalCount == 0.0 -> CountOfWarehouse.Zero
                    else -> CountOfWarehouse.Minus
                }
                WarehouseItem(
                    title = title.first,
                    count = totalCount,
                    suffix = title.second,
                    countOfWarehouse = countOfWarehouse
                )
            }.sortedByDescending { it.count }
    }


    private fun FoodListUi.toUi(): DomainWriteOffTable {
        val date = dateTodayArray()
        return DomainWriteOffTable(
            title = title,
            count = weightAll,
            countSuffix = weightSuffix,
            priceSuffix = getState().settings.currencySuffix,
            price = price,
            category = resourceProvider.getString(R.string.warehouse_screen_warehouse_food_write_off),
            day = date[0],
            month = date[1],
            year = date[2],
            status = false,
            note = resourceProvider.getString(R.string.warehouse_screen_warehouse_food_write_off),
            idPT = itemId,
            productOrigin = null
        )
    }

}
