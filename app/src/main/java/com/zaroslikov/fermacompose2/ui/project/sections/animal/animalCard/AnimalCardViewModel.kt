package com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalSizeRepository
import com.zaroslikov.domain.repository.AnimalVaccinationRepository
import com.zaroslikov.domain.repository.AnimalWeightRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
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
) : BaseViewModel2<AnimalCardState, AnimalCardIntent, AnimalCardReduce>(
    AnimalCardState(), AnimalCardReduce()
) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalCardDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalCardDestination.itemId])

    init {
        viewModelScope.launch {
            loadData()
        }
    }

    fun onIntent(intent: AnimalCardIntent) {
        when (intent) {
            AnimalCardIntent.ArchiveAnimalPressed -> updateArchive()
            is AnimalCardIntent.NoteChanged -> updateNote(intent.value)
            else -> Unit
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
            LoadDataAnimalCard(animal, count, size, vacc, weight)
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
            sendIntent(AnimalCardIntent.NoteChanged(note))
            animalRepository.updateAnimalTable(getState().animal)
        }
    }

    private fun updateArchive() {
        viewModelScope.launch {
            sendIntent(AnimalCardIntent.ArchiveAnimalPressed)
            animalRepository.updateAnimalTable(getState().animal)
        }
    }
}