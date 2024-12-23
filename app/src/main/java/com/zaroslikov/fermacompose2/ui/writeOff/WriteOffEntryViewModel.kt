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
import com.zaroslikov.fermacompose2.ui.home.PairString
import com.zaroslikov.fermacompose2.ui.home.TitleUiState
import com.zaroslikov.fermacompose2.ui.sale.SaleEntryViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WriteOffEntryViewModel(
    savedStateHandle: SavedStateHandle,
    val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[WriteOffEntryDestination.itemIdArg])

    val titleUiState: StateFlow<AnimalUiState> =
        itemsRepository.getItemsWriteoffList(itemId).map { AnimalUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalUiState()
            )


    var itemUiState by mutableStateOf(0.0)
        private set

    fun updateUiState(Pair<String, Boolean>) {
        viewModelScope.launch {
            itemUiState = if (boolean) {
                itemsRepository.getCurrentBalanceProduct(name)
                    .filterNotNull()
                    .first()
                    .toDouble()
            } else {
                itemsRepository.getCurrentExpensesProduct(name)
                    .filterNotNull()
                    .first()
                    .toDouble()
            }
        }
    }


    suspend fun saveItem(writeOffTable: WriteOffTable) {
        itemsRepository.insertWriteOff(writeOffTable)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}