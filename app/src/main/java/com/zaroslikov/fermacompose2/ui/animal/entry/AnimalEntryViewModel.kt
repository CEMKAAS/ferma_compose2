package com.zaroslikov.fermacompose2.ui.animal.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.data.room.mapper.AnimaMapper.toDomainMap
import com.zaroslikov.data.room.mapper.AnimaMapper.toRoomMap
import com.zaroslikov.data.room.mapper.toRoomMap
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import com.zaroslikov.fermacompose2.utils.SnackbarController
import com.zaroslikov.fermacompose2.utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalEntryDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalEntryDestination.itemId])
    val isEntry: Boolean = itemId == (-1).toLong()

    var animalUiState by mutableStateOf(
        AnimalEntryState().copy(
            isEntry = isEntry,
            countSuffix = resourceProvider.getString(R.string.suffix_pieces),
            foodDaySuffix = resourceProvider.getString(R.string.suffix_kilogram),
            category = resourceProvider.getString(R.string.animal_card_screen_add_category_expenses)
        )
    )
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    init {
        viewModelScope.launch {
            if (!isEntry) {
                val domainAnimalTable = itemsRepository.getAnimal(itemId)
                    .filterNotNull()
                    .first()
                    .toDomainMap()
                animalUiState = animalUiState.updateFromDomain(domainAnimalTable)
            }
            val typeList = itemsRepository.getTypeAnimal(itemIdPT).first()
            animalUiState = animalUiState.updateList(typeList)
        }
    }

    fun updateUiState(state: AnimalEntryState) {
        animalUiState =
            state
    }

    fun insertItem() {
        viewModelScope.launch {
            if (!isError()) {

                val idAnimal = itemsRepository.insertAnimalTable(
                    animalUiState.saveAnimal(itemIdPT = itemIdPT).toRoomMap()
                )

                val pair = animalUiState.updateForSave(idAnimal, itemIdPT)

                itemsRepository.insertAnimalCountTable(
                    pair.first
                )
                pair.second?.let {
                    itemsRepository.insertExpenses(it.toRoomMap())
                }

                /*
                    metricaWriteOff(writeOffUiState.copy(priceAll = autoCalculate()))
                 */
                _eventFlow.emit(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_sale_s)
                        .format(
                            animalUiState.title,
                            animalUiState.count,
                            animalUiState.countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    fun updateItem() {
        viewModelScope.launch {
            if (!isError()) {
                /*itemsRepository.updateAnimalTable(
                    animalUiState.saveAnimal(id = itemId, itemIdPT).toRoomMap()
                )*/
                _eventFlow.emit(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_refresh_s_s)
                        .format(
                            animalUiState.title,
                            animalUiState.count,
                            animalUiState.countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            itemsRepository.deleteAnimalTable(itemId)
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        animalUiState.title,
                        animalUiState.count,
                        animalUiState.countSuffix
                    ) //Todo Обновить название
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

    fun isError(): Boolean {
        updateUiState(animalUiState.validate())
        return animalUiState.hasAnyError
    }
}


