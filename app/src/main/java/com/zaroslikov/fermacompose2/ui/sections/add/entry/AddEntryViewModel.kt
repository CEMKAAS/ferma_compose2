package com.zaroslikov.fermacompose2.ui.sections.add.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryViewModel
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addRepository: AddRepository,
    private val animalRepository: AnimalRepository,
    private val warehouseRepository: WarehouseRepository,
    private val resourceProvider: ResourceProvider,
) : EntryViewModel<AddEntryState, AddEntryIntent>(AddEntryState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[AddEntryDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[AddEntryDestination.itemId])
    private val isEntry: Boolean = itemId == -1L

    override fun onIntent(intent: AddEntryIntent) {
        when (intent) {
            is AddEntryIntent.TitleAndSuffix -> updateTitleAndSuffix(intent.pair)
            is AddEntryIntent.TitleChanged -> updateTitle(intent.value)
            is AddEntryIntent.CountChanged -> updateCount(intent.value)
            is AddEntryIntent.SuffixClicked -> updateState { it.copy(countSuffix = intent.value) }
            is AddEntryIntent.CategoryChanged -> updateState { it.copy(category = intent.value) }
            is AddEntryIntent.Date -> updateState { it.copy(date = intent.value) }
            is AddEntryIntent.NoteChanged -> updateState { it.copy(note = intent.value) }
            is AddEntryIntent.Animal -> updateAnimal(intent.animal)
            is AddEntryIntent.AnimalClear -> updateAnimalClear(intent.value)
            is AddEntryIntent.AnimalNameById -> updateState { it.copy(animal = intent.value) }
            is AddEntryIntent.CountWarehouse -> updateState { it.copy(warehouseList = intent.value) }
            AddEntryIntent.Insert -> insert()
            AddEntryIntent.Update -> update()
            AddEntryIntent.Delete -> delete()
        }
    }

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {

            val titleList = addRepository.getItemsTitleAddList(itemIdPT).first()
            val categoryList = addRepository.getItemsCategoryAddList(itemIdPT).first()
            val animalList = animalRepository.getItemsAnimalAddList(itemIdPT).first()

            updateState {
                it.copy(
                    isEntry = isEntry,
                    category = resourceProvider.getString(R.string.support_text_no_category),
                    countSuffix = Suffix.PIECES,
                    pickList = it.pickList.copy(
                        titleList = titleList,
                        categoryList = categoryList,
                        animalList = animalList
                    ),
                )
            }
            if (!isEntry) {
                val domainAddTable = addRepository.getItemAdd(itemId)
                    .filterNotNull()
                    .first()

                updateState { it.toUiMap(domainAddTable) }

                updateWarehouseUiStateSync(getState().title)

                val currentAnimalId = getState().animalId
                if (currentAnimalId != null) {
                    val animal = addRepository.getAnimalById(currentAnimalId).first()
                    updateState { it.copy(animal = animal) }
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
        val pair = warehouseRepository
            .getCurrentBalanceProductList(name, itemIdPT.toLong())
            .filterNotNull()
            .firstOrNull()

        if (pair != null)
            updateState { it.copy(warehouseList = pair) }
    }

    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {
                addRepository.insertAdd(
                    getState().toDomainMap(itemIdPT = itemIdPT.toLong())
                )
//                metricAdd(addUiState)
                navigateTo(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_add_s)
                        .format(getState().title, getState().count, getState().countSuffix)
                )
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                addRepository.updateAdd(
                    getState().toDomainMap(id = itemId.toLong(), itemIdPT = itemIdPT.toLong())
                )
                navigateTo(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_refresh_s)
                        .format(getState().title, getState().count, getState().countSuffix)
                )
            }
        }
    }

    override fun delete() {
        viewModelScope.launch {
            addRepository.deleteAddById(itemId.toLong())
            navigateTo(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(getState().title, getState().count, getState().countSuffix)
            )
        }
    }

    private fun updateTitleAndSuffix(pair: Pair<String, Suffix>) {
        updateState {
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
        updateState {
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
        updateState {
            it.copy(
                count = count,
                error = it.error.copy(isErrorCount = count.isBlank())
            )
        }
    }

    private fun updateAnimal(animal: Pair<Long, String>) {
        updateState {
            it.copy(
                selectedAnimalIndex = animal.first,
                animalId = animal.first,
                animal = animal.second
            )
        }
    }

    private fun updateAnimalClear(animal: String) {
        updateState {
            it.copy(
                animalId = 0,
                animal = animal
            )
        }
    }

    override fun validation() {
        updateState { state ->
            state.copy(
                error = state.error.copy(
                    isErrorTitle = state.title.isBlank(),
                    isErrorSlash = state.title.isSlash(),
                    isErrorCount = state.count.isBlank(),
                )
            )
        }
    }

    fun AddEntryState.toUiMap(domain: DomainAddTable): AddEntryState {
        return copy(
            title = domain.title,
            count = domain.count.formatNumber(false),
            date = formatDateToString(
                domain.day,
                domain.month,
                domain.year
            ),
            countSuffix = domain.countSuffix,
            category = domain.category,
            selectedAnimalIndex = domain.animalId ?: 0,
            animalId = domain.animalId,
            note = domain.note
        )
    }

    fun AddEntryState.toDomainMap(
        id: Long = 0,
        itemIdPT: Long
    ): DomainAddTable {
        val dateList = date.split(".")
        return DomainAddTable(
            id = id,
            title = title.trim(),
            count = count.toConvertDbDouble(),
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            countSuffix = countSuffix,
            category = category.trim(),
            animalId = animalId,
            note = note.trim(),
            price = 0.0,
            idPT = itemIdPT
        )
    }
}


sealed class AddEntryIntent : BaseIntent {
    data class TitleChanged(val value: String) : AddEntryIntent()
    data class TitleAndSuffix(val pair: Pair<String, Suffix>) : AddEntryIntent()
    data class CountChanged(val value: String) : AddEntryIntent()
    data class SuffixClicked(val value: Suffix) : AddEntryIntent()
    data class CountWarehouse(val value: List<DomainCountSuffix>) : AddEntryIntent()
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

