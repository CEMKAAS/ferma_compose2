package com.zaroslikov.fermacompose2.ui.expenses

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ExpensesAnimalTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.ui.home.CategoryUiState
import com.zaroslikov.fermacompose2.ui.home.TitleUiState
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpensesEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle[ExpensesEditDestination.itemIdArg])
    private val itemIdPT: Int = checkNotNull(savedStateHandle[ExpensesEditDestination.itemIdArgTwo])

    private val _items = mutableStateOf<List<AnimalExpensesList2>>(emptyList())
    val items: State<List<AnimalExpensesList2>> = _items
    var itemUiState by mutableStateOf(ExpensesTableUiState())
        private set

    init {
        viewModelScope.launch {
            itemUiState = itemsRepository.getItemExpenses(itemId)
                .filterNotNull()
                .first()
                .toExpensesTableUiState()

            _items.value = itemsRepository.getItemsAnimalExpensesList2(itemIdPT, itemId.toLong())
        }
    }

    fun updateUiState2(itemDetails: AnimalExpensesList2) {
        itemDetails.ps
    }

    val titleUiState: StateFlow<TitleUiState> =
        itemsRepository.getItemsTitleExpensesList(itemIdPT).map { TitleUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TitleUiState()
            )


    val categoryUiState: StateFlow<CategoryUiState> =
        itemsRepository.getItemsCategoryExpensesList(itemIdPT).map { CategoryUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CategoryUiState()
            )

    val animalUiState: StateFlow<AnimalExpensesUiState> =
        itemsRepository.getItemsAnimalExpensesList(itemId).map { AnimalExpensesUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalExpensesUiState()
            )


    fun updateUiState(itemDetails: ExpensesTableUiState) {
        itemUiState =
            itemDetails
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
            } else if(it.ps){
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


    suspend fun saveItem() {
        itemsRepository.updateExpenses(itemUiState.toExpensesTable())
    }

    suspend fun deleteItem() {
        itemsRepository.deleteExpenses(itemUiState.toExpensesTable())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class ExpensesTableUiState(
    val id: Int = 0,
    val title: String = "", // название
    val count: String = "", // Кол-во
    val day: Int = 0,  // день
    val mount: Int = 0, // месяц
    val year: Int = 0, // время
    val priceAll: String = "",
    var suffix: String = "",
    var category: String = "",
    val note: String = "",
    val showFood: Boolean = false, // Показывать на складе иду
    val showWarehouse: Boolean = false, // Показывать на складе
    val showAnimals: Boolean = false, // Связывает животных
    val dailyExpensesFoodAndCount: Boolean = false, // указать вручную
    var dailyExpensesFood: String = "0", // Ежедневный расход еды
    var countAnimal: String = "0", // Кол-во животных
    val foodDesignedDay: Int = 0, // Кол-во дней
    val lastDayFood: String = "", //Последний день еды
    val idPT: Int = 0
)

fun ExpensesTable.toExpensesTableUiState(): ExpensesTableUiState = ExpensesTableUiState(
    id,
    title,
    count.toString(),
    day,
    mount,
    year,
    priceAll.toString(),
    suffix,
    category,
    note,
    showFood,
    showWarehouse,
    showAnimals,
    dailyExpensesFoodAndCount,
    dailyExpensesFood.toString(),
    countAnimal.toString(),
    foodDesignedDay,
    lastDayFood,
    idPT
)

fun ExpensesTableUiState.toExpensesTable(): ExpensesTable = ExpensesTable(
    id = id,
    title = title,
    count = count.replace(Regex("[^\\d.]"), "").replace(",", ".").toDouble(),
    day = day,
    mount = mount,
    year = year,
    priceAll = priceAll.replace(Regex("[^\\d.]"), "").replace(",", ".").toDouble(),
    suffix = suffix,
    category = category,
    note = note,
    showFood,
    showWarehouse,
    showAnimals,
    dailyExpensesFoodAndCount,
    dailyExpensesFood.toDouble(),
    countAnimal.toInt(),
    foodDesignedDay,
    lastDayFood,
    idPT = idPT,
)

