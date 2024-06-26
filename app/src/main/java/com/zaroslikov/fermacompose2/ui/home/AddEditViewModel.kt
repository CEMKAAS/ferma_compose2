package com.zaroslikov.fermacompose2.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle[AddEditDestination.itemIdArg])
    private val itemIdPT: Int = checkNotNull(savedStateHandle[AddEditDestination.itemIdArgTwo])

    var itemUiState by mutableStateOf(AddTableUiState())
        private set

    init {
        viewModelScope.launch {
            itemUiState = itemsRepository.getItemAdd(itemId)
                .filterNotNull()
                .first()
                .toAddTableUiState()
        }
    }

    fun updateUiState(itemDetails: AddTableUiState) {
        itemUiState =
            itemDetails
    }

    val titleUiState: StateFlow<TitleUiState> =
        itemsRepository.getItemsTitleAddList(itemIdPT).map { TitleUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TitleUiState()
            )


    val categoryUiState: StateFlow<CategoryUiState> =
        itemsRepository.getItemsCategoryAddList(itemIdPT).map { CategoryUiState(it) }
            .filterNotNull()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CategoryUiState()
            )

    val animalUiState: StateFlow<AnimalUiState> =
        itemsRepository.getItemsAnimalAddList(itemIdPT).map { AnimalUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalUiState()
            )

    suspend fun saveItem() {
        itemsRepository.updateItem(itemUiState.toAddTable())
    }

    suspend fun deleteItem() {
        itemsRepository.deleteItem(itemUiState.toAddTable())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class AddTableUiState(
    val id: Int = 0,
    val title: String = "", // название
    val count: String = "", // Кол-во
    val day: Int = 0,  // день
    val mount: Int = 0, // месяц
    val year: Int = 0, // время
    val priceAll: Double = 0.0,
    var suffix: String = "",
    var category: String = "",
    var animal: String = "",
    val idPT: Int = 0,
)

fun AddTable.toAddTableUiState(): AddTableUiState = AddTableUiState(
    id, title, count.toString(), day, mount, year, priceAll, suffix, category, animal, idPT
)

fun AddTableUiState.toAddTable(): AddTable = AddTable(
    id, title, count.replace(Regex("[^\\d.]"), "").replace(",", ".").toDouble(), day, mount, year, priceAll, suffix, category, animal, idPT
)