package com.zaroslikov.fermacompose2.ui.sections.sale.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.sale.BrieflySaleDomain
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saleRepository: SaleRepository,
    private val warehouseRepository: WarehouseRepository,
    private val resourceProvider: ResourceProvider
) : EntryNewViewModel<SaleListState, SaleListIntent>(SaleListState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[SaleDestination.itemIdArg])

    init {
        loadData()
    }

    override fun onIntent(intent: SaleListIntent) {
        when (intent) {
            is SaleListIntent.OpenBottomSheetEntry -> openBottomSheetEntry(
                openBottomSheetEntry = intent.value,
                domainSaleTable = intent.item
            )

            is SaleListIntent.OpenBottomSheetGroup -> openBottomSheetGroup(
                openBottomSheetGroup = intent.value,
                currentBriefly = intent.currentBriefly
            )

            is SaleListIntent.TitleChanged -> updateTitle(intent.value)
            is SaleListIntent.TitleAndSuffixClicked -> updateTitleAndSuffix(
                intent.title,
                intent.suffix
            )

            is SaleListIntent.CountChanged -> updateCount(intent.value)
            is SaleListIntent.SuffixClicked -> updateSuffix(intent.value)
            is SaleListIntent.PriceChanged -> updatePrice(intent.value)
            is SaleListIntent.AutoPriceClicked -> updateIsAutoPrice(intent.value)
            is SaleListIntent.CategoryChanged -> updateCategory(intent.value)
            is SaleListIntent.BuyerChanged -> updateBuyer(intent.value)
            SaleListIntent.BuyerClearClicked -> updateBuyerClean()
            is SaleListIntent.DateClicked -> updateDate(intent.value)
            is SaleListIntent.NoteChanged -> updateNote(intent.value)

            SaleListIntent.Insert -> insert()
            SaleListIntent.Update -> update()
            is SaleListIntent.Delete -> delete(intent.value)

            is SaleListIntent.SearchChanged -> updateSearch(intent.value)
            is SaleListIntent.CountWarehouse -> updateWarehouse(intent.value)
            is SaleListIntent.GroupClicked -> updateGroup(intent.value)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                saleRepository.getAllSaleItems(itemIdPT),
                saleRepository.getBrieflyItemSale(itemIdPT)
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

    private fun updateTitleAndSuffix(title: String, suffix: Suffix) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    title = title,
                    countSuffix = suffix,
                    error = it.currentProduct.error.copy(
                        isErrorTitle = title.isBlank(),
                        isErrorSlash = title.contains("/")
                    )
                )
            )
        }
//        updateWarehouseUiState(title)
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
//        updateWarehouseUiState(title)
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
        updatePriceAll()
    }

    private fun updatePrice(price: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    price = price,
                    error = it.currentProduct.error.copy(
                        isErrorPrice = price.isBlank()
                    )
                )
            )
        }
        updatePriceAll()
    }

    private fun updateIsAutoPrice(isAutoPrice: Boolean) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    isAutoPrice = isAutoPrice
                )
            )
        }
        updatePriceAll()
    }

    private fun updatePriceAll() {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    priceAll = if (it.currentProduct.isAutoPrice) (it.currentProduct.price.toConvertZeroDouble() * it.currentProduct.count.toConvertZeroDouble()).formatNumber()
                    else "0"
                )
            )
        }
    }

    private fun updateCountWarehouse(domainCountSuffix: List<DomainCountSuffix>) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    warehouseList = domainCountSuffix
                )
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

    private fun updateBuyer(buyer: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(buyer = buyer)
            )
        }
    }

    private fun updateBuyerClean() {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(buyer = "")
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

    private fun updateWarehouse(warehouseList: List<DomainCountSuffix>) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(warehouseList = warehouseList)
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

    private fun updateSearch(search: String) {
        updateState {
            it.copy(
                textSearch = search
            )
        }
    }

    private fun openBottomSheetGroup(
        openBottomSheetGroup: Boolean,
        currentBriefly: BrieflySaleDomain
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

    private suspend fun getDetailsName(name: String): List<DomainSaleTable> {
        return saleRepository.getBrieflyDetailsItemSale(itemIdPT, name).first()
    }

    private fun openBottomSheetEntry(
        openBottomSheetEntry: Boolean,
        domainSaleTable: DomainSaleTable?
    ) {
        if (openBottomSheetEntry)
            viewModelScope.launch {
                val titleList = saleRepository.getItemsTitleSaleList(itemIdPT).first()
                val categoryList = saleRepository.getItemsCategorySaleList(itemIdPT).first()
                val buyerList = saleRepository.getItemsBuyerSaleList(itemIdPT).first()

                updateState {
                    it.copy(
                        openBottomSheetEntry = true,
                        currentProduct = SaleEntryState2(
                            itemIdPT = itemIdPT,
                            category = resourceProvider.getString(R.string.support_text_no_category),
                            buyer = resourceProvider.getString(R.string.animal_card_screen_sale_note_no_buyer),
                            error = ErrorSale(),
                            pickList = it.currentProduct.pickList.copy(
                                titleList = titleList,
                                categoryList = categoryList,
                                buyerList = buyerList
                            )
                        )
                    )
                }
                domainSaleTable?.let {
                    updateState {
                        it.copy(
                            currentProduct = it.currentProduct.toUiMap22(
                                domainSaleTable
                            )
                        )
                    }
                    val suffix = getState().currentProduct.pickList.titleList
                        .firstOrNull { it.title == getState().currentProduct.title }
                        ?.category

                    suffix?.let {
                        updateWarehouseUiStateSync(getState().currentProduct.title, it)
                    }
                }
            }
        else updateState { it.copy(openBottomSheetEntry = false) }
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
            updateCountWarehouse(pair)
    }

    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {
                saleRepository.insertSale(
                    getState().currentProduct.toDomainMap()
                )
//                metricaSale(saleUiState.copy(priceAll = autoCalculate()))
                navigateTo(UiEvent.NavigateBack)
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
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                saleRepository.updateSale(getState().currentProduct.toDomainMap())
                navigateTo(UiEvent.NavigateBack)
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
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            saleRepository.deleteSaleById(id)
            navigateTo(UiEvent.NavigateBack)
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

    override fun validation() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    error = state.currentProduct.error.copy(
                        isErrorTitle = state.currentProduct.title.isBlank(),
                        isErrorSlash = state.currentProduct.title.isSlash(),
                        isErrorCount = state.currentProduct.count.isBlank(),
                        isErrorPrice = state.currentProduct.price.isBlank()
                    )
                )
            )
        }
    }

    private fun SaleEntryState2.toUiMap22(domain: DomainSaleTable): SaleEntryState2 {

        val isIndicatorsValue = setOf(domain.animalId, domain.animalCountId).any { it != null }

        return copy(
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
            isIndicatorsValue = isIndicatorsValue, error = ErrorSale()
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

sealed class SaleListIntent : BaseIntent {
    data class OpenBottomSheetGroup(
        val value: Boolean,
        val currentBriefly: BrieflySaleDomain = BrieflySaleDomain()
    ) : SaleListIntent()

    data class OpenBottomSheetEntry(
        val value: Boolean,
        val item: DomainSaleTable? = null
    ) : SaleListIntent()

    data class TitleChanged(val value: String) : SaleListIntent()
    data class TitleAndSuffixClicked(val title: String, val suffix: Suffix) : SaleListIntent()
    data class CountChanged(val value: String) : SaleListIntent()
    data class SuffixClicked(val value: Suffix) : SaleListIntent()
    data class PriceChanged(val value: String) : SaleListIntent()
    data class AutoPriceClicked(val value: Boolean) : SaleListIntent()
    data class CategoryChanged(val value: String) : SaleListIntent()
    data class DateClicked(val value: String) : SaleListIntent()
    data class BuyerChanged(val value: String) : SaleListIntent()
    data object BuyerClearClicked : SaleListIntent()
    data class NoteChanged(val value: String) : SaleListIntent()

    data class CountWarehouse(val value: List<DomainCountSuffix>) : SaleListIntent()
    data class GroupClicked(val value: Boolean) : SaleListIntent()
    data class SearchChanged(val value: String) : SaleListIntent()
    data object Insert : SaleListIntent()
    data object Update : SaleListIntent()
    data class Delete(val value: Long) : SaleListIntent()
}