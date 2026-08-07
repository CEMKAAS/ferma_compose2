package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto
import com.zaroslikov.domain.models.dto.template.DomainWriteOffTemplateDto
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TemplateType
import com.zaroslikov.domain.models.enums.supportUi.ProductOperation
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AppSettingsRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.domain.repository.template.TemplateRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.QrCodeIntent
import com.zaroslikov.fermacompose2.base.intent.TemplateIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel3
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.supportFun.build
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toSuffixList
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningType
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrPayload
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateFieldsState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateItem
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.mapperToBrieflyItem
import com.zaroslikov.fermacompose2.utils.QrGenerator
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
import kotlin.collections.isNotEmpty
import kotlin.text.contains
import kotlin.text.isNotBlank
import kotlin.text.trim

@HiltViewModel
class WriteOffViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val qrGenerator: QrGenerator,
    private val addRepository: AddRepository,
    private val writeOffRepository: WriteOffRepository,
    private val qrNavigationManager: QrNavigationManager,
    private val warehouseRepository: WarehouseRepository,
    private val settingsRepository: SettingsRepository,
    private val resourceProvider: ResourceProvider,
    private val projectRepository: ProjectRepository,
    private val templateRepository: TemplateRepository,
    private val yandexMetricRepository: YandexMetricRepository,
    private val appSettingsRepository: AppSettingsRepository
) : EntryNewViewModel3<WriteOffListState, WriteOffListIntent, WriteOffListReduce>(
    initialState = WriteOffListState(),
    reducer = WriteOffListReduce(resourceProvider),
    qrGenerator = qrGenerator,
    resourceProvider = resourceProvider,
    projectRepository = projectRepository,
    qrNavigationManager = qrNavigationManager,
    templateRepository = templateRepository,
    appSettingsRepository = appSettingsRepository
) {

    private val _itemIdPT: Long = checkNotNull(savedStateHandle[WriteOffDestination.itemIdArg])

    init {
        loadData()
        loadTemplate(TemplateType.WRITE_OFF)
    }

    override fun onIntent(intent: WriteOffListIntent) {
        sendIntent(intent)
        when (intent) {
            is WriteOffListIntent.LoadDataForDetailNomenclatura ->
                loadDataForDetailNomenclatura(intent.value)

            is WriteOffListIntent.OpenBottomSheetEntry ->
                loadDataForEntryOrEdit(
                    intent.isOpen,
                    intent.id,
                    intent.isSaveStateForBottomSheet,
                    intent.isTemplate
                )

            is WriteOffListIntent.TitleAndSuffix ->
                updateWarehouseUiState(intent.title, intent.writeOffProductOrigin)

            WriteOffListIntent.Insert -> insert()
            WriteOffListIntent.Update -> update()
            is WriteOffListIntent.Delete -> delete()

            else -> Unit
        }
    }

    override fun onQrCodeIntent(intent: QrCodeIntent) {
        sendQrCodeIntent(intent)
        when (intent) {
            is QrCodeIntent.CreateQrCodeClick -> generateQrCode(intent.value)
            is QrCodeIntent.RecoverClick -> recover(intent.value)
            is QrCodeIntent.QrDetected -> qrScanner(intent.value, TemplateType.WRITE_OFF, _itemIdPT)
            else -> Unit
        }
    }

    override fun loadData() {
        viewModelScope.launch {
            val isArchive = projectRepository.getIsArchiveProject(_itemIdPT).first()
            combine(
                writeOffRepository.getAllWriteOffItems(_itemIdPT),
                addRepository.getItemsTitleAddList(_itemIdPT, false),
                settingsRepository.getSettings(_itemIdPT)
            ) { addList, titleList, settings ->
                val brieflyList = brieflyList(addList, settings)
                LoadDataWriteOffList(addList, brieflyList, titleList, settings)
            }.collectLatest { (writeOffItems, writeOffBrieflyItems, titleList, setting) ->
                updateState {
                    it.copy(
                        idPT = _itemIdPT,
                        mainList = it.mainList.copy(
                            items = writeOffItems,
                            brieflyItems = writeOffBrieflyItems
                        ),
                        searchState = it.searchState.copy(
                            searchResults = writeOffItems,
                            searchBrieflyResults = writeOffBrieflyItems
                        ),
                        isNotProduction = titleList.isNotEmpty(),
                        settings = setting,
                        isLoading = false,
                        isArchive = isArchive
                    )
                }
            }
        }
    }

    private fun brieflyList(
        list: List<DomainWriteOffTable>,
        settings: DomainSettings
    ): List<BrieflyItem> {
        return list
            .groupBy { it.title }
            .map { (title, items) ->
                mapperToBrieflyItem(title, items, settings = settings)
            }
    }


    private suspend fun getDetailsName(name: String): List<DomainWriteOffTable> {
        return writeOffRepository.getBrieflyDetailsItemWriteOff(_itemIdPT, name).first()
    }

    override fun insert() {
        viewModelScope.launch {
            val currentProduct = getState().currentProduct
            writeOffRepository.insertWriteOff(currentProduct.product.updateForSave())
            yandexMetricRepository.metricalWriteOff(currentProduct)
            if (currentProduct.template.isTemplateEntry)
                sendIntent(WriteOffListIntent.OpenTemplateBottomSheetClick(false))
            else {
                showSnackbar(ProductOperation.ADD)
                loadDataForEntryOrEdit(false, null)
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            writeOffRepository.updateWriteOff(getState().currentProduct.product.updateForSave())
            if (getState().productDetail != null)
                updateState { it.copy(productDetail = getState().currentProduct.product.updateForSave()) }
            showSnackbar(ProductOperation.EDIT)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun delete() {
        viewModelScope.launch {
            getState().productDetail?.let { product ->
                writeOffRepository.deleteWriteOff(product.id)
                showSnackbar(ProductOperation.DELETE)
                sendIntent(WriteOffListIntent.OpenBottomSheetDelete(null))
            }
        }
    }

    override fun insertTemplate() {
        viewModelScope.launch {
            val domainTemplate = getState().currentProduct.toDomainTemplate()
            templateRepository.insert(domainTemplate)
            yandexMetricRepository.metricalTemplate(domainTemplate)
            loadDataForEntryOrEdit(false, null)

        }
    }

    override fun updateTemplate() {
        viewModelScope.launch {
            templateRepository.update(getState().currentProduct.toDomainTemplate())
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun deleteTemplate() {
        viewModelScope.launch {
            getState().templatesState.templateToDelete?.let { template ->
                templateRepository.deleteAddTemplateItemById(template.id)
                sendTemplateIntent(TemplateIntent.OpenTemplateDeleteBottomSheet(null))
            }
        }
    }


    private fun loadDataForDetailNomenclatura(title: String?) {
        viewModelScope.launch {
            if (title == null) {
                updateState { state ->
                    state.copy(
                        bottomSheetState = state.bottomSheetState.copy(
                            isOpenGroup = false
                        )
                    )
                }
                return@launch
            }
            val listBriefly = getDetailsName(name = title)
            val currentBriefly =
                mapperToBrieflyItem(title, listBriefly, settings = getState().settings)

            updateState {
                it.copy(
                    bottomSheetState = it.bottomSheetState.copy(
                        isOpenGroup = true,
                    ),
                    detailNomenclatura = it.detailNomenclatura.copy(
                        detail = currentBriefly,
                        productItems = listBriefly
                    )
                )
            }
        }
    }

    override fun loadDataForEntryOrEdit(
        isOpen: Boolean,
        id: Long?,
        isSaveStateForBottomSheet: Boolean,
        isTemplate: Boolean
    ) {
        viewModelScope.launch {
            if (!isOpen) {
                val state =
                    if (isSaveStateForBottomSheet) getState().currentProduct
                    else WriteOffProductState()
                sendIntent(
                    WriteOffListIntent.RefreshEntryBottomSheetState(
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
                                templateRepository.getTemplateItem(id).first()
                                    ?: return@launch
                            baseState.toUiMap23(template)
                        }

                        id == null -> baseState

                        else -> {
                            val addItem = writeOffRepository.getItemWriteOff(id).first()
                            baseState.toUi(addItem)
                        }
                    }
                } else getState().currentProduct
            sendIntent(
                WriteOffListIntent.RefreshEntryBottomSheetState(
                    true,
                    newState,
                    isTemplate = isTemplate
                )
            )
            newState.product.productOrigin?.let {
                updateWarehouseUiStateSync(newState.product.title, it)
            }
        }
    }

    private fun updateWarehouseUiState(name: String, productOrigin: ProductOrigin) {
        viewModelScope.launch {
            updateWarehouseUiStateSync(name, productOrigin)
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String, productOrigin: ProductOrigin) {
        val pair = if (productOrigin == ProductOrigin.EXPENSES)
            warehouseRepository.getCurrentExpensesProductList(name, _itemIdPT).first()
                .build(getState().settings)
        else
            warehouseRepository
                .getCurrentBalanceProductList(name, _itemIdPT).first().build(getState().settings)

        onIntent(WriteOffListIntent.RefreshWarehouseCount(pair))
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
                WriteOffListIntent.OpenTemplateBottomSheetClick(true, currentProduct)
            )
            updateWarehouseUiStateSync(
                currentProduct.product.title,
                currentProduct.product.productOrigin ?: ProductOrigin.SALE
            ) //TODO
        }
    }

    override fun loadDataForTemplatesBottomSheet(isOpen: Boolean) {
        viewModelScope.launch {
            if (!isOpen) return@launch
            templateRepository.getAllWriteOffTemplateItems(_itemIdPT)
                .collectLatest { it ->
                    sendTemplateIntent(TemplateIntent.LoadDataForTemplate(it.map { it.toTemplateItemUi() }))
                }
        }
    }

    override fun loadDataForTemplateBottomSheet(
        id: Long,
        qrPayload: QrPayload?
    ) {
        viewModelScope.launch {
            val template =
                templateRepository.getTemplateItem(qrPayload?.itemId ?: id).first()
                    ?: return@launch sendQrCodeIntent(
                        QrCodeIntent.OpenWarningQrCodeBottomSheetClick(
                            true,
                            QrCodeWarningType.LOCAL,
                            qrPayload?.backupData
                        )
                    )

            val baseState = loadDataForPickList()
            val currentProduct = baseState.toUiMap23(template, isTemplateEntry = true)
            sendIntent(WriteOffListIntent.OpenTemplateBottomSheetClick(true, currentProduct))
            updateWarehouseUiStateSync(
                name = currentProduct.product.title,
                productOrigin = currentProduct.product.productOrigin ?: ProductOrigin.SALE //TODO
            )
        }
    }

    override suspend fun loadDataForPickList(id: Long?): WriteOffProductState {
        return coroutineScope {
            val titleDeferred =
                async { writeOffRepository.getItemsWriteOffList(_itemIdPT).first() }
            val categoryDeferred =
                async { writeOffRepository.getItemsCategoryWriteOffList(_itemIdPT).first() }

            WriteOffProductState(
                product = WriteOffProduct(
                    projectId = _itemIdPT,
                    category = resourceProvider.getString(R.string.support_text_no_category),
                    priceSuffix = getState().settings.currencySuffix
                ),
                pickList = WriteOffPickList(
                    titles = titleDeferred.await(),
                    categories = categoryDeferred.await()
                )
            )
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
                ProductOperation.ADD -> resourceProvider.getString(R.string.snackbar_write_off_add)
                    .format(title, count, suffix)

                ProductOperation.EDIT -> resourceProvider.getString(R.string.snackbar_write_off_update)
                    .format(title, count, suffix)

                else -> resourceProvider.getString(R.string.snackbar_write_off_delete)
                    .format(title, count, suffix)
            }
        )
    }

    private fun WriteOffProductState.toUi(
        domain: DomainWriteOffTable
    ): WriteOffProductState {
        val hasIndicators = setOf(domain.animalCountId).any { it != null }
        return copy(
            product = product.copy(
                itemId = domain.id,
                title = domain.title,
                count = domain.count.formatNumber(false),
                countSuffix = domain.countSuffix,
                price = domain.price?.formatNumber(false) ?: "",
                priceAll = domain.priceAll?.formatNumber() ?: "",
                isAutoPrice = domain.priceAll != null,
                category = domain.category
                    ?: resourceProvider.getString(R.string.support_text_no_category),
                date = formatDateToString(
                    domain.day,
                    domain.month,
                    domain.year
                ),
                productOrigin = domain.productOrigin,
                status = domain.status,
                note = domain.note,
                animalCountId = domain.animalCountId,
                hasIndicators = hasIndicators,
                isEntry = false,
            ),
            pickList = pickList.copy(
                suffixList = domain.countSuffix.toSuffixList()
            )
        )
    }

    private fun WriteOffProductState.toUiMap23(
        domain: DomainTemplateTable,
        isTemplateEntry: Boolean = false,
        isMultiProjectTemplate: Boolean = false
    ): WriteOffProductState {

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
                price = domain.price?.formatNumber(false) ?: "",
                priceAll = domain.priceAll?.formatNumber() ?: "",
                isAutoPrice = domain.priceAll != null,
                productOrigin = domain.productOrigin,
                status = domain.writeOffStatus ?: false,
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
                    isPrice = domain.price == null,
                    isPriceAll = domain.priceAll == null,
                    isCategory = domain.category == null,
                    isDate = domain.isDate,
                    isNote = domain.note == null,
                    isWriteOffStatus = domain.writeOffStatus == null,
                    isMultiProjectTemplate = domain.isMultiProjectTemplate
                )
            ),
            errors = WriteOffError()
        )
    }

    private fun WriteOffProduct.updateForSave(): DomainWriteOffTable {
        val dateList = date.split(".")
        val category = category.trim()
        return DomainWriteOffTable(
            id = itemId,
            title = title.trim(),
            count = count.toConvertDbDouble(),
            countSuffix = countSuffix,
            price = if (price.isBlank() || price == "0") null else price.toConvertDbDouble(),
            priceAll = if (price.isBlank() || price == "0") null else if (isAutoPrice) priceAll.toConvertDbDouble() else null,
            priceSuffix = if (price.isBlank()) null else priceSuffix,
            category = if (category.contains(resourceProvider.getString(R.string.support_text_no_category)) || category.isEmpty())
                null else category,
            productOrigin = productOrigin,
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            note = note.trim(),
            status = status,
            animalCountId = animalCountId,
            idPT = _itemIdPT,
        )
    }

    private fun WriteOffProductState.toDomainTemplate(): DomainTemplateTable {
        val activeField = template.activeField
        return DomainTemplateTable(
            id = product.itemId,
            templateType = TemplateType.WRITE_OFF,
            nameTemplate = template.name.trim(),
            title = if (activeField.isTitle) null else product.title.trim(),
            count = if (activeField.isCount) null else product.count.toConvertDbDouble(),
            countSuffix = if (activeField.isSuffix) null else product.countSuffix,
            price = if (activeField.isPrice) null else product.price.toConvertZeroDouble(),
            priceAll = when {
                !activeField.isPrice && product.isAutoPrice && product.price.isNotBlank() ->
                    if (activeField.isCount) 0.0 else product.priceAll.toConvertDbDouble()

                else -> null
            },
            priceSuffix = product.priceSuffix,
            category = if (activeField.isCategory) null else product.category.trim(),
            isDate = activeField.isDate,
            note = if (activeField.isNote) null else product.note.trim(),
            writeOffStatus = if (activeField.isWriteOffStatus) null else product.status,
            productOrigin = if (activeField.isTitle) null else product.productOrigin,
            idPT = _itemIdPT,
            isPinned = template.pin,
            isMultiProjectTemplate = activeField.isMultiProjectTemplate
        )
    }

    private fun DomainWriteOffTemplateDto.toTemplateItemUi(): TemplateItem {
        val suffix = priceSuffix?.let { resourceProvider.getString(it.toResId()) }
        val description = listOfNotNull(
            title?.takeIf { it.isNotBlank() },
            count?.formatNumber(),
            countSuffix?.let { resourceProvider.getString(it.toResId()) },
            price?.let { "${it.formatNumber()} $suffix".trim() },
            priceAll?.let { if (it != 0.0) "${it.formatNumber()} $suffix".trim() else null },
            category?.takeIf { !it.contains(resourceProvider.getString(R.string.support_text_no_category)) && it.isNotBlank() },
            writeOffStatus?.let {
                resourceProvider.getString(
                    if (it) R.string.ration_button_own_needs
                    else R.string.ration_button_disposal
                )
            },
            note?.takeIf { it.isNotBlank() }
        ).joinToString(" · ")
        return TemplateItem(
            id = id,
            name = nameTemplate,
            description = description,
            isPinned = isPinned,
            isMultiProject = isMultiProject
        )
    }
}