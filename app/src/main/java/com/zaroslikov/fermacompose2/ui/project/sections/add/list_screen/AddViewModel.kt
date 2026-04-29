package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.supportUi.ProductOperation
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.supportFun.conversation3
import com.zaroslikov.fermacompose2.supportFun.conversation4
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.HomeDestination
import com.zaroslikov.fermacompose2.ui.project.sections.mapperToBrieflyItem
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2

@HiltViewModel
class AddViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addRepository: AddRepository,
    private val animalRepository: AnimalRepository,
    private val warehouseRepository: WarehouseRepository,
    private val resourceProvider: ResourceProvider,
    private val settingsRepository: SettingsRepository,
    private val projectRepository: ProjectRepository,
    private val yandexMetricRepository: YandexMetricRepository
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
            is AddListIntent.Delete -> delete(0)
            else -> Unit
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val isArchive = projectRepository.getIsArchiveProject(itemIdPT).first()
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
                        isLoading = false,
                        isArchive = isArchive
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
            .getCurrentBalanceProductList(name, itemIdPT).first()
            .build(getState().settings)
        onIntent(AddListIntent.RefreshWarehouseCount(pair))
    }

    private fun List<DomainCountSuffix>.build(
        settings: DomainSettings
    ): List<DomainCountSuffix> {
        return this.groupBy { it.suffix.conversation4(settings) }
            .map { (suffix, items) ->
                val totalCount = items.sumOf {
                    it.count.conversation3(it.suffix, settings)
                }
                DomainCountSuffix(
                    count = totalCount,
                    suffix = suffix
                )
            }
    }

    override fun insert() {
        viewModelScope.launch {
            addRepository.insertAdd(getState().currentProduct.toDomainMap())
            yandexMetricRepository.metricAdd(getState().currentProduct)
            showSnackbar(ProductOperation.ADD)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun update() {
        viewModelScope.launch {
            addRepository.updateAdd(getState().currentProduct.toDomainMap())
            /*  if (getState().currentDetail != null)
                  updateState { it.copy(currentDetail = getState().currentProduct.toDomainMap()) }*/
            showSnackbar(ProductOperation.EDIT)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            getState().currentDetail?.let { product ->
                addRepository.deleteAddById(product.id)
                showSnackbar(ProductOperation.DELETE)
                sendIntent(AddListIntent.OpenBottomSheetDelete(null))
            }
        }
    }

    private fun showSnackbar(productOperation: ProductOperation) {
        val (title, count, countSuffix) =
            if (productOperation == ProductOperation.DELETE) {
                val product = getState().currentDetail ?: DomainAddItemDto()
                Triple(product.title, product.count.formatNumber(), product.countSuffix)
            } else {
                val product = getState().currentProduct
                Triple(product.title, product.count, product.countSuffix)
            }
        val suffix = resourceProvider.getString(countSuffix.toResId())
        showMessage(
            when (productOperation) {
                ProductOperation.ADD -> resourceProvider.getString(R.string.snackbar_product_add)
                    .format(title, count, suffix)

                ProductOperation.EDIT -> resourceProvider.getString(R.string.snackbar_product_update)
                    .format(title, count, suffix)

                else -> resourceProvider.getString(R.string.snackbar_product_delete)
                    .format(title, count, suffix)
            }
        )
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
            priceSuffix = getState().settings.currencySuffix,
            category = category.trim(),
            animalId = animalId,
            note = note.trim(),
            price = 0.0,
            idPT = itemIdPT,
            animalCountId = animalCountId
        )
    }
}