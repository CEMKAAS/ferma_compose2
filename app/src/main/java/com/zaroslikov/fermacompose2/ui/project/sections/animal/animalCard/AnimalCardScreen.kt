@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.gray_4
import com.zaroslikov.fermacompose2.green_15
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_1
import com.zaroslikov.fermacompose2.orang_13
import com.zaroslikov.fermacompose2.orang_3
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.supportFun.getAgeFromDate
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogArchiveAnimal
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogInfo
import com.zaroslikov.fermacompose2.ui.elements.BigBorderButton
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.CountColorGradientCard
import com.zaroslikov.fermacompose2.ui.elements.ProductKillInfoCard
import com.zaroslikov.fermacompose2.ui.elements.SecondAnimalCard
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalParameter
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.IconAnimal
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_3


object AnimalCardDestination : NavigationDestination {
    override val route = "animalCard"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}

@Composable
fun AnimalCardProduct(
    navigateBack: () -> Unit,
    onNavigateSetting: (Pair<Long, Long>) -> Unit,
    onNavigateSize: (Pair<Long, Long>) -> Unit,
    onNavigateCount: (Pair<Long, Long>) -> Unit,
    onNavigateWeight: (Pair<Long, Long>) -> Unit,
    onNavigateVaccination: (Pair<Long, Long>) -> Unit,
    viewModel: AnimalCardViewModel = hiltViewModel()
) {
    val eventFlow = viewModel.navigation
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navigateBack()
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.animal_card_screen_animal_card,
                onNavigateBackClick = navigateBack,
                onSettingsClick = if (state.isArchive) null else {
                    { onNavigateSetting(state.itemIdPT to state.itemId) }
                },
                scrollBehavior = scrollBehavior
            )
        }) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            AnimalCardContainer(
                modifier = Modifier
                    .modifierScreen(innerPadding),
                state = state,
                onIntent = viewModel::onIntent,
                onNavigateSize = { onNavigateSize(state.itemIdPT to state.itemId) },
                onNavigateCount = { onNavigateCount(state.itemIdPT to state.itemId) },
                onNavigateWeight = { onNavigateWeight(state.itemIdPT to state.itemId) },
                onNavigateVaccination = { onNavigateVaccination(state.itemIdPT to state.itemId) }
            )
    }
}

@Composable
fun AnimalCardContainer(
    modifier: Modifier,
    state: AnimalCardState,
    onIntent: (AnimalCardIntent) -> Unit,
    onNavigateSize: () -> Unit,
    onNavigateCount: () -> Unit,
    onNavigateWeight: () -> Unit,
    onNavigateVaccination: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DataCardOne(state.animal, state.price)
        DataCardTwo(
            size = state.size,
            weight = state.weight,
            vaccination = state.vaccination,
            count = state.countAnimal,
            onNavigateSize = onNavigateSize,
            onNavigateCount = onNavigateCount,
            onNavigateWeight = onNavigateWeight,
            onNavigateVaccination = onNavigateVaccination
        )
        NoteWidget(note = state.animal.note, isArchive = state.isArchive)
        { onIntent(AnimalCardIntent.NoteChanged(it)) }
        PullOutCardNew(
            icon = R.drawable.baseline_analytics_24,
            intRes = R.string.animal_card_screen_animal_card_product,
            intTitleText = R.string.alert_dialog_info_title_product_add,
            intText = R.string.alert_dialog_info_text_product_add,
            list = state.productList,
            primalColor = animal_1
        ) { item, index ->
            ProductKillInfoCard(
                number = index + 1,
                name = item.title,
                weight = item.weight,
                linear = item.linear,
                volume = item.volume,
                pieces = item.pieces
            )
        }
        /*PullOutCard(
            intRes = R.string.animal_card_screen_animal_card_product_sale,
            intTitleText = R.string.alert_dialog_info_title_product_sale,
            intText = R.string.alert_dialog_info_text_product_sale,
            list = animalProductTable
        ) {
            Pair(it.title, "${it.priceAll} ${it.suffix}")
        }
           PullOutCard(
               modifier = Modifier,
               intRes = R.string.animal_card_screen_animal_card_product_expenses,
               intTitleText = R.string.alert_dialog_info_title_product_expenses,
               intText = R.string.alert_dialog_info_text_product_expenses,
               list = animalProductTable
           ) {
               Pair(it.title, "${it.priceAll} ${it.suffix}")
           }*/
        if (state.isOpenArchiveDialog)
            AlertDialogArchiveAnimal(
                onConfirmation = { onIntent(AnimalCardIntent.OpenArchiveDialogClicked(false)) },
                onArchiveClick = { onIntent(AnimalCardIntent.ArchiveAnimalPressed) }
            )
    }
}

@Composable
private fun DataCardOne(
    animal: DomainAnimalTable,
    price: Double?
) {
    val context = LocalContext.current
    CardFieldNew() {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            IconAnimal(
                sex = animal.sex,
                currentIcon = animal.currentIcon ?: R.drawable.baseline_pets_24,
                imagePath = animal.imagePath
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = animal.name,
                        style = text_16,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = marengo
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
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!animal.group)
                            CountColorGradientCard(sex = animal.sex)
                    }
                }
                AnimalParameter(
                    titleParameter = R.string.animal_list_age,
                    parameter = getAgeFromDate(context, animal.date) + " (${animal.date})",
                    icon = R.drawable.baseline_calendar_month_24,
                    iconColor = Color(0xFF009689),
                    iconColorSecond = Color(0xFFF0FDFA)
                )
                animal.dateFactory?.let {
                    AnimalParameter(
                        titleParameter = R.string.animal_list_date_factory,
                        parameter = getAgeFromDate(context, it) + " (${it})",
                        icon = R.drawable.baseline_event_24,
                        iconColor = Color(0xFF009689),
                        iconColorSecond = Color(0xFFF0FDFA)
                    )
                }
                AnimalParameter(
                    titleParameter = R.string.animal_list_food,
                    parameter = "${animal.foodDay.formatNumber()} ${stringResource(animal.foodDaySuffix.toResId())}",
                    icon = R.drawable.baseline_shopping_basket_24,
                    iconColor = Color(0xFFD08700),
                    iconColorSecond = orang_13
                )
                /* AnimalParameter(
                     titleParameter = R.string.search_section,
                     parameter = "$price",
                     icon = R.drawable.baseline_add_card_24,
                     iconColor = price_green,
                     iconColorSecond = price_green_2
                 )*/
            }
        }
    }
}


@Composable
private fun DataCardTwo(
    size: DomainAnimalSize?,
    weight: DomainAnimalWeight?,
    vaccination: DomainAnimalVaccination?,
    count: DomainAnimalCount?,
    onNavigateSize: () -> Unit,
    onNavigateCount: () -> Unit,
    onNavigateWeight: () -> Unit,
    onNavigateVaccination: () -> Unit
) {
    SecondAnimalCard(
        icon = R.drawable.baseline_pets_24,
        intRes = R.string.animal_card_screen_animal_card_two
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimalParameter(
                titleParameter = R.string.count_screen_title,
                parameter = count?.let { "${it.count} ${stringResource(it.suffix.toResId())}" }
                    ?: stringResource(R.string.animal_card_screen_animal_card_no_count),
                icon = R.drawable.baseline_spoke_24,
                iconColor = animal_1,
                iconColorSecond = green_15,
                isMore = true
            ) { onNavigateCount() }
            HorizontalDivider(thickness = 1.dp, color = gray_4)
            AnimalParameter(
                titleParameter = R.string.height_screen_title,
                parameter = if (size == null) stringResource(R.string.animal_card_screen_animal_card_no_height)
                else "${size.size} ${stringResource(size.suffix.toResId())}",
                icon = R.drawable.height_24dp_000000_fill0_wght400_grad0_opsz24,
                iconColor = orang_1,
                iconColorSecond = orang_3,
                isMore = true
            ) { onNavigateSize() }
            HorizontalDivider(thickness = 1.dp, color = gray_4)
            AnimalParameter(
                titleParameter = R.string.weight_screen_title,
                parameter = if (weight == null) stringResource(R.string.animal_card_screen_animal_card_no_weight)
                else "${weight.weight} ${stringResource(weight.suffix.toResId())}",
                icon = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
                iconColor = blue_1,
                iconColorSecond = blue_3,
                isMore = true
            ) { onNavigateWeight() }
            HorizontalDivider(thickness = 1.dp, color = gray_4)
            AnimalParameter(
                titleParameter = R.string.vaccination_screen_title,
                parameter = if (vaccination == null) stringResource(R.string.animal_card_screen_animal_card_no_vaccination)
                else "${vaccination.vaccination} ${vaccination.date}",
                icon = R.drawable.vaccines_24dp_000000_fill0_wght400_grad0_opsz24,
                iconColor = violet_1,
                iconColorSecond = violet_3,
                isMore = true
            ) { onNavigateVaccination() }
        }
    }
}


@Composable
fun NoteWidget(
    @StringRes intRes: Int = R.string.outlined_text_note,
    note: String,
    isArchive: Boolean,
    updateNote: (String) -> Unit
) {
    SecondAnimalCard(
        icon = R.drawable.baseline_sticky_note_2_24,
        intRes = intRes,
    ) {
        if (isArchive)
            Text(note, style = text_14, color = marengo)
        else
            OutlinedTextNoteNew(
                value = note,
                onValueChange = { updateNote(it) },
                leadingIconRes = null,
                labelIntRes = null,
                supportingText = R.string.support_text_widget_animal_note_tooltip,
                isBorderCard = false
            )
    }
}

@Composable
fun <T> PullOutCardNew(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    @StringRes intRes: Int,
    @StringRes intTitleText: Int,
    @StringRes intText: Int,
    primalColor: Color,
    list: List<T>,
    detailCard: @Composable (T, Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val extraPadding by animateDpAsState(
        if (expanded) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    var openAlertDialog by remember { mutableStateOf(false) }
    if (openAlertDialog) {
        AlertDialogInfo(
            onConfirmation = { openAlertDialog = false },
            intTitleText = intTitleText,
            intText = intText
        )
    }
    SecondAnimalCard(
        icon = icon,
        intRes = intRes
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (list.isNotEmpty()) {
                for (i in list.indices) {
                    detailCard(list[i], i)
                    if (i == 2 && !expanded)
                        break
                }
            } else Text(text = stringResource(R.string.analysis_screen_no))
            if (list.size > 3)
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            if (expanded) stringResource(R.string.button_wrap) else
                                stringResource(R.string.animal_card_screen_show_product_all).format(
                                    list.size
                                ),
                            style = text_14,
                            color = primalColor
                        )
                        Icon(
                            if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Показать меню",
                            tint = primalColor
                        )
                    }
                }
        }
    }
}