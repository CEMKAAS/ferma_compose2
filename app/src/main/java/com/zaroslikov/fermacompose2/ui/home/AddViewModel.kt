package com.zaroslikov.fermacompose2.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.water.BrieflyUiState
import com.zaroslikov.fermacompose2.data.water.HomeUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn


class AddViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {


    val itemId: Int = checkNotNull(savedStateHandle[HomeDestination.itemIdArg])

    private var _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    val homeUiState: StateFlow<HomeUiState> =
        itemsRepository.getAllItemsStream(itemId).map { HomeUiState(it) }
            .onStart {
                // Устанавливаем состояние загрузки перед началом загрузки данных
                _isLoading.value = true
            }.onEach {
                // Отключаем состояние загрузки после завершения загрузки данных
                _isLoading.value = false
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    val brieflyUiState: StateFlow<BrieflyUiState> =
        itemsRepository.getBrieflyItemAdd(itemId).map { BrieflyUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BrieflyUiState()
            )

    fun getDetailsName(name: String): Flow<List<AddTable>> {
        return itemsRepository.getBrieflyDetailsItemAdd(itemId.toLong(), name)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}


