package com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen

import androidx.annotation.DrawableRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.supportUi.TypeProduct
import com.zaroslikov.fermacompose2.base.intent.QrCodeIntent
import com.zaroslikov.fermacompose2.base.intent.TemplateIntent
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.supportFun.dateBuilder
import com.zaroslikov.fermacompose2.ui.elements.BrieflyCountCardNew
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.DetailProductCardNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextBuyerNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleSaleNew
import com.zaroslikov.fermacompose2.ui.elements.WarehouseCountCard
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.сompositions.WarningDeleteBottomSheet
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedSwitch
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTemplate
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.DetailSectionBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.EmptyState
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.InventoryBody
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.WarningCard2
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.EntryBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BaseSectionScreen

object SaleDestination : NavigationDestination {
    override val route = "Sale"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleScreen(
    navigateToFirstScreen: () -> Unit,
    navigateToItemProject: (Pair<Long, Boolean>) -> Unit,
    viewModel: SaleViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = state.ui.colors
    val iconRes = state.ui.iconRes

    val eventFlow = viewModel.navigation
    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.Navigate -> navigateToItemProject(event.value to true)
                is UiEvent.NavigateBack -> navigateToFirstScreen()
            }
        }
    }

    BaseSectionScreen(
        state = state,
        onGroupModeClick = { viewModel.onIntent(SaleListIntent.GroupClicked(it)) },
        onSearchChanged = { viewModel.onIntent(SaleListIntent.SearchChanged(it)) },
        onAddProductClick = { viewModel.onIntent(SaleListIntent.OpenBottomSheetEntry(it)) },
        onQrCodeIntent = viewModel::onQrCodeIntent,
        onTemplateIntent = viewModel::onTemplateIntent,
        content = { innerPadding ->
            SaleContainer(
                modifier = Modifier
                    .modifierScreenLazy(innerPadding),
                iconRes = iconRes,
                priceSuffix = state.settings.currencySuffix,
                details = state.mainList.isGroupMode,
                itemList = state.mainList.items,
                brieflyList = state.mainList.brieflyItems,
                searchList = state.searchState.searchResults,
                searchBrieflyList = state.searchState.searchBrieflyResults,
                isArchive = state.isArchive,
                onDetailsCardClick = { viewModel.onIntent(SaleListIntent.OpenBottomSheetDetail(it)) },
                onEditClick = {
                    viewModel.onIntent(
                        SaleListIntent.OpenBottomSheetEntry(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(SaleListIntent.OpenBottomSheetDelete(it)) },
                onDetailsClick = {
                    viewModel.onIntent(SaleListIntent.OpenBottomSheetGroup(it))
                }
            )
            if (state.bottomSheetState.isOpenEntry)
                SaleEntryBottomSheet(
                    colors = colors,
                    state = state.currentProduct,
                    onIntent = viewModel::onIntent,
                    onTemplateIntent = viewModel::onTemplateIntent
                )
            if (state.bottomSheetState.isOpenGroup)
                BrieflyBottomSheetSale(
                    state = state.detailNomenclatura.detail,
                    list = state.detailNomenclatura.productItems,
                    onDismissRequest = { viewModel.onIntent(SaleListIntent.OpenBottomSheetGroup(null)) },
                    onEditClick = {
                        viewModel.onIntent(
                            SaleListIntent.OpenBottomSheetEntry(true, it.id)
                        )
                    },
                    onDeleteClick = { viewModel.onIntent(SaleListIntent.OpenBottomSheetDelete(it)) },
                    iconRes = iconRes,
                    color = colors.first(),
                    isArchive = state.isArchive,
                )
            if (state.bottomSheetState.isOpenDetail)
                SaleDetailBottomSheet(
                    state = state.productDetail,
                    priceSuffix = state.settings.currencySuffix,
                    colors = colors,
                    onIntent = viewModel::onIntent,
                    isArchive = state.isArchive
                )
            if (state.bottomSheetState.isOpenProductDelete)
                WarningDeleteSaleBottomSheet(
                    onDismissRequest = {
                        viewModel.onIntent(SaleListIntent.OpenBottomSheetDelete(null))
                    },
                    onDeleteClick = { viewModel.onIntent(SaleListIntent.Delete) },
                    state = state.productDetail,
                    color = colors.first(),
                    priceSuffix = state.settings.currencySuffix
                )
            if (state.bottomSheetState.isOpenEntryInTemplate)
                SaleEnterInPatternBottomSheet(
                    colors = state.ui.colors,
                    state = state.currentProduct,
                    onIntent = viewModel::onIntent,
                    onDismissRequest = {
                        viewModel.onIntent(SaleListIntent.OpenTemplateBottomSheetClick(false))
                    },
                    onInsertClick = { viewModel.onIntent(SaleListIntent.Insert) },
                    onInsertAndScannerAgain = {
                        viewModel.onIntent(SaleListIntent.Insert)
                        viewModel.onQrCodeIntent(QrCodeIntent.OpenScannerQrCodeBottomSheetClick(true))
                    }
                )
        })
}

@Composable
private fun SaleDetailBottomSheet(
    state: DomainSaleTable?,
    priceSuffix: Suffix,
    colors: List<Color>,
    onIntent: (SaleListIntent) -> Unit,
    isArchive: Boolean
) {
    state?.let {
        val monthText = stringResource(id = monthToResString(state.month))
        val date = dateBuilder(state.day, monthText, state.year)
        DetailSectionBottomSheet(
            title = state.title,
            count = state.count,
            countSuffix = state.countSuffix,
            price = state.price,
            priceAll = state.priceAll,
            priceSuffix = priceSuffix,
            category = state.category,
            productOrigin = state.productOrigin,
            buyer = state.buyer ?: stringResource(R.string.animal_card_screen_sale_note_no_buyer),
            date = date,
            note = state.note,
            iconColor = blue_1,
            animalCountId = state.animalCountId,
            boxColor = Color(0xFFEFF6FF),
            colors = colors,
            isArchive = isArchive,
            onUpdateClick = { onIntent(SaleListIntent.OpenBottomSheetEntry(true, state.id)) },
            onDeleteClick = { onIntent(SaleListIntent.OpenBottomSheetDelete(state.id)) },
            onDismissRequest = { onIntent(SaleListIntent.OpenBottomSheetDetail(null)) },
        )
    }
}

@Composable
private fun WarningDeleteSaleBottomSheet(
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
    state: DomainSaleTable?,
    color: Color,
    priceSuffix: Suffix
) {
    WarningDeleteBottomSheet(
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDeleteClick,
        titleRes = R.string.base_section_delete_sale,
        supportRes = R.string.base_section_delete_support_sale,
        textButtonRes = R.string.base_section_button_delete_sale
    ) {
        state?.let { product ->
            DetailProductCardNew(
                title = product.title,
                count = product.count,
                suffix = product.countSuffix,
                price = product.priceAll ?: product.price,
                priceSuffix = priceSuffix,
                category = product.category,
                productOrigin = product.productOrigin,
                note = product.note,
                buyer = product.buyer,
                color = color,
                day = product.day,
                month = product.month,
                year = product.year,
                animalCountId = product.animalCountId,
                typeProduct = product.animalCountId?.let { TypeProduct.ANIMAL },
                isArchive = true,
                isCardField = false
            )
        }
    }
}

@Composable
private fun SaleContainer(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    details: Boolean,
    priceSuffix: Suffix,
    color: Color = blue_1,
    isArchive: Boolean,
    itemList: List<DomainSaleTable>,
    searchList: List<DomainSaleTable>,
    brieflyList: List<BrieflyItem>,
    searchBrieflyList: List<BrieflyItem>,
    onDetailsCardClick: (Long) -> Unit,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (String) -> Unit
) {
    InventoryBody(
        modifier = modifier,
        details = details,
        itemList = itemList,
        searchList = searchList,
        brieflyList = brieflyList, searchBrieflyList = searchBrieflyList,
        detailCard = { index, item ->
            DetailProductCardNew(
                title = item.title,
                count = item.count,
                suffix = item.countSuffix,
                price = item.priceAll ?: item.price,
                priceSuffix = priceSuffix,
                category = item.category,
                productOrigin = item.productOrigin,
                note = item.note,
                color = color,
                day = item.day,
                month = item.month,
                year = item.year,
                buyer = item.buyer,
                animalCountId = item.animalCountId,
                typeProduct = item.animalCountId?.let { TypeProduct.ANIMAL },
                isArchive = isArchive,
                onClick = { onDetailsCardClick(item.id) },
                onEditClick = { onEditClick(item.id) },
                onDeleteClick = { onDeleteClick(item.id) },
            )
        },
        brieflyCard = { item ->
            BrieflyCountCardNew(
                modifier = Modifier,
                title = item.title,
                weight = item.weight,
                linear = item.linear,
                volume = item.volume,
                icon = iconRes,
                color = color,
                colorSecondary = Color(0xFFEFF6FF),
                onClick = { onDetailsClick(item.title) },
                price = item.price,
                pieces = item.pieces,
                rowCount = item.rowCount,
            )
        },
        detailEmptyState = EmptyState(
            title = R.string.message_no_data_title_sale,
            message = R.string.message_no_data_message_sale,
            icon = iconRes
        ),
        iconColor = blue_1,
        backgroundColor = blue_3, isArchive = isArchive
    )
}

@Composable
private fun BrieflyBottomSheetSale(
    @DrawableRes iconRes: Int,
    list: List<DomainSaleTable>,
    color: Color = blue_1,
    state: BrieflyItem?,
    isArchive: Boolean,
    onEditClick: (DomainSaleTable) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDismissRequest: () -> Unit,
) {
    state?.let { currentBriefly ->
        BrieflyBottomSheetUniversal(
            list = list,
            title = currentBriefly.title,
            price = currentBriefly.price,
            weight = currentBriefly.weight,
            linear = currentBriefly.linear,
            volume = currentBriefly.volume,
            pieces = currentBriefly.pieces,
            iconRes = iconRes,
            onDismissRequest = onDismissRequest,
            itemCard = { product ->
                DetailProductCardNew(
                    isCardField = false,
                    count = product.count,
                    suffix = product.countSuffix,
                    price = product.priceAll ?: product.price,
                    priceSuffix = product.priceSuffix,
                    note = product.note,
                    buyer = product.buyer,
                    color = color,
                    day = product.day,
                    month = product.month,
                    year = product.year,
                    animalCountId = product.animalCountId,
                    isArchive = isArchive,
                    typeProduct = product.animalCountId?.let { TypeProduct.ANIMAL },
                    onDeleteClick = { onDeleteClick(product.id) },
                    onEditClick = { onEditClick(product) },
                )
            }
        )
    }
}

@Composable
private fun SaleEntryBottomSheet(
    colors: List<Color>,
    state: SaleProductState,
    onIntent: (SaleListIntent) -> Unit,
    onTemplateIntent: (TemplateIntent) -> Unit
) {
    val product = state.product
    val errors = state.errors
    val isTemplate = state.template.isTemplate
    val template = state.template.activeField

    EntryBottomSheet(
        titleEntryRes = if (isTemplate) R.string.template_title_entry else R.string.sale_screen_title_entry,
        titleEditRes = if (isTemplate) R.string.template_title_edit else R.string.sale_screen_title_edit,
        isEntry = product.isEntry,
        enabledButton = errors.hasAnyError,
        colors = colors,
        onDismissRequest = {
            onIntent(
                SaleListIntent.OpenBottomSheetEntry(
                    isOpen = false,
                    isSaveStateForBottomSheet = product.isEntry
                )
            )
        },
        onSecondDismissRequest = {
            onIntent(SaleListIntent.OpenBottomSheetEntry(false))
        },
        onInsertClick = {
            if (isTemplate) onTemplateIntent(TemplateIntent.InsertTemplate)
            else onIntent(SaleListIntent.Insert)
        },
        onUpdateClick = {
            if (isTemplate) onTemplateIntent(TemplateIntent.UpdateTemplate)
            else onIntent(SaleListIntent.Update)
        }
    ) {
        if (!product.isEntry && isTemplate)
            WarningCard2()
        if (isTemplate)
            OutlinedTextTemplate(
                value = state.template.name,
                onValueChange = { onIntent(SaleListIntent.NameTemplateChanged(it)) },
                isError = errors.isErrorNameTemplate,
            )
        OutlinedTextTitleSaleNew(
            value = product.title,
            onValueChange = {
                onIntent(SaleListIntent.TitleChanged(it))
            },
            onValueChoice = {
                onIntent(
                    SaleListIntent.TitleAndSuffixClicked(
                        it.title,
                        it.suffix,
                        it.productOrigin
                    )
                )
            },
            productOrigin = product.productOrigin,
            titleList = state.pickList.titles,
            isErrorTitle = errors.isErrorTitle,
            isErrorSlash = errors.isErrorSlash,
            readOnly = product.hasIndicators,
            enabled = !product.hasIndicators,
            isNecessarily = !template.isTitle,

            isShowSwitch = isTemplate,
            checked = template.isTitle,
            onCheckedChange = { onIntent(SaleListIntent.TitleTemplateChanged(it)) },
        )
        OutlinedTextCountNew(
            value = product.count,
            onValueChange = {
                onIntent(SaleListIntent.CountChanged(it))
            },
            suffix = product.countSuffix,
            onSuffixChange = { onIntent(SaleListIntent.SuffixClicked(it)) },
            isError = errors.isErrorCount,
            enabled = !product.hasIndicators,
            intResSup = R.string.support_text_count_product,

            isNecessarily = !isTemplate || !template.isCount,

            isShowSwitchForValue = isTemplate,
            checkedForValue = template.isCount,
            onCheckedForValueChange = { onIntent(SaleListIntent.CountTemplateChanged(it)) },

            isShowSwitchForSuffix = isTemplate,
            checkedForSuffix = template.isSuffix,
            onCheckedForSuffixChange = { onIntent(SaleListIntent.SuffixTemplateClicked(it)) },
        )
        if (!product.hasIndicators && !isTemplate)
            WarehouseCountCard(
                title = product.title,
                warehouseList = state.pickList.warehouseList
            )
        OutlinedPriceInputNew(
            price = product.price,
            onPriceChange = {
                onIntent(SaleListIntent.PriceChanged(it))
            },
            isAutoCalculate = product.isAutoPrice,
            onAutoCalculate = {
                onIntent(SaleListIntent.AutoPriceClicked(it))
            },
            isManyCount = true,
            isError = errors.isErrorPrice,
            isNecessarily = true,
            count = if (template.isCount) "" else product.count,
            countSuffix = if (template.isSuffix) Suffix.NO else product.countSuffix,
            priceAll = if (template.isCount) "-" else product.priceAll,
            priceSuffix = product.priceSuffix,

            isShowSwitch = isTemplate,
            checked = template.isPrice,
            onCheckedChange = { onIntent(SaleListIntent.PriceTemplateClicked(it)) },
        )
        OutlinedTextCategoryNew(
            value = product.category,
            onValueChange = { onIntent(SaleListIntent.CategoryChanged(it)) },
            titleList = state.pickList.categories,
            isShowSwitch = isTemplate,
            checked = template.isCategory,
            onCheckedChange = { onIntent(SaleListIntent.CategoryTemplateChanged(it)) },
        )
        if (!product.hasIndicators && !isTemplate)
            OutlinedTextDateNew(
                value = product.date,
                onValueChange = { onIntent(SaleListIntent.DateClicked(it)) }
            )
        if (isTemplate)
            OutlinedSwitch(
                checked = template.isDate,
                onCheckedChange = { onIntent(SaleListIntent.DateTemplateChanged(it)) },
                leadingIconRes = R.drawable.baseline_calendar_month_24,
                labelIntRes = R.string.outlined_text_current_date,
                supportingText = R.string.support_text_current_date,
            )
        OutlinedTextBuyerNew(
            value = product.buyer,
            onValueChange = {
                onIntent(SaleListIntent.BuyerChanged(it))
            },
            list = state.pickList.buyers,

            isShowSwitch = isTemplate,
            checked = template.isBuyer,
            onCheckedChange = { onIntent(SaleListIntent.BuyerTemplateChanged(it)) },
        )
        OutlinedTextNoteNew(
            value = product.note,
            onValueChange = { onIntent(SaleListIntent.NoteChanged(it)) },
            isShowSwitch = isTemplate,
            checked = template.isNote,
            onCheckedChange = { onIntent(SaleListIntent.NoteTemplateChanged(it)) },
        )
        if (isTemplate)
            OutlinedSwitch(
                checked = template.isMultiProjectTemplate,
                onCheckedChange = { onIntent(SaleListIntent.MultiProjectTemplateChanged(it)) }
            )
    }
}