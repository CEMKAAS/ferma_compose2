package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

import com.zaroslikov.domain.models.dto.add.DomainAddTemplateDto
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import kotlin.text.lowercase

class AddListReduce(private val resourceProvider: ResourceProvider) :
    BaseReducer<AddListState, AddListIntent>() {
    override fun reducer(
        state: AddListState,
        intent: AddListIntent
    ): AddListState {
        return when (intent) {
            is AddListIntent.GroupClicked -> state.updateGroup(intent.value)
            is AddListIntent.SearchChanged -> state.updateSearch(intent.value)
            is AddListIntent.RefreshEntryBottomSheetState -> state.updateEntryBottomSheet(
                isOpenEntryBottomSheet = intent.isOpen,
                entryState2 = intent.state,
                isSaveStateForEntry = intent.isSaveStateForBottomSheet,
                isTemplate = intent.isTemplate
            ).updateValid()

            is AddListIntent.OpenBottomSheetDelete -> state.updateOpenBottomSheetDelete(intent.value)
            is AddListIntent.OpenBottomSheetDetail -> state.updateOpenBottomSheetDetail(intent.value)
            is AddListIntent.OpenPatternsBottomSheetClick ->
                state.updateOpenPatternBottomSheet(intent.value)

            is AddListIntent.LoadDataForTemplate ->
                state.updateLoadDataForTemplate(intent.value)

            is AddListIntent.OpenQrCodeBottomSheetClick ->
                state.updateOpenQrCodeBottomSheet(intent.value)

            is AddListIntent.TitleChanged -> state.updateTitle(intent.value).updateValid()
            is AddListIntent.TitleAndSuffix -> state.updateTitleAndSuffix(intent.pair).updateValid()
            is AddListIntent.RefreshWarehouseCount -> state.updateWarehouseList(intent.value)
            is AddListIntent.CountChanged -> state.updateCount(intent.value).updateValid()
            is AddListIntent.SuffixClicked -> state.updateSuffix(intent.value)
            is AddListIntent.CategoryChanged -> state.updateCategory(intent.value)
            is AddListIntent.Date -> state.updateDate(intent.value)
            is AddListIntent.NoteChanged -> state.updateNote(intent.value)
            is AddListIntent.Animal -> state.updateAnimal(intent.animal)
            is AddListIntent.AnimalClear -> state.updateAnimalClear(intent.value)
            is AddListIntent.AnimalNameById -> state.updateAnimal(intent.value)

            //Template
            is AddListIntent.NameTemplateChanged -> state.updateNameTemplate(intent.value)
                .updateValid()

            is AddListIntent.TitleTemplateChanged -> state.updateTitleTemplate(intent.value)
                .updateValid()

            is AddListIntent.CountTemplateChanged -> state.updateCountTemplate(intent.value)
                .updateValid()

            is AddListIntent.SuffixTemplateClicked -> state.updateSuffixTemplate(intent.value)
            is AddListIntent.CategoryTemplateChanged -> state.updateCategoryTemplate(intent.value)
            is AddListIntent.AnimalTemplateChanged -> state.updateAnimalTemplate(intent.value)
            is AddListIntent.NoteTemplateChanged -> state.updateNoteTemplate(intent.value)
            else -> state
        }
    }

    private fun AddListState.updateOpenPatternBottomSheet(isOpenPatternsBottomSheet: Boolean): AddListState {
        return copy(
            isOpenPatternsBottomSheet = isOpenPatternsBottomSheet
        )
    }

    private fun AddListState.updateLoadDataForTemplate(domainAddTemplateDtoList: List<DomainAddTemplateDto>): AddListState {
        return copy(
            domainAddTemplateDtoList = domainAddTemplateDtoList
        )
    }

    private fun AddListState.updateOpenQrCodeBottomSheet(isOpenQrCodeBottomSheet: Boolean): AddListState {
        return copy(
            isOpenQrCodeBottomSheet = isOpenQrCodeBottomSheet
        )
    }

    private fun AddListState.updateOpenBottomSheetDetail(
        id: Long?
    ): AddListState {
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

    private fun AddListState.updateOpenBottomSheetDelete(id: Long?): AddListState {
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

    private fun AddListState.updateValid(): AddListState {
        val product = currentProduct

        val baseValid =
            if (product.isTemplate) {
                product.nameTemplate.isNotBlank() &&
                        (product.templateState.isTitle || product.title.isNotBlank()) &&
                        (product.templateState.isCount || product.count.isNotBlank()) &&
                        (product.templateState.isTitle || !product.title.isSlash())
            } else {
                product.title.isNotBlank() &&
                        product.count.isNotBlank() &&
                        !product.title.isSlash()
            }
        return copy(
            currentProduct = currentProduct.copy(
                hasAnyError = baseValid
            )
        )
    }

    private fun AddListState.updateGroup(isGroup: Boolean): AddListState {
        return copy(
            isGroup = isGroup
        )
    }

    private fun AddListState.updateSuffix(suffix: Suffix): AddListState {
        return copy(
            currentProduct = currentProduct.copy(countSuffix = suffix)
        )
    }

    private fun AddListState.updateCategory(category: String): AddListState {
        return copy(
            currentProduct = currentProduct.copy(category = category)
        )
    }

    private fun AddListState.updateDate(date: String): AddListState {
        return copy(
            currentProduct = currentProduct.copy(date = date)
        )
    }

    private fun AddListState.updateNote(note: String): AddListState {
        return copy(
            currentProduct = currentProduct.copy(note = note)
        )
    }

    private fun AddListState.updateAnimal(animal: String): AddListState {
        return copy(
            currentProduct = currentProduct.copy(animal = animal)
        )
    }

    private fun AddListState.updateSearch(search: String): AddListState {
        val query = search.trim().lowercase()

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
                            .contains(query)
            }

        val searchBrieflyList = if (query.isBlank() && isGroup) briefly
        else
            briefly.filter { item ->
                item.title.lowercase().contains(query) ||
                        item.weight.toString().lowercase().contains(query)
            }


        return copy(
            textSearch = search,
            searchBrieflyList = searchBrieflyList,
            searchList = searchList
        )
    }

    private fun AddListState.updateEntryBottomSheet(
        isOpenEntryBottomSheet: Boolean,
        entryState2: AddEntryState2,
        isSaveStateForEntry: Boolean,
        isTemplate: Boolean
    ): AddListState {
        return copy(
            openBottomSheetEntry = isOpenEntryBottomSheet,
            currentProduct = entryState2.copy(isTemplate = isTemplate),
            isSaveStateForBottomSheet = isSaveStateForEntry,
        )
    }

    private fun AddListState.updateTitleAndSuffix(pair: Pair<String, Suffix>): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                title = pair.first,
                countSuffix = pair.second,
                error = currentProduct.error.copy(
                    isErrorTitle = pair.first.isBlank(),
                    isErrorSlash = pair.first.contains("/")
                )
            )
        )
    }

    private fun AddListState.updateTitle(title: String): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                title = title,
                error = if (currentProduct.isTemplate) currentProduct.error
                else currentProduct.error.copy(
                    isErrorTitle = title.isBlank(),
                    isErrorSlash = title.contains("/")
                )
            )
        )
    }

    private fun AddListState.updateWarehouseList(warehouseList: List<DomainCountSuffix>): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                pickList = currentProduct.pickList.copy(
                    warehouseList = warehouseList
                )
            )
        )
    }

    private fun AddListState.updateCount(count: String): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                count = count,
                error = if (currentProduct.isTemplate) currentProduct.error
                else currentProduct.error.copy(isErrorCount = count.isBlank())
            )
        )
    }

    private fun AddListState.updateAnimal(animal: Pair<Long, String>): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                selectedAnimalIndex = animal.first,
                animalId = animal.first,
                animal = animal.second
            )
        )
    }

    private fun AddListState.updateAnimalClear(animal: String): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                animalId = null,
                animal = animal
            )
        )
    }

    private fun AddListState.updateNameTemplate(nameTemplate: String): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                nameTemplate = nameTemplate,
                error = currentProduct.error.copy(
                    isErrorNameTemplate = nameTemplate.isBlank()
                )
            )
        )
    }

    private fun AddListState.updateTitleTemplate(isTitle: Boolean): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                title = if (isTitle) "" else currentProduct.title,
                templateState = currentProduct.templateState.copy(
                    isTitle = isTitle
                )
            )
        )
    }

    private fun AddListState.updateCountTemplate(isCount: Boolean): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                count = if (isCount) "" else currentProduct.count,
                templateState = currentProduct.templateState.copy(
                    isCount = isCount
                )
            )
        )
    }

    private fun AddListState.updateSuffixTemplate(isSuffix: Boolean): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                countSuffix = if (isSuffix) Suffix.PIECES else currentProduct.countSuffix,
                templateState = currentProduct.templateState.copy(
                    isSuffix = isSuffix
                )
            )
        )
    }

    private fun AddListState.updateCategoryTemplate(isCategory: Boolean): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                category = if (isCategory) "" else currentProduct.category,
                templateState = currentProduct.templateState.copy(
                    isCategory = isCategory
                )
            )
        )
    }

    private fun AddListState.updateAnimalTemplate(isAnimal: Boolean): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                animal = if (isAnimal) "" else currentProduct.animal,
                animalId = null,
                templateState = currentProduct.templateState.copy(
                    isAnimal = isAnimal
                )
            )
        )
    }

    private fun AddListState.updateNoteTemplate(isNote: Boolean): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                note = if (isNote) "" else currentProduct.note,
                templateState = currentProduct.templateState.copy(
                    isNote = isNote
                )
            )
        )
    }
}