package com.zaroslikov.fermacompose2.ui.sections.add.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.supportFun.PairDataDoubleSting
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import com.zaroslikov.fermacompose2.utils.SnackbarController
import com.zaroslikov.fermacompose2.utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository,
    private val resourceProvider: ResourceProvider,
) : ViewModel() {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[AddEntryDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[AddEntryDestination.itemId])
    val isEntry: Boolean = itemId == -1L

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _state = MutableStateFlow(AddEntryState())
    val state: StateFlow<AddEntryState> = _state.asStateFlow()

    fun onIntent(intent: AddEntryIntent) {
        when (intent) {
            is AddEntryIntent.TitleAndSuffix -> updateTitleAndSuffix(intent.pair)
            is AddEntryIntent.TitleChanged -> updateTitle(intent.value)
            is AddEntryIntent.CountChanged -> updateCount(intent.value)
            is AddEntryIntent.Suffix -> _state.update { it.copy(countSuffix = intent.value) }
            is AddEntryIntent.CategoryChanged -> _state.update { it.copy(category = intent.value) }
            is AddEntryIntent.Date -> _state.update { it.copy(date = intent.value) }
            is AddEntryIntent.NoteChanged -> _state.update { it.copy(note = intent.value) }
            is AddEntryIntent.Animal -> updateAnimal(intent.animal)
            is AddEntryIntent.AnimalClear -> updateAnimalClear(intent.value)
            is AddEntryIntent.AnimalNameById -> _state.update { it.copy(animal = intent.value) }
            is AddEntryIntent.CountWarehouse -> _state.update { it.copy(warehouseList = intent.value) }
            AddEntryIntent.Insert -> insertItem()
            AddEntryIntent.Update -> updateItem()
            AddEntryIntent.Delete -> deleteItem()
        }
    }

    init {
        loadInitialData()
    }


    fun loadInitialData() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isEntry = isEntry,
                    category = resourceProvider.getString(R.string.support_text_no_category),
                    countSuffix = resourceProvider.getString(R.string.suffix_pieces),
                    titleList = itemsRepository.getItemsTitleAddList(itemIdPT).first(),
                    categoryList = itemsRepository.getItemsCategoryAddList(itemIdPT).first(),
                    animalList = itemsRepository.getItemsAnimalAddList(itemIdPT).first()
                )
            }
            if (!isEntry) {
                val domainAddTable = itemsRepository.getItemAdd(itemId)
                    .filterNotNull()
                    .first()

                _state.update { it.toUiMap(domainAddTable) }

                updateWarehouseUiStateSync(_state.value.title)

                val currentAnimalId = _state.value.animalId
                if (currentAnimalId != null) {
                    val animal = itemsRepository.getAnimalById(currentAnimalId).first()
                    _state.update { it.copy(animal = animal) }
                }
            }
        }
    }


    private fun updateWarehouseUiState(name: String) {
        viewModelScope.launch {
            updateWarehouseUiStateSync(name)
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String) {
        val pair = itemsRepository
            .getCurrentBalanceProductList(name, itemIdPT.toLong())
            .filterNotNull()
            .firstOrNull()

        pair?.let {
            _state.update { it.copy(warehouseList = pair) }
        }
    }

    fun insertItem() {
        viewModelScope.launch {
            if (!isError()) {
                itemsRepository.insertItem(
                    _state.value.toDomainMap(itemIdPT = itemIdPT.toLong())
                )
//                metricAdd(addUiState)
                _eventFlow.emit(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_add_s)
                        .format(_state.value.title, _state.value.count, _state.value.countSuffix)
                )
            }
        }
    }

    fun updateItem() {
        viewModelScope.launch {
            if (!isError()) {
                itemsRepository.updateItem(
                    _state.value.toDomainMap(id = itemId.toLong(), itemIdPT = itemIdPT.toLong())
                )
                _eventFlow.emit(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_refresh_s)
                        .format(_state.value.title, _state.value.count, _state.value.countSuffix)
                )
            }
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            itemsRepository.deleteAddById(itemId.toLong())
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(_state.value.title, _state.value.count, _state.value.countSuffix)
            )
        }
    }

    fun showMessage(message: String) {
        viewModelScope.launch {
            SnackbarController.sendEvent(
                event = SnackbarEvent(
                    message = message
                )
            )
        }
    }


    private fun updateTitleAndSuffix(pair: Pair<String, String>) {
        _state.update {
            it.copy(
                title = pair.first,
                countSuffix = pair.second,
                error = it.error.copy(
                    isErrorTitle = pair.first.isBlank(),
                    isErrorSlash = pair.first.contains("/")
                )
            )
        }
        updateWarehouseUiState(pair.first)
    }

    private fun updateTitle(title: String) {
        _state.update {
            it.copy(
                title = title,
                error = it.error.copy(
                    isErrorTitle = title.isBlank(),
                    isErrorSlash = title.contains("/")
                )
            )
        }
        updateWarehouseUiState(title)
    }

    private fun updateCount(count: String) {
        _state.update {
            it.copy(
                count = count,
                error = it.error.copy(isErrorCount = count.isBlank())
            )
        }
    }

    private fun updateAnimal(animal: Pair<Long, String>) {
        _state.update {
            it.copy(
                selectedAnimalIndex = animal.first,
                animalId = animal.first,
                animal = animal.second
            )
        }
    }

    private fun updateAnimalClear(animal: String) {
        _state.update {
            it.copy(
                animalId = 0,
                animal = animal
            )
        }
    }

    private fun validation() {
        _state.update { state ->
            state.copy(
                error = state.error.copy(
                    isErrorTitle = state.title.isBlank(),
                    isErrorSlash = state.title.contains("/"),
                    isErrorCount = state.count.isBlank(),
                )
            )
        }
    }

    private fun isError(): Boolean {
        validation()
        return _state.value.error.hasAnyError
    }
}


sealed class AddEntryIntent {
    data class TitleChanged(val value: String) : AddEntryIntent()
    data class TitleAndSuffix(val pair: Pair<String, String>) : AddEntryIntent()
    data class CountChanged(val value: String) : AddEntryIntent()
    data class Suffix(val value: String) : AddEntryIntent()
    data class CountWarehouse(val value: List<PairDataDoubleSting>) : AddEntryIntent()
    data class CategoryChanged(val value: String) : AddEntryIntent()
    data class Date(val value: String) : AddEntryIntent()
    data class Animal(val animal: Pair<Long, String>) : AddEntryIntent()
    data class AnimalNameById(val value: String) : AddEntryIntent()
    data class AnimalClear(val value: String) : AddEntryIntent()
    data class NoteChanged(val value: String) : AddEntryIntent()
    data object Insert : AddEntryIntent()
    data object Update : AddEntryIntent()
    data object Delete : AddEntryIntent()
}

