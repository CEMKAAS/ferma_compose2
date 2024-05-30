package com.zaroslikov.fermacompose2.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class AddEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {


    val itemId: Int = checkNotNull(savedStateHandle[AddEditDestination.itemIdArg])
    val itemIdPT:Int = checkNotNull(savedStateHandle[AddEditDestination.itemIdArgTwo])
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

    suspend fun itemAdd(id: Int) = itemsRepository.getItemAdd(id)

    suspend fun saveItem() {

        itemsRepository.insertItem(itemUiState.toAddTable())
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
    val priceAll: String = "",
    val idPT: Int = 0,
    var suffix: String = "",
    var category: String = "",
    var animal: String = "",
)

fun AddTable.toAddTableUiState(): AddTableUiState = AddTableUiState(
    id, title, count.toString(), day, mount, year, priceAll, idPT, suffix, category, animal
)

fun AddTableUiState.toAddTable(): AddTable = AddTable(
    id, title, count.toDouble(), day, mount, year, priceAll, idPT, suffix, category, animal
)