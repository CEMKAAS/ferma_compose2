package com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.zaroslikov.domain.models.animal.breed.DomainAnimalBreed
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.ui.elements.icon.drawableRes
import com.zaroslikov.domain.models.list.suffixPiecesList
import com.zaroslikov.domain.models.list.suffixWeightDayList
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.project.sections.animal.group.GroupSexCountFields
import com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.card.GroupHeadsChips
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_4
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyPhase
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyProgress
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.StrokeCap
import com.zaroslikov.fermacompose2.animal_2
import com.zaroslikov.fermacompose2.animal_3
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_8
import com.zaroslikov.fermacompose2.green_g_3
import com.zaroslikov.fermacompose2.green_g_4
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard.ArchivePregnancyDialog
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardClips
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.empty_list.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.CountColorGradientCard
import com.zaroslikov.fermacompose2.ui.elements.DateFactoryCardNew
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.AnimalNameOutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextAnimalBreedNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenuEdit
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextAnimalTypeNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextSexNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigationNew
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.WarningDeleteBottomSheet
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.EmptyState
import com.zaroslikov.fermacompose2.ui.project.sections.baseComposable.InventoryBody
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.EntryBottomSheet
import com.zaroslikov.fermacompose2.ui.project.sections.animal.herd.AnimalHerdListCard
import com.zaroslikov.fermacompose2.ui.project.sections.animal.group.MergeAnimalsBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.TemplateFab
import com.zaroslikov.fermacompose2.red_14
import java.io.File


object AnimalDestination : NavigationDestination {
    override val route = "animal"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalListScreen(
    navigateToItemCard: (Pair<Long, Long>) -> Unit = {},
    navigateToHerdCard: (Pair<Long, Long>) -> Unit = {},
    viewModel: AnimalListViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = listOf(animal_1, animal_2)
    val primeColor = animal_2

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNavigationNew(
                value = state.textSearch,
                isGroup = state.isArchive,
                isAnimal = true,
                onValueChange = { viewModel.onIntent(AnimalListIntent.SearchChanged(it)) },
                onClick = { viewModel.onIntent(AnimalListIntent.ArchiveClicked(it)) },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (!state.isArchiveProject)
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TemplateFab(iconRes = R.drawable.ic_herd, color = colors.first()) {
                        viewModel.onIntent(AnimalListIntent.OpenMergeSheet(true))
                    }
                    NeonGlowFab(colors = colors) {
                        viewModel.onIntent(AnimalListIntent.OpenBottomSheetEntry(true))
                    }
                }
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            AnimalContainer(
                modifier = Modifier
                    .modifierScreenLazy(innerPadding),
                itemList = state.list,
                color = primeColor,
                details = state.isArchive,
                searchList = state.searchList,
                brieflyList = state.archiveList,
                searchBrieflyList = state.searchArchiveList,
                isArchiveProject = state.isArchiveProject,
                onClick = { navigateToItemCard(state.idPT to it) },
                onHerdClick = { navigateToHerdCard(state.idPT to it) },
                onArchiveClick = {
                    viewModel.onIntent(AnimalListIntent.Archive(it.first, it.second))
                },
                onDeleteClick = { viewModel.onIntent(AnimalListIntent.OpenBottomSheetDelete(it)) },
                onGroupClick = { viewModel.onIntent(AnimalListIntent.OpenMergeSheet(true, it)) }
            )
        if (state.merge.isOpen)
            MergeAnimalsBottomSheet(
                state = state.merge,
                colors = colors,
                onDismissRequest = { viewModel.onIntent(AnimalListIntent.OpenMergeSheet(false)) },
                onCandidateToggled = { viewModel.onIntent(AnimalListIntent.MergeCandidateToggled(it)) },
                onModeChanged = { viewModel.onIntent(AnimalListIntent.MergeModeChanged(it)) },
                onGroupChosen = { viewModel.onIntent(AnimalListIntent.MergeGroupChosen(it)) },
                onNameChanged = { viewModel.onIntent(AnimalListIntent.MergeNameChanged(it)) },
                onConfirm = { viewModel.onIntent(AnimalListIntent.MergeConfirmed) }
            )
        if (state.openBottomSheetEntry)
            AnimalEntryBottomSheet(
                state = state.currentProduct,
                priceSuffix = state.settings.currencySuffix,
                colors = colors,
                onIntent = viewModel::onIntent,
            )
        if (state.isOpenBottomSheetDelete)
            WarningDeleteAnimalBottomSheet(
                state = state.currentAnimal,
                hasPregnancyHistory = state.deleteHasPregnancyHistory,
                onDismissRequest = { viewModel.onIntent(AnimalListIntent.OpenBottomSheetDelete(null)) },
                onDeleteClick = { viewModel.onIntent(AnimalListIntent.Delete) },
            )
        state.archivePregnancyAnimalId?.let { animalId ->
            ArchivePregnancyDialog(
                onDismissRequest = { viewModel.onIntent(AnimalListIntent.OpenArchivePregnancyDialog(null)) },
                onConfirm = { viewModel.onIntent(AnimalListIntent.ArchivePregnancyConfirmed(animalId, it)) }
            )
        }
    }
}

@Composable
private fun WarningDeleteAnimalBottomSheet(
    state: AnimalListUi,
    hasPregnancyHistory: Boolean,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit
) {
    WarningDeleteBottomSheet(
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDeleteClick,
        titleRes = R.string.base_section_delete_animal,
        supportRes = R.string.base_section_delete_support_animal,
        textRes = R.string.base_section_warning_animal,
        textButtonRes = R.string.base_section_button_delete_animal
    ) {
        AnimalCard(
            animal = state,
            isArchive = true
        )
        if (hasPregnancyHistory)
            Text(
                text = stringResource(R.string.pregnancy_delete_history_warning),
                style = text_14,
                color = red_14
            )
    }
}


@Composable
internal fun AnimalEntryBottomSheet(
    colors: List<Color>,
    state: AnimalEntryState2,
    onIntent: (AnimalListIntent) -> Unit,
    priceSuffix: Suffix
) {
    EntryBottomSheet(
        isEntry = true,
        enabledButton = state.hasAnyError,
        colors = colors,
        onDismissRequest = {
            onIntent(
                AnimalListIntent.OpenBottomSheetEntry(
                    false,
                    isSaveStateForBottomSheet = true
                )
            )
        },
        onSecondDismissRequest = {
            onIntent(AnimalListIntent.OpenBottomSheetEntry(false))
        },
        onInsertClick = { onIntent(AnimalListIntent.Insert) },
        titleEntryRes = R.string.animal_entry_screen_title_entry,
        titleEditRes = R.string.animal_entry_screen_title_edit
    ) {
        GroupCard(
            titleRes = R.string.animal_entry_screen_animal,
            iconOneRes = R.drawable.baseline_pets_24,
            iconTwoRes = R.drawable.baseline_spoke_24,
            textOneRes = R.string.ration_button_one,
            textTwoRes = R.string.ration_button_many,
            isSecondValue = state.isAnimalGroup,
            onClick = { onIntent(AnimalListIntent.AnimalGroupClicked(it)) },
        )
        AnimalNameOutlinedTextNew(
            value = state.title,
            onValueChange = {
                onIntent(AnimalListIntent.TitleChanged(it))
            },
            isAnimalGroup = state.isAnimalGroup,
            isErrorTitle = state.error.isErrorTitle
        )
        OutlinedTextAnimalTypeNew(
            value = state.type,
            onValueChange = {
                onIntent(AnimalListIntent.TypeChanged(it))
            },
            list = remember(state.typeList, state.isAnimalGroup, state.sex) {
                AnimalSpecies.suggestions(state.isAnimalGroup, state.sex, state.typeList)
            },
            intResSup = if (state.isAnimalGroup) R.string.support_text_type_animals else R.string.support_text_type_animal,
        )
        OutlinedTextAnimalBreedNew(
            value = state.breed,
            onValueChange = {
                onIntent(AnimalListIntent.BreedChanged(it))
            },
            list = remember(state.breedList, state.type) {
                DomainAnimalBreed.suggestions(state.breedList, state.type)
            },
            isAnimalGroup = state.isAnimalGroup
        )
        if (state.isAnimalGroup)
            OutlinedTextCountNew(
                value = state.count,
                onValueChange = {
                    onIntent(AnimalListIntent.CountChanged(it))
                },
                suffix = state.countSuffix,
                onSuffixChange = { onIntent(AnimalListIntent.SuffixClicked(it)) },
                suffixList = suffixPiecesList,
                isError = state.error.isErrorCount,
                drawableRes = R.drawable.baseline_spoke_24,
                intResSup = R.string.support_text_count_animals,
            )
        if (state.isAnimalGroup)
            GroupSexCountFields(
                females = state.femalesCount,
                males = state.malesCount,
                onFemalesChange = { onIntent(AnimalListIntent.FemalesCountChanged(it)) },
                onMalesChange = { onIntent(AnimalListIntent.MalesCountChanged(it)) },
                isError = state.error.isErrorSexCount
            )
        if (!state.isAnimalGroup)
            OutlinedTextSexNew(
                value = state.sex,
                onValueChange = {
                    onIntent(AnimalListIntent.SexClicked(it))
                },
            )
        OutlinedPriceInputNew(
            price = state.price,
            onPriceChange = {
                onIntent(AnimalListIntent.PriceChanged(it))
            },
            isAutoCalculate = state.isAutoPrice,
            onAutoCalculate = {
                onIntent(AnimalListIntent.AutoPriceClicked(it))
            },
            isManyCount = state.isAnimalGroup,
            count = state.count,
            countSuffix = state.countSuffix,
            priceAll = state.priceAll,
            priceSuffix = priceSuffix,
            supportTextRes = if (state.isAnimalGroup) R.string.support_text_type_animals else R.string.support_text_price_animal,
            supportTextResAutoCal = R.string.support_text_price_animal,
            tooltipTextResAutoCal = R.string.tooltip_auto_calculate_animal,
        )
        OutlinedTextDateNew(
            value = state.dateBorn,
            intRes = R.string.outlined_text_date_born,
            intResSup = if (!state.isAnimalGroup) R.string.outlined_text_date_born
            else R.string.support_text_date_born_s,
            onValueChange = {
                onIntent(AnimalListIntent.DateClicked(it))
            }
        )
        DateFactoryCardNew(
            dateBoring = state.dateBorn,
            dateFactory = state.dateFactory,
            isDateFactory = state.isDateFactory,
            dateFactoryClicked = { onIntent(AnimalListIntent.DateFactoryClicked(it)) },
            dateFactoryChanged = { onIntent(AnimalListIntent.DateFactoryChanged(it)) },
            intTitle = if (!state.isAnimalGroup) R.string.checkbox_born else R.string.checkbox_born_s,
            intTooltip = R.string.tooltip_animals_born,
            intRes = R.string.outlined_text_date_factory,
            intResSup = if (!state.isAnimalGroup) R.string.support_text_date_factory else R.string.support_text_date_factory_s,
        )
        OutlinedTextCountNew(
            value = state.foodDay,
            onValueChange = {
                onIntent(AnimalListIntent.FoodDayChanged(it))
            },
            suffix = state.foodDaySuffix,
            onSuffixChange = {
                onIntent(AnimalListIntent.FoodDaySuffixClicked(it))
            },
            isNecessarily = false,
            suffixList = suffixWeightDayList,
            isError = false,
            drawableRes = R.drawable.outline_restaurant_24,
            intRes = R.string.outlined_food_day_animals,
            intResSup = if (!state.isAnimalGroup) R.string.support_text_food_day_animal else R.string.support_text_food_day_animals,
        )
        OutlinedTextNoteNew(
            value = state.note,
            onValueChange = {
                onIntent(AnimalListIntent.NoteChanged(it))
            }
        )
    }
}


@Composable
private fun AnimalContainer(
    modifier: Modifier = Modifier,
    color: Color,
    details: Boolean,
    isArchiveProject: Boolean,
    itemList: List<AnimalListItem>,
    searchList: List<AnimalListItem>,
    brieflyList: List<AnimalListUi>,
    searchBrieflyList: List<AnimalListUi>,
    onClick: (Long) -> Unit,
    onHerdClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onArchiveClick: (Pair<Long, Boolean>) -> Unit,
    onGroupClick: (Long) -> Unit,
) {
    InventoryBody(
        modifier = modifier,
        details = details,
        itemList = itemList,
        searchList = searchList,
        brieflyList = brieflyList, searchBrieflyList = searchBrieflyList,
        detailCard = { _, row ->
            when (row) {
                is AnimalListItem.Herd -> AnimalHerdListCard(herd = row.herd) { onHerdClick(row.herd.id) }
                is AnimalListItem.Animal -> {
                    val item = row.animal
                    AnimalCard(
                        animal = item,
                        onClick = { onClick(item.id) },
                        onArchiveClick = { onArchiveClick(item.id to true) },
                        isArchive = isArchiveProject,
                        onDeleteClick = { onDeleteClick(item.id) },
                        // В сборную группу — и животное, и обезличенную группу, если они ещё ни в какой не состоят
                        onGroupClick = if (item.herdId == null && !item.isArchive) {
                            { onGroupClick(item.id) }
                        } else null
                    )
                }
            }
        },
        brieflyCard = { item ->
            AnimalCard(
                animal = item,
                onClick = { onClick(item.id) },
                onUnarchiveClick = { onArchiveClick(item.id to false) },
                isArchive = isArchiveProject,
                onDeleteClick = { onDeleteClick(item.id) })
        },
        detailEmptyState = EmptyState(
            title = R.string.message_no_data_title_animals,
            message = R.string.message_no_data_message_animals,
            icon = R.drawable.baseline_pets_24
        ),
        brieflyEmptyState = EmptyState(
            title = R.string.message_no_data_title_animals_archive,
            message = R.string.message_no_data_message_animals_archive,
            support = R.string.message_no_data_message_support_animals_archive,
            icon = R.drawable.baseline_archive_24
        ),
        iconColor = animal_1,
        backgroundColor = animal_3,
        isArchive = isArchiveProject,
        bottomContentPadding = TWO_FAB_BOTTOM_PADDING
    )
}


@Composable
fun AnimalCard(
    animal: AnimalListUi,
    isArchive: Boolean,
    onClick: (() -> Unit)? = null,
    onArchiveClick: (() -> Unit)? = null,
    onUnarchiveClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    onGroupClick: (() -> Unit)? = null,
) {
    val cardField: @Composable () -> Unit = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PregnancyRingIcon(progress = animal.pregnancyProgress) {
                    IconAnimal(
                        sex = animal.sex,
                        imagePath = animal.imagePath,
                        currentIcon = animal.currentIcon,
                        isArchive = animal.isArchive
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // Имя, тип и порода — один плотный блок; различаются размером и цветом
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = animal.name,
                                style = textBold_16,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = animal.type,
                                    style = text_14,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = marengo,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                if (animal.sex != null) {
                                    CountColorGradientCard(sex = animal.sex)
                                    animal.pregnancyProgress?.let { PregnancyHeartCard(it) }
                                }
                                if (animal.isArchive)
                                    CardClips(
                                        colorBackground = gray_6,
                                        colorBorder = gray_8,
                                        colorText = marengo,
                                        value = stringResource(R.string.alert_dialog_info_archive_animal),
                                        icon = R.drawable.baseline_archive_24,
                                        colorIcon = marengo
                                    )
                            }
                            // Порода необязательна — строка только если указана
                            animal.breed?.let { AnimalBreedText(breed = it) }
                        }
                        // Обезличенная группа: поголовье отдельной строкой, за ним — разбивка по полу
                        if (animal.sex == null) GroupHeadsChips(animal)
                        // Видно в поиске и архиве: животное состоит в сборной группе
                        if (animal.herdName != null)
                            CardClips(
                                colorBackground = green_g_3,
                                colorBorder = green_g_4,
                                colorText = dark,
                                value = stringResource(R.string.animal_group_member_of, animal.herdName),
                                icon = R.drawable.ic_herd,
                                colorIcon = animal_1
                            )
                    }
                    if (!isArchive)
                        DropdownMenuEdit(
                            onArchiveClick = onArchiveClick,
                            onUnarchiveClick = onUnarchiveClick,
                            onDeleteClick = onDeleteClick,
                            onGroupClick = onGroupClick
                        )
                }
            }
        }
    }
    if (onClick != null)
        CardFieldNew(
            onClick = onClick
        ) { cardField() }
    else BorderCard { cardField() }
}

/** Строка породы под типом: мельче и светлее типа — так её не спутать с типом. */
@Composable
fun AnimalBreedText(breed: String) {
    Text(
        text = breed,
        style = text_12,
        color = gray_7,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun AnimalParameter(
    modifier: Modifier = Modifier,
    @StringRes titleParameter: Int,
    parameter: String,
    icon: Int,
    iconColor: Color,
    iconColorSecond: Color,
    isMore: Boolean = false,
    /** Доп. элементы справа от значения (например, разбивка по полу в сборной группе). */
    trailingValue: (@Composable RowScope.() -> Unit)? = null,
    // onClick обязан оставаться последним: вызывающие передают его завершающей лямбдой
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = iconColorSecond
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    painterResource(icon),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(14.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    stringResource(titleParameter), color = grey, style = text_12,
                    overflow = TextOverflow.Ellipsis, maxLines = 1,
                )
                if (trailingValue == null)
                    Text(
                        text = parameter, color = dark, maxLines = 1, style = text_12,
                        overflow = TextOverflow.Ellipsis
                    )
                else Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = parameter, color = dark, maxLines = 1, style = text_12,
                        overflow = TextOverflow.Ellipsis
                    )
                    trailingValue()
                }
            }
        }
        if (isMore)
            Icon(
                painterResource(R.drawable.outline_keyboard_arrow_right_24),
                contentDescription = null,
                tint = grey
            )
    }
}

@Composable
fun IconAnimal(
    modifier: Modifier = Modifier,
    sizeIcon: Dp = 64.dp,
    sex: Boolean?,
    imagePath: String?,
    currentIcon: AppIcon,
    isArchive: Boolean = false
) {
    val colors = when {
        isArchive -> listOf(gray_8, grey)
        else -> when (sex) {
            true -> listOf(Color(0xFF51A2FF), Color(0xFF615FFF))
            false -> listOf(Color(0xFFFB64B6), Color(0xFFFF2056))
            null -> listOf(animal_1, animal_2)// TODO
        }
    }

    val gradient = Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )
    val painter = when {
        imagePath != null -> rememberAsyncImagePainter(File(imagePath))
        else -> painterResource(currentIcon.drawableRes)
    }
    val colorFilter = when {
        isArchive && imagePath != null -> ColorFilter.tint(Color.Gray, BlendMode.Saturation)
        isArchive && imagePath == null -> ColorFilter.tint(Color.Gray, BlendMode.SrcIn)
        !isArchive && imagePath == null -> ColorFilter.tint(Color.White)
        else -> null
    }

    Box(
        modifier = modifier
            .size(sizeIcon)
            .clip(CircleShape)
            .background(brush = gradient)
            .border(
                width = 4.dp,
                shape = CircleShape,
                color = Color.White
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = colorFilter,
            modifier = Modifier
                .then(
                    if (imagePath == null) Modifier.size(sizeIcon / 2)/*.rotate(35f)*/ else Modifier
                )
        )
    }
}

@Composable
fun GroupCard(
    @StringRes titleRes: Int,
    @DrawableRes iconOneRes: Int,
    @DrawableRes iconTwoRes: Int,
    @StringRes textOneRes: Int,
    @StringRes textTwoRes: Int,
    isSecondValue: Boolean,
    onClick: (Boolean) -> Unit
) {
    BorderCard(
        modifier = Modifier.fillMaxWidth(),
        padding = PaddingValues(18.dp),
        shape = RoundedCornerShape(16.dp),
        borderColor = green_g_4,
        containerColor = green_g_3
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(titleRes),
                style = text_16
            )
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TypeAnimalCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    title = textOneRes,
                    icon = iconOneRes,
                    isAnimalGroup = !isSecondValue,
                    onClick = { onClick(!isSecondValue) }
                )
                TypeAnimalCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    title = textTwoRes,
                    icon = iconTwoRes,
                    isAnimalGroup = isSecondValue,
                    onClick = { onClick(!isSecondValue) }
                )
            }
        }
    }
}

@Composable
fun TypeAnimalCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @StringRes title: Int,
    @DrawableRes icon: Int,
    isAnimalGroup: Boolean
) {
    val colors = if (isAnimalGroup) Triple(animal_1, Color.White, animal_1)
    else Triple(Color.White, dark, grey_2)

    val shape = RoundedCornerShape(14.dp)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable { onClick() },
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = colors.first
        ),
        border = BorderStroke(
            width = 1.dp,
            color = colors.third
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painterResource(icon),
                contentDescription = null,
                tint = colors.second
            )
            Text(
                text = stringResource(title),
                style = text_14,
                color = colors.second,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


/** Две FAB (объединение + добавление) не должны перекрывать последнюю карточку. */
private val TWO_FAB_BOTTOM_PADDING = 140.dp

/**
 * Иконка животного в кольце прогресса беременности; без [progress] — иконка как есть.
 * [ringSize] — внешний диаметр кольца; чтобы кольцо плотно облегало иконку, передавайте
 * размер иконки + 2 × [strokeWidth].
 */
@Composable
fun PregnancyRingIcon(
    progress: PregnancyProgress?,
    ringSize: Dp = 76.dp,
    strokeWidth: Dp = 4.dp,
    icon: @Composable () -> Unit
) {
    if (progress == null) {
        icon()
        return
    }
    val accent = progress.accentColor()
    Box(
        modifier = Modifier.size(ringSize),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { progress.fraction },
            modifier = Modifier.size(ringSize),
            color = accent,
            trackColor = gray_4,
            strokeWidth = strokeWidth,
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp
        )
        icon()
    }
}

/** Цвет фазы срока — как на экране беременности. */
private fun PregnancyProgress.accentColor(): Color = when (phase) {
    PregnancyPhase.IN_PROGRESS -> animal_1
    PregnancyPhase.BIRTH_WINDOW -> orang_9
    PregnancyPhase.OVERDUE -> error_base
}

/** Значок сердца рядом со значком пола — у самки идёт беременность. */
@Composable
fun PregnancyHeartCard(progress: PregnancyProgress) {
    Box(
        modifier = Modifier
            .background(color = progress.accentColor(), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(R.drawable.baseline_favorite_24),
            contentDescription = stringResource(R.string.pregnancy_heart_description),
            tint = Color.White,
            modifier = Modifier.size(12.dp)
        )
    }
}
