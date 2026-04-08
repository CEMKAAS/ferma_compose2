package com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
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
    private val projectRepository: ProjectRepository
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
            is SaleListIntent.Delete -> delete(intent.value)
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
            warehouseRepository.getCurrentExpensesProductList(name, itemIdPT)
                .filterNotNull()
                .firstOrNull()
        else
            warehouseRepository
                .getCurrentBalanceProductList(name, itemIdPT)
                .filterNotNull()
                .firstOrNull()
        if (pair != null)
            onIntent(SaleListIntent.RefreshWarehouseCount(pair))
    }

    override fun insert() {
        viewModelScope.launch {
            saleRepository.insertSale(getState().currentProduct.toDomainMap())
//                metricaSale(saleUiState.copy(priceAll = autoCalculate()))
            loadDataForEntryOrEdit(false, null)
            showMessage(
                resourceProvider.getString(R.string.toast_sale_s)
                    .format(
                        getState().currentProduct.title,
                        getState().currentProduct.count,
                        getState().currentProduct.countSuffix
                    ) //Todo Обновить название
            )
        }
    }

    override fun update() {
        viewModelScope.launch {
            saleRepository.updateSale(getState().currentProduct.toDomainMap())
            if (getState().currentDetail != null)
                updateState { it.copy(currentDetail = getState().currentProduct.toDomainMap()) }
            loadDataForEntryOrEdit(false, null)
            showMessage(
                resourceProvider.getString(R.string.toast_refresh_s_s)
                    .format(
                        getState().currentProduct.title,
                        getState().currentProduct.count,
                        getState().currentProduct.countSuffix
                    ) //Todo Обновить название
            )
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            saleRepository.deleteSaleById(id)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        getState().currentProduct.title,
                        getState().currentProduct.count,
                        getState().currentProduct.countSuffix
                    ) //Todo Обновить название
            )
        }
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