package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen

import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.monthToResString

import com.zaroslikov.fermacompose2.utils.ResourceProvider

class WriteOffListReduce(
    private val resourceProvider: ResourceProvider
) : BaseReducer<WriteOffListState, WriteOffListIntent>() {
    override fun reducer(
        state: WriteOffListState,
        intent: WriteOffListIntent
    ): WriteOffListState {
        return when (intent) {

            is WriteOffListIntent.RefreshEntryBottomSheetState -> state.updateEntryBottomSheet(
                intent.isOpen,
                intent.state,
                intent.isSaveStateForBottomSheet
            ).updateValid()

            is WriteOffListIntent.RefreshWarehouseCount -> state.updateWarehouseCount(intent.value)

            is WriteOffListIntent.StatusClicked -> state.updateStatus(intent.value)
            is WriteOffListIntent.TitleAndSuffix ->
                state.updateTitleAndSuffix(intent.title, intent.suffix).updateValid()

            is WriteOffListIntent.CountChanged ->
                state.updateCount(intent.value).updatePriceAll().updateValid()

            is WriteOffListIntent.PriceChanged ->
                state.updatePrice(intent.value).updatePriceAll()

            is WriteOffListIntent.AutoPriceClicked ->
                state.updateIsAutoPrice(intent.value).updatePriceAll()

            is WriteOffListIntent.DateClicked -> state.updateDate(intent.value)
            is WriteOffListIntent.NoteChanged -> state.updateNote(intent.value)


            is WriteOffListIntent.SearchChanged -> state.updateSearch(intent.value)
            is WriteOffListIntent.GroupClicked -> state.updateGroup(intent.value)
            else -> state
        }
    }


    private fun WriteOffListState.updateValid(): WriteOffListState {
        val baseValid =
            currentProduct.title.isNotBlank() && currentProduct.count.isNotBlank()
        return copy(
            currentProduct = currentProduct.copy(
                hasAnyError = baseValid
            )
        )
    }

    private fun WriteOffListState.updateEntryBottomSheet(
        isOpenEntryBottomSheet: Boolean,
        entryState2: WriteOffEntryState2,
        isSaveStateForEntry: Boolean
    ): WriteOffListState {
        return copy(
            isOpenEntryBottomSheet = isOpenEntryBottomSheet,
            currentProduct = entryState2,
            isSaveStateForBottomSheet = isSaveStateForEntry
        )
    }

    private fun WriteOffListState.updateTitleAndSuffix(
        title: String,
        suffix: Suffix
    ): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                title = title,
                countSuffix = suffix,
                error = currentProduct.error.copy(
                    isErrorTitle = title.isBlank(),
                    isErrorSlash = title.isSlash()
                )
            )
        )
    }

    private fun WriteOffListState.updateCount(count: String): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                count = count,
                error = currentProduct.error.copy(
                    isErrorCount = count.isBlank()
                )
            )
        )
    }

    private fun WriteOffListState.updatePrice(price: String): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(price = price)
        )
    }

    private fun WriteOffListState.updateIsAutoPrice(isAutoPrice: Boolean): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(isAutoPrice = isAutoPrice)
        )
    }

    private fun WriteOffListState.updatePriceAll(): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                priceAll = if (currentProduct.isAutoPrice) (currentProduct.price.toConvertZeroDouble() * currentProduct.count.toConvertZeroDouble()).formatNumber()
                else "0"
            )
        )
    }

    private fun WriteOffListState.updateDate(date: String): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(date = date)
        )
    }

    private fun WriteOffListState.updateNote(note: String): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(note = note)
        )
    }

    private fun WriteOffListState.updateStatus(status: Boolean): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(status = status)
        )
    }

    private fun WriteOffListState.updateWarehouseCount(warehouseList: List<DomainCountSuffix>): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                pickList = currentProduct.pickList.copy(
                    warehouseList = warehouseList
                )
            )
        )
    }

    private fun WriteOffListState.updateGroup(isGroup: Boolean): WriteOffListState {
        return copy(
            isGroup = isGroup
        )
    }

    private fun WriteOffListState.updateSearch(search: String): WriteOffListState {
        val query = textSearch.trim().lowercase()

        val searchList = if (query.isBlank() && !isGroup) list
        else
            list.filter { item ->
                item.title.lowercase().contains(query) ||
                        item.note.lowercase().contains(query) ||
                        /*  item.category.lowercase().contains(query) ||*/
                        item.count.toString().lowercase().contains(query) ||
                        resourceProvider.getString(item.countSuffix.toResId()).lowercase()
                            .contains(query)
                "${item.day} ${resourceProvider.getString(monthToResString(item.month))} ${item.year}".lowercase()
                    .contains(query) ||
                        (item.priceAll ?: item.price).toString().lowercase().contains(query)
            }

        val searchBrieflyList = if (query.isBlank() && isGroup) briefly
        else
            briefly.filter { item ->
                item.title.lowercase().contains(query) ||
                        item.count.toString().lowercase().contains(query) ||
                        (item.price).toString().lowercase().contains(query)
            }
        return copy(
            textSearch = search,
            searchList = searchList,
            searchBrieflyList = searchBrieflyList
        )
    }
}