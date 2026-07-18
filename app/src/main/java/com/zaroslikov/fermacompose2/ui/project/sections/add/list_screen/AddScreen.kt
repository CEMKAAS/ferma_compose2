@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.dto.add.DomainAddItemDto2
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.supportUi.TypeProduct
import com.zaroslikov.fermacompose2.alabaster
import com.zaroslikov.fermacompose2.green_11
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.green_g_1
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.supportFun.dateBuilder
import com.zaroslikov.fermacompose2.ui.elements.BrieflyCountCardNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.DetailProductCardNew
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextAnimalNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCategoryNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigationNew
import com.zaroslikov.fermacompose2.ui.elements.WarehouseCountCard
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.сompositions.WarningDeleteBottomSheet
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedSwitch
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTemplate
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.EnterInPatternBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.TemplatesBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrCodeWarningBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.QrScannerScreen
import com.zaroslikov.fermacompose2.ui.elements.bottomSheet.TemplateCard
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyBottomSheetUniversal
import com.zaroslikov.fermacompose2.ui.project.sections.BrieflyItem
import com.zaroslikov.fermacompose2.ui.project.sections.DetailSectionBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.EmptyState
import com.zaroslikov.fermacompose2.ui.project.sections.InventoryBody
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.EntryBottomSheet
import io.appmetrica.analytics.AppMetrica

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    viewModel: AddViewModel = hiltViewModel(),
    navigateToFirstScreen: () -> Unit,
    navigateToItemProject: (Pair<Long, Boolean>) -> Unit,
    navigationToAnalysis: (Triple<Long, String, Suffix>) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val state by viewModel.state.collectAsStateWithLifecycle()
    val idProject = state.idPT
    val eventFlow = viewModel.navigation
    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.Navigate -> navigateToItemProject(event.value to true)
                is UiEvent.NavigateBack -> navigateToFirstScreen()
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNavigationNew(
                value = state.searchState.searchQuery,
                isGroup = state.mainList.isGroupMode,
                onValueChange = { viewModel.onIntent(AddListIntent.SearchChanged(it)) },
                onClick = { viewModel.onIntent(AddListIntent.GroupClicked(it)) },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (!state.isArchive)
                NeonGlowFab(
                    colors = state.ui.colors,
                    onClick = { viewModel.onIntent(AddListIntent.OpenBottomSheetEntry(true)) },
                    onLongClick = {
                        viewModel.onIntent(
                            AddListIntent.OpenPatternsBottomSheetClick(true)
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
            AddContainer2(
                modifier = Modifier
                    .modifierScreenLazy(innerPadding),
                iconRes = state.ui.iconRes,
                itemList = state.mainList.items,
                searchList = state.searchState.searchResults,
                brieflyList = state.mainList.brieflyItems,
                details = state.mainList.isGroupMode,
                searchBrieflyList = state.searchState.searchBrieflyResults,
                isArchive = state.isArchive,
                onDetailsCardClick = { viewModel.onIntent(AddListIntent.OpenBottomSheetDetail(it)) },
                onEditClick = {
                    viewModel.onIntent(
                        AddListIntent.OpenBottomSheetEntry(true, it)
                    )
                },
                onDeleteClick = { viewModel.onIntent(AddListIntent.OpenBottomSheetDelete(it)) },
                onDetailsClick = {
                    viewModel.onIntent(
                        AddListIntent.LoadDataForDetailNomenclatura(it)
                    )
                })
        if (state.bottomSheetState.isOpenEntry)
            AddEntryBottomSheet(
                state = state.currentProduct,
                colors = state.ui.colors,
                onIntent = viewModel::onIntent
            )
        if (state.bottomSheetState.isOpenGroup)
            BrieflyBottomSheetAdd(
                iconRes = state.ui.iconRes,
                list = state.detailNomenclatura.productItems,
                state = state.detailNomenclatura.detail,
                onDismissRequest = { viewModel.onIntent(AddListIntent.OpenBottomSheetGroup(false)) },
                onEditClick = {
                    viewModel.onIntent(
                        AddListIntent.OpenBottomSheetEntry(true, it)
                    )
                },
                isArchive = state.isArchive,
                onDeleteClick = { viewModel.onIntent(AddListIntent.OpenBottomSheetDelete(it)) },
                onAnalysisClick = {
                    navigationToAnalysis(Triple(idProject, it.first, it.second))
                    AppMetrica.reportEvent("Переход в полный анализ продукта")
                }
            )
        if (state.bottomSheetState.isOpenDetail)
            AddDetailBottomSheet(
                state = state.productDetail,
                colors = state.ui.colors,
                onIntent = viewModel::onIntent,
                isArchive = state.isArchive
            )
        if (state.bottomSheetState.isOpenProductDelete)
            WarningDeleteAddBottomSheet(
                onDismissRequest = { viewModel.onIntent(AddListIntent.OpenBottomSheetDelete(null)) },
                onDeleteClick = { viewModel.onIntent(AddListIntent.Delete) },
                state = state.productDetail,
            )


        if (state.bottomSheetState.isOpenTemplateDelete)
            WarningDeleteTemplateBottomSheet(
                iconRes = state.ui.iconRes,
                onDismissRequest = {
                    viewModel.onIntent(AddListIntent.OpenTemplateDeleteBottomSheet(null))
                },
                onDeleteClick = { viewModel.onIntent(AddListIntent.DeleteTemplate) },
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
                    viewModel.onIntent(AddListIntent.SetPinOfTemplateClick(it))
                },
                onDismissRequest = {
                    viewModel.onIntent(AddListIntent.OpenPatternsBottomSheetClick(value = false))
                },
                onCreatePatternClick = {
                    viewModel.onIntent(
                        AddListIntent.OpenBottomSheetEntry(isOpen = true, isTemplate = true)
                    )
                },
                onChoicePatternClick = {
                    viewModel.onIntent(AddListIntent.LoadDataForTemplateBottomSheetClick(it))
                },
                onEditTemplateClick = {
                    viewModel.onIntent(
                        AddListIntent.OpenBottomSheetEntry(
                            isOpen = true,
                            id = it,
                            isTemplate = true
                        )
                    )
                },
                onCreateQrCodeClick = { viewModel.onIntent(AddListIntent.CreateQrCodeClick(it)) },
                onDeleteTemplateClick = {
                    viewModel.onIntent(AddListIntent.OpenTemplateDeleteBottomSheet(it))
                }
            )
        if (state.bottomSheetState.isOpenCreateQrCode)
            QrCodeBottomSheet(
                colors = state.ui.colors,
                qrCodeData = state.qrCodeState,
            ) { viewModel.onIntent(AddListIntent.OpenQrCodeBottomSheetClick(false)) }

        if (state.bottomSheetState.isOpenEntryInTemplate)
            EnterInPatternBottomSheet(
                colors = state.ui.colors,
                addProductState = state.currentProduct,
                onDismissRequest = {
                    viewModel.onIntent(AddListIntent.OpenTemplateBottomSheetClick(false))
                },
                onTitleChange = { viewModel.onIntent(AddListIntent.TitleChanged(it)) },
                onTitleAndSuffix = { viewModel.onIntent(AddListIntent.TitleAndSuffix(it)) },
                onCountChange = { viewModel.onIntent(AddListIntent.CountChanged(it)) },
                onSuffixChange = { viewModel.onIntent(AddListIntent.SuffixClicked(it)) },
                onCategoryChange = { viewModel.onIntent(AddListIntent.CategoryChanged(it)) },
                onAnimalChange = { viewModel.onIntent(AddListIntent.Animal(it)) },
                onAnimalClearChange = { viewModel.onIntent(AddListIntent.AnimalClear(it)) },
                onNoteChange = { viewModel.onIntent(AddListIntent.NoteChanged(it)) },
                onInsertClick = { viewModel.onIntent(AddListIntent.Insert) },
                onInsertAndScannerAgain = {
                    viewModel.onIntent(AddListIntent.Insert)
                    viewModel.onIntent(AddListIntent.OpenScannerQrCodeBottomSheetClick(true))
                }
            )
        if (state.bottomSheetState.isOpenWarningQrCode)
            QrCodeWarningBottomSheet(
                backupData = state.qrCodeWarning.templateBackup,
                qrCodeWarningType = state.qrCodeWarning.warningType,
                onDismissRequest = {
                    viewModel.onIntent(AddListIntent.OpenWarningQrCodeBottomSheetClick(false))
                },
                onScannerClick = {
                    viewModel.onIntent(AddListIntent.OpenScannerQrCodeBottomSheetClick(true))
                },
                onRecoverClick = {
                    viewModel.onIntent(AddListIntent.RecoverClick(it))
                }
            )
        if (state.bottomSheetState.isOpenScannerQrCode)
            QrScannerScreen(
                onQrDetected = { viewModel.onIntent(AddListIntent.QrDetected(it)) },
                onDismissRequest = {
                    viewModel.onIntent(AddListIntent.OpenScannerQrCodeBottomSheetClick(false))
                }
            )
    }
}

@Composable
private fun AddDetailBottomSheet(
    state: DomainAddItemDto2?,
    colors: List<Color>,
    onIntent: (AddListIntent) -> Unit,
    isArchive: Boolean
) {
    state?.let {
        val monthText = stringResource(id = monthToResString(state.month))
        val date = dateBuilder(state.day, monthText, state.year)
        DetailSectionBottomSheet(
            title = state.title,
            count = state.count,
            countSuffix = state.countSuffix,
            category = state.category,
            date = date,
            animal = state.nameAnimal,
            note = state.note,
            animalId = state.animalCountId,
            iconColor = green_shamrock,
            boxColor = alabaster,
            colors = colors,
            isArchive = isArchive,
            onUpdateClick = { onIntent(AddListIntent.OpenBottomSheetEntry(true, state.id)) },
            onDeleteClick = { onIntent(AddListIntent.OpenBottomSheetDelete(state.id)) },
            onDismissRequest = { onIntent(AddListIntent.OpenBottomSheetDetail(null)) },
        )
    }
}

@Composable
private fun WarningDeleteAddBottomSheet(
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
    state: DomainAddItemDto2?,
) {
    WarningDeleteBottomSheet(
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDeleteClick
    ) {
        state?.let { product ->
            DetailProductCardNew(
                isCardField = false,
                title = product.title,
                count = product.count,
                suffix = product.countSuffix,
                category = product.category,
                note = product.note,
                animal = product.nameAnimal,
                color = green_shamrock,
                day = product.day,
                month = product.month,
                year = product.year,
                isArchive = true
            )
        }
    }
}

@Composable
fun WarningDeleteTemplateBottomSheet(
    @DrawableRes iconRes: Int,
    iconColor: Color,
    iconBorderColor: Color,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
    templateItem: TemplateItem?,
) {
    WarningDeleteBottomSheet(
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDeleteClick
    ) {
        templateItem?.let { template ->
            TemplateCard(
                iconRes = iconRes,
                title = template.name,
                value = template.description.ifBlank { null },
                pin = template.isPinned,
                iconColor = iconColor,
                iconBorderColor = iconBorderColor,
                isMultiProject = template.isMultiProject
            )
        }
    }
}

@Composable
fun AddContainer2(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    details: Boolean,
    isArchive: Boolean,
    itemList: List<DomainAddItemDto2>,
    searchList: List<DomainAddItemDto2>,
    brieflyList: List<BrieflyItem>,
    searchBrieflyList: List<BrieflyItem>,
    onDetailsCardClick: (Long) -> Unit,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (String) -> Unit
) {
    InventoryBody(
        modifier = modifier,
        itemList = itemList,
        searchList = searchList,
        brieflyList = brieflyList,
        searchBrieflyList = searchBrieflyList,
        detailCard = { index, item ->
            DetailProductCardNew(
                title = item.title,
                count = item.count,
                suffix = item.countSuffix,
                category = item.category,
                note = item.note,
                animal = item.nameAnimal,
                color = green_shamrock,
                day = item.day,
                month = item.month,
                year = item.year,
                typeProduct = item.animalCountId?.let { TypeProduct.KILL },
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
                color = green_shamrock,
                colorSecondary = alabaster,
                onClick = { onDetailsClick(item.title) },
                price = item.price,
                pieces = item.pieces,
                rowCount = item.rowCount,
            )
        },
        detailEmptyState = EmptyState(
            title = R.string.message_no_data_title_add,
            message = R.string.message_no_data_message_add,
            icon = iconRes
        ),
        details = details,
        iconColor = green_shamrock,
        backgroundColor = green_g_1, isArchive = isArchive
    )
}

@Composable
fun BrieflyBottomSheetAdd(
    @DrawableRes iconRes: Int,
    list: List<DomainAddItemDto2>,
    isArchive: Boolean,
    state: BrieflyItem?,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDismissRequest: () -> Unit,
    onAnalysisClick: (Pair<String, Suffix>) -> Unit = {},
) {
    state?.let { currentBriefly ->
        BrieflyBottomSheetUniversal(
            list = list,
            iconRes = iconRes,
            title = currentBriefly.title,
            price = null,
            weight = currentBriefly.weight,
            linear = currentBriefly.linear,
            volume = currentBriefly.volume,
            pieces = currentBriefly.pieces,
            onDismissRequest = onDismissRequest,
            onAnalysisClick = { onAnalysisClick(currentBriefly.title to it) }, //TODO
            itemCard = { product ->
                DetailProductCardNew(
                    modifier = Modifier,
                    isCardField = false,
                    count = product.count,
                    suffix = product.countSuffix,
                    category = product.category,
                    note = product.note,
                    animal = product.nameAnimal,
                    color = green_shamrock,
                    day = product.day,
                    month = product.month,
                    year = product.year,
                    isArchive = isArchive,
                    onDeleteClick = { onDeleteClick(product.id) },
                    onEditClick = { onEditClick(product.id) },
                    onClick = { }
                )
            })
    }
}

@Composable
fun AddEntryBottomSheet(
    state: AddProductState,
    colors: List<Color>,
    onIntent: (AddListIntent) -> Unit
) {
    val template = state.template.activeField
    val product = state.product
    val errors = state.errors
    val isTemplate = state.template.isTemplate

    EntryBottomSheet(
        titleEntryRes = if (isTemplate) R.string.template_title_entry else R.string.add_screen_title_entry,
        titleEditRes = if (isTemplate) R.string.template_title_edit else R.string.add_screen_title_edit,
        isEntry = isTemplate,
        enabledButton = errors.hasAnyError,
        colors = colors,
        onDismissRequest = {
            onIntent(
                AddListIntent.OpenBottomSheetEntry(
                    isOpen = false,
                    isSaveStateForBottomSheet = product.isEntry
                )
            )
        },
        onSecondDismissRequest = {
            onIntent(AddListIntent.OpenBottomSheetEntry(false))
        },
        onInsertClick = {
            if (isTemplate) onIntent(AddListIntent.InsertTemplate)
            else onIntent(AddListIntent.Insert)
        },
        onUpdateClick = {
            if (isTemplate) onIntent(AddListIntent.UpdateTemplate)
            else onIntent(AddListIntent.Update)
        },
    ) {
        if (!product.isEntry && isTemplate)
            WarningCard2()
        if (isTemplate)
            OutlinedTextTemplate(
                value = state.template.name,
                onValueChange = { onIntent(AddListIntent.NameTemplateChanged(it)) },
                isError = errors.isErrorNameTemplate,
            )
        OutlinedTextTitleAddNew(
            value = product.title,
            onValueChange = { onIntent(AddListIntent.TitleChanged(it)) },
            onValueChangeSuffix = { onIntent(AddListIntent.TitleAndSuffix(it)) },
            titleList = state.pickList.titles,
            isErrorTitle = errors.isErrorTitle,
            isErrorSlash = errors.isErrorSlash,
            drawableRes = R.drawable.icon_add_product,

            isShowSwitch = isTemplate,
            checked = template.isTitle,
            isNecessarily = !isTemplate || !template.isTitle,
            onCheckedChange = { onIntent(AddListIntent.TitleTemplateChanged(it)) },
        )
        OutlinedTextCountNew(
            value = product.count,
            onValueChange = {
                onIntent(AddListIntent.CountChanged(it))
            },
            suffix = product.countSuffix,
            onSuffixChange = { onIntent(AddListIntent.SuffixClicked(it)) },
            isError = errors.isErrorCount,
            intResSup = R.string.support_text_count_product,
            isNecessarily = !isTemplate || !template.isCount,

            isShowSwitchForValue = isTemplate,
            checkedForValue = template.isCount,
            onCheckedForValueChange = { onIntent(AddListIntent.CountTemplateChanged(it)) },

            isShowSwitchForSuffix = isTemplate,
            checkedForSuffix = template.isSuffix,
            onCheckedForSuffixChange = { onIntent(AddListIntent.SuffixTemplateClicked(it)) },

            )
        if (!product.hasIndicators && !isTemplate)
            WarehouseCountCard(
                title = product.title,
                warehouseList = state.pickList.warehouseList
            )
        OutlinedTextCategoryNew(
            value = product.category,
            onValueChange = { onIntent(AddListIntent.CategoryChanged(it)) },
            titleList = state.pickList.categories,
            isShowSwitch = isTemplate,
            checked = template.isCategory,
            onCheckedChange = { onIntent(AddListIntent.CategoryTemplateChanged(it)) },
        )
        if (!product.hasIndicators && !isTemplate)
            OutlinedTextDateNew(
                value = product.date,
                onValueChange = { onIntent(AddListIntent.Date(it)) }
            )
        if (!product.hasIndicators && state.pickList.animals.isNotEmpty())
            OutlinedTextAnimalNew(
                value = product.animalName,
                onValueChange = { onIntent(AddListIntent.Animal(it)) },
                selectedAnimalIndex = product.selectedAnimalIndex,
                onClickClear = { onIntent(AddListIntent.AnimalClear(it)) },
                animalList = state.pickList.animals,
                enabledChecked = !template.isMultiProjectTemplate,
                isShowSwitch = isTemplate,
                checked = template.isAnimal,
                onCheckedChange = { onIntent(AddListIntent.AnimalTemplateChanged(it)) },
            )
        OutlinedTextNoteNew(
            value = product.note,
            onValueChange = { onIntent(AddListIntent.NoteChanged(it)) },
            isShowSwitch = isTemplate,
            checked = template.isNote,
            onCheckedChange = { onIntent(AddListIntent.NoteTemplateChanged(it)) },
        )
        if (isTemplate)
            OutlinedSwitch(
                checked = template.isMultiProjectTemplate,
                onCheckedChange = { onIntent(AddListIntent.MultiProjectTemplateChanged(it)) }
            )
    }
}

@Composable
fun WarningCard2() {
    WarningCard(
        colorBackground = price_green_2,
        colorBorder = green_11,
        colorIcon = price_green,
        colorIconBackground = green_8,
        colorTitle = green_9,
        colorText = green_9,
        icon = R.drawable.outline_qr_code_24,
        title = R.string.warning_qr_code_card_title,
        text = R.string.warning_qr_code_card_title_support
    )
}

enum class Page {
    WRITE_OFF, SALE, ADD, EXPENSES, ANIMAL
}