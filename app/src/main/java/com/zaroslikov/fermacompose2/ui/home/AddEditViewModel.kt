package com.zaroslikov.fermacompose2.ui.home

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

class AddEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[AddEditDestination.itemIdArg])


    init {
       viewModelScope.launch {
           val   itemUiState = itemsRepository.getItemAdd(itemId)
                .filterNotNull()
                .first()
                .toItemUiState(true)
        }
    }

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

    suspend fun itemAdd(id: Int) = itemsRepository.getItemAdd(id)

//    suspend fun saveItem(addTable: AddTable) {
//        itemsRepository.insertItem(addTable)
//    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}