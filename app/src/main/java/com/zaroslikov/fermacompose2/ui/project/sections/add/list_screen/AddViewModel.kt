package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.HomeDestination
import com.zaroslikov.fermacompose2.ui.project.sections.mapperToBrieflyItem
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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
    private val resourceProvider: ResourceProvider,
    private val settingsRepository: SettingsRepository,
) : EntryNewViewModel2<AddListState, AddListIntent, AddListReduce>(
    AddListState(),
    AddListReduce(resourceProvider)
) {
    private val itemIdPT: Long = checkNotNull(savedStateHandle[HomeDestination.itemIdArg])

    init {
        loadData()
    }

    override fun onIntent(intent: AddListIntent) {
        sendIntent(intent)
        when (intent) {
            is AddListIntent.OpenBottomSheetGroup -> openBottomSheetGroup(title = intent.title)

            is AddListIntent.OpenBottomSheetEntry -> loadDataForEntryOrEdit(
                intent.isOpen,
                intent.state,
                intent.isSaveStateForBottomSheet
            )

            is AddListIntent.TitleChanged -> updateWarehouseUiState(intent.value)
            is AddListIntent.TitleAndSuffix -> updateWarehouseUiState(intent.pair.first)
            AddListIntent.Insert -> insert()
            AddListIntent.Update -> update()
            is AddListIntent.Delete -> delete(intent.value)
            else -> Unit
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                addRepository.getAllItems(itemIdPT),
                settingsRepository.getSettings(itemIdPT)
            ) { addList, settings ->
                val brieflyList = brieflyList(addList, settings)
                Triple(addList, brieflyList, settings)
            }.collectLatest { (addList, briefly, settings) ->
                val currentDetail = getState().currentDetail
                updateState { state ->
                    state.copy(
                        idPT = itemIdPT,
                        list = addList,
                        searchList = addList,
                        briefly = briefly,
                        settings = settings,
                        searchBrieflyList = briefly,
                        currentDetail = currentDetail?.let { detail ->
                            addList.find { it.id == detail.id }
                        },
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun brieflyList(
        list: List<DomainAddItemDto>,
        settings: DomainSettings
    ): List<BrieflyItem> {
        return list
            .groupBy { it.title }
            .map { (title, items) ->
                mapperToBrieflyItem(title, items, settings = settings)
            }
    }

    private fun openBottomSheetGroup(title: String?) {
        viewModelScope.launch {
            if (title == null) {
                updateState { state ->
                    state.copy(openBottomSheetGroup = false)
                }
                return@launch
            }
            val listBriefly = getDetailsName(name = title)
            val currentBriefly =
                mapperToBrieflyItem(title, listBriefly, settings = getState().settings)
            updateState {
                it.copy(
                    openBottomSheetGroup = true,
                    currentBriefly = currentBriefly,
                    listBriefly = listBriefly
                )
            }
        }
    }

    private suspend fun getDetailsName(name: String): List<DomainAddItemDto> {
        return addRepository.getBrieflyDetailsItemAdd(itemIdPT, name).first()
    }

    private fun loadDataForEntryOrEdit(
        isOpen: Boolean,
        domain: DomainAddItemDto?,
        isSaveStateForBottomSheet: Boolean = false
    ) {
        viewModelScope.launch {
            if (!isOpen) {
                val state =
                    if (isSaveStateForBottomSheet) getState().currentProduct
                    else AddEntryState2()
                onIntent(
                    AddListIntent.RefreshEntryBottomSheetState(
                        false, state, isSaveStateForBottomSheet
                    )
                )
                return@launch
            }
            val newState = if (!getState().isSaveStateForBottomSheet || domain != null) {
                val titleDeferred =
                    async { addRepository.getItemsTitleAddList(itemIdPT).first() }
                val categoryDeferred =
                    async { addRepository.getItemsCategoryAddList(itemIdPT).first() }
                val animalDeferred = async {
                    animalRepository.getItemsAnimalAddList(itemIdPT).first()
                }

                val baseState = AddEntryState2(
                    itemIdPT = itemIdPT,
                    category = resourceProvider.getString(R.string.support_text_no_category),
                    pickList = PickList(
                        titleList = titleDeferred.await(),
                        categoryList = categoryDeferred.await(),
                        animalList = animalDeferred.await()
                    )
                )
                if (domain == null) baseState
                else {
                    val editState = baseState.toUiMap22(domain)
                    val animal = editState.animalId
                        ?.let { addRepository.getAnimalById(it).first() }
                    animal?.let { editState.copy(animal = it) } ?: editState
                }
            } else getState().currentProduct
            updateWarehouseUiState(newState.title)
            onIntent(AddListIntent.RefreshEntryBottomSheetState(true, newState))
        }
    }

    private fun updateWarehouseUiState(name: String) {
        viewModelScope.launch {
            updateWarehouseUiStateSync(name)
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String) {
        val pair = warehouseRepository
            .getCurrentBalanceProductList(name, itemIdPT)
            .filterNotNull()
            .firstOrNull()
        pair?.let {
            onIntent(AddListIntent.RefreshWarehouseCount(pair))
        }
    }

    override fun insert() {
        viewModelScope.launch {
            addRepository.insertAdd(getState().currentProduct.toDomainMap())
            loadDataForEntryOrEdit(false, null)
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

    override fun update() {
        viewModelScope.launch {
            addRepository.updateAdd(getState().currentProduct.toDomainMap())
            loadDataForEntryOrEdit(false, null)
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

    private fun AddEntryState2.toUiMap22(domain: DomainAddItemDto): AddEntryState2 {
        val isIndicatorsValue = domain.animalCountId != null
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
            animalCountId = domain.animalCountId,
            isIndicatorsValue = isIndicatorsValue,
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
            idPT = itemIdPT,
            animalCountId = animalCountId
        )
    }
}