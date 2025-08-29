package com.zaroslikov.fermacompose2.ui.sections.writeOff.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import com.zaroslikov.data.room.dto.BrieflyUiState
import com.zaroslikov.data.room.dto.WriteOffUiState
import com.zaroslikov.data.room.dto.DataPairListState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class WriteOffViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[WriteOffDestination.itemIdArg])

    private var _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    val writeOffUiState: StateFlow<WriteOffUiState> =
        itemsRepository.getAllWriteOffItems(itemId).map { WriteOffUiState(it) }.onStart {
            // Устанавливаем состояние загрузки перед началом загрузки данных
            _isLoading.value = true
        }.onEach {
            // Отключаем состояние загрузки после завершения загрузки данных
            _isLoading.value = false
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = WriteOffUiState()
            )

    val titleUiState: StateFlow<DataPairListState> =
        itemsRepository.getItemsTitleAddList(itemId).map { DataPairListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue =  DataPairListState()
            )

    val brieflyUiState: StateFlow<BrieflyUiState> =
        itemsRepository.getBrieflyItemWriteOff(itemId).map { BrieflyUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BrieflyUiState()
            )

    fun getDetailsName(name: String): Flow<List<WriteOffTable>> {
        return itemsRepository.getBrieflyDetailsItemWriteOff(itemId.toLong(), name)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

