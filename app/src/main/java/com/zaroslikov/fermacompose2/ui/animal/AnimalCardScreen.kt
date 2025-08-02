package com.zaroslikov.fermacompose2.ui.animal


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.Domain.models.DomainIndicatorsVM
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.supportFun.getAgeFromDate
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.composeElement.AlertDialog.AlertDialogAddAnimal
import com.zaroslikov.fermacompose2.ui.composeElement.AlertDialog.AlertDialogArchiveAnimal
import com.zaroslikov.fermacompose2.ui.composeElement.AlertDialog.AlertDialogKillAnimal
import com.zaroslikov.fermacompose2.ui.composeElement.AlertDialog.AlertDialogSaleAnimal
import com.zaroslikov.fermacompose2.ui.composeElement.AlertDialog.AlertDialogWriteOffAnimal
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonArchive
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonCustom
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.IconAndText
import com.zaroslikov.fermacompose2.ui.composeElement.IconAndTextMore
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNoteWidget
import com.zaroslikov.fermacompose2.ui.composeElement.TextAndIconRow
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_18
import com.zaroslikov.fermacompose2.ui.finance.PullOutCard
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlinx.coroutines.launch

object AnimalCardDestination : NavigationDestination {
    override val route = "animalCard"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemCategory"
    val routeWithArgs = "$route/{$itemIdArg}/{$itemIdArgTwo}"
}

@Composable
fun AnimalCardProduct(
    navigateBack: () -> Unit,
    onNavigateSetting: (Int) -> Unit,
    onNavigateIndicators: (Triple<Int, Int, Long>) -> Unit,
    viewModel: AnimalCardViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val animalTable = viewModel.animalTableUiState
    val product = viewModel.productState(animalTable.name).collectAsState()
    val buyerUiState by viewModel.buyerUiState.collectAsState()
    val titleUiState by viewModel.titleUiState.collectAsState()

    val cor = rememberCoroutineScope()

    Scaffold(topBar = {
        TopAppBarStart(
            title = animalTable.name,
            true,
            navigateUp = navigateBack,
            settingUp = { onNavigateSetting(animalTable.id) }
        )
    }) { innerPadding ->
        AnimalCardContainer(
            modifier = Modifier
                .modifierScreen(innerPadding),
            animalTable = animalTable,
            animalWeightTable = viewModel.domainWeight,
            animalSizeTable = viewModel.domainHeight,
            animalCountTable = viewModel.domainCount,
            animalVaccinationTable = viewModel.domainVaccination,
            titleList = titleUiState.list,
            buyerList = buyerUiState.list,
            animalProductTable = product.value.itemList,
            countWarehouse = viewModel.countInWarehouse,
            onNavigateIndicators = {
                onNavigateIndicators(
                    Triple(
                        it.first,
                        it.second,
                        viewModel.itemIdPT.toLong()
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
                    viewModel.insertAddAnimal(it)
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
            updateNote = {
                viewModel.updateNote(it)
            },
            updateArchive = {
                viewModel.updateArchive()
                navigateBack()
            }
        )
    }
}

@Composable
fun AnimalCardContainer(
    modifier: Modifier,
    animalTable: AnimalEditUiState,
    animalWeightTable: DomainIndicatorsVM?,
    animalSizeTable: DomainIndicatorsVM?,
    animalCountTable: DomainIndicatorsVM,
    animalVaccinationTable: DomainIndicatorsVM?,
    countWarehouse: Double,
    titleList: List<String>,
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
    updateNote: (String) -> Unit,
    updateArchive: () -> Unit
) {
    var openSaleDialog by rememberSaveable { mutableStateOf(false) }
    var openKillDialog by rememberSaveable { mutableStateOf(false) }
    var openAddDialog by rememberSaveable { mutableStateOf(false) }
    var openWriteOffDialog by rememberSaveable { mutableStateOf(false) }
    var openArchiveDialog by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        DataCardOne(animalTable)
        DataCardTwo(
            isSoloAnimal = !animalTable.groop,
            countTable = animalCountTable,
            weightTable = animalWeightTable,
            sizeTable = animalSizeTable,
            vaccinationsTable = animalVaccinationTable,
            onNavigateIndicators = { onNavigateIndicators(Pair(animalTable.id, it)) }

        )
        NoteWidget(animalTable.note) { updateNote(it) }
        PullOutCard(
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
        }
        ButtonPanel(
            onSaleClick = { openSaleDialog = !openSaleDialog },
            onKillClick = { openKillDialog = !openKillDialog },
            onAddClick = { openAddDialog = !openAddDialog },
            onWriteOffClick = { openWriteOffDialog = !openWriteOffDialog },
            onArchiveClick = { openArchiveDialog = !openArchiveDialog }
        )
        if (openSaleDialog)
            AlertDialogSaleAnimal(
                buyerList = buyerList,
                isAnimalGroup = animalTable.groop,
                title = animalTable.name,
                countAll = animalCountTable.weight,
                countSuffix = animalCountTable.suffix,
                idAnimal = animalTable.id.toLong(),
                idPT = animalTable.idPT,
                onSaveClick = onSaleClick,
                onUpdateAnimalGroupClick = onUpdateAnimalGroupClick,
                onConfirmation = { openSaleDialog = !openSaleDialog },
            )
        if (openKillDialog)
            AlertDialogKillAnimal(
                titleList = titleList,
                isAnimalGroup = animalTable.groop,
                title = animalTable.name,
                countAnimalAll = animalCountTable.weight,
                countSuffix = animalCountTable.suffix,
                weight = animalWeightTable?.weight,
                weightSuffix = animalWeightTable?.suffix,
                idAnimal = animalTable.id.toLong(),
                idPT = animalTable.idPT,
                onSaveProductClick = onSaleProductClick,
                onSaveCountClick = onSaleCountClick,
                onUpdateCountWarehouse = onUpdateCountWarehouse,
                onUpdateAnimalGroupClick = onUpdateAnimalGroupClick,
                onConfirmation = { openKillDialog = !openKillDialog },
            )
        if (openAddDialog)
            AlertDialogAddAnimal(
                title = animalTable.name,
                countAll = animalCountTable.weight,
                countSuffix = animalCountTable.suffix,
                idPT = animalTable.id,
                onConfirmation = { openAddDialog = !openAddDialog },
                onSaveClick = onAddAnimalClick
            )

        if (openWriteOffDialog)
            AlertDialogWriteOffAnimal(
                isAnimalGroup = animalTable.groop,
                title = animalTable.name,
                countAll = animalCountTable.weight,
                countSuffix = animalCountTable.suffix,
                idPT = animalTable.idPT,
                idAnimal = animalTable.id.toLong(),
                onConfirmation = { openWriteOffDialog = !openWriteOffDialog },
                onSaveClick = onAddWriteOffClick,
                onUpdateAnimalGroupClick = onUpdateAnimalGroupClick
            )

        if (openArchiveDialog)
            AlertDialogArchiveAnimal(
                onConfirmation = { openArchiveDialog = !openArchiveDialog },
                onArchiveClick = updateArchive
            )
    }
}

@Composable
private fun DataCardOne(
    animalTable: AnimalEditUiState
) {
    val context = LocalContext.current
    CardField {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.animal_card_screen_animal_card),
                style = textBold_18
            )
            IconAndText(
                iconRes = R.drawable.baseline_pets_24,
                valueString = animalTable.type
            )
            if (!animalTable.groop) {
                IconAndText(
                    iconRes = if (animalTable.sex == "Мужской") R.drawable.baseline_male_24 else R.drawable.baseline_female_24,
                    valueString = animalTable.sex
                )
            }
            IconAndText(
                iconRes = R.drawable.baseline_calendar_month_24,
                valueString = if (animalTable.data != "") animalTable.data + " (${
                    getAgeFromDate(
                        context,
                        animalTable.data
                    )
                })" else "" //Todo fix construction
            )
            if (animalTable.dateFactory != "")
                IconAndText(
                    iconRes = R.drawable.baseline_event_24,
                    valueString = animalTable.dateFactory
                )
            if (animalTable.groop) {
                IconAndText(
                    iconRes = R.drawable.baseline_add_card_24,
                    valueString = stringResource(
                        R.string.card_ruble_s,
                        animalTable.price.toConvertZeroDouble().formatNumber()
                    )
                )
            }
            IconAndText(
                iconRes = R.drawable.baseline_calendar_month_24,//TODO нужен мешок
                valueString =
                if (animalTable.foodDay == "0.0") stringResource(R.string.animal_card_screen_animal_card_no_food_day)
                else "${
                    animalTable.foodDay.toConvertZeroDouble().formatNumber()
                } ${animalTable.suffixFoodDay}"
            )
        }
    }
}


@Composable
private fun DataCardTwo(
    isSoloAnimal: Boolean,
    countTable: DomainIndicatorsVM,
    weightTable: DomainIndicatorsVM?,
    sizeTable: DomainIndicatorsVM?,
    vaccinationsTable: DomainIndicatorsVM?,
    onNavigateIndicators: (Int) -> Unit
) {
    CardField {
        Column {
            Text(
                text = stringResource(R.string.animal_card_screen_animal_card_two),
                style = textBold_18
            )
            if (!isSoloAnimal)
                IconAndTextMore(
                    iconRes = R.drawable.baseline_spoke_24,
                    valueString = stringResource(R.string.card_pieces_s, countTable.weight),
                    onClick = { onNavigateIndicators(2) }
                )
            else {
                IconAndTextMore(
                    iconRes = R.drawable.height_24dp_000000_fill0_wght400_grad0_opsz24,
                    valueString = if (sizeTable == null) stringResource(R.string.animal_card_screen_animal_card_no_height)
                    else "${sizeTable.weight} ${sizeTable.suffix}",
                    onClick = { onNavigateIndicators(1) }
                )
            }
            IconAndTextMore(
                iconRes = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
                valueString = if (weightTable == null) stringResource(R.string.animal_card_screen_animal_card_no_weight)
                else "${weightTable.weight} ${weightTable.suffix}",
                onClick = { onNavigateIndicators(0) }
            )
            IconAndTextMore(
                iconRes = R.drawable.vaccines_24dp_000000_fill0_wght400_grad0_opsz24,
                valueString = if (vaccinationsTable == null) stringResource(R.string.animal_card_screen_animal_card_no_vaccination)
                else "${vaccinationsTable.weight} ${vaccinationsTable.date}",
                onClick = { onNavigateIndicators(3) }
            )
        }
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
