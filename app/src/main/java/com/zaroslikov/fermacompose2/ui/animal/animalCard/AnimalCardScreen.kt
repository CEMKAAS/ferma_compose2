package com.zaroslikov.fermacompose2.ui.animal.animalCard


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.DomainIndicatorsVM
import com.zaroslikov.domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.data.room.table.ferma.AddTable
import com.zaroslikov.data.room.table.ferma.ExpensesTable
import com.zaroslikov.data.room.table.ferma.SaleTable
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import com.zaroslikov.data.room.dto.PairData
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.fermacompose2.supportFun.getAgeFromDate
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogAddAnimal
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogGroupToSolo
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogKillAnimal
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogSaleAnimal
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogWriteOffAnimal
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
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlinx.coroutines.launch

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
    onNavigateIndicators: (Triple<Int, Int, Long>) -> Unit,
    viewModel: AnimalCardViewModel = hiltViewModel()
) {
//    val animalTable = viewModel.state.collectAsState()
//    val product = viewModel.productState(animalTable.value.name).collectAsState()
//    val buyerUiState by viewModel.buyerUiState.collectAsState()
//    val titleUiState by viewModel.titleUiState.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
    val cor = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        TopAppBarStart(
            title = stringResource(R.string.animal_card_screen_animal_card),
            true,
            navigateUp = navigateBack,
            settingUp = { onNavigateSetting(viewModel.itemIdPT to viewModel.itemId) }
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
//                titleList = titleUiState.list,
//                buyerList = buyerUiState.list,
//                animalProductTable = product.value.itemList,
                countWarehouse = viewModel.countInWarehouse,
                onNavigateIndicators = {
                    onNavigateIndicators(
                        Triple(
                            it.first,
                            it.second,
                            viewModel.itemIdPT
                        )
                    )
                },
                onSaleClick = {
                    viewModel.insertSaleAnimal(
                        Triple(
                            it.first,
                            it.second,
                            it.third
                        )
                    )
                    if (it.third) navigateBack()
                },
                onSaleProductClick = {
                    viewModel.saveAddAnimal(it)
                },
                onSaleCountClick = {
                    viewModel.saveCountAnimal(it)
                    if (it.third) navigateBack() // возможно баг(возвращение редактор или вакцину)
                },
                onAddAnimalClick = {
                    cor.launch {
//                        viewModel.insertAddAnimal()
                    }
                },
                onAddWriteOffClick = {
                    cor.launch {
                        viewModel.insertWriteOffAnimal(it)
                        if (it.third) navigateBack()
                    }
                },
                onUpdateCountWarehouse = {
                    viewModel.updateUiState(it)
                },
                onUpdateAnimalGroupClick = viewModel::updateAnimalGroup,
                onValueChange = viewModel::update,
                updateArchive = {
                    viewModel.updateArchive()
                    navigateBack()
                },
                viewModel = viewModel
            )
    }
}

@Composable
fun AnimalCardContainer(
    modifier: Modifier,
    state: AnimalCardState,
    onIntent: (AnimalCardIntent) -> Unit,
    countWarehouse: Double,
    titleList: List<PairData>,
    buyerList: List<String>,
    animalProductTable: List<AnimalTitSuff>,
    onNavigateIndicators: (Pair<Int, Int>) -> Unit,
    onUpdateCountWarehouse: suspend (String) -> DomainPairDataDoubleSting,
    onUpdateAnimalGroupClick: (String) -> Unit,
    onSaleProductClick: (AddTable) -> Unit,
    onSaleCountClick: (Triple<DomainIndicatorsVM, WriteOffTable, Boolean>) -> Unit,
    onSaleClick: (Triple<DomainIndicatorsVM, SaleTable, Boolean>) -> Unit,
    onAddAnimalClick: (Pair<DomainIndicatorsVM, ExpensesTable?>) -> Unit,
    onAddWriteOffClick: (Triple<DomainIndicatorsVM, WriteOffTable?, Boolean>) -> Unit,
    onValueChange: (AnimalCardState) -> Unit,
    updateArchive: () -> Unit,
    viewModel: AnimalCardViewModel
) {

    Column(modifier = modifier) {
        DataCardOne(state.animal, state.price)
        DataCardTwo(
            isGroup = state.animal.group,
            size = state.size,
            weight = state.weight,
            vaccination = state.vaccination,
            count = state.countAnimal,
            onNavigateIndicators = { onNavigateIndicators(Pair(state.id.toInt(), it)) }
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
            onKillClick = {
//                openKillDialog = !openKillDialog
            },
            onAddClick = { onIntent(AnimalCardIntent.DialogAddClicked(true)) },
            onSaleClick = { onIntent(AnimalCardIntent.DialogSaleClicked(true)) },
            onWriteOffClick = { onIntent(AnimalCardIntent.DialogWriteOffClicked(true)) },
            onArchiveClick = {
//                openArchiveDialog = !openArchiveDialog
            }
        )
        if (state.openSaleDialog)
            AlertDialogSaleAnimal(
                state = state.saleAnimal,
                onIntent = onIntent,
                isAnimalGroup = state.animal.group,
                countAll = state.countAnimal.count,
                countSuffix = state.countAnimal.suffix,
                buyerList = state.buyerList
            )

        if (state.openKillDialog)
            AlertDialogKillAnimal(
                titleList = titleList,
                isAnimalGroup = state.group,
                title = state.name,
                countAnimalAll = animalCountTable.weight,
                countSuffix = animalCountTable.suffix,
                weight = animalWeightTable?.weight,
                weightSuffix = animalWeightTable?.suffix,
                idAnimal = state.id,
                idPT = state.idPT.toInt(),
                onSaveProductClick = onSaleProductClick,
                onSaveCountClick = onSaleCountClick,
                onUpdateCountWarehouse = onUpdateCountWarehouse,
                onUpdateAnimalGroupClick = onUpdateAnimalGroupClick,
                onConfirmation = { openKillDialog = !openKillDialog },
            )
        /*
          if (openArchiveDialog)
                    AlertDialogArchiveAnimal(
                        onConfirmation = { openArchiveDialog = !openArchiveDialog },
                        onArchiveClick = updateArchive
                    )*/

        if (state.openWriteOffDialog)
            AlertDialogWriteOffAnimal(
                state = state.writeOffAnimal,
                onIntent = onIntent,
                isAnimalGroup = state.animal.group,
                countAll = state.countAnimal.count,
                countSuffix = state.countAnimal.suffix
            )

        if (state.openAddDialog)
            AlertDialogAddAnimal(
                state = state.addAnimal,
                onIntent = onIntent
            )
        if (state.openSoloDialog)
            AlertDialogGroupToSolo(
                sex = state.animal.sex,
                onUpdateSex = { onIntent(AnimalCardIntent.SexClicked(it)) },
                onConfirmation = { onIntent(AnimalCardIntent.DialogSoloClicked(false)) }
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
    onNavigateIndicators: (Int) -> Unit
) {
    CardField(
        row = false
    ) {
        Text(
            text = stringResource(R.string.animal_card_screen_animal_card_two),
            style = textBold_18
        )
        if (isGroup)
            IconAndTextMore(
                iconRes = R.drawable.baseline_spoke_24,
                valueString = stringResource(R.string.card_pieces_s, count.count.toString()),
                onClick = { onNavigateIndicators(2) }
            )
        else
            IconAndTextMore(
                iconRes = R.drawable.height_24dp_000000_fill0_wght400_grad0_opsz24,
                valueString = if (size == null) stringResource(R.string.animal_card_screen_animal_card_no_height)
                else "${size.size} ${size.suffix}",
                onClick = { onNavigateIndicators(1) }
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
