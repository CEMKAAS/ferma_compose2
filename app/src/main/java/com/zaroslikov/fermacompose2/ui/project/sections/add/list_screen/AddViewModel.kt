package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto2
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
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel3
import com.zaroslikov.fermacompose2.utils.QrGenerator
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.supportFun.build
import com.zaroslikov.fermacompose2.supportFun.conversation3
import com.zaroslikov.fermacompose2.supportFun.conversation4
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningType
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.HomeDestination
import com.zaroslikov.fermacompose2.ui.project.sections.mapperToBrieflyItem
import com.zaroslikov.fermacompose2.utils.QrNavigationManager
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    private val qrGenerator: QrGenerator,
    private val addRepository: AddRepository,
    private val resourceProvider: ResourceProvider,
    private val animalRepository: AnimalRepository,
    private val qrNavigationManager: QrNavigationManager,
    private val warehouseRepository: WarehouseRepository,
    private val settingsRepository: SettingsRepository,
    private val projectRepository: ProjectRepository,
    private val addTemplateRepository: AddTemplateRepository,
    private val yandexMetricRepository: YandexMetricRepository
) : EntryNewViewModel3<AddListState, AddListIntent, AddListReduce>(
    initialState = AddListState(),
    reducer = AddListReduce(resourceProvider),
    qrGenerator = qrGenerator,
    resourceProvider = resourceProvider,
    projectRepository = projectRepository,
    qrNavigationManager = qrNavigationManager,
    addTemplateRepository = addTemplateRepository
) {
    private val _itemIdPT: Long = checkNotNull(savedStateHandle[HomeDestination.itemIdArg])

    init {
        loadData()
        loadTemplate()
    }

    override fun onIntent(intent: AddListIntent) {
        sendIntent(intent)
        when (intent) {

            is AddListIntent.LoadDataForDetailNomenclatura ->
                loadDataForDetailNomenclatura(intent.title)

            is AddListIntent.OpenBottomSheetEntry -> loadDataForEntryOrEdit(
                intent.isOpen,
                intent.id,
                intent.isSaveStateForBottomSheet,
                intent.isTemplate
            )


            is AddListIntent.LoadDataForTemplateBottomSheetClick ->
                loadDataForTemplateBottomSheet(intent.value)


            is AddListIntent.TitleChanged -> updateWarehouseUiState(intent.value)
            is AddListIntent.TitleAndSuffix -> updateWarehouseUiState(intent.pair.first)

            AddListIntent.Insert -> insert()
            AddListIntent.Update -> update()
            is AddListIntent.Delete -> delete()

            is AddListIntent.InsertTemplate -> insertTemplate()
            is AddListIntent.UpdateTemplate -> updateTemplate()
            is AddListIntent.DeleteTemplate -> deleteTemplate()

            is AddListIntent.QrDetected -> qrScanner(intent.value)
            is AddListIntent.CreateQrCodeClick -> generateQrCode(intent.value)
            is AddListIntent.SetPinOfTemplateClick -> setPin(intent.value)

            is AddListIntent.RecoverClick -> recover(intent.value)
            is AddListIntent.OpenPatternsBottomSheetClick ->
                loadDataForTemplatesBottomSheet(intent.value)

            else -> Unit
        }
    }

    override fun loadData() {
        viewModelScope.launch {
            val isArchive = projectRepository.getIsArchiveProject(_itemIdPT).first()
            combine(
                addRepository.getAllItems(_itemIdPT),
                settingsRepository.getSettings(_itemIdPT)
            ) { addList, settings ->
                val brieflyList = brieflyList(addList, settings)
                Triple(addList, brieflyList, settings)
            }.collectLatest { (addList, briefly, settings) ->
                sendIntent(
                    AddListIntent.LoadData(
                        _itemIdPT,
                        addList,
                        briefly,
                        settings,
                        false,
                        isArchive
                    )
                )
            }
        }
    }

    private fun loadTemplate() {
        val template = qrNavigationManager.consume()
        if (template != null) {
            when {
                template.isMultiProjectTemplate -> template.backupData?.let { recover(template.backupData) }
                else -> loadDataForTemplateBottomSheet(template.itemId, template)
            }
        }
    }


    private fun brieflyList(
        list: List<DomainAddItemDto2>,
        settings: DomainSettings
    ): List<BrieflyItem> {
        return list
            .groupBy { it.title }
            .map { (title, items) ->
                mapperToBrieflyItem(title, items, settings)
            }
    }

    private fun loadDataForDetailNomenclatura(title: String?) {
        viewModelScope.launch {
            if (title == null) return@launch sendIntent(AddListIntent.OpenBottomSheetGroup(false))
            val listBriefly = addRepository.getBrieflyDetailsItemAdd(_itemIdPT, title).first()
            val currentBriefly =
                mapperToBrieflyItem(title, listBriefly, getState().settings)
            sendIntent(AddListIntent.OpenBottomSheetGroup(true, currentBriefly, listBriefly))
        }
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
                    else AddProductState()
                sendIntent(
                    AddListIntent.RefreshEntryBottomSheetState(
                        false, state, isSaveStateForBottomSheet, false
                    )
                )
                return@launch
            }
            val newState =
                if (!getState().bottomSheetState.isSaveStateForBottomSheet || id != null) {
                    val baseState = loadDataForPickList()
                    when {
                        isTemplate && id == null -> baseState

                        isTemplate && id != null -> {
                            val template =
                                addTemplateRepository.getAddTemplateItem(id).first()
                                    ?: return@launch
                            baseState.toUiMap23(template)
                            val animal = template.animalId
                                ?.let { addRepository.getAnimalById(it).first() }
                            animal?.let { baseState.copy(product = baseState.product.copy(animalName = it)) }
                                ?: baseState
                        }

                        id == null -> baseState

                        else -> {
                            val addItem = addRepository.getItem(id).first()
                            baseState.toUiMap22(addItem)
                        }
                    }
                } else getState().currentProduct
            updateWarehouseUiState(newState.product.title)
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

    private suspend fun loadDataForPickList(): AddProductState {
        return coroutineScope {
            val titleDeferred =
                async { addRepository.getItemsTitleAddList(_itemIdPT).first() }
            val categoryDeferred =
                async { addRepository.getItemsCategoryAddList(_itemIdPT).first() }
            val animalDeferred = async {
                animalRepository.getItemsAnimalAddList(_itemIdPT).first()
            }
            AddProductState(
                product = AddProduct(
                    projectId = _itemIdPT,
                    category = resourceProvider.getString(R.string.support_text_no_category)
                ),
                pickList = AddPickList(
                    titles = titleDeferred.await(),
                    categories = categoryDeferred.await(),
                    animals = animalDeferred.await()
                )
            )
        }
    }


    override fun loadDataForTemplatesBottomSheet(
        isOpen: Boolean
    ) {
        viewModelScope.launch {
            if (!isOpen) return@launch
            addTemplateRepository.getAllAddTemplateItems(_itemIdPT)
                .collectLatest { it ->
                    sendIntent(AddListIntent.LoadDataForTemplate(it.map { it.toTemplateItemUi() }))
                }
        }
    }

    private fun loadDataForTemplateBottomSheet(
        id: Long,
        qrPayload: QrPayload? = null
    ) {
        viewModelScope.launch {
            val template =
                addTemplateRepository.getAddTemplateItem(qrPayload?.itemId ?: id).first()
                    ?: return@launch sendIntent(
                        AddListIntent.OpenWarningQrCodeBottomSheetClick(
                            true,
                            QrCodeWarningType.LOCAL,
                            qrPayload?.backupData
                        )
                    )

            val baseState = loadDataForPickList()
            val currentProduct = baseState.toUiMap23(template, isTemplateEntry = true)
            sendIntent(AddListIntent.OpenTemplateBottomSheetClick(true, currentProduct))
            updateWarehouseUiStateSync(currentProduct.product.title)
        }
    }

    override fun recover(domainTemplateTable: DomainTemplateTable) {
        viewModelScope.launch {
            val baseState = loadDataForPickList()
            val currentProduct = baseState.toUiMap23(
                domainTemplateTable,
                isTemplateEntry = true,
                domainTemplateTable.isMultiProjectTemplate
            )
            sendIntent(
                AddListIntent.OpenTemplateBottomSheetClick(true, currentProduct)
            )
            updateWarehouseUiStateSync(currentProduct.product.title)
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String) {
        val pair = warehouseRepository
            .getCurrentBalanceProductList(name, _itemIdPT).first()
            .build(getState().settings)
        onIntent(AddListIntent.RefreshWarehouseCount(pair))
    }

    override fun insert() {
        viewModelScope.launch {
            val currentProduct = getState().currentProduct
            addRepository.insertAdd(currentProduct.toDomainMap())
            yandexMetricRepository.metricAdd(currentProduct)
            if (currentProduct.template.isTemplateEntry)
                sendIntent(AddListIntent.OpenTemplateBottomSheetClick(false))
            else {
                loadDataForEntryOrEdit(false, null)
                showSnackbar(ProductOperation.ADD)
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            addRepository.updateAdd(getState().currentProduct.toDomainMap())
            showSnackbar(ProductOperation.EDIT)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun delete() {
        viewModelScope.launch {
            getState().productDetail?.let { product ->
                addRepository.deleteAddById(product.id)
                showSnackbar(ProductOperation.DELETE)
                sendIntent(AddListIntent.OpenBottomSheetDelete(null))
            }
        }
    }

    override fun insertTemplate() {
        viewModelScope.launch {
            addTemplateRepository.insert(getState().currentProduct.toDomainTemplate())
            yandexMetricRepository.metricalTemplate(getState().currentProduct)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun updateTemplate() {
        viewModelScope.launch {
            addTemplateRepository.update(getState().currentProduct.toDomainTemplate())
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun deleteTemplate() {
        viewModelScope.launch {
            getState().templatesState.templateToDelete?.let { template ->
                addTemplateRepository.deleteAddTemplateItemById(template.id)
                sendIntent(AddListIntent.OpenTemplateDeleteBottomSheet(null))
            }
        }
    }

    private fun showSnackbar(productOperation: ProductOperation) {
        val (title, count, countSuffix) =
            if (productOperation == ProductOperation.DELETE) {
                val product = getState().productDetail ?: DomainAddItemDto()
                Triple(product.title, product.count.formatNumber(), product.countSuffix)
            } else {
                val product = getState().currentProduct.product
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

    override fun createOpenQrIntent(data: QrCodeData): AddListIntent {
        return AddListIntent.OpenQrCodeBottomSheetClick(true, data)
    }

    override fun createOpenQrWarningIntent(warning: QrCodeWarningType): AddListIntent {
        return AddListIntent.OpenWarningQrCodeBottomSheetClick(
            value = true,
            qrCodeWarningType = QrCodeWarningType.GLOBAL
        )
    }

    override suspend fun onQrPayloadReceived(payload: QrPayload) {
        if (payload.templateType == TemplateType.ADD && payload.idPT == _itemIdPT) {
            loadDataForTemplateBottomSheet(payload.itemId, payload)
        } else {
            qrNavigationManager.put(payload)
            navigateTo(UiEvent.Navigate(payload.idPT))
        }
    }

    private fun AddProductState.toUiMap22(domain: DomainAddItemDto): AddProductState {
        val isIndicatorsValue = domain.animalCountId != null
        return copy(
            product = product.copy(
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
                animalName = domain.nameAnimal ?: "",
                note = domain.note,
                projectId = domain.idPT,
                isEntry = false,
                animalCountId = domain.animalCountId,
                hasIndicators = isIndicatorsValue,
            ),
            errors = AddErrors()
        )
    }

    private fun AddProductState.toUiMap23(
        domain: DomainTemplateTable,
        isTemplateEntry: Boolean = false,
        isMultiProjectTemplate: Boolean = false
    ): AddProductState {

        val category =
            domain.category?.ifBlank { resourceProvider.getString(R.string.support_text_no_category) }
                ?: ""

        return copy(
            product = product.copy(
                itemId = if (isTemplateEntry) product.itemId else domain.id,
                title = domain.title ?: "",
                count = domain.count?.formatNumber(false) ?: "",
                countSuffix = domain.countSuffix ?: Suffix.NO,
                category = category,
                selectedAnimalIndex = domain.animalId ?: 0,
                animalId = domain.animalId,
                note = domain.note ?: "",
                projectId = if (isMultiProjectTemplate) _itemIdPT else domain.idPT,
                isEntry = false,
            ),
            template = template.copy(
                name = domain.nameTemplate,
                isTemplate = false,
                isTemplateEntry = isTemplateEntry,
                pin = domain.isPinned,
                activeField = TemplateFieldsState(
                    isTitle = domain.title == null,
                    isCount = domain.count == null,
                    isSuffix = domain.countSuffix == null,
                    isCategory = domain.category == null,
                    isAnimal = domain.animalId == null,
                    isNote = domain.note == null,
                    isMultiProjectTemplate = domain.isMultiProjectTemplate
                )
            ),
            errors = AddErrors()
        )
    }

    private fun AddProductState.toDomainMap(): DomainAddTable {
        val dateList = product.date.split(".")
        val category = product.category.trim()
        return DomainAddTable(
            id = product.itemId,
            title = product.title.trim(),
            count = product.count.toConvertDbDouble(),
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            countSuffix = product.countSuffix,
            priceSuffix = getState().settings.currencySuffix,
            category = if (category.contains(resourceProvider.getString(R.string.support_text_no_category)) || category.isEmpty())
                null else category,
            animalId = product.animalId,
            note = product.note.trim(),
            price = 0.0,
            idPT = product.projectId,
            animalCountId = product.animalCountId
        )
    }

    private fun AddProductState.toDomainTemplate(): DomainTemplateTable {
        val category = product.category.trim()
        val activeField = template.activeField
        return DomainTemplateTable(
            id = product.itemId,
            templateType = TemplateType.ADD,
            nameTemplate = template.name.trim(),
            title = if (activeField.isTitle) null else product.title.trim(),
            count = if (activeField.isCount) null else product.count.toConvertDbDouble(),
            countSuffix = if (activeField.isSuffix) null else product.countSuffix,
            priceSuffix = getState().settings.currencySuffix,
            category = if (activeField.isCategory) null else category,
            animalId = if (activeField.isAnimal || activeField.isMultiProjectTemplate) null else product.animalId,
            note = if (activeField.isNote) null else product.note.trim(),
            price = 0.0,
            idPT = _itemIdPT,
            isPinned = template.pin,
            isMultiProjectTemplate = activeField.isMultiProjectTemplate
        )
    }
}

fun DomainTemplateTable.toQrPayload(): QrPayload {
    return QrPayload(
        templateType = templateType,
        isMultiProjectTemplate = isMultiProjectTemplate,
        itemId = id,
        idPT = idPT,
        backupData = this
    )
}