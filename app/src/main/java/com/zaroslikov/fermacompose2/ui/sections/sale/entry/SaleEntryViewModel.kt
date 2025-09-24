package com.zaroslikov.fermacompose2.ui.sections.sale.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryViewModel
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaleEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saleRepository: SaleRepository,
    private val warehouseRepository: WarehouseRepository,
    private val resourceProvider: ResourceProvider,
) : EntryViewModel<SaleEntryState, SaleEntryIntent>(SaleEntryState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[SaleEntryDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[SaleEntryDestination.itemId])
    val isEntry: Boolean = itemId == -1L


    override fun onIntent(intent: SaleEntryIntent) {
        when (intent) {
            is SaleEntryIntent.TitleChanged -> updateTitle(intent.value)
            is SaleEntryIntent.TitleAndSuffixClicked -> updateTitleAndSuffix(
                intent.title,
                intent.suffix
            )

            is SaleEntryIntent.SuffixClicked -> updateState { it.copy(countSuffix = intent.value) }
            is SaleEntryIntent.CountChanged -> updateCount(intent.value)
            is SaleEntryIntent.AutoPriceClicked -> updateIsAutoPrice(intent.value)
            is SaleEntryIntent.PriceChanged -> updatePrice(intent.value)
            is SaleEntryIntent.CategoryChanged -> updateState { it.copy(category = intent.value) }
            is SaleEntryIntent.BuyerChanged -> updateState { it.copy(buyer = intent.value) }
            SaleEntryIntent.BuyerClearClicked -> updateState { it.copy(buyer = "") }
            is SaleEntryIntent.DateClicked -> updateState { it.copy(date = intent.value) }
            is SaleEntryIntent.NoteChanged -> updateState { it.copy(note = intent.value) }
            SaleEntryIntent.Insert -> insert()
            SaleEntryIntent.Update -> update()
            SaleEntryIntent.Delete -> delete()
        }
    }

    init {
        viewModelScope.launch {
            if (!isEntry) {
                val domainSaleTable = saleRepository.getItemSale(itemId)
                    .filterNotNull()
                    .first()

                updateState { it.updateFromDomain(domainSaleTable) }
            }

            val titleList = saleRepository.getItemsTitleSaleList(itemIdPT).first()
            val categoryList = saleRepository.getItemsCategorySaleList(itemIdPT).first()
            val buyerList = saleRepository.getItemsBuyerSaleList(itemIdPT).first()
            updateState {
                it.copy(
                    isEntry = isEntry,
                    category = resourceProvider.getString(R.string.support_text_no_category),
                    countSuffix = Suffix.PIECES,
                    buyer = resourceProvider.getString(R.string.animal_card_screen_sale_note_no_buyer),
                    pickList = it.pickList.copy(
                        titleList = titleList,
                        categoryList = categoryList,
                        buyerList = buyerList
                    )
                )
            }

            val suffix = getState().pickList.titleList
                .firstOrNull { it.title == getState().title }
                ?.category

            suffix?.let {
                if (!isEntry) updateWarehouseUiStateSync(getState().title, it)
            }
        }
    }

    fun updateWarehouseUiState(name: String, category: Category) {
        viewModelScope.launch {
            updateWarehouseUiStateSync(name, category)
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String, category: Category) {
        val pair = if (category == Category.EXPENSES)
            warehouseRepository.getCurrentExpensesProductList(name, itemIdPT.toLong())
                .filterNotNull()
                .firstOrNull()
        else
            warehouseRepository
                .getCurrentBalanceProductList(name, itemIdPT.toLong())
                .filterNotNull()
                .firstOrNull()

        if (pair != null)
            updateCountWarehouse(pair)
    }


    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {
                saleRepository.insertSale(
                    getState().updateForSave(itemIdPT = itemIdPT)
                )
//                metricaSale(saleUiState.copy(priceAll = autoCalculate()))
                navigateTo(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_sale_s)
                        .format(
                            getState().title,
                            getState().count,
                            getState().countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                saleRepository.updateSale(getState().updateForSave(itemId, itemIdPT))
                navigateTo(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_refresh_s_s)
                        .format(
                            getState().title,
                            getState().count,
                            getState().countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    override fun delete() {
        viewModelScope.launch {
            saleRepository.deleteSaleById(itemId.toLong())
            navigateTo(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        getState().title,
                        getState().count,
                        getState().countSuffix
                    ) //Todo Обновить название
            )
        }
    }

    override fun validation() {
        updateState { state ->
            state.copy(
                error = state.error.copy(
                    isErrorTitle = state.title.isBlank(),
                    isErrorSlash = state.title.isSlash(),
                    isErrorCount = state.count.isBlank(),
                    isErrorPrice = state.priceAll.isBlank()
                )
            )
        }
    }

    private fun updateTitle(title: String) {
        updateState {
            it.copy(
                title = title,
                error = it.error.copy(
                    isErrorTitle = title.isBlank(),
                    isErrorSlash = title.isSlash()
                )
            )
        }
    }

    private fun updateTitleAndSuffix(title: String, suffix: Suffix) {
        updateState {
            it.copy(
                title = title,
                countSuffix = suffix,
                error = it.error.copy(
                    isErrorTitle = title.isBlank(),
                    isErrorSlash = title.contains("/")
                )
            )
        }
    }

    private fun updateCount(count: String) {
        updateState {
            it.copy(
                count = count,
                error = it.error.copy(
                    isErrorCount = count.isBlank()
                )
            )
        }
        updatePriceAll()
    }

    private fun updatePrice(price: String) {
        updateState {
            it.copy(
                price = price,
                error = it.error.copy(
                    isErrorPrice = price.isBlank()
                )
            )
        }
        updatePriceAll()
    }

    private fun updateIsAutoPrice(isAutoPrice: Boolean) {
        updateState {
            it.copy(
                isAutoPrice = isAutoPrice
            )
        }
        updatePriceAll()
    }

    private fun updatePriceAll() {
        updateState {
            it.copy(
                priceAll = if (it.isAutoPrice) (it.price.toConvertZeroDouble() * it.count.toConvertZeroDouble()).formatNumber()
                else "0"
            )
        }
    }

    fun updateCountWarehouse(domainCountSuffix: List<DomainCountSuffix>) {
        updateState {
            it.copy(
                warehouseList = domainCountSuffix
            )
        }
    }
}


sealed class SaleEntryIntent : BaseIntent {
    data class TitleChanged(val value: String) : SaleEntryIntent()
    data class TitleAndSuffixClicked(val title: String, val suffix: Suffix) :
        SaleEntryIntent()

    data class CountChanged(val value: String) : SaleEntryIntent()
    data class SuffixClicked(val value: Suffix) : SaleEntryIntent()
    data class PriceChanged(val value: String) : SaleEntryIntent()
    data class AutoPriceClicked(val value: Boolean) : SaleEntryIntent()
    data class CategoryChanged(val value: String) : SaleEntryIntent()
    data class DateClicked(val value: String) : SaleEntryIntent()
    data class BuyerChanged(val value: String) : SaleEntryIntent()
    data object BuyerClearClicked : SaleEntryIntent()
    data class NoteChanged(val value: String) : SaleEntryIntent()
    data object Insert : SaleEntryIntent()
    data object Update : SaleEntryIntent()
    data object Delete : SaleEntryIntent()
}

