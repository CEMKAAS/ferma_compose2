package com.zaroslikov.fermacompose2.ui.project.sections.animal.edit


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.ui.formatNumber
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
            val domainAnimalTable = animalRepository.getAnimal(itemId).first()
            onIntent(
                AnimalEditIntent.LoadDate(domainAnimalTable.toUi(typeList))
            )
            onIntent(AnimalEditIntent.LoadingChanged(false))
        }
    }

    override fun update() {
        viewModelScope.launch {
            animalRepository.updateAnimalTable(getState().currentProduct.toDomain())
            navigateTo(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_refresh_s_s)
                    .format(
                        getState().currentProduct.name,
                        getState().currentProduct.type,
                        getState().currentProduct.note
                    )
            )
        }
    }

    private fun DomainAnimalTable.toUi(typeList: List<String>): AnimalUi {
        return AnimalUi(
            id = id,
            name = name,
            type = type,
            isDateFactory = dateFactory == null,
            dateBorn = date,
            dateFactory = dateFactory ?: date,
            isAnimalGroup = group,
            sex = sex,
            note = note,
            foodDay = foodDay.formatNumber(),
            foodDaySuffix = foodDaySuffix,
            image = image,
            archive = archive,
            idPT = idPT,
            pickList = PickAnimalEditList(
                typeList = typeList
            )
        )
    }

    private fun AnimalUi.toDomain(): DomainAnimalTable {
        return DomainAnimalTable(
            id = id,
            name = name,
            type = type,
            date = dateBorn,
            dateFactory = if (isDateFactory) null else dateFactory,
            group = isAnimalGroup,
            sex = sex,
            note = note,
            image = null,
            archive = archive,
            foodDay = if (foodDay.isBlank()) 0.0 else foodDay.toConvertDbDouble(),
            foodDaySuffix = foodDaySuffix,
            idPT = idPT
        )
    }

    override fun insert() {}
    override fun delete(id: Long) {}
}