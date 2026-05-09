package com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen

import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.supportFun.toSuffixList
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import kotlin.text.lowercase

class SaleListReduce(
    private val resourceProvider: ResourceProvider
) : BaseReducer<SaleListState, SaleListIntent>() {
    override fun reducer(
        state: SaleListState,
        intent: SaleListIntent
    ): SaleListState {
        return when (intent) {
            is SaleListIntent.RefreshEntryBottomSheetState -> state.updateEntryBottomSheet(
                isOpenEntryBottomSheet = intent.isOpen,
                isSaveStateForEntry = intent.isSaveStateForBottomSheet,
                entryState2 = intent.state
            ).updateValid()

            is SaleListIntent.OpenBottomSheetDelete -> state.updateOpenBottomSheetDelete(intent.value)
            is SaleListIntent.OpenBottomSheetDetail -> state.updateOpenBottomSheetDetail(intent.value)

            is SaleListIntent.SearchChanged -> state.updateSearch(intent.value)
            is SaleListIntent.GroupClicked -> state.updateGroup(intent.value)
            is SaleListIntent.TitleChanged -> state.updateTitle(intent.value).updateValid()
            is SaleListIntent.TitleAndSuffixClicked ->
                state.updateTitleAndSuffix(intent.title, intent.suffix, intent.productOrigin)
                    .updateValid()

            is SaleListIntent.CountChanged ->
                state.updateCount(intent.value).updatePriceAll().updateValid()

            is SaleListIntent.SuffixClicked -> state.updateSuffix(intent.value)
            is SaleListIntent.RefreshWarehouseCount -> state.updateCountWarehouse(intent.value)
            is SaleListIntent.PriceChanged ->
                state.updatePrice(intent.value).updatePriceAll().updateValid()

            is SaleListIntent.AutoPriceClicked ->
                state.updateIsAutoPrice(intent.value).updatePriceAll()

            is SaleListIntent.CategoryChanged -> state.updateCategory(intent.value)
            is SaleListIntent.BuyerChanged -> state.updateBuyer(intent.value)
            SaleListIntent.BuyerClearClicked -> state.updateBuyerClean()
            is SaleListIntent.DateClicked -> state.updateDate(intent.value)
            is SaleListIntent.NoteChanged -> state.updateNote(intent.value)
            else -> state
        }
    }

    private fun SaleListState.updateOpenBottomSheetDetail(
        id: Long?
    ): SaleListState {
        return if (id == null)
            copy(
                isOpenBottomSheetDetail = false,
                currentDetail = null
            )
        else {
            val domain = list.find { it.id == id }
            copy(
                isOpenBottomSheetDetail = domain?.let { true } ?: false,
                currentDetail = domain
            )
        }
    }

    private fun SaleListState.updateOpenBottomSheetDelete(id: Long?): SaleListState {
        return if (id == null)
            copy(
                isOpenBottomSheetDelete = false,
                currentDetail = null
            )
        else {
            val domain = list.find { it.id == id }
            copy(
                isOpenBottomSheetDelete = domain?.let { true } ?: false,
                currentDetail = domain
            )
        }
    }


    private fun SaleListState.updateValid(): SaleListState {
        val baseValid =
            currentProduct.title.isNotBlank() && currentProduct.count.isNotBlank()
                    && currentProduct.price.isNotBlank() && !currentProduct.title.isSlash()
        return copy(
            currentProduct = currentProduct.copy(
                hasAnyError = baseValid
            )
        )
    }

    private fun SaleListState.updateTitleAndSuffix(
        title: String,
        suffix: Suffix,
        productOriginProduct: ProductOrigin
    ): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                title = title,
                productOrigin = productOriginProduct,
                countSuffix = suffix,
                pickList = currentProduct.pickList.copy(suffixList = suffix.toSuffixList()),
                error = currentProduct.error.copy(
                    isErrorTitle = title.isBlank(),
                    isErrorSlash = title.contains("/")
                )
            )
        )
    }

    private fun SaleListState.updateTitle(title: String): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                title = title,
                error = currentProduct.error.copy(
                    isErrorTitle = title.isBlank(),
                    isErrorSlash = title.contains("/")
                )
            )
        )
    }

    private fun SaleListState.updateCount(count: String): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                count = count,
                error = currentProduct.error.copy(isErrorCount = count.isBlank())
            )
        )
    }

    private fun SaleListState.updatePrice(price: String): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                price = price,
                error = currentProduct.error.copy(
                    isErrorPrice = price.isBlank()
                )
            )
        )
    }

    private fun SaleListState.updateIsAutoPrice(isAutoPrice: Boolean): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                isAutoPrice = isAutoPrice
            )
        )
    }

    private fun SaleListState.updatePriceAll(): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                priceAll = if (currentProduct.isAutoPrice) (currentProduct.price.toConvertZeroDouble() * currentProduct.count.toConvertZeroDouble()).formatNumber()
                else "0"
            )
        )
    }

    private fun SaleListState.updateCountWarehouse(domainCountSuffix: List<DomainCountSuffix>): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                pickList = currentProduct.pickList.copy(
                    warehouseList = domainCountSuffix
                )
            )
        )
    }

    private fun SaleListState.updateSuffix(suffix: Suffix): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(countSuffix = suffix)
        )
    }

    private fun SaleListState.updateCategory(category: String): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(category = category)
        )
    }


    private fun SaleListState.updateDate(date: String): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(date = date)
        )
    }

    private fun SaleListState.updateBuyer(buyer: String): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(buyer = buyer)
        )
    }

    private fun SaleListState.updateBuyerClean(): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(buyer = "")
        )
    }

    private fun SaleListState.updateNote(note: String): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(note = note)
        )
    }

    private fun SaleListState.updateGroup(isGroup: Boolean): SaleListState {
        return copy(
            isGroup = isGroup
        )
    }

    private fun SaleListState.updateSearch(textSearch: String): SaleListState {
        val query = textSearch.trim().lowercase()

        val searchList = if (query.isBlank() && !isGroup) list
        else
            list.filter { item ->
                val category =
                    item.category ?: resourceProvider.getString(R.string.support_text_no_category)
                item.title.lowercase().contains(query) ||
                        item.note.lowercase().contains(query) ||
                        category.lowercase().contains(query) ||
                        item.count.toString().lowercase().contains(query) ||
                        resourceProvider.getString(item.countSuffix.toResId()).lowercase()
                            .contains(query) ||
                        "${item.day} ${resourceProvider.getString(monthToResString(item.month))} ${item.year}".lowercase()
                            .contains(query) ||
                        item.buyer?.lowercase()?.contains(query) == true ||
                        (item.priceAll ?: item.price).toString().lowercase().contains(query)
            }

        val searchBrieflyList = if (query.isBlank() && isGroup) briefly
        else
            briefly.filter { item ->
                item.title.lowercase().contains(query) ||
                        item.weight?.value.toString().lowercase().contains(query) ||
                        (item.price).toString().lowercase().contains(query)
            }
        return copy(
            textSearch = textSearch,
            searchBrieflyList = searchBrieflyList,
            searchList = searchList
        )
    }

    private fun SaleListState.updateEntryBottomSheet(
        isOpenEntryBottomSheet: Boolean,
        entryState2: SaleEntryState2,
        isSaveStateForEntry: Boolean
    ): SaleListState {
        return copy(
            isOpenBottomSheetEntry = isOpenEntryBottomSheet,
            currentProduct = entryState2,
            isSaveStateForBottomSheet = isSaveStateForEntry
        )
    }
}