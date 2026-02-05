package com.zaroslikov.fermacompose2.ui.warehouse

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.dto.add.DomainFastAddProduct
import com.zaroslikov.domain.models.dto.shared.DomainTitleCountSuffix
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import com.zaroslikov.fermacompose2.supportFun.conversation3
import com.zaroslikov.fermacompose2.supportFun.conversation4
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseDestination
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.map

@HiltViewModel
class WarehouseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val warehouseRepository: WarehouseRepository,
    private val addRepository: AddRepository,
    private val settingsRepository: SettingsRepository,
) : ListViewModel<WarehouseState, WarehouseIntent>(WarehouseState()) {

    private val itemId: Long = checkNotNull(savedStateHandle[WarehouseDestination.itemIdArg])

    init {
        loadData()
    }

    fun onIntent(intent: WarehouseIntent) {
        when (intent) {
            is WarehouseIntent.FastAddClicked -> fastAddSave(intent.value)
            is WarehouseIntent.ShowFastAddClicked -> updateShowFastAdd(intent.value)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            combine(
                warehouseRepository.getCurrentBalanceWarehouse(itemId),
                warehouseRepository.getCurrentExpensesWarehouse(itemId),
                addRepository.getFastAddProduct(itemId),
                settingsRepository.getSettings(itemId)
            ) { warehous, tsd, sd, set ->
                buildUiState(warehous, tsd, sd, set)
            }.collect { newState ->
                updateState {
                    it.copy(
                        isLoading = false,
                        productList = newState.first,
                        expensesList = newState.second,
                        fastAddList = newState.third,
                        idPT = itemId
                    )
                }
            }
        }
    }

    private fun updateShowFastAdd(isShowFastAddProduct: Boolean) {
        updateState {
            it.copy(
                isShowFastAddProduct = isShowFastAddProduct
            )
        }
    }

    private fun fastAddSave(domain: DomainFastAddProduct) {
        viewModelScope.launch {
            val dateList = dateToday().split(".")
            addRepository.insertAdd(
                DomainAddTable(
                    title = domain.title,
                    count = domain.count,
                    countSuffix = domain.suffix,
                    day = dateList[0].toInt(),
                    month = dateList[1].toInt(),
                    year = dateList[2].toInt(),
                    price = 0.0,
                    category = domain.category ?: "TODO()", //FIXME
                    animalId = domain.idAnimal,
                    note = "TODO()", //TODO
                    idPT = itemId
                )
            )
            updateShowFastAdd(false)
            /* showMessage(
                 resourceProvider.getString(R.string.toast_add_s)
                     .format(
                         getState().currentProduct.title,
                         getState().currentProduct.count,
                         getState().currentProduct.countSuffix
                     )
             )*/
        }
    }

    private fun buildUiState(
        productList: List<DomainTitleCountSuffix>,
        expensesList: List<DomainTitleCountSuffix>,
        fastAddList: List<DomainFastAddProduct>,
        settings: DomainSettings
    ): Triple<List<DomainTitleCountSuffix>, List<DomainTitleCountSuffix>, List<DomainFastAddProduct>> {
        return Triple(build(productList, settings), build(expensesList, settings), fastAddList)
    }

    private fun build(
        productList: List<DomainTitleCountSuffix>,
        settings: DomainSettings
    ): List<DomainTitleCountSuffix> {
        return productList
            .groupBy { it.title to it.suffix.conversation4(settings) }
            .map { (title, items) ->

                val totalCount = items.sumOf {
                    it.count.conversation3(it.suffix, settings)
                }
                DomainTitleCountSuffix(
                    title = title.first,
                    count = totalCount,
                    suffix = title.second
                )
            }.filter { it.count > 0 }
    }

    /*val homeFoodUiState: StateFlow<ExpensesUiState> =
            itemsRepository.getCurrentFoodWarehouse(itemId).map { ExpensesUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = ExpensesUiState()
                )

        suspend fun saveItem(writeOffTable: WriteOffTable, expensesTable: ExpensesTable) {
            itemsRepository.updateExpenses(
                expensesTable.copy(
                    isShowFood = false,
                    isShowWarehouse = false
                )
            )
            itemsRepository.insertWriteOff(writeOffTable)
        }*/
}

sealed class WarehouseIntent() : BaseIntent {
    data class FastAddClicked(val value: DomainFastAddProduct) : WarehouseIntent()
    data class ShowFastAddClicked(val value: Boolean) : WarehouseIntent()
}
