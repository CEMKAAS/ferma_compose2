@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.animalCard


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.gray_4
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_1
import com.zaroslikov.fermacompose2.orang_3
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.supportFun.getAgeFromDate
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.animal.list_screen.AnimalParameter
import com.zaroslikov.fermacompose2.ui.animal.list_screen.IconAnimal
import com.zaroslikov.fermacompose2.ui.animal.list_screen.TableAnimalParameter
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogArchiveAnimal
import com.zaroslikov.fermacompose2.ui.elements.ButtonArchive
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.CountColorGradientCard
import com.zaroslikov.fermacompose2.ui.elements.IconAndText
import com.zaroslikov.fermacompose2.ui.elements.IconAndTextMore
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNoteWidget
import com.zaroslikov.fermacompose2.ui.elements.TextAndIconRow
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenuEdit
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.textBold_18
import com.zaroslikov.fermacompose2.ui.elements.textBold_20
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_20
import com.zaroslikov.fermacompose2.ui.finance.analysis.PullOutCard
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.start.formatNumber
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

    Scaffold(topBar = {
        TopAppBarStart(
            title = stringResource(R.string.animal_card_screen_animal_card),
            true,
            navigateUp = navigateBack,
            settingUp = { onNavigateSetting(state.itemIdPT to state.itemId) },
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
        DataCardOne(Modifier, state.animal, state.price)
        DataCardTwo(
            isGroup = state.animal.group,
            size = state.size,
            weight = state.weight,
            vaccination = state.vaccination,
            count = state.countAnimal,
            onNavigateSize = onNavigateSize,
            onNavigateCount = onNavigateCount,
            onNavigateWeight = onNavigateWeight,
            onNavigateVaccination = onNavigateVaccination
        )
        NoteWidget(state.animal.note) { onIntent(AnimalCardIntent.NoteChanged(it)) }
        PullOutCard(
            intRes = R.string.animal_card_screen_animal_card_product,
            intTitleText = R.string.alert_dialog_info_title_product_add,
            intText = R.string.alert_dialog_info_text_product_add,
            list = state.productList
        ) {
            it.title to "${it.count.formatNumber()} ${stringResource(it.suffix.toResId())}"
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
        ButtonArchive { onIntent(AnimalCardIntent.OpenArchiveDialogClicked(true)) }
        if (state.isOpenArchiveDialog)
            AlertDialogArchiveAnimal(
                onConfirmation = { onIntent(AnimalCardIntent.OpenArchiveDialogClicked(false)) },
                onArchiveClick = { onIntent(AnimalCardIntent.ArchiveAnimalPressed) }
            )
    }
}

@Composable
private fun DataCardOne(
    modifier: Modifier = Modifier,
    animal: DomainAnimalTable,
    price: Double?
) {
    val context = LocalContext.current
    CardFieldNew(
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
                    AnimalParameter(
                        titleParameter = R.string.animal_list_age,
                        parameter = animal.date + " (${getAgeFromDate(context, animal.date)})",
                        icon = R.drawable.baseline_calendar_month_24,
                        iconColor = Color(0xFF009689),
                        iconColorSecond = Color(0xFFF0FDFA)
                    )
                    animal.dateFactory?.let {
                        AnimalParameter(
                            titleParameter = R.string.animal_list_date_factory,
                            parameter = it + " (${getAgeFromDate(context, it)})",
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
                        iconColorSecond = Color(0xFFFEFCE8)
                    )
                    AnimalParameter(
                        titleParameter =  R.string.search_section,
                        parameter = "$price",
                        icon = R.drawable.baseline_add_card_24,
                        iconColor = price_green,
                        iconColorSecond = price_green_2
                    )
                }

            }

            /*CardFieldNew(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = state.name,
                    style = textBold_18
                )
                IconAndText(
                    iconRes = R.drawable.baseline_pets_24,
                    valueString = state.type
                )
                if (!state.group) {
                    IconAndText(
                        iconRes = if (state.sex) R.drawable.baseline_male_24 else R.drawable.baseline_female_24,
                        valueString = stringResource(if (state.sex) R.string.animal_entry_screen_sex_man else R.string.animal_entry_screen_sex_woman)
                    )
                }
                IconAndText(
                    iconRes = R.drawable.baseline_calendar_month_24,
                    valueString = state.date + getAgeFromDate(context, state.date)
                )
                state.dateFactory?.let {
                    IconAndText(
                        iconRes = R.drawable.baseline_event_24,
                        valueString = it
                    )
                }
                if (price != null)
                    IconAndText(
                        iconRes = R.drawable.baseline_add_card_24,
                        valueString = stringResource(
                            R.string.card_ruble_s,
                            price.formatNumber()
                        )
                    )
                IconAndText(
                    iconRes = R.drawable.baseline_calendar_month_24,//TODO нужен мешок
                    valueString =
                        if (state.foodDay == 0.0) stringResource(R.string.animal_card_screen_animal_card_no_food_day)
                        else "${state.foodDay.formatNumber()} ${state.foodDaySuffix}"
                )
            }*/
        }
    )
}


@Composable
private fun DataCardTwo(
    isGroup: Boolean,
    size: DomainAnimalSize?,
    weight: DomainAnimalWeight?,
    vaccination: DomainAnimalVaccination?,
    count: DomainAnimalCount,
    onNavigateSize: () -> Unit,
    onNavigateCount: () -> Unit,
    onNavigateWeight: () -> Unit,
    onNavigateVaccination: () -> Unit
) {
    CardFieldNew(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.animal_card_screen_animal_card_two),
            style = text_20
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimalParameter(
                titleParameter = R.string.count_screen_title,
                parameter = "${count.count} ${stringResource(count.suffix.toResId())}",
                icon = R.drawable.baseline_spoke_24,
                iconColor = Color(0xFF009689),
                iconColorSecond = Color(0xFFF0FDFA),
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
private fun NoteWidget(
    note: String,
    updateNote: (String) -> Unit
) {
    OutlinedTextNoteNew(
        value = note,
        onValueChange = { updateNote(it) },
        isBorderCard = false,
        label = R.string.card_note,
        supportingText = R.string.support_text_widget_animal_note_tooltip
    )
}

