package com.zaroslikov.fermacompose2.ui.writeOff


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.ui.home.AnimalUiState
import com.zaroslikov.fermacompose2.ui.home.CategoryUiState
import com.zaroslikov.fermacompose2.ui.home.TitleUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class WriteOffEntryViewModel (
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[WriteOffEntryDestination.itemIdArg])

    val titleUiState: StateFlow<TitleUiState> =
        itemsRepository.getItemsTitleAddList(itemId).map { TitleUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TitleUiState()
            )


    val categoryUiState: StateFlow<CategoryUiState> =
        itemsRepository.getItemsCategoryWriteOffList(itemId).map {CategoryUiState(it)}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CategoryUiState()
            )

    val animalUiState: StateFlow<AnimalUiState> =
        itemsRepository.getItemsAnimalWriteOffList(itemId).map {AnimalUiState(it)}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalUiState()
            )

    suspend fun saveItem(writeOffTable: WriteOffTable) {
        itemsRepository.insertWriteOff(writeOffTable)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

/**
 * Ui State for HomeScreen
 */