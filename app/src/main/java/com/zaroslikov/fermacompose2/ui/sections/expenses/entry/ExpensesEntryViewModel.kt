package com.zaroslikov.fermacompose2.ui.sections.expenses.entry


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ExpensesAnimalTable
import com.zaroslikov.fermacompose2.data.mapper.AnimaMapper.toDomainMap
import com.zaroslikov.fermacompose2.data.mapper.AnimaMapper.toRoomMap
import com.zaroslikov.fermacompose2.data.mapper.toDomainMap
import com.zaroslikov.fermacompose2.data.mapper.toRoomMap
import com.zaroslikov.fermacompose2.supportFun.DataPairListState
import com.zaroslikov.fermacompose2.supportFun.DataStringListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.sections.sale.entry.SaleEntryDestination
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
import kotlin.collections.forEach


@HiltViewModel
class ExpensesEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val itemIdPT: Int = checkNotNull(savedStateHandle[SaleEntryDestination.itemIdPT])
    private val itemId: Int = checkNotNull(savedStateHandle[SaleEntryDestination.itemId])
    val isEntry: Boolean = itemId == -1

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    var expensesUiState by mutableStateOf(
        ExpensesEntryState().copy(
            isEntry = isEntry,
            feedFoodChipSuffix = resourceProvider.getString(R.string.suffix_kilogram),
            feedFoodInputSuffix = resourceProvider.getString(R.string.suffix_kilogram),
            category = resourceProvider.getString(R.string.support_text_no_category),
            countSuffix = resourceProvider.getString(R.string.suffix_pieces),
            weightSuffix = resourceProvider.getString(R.string.suffix_kilogram),
        )
    )
        private set

    init {
        viewModelScope.launch {
            if (!isEntry) {
                val domainExpensesTable = itemsRepository.getItemExpenses(itemId)
                    .filterNotNull()
                    .first()
                    .toDomainMap()

                expensesUiState = expensesUiState.updateFromDomain(domainExpensesTable,
                    suffix = resourceProvider.getString(R.string.suffix_kilogram),
                    countSuffix = resourceProvider.getString(R.string.suffix_pieces) )
            }
            val animalList = itemsRepository.getItemsAnimalExpensesList2(itemIdPT, itemId.toLong())
            expensesUiState = expensesUiState.copy(
                animalList2 = animalList
            )
        }
    }

    fun updateUiState(expensesEntryState: ExpensesEntryState) {
        expensesUiState =
            expensesEntryState
    }

    fun updateWarehouseUiState(name: String) {
        viewModelScope.launch {
            expensesUiState = expensesUiState.copy(
                countInWarehouse = itemsRepository.getCurrentBalanceProduct(name, itemId.toLong())
                    .filterNotNull()
                    .first()
                    .toDomainMap()
            )
        }
    }

    val titleUiState: StateFlow<DataPairListState> =
        itemsRepository.getItemsTitleExpensesList(itemIdPT).map { DataPairListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataPairListState()
            )

    val categoryUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsCategoryExpensesList(itemIdPT).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )


    fun insertItem() {
        viewModelScope.launch {
            if (!isError()) {
                expensesUiState = expensesUiState.updateForSaveAnimalList()
                val id = itemsRepository.insertExpenses(
                    expensesUiState.updateForSave(itemIdPT = itemIdPT.toLong()).toRoomMap()
                )
                setExpensesAnimal(id)
//                metricalExpenses() TODO Нужно придумать, как грамотно сохранять
                _eventFlow.emit(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_expenses_s_s)
                        .format(
                            expensesUiState.title,
                            expensesUiState.count,
                            expensesUiState.countSuffix
                        )
                )
            }
        }
    }

    fun updateItem() {
        viewModelScope.launch {
            if (!isError()) {
                expensesUiState = expensesUiState.updateForSaveAnimalList()
                itemsRepository.updateExpenses(
                    expensesUiState.updateForSave(
                        id = itemId.toLong(),
                        itemIdPT = itemIdPT.toLong()
                    )
                        .toRoomMap()
                )
                saveExpensesAnimal()
                _eventFlow.emit(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_refresh_s_s)
                        .format(
                            expensesUiState.title,
                            expensesUiState.count,
                            expensesUiState.countSuffix
                        )
                )
            }
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            itemsRepository.deleteExpenses(
                expensesUiState.updateForSave(itemIdPT = itemIdPT.toLong()).toRoomMap()
            )
            deleteExpensesAnimal()
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        expensesUiState.title,
                        expensesUiState.count,
                        expensesUiState.countSuffix
                    )
            )
        }
    }

    private suspend fun setExpensesAnimal(id: Long) {
        expensesUiState.animalList2.filter { it.ps }.map {
            ExpensesAnimalTable(
                idExpenses = id,
                idAnimal = it.id.toLong(),
                percentExpenses = it.presentException,
                idPT = itemIdPT.toLong()
            )
        }.forEach {
            itemsRepository.insertExpensesAnimal(it)
        }
    }

    private suspend fun saveExpensesAnimal() {
        expensesUiState.animalList2.forEach {
            val table = ExpensesAnimalTable(
                id = it.idExpensesAnimal,
                idExpenses = itemId.toLong(),
                idAnimal = it.id.toLong(),
                percentExpenses = it.presentException,
                idPT = itemIdPT.toLong()
            )

            when {
                it.ps && it.idExpensesAnimal == 0L -> itemsRepository.insertExpensesAnimal(table)
                it.ps -> itemsRepository.updateExpensesAnimal(table)
                else -> itemsRepository.deleteExpensesAnimal(table)
            }
        }
    }

    suspend fun deleteExpensesAnimal() {
        expensesUiState.animalList2.filter { it.idExpensesAnimal != 0L }
            .forEach {
                val table = ExpensesAnimalTable(
                    id = it.idExpensesAnimal,
                    idExpenses = itemId.toLong(),
                    idAnimal = it.id.toLong(),
                    percentExpenses = it.presentException,
                    idPT = itemIdPT.toLong()
                )
                itemsRepository.deleteExpensesAnimal(table)
            }
    }

    private fun isError(): Boolean {
        updateUiState(expensesUiState.validate())
        return expensesUiState.hasAnyError
    }

    private fun showMessage(message: String) {
        viewModelScope.launch {
            SnackbarController.sendEvent(
                event = SnackbarEvent(
                    message = message
                )
            )
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

//TODO придумать, что деать с этими классами
data class AnimalExpensesList2(
    val id: Int,
    val name: String,
    val foodDay: Double,
    val foodDaySuffix: String,
    val countAnimal: Int,
    val idExpensesAnimal: Long,
    val ps: Boolean = false,
    val presentException: Double = 0.0,
)
