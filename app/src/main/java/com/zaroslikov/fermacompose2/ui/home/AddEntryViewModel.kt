package com.zaroslikov.fermacompose2.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class AddEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[AddEntryDestination.itemIdArg])

    val titleUiState: StateFlow<TitleUiState> =
        itemsRepository.getItemsTitleAddList(itemId).map { TitleUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TitleUiState()
            )


    val categoryUiState: StateFlow<CategoryUiState> =
        itemsRepository.getItemsCategoryAddList(itemId).map { CategoryUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CategoryUiState()
            )

    val animalUiState: StateFlow<AnimalUiState> =
        itemsRepository.getItemsAnimalAddList(itemId).map { AnimalUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalUiState()
            )

    suspend fun saveItem(addTable: AddTable) {
        itemsRepository.insertItem(addTable)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

/**
 * Ui State for HomeScreen
 */
data class TitleUiState(val titleList: List<String> = listOf())

data class CategoryUiState(val categoryList: List<String> = listOf())
data class AnimalUiState(val animalList: List<PairString> = listOf())

data class PairString(val name: String, val type:String)