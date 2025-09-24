package com.zaroslikov.fermacompose2.ui.animal.indicators.count

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import com.zaroslikov.fermacompose2.ui.animal.indicators.size.AnimalSizeDestination
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalCountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalCountRepository: AnimalCountRepository,
    private val resourceProvider: ResourceProvider
) : ListViewModel<AnimalCountState, AnimalCountIntent>(AnimalCountState()) {
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalCountDestination.itemId])
    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalCountDestination.itemIdPT])

    init {
        viewModelScope.launch {
            loadData()
        }
    }

    /*fun onIntent(intent: AnimalCountIntent) {
        return when (intent) {
        }
    }*/

    suspend fun loadData() {
        updateState { it.copy(isLoading = true) }
        animalCountRepository.getCountAnimal(itemId).collectLatest { countList ->
            updateState {
                it.copy(
                    idPT = itemIdPT,
                    countList = countList,
                    isLoading = false
                )
            }
        }
    }
}


sealed class AnimalCountIntent(

) : BaseIntent