package com.zaroslikov.fermacompose2.ui.writeOff

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.ui.home.TitleUiState
import com.zaroslikov.fermacompose2.ui.sale.SaleDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class WriteOffViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[WriteOffDestination.itemIdArg])

    val writeOffUiState: StateFlow<WriteOffUiState> =
        itemsRepository.getAllWriteOffItems(itemId).map { WriteOffUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = WriteOffUiState()
            )

    val titleUiState: StateFlow<TitleUiState> =
        itemsRepository.getItemsTitleAddList(itemId).map { TitleUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TitleUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
data class WriteOffUiState(val itemList: List<WriteOffTable> = listOf())
