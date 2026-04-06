package com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider

class AnimalListReduce(private val resourceProvider: ResourceProvider) :
    BaseReducer<AnimalListState, AnimalListIntent>() {
    override fun reducer(
        state: AnimalListState,
        intent: AnimalListIntent
    ): AnimalListState {
        return when (intent) {
            is AnimalListIntent.GroupClicked -> state.updateGroup(intent.value)
            is AnimalListIntent.SearchChanged -> state.updateSearch(intent.value)

            is AnimalListIntent.RefreshEntryBottomSheetState -> state.updateEntryBottomSheet(
                intent.isOpen,
                intent.state,
                intent.isSaveStateForBottomSheet
            ).updateValid()

            is AnimalListIntent.AnimalGroupClicked -> state.updateGroup(intent.value).updateValid()
            is AnimalListIntent.TitleChanged -> state.updateTitle(intent.value).updateValid()
            is AnimalListIntent.TypeChanged -> state.updateType(intent.value).updateValid()
            is AnimalListIntent.CountChanged -> state.updateCount(intent.value).updatePriceAll()
                .updateValid()

            is AnimalListIntent.SuffixClicked -> state.updateSuffix(intent.value)
            is AnimalListIntent.PriceChanged -> state.updatePrice(intent.value).updatePriceAll()
            is AnimalListIntent.AutoPriceClicked ->
                state.updateIsAutoPrice(intent.value).updatePriceAll()

            is AnimalListIntent.DateClicked -> state.updateDate(intent.value)
            is AnimalListIntent.DateFactoryClicked -> state.updateIsDateFactory(intent.value)
            is AnimalListIntent.DateFactoryChanged -> state.updateDateFactory(intent.value)
            is AnimalListIntent.FoodDayChanged -> state.updateFoodDay(intent.value)
            is AnimalListIntent.FoodDaySuffixClicked -> state.updateFoodDaySuffix(intent.value)
            is AnimalListIntent.NoteChanged -> state.updateNote(intent.value)
            is AnimalListIntent.SexClicked -> state.updateSex(intent.value)
            else -> state
        }
    }

    private fun AnimalListState.updateEntryBottomSheet(
        isOpenEntryBottomSheet: Boolean,
        entryState2: AnimalEntryState2,
        isSaveStateForEntry: Boolean
    ): AnimalListState {
        return copy(
            openBottomSheetEntry = isOpenEntryBottomSheet,
            currentProduct = entryState2,
            isSaveStateForEntry = isSaveStateForEntry
        )
    }

    private fun AnimalListState.updateValid(): AnimalListState {
        val title = currentProduct.title.isNotBlank()
        val type = currentProduct.type.isNotBlank()
        val count = currentProduct.count.isNotBlank()

        val baseValid = when (currentProduct.isAnimalGroup) {
            true -> title && count && type
            else -> title && type
        }
        return copy(
            currentProduct = currentProduct.copy(
                hasAnyError = baseValid
            )
        )
    }

    private fun AnimalListState.updateSearch(textSearch: String): AnimalListState {
        val query = textSearch.trim().lowercase()

        val searchList = if (query.isBlank() && !isArchive) list
        else
            list.filter { item ->

                item.name.lowercase().contains(query) ||
                        item.type.lowercase().contains(query) ||
                        item.date.lowercase().contains(query) ||
                        item.count.toString().lowercase().contains(query) /*||*/
                /*stringResource(item.countSuffix.toResId()).lowercase().contains(query)*/
                /* "${item.day} ${stringResource(monthToResString(item.month))} ${item.year}".lowercase()
                     .contains(query) ||
                         (item.priceAll ?: item.price).toString().lowercase().contains(query)*/
            } //TODO

        val searchArchiveList = if (query.isBlank() && isArchive) list
        else
            list.filter { item ->

                item.name.lowercase().contains(query) ||
                        item.type.lowercase().contains(query) ||
                        item.date.lowercase().contains(query) ||
                        item.count.toString().lowercase().contains(query) /*||*/
                /*stringResource(item.countSuffix.toResId()).lowercase().contains(query)*/
                /* "${item.day} ${stringResource(monthToResString(item.month))} ${item.year}".lowercase()
                     .contains(query) ||
                         (item.priceAll ?: item.price).toString().lowercase().contains(query)*/
            }
        return copy(
            textSearch = textSearch,
            searchList = searchList,
            searchArchiveList = searchArchiveList
        )
    }

    private fun AnimalListState.updateGroup(isGroup: Boolean): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                isAnimalGroup = isGroup
            )
        )
    }

    private fun AnimalListState.updateTitle(title: String): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                title = title,
                error = currentProduct.error.copy(
                    isErrorTitle = title.isBlank(),
                )
            )
        )
    }

    private fun AnimalListState.updateType(type: String): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                type = type,
                error = currentProduct.error.copy(
                    isErrorType = type.isBlank(),
                )
            )
        )
    }

    private fun AnimalListState.updateCount(count: String): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                count = count,
                error = currentProduct.error.copy(
                    isErrorCount = count.isBlank()
                )
            )
        )
    }

    private fun AnimalListState.updateSuffix(suffix: Suffix): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                countSuffix = suffix,
            )
        )
    }

    private fun AnimalListState.updatePrice(price: String): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                price = price
            )
        )
    }

    private fun AnimalListState.updateIsAutoPrice(isAutoPrice: Boolean): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                isAutoPrice = isAutoPrice
            )
        )
    }

    private fun AnimalListState.updatePriceAll(): AnimalListState {
        val price = currentProduct.price.toConvertZeroDouble()
        val count = currentProduct.count.toConvertZeroDouble()
        return copy(
            currentProduct = currentProduct.copy(
                priceAll = if (currentProduct.isAutoPrice) (price * count).formatNumber() else "0"
            )
        )
    }

    private fun AnimalListState.updateDate(date: String): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                dateBorn = date,
                dateFactory = if (currentProduct.isDateFactory) date else dateToday()
            )
        )
    }

    private fun AnimalListState.updateIsDateFactory(isDateFactory: Boolean): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                isDateFactory = isDateFactory,
            )
        )
    }

    private fun AnimalListState.updateDateFactory(dateFactory: String): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                dateFactory = dateFactory
            )
        )
    }

    private fun AnimalListState.updateFoodDay(foodDay: String): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                foodDay = foodDay
            )
        )
    }

    private fun AnimalListState.updateFoodDaySuffix(foodDaySuffix: Suffix): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                foodDaySuffix = foodDaySuffix
            )
        )
    }

    private fun AnimalListState.updateNote(note: String): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                note = note
            )
        )
    }

    private fun AnimalListState.updateSex(sex: Boolean): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                sex = sex
            )
        )
    }
}