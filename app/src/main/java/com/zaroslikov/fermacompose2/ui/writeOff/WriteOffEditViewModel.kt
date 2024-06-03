package com.zaroslikov.fermacompose2.ui.writeOff

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.ui.home.AnimalUiState
import com.zaroslikov.fermacompose2.ui.home.CategoryUiState
import com.zaroslikov.fermacompose2.ui.home.TitleUiState
import com.zaroslikov.fermacompose2.ui.sale.SaleEditDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WriteOffEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle[WriteOffEditDestination.itemIdArg])
    private val itemIdPT: Int = checkNotNull(savedStateHandle[WriteOffEditDestination.itemIdArgTwo])

    var itemUiState by mutableStateOf(WriteOffTableUiState())
        private set

    init {
        viewModelScope.launch {
            itemUiState = itemsRepository.getItemWriteOff(itemId)
                .filterNotNull()
                .first()
                .toWriteOffTableUiState()
        }
    }

    fun updateUiState(itemDetails: WriteOffTableUiState) {
        itemUiState =
            itemDetails
    }

    val titleUiState: StateFlow<TitleUiState> =
        itemsRepository.getItemsTitleWriteOffList(itemIdPT).map { TitleUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TitleUiState()
            )


    val categoryUiState: StateFlow<CategoryUiState> =
        itemsRepository.getItemsCategoryWriteOffList(itemIdPT).map { CategoryUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CategoryUiState()
            )

    val animalUiState: StateFlow<AnimalUiState> =
        itemsRepository.getItemsAnimalWriteOffList(itemIdPT).map { AnimalUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalUiState()
            )

    suspend fun saveItem() {
        itemsRepository.updateWriteOff(itemUiState.toWriteOffTable())
    }

    suspend fun deleteItem() {
        itemsRepository.deleteWriteOff(itemUiState.toWriteOffTable())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class WriteOffTableUiState(
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
    var buyer: String = ""
)

fun WriteOffTable.toWriteOffTableUiState(): WriteOffTableUiState = WriteOffTableUiState(
    id, title, count.toString(), day, mount, year, priceAll, idPT, suffix, category, animal
)

fun WriteOffTableUiState.toWriteOffTable(): WriteOffTable = WriteOffTable(
    id = id,
    title = title,
    count = count.toDouble(),
    day = day,
    mount, year, priceAll, suffix, category, animal, idPT
)

