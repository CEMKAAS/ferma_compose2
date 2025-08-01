package com.zaroslikov.fermacompose2.ui.sections.expenses

import android.util.Log
import androidx.compose.runtime.State
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
import com.zaroslikov.fermacompose2.supportFun.calculatePriceAll
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.sections.sale.SaleEntryDestination
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import com.zaroslikov.fermacompose2.utils.SnackbarController
import com.zaroslikov.fermacompose2.utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel
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

    private val _items = mutableStateOf<List<AnimalExpensesList2>>(emptyList())
    val items: State<List<AnimalExpensesList2>> = _items

    var animalList2 = mutableListOf<AnimalExpensesList2>()

    init {
        viewModelScope.launch {
            if (!isEntry)
                expensesUiState = itemsRepository.getItemExpenses(itemId)
                    .filterNotNull()
                    .first()
                    .toDomainMap()

            animalList2 =
                itemsRepository.getItemsAnimalExpensesList2(itemIdPT, itemId.toLong())
                    .toMutableList()
            Log.i("Animal", ":$animalList2 ")
        }
    }

    fun updateUiState(domainExpensesTable: DomainExpensesTable) {
        expensesUiState =
            domainExpensesTable
    }

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

    val titleUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsTitleExpensesList(itemIdPT).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )

    val categoryUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsCategoryExpensesList(itemIdPT).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )

    val animalUiState: StateFlow<AnimalExpensesUiState> =
        itemsRepository.getItemsAnimalExpensesList(itemIdPT).map { AnimalExpensesUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalExpensesUiState()
            )


    suspend fun saveItem(expensesTable: ExpensesTable, set: MutableMap<Long, Double>) {
        val id = itemsRepository.insertExpenses(expensesTable)

        setExpensesAnimal(set, id, itemId).forEach {
            itemsRepository.insertExpensesAnimal(it)
        }

    }

    fun insertItem() {
        viewModelScope.launch {
            if (!isError()) {
                saveExpensesAnimal(animalList2)
                autoCalculate()
                itemsRepository.insertExpenses(
                    expensesUiState.copy(
                        priceAll = autoCalculate(),
                        idPT = itemIdPT.toLong()
                    ).toRoomMap()
                )
//            metricaSale(saleUiState.copy(priceAll = autoCalculate()))
                _eventFlow.emit(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_expenses_s_s)
                        .format(
                            expensesUiState.title,
                            expensesUiState.count,
                            expensesUiState.suffix
                        )
                )
            }
        }
    }

    fun updateItem() {
        viewModelScope.launch {
            if (!isError()) {
                autoCalculate()
                itemsRepository.updateExpenses(
                    expensesUiState.copy(priceAll = autoCalculate(), idPT = itemIdPT.toLong())
                        .toRoomMap()
                )
                _eventFlow.emit(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_refresh_s_s)
                        .format(
                            expensesUiState.title,
                            expensesUiState.count,
                            expensesUiState.suffix
                        )
                )
            }
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            itemsRepository.deleteExpenses(
                expensesUiState.copy(idPT = itemIdPT.toLong()).toRoomMap()
            )
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        expensesUiState.title,
                        expensesUiState.count,
                        expensesUiState.suffix
                    )
            )
        }
    }

    suspend fun saveExpensesAnimal(animalExpensesList2: MutableList<AnimalExpensesList2>) {
        animalExpensesList2.forEach {
            if (it.ps && (it.idExpensesAnimal == 0.toLong())) {
                itemsRepository.insertExpensesAnimal(
                    ExpensesAnimalTable(
                        id = 0,
                        idExpenses = itemId.toLong(),
                        idAnimal = it.id.toLong(),
                        percentExpenses = it.presentException,
                        idPT = itemIdPT.toLong()
                    )
                )
            } else if (it.ps) {
                itemsRepository.updateExpensesAnimal(
                    ExpensesAnimalTable(
                        id = it.idExpensesAnimal,
                        idExpenses = itemId.toLong(),
                        idAnimal = it.id.toLong(),
                        percentExpenses = it.presentException,
                        idPT = itemIdPT.toLong()
                    )
                )
            } else {
                itemsRepository.deleteExpensesAnimal(
                    ExpensesAnimalTable(
                        id = it.idExpensesAnimal,
                        idExpenses = itemId.toLong(),
                        idAnimal = it.id.toLong(),
                        percentExpenses = it.presentException,
                        idPT = itemIdPT.toLong()
                    )
                )
            }
        }
    }

    suspend fun deleteExpensesAnimal(animalExpensesList2: List<AnimalExpensesList2>) {
        animalExpensesList2.forEach {
            if (it.idExpensesAnimal != 0.toLong()) {
                itemsRepository.deleteExpensesAnimal(
                    ExpensesAnimalTable(
                        id = it.idExpensesAnimal,
                        idExpenses = itemId.toLong(),
                        idAnimal = it.id.toLong(),
                        percentExpenses = it.presentException,
                        idPT = itemIdPT.toLong()
                    )
                )
            }
        }
    }

    private fun isError(): Boolean {
        updateUiState(expensesUiState.validate())
        return expensesUiState.hasAnyError
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

    fun autoCalculate(): String = if (isAutoCalculate.value) calculatePriceAll(
        expensesUiState.priceAll,
        expensesUiState.count
    ) else expensesUiState.priceAll

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
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