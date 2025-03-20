package com.zaroslikov.fermacompose2.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.supportFun.DataStringListState
import com.zaroslikov.fermacompose2.supportFun.DataTripleListState
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

    val titleUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsTitleAddList(itemId).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )

    val categoryUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsCategoryAddList(itemId).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )

    val animalUiState: StateFlow<DataTripleListState> =
        itemsRepository.getItemsAnimalAddList(itemId).map { DataTripleListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataTripleListState()
            )


    var itemUiState by mutableDoubleStateOf(0.0)
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



