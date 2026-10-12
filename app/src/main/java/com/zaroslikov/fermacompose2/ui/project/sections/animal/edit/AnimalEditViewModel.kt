package com.zaroslikov.fermacompose2.ui.project.sections.animal.edit


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.animal.breed.DomainAnimalBreed
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.repository.AnimalPregnancyRepository
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository,
    private val animalCountRepository: AnimalCountRepository,
    private val pregnancyRepository: AnimalPregnancyRepository,
    private val resourceProvider: ResourceProvider
) : EntryNewViewModel2<AnimalEditState, AnimalEditIntent, AnimalEditReduce>(
    AnimalEditState(),
    AnimalEditReduce()
) {
    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalEditDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalEditDestination.itemId])


    init {
        loadDataUpdate()
    }


    override fun onIntent(intent: AnimalEditIntent) {
        sendIntent(intent)
        when (intent) {
            AnimalEditIntent.Update -> update()
            else -> Unit
        }
    }

    fun loadDataUpdate() {
        viewModelScope.launch {
            val typeList = animalRepository.getTypeAnimal(itemIdPT).first()
            val breedList = animalRepository.getBreedAnimal(itemIdPT).first()
            val domainAnimalTable = animalRepository.getAnimal(itemId).first()
            // Поголовье группы — чтобы самок и самцов не оказалось больше
            val headCount = animalCountRepository.getCountAnimalLimit(itemId).first()
                ?.count?.replace(',', '.')?.toDoubleOrNull() ?: 0.0
            // Активная беременность фиксирует пол и вид самки
            val lockedSpecies = pregnancyRepository.getActivePregnancyId(itemId)
                ?.let { pregnancyRepository.getPregnancy(it) }
                ?.species
            onIntent(
                AnimalEditIntent.LoadDate(
                    domainAnimalTable.toUi(typeList, breedList)
                        .copy(pregnancyLockedSpecies = lockedSpecies, headCount = headCount)
                )
            )
            onIntent(AnimalEditIntent.LoadingChanged(false))
        }
    }

    override fun update() {
        if (getState().currentProduct.error.isErrorPregnancyType) {
            showMessage(resourceProvider.getString(R.string.pregnancy_finish_first))
            return
        }
        viewModelScope.launch {
            animalRepository.updateAnimalTable(getState().currentProduct.toDomain())
            showMessage(
                resourceProvider.getString(if (getState().currentProduct.isAnimalGroup) R.string.snackbar_animal_update_multiple else R.string.snackbar_animal_update_single)
            )
            navigateTo(UiEvent.NavigateBack)
        }
    }

    private fun DomainAnimalTable.toUi(typeList: List<String>, breedList: List<DomainAnimalBreed>): AnimalUi {
        return AnimalUi(
            id = id,
            name = name,
            type = type,
            breed = breed.orEmpty(),
            isDateFactory = dateFactory.isNullOrBlank(),
            dateBorn = date,
            dateFactory = dateFactory?.takeIf { it.isNotBlank() } ?: date,
            isAnimalGroup = group,
            sex = sex,
            note = note,
            foodDay = foodDay.formatNumber(),
            foodDaySuffix = foodDaySuffix,
            archive = archive,
            idPT = idPT,
            imagePath = imagePath,
            currentIcon = currentIcon ?: AppIcon.PETS,
            femalesCount = if (group && femalesCount > 0) femalesCount.toString() else "",
            malesCount = if (group && malesCount > 0) malesCount.toString() else "",
            pickList = PickAnimalEditList(
                typeList = typeList,
                breedList = breedList
            )
        )
    }

    private fun AnimalUi.toDomain(): DomainAnimalTable {
        return DomainAnimalTable(
            id = id,
            name = name.trim(),
            type = type.trim(),
            breed = breed.trim().ifEmpty { null },
            species = AnimalSpecies.resolve(type),
            date = dateBorn,
            dateFactory = if (isDateFactory) null else dateFactory,
            group = isAnimalGroup,
            sex = sex,
            note = note.trim(),
            archive = archive,
            foodDay = if (foodDay.isBlank()) 0.0 else foodDay.toConvertDbDouble(),
            foodDaySuffix = foodDaySuffix,
            idPT = idPT,
            imagePath = imagePath,
            currentIcon = if (imagePath != null) null else currentIcon,
            femalesCount = if (isAnimalGroup) femalesCount.toIntOrNull() ?: 0 else 0,
            malesCount = if (isAnimalGroup) malesCount.toIntOrNull() ?: 0 else 0
        )
    }

    override fun insert() {}
    override fun delete(id: Long) {}
}