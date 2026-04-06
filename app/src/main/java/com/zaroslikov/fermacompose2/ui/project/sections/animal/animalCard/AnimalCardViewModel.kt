package com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.dto.add.DomainAnimalCountSuffix
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalSizeRepository
import com.zaroslikov.domain.repository.AnimalVaccinationRepository
import com.zaroslikov.domain.repository.AnimalWeightRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel2
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.mapperToBrieflyItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.component1


@HiltViewModel
class AnimalCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository,
    private val animalSizeRepository: AnimalSizeRepository,
    private val animalCountRepository: AnimalCountRepository,
    private val animalWeightRepository: AnimalWeightRepository,
    private val animalVaccinationRepository: AnimalVaccinationRepository,
    private val addRepository: AddRepository,
    private val settingsRepository: SettingsRepository
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
        combine(
            animalRepository.getAnimal(itemId),
            animalCountRepository.getCountAnimalLimit(itemId),
            animalSizeRepository.getSizeAnimalLimit(itemId),
            animalVaccinationRepository.getVaccinationAnimalLimit(itemId),
            animalWeightRepository.getWeightAnimalLimit(itemId),
            addRepository.getProductAnimal(itemId),
            settingsRepository.getSettings(itemIdPT)
        ) { list ->
            val productList = productsGetFromAnimalList(
                list[5] as List<DomainAnimalCountSuffix>,
                list[6] as DomainSettings
            )

            LoadDataAnimalCard(
                list[0] as DomainAnimalTable,
                list[1] as DomainAnimalCount?, list[2] as DomainAnimalSize?,
                list[3] as DomainAnimalVaccination?, list[4] as DomainAnimalWeight?,
                productList,
                list[6] as DomainSettings
            )
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
                    productList = data.productList,
                    settings = data.settings
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

    private fun productsGetFromAnimalList(
        productList: List<DomainAnimalCountSuffix>,
        domainSettings: DomainSettings
    ): List<BrieflyItem> {
        return productList
            .groupBy { it.title }
            .map { (title, items) ->
                mapperToBrieflyItem(title, items, settings = domainSettings)
            }
    }
}