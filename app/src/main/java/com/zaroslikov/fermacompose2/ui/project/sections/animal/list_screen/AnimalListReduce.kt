package com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen

import android.util.Log
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.ui.project.sections.animal.group.isSexSplitExceeding
import com.zaroslikov.fermacompose2.supportFun.formatDateToLong
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import com.zaroslikov.fermacompose2.ui.project.sections.animal.group.MergeGroupState

class AnimalListReduce(private val resourceProvider: ResourceProvider) :
    BaseReducer<AnimalListState, AnimalListIntent>() {
    override fun reducer(
        state: AnimalListState,
        intent: AnimalListIntent
    ): AnimalListState {
        return when (intent) {
            is AnimalListIntent.ArchiveClicked -> state.updateAnimalArchive(intent.value)
            is AnimalListIntent.SearchChanged -> state.updateSearch(intent.value)

            is AnimalListIntent.OpenBottomSheetDelete -> state.updateOpenBottomSheetDelete(intent.value)
                .copy(deleteHasPregnancyHistory = false)

            is AnimalListIntent.OpenArchivePregnancyDialog -> state.copy(archivePregnancyAnimalId = intent.animalId)
            is AnimalListIntent.ArchivePregnancyConfirmed -> state.copy(archivePregnancyAnimalId = null)

            is AnimalListIntent.RefreshEntryBottomSheetState -> state.updateEntryBottomSheet(
                intent.isOpen,
                intent.state,
                intent.isSaveStateForBottomSheet
            ).updateValid()

            is AnimalListIntent.AnimalGroupClicked -> state.updateGroup(intent.value).updateValid()
            is AnimalListIntent.TitleChanged -> state.updateTitle(intent.value).updateValid()
            is AnimalListIntent.TypeChanged -> state.updateType(intent.value).updateValid()
            is AnimalListIntent.BreedChanged -> state.updateBreed(intent.value)
            is AnimalListIntent.CountChanged -> state.updateCount(intent.value).updatePriceAll()
                .updateValid()

            is AnimalListIntent.FemalesCountChanged ->
                state.copy(currentProduct = state.currentProduct.copy(femalesCount = intent.value)).updateValid()

            is AnimalListIntent.MalesCountChanged ->
                state.copy(currentProduct = state.currentProduct.copy(malesCount = intent.value)).updateValid()

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

            is AnimalListIntent.OpenMergeSheet ->
                if (intent.value) state else state.copy(merge = MergeGroupState())

            is AnimalListIntent.RefreshMergeState -> state.copy(merge = intent.state)
            is AnimalListIntent.MergeCandidateToggled -> state.copy(merge = state.merge.toggle(intent.id))
            is AnimalListIntent.MergeModeChanged -> state.copy(merge = state.merge.changeMode(intent.isNewGroup))
            is AnimalListIntent.MergeGroupChosen -> state.copy(merge = state.merge.chooseGroup(intent.id))
            is AnimalListIntent.MergeNameChanged -> state.copy(merge = state.merge.changeName(intent.value))
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
        // Самок и самцов не больше, чем голов в группе
        val isErrorSexCount = currentProduct.isAnimalGroup && isSexSplitExceeding(
            total = currentProduct.count.replace(',', '.').toDoubleOrNull() ?: 0.0,
            females = currentProduct.femalesCount,
            males = currentProduct.malesCount
        )

        val baseValid = when (currentProduct.isAnimalGroup) {
            true -> title && count && type && !isErrorSexCount
            else -> title && type
        }
        return copy(
            currentProduct = currentProduct.copy(
                hasAnyError = baseValid,
                error = currentProduct.error.copy(isErrorSexCount = isErrorSexCount)
            )
        )
    }

    //TODO сделать null
    private fun AnimalListState.updateOpenBottomSheetDelete(id: Long?): AnimalListState {
        Log.i("animal_list", "updateOpenBottomSheetDelete: $id")
        return if (id == null)
            copy(
                isOpenBottomSheetDelete = false,
                currentAnimal = AnimalListUi()
            )
        else {
            // В активном режиме ищем и среди членов сборных групп (они видны в поиске)
            val currentList = if (isArchive) allActive else archiveList
            val domain = currentList.find { it.id == id }
            copy(
                isOpenBottomSheetDelete = domain?.let { true } ?: false,
                currentAnimal = domain ?: AnimalListUi()
            )
        }
    }

    private fun AnimalListState.updateAnimalArchive(isArchive: Boolean): AnimalListState {
        return copy(
            isArchive = isArchive
        )
    }

    private fun AnimalListState.updateSearch(textSearch: String): AnimalListState {
        val query = textSearch.trim().lowercase()

        // Поиск по сборным группам (по названию) и по всем активным животным, включая их членов;
        // без запроса — верхний уровень
        val searchList = if (query.isBlank()) list
        else herds.filter { it.name.lowercase().contains(query) }.map { AnimalListItem.Herd(it) } +
            allActive.filter { item ->
                item.name.lowercase().contains(query) ||
                        item.type.lowercase().contains(query) ||
                        item.date.lowercase().contains(query) ||
                        (item.count?.lowercase()?.contains(query) == true) /*||*/
                /*stringResource(item.countSuffix.toResId()).lowercase().contains(query)*/
                /* "${item.day} ${stringResource(monthToResString(item.month))} ${item.year}".lowercase()
                     .contains(query) ||
                         (item.priceAll ?: item.price).toString().lowercase().contains(query)*/
            }.map { AnimalListItem.Animal(it) } //TODO

        val searchArchiveList = if (query.isBlank()) archiveList
        else
            archiveList.filter { item ->
                item.name.lowercase().contains(query) ||
                        item.type.lowercase().contains(query) ||
                        item.date.lowercase().contains(query) ||
                        (item.count?.lowercase()?.contains(query) == true) /*||*/
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
                isAnimalGroup = isGroup,
                type = AnimalSpecies.adaptType(currentProduct.type, isGroup, currentProduct.sex)
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

    private fun AnimalListState.updateBreed(breed: String): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                breed = breed
            )
        )
    }

    private fun AnimalListState.updateType(type: String): AnimalListState {
        return copy(
            currentProduct = currentProduct.copy(
                type = type,
                // «Бык»/«Корова» однозначно задают пол особи
                sex = if (currentProduct.isAnimalGroup) currentProduct.sex
                else AnimalSpecies.sexOf(type) ?: currentProduct.sex,
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
                // Дата завода не может быть раньше рождения, иначе сохраняем выбранную пользователем
                dateFactory = if (currentProduct.isDateFactory ||
                    formatDateToLong(currentProduct.dateFactory) < formatDateToLong(date)
                ) date else currentProduct.dateFactory
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
                sex = sex,
                type = AnimalSpecies.adaptType(currentProduct.type, currentProduct.isAnimalGroup, sex)
            )
        )
    }
}