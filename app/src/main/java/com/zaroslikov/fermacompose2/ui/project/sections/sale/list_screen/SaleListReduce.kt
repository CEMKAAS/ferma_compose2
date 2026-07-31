package com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen

import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.R
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
import kotlin.text.lowercase

class SaleListReduce(
    private val resourceProvider: ResourceProvider
) : SectionReducer<SaleListState, SaleListIntent>() {
    override fun reducer(
        state: SaleListState,
        intent: SaleListIntent
    ): SaleListState {
        return when (intent) {
            is SaleListIntent.RefreshEntryBottomSheetState -> state.updateEntryBottomSheet(
                isOpenEntryBottomSheet = intent.isOpen,
                isSaveStateForEntry = intent.isSaveStateForBottomSheet,
                entryState2 = intent.state,
                isTemplate = intent.isTemplate
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
            is SaleListIntent.DateClicked -> state.updateDate(intent.value)
            is SaleListIntent.NoteChanged -> state.updateNote(intent.value)

            //Template
            is SaleListIntent.NameTemplateChanged -> state.updateNameTemplate(intent.value)
                .updateValid()

            is SaleListIntent.TitleTemplateChanged -> state.updateTitleTemplate(intent.value)
                .updateValid()

            is SaleListIntent.CountTemplateChanged -> state.updateCountTemplate(intent.value)
                .updateValid()

            is SaleListIntent.PriceTemplateClicked -> state.updatePriceTemplate(intent.value)
                .updateValid()

            is SaleListIntent.SuffixTemplateClicked -> state.updateSuffixTemplate(intent.value)
            is SaleListIntent.CategoryTemplateChanged -> state.updateCategoryTemplate(intent.value)
            is SaleListIntent.DateTemplateChanged -> state.updateDateTemplate(intent.value)
            is SaleListIntent.BuyerTemplateChanged -> state.updateBuyerTemplate(intent.value)
            is SaleListIntent.NoteTemplateChanged -> state.updateNoteTemplate(intent.value)
            is SaleListIntent.MultiProjectTemplateChanged -> state.updateMultiProjectTemplate(intent.value)

            //Templates
            is SaleListIntent.OpenTemplateBottomSheetClick ->
                state.updateOpenEntryInTemplate(intent.value, intent.toUiMap23).updateValid()

            else -> state
        }
    }

    override fun qrReducer(
        state: SaleListState,
        intent: QrCodeIntent
    ): SaleListState {
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
        state: SaleListState,
        intent: TemplateIntent
    ): SaleListState {
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


    private fun SaleListState.updateOpenBottomSheetDetail(
        id: Long?
    ): SaleListState {
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

    private fun SaleListState.updateOpenBottomSheetDelete(id: Long?): SaleListState {
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


    private fun SaleListState.updateValid(): SaleListState {
        val product = currentProduct.product
        val template = currentProduct.template

        val baseValid =
            when {
                template.isTemplate -> template.name.isNotBlank() &&
                        (template.activeField.isTitle || product.title.isNotBlank()
                                || !product.title.isSlash()) &&
                        (template.activeField.isCount || product.count.isNotBlank()) &&
                        (template.activeField.isPrice || product.price.isNotBlank())

                else -> product.title.isNotBlank() &&
                        !product.title.isSlash() &&
                        product.count.isNotBlank() &&
                        product.price.isNotBlank()
            }

        return copy(
            currentProduct = currentProduct.copy(
                errors = currentProduct.errors.copy(
                    hasAnyError = baseValid
                )
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
                product = currentProduct.product.copy(
                    title = title,
                    productOrigin = productOriginProduct,
                    countSuffix = suffix
                ),
                pickList = currentProduct.pickList.copy(suffixList = suffix.toSuffixList()),
                errors = currentProduct.errors.copy(
                    isErrorTitle = title.isBlank(),
                    isErrorSlash = title.contains("/")
                )
            )
        )
    }

    private fun SaleListState.updateTitle(title: String): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    title = title
                ),
                errors = currentProduct.errors.copy(
                    isErrorTitle = title.isBlank(),
                    isErrorSlash = title.contains("/")
                )
            )
        )
    }

    private fun SaleListState.updateCount(count: String): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    count = count
                ),
                errors = currentProduct.errors.copy(isErrorCount = count.isBlank())
            )
        )
    }

    private fun SaleListState.updatePrice(price: String): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    price = price
                ),
                errors = currentProduct.errors.copy(
                    isErrorPrice = price.isBlank()
                )
            )
        )
    }

    private fun SaleListState.updateIsAutoPrice(isAutoPrice: Boolean): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    isAutoPrice = isAutoPrice
                )
            )
        )
    }

    private fun SaleListState.updatePriceAll(): SaleListState {
        val product = currentProduct.product
        return copy(
            currentProduct = currentProduct.copy(
                product = product.copy(
                    priceAll = if (product.isAutoPrice)
                        (product.price.toConvertZeroDouble() * product.count.toConvertZeroDouble()).formatNumber()
                    else "0"
                ),
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
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    countSuffix = suffix
                )
            )
        )
    }

    private fun SaleListState.updateCategory(category: String): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    category = category
                )
            )
        )
    }

    private fun SaleListState.updateDate(date: String): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    date = date
                )
            )
        )
    }

    private fun SaleListState.updateBuyer(buyer: String): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    buyer = buyer
                )
            )
        )
    }

    private fun SaleListState.updateNote(note: String): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                product = currentProduct.product.copy(
                    note = note
                )
            )
        )
    }

    private fun SaleListState.updateGroup(isGroupMode: Boolean): SaleListState {
        return copy(
            mainList = mainList.copy(
                isGroupMode = isGroupMode
            )
        )
    }

    private fun SaleListState.updateSearch(textSearch: String): SaleListState {
        val query = textSearch.trim().lowercase()

        val searchResults = if (query.isBlank() && !mainList.isGroupMode) mainList.items
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
                            .contains(query) ||
                        item.buyer?.lowercase()?.contains(query) == true ||
                        (item.priceAll ?: item.price).toString().lowercase().contains(query)
            }

        val searchBrieflyResults =
            if (query.isBlank() && mainList.isGroupMode) mainList.brieflyItems
            else
                mainList.brieflyItems.filter { item ->
                    item.title.lowercase().contains(query) ||
                            item.weight?.value.toString().lowercase().contains(query) ||
                            (item.price).toString().lowercase().contains(query)
                }
        return copy(
            searchState = searchState.copy(
                searchQuery = textSearch,
                searchResults = searchResults,
                searchBrieflyResults = searchBrieflyResults
            )
        )
    }

    private fun SaleListState.updateEntryBottomSheet(
        isOpenEntryBottomSheet: Boolean,
        entryState2: SaleProductState,
        isSaveStateForEntry: Boolean,
        isTemplate: Boolean
    ): SaleListState {
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

    //Entry Template
    private fun SaleListState.updateNameTemplate(nameTemplate: String): SaleListState {
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

    private fun SaleListState.updateTitleTemplate(isTitle: Boolean): SaleListState {
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

    private fun SaleListState.updateCountTemplate(isCount: Boolean): SaleListState {
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

    private fun SaleListState.updatePriceTemplate(isPrice: Boolean): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isPrice = isPrice
                    )
                ),
                errors = currentProduct.errors.copy(
                    isErrorPrice = false
                )
            )
        )
    }

    private fun SaleListState.updateSuffixTemplate(isSuffix: Boolean): SaleListState {
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

    private fun SaleListState.updateCategoryTemplate(isCategory: Boolean): SaleListState {
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

    private fun SaleListState.updateDateTemplate(isDate: Boolean): SaleListState {
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

    private fun SaleListState.updateBuyerTemplate(isBuyer: Boolean): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isBuyer = isBuyer
                    )
                )
            )
        )
    }

    private fun SaleListState.updateNoteTemplate(isNote: Boolean): SaleListState {
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

    private fun SaleListState.updateMultiProjectTemplate(isMultiProjectTemplate: Boolean): SaleListState {
        return copy(
            currentProduct = currentProduct.copy(
                template = currentProduct.template.copy(
                    activeField = currentProduct.template.activeField.copy(
                        isMultiProjectTemplate = isMultiProjectTemplate
                    )
                )
            )
        )
    }

    private fun SaleListState.updateOpenEntryInTemplate(
        isOpenEntryInTemplateBottomSheet: Boolean,
        value: SaleProductState,
    ): SaleListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenEntryInTemplate = isOpenEntryInTemplateBottomSheet
            ),
            currentProduct = value,
        )
    }

    private fun SaleListState.updateOpenWarningQrCode(
        bool: Boolean,
        warning: QrCodeWarningType,
        backupData: DomainTemplateTable?
    ): SaleListState {
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

    private fun SaleListState.updateOpenScannerQrCode(
        bool: Boolean,
    ): SaleListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenScannerQrCode = bool
            )
        )
    }

    private fun SaleListState.updateOpenTemplateDeleteBottomSheet(id: Long?): SaleListState {
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

    private fun SaleListState.updateOpenPatternBottomSheet(isOpenTemplates: Boolean): SaleListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenTemplates = isOpenTemplates
            )
        )
    }

    private fun SaleListState.updateLoadDataForTemplate(templateItems: List<TemplateItem>): SaleListState {
        return copy(
            templatesState = templatesState.copy(
                templatesList = templateItems
            )
        )
    }

    private fun SaleListState.updateOpenQrCodeBottomSheet(
        isOpenCreateQrCode: Boolean,
        qrCodeState: QrCodeData?
    ): SaleListState {
        return copy(
            bottomSheetState = bottomSheetState.copy(
                isOpenCreateQrCode = isOpenCreateQrCode,
            ),
            qrCodeState = qrCodeState,
        )
    }
}