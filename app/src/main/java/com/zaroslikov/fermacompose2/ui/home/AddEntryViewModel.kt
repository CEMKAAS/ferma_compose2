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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


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

    val animalUiState: StateFlow<AnimalUiState2> =
        itemsRepository.getItemsAnimalAddList(itemId).map { AnimalUiState2(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalUiState2()
            )


    var itemUiState by mutableStateOf(0.0)
        private set


    fun updateUiState(name: String) {
        viewModelScope.launch {
            itemUiState = itemsRepository.getCurrentBalanceProduct(name, itemId.toLong())
                .filterNotNull()
                .first()
                .toDouble()
        }
    }


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

data class AnimalUiState2(val animalList: List<AnimalString> = listOf())

data class PairString(val name: String, val type:String)

data class AnimalString(val id:Long, val name: String, val type:String)