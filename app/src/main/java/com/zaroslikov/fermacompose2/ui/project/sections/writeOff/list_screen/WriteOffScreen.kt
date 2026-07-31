package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.compose.rememberRewardedAdLoader
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoadResult
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.supportUi.TypeProduct
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.base.intent.QrCodeIntent
import com.zaroslikov.fermacompose2.base.intent.TemplateIntent
import com.zaroslikov.fermacompose2.supportFun.dateBuilder
import com.zaroslikov.fermacompose2.ui.elements.BrieflyCountCardNew
import com.zaroslikov.fermacompose2.ui.elements.DetailProductCardNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleSaleNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedWriteOffStatus
import com.zaroslikov.fermacompose2.ui.elements.WarehouseCountCard
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.сompositions.WarningDeleteBottomSheet
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedSwitch
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTemplate
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.DetailSectionBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.EmptyState
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.InventoryBody
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.WarningCard2
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.EntryBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.BaseSectionScreen
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_3

object WriteOffDestination : NavigationDestination {
    override val route = "WriteOff"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteOffScreen(
    navigateToFirstScreen: () -> Unit,
    navigateToItemProject: (Pair<Long, Boolean>) -> Unit,
    viewModel: WriteOffViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = state.ui.colors
    val primeColor = violet_1
    val priceSuffix = state.settings.currencySuffix
    val writeOffBoolean = state.isNotProduction
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
        onGroupModeClick = { viewModel.onIntent(WriteOffListIntent.GroupClicked(it)) },
        onSearchChanged = { viewModel.onIntent(WriteOffListIntent.SearchChanged(it)) },
        onAddProductClick = { viewModel.onIntent(WriteOffListIntent.OpenBottomSheetEntry(it)) },
        onQrCodeIntent = viewModel::onQrCodeIntent,
        onTemplateIntent = viewModel::onTemplateIntent,
        content = { innerPadding ->

            WriteOffContainer(
                modifier = Modifier
                    .modifierScreenLazy(innerPadding),

                details = state.mainList.isGroupMode,
                itemList = state.mainList.items,
                brieflyList = state.mainList.brieflyItems,
                searchList = state.searchState.searchResults,
                searchBrieflyList = state.searchState.searchBrieflyResults,

                isArchive = state.isArchive,
                onDetailsCardClick = {
                    viewModel.onIntent(
                        WriteOffListIntent.OpenBottomSheetDetail(
                            it
                        )
                    )
                },
                onEditClick = {
                    viewModel.onIntent(
                        WriteOffListIntent.OpenBottomSheetEntry(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(WriteOffListIntent.OpenBottomSheetDelete(it)) },
                onDetailsClick = {
                    viewModel.onIntent(
                        WriteOffListIntent.LoadDataForDetailNomenclatura(it)
                    )
                },
                color = primeColor,
                priceSuffix = priceSuffix,
                iconRes = iconRes,
                writeOffBoolean = writeOffBoolean
            )
            if (state.bottomSheetState.isOpenEntry)
                WriteOffEntryBottomSheet(
                    colors = colors,
                    state = state.currentProduct,
                    onIntent = viewModel::onIntent,
                    onTemplateIntent = viewModel::onTemplateIntent
                )
            if (state.bottomSheetState.isOpenGroup)
                BrieflyBottomSheetWriteOff(
                    iconRes = iconRes,
                    color = primeColor,
                    state = state.detailNomenclatura.detail,
                    list = state.detailNomenclatura.productItems,
                    priceSuffix = priceSuffix,
                    isArchive = state.isArchive,
                    onDismissRequest = {
                        viewModel.onIntent(WriteOffListIntent.LoadDataForDetailNomenclatura(null))
                    },
                    onEditClick = {
                        viewModel.onIntent(
                            WriteOffListIntent.OpenBottomSheetEntry(true, it)
                        )
                    },
                    onDeleteClick = { viewModel.onIntent(WriteOffListIntent.OpenBottomSheetDelete(it)) },
                )
            if (state.bottomSheetState.isOpenDetail)
                WriteOffDetailBottomSheet(
                    state = state.productDetail,
                    colors = colors,
                    isArchive = state.isArchive,
                    onIntent = viewModel::onIntent,
                    priceSuffix = state.settings.currencySuffix
                )
            if (state.bottomSheetState.isOpenProductDelete)
                WarningDeleteWriteOffBottomSheet(
                    state = state.productDetail,
                    color = colors.first(),
                    priceSuffix = priceSuffix,
                    onDismissRequest = {
                        viewModel.onIntent(WriteOffListIntent.OpenBottomSheetDelete(null))
                    },
                    onDeleteClick = { viewModel.onIntent(WriteOffListIntent.Delete) }
                )

            if (state.bottomSheetState.isOpenEntryInTemplate)
                WriteOffEnterInPatternBottomSheet(
                    colors = state.ui.colors,
                    state = state.currentProduct,
                    onIntent = viewModel::onIntent,
                    onDismissRequest = {
                        viewModel.onIntent(WriteOffListIntent.OpenTemplateBottomSheetClick(false))
                    },
                    onInsertClick = { viewModel.onIntent(WriteOffListIntent.Insert) },
                    onInsertAndScannerAgain = {
                        viewModel.onIntent(WriteOffListIntent.Insert)
                        viewModel.onQrCodeIntent(
                            QrCodeIntent.OpenScannerQrCodeBottomSheetClick(true)
                        )
                    }
                )
        })
}

@Composable
private fun WriteOffDetailBottomSheet(
    state: DomainWriteOffTable?,
    colors: List<Color>,
    isArchive: Boolean,
    onIntent: (WriteOffListIntent) -> Unit,
    priceSuffix: Suffix
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
            date = date,
            note = state.note,
            statusWriteOff = state.status,
            animalCountId = state.animalCountId,
            iconColor = violet_1,
            boxColor = Color(0xFFFAF5FF),
            colors = colors,
            isArchive = isArchive,
            onUpdateClick = { onIntent(WriteOffListIntent.OpenBottomSheetEntry(true, state.id)) },
            onDeleteClick = { onIntent(WriteOffListIntent.OpenBottomSheetDelete(state.id)) },
            onDismissRequest = { onIntent(WriteOffListIntent.OpenBottomSheetDetail(null)) }
        )
    }
}

@Composable
private fun WarningDeleteWriteOffBottomSheet(
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
    state: DomainWriteOffTable?,
    color: Color,
    priceSuffix: Suffix
) {
    WarningDeleteBottomSheet(
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDeleteClick,
        titleRes = R.string.base_section_delete_write_off,
        supportRes = R.string.base_section_delete_support_write_off,
        textButtonRes = R.string.base_section_button_delete_write_off
    ) {
        state?.let { product ->
            DetailProductCardNew(
                title = product.title,
                count = product.count,
                suffix = product.countSuffix,
                price = product.priceAll ?: product.price,
                statusWriteOff = product.status,
                category = product.category,
                productOrigin = product.productOrigin,
                note = product.note,
                color = color,
                day = product.day,
                month = product.month,
                year = product.year,
                priceSuffix = priceSuffix,
                animalCountId = product.animalCountId,
                isArchive = true,
                isCardField = false,
            )
        }
    }
}

@Composable
private fun WriteOffContainer(
    modifier: Modifier = Modifier,
    @StringRes iconRes: Int,
    color: Color,
    details: Boolean,
    priceSuffix: Suffix,
    isArchive: Boolean,
    itemList: List<DomainWriteOffTable>,
    searchList: List<DomainWriteOffTable>,
    brieflyList: List<BrieflyItem>,
    searchBrieflyList: List<BrieflyItem>,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (String) -> Unit,
    onDetailsCardClick: (Long) -> Unit,
    writeOffBoolean: Boolean
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
                productOrigin = item.productOrigin,
                statusWriteOff = item.status,
                category = item.category,
                note = item.note,
                color = color,
                day = item.day,
                month = item.month,
                year = item.year,
                typeProduct = item.animalCountId?.let { TypeProduct.ANIMAL },
                animalCountId = item.animalCountId,
                isArchive = isArchive,
                onClick = { onDetailsCardClick(item.id) },
                onEditClick = { onEditClick(item.id) },
                onDeleteClick = { onDeleteClick(item.id) },
            )
        },
        brieflyCard = { item ->
            BrieflyCountCardNew(
                modifier = Modifier,
                color = color,
                title = item.title,
                price = item.price,
                weight = item.weight,
                linear = item.linear,
                volume = item.volume,
                pieces = item.pieces,
                rowCount = item.rowCount,
                icon = iconRes,
                colorSecondary = violet_3,
                onClick = { onDetailsClick(item.title) })
        },
        detailEmptyState = EmptyState(
            title = R.string.message_no_data_title_write_off,
            message = R.string.message_no_data_message_write_off,
            support = if (writeOffBoolean) null else R.string.message_no_data_message_write_off_support,
            icon = iconRes
        ),
        iconColor = violet_1,
        backgroundColor = violet_3,
        isArchive = isArchive
    )
}


@Composable
private fun BrieflyBottomSheetWriteOff(
    @DrawableRes iconRes: Int,
    list: List<DomainWriteOffTable>,
    state: BrieflyItem?,
    color: Color,
    isArchive: Boolean,
    priceSuffix: Suffix,
    onDismissRequest: () -> Unit,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
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
                    category = product.category,
                    statusWriteOff = product.status,
                    note = product.note,
                    color = color,
                    day = product.day,
                    month = product.month,
                    year = product.year,
                    priceSuffix = priceSuffix,
                    animalCountId = product.animalCountId,
                    isArchive = isArchive,
                    onDeleteClick = { onDeleteClick(product.id) },
                    onEditClick = { onEditClick(product.id) }
                )
            }
        )
    }
}


@Composable
private fun WriteOffEntryBottomSheet(
    colors: List<Color>,
    state: WriteOffProductState,
    onIntent: (WriteOffListIntent) -> Unit,
    onTemplateIntent: (TemplateIntent) -> Unit
) {
    val product = state.product
    val errors = state.errors
    val isTemplate = state.template.isTemplate
    val template = state.template.activeField

    EntryBottomSheet(
        titleEntryRes = if (isTemplate) R.string.template_title_entry else R.string.write_off_screen_title_entry,
        titleEditRes = if (isTemplate) R.string.template_title_edit else R.string.write_off_screen_title_edit,
        isEntry = product.isEntry,
        enabledButton = errors.hasAnyError,
        colors = colors,
        onDismissRequest = {
            onIntent(
                WriteOffListIntent.OpenBottomSheetEntry(
                    isOpen = false,
                    isSaveStateForBottomSheet = product.isEntry
                )
            )
        },
        onSecondDismissRequest = { onIntent(WriteOffListIntent.OpenBottomSheetEntry(false)) },
        onInsertClick = {
            if (isTemplate) onTemplateIntent(TemplateIntent.InsertTemplate)
            else onIntent(WriteOffListIntent.Insert)
        },
        onUpdateClick = {
            if (isTemplate) onTemplateIntent(TemplateIntent.UpdateTemplate)
            else onIntent(WriteOffListIntent.Update)
        }
    ) {
        if (!product.isEntry && isTemplate)
            WarningCard2()
        if (isTemplate)
            OutlinedTextTemplate(
                value = state.template.name,
                onValueChange = { onIntent(WriteOffListIntent.NameTemplateChanged(it)) },
                isError = errors.isErrorNameTemplate,
            )
        OutlinedWriteOffStatus(
            value = product.status,
            onValueChange = { onIntent(WriteOffListIntent.StatusClicked(it)) },
            isShowSwitch = isTemplate,
            checked = template.isWriteOffStatus,
            onCheckedChange = { onIntent(WriteOffListIntent.WriteOffStatusClicked(it)) },
        )
        OutlinedTextTitleSaleNew(
            value = product.title,
            onValueChoice = {
                onIntent(WriteOffListIntent.TitleAndSuffix(it.title, it.suffix, it.productOrigin))
            },
            productOrigin = product.productOrigin,
            titleList = state.pickList.titles,
            isErrorTitle = state.errors.isErrorTitle,
            isErrorSlash = state.errors.isErrorSlash,
            intResSup = R.string.support_text_price_write_product,
            isMore = true,
            readOnly = true,
            enabled = !product.hasIndicators || !template.isTitle,
            isNecessarily = !template.isTitle,
            isShowSwitch = isTemplate,
            checked = template.isTitle,
            enabledChecked = !template.isMultiProjectTemplate,
            onCheckedChange = { onIntent(WriteOffListIntent.TitleTemplateChanged(it)) },
        )
        OutlinedTextCountNew(
            value = product.count,
            onValueChange = {
                onIntent(WriteOffListIntent.CountChanged(it))
            },
            suffix = product.countSuffix,
            suffixList = state.pickList.suffixList,
            isError = state.errors.isErrorCount,
            enabled = !product.hasIndicators,
            intResSup = R.string.support_text_count_product_write_off,

            isNecessarily = !isTemplate || !template.isCount,

            isShowSwitchForValue = isTemplate,
            checkedForValue = template.isCount,
            onCheckedForValueChange = { onIntent(WriteOffListIntent.CountTemplateChanged(it)) },

            isShowSwitchForSuffix = isTemplate,
            checkedForSuffix = template.isSuffix,
            enabledCheckedForSuffix = !template.isTitle,
            onCheckedForSuffixChange = { onIntent(WriteOffListIntent.SuffixTemplateClicked(it)) },
        )
        if (!product.hasIndicators && !isTemplate)
            WarehouseCountCard(
                title = product.title,
                warehouseList = state.pickList.warehouseList
            )
        OutlinedPriceInputNew(
            price = product.price,
            onPriceChange = {
                onIntent(WriteOffListIntent.PriceChanged(it))
            },
            isAutoCalculate = product.isAutoPrice,
            onAutoCalculate = {
                onIntent(WriteOffListIntent.AutoPriceClicked(it))
            },
            isManyCount = true,
            count = if (template.isCount) "" else product.count,
            countSuffix = if (template.isSuffix) Suffix.NO else product.countSuffix,
            priceAll = product.priceAll,
            priceSuffix = product.priceSuffix,
            supportTextRes = R.string.support_text_price_write_off_all,
            supportTextResAutoCal = R.string.support_text_price_write_off_one,
            tooltipTextResAutoCal = R.string.expenses_entry_screen_auto_calculate,

            isShowSwitch = isTemplate,
            checked = template.isPrice,
            onCheckedChange = { onIntent(WriteOffListIntent.PriceTemplateClicked(it)) },
        )
        OutlinedTextCategoryNew(
            value = product.category,
            onValueChange = { onIntent(WriteOffListIntent.CategoryChanged(it)) },
            titleList = state.pickList.categories,

            isShowSwitch = isTemplate,
            checked = template.isCategory,
            onCheckedChange = { onIntent(WriteOffListIntent.CategoryTemplateChanged(it)) },
        )
        if (!product.hasIndicators && !isTemplate)
            OutlinedTextDateNew(
                value = product.date,
                onValueChange = {
                    onIntent(WriteOffListIntent.DateClicked(it))
                }
            )
        if (isTemplate)
            OutlinedSwitch(
                checked = template.isDate,
                onCheckedChange = { onIntent(WriteOffListIntent.DateTemplateChanged(it)) },
                leadingIconRes = R.drawable.baseline_calendar_month_24,
                labelIntRes = R.string.outlined_text_current_date,
                supportingText = R.string.support_text_current_date,
            )
        OutlinedTextNoteNew(
            value = product.note,
            onValueChange = {
                onIntent(WriteOffListIntent.NoteChanged(it))
            },
            isShowSwitch = isTemplate,
            checked = template.isNote,
            onCheckedChange = { onIntent(WriteOffListIntent.NoteTemplateChanged(it)) },
        )
        if (isTemplate)
            OutlinedSwitch(
                checked = template.isMultiProjectTemplate,
                onCheckedChange = { onIntent(WriteOffListIntent.MultiProjectTemplateChanged(it)) },
                supportingText = R.string.support_text_multi_project_template_write_off
            )
    }
}




