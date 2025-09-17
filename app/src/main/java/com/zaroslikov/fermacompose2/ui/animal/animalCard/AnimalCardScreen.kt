package com.zaroslikov.fermacompose2.ui.animal.animalCard


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.zaroslikov.fermacompose2.supportFun.getAgeFromDate
import com.zaroslikov.fermacompose2.ui.animal.animal_dialog.AlertDialogAddAnimal
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogGroupToSolo
import com.zaroslikov.fermacompose2.ui.animal.animal_dialog.AlertDialogKillAnimal
import com.zaroslikov.fermacompose2.ui.animal.animal_dialog.AlertDialogSaleAnimal
import com.zaroslikov.fermacompose2.ui.animal.animal_dialog.AlertDialogWriteOffAnimal
import com.zaroslikov.fermacompose2.ui.elements.ButtonArchive
import com.zaroslikov.fermacompose2.ui.elements.ButtonCustom
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.IconAndText
import com.zaroslikov.fermacompose2.ui.elements.IconAndTextMore
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNoteWidget
import com.zaroslikov.fermacompose2.ui.elements.TextAndIconRow
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.textBold_18
//import com.zaroslikov.fermacompose2.ui.finance.PullOutCard
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.start.formatNumber


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
    viewModel: AnimalCardViewModel = hiltViewModel()
) {
    val eventFlow = viewModel.navigation
    val state by viewModel.state.collectAsStateWithLifecycle()
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

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
            settingUp = { onNavigateSetting(state.itemIdPT to state.itemId) }
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
                onNavigateSize = { onNavigateSize(state.itemIdPT to state.itemId) },
                onIntent = viewModel::onIntent
            )
    }
}

@Composable
fun AnimalCardContainer(
    modifier: Modifier,
    state: AnimalCardState,
    onIntent: (AnimalCardIntent) -> Unit,
    onNavigateSize: () -> Unit,
) {

    Column(modifier = modifier) {
        DataCardOne(state.animal, state.price)
        DataCardTwo(
            isGroup = state.animal.group,
            size = state.size,
            weight = state.weight,
            vaccination = state.vaccination,
            count = state.countAnimal,
            onNavigateSize = onNavigateSize,
            onNavigateIndicators = { } // TODO Id navigation
        )
        NoteWidget(state.animal.note) { onIntent(AnimalCardIntent.NoteChanged(it)) }
        /*   PullOutCard(
               modifier = Modifier,
               intRes = R.string.animal_card_screen_animal_card_product,
               intTitleText = R.string.alert_dialog_info_title_product_add,
               intText = R.string.alert_dialog_info_text_product_add,
               list = animalProductTable
           ) {
               Pair(it.title, "${it.priceAll} ${it.suffix}")
           }
           PullOutCard(
               modifier = Modifier,
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
        ButtonPanel(
            onKillClick = { onIntent(AnimalCardIntent.DialogKillClicked(true)) },
            onAddClick = { onIntent(AnimalCardIntent.DialogAddClicked(true)) },
            onSaleClick = { onIntent(AnimalCardIntent.DialogSaleClicked(true)) },
            onWriteOffClick = { onIntent(AnimalCardIntent.DialogWriteOffClicked(true)) },
            onArchiveClick = {
//                openArchiveDialog = !openArchiveDialog
            }
        )

        if (state.openKillDialog)
            AlertDialogKillAnimal(
                state = state.actionAnimal,
                onIntent = onIntent,
                isAnimalGroup = state.animal.group,
                countAnimalAll = state.countAnimal.count,
                countSuffix = state.countAnimal.suffix,
                weight = state.weight?.weight,
                weightSuffix = state.weight?.suffix,
            )
        if (state.openSaleDialog)
            AlertDialogSaleAnimal(
                state = state.actionAnimal,
                onIntent = onIntent,
                isAnimalGroup = state.animal.group,
                countAll = state.countAnimal.count,
                countSuffix = state.countAnimal.suffix,
                buyerList = state.buyerList
            )
        if (state.openWriteOffDialog)
            AlertDialogWriteOffAnimal(
                state = state.actionAnimal,
                onIntent = onIntent,
                isAnimalGroup = state.animal.group,
                countAll = state.countAnimal.count,
                countSuffix = state.countAnimal.suffix
            )
        if (state.openAddDialog)
            AlertDialogAddAnimal(
                state = state.actionAnimal,
                onIntent = onIntent,
                countAllAnimal = state.countAnimal.count
            )
        /*    if (openArchiveDialog)
                     AlertDialogArchiveAnimal(
                         onConfirmation = { openArchiveDialog = !openArchiveDialog },
                         onArchiveClick = updateArchive
                     )*/
        if (state.openSoloDialog)
            AlertDialogGroupToSolo(
                sex = state.animal.sex,
                onUpdateSex = { onIntent(AnimalCardIntent.SexClicked(it)) },
                onConfirmation = { onIntent(AnimalCardIntent.DialogSoloClicked(false)) },
                onSave = { onIntent(AnimalCardIntent.SaveGroupPressed) }
            )
    }
}

@Composable
private fun DataCardOne(
    state: DomainAnimalTable,
    price: Double?
) {
    val context = LocalContext.current
    CardField(
        row = false,
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
    }
}


@Composable
private fun DataCardTwo(
    isGroup: Boolean,
    size: DomainAnimalSize?,
    weight: DomainAnimalWeight?,
    vaccination: DomainAnimalVaccination?,
    count: DomainAnimalCount,
    onNavigateSize: () -> Unit,
    onNavigateIndicators: (Int) -> Unit
) {
    CardField(
        row = false
    ) {
        Text(
            text = stringResource(R.string.animal_card_screen_animal_card_two),
            style = textBold_18
        )

        IconAndTextMore(
            iconRes = R.drawable.baseline_spoke_24,
            valueString = stringResource(R.string.card_pieces_s, count.count.toString()),
            onClick = { onNavigateIndicators(2) }
        )
        IconAndTextMore(
            iconRes = R.drawable.height_24dp_000000_fill0_wght400_grad0_opsz24,
            valueString = if (size == null) stringResource(R.string.animal_card_screen_animal_card_no_height)
            else "${size.size} ${size.suffix}",
            onClick = { onNavigateSize() }
        )
        IconAndTextMore(
            iconRes = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
            valueString = if (weight == null) stringResource(R.string.animal_card_screen_animal_card_no_weight)
            else "${weight.weight} ${weight.suffix}",
            onClick = { onNavigateIndicators(0) }
        )
        IconAndTextMore(
            iconRes = R.drawable.vaccines_24dp_000000_fill0_wght400_grad0_opsz24,
            valueString = if (vaccination == null) stringResource(R.string.animal_card_screen_animal_card_no_vaccination)
            else "${vaccination.vaccination} ${vaccination.date}",
            onClick = { onNavigateIndicators(3) }
        )
    }
}


@Composable
private fun NoteWidget(
    note: String,
    updateNote: (String) -> Unit
) {
    CardField(
        row = false,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TextAndIconRow(
            intRes = R.string.card_note,
            titleStyle = textBold_18,
            isTooltipShow = true,
            isShowValue = false,
            intTooltip = R.string.support_text_widget_animal_note_tooltip
        )
        OutlinedTextNoteWidget(
            value = note,
            onValueChange = { updateNote(it) },
        )
    }
}


@Composable
private fun ButtonPanel(
    onSaleClick: () -> Unit,
    onKillClick: () -> Unit,
    onAddClick: () -> Unit,
    onWriteOffClick: () -> Unit,
    onArchiveClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ButtonCustom(
                modifier = Modifier.weight(1f),
                onClick = onSaleClick,
                intRes = R.string.button_sale,
                drawableRes = R.drawable.baseline_add_card_24
            )
            ButtonCustom(
                modifier = Modifier.weight(1f),
                onClick = onKillClick,
                intRes = R.string.button_kill,
                drawableRes = R.drawable.icons8__meat60
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ButtonCustom(
                modifier = Modifier.weight(1f),
                onClick = onAddClick,
                intRes = R.string.button_add,
                drawableRes = R.drawable.baseline_add_circle_outline_24
            )
            ButtonCustom(
                modifier = Modifier.weight(1f),
                onClick = onWriteOffClick,
                intRes = R.string.button_write_off,
                drawableRes = R.drawable.baseline_edit_note_24
            )
        }
        ButtonArchive { onArchiveClick() }
    }
}
