package com.zaroslikov.fermacompose2.ui.animal.animalCard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalSizeRepository
import com.zaroslikov.domain.repository.AnimalVaccinationRepository
import com.zaroslikov.domain.repository.AnimalWeightRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AnimalCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository,
    private val animalSizeRepository: AnimalSizeRepository,
    private val animalCountRepository: AnimalCountRepository,
    private val animalWeightRepository: AnimalWeightRepository,
    private val animalVaccinationRepository: AnimalVaccinationRepository,
    private val warehouseRepository: WarehouseRepository,
    private val resourceProvider: ResourceProvider,
    private val addRepository: AddRepository,
) : BaseViewModel<AnimalCardState, AnimalCardIntent>(AnimalCardState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalCardDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalCardDestination.itemId])

    init {
        viewModelScope.launch {
            loadData()
        }
    }

    fun onIntent(intent: AnimalCardIntent) {
        when (intent) {
            is AnimalCardIntent.NoteChanged -> updateNote(intent.value)
            is AnimalCardIntent.OpenArchiveDialogClicked -> updateOpenArchiveDialog(intent.value)
            AnimalCardIntent.ArchiveAnimalPressed -> updateArchive()
        }
    }

    private suspend fun loadData() {
        val productList = addRepository.getProductAnimal(itemId).first()
        combine(
            animalRepository.getAnimal(itemId),
            animalCountRepository.getCountAnimalLimit(itemId),
            animalSizeRepository.getSizeAnimalLimit(itemId),
            animalVaccinationRepository.getVaccinationAnimalLimit(itemId),
            animalWeightRepository.getWeightAnimalLimit(itemId)
        ) { animal, count, size, vacc, weight ->
            AnimalData(animal, count, size, vacc, weight)
        }.collectLatest { data ->
            updateState { animal ->
                animal.copy(
                    isLoading = false,
                    animal = data.animal,
                    countAnimal = data.count,
                    size = data.size,
                    vaccination = data.vacc,
                    weight = data.weight,
                    itemId = itemId,
                    itemIdPT = itemIdPT,
                    productList = productList
                )
            }
        }
    }

    private fun updateNote(note: String) {
        viewModelScope.launch {
            updateState { state ->
                state.copy(
                    animal = state.animal.copy(
                        note = note
                    )
                )
            }
            animalRepository.updateAnimalTable(getState().animal)
        }
    }

    private fun updateOpenArchiveDialog(isOpenArchiveDialog: Boolean) {
        updateState { state ->
            state.copy(
                isOpenArchiveDialog = isOpenArchiveDialog
            )
        }
    }

    private fun updateArchive() {
        viewModelScope.launch {
            updateState { state ->
                state.copy(
                    animal = state.animal.copy(
                        archive = true
                    )
                )
            }
            animalRepository.updateAnimalTable(getState().animal)
        }
    }
}


sealed class AnimalCardIntent : BaseIntent {
    // Animal Note Animal
    data class NoteChanged(val value: String) : AnimalCardIntent()
    data class OpenArchiveDialogClicked(val value: Boolean) : AnimalCardIntent()
    data object ArchiveAnimalPressed : AnimalCardIntent()
}

data class AnimalData(
    val animal: DomainAnimalTable,
    val count: DomainAnimalCount,
    val size: DomainAnimalSize?,
    val vacc: DomainAnimalVaccination?,
    val weight: DomainAnimalWeight?
)