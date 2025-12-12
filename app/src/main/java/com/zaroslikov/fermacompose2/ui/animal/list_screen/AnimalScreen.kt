package com.zaroslikov.fermacompose2.ui.animal.list_screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.list.suffixPiecesList
import com.zaroslikov.domain.models.list.suffixWeightDayList
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.animal_2
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.green_g_3
import com.zaroslikov.fermacompose2.green_g_4
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.supportFun.getAgeFromDate
import com.zaroslikov.fermacompose2.ui.animal.indicators.EntryBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.CountColorGradientCard
import com.zaroslikov.fermacompose2.ui.elements.DateFactoryCardNew
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.AnimalNameOutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenuEdit
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedPriceInputNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextAnimalTypeNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextSexNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarNavigationNew
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.sections.InventoryBody


object AnimalDestination : NavigationDestination {
    override val route = "animal"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalScreen(
    /* navigateToStart: () -> Unit,
     navigateToModalSheet: (DrawerNavigation) -> Unit,
     drawerState: DrawerState,*/
    navigateToItemCard: (Pair<Long, Long>) -> Unit = {},
    modifier: Modifier = Modifier,
    /*navigateToItemAdd: (Long) -> Unit,*/
    /*   isFirstStart: Boolean,*/
    viewModel: AnimalViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = listOf(animal_1, animal_2)
    val primeColor = animal_2
    val idProject = state.idPT

    val query = state.textSearch.trim().lowercase()

    val searchList = if (query.isBlank() && !state.isArchive) state.list
    else
        state.list.filter { item ->

            item.name.lowercase().contains(query) ||
                    item.type.lowercase().contains(query) ||
                    item.date.lowercase().contains(query) ||
                    item.count.toString().lowercase().contains(query) /*||*/
            /*stringResource(item.countSuffix.toResId()).lowercase().contains(query)*/
            /* "${item.day} ${stringResource(monthToResString(item.month))} ${item.year}".lowercase()
                 .contains(query) ||
                     (item.priceAll ?: item.price).toString().lowercase().contains(query)*/
        }

    val searchList2 = if (query.isBlank() && state.isArchive) state.list
    else
        state.list.filter { item ->

            item.name.lowercase().contains(query) ||
                    item.type.lowercase().contains(query) ||
                    item.date.lowercase().contains(query) ||
                    item.count.toString().lowercase().contains(query) /*||*/
            /*stringResource(item.countSuffix.toResId()).lowercase().contains(query)*/
            /* "${item.day} ${stringResource(monthToResString(item.month))} ${item.year}".lowercase()
                 .contains(query) ||
                     (item.priceAll ?: item.price).toString().lowercase().contains(query)*/
        }



    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNavigationNew(
                scrollBehavior = scrollBehavior,
                value = state.textSearch,
                isGroup = state.isArchive,
                onClick = { viewModel.onIntent(AnimalListIntent.GroupClicked(it)) },
                onValueChange = { viewModel.onIntent(AnimalListIntent.SearchChanged(it)) }
            )
        },
        floatingActionButton = {
            NeonGlowFab(colors = colors) {
                viewModel.onIntent(
                    AnimalListIntent.OpenBottomSheetEntry(
                        true
                    )
                )
            }
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = modifier.padding(innerPadding),
            )
        else
            AnimalContainer(
                modifier = Modifier
                    .modifierScreenLazy(innerPadding),
                itemList = state.list,
                color = primeColor,
                details = state.isArchive,
                searchList = searchList,
                brieflyList = searchList2,
                onInsertClick = { viewModel.onIntent(AnimalListIntent.OpenBottomSheetEntry(true)) },
                onEditClick = { },
                onDeleteClick = { viewModel.onIntent(AnimalListIntent.Delete(it)) },
                onDetailsClick = { TODO() },
                onClick = { navigateToItemCard(state.idPT to it) },
            )
        if (state.openBottomSheetEntry)
            AnimalEntryBottomSheet(
                modifier = Modifier,
                state = state.currentProduct,
                colors = colors,
                onIntent = viewModel::onIntent
            )
    }
}

@Composable
private fun AnimalEntryBottomSheet(
    modifier: Modifier,
    colors: List<Color>,
    state: AnimalEntryState2,
    onIntent: (AnimalListIntent) -> Unit
) {
    EntryBottomSheet(
        modifier = Modifier,
        isEntry = true,
        enabledButton = state.enabledButton(),
        colors = colors,
        onDismissRequest = {
            onIntent(
                AnimalListIntent.OpenBottomSheetEntry(false)
            )
        },
        onInsertClick = { onIntent(AnimalListIntent.Insert) },
        onUpdateClick = { onIntent(AnimalListIntent.Update) }
    ) {
        /*if (state.isEntry)*/
        AnimalGroupCard(
            isAnimalGroup = state.isAnimalGroup,
            animalGroupClicked = { onIntent(AnimalListIntent.AnimalGroupClicked(it)) }
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
            list = state.typeList,
        )
        if (state.isAnimalGroup /*&& state.isEntry*/)
            OutlinedTextCountNew(
                value = state.count,
                onValueChange = {
                    onIntent(AnimalListIntent.CountChanged(it))
                },
                onSuffixChange = { onIntent(AnimalListIntent.SuffixClicked(it)) },
                suffixList = suffixPiecesList,
                drawableRes = R.drawable.baseline_spoke_24,
                isError = state.error.isErrorCount,
                suffix = state.countSuffix,
                intRes = R.string.outlined_text_field_quantity,
                intResSup = R.string.support_text_count_animals,
                /*      isWarehouseShow = false,
                      isDropMenuShow = false*/
            )
        if (!state.isAnimalGroup)
            OutlinedTextSexNew(
                value = state.sex,
                onValueChange = {
                    onIntent(AnimalListIntent.SexClicked(it))
                },
            )
        /*if (state.isEntry)*/
        OutlinedPriceInputNew(
            price = state.price,
            onPriceChange = {
                onIntent(AnimalListIntent.PriceChanged(it))
            },
            priceAll = state.priceAll,
            isAutoCalculate = state.isAutoPrice,
            onAutoCalculate = {
                onIntent(AnimalListIntent.AutoPriceClicked(it))
            },
            isManyCount = state.isAnimalGroup,
            count = state.count,
            countSuffix = state.countSuffix,
            priceSuffix = Suffix.RUBLE,
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
            isError = false,
            onSuffixChange = {
                onIntent(AnimalListIntent.FoodDaySuffixClicked(it))
            },
            suffixList = suffixWeightDayList,
            intRes = R.string.outlined_food_day_animals,
            intResSup = if (!state.isAnimalGroup) R.string.support_text_food_day_animal else R.string.support_text_food_day_animals,
            suffix = state.foodDaySuffix,
            /*isWarehouseShow = false,
            isDropMenuShow = true,
            versionDropMenu = DropdownMenu.WEIGHT,
            isNecessarily = false*/
        )
        /*if (state.isEntry)*/
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
    itemList: List<DomainAnimalWithCount>,
    searchList: List<DomainAnimalWithCount>,
    brieflyList: List<DomainAnimalWithCount>,
    onInsertClick: () -> Unit,
    onClick: (Long) -> Unit,
    onEditClick: (DomainAnimalWithCount) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDetailsClick: (DomainAnimalWithCount) -> Unit
) {
    InventoryBody(
        modifier = modifier,
        details = details,
        itemList = itemList,
        searchList = searchList,
        brieflyList = brieflyList,
        onInsertClick = onInsertClick,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onDetailsClick = onDetailsClick,
        detailCard = { item ->
            AnimalCard(
                modifier = Modifier,
                animal = item,
                onClick = { onClick(item.id) },
                onEditClick = { TODO() },
                onArchive = { TODO() },
                onDeleteClick = { onDeleteClick(item.id) })
        },
        brieflyCard = { item ->
            AnimalCard(
                modifier = Modifier,
                animal = item,
                onClick = { TODO() },
                onEditClick = { TODO() },
                onArchive = { TODO() },
                onDeleteClick = { onDeleteClick(item.id) })
        },
        titleRes = R.string.message_no_date_title_animals,
        messageRes = R.string.message_no_date_message_animals,
        supportRes = R.string.message_no_date_support_text_animals,
        buttonRes = R.string.button_animals_message_no_data
    )
}


@Composable
fun AnimalCard(
    modifier: Modifier = Modifier,
    animal: DomainAnimalWithCount,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onArchive: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val context = LocalContext.current
    CardFieldNew(
        onClick = { onClick() },
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
        contentRow = {
            IconAnimal(sex = animal.sex)
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = animal.name,
                            style = text_16,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        DropdownMenuEdit(
                            onEditClick = onEditClick,
                            onArchiveClick = onArchive,
                            onDeleteClick = onDeleteClick
                        )
                    }
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
                            color = marengo
                        )
                        if (!animal.group)
                            CountColorGradientCard(sex = animal.sex)
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TableAnimalParameter(
                        oneTitleParameter = R.string.animal_list_age,
                        oneParameter = getAgeFromDate(
                            context,
                            animal.dateFactory ?: animal.date
                        ),
                        oneIcon = R.drawable.baseline_calendar_month_24,
                        oneIconColor = Color(0xFF009689),
                        oneIconColorSecond = Color(0xFFF0FDFA),
                        twoTitleParameter = R.string.animal_list_expenses,
                        twoParameter = "333",
                        twoIcon = R.drawable.icon_expenses,
                        twoIconColor = Color(0xFF0092B8),
                        twoIconColorSecond = Color(0xFFECFEFF)
                    )
                    TableAnimalParameter(
                        oneTitleParameter = R.string.animal_list_food,
                        oneParameter = "Корма",
                        oneIcon = R.drawable.baseline_shopping_basket_24,
                        oneIconColor = Color(0xFFD08700),
                        oneIconColorSecond = Color(0xFFFEFCE8),
                        twoTitleParameter = R.string.animal_list_count,
                        twoParameter = animal.count.toString(),
                        twoIcon = R.drawable.baseline_spoke_24,
                        twoIconColor = Color(0xFF00786F),
                        twoIconColorSecond = Color(0xFFCBFBF1)
                    )
                }


                /*Text(
                text = animal.name,
                style = textBold_16
            )
            IconAndText(
                iconRes = R.drawable.baseline_pets_24,
                valueString = animal.type
            )
            if (!animal.group)
                IconAndText(
                    iconRes = if (animal.sex) R.drawable.baseline_male_24 else R.drawable.baseline_female_24,
                    valueString = stringResource(
                        if (animal.sex) R.string.animal_entry_screen_sex_man else R.string.animal_entry_screen_sex_woman
                    )
                )
            else
                IconAndText(
                    iconRes = R.drawable.baseline_spoke_24,
                    valueString = "${animal.count} ${animal.suffix}"
                )

            IconAndText(
                iconRes = if (animal.dateFactory == null) R.drawable.baseline_calendar_month_24 else R.drawable.baseline_event_24,
                valueString = animal.dateFactory ?: animal.date
            )
        }*/
            }
        }
    )
}


@Composable
fun TableAnimalParameter(
    @StringRes oneTitleParameter: Int,
    oneParameter: String,
    oneIcon: Int,
    oneIconColor: Color,
    oneIconColorSecond: Color,
    @StringRes twoTitleParameter: Int,
    twoParameter: String,
    twoIcon: Int,
    twoIconColor: Color,
    twoIconColorSecond: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimalParameter(
            modifier = Modifier.weight(1f),
            titleParameter = oneTitleParameter,
            parameter = oneParameter,
            icon = oneIcon,
            iconColor = oneIconColor,
            iconColorSecond = oneIconColorSecond
        )
        AnimalParameter(
            modifier = Modifier.weight(1f),
            titleParameter = twoTitleParameter,
            parameter = twoParameter,
            icon = twoIcon,
            iconColor = twoIconColor,
            iconColorSecond = twoIconColorSecond
        )
    }
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
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
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
                    modifier = Modifier
                        /*   .size(14.dp)*/
                        .padding(8.dp),
                    contentDescription = null, tint = iconColor
                )
            }
            Column {
                Text(
                    stringResource(titleParameter), color = grey, maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = parameter, color = dark, maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
    size: Dp = 64.dp,
    sex: Boolean,
) {
    val glowColor = Color(0xFF00A63E)

    val colors = if (sex)
        listOf(Color(0xFF51A2FF), Color(0xFF615FFF))
    else listOf(Color(0xFFFB64B6), Color(0xFFFF2056))

    val gradient = Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )
    Box(
        modifier = modifier
            .size(size)
            /*.graphicsLayer {
                // имитация неонового свечения
                shadowElevation = 20f
                shape = CircleShape
                clip = false
                ambientShadowColor = glowColor.copy(alpha = 0.8f)
                spotShadowColor = glowColor.copy(alpha = 0.8f)
            }*/
            .clip(CircleShape)
            .background(brush = gradient)
            .border(
                width = 4.dp,
                shape = CircleShape,
                color = Color.White
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(R.drawable.baseline_pets_24),
            contentDescription = "Добавить",
            tint = Color.White,
            modifier = Modifier.size(size / 2)
        )
    }
}

@Composable
private fun AnimalGroupCard(
    isAnimalGroup: Boolean,
    animalGroupClicked: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = green_g_3
        ),
        border = BorderStroke(
            width = 1.dp,
            color = green_g_4
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.animal_entry_screen_animal),
                style = text_16
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TypeAnimalCard(
                    modifier = Modifier.weight(1f),
                    title = R.string.ration_button_one,
                    icon = R.drawable.baseline_pets_24,
                    isAnimalGroup = !isAnimalGroup,
                    onClick = { animalGroupClicked(!isAnimalGroup) }
                )
                TypeAnimalCard(
                    modifier = Modifier.weight(1f),
                    title = R.string.ration_button_many,
                    icon = R.drawable.baseline_spoke_24,
                    isAnimalGroup = isAnimalGroup,
                    onClick = { animalGroupClicked(!isAnimalGroup) }
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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
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
            Text(text = stringResource(title), style = text_14, color = colors.second)
        }
    }
}

