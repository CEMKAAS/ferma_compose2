package com.zaroslikov.fermacompose2.ui.animal.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository
) : ListViewModel<AnimalListState, AnimalListIntent>(AnimalListState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalDestination.itemIdArg])

    init {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            animalRepository.getAllAnimal(itemIdPT).collectLatest { list ->
                updateState {
                    it.copy(
                        idPT = itemIdPT,
                        list = list,
                        isLoading = false
                    )
                }
            }
        }
    }
}

sealed class AnimalListIntent : BaseIntent
