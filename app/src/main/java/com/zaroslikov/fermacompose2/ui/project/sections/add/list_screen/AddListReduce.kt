package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

import com.zaroslikov.domain.models.dto.add.DomainAddItemDto2
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.QrCodeIntent
import com.zaroslikov.fermacompose2.base.intent.TemplateIntent

import com.zaroslikov.fermacompose2.base.reduce.SectionReducer
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningType
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyItem
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import kotlin.text.lowercase

class AddListReduce(private val resourceProvider: ResourceProvider) :
    SectionReducer<AddListState, AddListIntent>() {
    override fun reducer(
        state: AddListState,
        intent: AddListIntent
    ): AddListState {
        return when (intent) {
            is AddListIntent.LoadData -> state.updateLoadData(
                intent.itemIdPT, intent.addList, intent.briefly, intent.settings,
                intent.isLoading, intent.isArchive
            )

            is AddListIntent.GroupClicked -> state.updateGroup(intent.value)
            is AddListIntent.SearchChanged -> state.updateSearch(intent.value)
            is AddListIntent.RefreshEntryBottomSheetState -> state.updateEntryBottomSheet(
                isOpenEntryBottomSheet = intent.isOpen,
                entryState2 = intent.state,
                isSaveStateForEntry = intent.isSaveStateForBottomSheet,
                isTemplate = intent.isTemplate
            ).updateValid()

            is AddListIntent.OpenBottomSheetGroup ->
                state.updateOpenBottomSheetGroup(intent.isOpen, intent.detail, intent.productItems)

            is AddListIntent.OpenBottomSheetDelete -> state.updateOpenBottomSheetDelete(intent.value)
            is AddListIntent.OpenBottomSheetDetail -> state.updateOpenBottomSheetDetail(intent.value)


            is AddListIntent.TitleChanged -> state.updateTitle(intent.value).updateValid()
            is AddListIntent.TitleAndSuffix -> state.updateTitleAndSuffix(intent.pair).updateValid()
            is AddListIntent.RefreshWarehouseCount -> state.updateWarehouseList(intent.value)
            is AddListIntent.CountChanged -> state.updateCount(intent.value).updateValid()
            is AddListIntent.SuffixClicked -> state.updateSuffix(intent.value).updateValid()
            is AddListIntent.CategoryChanged -> state.updateCategory(intent.value).updateValid()
            is AddListIntent.Date -> state.updateDate(intent.value)
            is AddListIntent.NoteChanged -> state.updateNote(intent.value).updateValid()
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
            is AddListIntent.DateTemplateChanged -> state.updateDateTemplate(intent.value)
            is AddListIntent.AnimalTemplateChanged -> state.updateAnimalTemplate(intent.value)
            is AddListIntent.NoteTemplateChanged -> state.updateNoteTemplate(intent.value)
            is AddListIntent.MultiProjectTemplateChanged -> state.updateMultiProjectTemplate(intent.value)

            //Templates
            is AddListIntent.OpenTemplateBottomSheetClick ->
                state.updateOpenEntryInTemplate(intent.value, intent.toUiMap23).updateValid()

            else -> state
        }
    }

    override fun qrReducer(
        state: AddListState,
        intent: QrCodeIntent
    ): AddListState {
        return when (intent) {
            is QrCodeIntent.OpenScannerQrCodeBottomSheetClick ->
                state.updateOpenScannerQrCode(intent.value)

            is QrCodeIntent.OpenWarningQrCodeBottomSheetClick ->
                state.updateOpenWarningQrCode(
                    intent.value,
                    intent.qrCodeWarningType,
                    intent.backupData
                )

            is QrCodeIntent.OpenQrCodeBottomSheetClick ->
                state.updateOpenQrCodeBottomSheet(intent.value, intent.qrCode)

            else -> state
        }
    }

    override fun templateReducer(
        state: AddListState,
        intent: TemplateIntent
    ): AddListState {
        return when (intent) {
            is TemplateIntent.OpenTemplateDeleteBottomSheet ->
                state.updateOpenTemplateDeleteBottomSheet(intent.value)

            is TemplateIntent.OpenPatternsBottomSheetClick ->
                state.updateOpenPatternBottomSheet(intent.value)

            is TemplateIntent.LoadDataForTemplate ->
                state.updateLoadDataForTemplate(intent.value)

            else -> state
        }
    }

    private fun AddListState.updateLoadData(
        itemIdPT: Long,
        items: List<DomainAddItemDto2>,
        brieflyItems: List<BrieflyItem>,
        settings: DomainSettings,
        isLoading: Boolean,
        isArchive: Boolean
    ): AddListState {
        return copy(
            idPT = itemIdPT,
            mainList = mainList.copy(
                items = items,
                brieflyItems = brieflyItems,
            ),
            searchState = searchState.copy(
                searchResults = items,
                searchBrieflyResults = brieflyItems
            ),
            productDetail = productDetail?.let { detail ->
                items.find { it.id == detail.id }
            }, //TODO Заменить на отдельную фукнцию
            settings = settings,
            isLoading = isLoading,
            isArchive = isArchive
        )
    }

    private fun AddListState.updateOpenEntryInTemplate(
        isOpenEntryInTemplateBottomSheet: Boolean,
        value: AddProductState,
    ): AddListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenEntryInTemplate = isOpenEntryInTemplateBottomSheet
            ),
            currentProduct = value,
        )
    }

    private fun AddListState.updateOpenScannerQrCode(
        bool: Boolean,
    ): AddListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenScannerQrCode = bool
            )
        )
    }

    private fun AddListState.updateOpenWarningQrCode(
        bool: Boolean,
        warning: QrCodeWarningType,
        backupData: DomainTemplateTable?
    ): AddListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenWarningQrCode = bool,
                isOpenScannerQrCode = false
            ),
            qrCodeWarning = qrCodeWarning.copy(
                warningType = warning,
                templateBackup = backupData
            )
        )
    }

    private fun AddListState.updateOpenPatternBottomSheet(isOpenTemplates: Boolean): AddListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenTemplates = isOpenTemplates
            )
        )
    }

    private fun AddListState.updateLoadDataForTemplate(templateItems: List<TemplateItem>): AddListState {
        return copy(
            templatesState = templatesState.copy(
                templatesList = templateItems
            )
        )
    }

    private fun AddListState.updateOpenQrCodeBottomSheet(
        isOpenCreateQrCode: Boolean,
        qrCodeState: QrCodeData?
    ): AddListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenCreateQrCode = isOpenCreateQrCode,
            ),
            qrCodeState = qrCodeState,
        )
    }

    private fun AddListState.updateOpenBottomSheetDetail(
        id: Long?
    ): AddListState {
        return if (id == null)
            copy(
                bottomSheetState = bottomSheetState.copy(
                    isOpenDetail = false,
                ),
                productDetail = null
            )
        else {
            val domain = mainList.items.find { it.id == id }
            copy(
                bottomSheetState = bottomSheetState.copy(
                    isOpenDetail = domain?.let { true } ?: false),
                productDetail = domain
            )
        }
    }

    private fun AddListState.updateOpenBottomSheetGroup(
        isOpenGroup: Boolean,
        detail: BrieflyItem?,
        productItems: List<DomainAddItemDto2>
    ): AddListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenGroup = isOpenGroup,
            ),
            detailNomenclatura = detailNomenclatura.copy(
                detail = detail,
                productItems = productItems
            )
        )
    }

    private fun AddListState.updateOpenBottomSheetDelete(id: Long?): AddListState {
        return if (id == null)
            copy(
                bottomSheetState = bottomSheetState.copy(
                    isOpenProductDelete = false
                ),
                productDetail = null
            )
        else {
            val domain = mainList.items.find { it.id == id }
            copy(
                bottomSheetState = bottomSheetState.copy(
                    isOpenProductDelete = domain?.let { true } ?: false),
                productDetail = domain
            )
        }
    }

    private fun AddListState.updateOpenTemplateDeleteBottomSheet(id: Long?): AddListState {
        return if (id == null)
            copy(
                bottomSheetState = bottomSheetState.copy(
                    isOpenTemplateDelete = false
                ),
                productDetail = null
            )
        else {
            val templateItem = templatesState.templatesList.find { it.id == id }
            copy(
                bottomSheetState = bottomSheetState.copy(
                    isOpenTemplateDelete = templateItem?.let { true } ?: false),
                templatesState = templatesState.copy(
                    templateToDelete = templateItem
                )
            )
        }
    }

    private fun AddListState.updateValid(): AddListState {
        val product = currentProduct.product
        val template = currentProduct.template

        val baseValid =
            when {
                template.isTemplate -> template.name.isNotBlank() &&
                        (template.activeField.isTitle || product.title.isNotBlank()
                                || !product.title.isSlash()) &&
                        (template.activeField.isCount || product.count.isNotBlank())

                else -> product.title.isNotBlank() &&
                        product.count.isNotBlank() &&
                        !product.title.isSlash()
            }
        return copy(
            currentProduct = currentProduct.copy(
                errors = currentProduct.errors.copy(
                    hasAnyError = baseValid
                )
            )
        )
    }

    private fun AddListState.updateGroup(isGroup: Boolean): AddListState {
        return copy(
            mainList = mainList.copy(
                isGroupMode = isGroup
            )
        )
    }

    private fun AddListState.updateSuffix(suffix: Suffix): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(countSuffix = suffix)
            )
        )
    }

    private fun AddListState.updateCategory(category: String): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(category = category)
            )
        )
    }

    private fun AddListState.updateDate(date: String): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(date = date)
            )
        )
    }

    private fun AddListState.updateNote(note: String): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(note = note)
            )
        )
    }

    private fun AddListState.updateAnimal(animal: String): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(animalName = animal)
            )
        )
    }

    private fun AddListState.updateSearch(search: String): AddListState {
        val query = search.trim().lowercase()

        val searchList = if (query.isBlank() && !mainList.isGroupMode) mainList.items
        else
            mainList.items.filter { item ->

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

        val searchBrieflyList = if (query.isBlank() && mainList.isGroupMode) mainList.brieflyItems
        else
            mainList.brieflyItems.filter { item ->
                item.title.lowercase().contains(query) ||
                        item.weight.toString().lowercase().contains(query)
            }

        return copy(
            searchState = searchState.copy(
                searchQuery = search,
                searchBrieflyResults = searchBrieflyList,
                searchResults = searchList
            )
        )
    }

    private fun AddListState.updateEntryBottomSheet(
        isOpenEntryBottomSheet: Boolean,
        entryState2: AddProductState,
        isSaveStateForEntry: Boolean,
        isTemplate: Boolean
    ): AddListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenEntry = isOpenEntryBottomSheet,
                isSaveStateForBottomSheet = isSaveStateForEntry,
            ),
            currentProduct = entryState2.copy(
                template = entryState2.template.copy(
                    isTemplate = isTemplate
                )
            )
        )
    }

    private fun AddListState.updateTitleAndSuffix(pair: Pair<String, Suffix>): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    title = pair.first,
                    countSuffix = pair.second
                ),
                errors = currentProduct.errors.copy(
                    isErrorTitle = pair.first.isBlank(),
                    isErrorSlash = pair.first.contains("/")
                )
            )
        )
    }

    private fun AddListState.updateTitle(title: String): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    title = title,
                ),
                errors = if (currentProduct.template.isTemplate) currentProduct.errors
                else currentProduct.errors.copy(
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
                product = currentProduct.product.copy(
                    count = count
                ),
                errors = if (currentProduct.template.isTemplate) currentProduct.errors
                else currentProduct.errors.copy(isErrorCount = count.isBlank())
            )
        )
    }

    private fun AddListState.updateAnimal(animal: Pair<Long, String>): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    selectedAnimalIndex = animal.first,
                    animalId = animal.first,
                    animalName = animal.second
                )
            )
        )
    }

    private fun AddListState.updateAnimalClear(animal: String): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    animalId = null,
                    animalName = animal
                )
            )
        )
    }

    private fun AddListState.updateNameTemplate(nameTemplate: String): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    name = nameTemplate
                ),
                errors = currentProduct.errors.copy(
                    isErrorNameTemplate = nameTemplate.isBlank()
                )
            )
        )
    }

    private fun AddListState.updateTitleTemplate(isTitle: Boolean): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isTitle = isTitle
                    )
                ),
                errors = currentProduct.errors.copy(
                    isErrorTitle = false,
                    isErrorSlash = false,
                )
            )
        )
    }

    private fun AddListState.updateCountTemplate(isCount: Boolean): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isCount = isCount
                    )
                ),
                errors = currentProduct.errors.copy(
                    isErrorCount = false
                )
            )
        )
    }

    private fun AddListState.updateSuffixTemplate(isSuffix: Boolean): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isSuffix = isSuffix
                    )
                )
            )
        )
    }

    private fun AddListState.updateCategoryTemplate(isCategory: Boolean): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isCategory = isCategory
                    )
                )
            )
        )
    }

    private fun AddListState.updateDateTemplate(isDate: Boolean): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isDate = isDate
                    )
                )
            )
        )
    }

    private fun AddListState.updateAnimalTemplate(isAnimal: Boolean): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isAnimal = isAnimal
                    )
                )
            )
        )
    }

    private fun AddListState.updateNoteTemplate(isNote: Boolean): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isNote = isNote
                    )
                )
            )
        )
    }

    private fun AddListState.updateMultiProjectTemplate(isMultiProjectTemplate: Boolean): AddListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isAnimal = true,
                        isMultiProjectTemplate = isMultiProjectTemplate
                    )
                )
            )
        )
    }
}


