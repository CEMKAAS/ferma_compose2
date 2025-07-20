package com.zaroslikov.fermacompose2.ui.sections.expenses

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.Domain.models.DomainExpensesTable
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ExpensesAnimalTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.mapper.toDomainMap
import com.zaroslikov.fermacompose2.data.mapper.toRoomMap
import com.zaroslikov.fermacompose2.supportFun.DataStringListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.sections.sale.SaleEntryDestination
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import com.zaroslikov.fermacompose2.utils.SnackbarController
import com.zaroslikov.fermacompose2.utils.SnackbarEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExpensesEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val itemIdPT: Int = checkNotNull(savedStateHandle[SaleEntryDestination.itemIdPT])
    private val itemId: Int = checkNotNull(savedStateHandle[SaleEntryDestination.itemId])
    val isEntry: Boolean = itemId == -1
    var isIndicatorsValue = false

    val isAutoCalculate = mutableStateOf(false)

    var expensesUiState by mutableStateOf(
        DomainExpensesTable().copy(
            category = resourceProvider.getString(R.string.support_text_no_category),
            suffix = resourceProvider.getString(R.string.suffix_pieces),
        )
    )
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        if (!isEntry)
            viewModelScope.launch {
                expensesUiState = itemsRepository.getItemExpenses(itemId)
                    .filterNotNull()
                    .first()
                    .toDomainMap()
            }
    }

    fun updateUiState(domainExpensesTable: DomainExpensesTable) {
        expensesUiState =
            domainExpensesTable
    }

    val titleUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsTitleExpensesList(itemId).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )


    val categoryUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsCategoryExpensesList(itemId).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )

    val animalUiState: StateFlow<AnimalExpensesUiState> =
        itemsRepository.getItemsAnimalExpensesList(itemId).map { AnimalExpensesUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalExpensesUiState()
            )

    var itemUiState by mutableStateOf(DomainPairDataDoubleSting())
        private set

    fun updateWarehouseUiState(name: String) {
        viewModelScope.launch {
            itemUiState = itemsRepository.getCurrentBalanceProduct(name, itemId.toLong())
                .filterNotNull()
                .first()
                .toDomainMap()
        }
    }


    suspend fun saveItem(expensesTable: ExpensesTable, set: MutableMap<Long, Double>) {
        val id = itemsRepository.insertExpenses(expensesTable)


        setExpensesAnimal(set, id, itemId).forEach {
            itemsRepository.insertExpensesAnimal(it)
        }

    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun insertItem() {
        viewModelScope.launch {
            itemsRepository.insertExpenses(
                expensesUiState.toRoomMap()
            )
//            metricaSale(saleUiState.copy(priceAll = autoCalculate()))
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_sale_s)
                    .format(
                        expensesUiState.title,
                        expensesUiState.count,
                        expensesUiState.suffix
                    ) //Todo Обновить название
            )
        }
    }

    fun updateItem() {
        viewModelScope.launch {
//            autoCalculate()
            itemsRepository.updateExpenses(expensesUiState.toRoomMap())
            _eventFlow.emit(UiEvent.NavigateBack)
//            showMessage(
//                resourceProvider.getString(R.string.toast_refresh_s_s)
//                    .format(
//                        saleUiState.title,
//                        saleUiState.count,
//                        saleUiState.suffix
//                    ) //Todo Обновить название
//            )
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            itemsRepository.deleteExpenses(expensesUiState.toRoomMap())
            _eventFlow.emit(UiEvent.NavigateBack)
//            showMessage(
//                resourceProvider.getString(R.string.toast_delete_s)
//                    .format(
//                        saleUiState.title,
//                        saleUiState.count,
//                        saleUiState.suffix
//                    ) //Todo Обновить название
//            )
        }
    }

    fun showMessage(message: String) {
        viewModelScope.launch {
            SnackbarController.sendEvent(
                event = SnackbarEvent(
                    message = message
                )
            )
        }
    }


}

fun setExpensesAnimal(
    set: MutableMap<Long, Double>,
    idExpenses: Long,
    idPT: Int
): MutableList<ExpensesAnimalTable> {

    val list = mutableListOf<ExpensesAnimalTable>()

    if (set.isNotEmpty()) {
        set.forEach {
            list.add(
                ExpensesAnimalTable(
                    id = 0,
                    idExpenses = idExpenses,
                    idAnimal = it.key,
                    percentExpenses = it.value,
                    idPT = idPT.toLong()
                )
            )
        }
    }
    return list
}

data class AnimalExpensesUiState(val animalList: List<AnimalExpensesList> = listOf())

data class AnimalExpensesList(
    val id: Int,
    val name: String,
    val foodDay: Double,
    val countAnimal: Int
)

data class AnimalExpensesList2(
    val id: Int,
    val name: String,
    val foodDay: Double,
    val countAnimal: Int,
    val idExpensesAnimal: Long,
    var ps: Boolean,
    var presentException: Double,
)