package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto
import com.zaroslikov.domain.models.dto.add.DomainAddTemplateDto
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TemplateType
import com.zaroslikov.domain.models.enums.supportUi.ProductOperation
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.domain.repository.template.AddTemplateRepository
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
    private val addTemplateRepository: AddTemplateRepository,
    private val yandexMetricRepository: YandexMetricRepository
) : EntryNewViewModel2<AddListState, AddListIntent, AddListReduce>(
    AddListState(),
    AddListReduce(resourceProvider)
) {
    private val _itemIdPT: Long = checkNotNull(savedStateHandle[HomeDestination.itemIdArg])

    init {
        loadData()
    }

    override fun onIntent(intent: AddListIntent) {
        sendIntent(intent)
        when (intent) {
            is AddListIntent.OpenBottomSheetGroup -> openBottomSheetGroup(title = intent.title)
            is AddListIntent.OpenBottomSheetEntry -> loadDataForEntryOrEdit(
                intent.isOpen,
                intent.id,
                intent.isSaveStateForBottomSheet,
                intent.isTemplate
            )

            is AddListIntent.OpenPatternsBottomSheetClick -> loadDataForTemplateBottomSheet(intent.value)

            is AddListIntent.TitleChanged -> updateWarehouseUiState(intent.value)
            is AddListIntent.TitleAndSuffix -> updateWarehouseUiState(intent.pair.first)
            AddListIntent.Insert -> insert()
            AddListIntent.Update -> update()
            is AddListIntent.Delete -> delete(0)
            is AddListIntent.InsertTemplate -> insertTemplate()
            is AddListIntent.UpdateTemplate -> updateTemplate()
            is AddListIntent.DeleteTemplate -> deleteTemplate(0)
            else -> Unit
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val isArchive = projectRepository.getIsArchiveProject(_itemIdPT).first()
            combine(
                addRepository.getAllItems(_itemIdPT),
                settingsRepository.getSettings(_itemIdPT)
            ) { addList, settings ->
                val brieflyList = brieflyList(addList, settings)
                Triple(addList, brieflyList, settings)
            }.collectLatest { (addList, briefly, settings) ->
                val currentDetail = getState().currentDetail
                updateState { state ->
                    state.copy(
                        idPT = _itemIdPT,
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
        return addRepository.getBrieflyDetailsItemAdd(_itemIdPT, name).first()
    }

    private fun loadDataForEntryOrEdit(
        isOpen: Boolean,
        id: Long?,
        isSaveStateForBottomSheet: Boolean = false,
        isTemplate: Boolean = false
    ) {
        viewModelScope.launch {
            if (!isOpen) {
                val state =
                    if (isSaveStateForBottomSheet) getState().currentProduct
                    else AddEntryState2()
                sendIntent(
                    AddListIntent.RefreshEntryBottomSheetState(
                        false, state, isSaveStateForBottomSheet, false
                    )
                )
                return@launch
            }
            val newState = if (!getState().isSaveStateForBottomSheet || id != null) {
                val titleDeferred =
                    async { addRepository.getItemsTitleAddList(_itemIdPT).first() }
                val categoryDeferred =
                    async { addRepository.getItemsCategoryAddList(_itemIdPT).first() }
                val animalDeferred = async {
                    animalRepository.getItemsAnimalAddList(_itemIdPT).first()
                }

                val baseState = AddEntryState2(
                    itemIdPT = _itemIdPT,
                    category = resourceProvider.getString(R.string.support_text_no_category),
                    pickList = PickList(
                        titleList = titleDeferred.await(),
                        categoryList = categoryDeferred.await(),
                        animalList = animalDeferred.await()
                    )
                )
                when {
                    isTemplate && id == null -> baseState

                    isTemplate && id != null -> {
                        val template = addTemplateRepository.getAddTemplateItem(id).first()
                        baseState.toUiMap23(template)
                    }

                    id == null -> baseState

                    else -> {
                        val addItem = addRepository.getItem(id).first()
                        val editState = baseState.toUiMap22(addItem)
                        val animal = editState.animalId
                            ?.let { addRepository.getAnimalById(it).first() }

                        animal?.let { editState.copy(animal = it) } ?: editState
                    }
                }
            } else getState().currentProduct
            updateWarehouseUiState(newState.title)
            sendIntent(
                AddListIntent.RefreshEntryBottomSheetState(
                    true,
                    newState,
                    isTemplate = isTemplate
                )
            )
        }
    }

    private fun updateWarehouseUiState(name: String) {
        viewModelScope.launch {
            updateWarehouseUiStateSync(name)
        }
    }

    private fun loadDataForTemplateBottomSheet(
        isOpen: Boolean
    ) {
        viewModelScope.launch {
            if (!isOpen) return@launch
            val templateList = addTemplateRepository.getAllAddTemplateItems(_itemIdPT)
                .first().map { it.toTemplateItemUi() }
            sendIntent(AddListIntent.LoadDataForTemplate(templateList))
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String) {
        val pair = warehouseRepository
            .getCurrentBalanceProductList(name, _itemIdPT).first()
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

    fun insertTemplate() {
        viewModelScope.launch {
            addTemplateRepository.insert(getState().currentProduct.toDomainTemplate())
            yandexMetricRepository.metricAdd(getState().currentProduct)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun update() {
        viewModelScope.launch {
            addRepository.updateAdd(getState().currentProduct.toDomainMap())
            showSnackbar(ProductOperation.EDIT)
            loadDataForEntryOrEdit(false, null)
        }
    }

    fun updateTemplate() {
        viewModelScope.launch {
            addTemplateRepository.update(getState().currentProduct.toDomainTemplate())
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

    fun deleteTemplate(id: Long) {
        viewModelScope.launch {
            getState().currentDetail?.let { product ->
                addRepository.deleteAddById(product.id)
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

    private fun AddEntryState2.toUiMap22(domain: DomainAddTable): AddEntryState2 {
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
            category = domain.category
                ?: resourceProvider.getString(R.string.support_text_no_category),
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

    private fun AddEntryState2.toUiMap23(domain: DomainTemplateTable): AddEntryState2 {
        return copy(
            itemId = domain.id,
            nameTemplate = domain.nameTemplate,
            title = domain.title ?: "",
            count = domain.count?.formatNumber(false) ?: "",
            countSuffix = domain.countSuffix ?: Suffix.PIECES,
            category = domain.category
                ?: resourceProvider.getString(R.string.support_text_no_category),
            selectedAnimalIndex = domain.animalId ?: 0,
            animalId = domain.animalId,
            note = domain.note ?: "",
            itemIdPT = domain.idPT,
            isEntry = false,
            isTemplate = true,
            templateEntryState = TemplateEntryState(
                isTitle = domain.title == null,
                isCount = domain.count == null,
                isSuffix = domain.countSuffix == null,
                isCategory = domain.category == null,
                isAnimal = domain.animalId == null,
                isNote = domain.note == null

            ),
            error = ErrorAdd()
        )
    }

    private fun AddEntryState2.toDomainMap(): DomainAddTable {
        val dateList = date.split(".")
        val category = category.trim()
        return DomainAddTable(
            id = itemId,
            title = title.trim(),
            count = count.toConvertDbDouble(),
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            countSuffix = countSuffix,
            priceSuffix = getState().settings.currencySuffix,
            category = if (category.contains(resourceProvider.getString(R.string.support_text_no_category)) || category.isEmpty())
                null else category,
            animalId = animalId,
            note = note.trim(),
            price = 0.0,
            idPT = itemIdPT,
            animalCountId = animalCountId
        )
    }

    private fun AddEntryState2.toDomainTemplate(): DomainTemplateTable {
        val category = category.trim()
        return DomainTemplateTable(
            id = itemId,
            templateType = TemplateType.ADD,
            nameTemplate = nameTemplate.trim(),
            title = if (templateEntryState.isTitle) null else title.trim(),
            count = if (templateEntryState.isCount) null else count.toConvertDbDouble(),
            countSuffix = if (templateEntryState.isSuffix) null else countSuffix,
            priceSuffix = getState().settings.currencySuffix,
            category = if (templateEntryState.isCategory) null else
                if (category.contains(resourceProvider.getString(R.string.support_text_no_category)) || category.isEmpty())
                    null else category,
            animalId = if (templateEntryState.isAnimal) null else animalId,
            note = if (templateEntryState.isNote) null else note.trim(),
            price = 0.0,
            idPT = _itemIdPT,
        )
    }

    private fun DomainAddTemplateDto.toTemplateItemUi(): TemplateItem {
        val description = listOfNotNull(
            title?.takeIf { it.isNotBlank() },
            count?.formatNumber(),
            countSuffix?.let { resourceProvider.getString(it.toResId()) },
            category?.takeIf { it.isNotBlank() },
            nameAnimal?.takeIf { it.isNotBlank() },
            note?.takeIf { it.isNotBlank() }
        ).joinToString(" · ")
        return TemplateItem(
            id = id,
            nameTemplate = nameTemplate,
            description = description
        )
    }
}