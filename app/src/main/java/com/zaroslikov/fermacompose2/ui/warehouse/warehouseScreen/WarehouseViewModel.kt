package com.zaroslikov.fermacompose2.ui.warehouse

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.data.room.dao.WarehouseDao
import com.zaroslikov.data.room.table.ferma.AddTable
import com.zaroslikov.data.room.table.ferma.ExpensesTable
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import com.zaroslikov.data.room.dto.ExpensesUiState
import com.zaroslikov.domain.models.dto.shared.DomainTitleCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import com.zaroslikov.fermacompose2.supportFun.conversation2
import com.zaroslikov.fermacompose2.ui.finance.analysis.Buyer
import com.zaroslikov.fermacompose2.ui.warehouse.warehouseScreen.WarehouseDestination
import com.zaroslikov.fermacompose2.ui.warehouse.warehouseScreen.WarehouseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.map

@HiltViewModel
class WarehouseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val warehouseRepository: WarehouseRepository
) : ListViewModel<WarehouseState, WarehouseIntent>(WarehouseState()) {

    private val itemId: Long = checkNotNull(savedStateHandle[WarehouseDestination.itemIdArg])

    private val baseSuffix: Suffix = Suffix.KILOGRAM

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            combine(
                warehouseRepository.getCurrentBalanceWarehouse(itemId),
                warehouseRepository.getCurrentExpensesWarehouse(itemId)
            ) { warehous, tsd ->
                buildUiState(warehous, tsd)
            }.collect { newState ->
                updateState {
                    it.copy(
                        isLoading = false,
                        productList = newState.first,
                        expensesList = newState.second
                    )
                }
            }
        }
    }

    private fun buildUiState(
        productList: List<DomainTitleCountSuffix>,
        expensesList: List<DomainTitleCountSuffix>
    ): Pair<List<DomainTitleCountSuffix>, List<DomainTitleCountSuffix>> {
        return build(productList) to build(expensesList)
    }

    private fun build(productList: List<DomainTitleCountSuffix>): List<DomainTitleCountSuffix> {
        return productList
            .groupBy { it.title }
            .map { (title, items) ->
                val totalCount = items.sumOf {
                    it.count.conversation2(
                        suffix = it.suffix, baseSuffix, baseSuffix
                    )
                }

                DomainTitleCountSuffix(
                    title = title,
                    count = totalCount,
                    suffix = baseSuffix
                )
            }.filter { it.count > 0 }
    }

    /*    val homeUiState: StateFlow<WarehouseUiState> =
            addRepository.getCurrentBalanceWarehouse(itemId).map { WarehouseUiState(it) }.onStart {
                // Устанавливаем состояние загрузки перед началом загрузки данных
                _isLoading.value = true
            }.onEach {
                // Отключаем состояние загрузки после завершения загрузки данных
                _isLoading.value = false
            }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = WarehouseUiState()
                )*/

    /*    val homeFoodUiState: StateFlow<ExpensesUiState> =
            itemsRepository.getCurrentFoodWarehouse(itemId).map { ExpensesUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = ExpensesUiState()
                )

        val homeExpensesUiState: StateFlow<WarehouseUiState> =
            itemsRepository.getCurrentExpensesWarehouse(itemId).map { WarehouseUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = WarehouseUiState()
                )

        val fastAddUiState: StateFlow<FastAddUiState> =
            itemsRepository.getFastAddProduct(itemId.toLong()).map { FastAddUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = FastAddUiState()
                )

        suspend fun saveItem(writeOffTable: WriteOffTable, expensesTable: ExpensesTable) {
            itemsRepository.updateExpenses(
                expensesTable.copy(
                    isShowFood = false,
                    isShowWarehouse = false
                )
            )
            itemsRepository.insertWriteOff(writeOffTable)
        }

        suspend fun saveFastAddItem(fastAdd: FastAdd) {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            itemsRepository.insertItem(
                item = AddTable(
                    0,
                    title = fastAdd.title,
                    count = fastAdd.disc,
                    day = calendar[Calendar.DAY_OF_MONTH],
                    mount = calendar[Calendar.MONTH] + 1,
                    year = calendar[Calendar.YEAR],
                    price = 0.0,
                    countSuffix = fastAdd.suffix,
                    category = fastAdd.category,
                    animalId = fastAdd.idAnimal,
                    note = "",
                    idPT = itemId.toLong()
                )
            )
        }

        companion object {
            private const val TIMEOUT_MILLIS = 5_000L
        }*/
}

sealed class WarehouseIntent() : BaseIntent

//data class WarehouseUiState(val itemList: List<WarehouseData> = listOf())
data class FastAddUiState(val itemList: List<FastAdd> = listOf())

data class FastAdd(
    val title: String,
    val disc: Double,
    val suffix: String,
    val category: String,
    val idAnimal: Long,
    val animal: String,
    val count: Int
)