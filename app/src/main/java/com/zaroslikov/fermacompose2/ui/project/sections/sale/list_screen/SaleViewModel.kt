package com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.supportUi.ProductOperation
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SaleRepository
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
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
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
import kotlin.collections.component1
import kotlin.collections.component2

@HiltViewModel
class SaleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saleRepository: SaleRepository,
    private val warehouseRepository: WarehouseRepository,
    private val settingsRepository: SettingsRepository,
    private val resourceProvider: ResourceProvider,
    private val projectRepository: ProjectRepository,
    private val yandexMetricRepository: YandexMetricRepository
) : EntryNewViewModel2<SaleListState, SaleListIntent, SaleListReduce>(
    SaleListState(),
    SaleListReduce(resourceProvider)
) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[SaleDestination.itemIdArg])

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
                updateWarehouseUiState(intent.title, intent.category)

            SaleListIntent.Insert -> insert()
            SaleListIntent.Update -> update()
            SaleListIntent.Delete -> delete(0)
            else -> Unit
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val isArchive = projectRepository.getIsArchiveProject(itemIdPT).first()
            combine(
                saleRepository.getAllSaleItems(itemIdPT),
                settingsRepository.getSettings(itemIdPT)
            ) { addList, settings ->
                val brieflyList = brieflyList(addList, settings)
                Triple(addList, brieflyList, settings)
            }.collectLatest { (addList, briefly, settings) ->
                updateState {
                    it.copy(
                        idPT = itemIdPT,
                        list = addList,
                        searchList = addList,
                        briefly = briefly,
                        searchBrieflyList = briefly,
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
                mapperToBrieflyItem(title, items, settings = settings)
            }
    }

    private fun openBottomSheetGroup(title: String?) {
        viewModelScope.launch {
            if (title == null) {
                updateState { state ->
                    state.copy(isOpenBottomSheetGroup = false)
                }
                return@launch
            }
            val listBriefly = getDetailsName(name = title)
            val currentBriefly =
                mapperToBrieflyItem(title, listBriefly, settings = getState().settings)
            updateState {
                it.copy(
                    isOpenBottomSheetGroup = true,
                    currentBriefly = currentBriefly,
                    listBriefly = listBriefly
                )
            }
        }
    }

    private suspend fun getDetailsName(name: String): List<DomainSaleTable> {
        return saleRepository.getBrieflyDetailsItemSale(itemIdPT, name).first()
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
                    else SaleEntryState2()
                onIntent(
                    SaleListIntent.RefreshEntryBottomSheetState(
                        false, state, isSaveStateForBottomSheet
                    )
                )
                return@launch
            }
            val newState = if (!getState().isSaveStateForBottomSheet || domain != null) {
                val titleDeferred =
                    async { saleRepository.getItemsTitleSaleList(itemIdPT).first() }
                val categoryDeferred =
                    async { saleRepository.getItemsCategorySaleList(itemIdPT).first() }
                val buyerDeferred = async {
                    saleRepository.getItemsBuyerSaleList(itemIdPT).first()
                }

                val baseState = SaleEntryState2(
                    itemIdPT = itemIdPT,
                    category = resourceProvider.getString(R.string.support_text_no_category),
                    buyer = resourceProvider.getString(R.string.animal_card_screen_sale_note_no_buyer),
                    pickList = PickSaleList(
                        titleList = titleDeferred.await(),
                        categoryList = categoryDeferred.await(),
                        buyerList = buyerDeferred.await()
                    )
                )
                if (domain == null) baseState
                else {
                    val saleCategory = baseState.pickList.titleList
                        .firstOrNull { it.title == domain.title }
                        ?.category

                    baseState.toUiMap22(domain).copy(saleCategory = saleCategory)
                }
            } else getState().currentProduct
            onIntent(SaleListIntent.RefreshEntryBottomSheetState(true, newState))
            newState.saleCategory?.let { updateWarehouseUiStateSync(newState.title, it) }
        }
    }


    private fun updateWarehouseUiState(name: String, category: Category) {
        viewModelScope.launch {
            updateWarehouseUiStateSync(name, category)
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String, category: Category) {
        val pair = if (category == Category.EXPENSES)
            warehouseRepository.getCurrentExpensesProductList(name, itemIdPT).first()
                .build(getState().settings)
        else
            warehouseRepository
                .getCurrentBalanceProductList(name, itemIdPT).first()
                .build(getState().settings)

        onIntent(SaleListIntent.RefreshWarehouseCount(pair))
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
            saleRepository.insertSale(getState().currentProduct.toDomainMap())
            yandexMetricRepository.metricSale(getState().currentProduct)
            showSnackbar(ProductOperation.ADD)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun update() {
        viewModelScope.launch {
            saleRepository.updateSale(getState().currentProduct.toDomainMap())
            if (getState().currentDetail != null)
                updateState { it.copy(currentDetail = getState().currentProduct.toDomainMap()) }
            showSnackbar(ProductOperation.EDIT)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            getState().currentDetail?.let { product ->
                saleRepository.deleteSaleById(product.id)
                showSnackbar(ProductOperation.DELETE)
                sendIntent(SaleListIntent.OpenBottomSheetDelete(null))
            }
        }
    }

    private fun showSnackbar(productOperation: ProductOperation) {
        val (title, count) =
            if (productOperation == ProductOperation.DELETE) {
                val product = getState().currentDetail ?: DomainAddItemDto()
                product.title to product.count.formatNumber()
            } else {
                val product = getState().currentProduct
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

    private fun SaleEntryState2.toUiMap22(domain: DomainSaleTable): SaleEntryState2 {
        val isIndicatorsValue = setOf(domain.animalId, domain.animalCountId).any { it != null }
        return copy(
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
            category = domain.category,
            buyer = domain.buyer ?: buyer,
            note = domain.note,
            animalId = domain.animalId,
            animalCountId = domain.animalCountId,
            isEntry = false,
            isIndicatorsValue = isIndicatorsValue, error = ErrorSale(),
            itemIdPT = domain.idPT
        )
    }

    private fun SaleEntryState2.toDomainMap(): DomainSaleTable {
        val dateList = date.split(".")
        return DomainSaleTable(
            id = itemId,
            title = title.trim(),
            count = count.toConvertDbDouble(),
            countSuffix = countSuffix,
            price = price.toConvertDbDouble(),
            priceAll = if (isAutoPrice) priceAll.toConvertDbDouble() else null,
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            category = category.trim(),
            note = note.trim(),
            buyer = if (buyer.isBlank()) null else buyer.trim(),
            idPT = itemIdPT,
            animalId = animalId,
            animalCountId = animalCountId
        )
    }
}