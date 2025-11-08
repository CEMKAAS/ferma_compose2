package com.zaroslikov.fermacompose2.ui.sections.add.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.dto.add.BrieflyAddDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.sections.HomeDestination
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addRepository: AddRepository,
    private val animalRepository: AnimalRepository,
    private val warehouseRepository: WarehouseRepository,
    private val resourceProvider: ResourceProvider
) : EntryNewViewModel<AddListState, AddListIntent>(AddListState()) {
    private val itemIdPT: Long = checkNotNull(savedStateHandle[HomeDestination.itemIdArg])

    init {
        loadData()
    }

    override fun onIntent(intent: AddListIntent) {
        when (intent) {
            is AddListIntent.OpenBottomSheetGroup -> openBottomSheetGroup(
                openBottomSheetGroup = intent.value,
                currentBriefly = intent.currentBriefly
            )

            is AddListIntent.OpenBottomSheetEntry -> openBottomSheetEntry(
                openBottomSheetEntry = intent.value,
                domainAddTable = intent.item
            )

            is AddListIntent.TitleAndSuffix -> updateTitleAndSuffix(intent.pair)
            is AddListIntent.TitleChanged -> updateTitle(intent.value)
            is AddListIntent.CountChanged -> updateCount(intent.value)
            is AddListIntent.SuffixClicked -> updateSuffix(intent.value)
            is AddListIntent.CategoryChanged -> updateCategory(intent.value)
            is AddListIntent.Date -> updateDate(intent.value)
            is AddListIntent.NoteChanged -> updateNote(intent.value)
            is AddListIntent.Animal -> updateAnimal(intent.animal)
            is AddListIntent.AnimalClear -> updateAnimalClear(intent.value)
            is AddListIntent.AnimalNameById -> updateAnimal(intent.value)
            is AddListIntent.CountWarehouse -> updateWarehouse(intent.value)
            AddListIntent.Insert -> insert()
            AddListIntent.Update -> update()
            is AddListIntent.Delete -> delete(intent.value)
            is AddListIntent.SearchChanged -> updateSearch(intent.value)
            is AddListIntent.GroupClicked -> updateGroup(intent.value)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                addRepository.getAllItems(itemIdPT),
                addRepository.getBrieflyItemAdd(itemIdPT)
            ) { addList, briefly ->
                addList to briefly
            }.collectLatest { (addList, briefly) ->
                updateState {
                    it.copy(
                        idPT = itemIdPT,
                        list = addList,
                        briefly = briefly,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun openBottomSheetGroup(
        openBottomSheetGroup: Boolean,
        currentBriefly: BrieflyAddDomain
    ) {
        viewModelScope.launch {
            val listBriefly = getDetailsName(name = currentBriefly.title)
            updateState {
                it.copy(
                    openBottomSheetGroup = openBottomSheetGroup,
                    currentBriefly = currentBriefly,
                    listBriefly = listBriefly
                )
            }
        }
    }

    private suspend fun getDetailsName(name: String): List<DomainAddTable> {
        return addRepository.getBrieflyDetailsItemAdd(itemIdPT, name).first()
    }

    private fun openBottomSheetEntry(
        openBottomSheetEntry: Boolean,
        domainAddTable: DomainAddTable?
    ) {
        if (openBottomSheetEntry)
            viewModelScope.launch {
                val titleList = addRepository.getItemsTitleAddList(itemIdPT).first()
                val categoryList = addRepository.getItemsCategoryAddList(itemIdPT).first()
                val animalList = animalRepository.getItemsAnimalAddList(itemIdPT).first()

                updateState {
                    it.copy(
                        openBottomSheetEntry = true,
                        currentProduct = AddEntryState2(
                            itemIdPT = itemIdPT,
                            category = resourceProvider.getString(R.string.support_text_no_category),
                            error = ErrorAdd(),
                            pickList = it.currentProduct.pickList.copy(
                                titleList = titleList,
                                categoryList = categoryList,
                                animalList = animalList
                            )
                        )
                    )
                }
                domainAddTable?.let {
                    updateState {
                        it.copy(
                            currentProduct = it.currentProduct.toUiMap22(
                                domainAddTable
                            )
                        )
                    }
                    updateWarehouseUiStateSync(getState().currentProduct.title)
                    val currentAnimalId = getState().currentProduct.animalId
                    if (currentAnimalId != null) {
                        val animal = addRepository.getAnimalById(currentAnimalId).first()
                        updateState {
                            it.copy(
                                currentProduct = it.currentProduct.copy(
                                    animal = animal
                                )
                            )
                        }
                    }
                }
            }
        else updateState { it.copy(openBottomSheetEntry = false) }
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
            updateState {
                it.copy(
                    currentProduct = it.currentProduct.copy(
                        warehouseList = pair
                    )
                )
            }
    }

    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {
                addRepository.insertAdd(
                    getState().currentProduct.toDomainMap()
                )
//                metricAdd(addUiState)
                openBottomSheetEntry(false, null)
                showMessage(
                    resourceProvider.getString(R.string.toast_add_s)
                        .format(
                            getState().currentProduct.title,
                            getState().currentProduct.count,
                            getState().currentProduct.countSuffix
                        )
                )
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                addRepository.updateAdd(
                    getState().currentProduct.toDomainMap()
                )
                openBottomSheetEntry(false, null)
                showMessage(
                    resourceProvider.getString(R.string.toast_refresh_s)
                        .format(
                            getState().currentProduct.title,
                            getState().currentProduct.count,
                            getState().currentProduct.countSuffix
                        )
                )
            }
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            addRepository.deleteAddById(id)
            navigateTo(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        getState().currentProduct.title,
                        getState().currentProduct.count,
                        getState().currentProduct.countSuffix
                    )
            )
        }
    }

    private fun updateGroup(isGroup: Boolean) {
        updateState {
            it.copy(
                isGroup = isGroup
            )
        }
    }

    private fun updateSuffix(suffix: Suffix) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(countSuffix = suffix)
            )
        }
    }

    private fun updateCategory(category: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(category = category)
            )
        }
    }

    private fun updateDate(date: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(date = date)
            )
        }
    }

    private fun updateNote(note: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(note = note)
            )
        }
    }

    private fun updateAnimal(animal: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(animal = animal)
            )
        }
    }

    private fun updateWarehouse(warehouseList: List<DomainCountSuffix>) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(warehouseList = warehouseList)
            )
        }
    }

    private fun updateSearch(search: String) {
        updateState {
            it.copy(
                textSearch = search
            )
        }
    }

    private fun updateTitleAndSuffix(pair: Pair<String, Suffix>) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    title = pair.first,
                    countSuffix = pair.second,
                    error = it.currentProduct.error.copy(
                        isErrorTitle = pair.first.isBlank(),
                        isErrorSlash = pair.first.contains("/")
                    )
                )
            )
        }
        updateWarehouseUiState(pair.first)
    }

    private fun updateTitle(title: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    title = title,
                    error = it.currentProduct.error.copy(
                        isErrorTitle = title.isBlank(),
                        isErrorSlash = title.contains("/")
                    )
                )
            )
        }
        updateWarehouseUiState(title)
    }

    private fun updateCount(count: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    count = count,
                    error = it.currentProduct.error.copy(isErrorCount = count.isBlank())
                )
            )
        }
    }

    private fun updateAnimal(animal: Pair<Long, String>) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    selectedAnimalIndex = animal.first,
                    animalId = animal.first,
                    animal = animal.second
                )
            )
        }
    }

    private fun updateAnimalClear(animal: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    animalId = 0,
                    animal = animal
                )
            )
        }
    }

    override fun validation() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    error = state.currentProduct.error.copy(
                        isErrorTitle = state.currentProduct.title.isBlank(),
                        isErrorSlash = state.currentProduct.title.isSlash(),
                        isErrorCount = state.currentProduct.count.isBlank(),
                    )
                )
            )
        }
    }

    private fun AddEntryState2.toUiMap22(domain: DomainAddTable): AddEntryState2 {
        return copy(
            itemId = domain.id,
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
            note = domain.note,
            itemIdPT = domain.idPT,
            isEntry = false,
            error = ErrorAdd()
        )
    }

    private fun AddEntryState2.toDomainMap(): DomainAddTable {
        val dateList = date.split(".")
        return DomainAddTable(
            id = itemId,
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

sealed class AddListIntent : BaseIntent {
    data class OpenBottomSheetGroup(
        val value: Boolean,
        val currentBriefly: BrieflyAddDomain = BrieflyAddDomain()
    ) : AddListIntent()

    data class OpenBottomSheetEntry(
        val value: Boolean,
        val item: DomainAddTable? = null
    ) : AddListIntent()

    data class GroupClicked(val value: Boolean) : AddListIntent()
    data class TitleChanged(val value: String) : AddListIntent()
    data class TitleAndSuffix(val pair: Pair<String, Suffix>) : AddListIntent()
    data class CountChanged(val value: String) : AddListIntent()
    data class SuffixClicked(val value: Suffix) : AddListIntent()
    data class CountWarehouse(val value: List<DomainCountSuffix>) : AddListIntent()
    data class CategoryChanged(val value: String) : AddListIntent()
    data class Date(val value: String) : AddListIntent()
    data class Animal(val animal: Pair<Long, String>) : AddListIntent()
    data class AnimalNameById(val value: String) : AddListIntent()
    data class AnimalClear(val value: String) : AddListIntent()
    data class NoteChanged(val value: String) : AddListIntent()
    data class SearchChanged(val value: String) : AddListIntent()
    data object Insert : AddListIntent()
    data object Update : AddListIntent()
    data class Delete(val value: Long) : AddListIntent()
}