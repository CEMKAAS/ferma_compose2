package com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.supportUi.TypeProduct
import com.zaroslikov.fermacompose2.alabaster
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.supportFun.dateBuilder
import com.zaroslikov.fermacompose2.ui.elements.BrieflyCountCardNew
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.DetailProductCardNew
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextBuyerNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleSaleNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigationNew
import com.zaroslikov.fermacompose2.ui.elements.WarehouseCountCard
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.сompositions.WarningDeleteBottomSheet
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedSwitch
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTemplate
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.EnterInPatternBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrScannerScreen
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.TemplatesBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.DetailSectionBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.EmptyState
import com.zaroslikov.fermacompose2.ui.project.sections.InventoryBody
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddProductState
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.WarningCard2
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.WarningDeleteTemplateBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.EntryBottomSheet
import kotlin.math.log

object SaleDestination : NavigationDestination {
    override val route = "Sale"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleScreen(
    viewModel: SaleViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = state.ui.colors
    val iconRes = state.ui.iconRes

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNavigationNew(
                value = state.searchState.searchQuery,
                isGroup = state.mainList.isGroupMode,
                onValueChange = { viewModel.onIntent(SaleListIntent.SearchChanged(it)) },
                onClick = { viewModel.onIntent(SaleListIntent.GroupClicked(it)) },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (!state.isArchive)
                NeonGlowFab(
                    colors = state.ui.colors,
                    onClick = { viewModel.onIntent(SaleListIntent.OpenBottomSheetEntry(true)) },
                    onLongClick = {
                        viewModel.onIntent(
                            SaleListIntent.OpenPatternsBottomSheetClick(true)
                        )
                    }
                )
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
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
                priceSuffix = state.settings.currencySuffix,
                state = state.currentProduct,
                onIntent = viewModel::onIntent
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
                onDismissRequest = { viewModel.onIntent(SaleListIntent.OpenBottomSheetDelete(null)) },
                onDeleteClick = { viewModel.onIntent(SaleListIntent.Delete) },
                state = state.productDetail,
                color = colors.first(),
                priceSuffix = state.settings.currencySuffix
            )


        if (state.bottomSheetState.isOpenTemplateDelete)
            WarningDeleteTemplateBottomSheet(
                iconRes = state.ui.iconRes,
                onDismissRequest = {
                    viewModel.onIntent(SaleListIntent.OpenTemplateDeleteBottomSheet(null))
                },
                onDeleteClick = { viewModel.onIntent(SaleListIntent.DeleteTemplate) },
                templateItem = state.templatesState.templateToDelete,
                iconColor = green_shamrock,
                iconBorderColor = alabaster,
            )
        if (state.bottomSheetState.isOpenTemplates)
            TemplatesBottomSheet(
                list = state.templatesState.templatesList,
                iconRes = state.ui.iconRes,
                colors = state.ui.colors,
                iconColor = green_shamrock,
                iconBorderColor = alabaster,
                onSetPinnedClick = {
                    viewModel.onIntent(SaleListIntent.SetPinOfTemplateClick(it))
                },
                onDismissRequest = {
                    viewModel.onIntent(SaleListIntent.OpenPatternsBottomSheetClick(value = false))
                },
                onCreatePatternClick = {
                    viewModel.onIntent(
                        SaleListIntent.OpenBottomSheetEntry(isOpen = true, isTemplate = true)
                    )
                },
                onChoicePatternClick = {
                    viewModel.onIntent(SaleListIntent.LoadDataForTemplateBottomSheetClick(it))
                },
                onEditTemplateClick = {
                    viewModel.onIntent(
                        SaleListIntent.OpenBottomSheetEntry(
                            isOpen = true,
                            id = it,
                            isTemplate = true
                        )
                    )
                },
                onCreateQrCodeClick = { viewModel.onIntent(SaleListIntent.CreateQrCodeClick(it)) },
                onDeleteTemplateClick = {
                    viewModel.onIntent(SaleListIntent.OpenTemplateDeleteBottomSheet(it))
                }
            )
        if (state.bottomSheetState.isOpenCreateQrCode)
            QrCodeBottomSheet(
                colors = state.ui.colors,
                qrCodeData = state.qrCodeState,
            ) { viewModel.onIntent(SaleListIntent.OpenQrCodeBottomSheetClick(false)) }

        if (state.bottomSheetState.isOpenEntryInTemplate)
            EnterInPatternBottomSheet(
                colors = state.ui.colors,
                addProductState = AddProductState(),
                onDismissRequest = {
                    viewModel.onIntent(SaleListIntent.OpenTemplateBottomSheetClick(false))
                },
                onTitleChange = { viewModel.onIntent(SaleListIntent.TitleChanged(it)) },
                onTitleAndSuffix = { /*viewModel.onIntent(SaleListIntent.TitleAndSuffixClicked(it))*/ },
                onCountChange = { viewModel.onIntent(SaleListIntent.CountChanged(it)) },
                onSuffixChange = { viewModel.onIntent(SaleListIntent.SuffixClicked(it)) },
                onCategoryChange = { viewModel.onIntent(SaleListIntent.CategoryChanged(it)) },

                onNoteChange = { viewModel.onIntent(SaleListIntent.NoteChanged(it)) },
                onInsertClick = { viewModel.onIntent(SaleListIntent.Insert) },
                onInsertAndScannerAgain = {
                    viewModel.onIntent(SaleListIntent.Insert)
                    viewModel.onIntent(SaleListIntent.OpenScannerQrCodeBottomSheetClick(true))
                },
                onBuyerChange = { viewModel.onIntent(SaleListIntent.BuyerChanged(it)) },
            )
        if (state.bottomSheetState.isOpenWarningQrCode)
            QrCodeWarningBottomSheet(
                backupData = state.qrCodeWarning.templateBackup,
                qrCodeWarningType = state.qrCodeWarning.warningType,
                onDismissRequest = {
                    viewModel.onIntent(SaleListIntent.OpenWarningQrCodeBottomSheetClick(false))
                },
                onScannerClick = {
                    viewModel.onIntent(SaleListIntent.OpenScannerQrCodeBottomSheetClick(true))
                },
                onRecoverClick = {
                    viewModel.onIntent(SaleListIntent.RecoverClick(it))
                }
            )
        if (state.bottomSheetState.isOpenScannerQrCode)
            QrScannerScreen(
                onQrDetected = { viewModel.onIntent(SaleListIntent.QrDetected(it)) },
                onDismissRequest = {
                    viewModel.onIntent(SaleListIntent.OpenScannerQrCodeBottomSheetClick(false))
                }
            )

    }
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
    priceSuffix: Suffix,
    state: SaleProductState,
    onIntent: (SaleListIntent) -> Unit
) {
    val product = state.product
    val errors = state.errors
    val isTemplate = state.template.isTemplate
    val template = state.template.activeField
    Log.i("sale_entry", "SaleEntryBottomSheet_State: $product¬  ")
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
            if (isTemplate) onIntent(SaleListIntent.InsertTemplate)
            else onIntent(SaleListIntent.Insert)
        },
        onUpdateClick = {
            if (isTemplate) onIntent(SaleListIntent.UpdateTemplate)
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
        Log.i("sale_entry", "enabled: ${!product.isIndicatorsValue || !template.isTitle}")
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
            readOnly = product.isIndicatorsValue || template.isTitle,
            enabled = !product.isIndicatorsValue || !template.isTitle,
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
            enabled = !product.isIndicatorsValue,
            intResSup = R.string.support_text_count_product,

            isNecessarily = !isTemplate || !template.isCount,

            isShowSwitchForValue = isTemplate,
            checkedForValue = template.isCount,
            onCheckedForValueChange = { onIntent(SaleListIntent.CountTemplateChanged(it)) },

            isShowSwitchForSuffix = isTemplate,
            checkedForSuffix = template.isSuffix,
            onCheckedForSuffixChange = { onIntent(SaleListIntent.SuffixTemplateClicked(it)) },
        )
        if (!product.isIndicatorsValue)
            WarehouseCountCard(
                title = product.title,
                warehouseList = state.pickList.warehouseList
            )
        OutlinedPriceInputNew(
            price = product.price,
            onPriceChange = {
                onIntent(SaleListIntent.PriceChanged(it))
            },
            priceAll = product.priceAll,
            isError = errors.isErrorPrice,
            isAutoCalculate = product.isAutoPrice,
            onAutoCalculate = {
                onIntent(SaleListIntent.AutoPriceClicked(it))
            },
            isNecessarily = true,
            isManyCount = true,
            count = product.count,
            countSuffix = product.countSuffix,
            priceSuffix = priceSuffix,

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
        if (!product.isIndicatorsValue)
            OutlinedTextDateNew(
                value = product.date,
                onValueChange = { onIntent(SaleListIntent.DateClicked(it)) }
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