package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen

import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.base.intent.QrCodeIntent
import com.zaroslikov.fermacompose2.base.intent.TemplateIntent
import com.zaroslikov.fermacompose2.base.reduce.SectionReducer
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.supportFun.toSuffixList
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningType
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeData
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateItem
import com.zaroslikov.fermacompose2.utils.ResourceProvider

class WriteOffListReduce(
    private val resourceProvider: ResourceProvider
) : SectionReducer<WriteOffListState, WriteOffListIntent>() {
    override fun reducer(
        state: WriteOffListState,
        intent: WriteOffListIntent
    ): WriteOffListState {
        return when (intent) {
            is WriteOffListIntent.RefreshEntryBottomSheetState -> state.updateEntryBottomSheet(
                intent.isOpen,
                intent.state,
                intent.isSaveStateForBottomSheet,
                intent.isTemplate
            ).updateValid()

            is WriteOffListIntent.RefreshWarehouseCount -> state.updateWarehouseCount(intent.value)

            is WriteOffListIntent.StatusClicked -> state.updateStatus(intent.value)
            is WriteOffListIntent.TitleAndSuffix ->
                state.updateTitleAndSuffix(
                    intent.title,
                    intent.suffix,
                    intent.writeOffProductOrigin
                )
                    .updateValid()

            is WriteOffListIntent.CountChanged ->
                state.updateCount(intent.value).updatePriceAll().updateValid()

            is WriteOffListIntent.SuffixClicked -> state.updateSuffix(intent.value)

            is WriteOffListIntent.PriceChanged ->
                state.updatePrice(intent.value).updatePriceAll()

            is WriteOffListIntent.AutoPriceClicked ->
                state.updateIsAutoPrice(intent.value).updatePriceAll()

            is WriteOffListIntent.CategoryChanged ->
                state.updateCategory(intent.value)

            is WriteOffListIntent.DateClicked -> state.updateDate(intent.value)
            is WriteOffListIntent.NoteChanged -> state.updateNote(intent.value)


            is WriteOffListIntent.SearchChanged -> state.updateSearch(intent.value)
            is WriteOffListIntent.GroupClicked -> state.updateGroup(intent.value)
            is WriteOffListIntent.OpenBottomSheetDetail -> state.updateOpenBottomSheetDetail(intent.value)
            is WriteOffListIntent.OpenBottomSheetDelete -> state.updateOpenBottomSheetDelete(intent.value)

            //Template
            is WriteOffListIntent.NameTemplateChanged -> state.updateNameTemplate(intent.value)
                .updateValid()

            is WriteOffListIntent.WriteOffStatusClicked -> state.updateWriteOffStatusTemplate(intent.value)

            is WriteOffListIntent.TitleTemplateChanged -> state.updateTitleTemplate(intent.value)
                .updateValid()

            is WriteOffListIntent.CountTemplateChanged -> state.updateCountTemplate(intent.value)
                .updateValid()

            is WriteOffListIntent.PriceTemplateClicked -> state.updatePriceTemplate(intent.value)
            is WriteOffListIntent.SuffixTemplateClicked -> state.updateSuffixTemplate(intent.value)
            is WriteOffListIntent.CategoryTemplateChanged -> state.updateCategoryTemplate(intent.value)
            is WriteOffListIntent.DateTemplateChanged -> state.updateDateTemplate(intent.value)
            is WriteOffListIntent.NoteTemplateChanged -> state.updateNoteTemplate(intent.value)
            is WriteOffListIntent.MultiProjectTemplateChanged -> state.updateMultiProjectTemplate(
                intent.value
            )

            //Templates
            is WriteOffListIntent.OpenTemplateBottomSheetClick ->
                state.updateOpenEntryInTemplate(intent.value, intent.toUiMap23).updateValid()

            else -> state
        }
    }

    override fun qrReducer(
        state: WriteOffListState,
        intent: QrCodeIntent
    ): WriteOffListState {
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
        state: WriteOffListState,
        intent: TemplateIntent
    ): WriteOffListState {
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

    private fun WriteOffListState.updateOpenBottomSheetDetail(id: Long?): WriteOffListState {
        return if (id == null)
            copy(
                bottomSheetState = bottomSheetState.copy(
                    isOpenDetail = false
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

    private fun WriteOffListState.updateOpenBottomSheetDelete(id: Long?): WriteOffListState {
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

    private fun WriteOffListState.updateValid(): WriteOffListState {
        val product = currentProduct.product
        val template = currentProduct.template
        val baseValid =
            when {
                template.isTemplate -> template.name.isNotBlank() &&
                        (template.activeField.isTitle || product.title.isNotBlank()
                                || !product.title.isSlash()) &&
                        (template.activeField.isCount || product.count.isNotBlank())

                else -> product.title.isNotBlank() &&
                        !product.title.isSlash() &&
                        product.count.isNotBlank()

            }
        return copy(
            currentProduct = currentProduct.copy(
                errors = currentProduct.errors.copy(
                    hasAnyError = baseValid
                )
            )
        )
    }

    private fun WriteOffListState.updateEntryBottomSheet(
        isOpenEntryBottomSheet: Boolean,
        entryState2: WriteOffProductState,
        isSaveStateForEntry: Boolean,
        isTemplate: Boolean
    ): WriteOffListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenEntry = isOpenEntryBottomSheet,
                isSaveStateForBottomSheet = isSaveStateForEntry
            ),
            currentProduct = entryState2.copy(
                template = entryState2.template.copy(
                    isTemplate = isTemplate
                )
            )
        )
    }

    private fun WriteOffListState.updateTitleAndSuffix(
        title: String,
        suffix: Suffix,
        writeOffProductOrigin: ProductOrigin
    ): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    title = title,
                    countSuffix = suffix,
                    productOrigin = writeOffProductOrigin
                ),
                pickList = currentProduct.pickList.copy(suffixList = suffix.toSuffixList()),
                errors = currentProduct.errors.copy(
                    isErrorTitle = title.isBlank(),
                    isErrorSlash = title.isSlash()
                )
            )
        )
    }

    private fun WriteOffListState.updateCount(count: String): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    count = count
                ),
                errors = currentProduct.errors.copy(
                    isErrorCount = count.isBlank()
                )
            )
        )
    }

    private fun WriteOffListState.updateSuffix(countSuffix: Suffix): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    countSuffix = countSuffix
                )
            )
        )
    }

    private fun WriteOffListState.updatePrice(price: String): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(price = price)
            )
        )
    }

    private fun WriteOffListState.updateIsAutoPrice(isAutoPrice: Boolean): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(isAutoPrice = isAutoPrice)
            )
        )
    }

    private fun WriteOffListState.updatePriceAll(): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    priceAll = if (currentProduct.product.isAutoPrice) (
                            currentProduct.product.price.toConvertZeroDouble() *
                                    currentProduct.product.count.toConvertZeroDouble()).formatNumber()
                    else "0"
                )
            )
        )
    }

    private fun WriteOffListState.updateCategory(category: String): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(category = category)
            )
        )
    }

    private fun WriteOffListState.updateDate(date: String): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(date = date)
            )
        )
    }

    private fun WriteOffListState.updateNote(note: String): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(product = currentProduct.product.copy(note = note))
        )
    }

    private fun WriteOffListState.updateStatus(status: Boolean): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(product = currentProduct.product.copy(status = status))
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
            mainList = mainList.copy(
                isGroupMode = isGroup
            )
        )
    }

    private fun WriteOffListState.updateSearch(search: String): WriteOffListState {
        val query = search.trim().lowercase()

        val searchList = if (query.isBlank() && !mainList.isGroupMode) mainList.items
        else
            mainList.items.filter { item ->
                val category = item.category ?: ""
                item.title.lowercase().contains(query) ||
                        item.note.lowercase().contains(query) ||
                        category.lowercase().contains(query) ||
                        item.count.toString().lowercase().contains(query) ||
                        resourceProvider.getString(item.countSuffix.toResId()).lowercase()
                            .contains(query)
                "${item.day} ${resourceProvider.getString(monthToResString(item.month))} ${item.year}".lowercase()
                    .contains(query) ||
                        (item.priceAll ?: item.price).toString().lowercase().contains(query)
            }

        val searchBrieflyList = if (query.isBlank() && mainList.isGroupMode) mainList.brieflyItems
        else
            mainList.brieflyItems.filter { item ->
                item.title.lowercase().contains(query) ||
                        item.volume.toString().lowercase().contains(query) ||
                        (item.price).toString().lowercase().contains(query)
            }
        return copy(
            searchState = searchState.copy(
                searchQuery = search,
                searchBrieflyResults = searchBrieflyList,
                searchResults = searchList
            )
        )
    }

    private fun WriteOffListState.updateNameTemplate(nameTemplate: String): WriteOffListState {
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

    private fun WriteOffListState.updateWriteOffStatusTemplate(isWriteOffStatus: Boolean): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isWriteOffStatus = isWriteOffStatus
                    )
                )
            )
        )
    }

    private fun WriteOffListState.updateTitleTemplate(isTitle: Boolean): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isTitle = isTitle,
                        isSuffix = isTitle
                    )
                )
            )
        )
    }

    private fun WriteOffListState.updateCountTemplate(isCount: Boolean): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isCount = isCount
                    )
                )
            )
        )
    }

    private fun WriteOffListState.updatePriceTemplate(isPrice: Boolean): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isPrice = isPrice
                    )
                )
            )
        )
    }

    private fun WriteOffListState.updateSuffixTemplate(isSuffix: Boolean): WriteOffListState {
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

    private fun WriteOffListState.updateCategoryTemplate(isCategory: Boolean): WriteOffListState {
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

    private fun WriteOffListState.updateDateTemplate(isDate: Boolean): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isCategory = isDate
                    )
                )
            )
        )
    }

    private fun WriteOffListState.updateNoteTemplate(isNote: Boolean): WriteOffListState {
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

    private fun WriteOffListState.updateMultiProjectTemplate(isMultiProjectTemplate: Boolean): WriteOffListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isMultiProjectTemplate = isMultiProjectTemplate,
                        isTitle = isMultiProjectTemplate,
                        isSuffix = isMultiProjectTemplate
                    )
                )
            )
        )
    }

    private fun WriteOffListState.updateOpenEntryInTemplate(
        isOpenEntryInTemplateBottomSheet: Boolean,
        value: WriteOffProductState,
    ): WriteOffListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenEntryInTemplate = isOpenEntryInTemplateBottomSheet
            ),
            currentProduct = value,
        )
    }

    private fun WriteOffListState.updateOpenWarningQrCode(
        bool: Boolean,
        warning: QrCodeWarningType,
        backupData: DomainTemplateTable?
    ): WriteOffListState {
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

    private fun WriteOffListState.updateOpenScannerQrCode(
        bool: Boolean,
    ): WriteOffListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenScannerQrCode = bool
            )
        )
    }

    private fun WriteOffListState.updateOpenTemplateDeleteBottomSheet(id: Long?): WriteOffListState {
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

    private fun WriteOffListState.updateOpenPatternBottomSheet(isOpenTemplates: Boolean): WriteOffListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenTemplates = isOpenTemplates
            )
        )
    }

    private fun WriteOffListState.updateLoadDataForTemplate(templateItems: List<TemplateItem>): WriteOffListState {
        return copy(
            templatesState = templatesState.copy(
                templatesList = templateItems
            )
        )
    }

    private fun WriteOffListState.updateOpenQrCodeBottomSheet(
        isOpenCreateQrCode: Boolean,
        qrCodeState: QrCodeData?
    ): WriteOffListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenCreateQrCode = isOpenCreateQrCode,
            ),
            qrCodeState = qrCodeState,
        )
    }
}
