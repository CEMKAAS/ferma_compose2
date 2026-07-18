package com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TemplateType
import com.zaroslikov.domain.models.enums.supportUi.ProductOperation
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.domain.repository.template.AddTemplateRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel3
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.supportFun.build
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.toSuffixList
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningType
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddListIntent
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeData
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrPayload
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateFieldsState
import com.zaroslikov.fermacompose2.ui.project.sections.mapperToBrieflyItem
import com.zaroslikov.fermacompose2.utils.QrGenerator
import com.zaroslikov.fermacompose2.utils.QrNavigationManager
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
import kotlin.text.trim

@HiltViewModel
class SaleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val qrGenerator: QrGenerator,
    private val qrNavigationManager: QrNavigationManager,
    private val addTemplateRepository: AddTemplateRepository,
    private val saleRepository: SaleRepository,
    private val warehouseRepository: WarehouseRepository,
    private val settingsRepository: SettingsRepository,
    private val resourceProvider: ResourceProvider,
    private val projectRepository: ProjectRepository,
    private val yandexMetricRepository: YandexMetricRepository
) : EntryNewViewModel3<SaleListState, SaleListIntent, SaleListReduce>(
    SaleListState(),
    SaleListReduce(resourceProvider),
    qrGenerator = qrGenerator,
    resourceProvider = resourceProvider,
    projectRepository = projectRepository,
    qrNavigationManager = qrNavigationManager,
    addTemplateRepository = addTemplateRepository
) {

    private val _itemIdPT: Long = checkNotNull(savedStateHandle[SaleDestination.itemIdArg])

    init {
        loadData()
    }

    override fun onIntent(intent: SaleListIntent) {
        sendIntent(intent)
        when (intent) {
            is SaleListIntent.OpenBottomSheetEntry -> loadDataForEntryOrEdit(
                intent.isOpen,
                intent.item,
                intent.isSaveStateForBottomSheet
            )

            is SaleListIntent.OpenBottomSheetGroup -> openBottomSheetGroup(intent.value)

            is SaleListIntent.TitleAndSuffixClicked ->
                updateWarehouseUiState(intent.title, intent.productOrigin)

            SaleListIntent.Insert -> insert()
            SaleListIntent.Update -> update()
            SaleListIntent.Delete -> delete()

            SaleListIntent.InsertTemplate -> insertTemplate()
            SaleListIntent.UpdateTemplate -> updateTemplate()
            SaleListIntent.DeleteTemplate -> deleteTemplate()

            is SaleListIntent.QrDetected -> qrScanner(intent.value)
            is SaleListIntent.CreateQrCodeClick -> generateQrCode(intent.value)
            is SaleListIntent.SetPinOfTemplateClick -> setPin(intent.value)

            is AddListIntent.RecoverClick -> recover(intent.value)
            is AddListIntent.OpenPatternsBottomSheetClick ->
                loadDataForTemplatesBottomSheet(intent.value)

            else -> Unit
        }
    }

    override fun createOpenQrIntent(data: QrCodeData): SaleListIntent {
        TODO("Not yet implemented")
    }

    override fun createOpenQrWarningIntent(warning: QrCodeWarningType): SaleListIntent {
        TODO("Not yet implemented")
    }

    override suspend fun onQrPayloadReceived(payload: QrPayload) {
        TODO("Not yet implemented")
    }

    override fun loadData() {
        viewModelScope.launch {
            val isArchive = projectRepository.getIsArchiveProject(_itemIdPT).first()
            combine(
                saleRepository.getAllSaleItems(_itemIdPT),
                settingsRepository.getSettings(_itemIdPT)
            ) { addList, settings ->
                val brieflyList = brieflyList(addList, settings)
                Triple(addList, brieflyList, settings)
            }.collectLatest { (saleItems, saleBrieflyItems, settings) ->
                updateState {
                    it.copy(
                        idPT = _itemIdPT,
                        mainList = it.mainList.copy(
                            items = saleItems,
                            brieflyItems = saleBrieflyItems,
                        ),
                        searchState = it.searchState.copy(
                            searchResults = saleItems,
                            searchBrieflyResults = saleBrieflyItems
                        ),
                        settings = settings,
                        isLoading = false,
                        isArchive = isArchive
                    )
                }
            }
        }
    }

    private fun brieflyList(
        list: List<DomainSaleTable>,
        settings: DomainSettings
    ): List<BrieflyItem> {
        return list
            .groupBy { it.title }
            .map { (title, items) ->
                mapperToBrieflyItem(title, items, settings)
            }
    }

    private fun openBottomSheetGroup(title: String?) {
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
            val productItems = getDetailsName(name = title)
            val detail =
                mapperToBrieflyItem(title, productItems, settings = getState().settings)
            updateState {
                it.copy(
                    bottomSheetState = it.bottomSheetState.copy(
                        isOpenGroup = true,
                    ),
                    detailNomenclatura = it.detailNomenclatura.copy(
                        detail = detail,
                        productItems = productItems
                    )
                )
            }
        }
    }

    private suspend fun getDetailsName(name: String): List<DomainSaleTable> {
        return saleRepository.getBrieflyDetailsItemSale(_itemIdPT, name).first()
    }

    private fun loadDataForEntryOrEdit(
        isOpen: Boolean,
        domain: DomainSaleTable?,
        isSaveStateForBottomSheet: Boolean = false
    ) {
        viewModelScope.launch {
            if (!isOpen) {
                val state =
                    if (isSaveStateForBottomSheet) getState().currentProduct
                    else SaleProductState()
                onIntent(
                    SaleListIntent.RefreshEntryBottomSheetState(
                        false, state, isSaveStateForBottomSheet
                    )
                )
                return@launch
            }
            val newState =
                if (!getState().bottomSheetState.isSaveStateForBottomSheet || domain != null) {
                    val titleDeferred =
                        async { saleRepository.getItemsTitleSaleList(_itemIdPT).first() }
                    val categoryDeferred =
                        async { saleRepository.getItemsCategorySaleList(_itemIdPT).first() }
                    val buyerDeferred = async {
                        saleRepository.getItemsBuyerSaleList(_itemIdPT).first()
                    }

                    val baseState = SaleProductState(
                        product = SaleProduct(
                            projectId = _itemIdPT,
                            category = resourceProvider.getString(R.string.support_text_no_category),
                            buyer = resourceProvider.getString(R.string.animal_card_screen_sale_note_no_buyer),

                            ),
                        pickList = PickSaleList(
                            titles = titleDeferred.await(),
                            categories = categoryDeferred.await(),
                            buyers = buyerDeferred.await()
                        )
                    )
                    if (domain == null) baseState
                    else {
                        /* val saleCategory = baseState.pickList.titleList
                             .firstOrNull { it.title == domain.title }
                             ?.category*/

                        baseState.toUiMap22(domain)/*.copy(saleCategory = saleCategory)*/
                    }
                } else getState().currentProduct
            onIntent(SaleListIntent.RefreshEntryBottomSheetState(true, newState))
            newState.product.productOrigin?.let {
                updateWarehouseUiStateSync(newState.product.title, it)
            }
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
                .getCurrentBalanceProductList(name, _itemIdPT).first()
                .build(getState().settings)

        onIntent(SaleListIntent.RefreshWarehouseCount(pair))
    }


    override fun insert() {
        viewModelScope.launch {
            val currentProduct = getState().currentProduct
            saleRepository.insertSale(currentProduct.toDomainMap())
            yandexMetricRepository.metricSale(currentProduct)
            if (currentProduct.template.isTemplateEntry)
                sendIntent(SaleListIntent.OpenTemplateBottomSheetClick(false))
            else {
                showSnackbar(ProductOperation.ADD)
                loadDataForEntryOrEdit(false, null)
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            saleRepository.updateSale(getState().currentProduct.toDomainMap())
            if (getState().productDetail != null)
                updateState { it.copy(productDetail = getState().currentProduct.toDomainMap()) }
            showSnackbar(ProductOperation.EDIT)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun delete() {
        viewModelScope.launch {
            getState().productDetail?.let { product ->
                saleRepository.deleteSaleById(product.id)
                showSnackbar(ProductOperation.DELETE)
                sendIntent(SaleListIntent.OpenBottomSheetDelete(null))
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
                sendIntent(SaleListIntent.OpenTemplateDeleteBottomSheet(null))
            }
        }
    }

    private fun showSnackbar(productOperation: ProductOperation) {
        val (title, count) =
            if (productOperation == ProductOperation.DELETE) {
                val product = getState().productDetail ?: DomainAddItemDto()
                product.title to product.count.formatNumber()
            } else {
                val product = getState().currentProduct.product
                product.title to product.count
            }
        val suffix = resourceProvider.getString(getState().settings.currencySuffix.toResId())
        showMessage(
            when (productOperation) {
                ProductOperation.ADD -> resourceProvider.getString(R.string.snackbar_sale_add)
                    .format(title, count, suffix)

                ProductOperation.EDIT -> resourceProvider.getString(R.string.snackbar_sale_update)
                    .format(title, count, suffix)

                else -> resourceProvider.getString(R.string.snackbar_sale_delete)
                    .format(title, count, suffix)
            }
        )
    }

    private fun SaleProductState.toUiMap22(domain: DomainSaleTable): SaleProductState {
        val isIndicatorsValue = setOf(domain.animalId, domain.animalCountId).any { it != null }
        return copy(
            product = product.copy(
                itemId = domain.id,
                title = domain.title,
                count = domain.count.formatNumber(false),
                countSuffix = domain.countSuffix,
                isAutoPrice = domain.priceAll != null,
                price = domain.price.formatNumber(false),
                priceAll = domain.priceAll?.formatNumber() ?: "",
                date = formatDateToString(
                    domain.day,
                    domain.month,
                    domain.year
                ),
                category = domain.category ?: product.category,
                buyer = domain.buyer ?: product.buyer,
                note = domain.note,
                animalId = domain.animalId,
                animalCountId = domain.animalCountId,
                isEntry = false,
                isIndicatorsValue = isIndicatorsValue,
                projectId = domain.idPT,
                productOrigin = domain.productOrigin
            ),
            pickList = pickList.copy(
                suffixList = domain.countSuffix.toSuffixList()
            ),
            errors = ErrorSale(),
        )
    }

    private fun SaleProductState.toUiMap23(
        domain: DomainTemplateTable,
        isTemplateEntry: Boolean = false,
        isMultiProjectTemplate: Boolean = false
    ): SaleProductState {
        val category =
            domain.category?.ifBlank { resourceProvider.getString(R.string.support_text_no_category) }
                ?: ""
        val buyer =
            domain.buyer?.ifBlank { resourceProvider.getString(R.string.animal_card_screen_sale_note_no_buyer) }
                ?: ""
        return copy(
            product = product.copy(
                itemId = if (isTemplateEntry) product.itemId else domain.id,
                title = domain.title ?: "",
                count = domain.count?.formatNumber(false) ?: "",
                countSuffix = domain.countSuffix ?: Suffix.NO,
                isAutoPrice = domain.priceAll != null,
                price = domain.price?.formatNumber(false) ?: "",
                priceAll = domain.priceAll?.formatNumber() ?: "",
                category = category,
                buyer = buyer,
                note = domain.note ?: "",
                animalId = domain.animalId,
                isEntry = false,
                projectId = domain.idPT,
//                productOrigin = domain.productOrigin TODO не совсем уверен, что это
            ),
            //TODO не припоминаю, зачем тут лист единицы измерения
            /* pickList = pickList.copy(
                 suffixList = domain.countSuffix.toSuffixList()
             ),*/
            template = template.copy(
                name = domain.nameTemplate,
                isTemplate = false,
                isTemplateEntry = isTemplateEntry,
                pin = domain.isPinned,
                activeField = TemplateFieldsState(
                    isTitle = domain.title == null,
                    isCount = domain.count == null,
                    isPrice = domain.price == null,
                    isSuffix = domain.countSuffix == null,
                    isCategory = domain.category == null,
                    isBuyer = domain.buyer == null,
                    isNote = domain.note == null,
                    isMultiProjectTemplate = domain.isMultiProjectTemplate
                )
            ),
            errors = ErrorSale(),
        )
    }

    private fun SaleProductState.toDomainMap(): DomainSaleTable {
        val dateList = product.date.split(".")
        val category = product.category.trim()
        val buyer = product.buyer.trim()
        return DomainSaleTable(
            id = product.itemId,
            title = product.title.trim(),
            count = product.count.toConvertDbDouble(),
            countSuffix = product.countSuffix,
            price = product.price.toConvertDbDouble(),
            priceAll = if (product.isAutoPrice) product.priceAll.toConvertDbDouble() else null,
            priceSuffix = getState().settings.currencySuffix,
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            category = if (category.contains(resourceProvider.getString(R.string.support_text_no_category)) || category.isEmpty())
                null else category,
            note = product.note.trim(),
            buyer = if (buyer.contains(resourceProvider.getString(R.string.animal_card_screen_sale_note_no_buyer)) || buyer.isEmpty())
                null else buyer,
            idPT = _itemIdPT,
            animalId = product.animalId,
            animalCountId = product.animalCountId,
            productOrigin = product.productOrigin ?: ProductOrigin.SALE
        )
    }

    private fun SaleProductState.toDomainTemplate(): DomainTemplateTable {
        val category = product.category.trim()
        val activeField = template.activeField
        return DomainTemplateTable(
            id = product.itemId,
            templateType = TemplateType.SALE,
            nameTemplate = template.name.trim(),
            title = if (activeField.isTitle) null else product.title.trim(),
            count = if (activeField.isCount) null else product.count.toConvertDbDouble(),
            countSuffix = if (activeField.isSuffix) null else product.countSuffix,
            price = product.price.toConvertDbDouble(),
            priceAll = if (product.isAutoPrice) product.priceAll.toConvertDbDouble() else null,
            priceSuffix = getState().settings.currencySuffix,
            category = if (activeField.isCategory) null else category,
            animalId = if (activeField.isAnimal || activeField.isMultiProjectTemplate) null else product.animalId,
            note = if (activeField.isNote) null else product.note.trim(),
            buyer = if (activeField.isBuyer) null else product.buyer.trim(),
            idPT = _itemIdPT,
            isPinned = template.pin,
            isMultiProjectTemplate = activeField.isMultiProjectTemplate
        )
    }
}