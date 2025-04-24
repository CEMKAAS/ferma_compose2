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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.Domain.models.DomainIndicatorsVM
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.composeElement.AlertDialogKillAnimal
import com.zaroslikov.fermacompose2.ui.composeElement.AlertDialogSaleAnimal
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.IconAndText
import com.zaroslikov.fermacompose2.ui.composeElement.IconAndTextMore
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_18
import com.zaroslikov.fermacompose2.ui.composeElement.text_16
import com.zaroslikov.fermacompose2.ui.finance.PullOutCard
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
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
    onNavigateIndicators: (Pair<Int, Int>) -> Unit,
    viewModel: AnimalCardViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val animalTable = viewModel.itemUiState
    val product = viewModel.productState(animalTable.name).collectAsState()
    val buyerUiState by viewModel.buyerUiState.collectAsState()
    val titleUiState by viewModel.titleUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

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
                .modifierScreen(innerPadding)
                .verticalScroll(rememberScrollState()),
            animalTable = animalTable,
            animalWeightTable = viewModel.domainWeight,
            animalSizeTable = viewModel.domainHeight,
            animalCountTable = viewModel.domainCount,
            animalVaccinationTable = viewModel.domainVaccination,
            titleList = titleUiState.list,
            buyerList = buyerUiState.list,
            animalProductTable = product.value.itemList,
            countWarehouse = viewModel.countInWarehouse,
            onNavigateIndicators = onNavigateIndicators,
            onSaleClick = {
                coroutineScope.launch {
                    viewModel.saveSaleAnimal(
                        Triple(
                            it.first,
                            it.second,
                            animalTable.toAnimalTable().copy(arhiv = it.third)
                        )
                    )
                    if (it.third) navigateBack()
                }
            },
            onUpdateCountWarehouse = {
                coroutineScope.launch {
                    viewModel.updateUiState(it)
                }
            }
        )
    }
}

@Composable
fun AnimalCardContainer(
    modifier: Modifier,
    animalTable: AnimalEditUiState,
    animalWeightTable: DomainIndicatorsVM,
    animalSizeTable: DomainIndicatorsVM,
    animalCountTable: DomainIndicatorsVM,
    animalVaccinationTable: DomainIndicatorsVM,
    countWarehouse: Double,
    titleList: List<String>,
    buyerList: List<String>,
    animalProductTable: List<AnimalTitSuff>,
    onNavigateIndicators: (Pair<Int, Int>) -> Unit,
    onUpdateCountWarehouse: (String) -> Unit,
    onSaleClick: (Triple<SaleTable, AnimalCountTable, Boolean>) -> Unit
) {

    var openSaleDialog by rememberSaveable { mutableStateOf(false) }
    var openKillDialog by rememberSaveable { mutableStateOf(false) }
    val openArchiveDialog by rememberSaveable { mutableStateOf(false) }

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
        PullOutCard(
            modifier = Modifier,
            intRes = R.string.animal_card_screen_animal_card_product,
            intTitleText = R.string.alert_dialog_info_title_product_add,
            intText = R.string.alert_dialog_info_text_product_add,
            list = animalProductTable
        ) {
            Pair(it.title, "${it.priceAll} ${it.suffix}")
        }
        CardField {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.card_note),
                    style = textBold_18
                )
                Text(
                    text = if (animalTable.note == "") stringResource(R.string.analysis_screen_no) else animalTable.note,
                    style = text_16
                )
            }
        }
        ButtonPanel(
            onSaleClick = { openSaleDialog = !openSaleDialog },
            onKillClick = { openKillDialog = !openKillDialog },
            onArchiveClick = {}
        )
        if (openSaleDialog) {
            AlertDialogSaleAnimal(
                drawableRes = R.drawable.baseline_add_card_24,
                buyerList = buyerList,
                isAnimalGroup = animalTable.groop,
                title = animalTable.name,
                countAll = animalCountTable.weight.toInt(),
                suffix = animalCountTable.suffix,
                idPT = animalTable.idPT,
                onSaveClick = { onSaleClick(it) },
                onConfirmation = { openSaleDialog = !openSaleDialog },
            )
        }
        if (openKillDialog) {
            AlertDialogKillAnimal(
                drawableRes = R.drawable.baseline_add_card_24,
                titleList = titleList,
                isAnimalGroup = animalTable.groop,
                title = animalTable.name,
                countAll = animalCountTable.weight.toInt(),
                suffix = animalCountTable.suffix,
                idPT = animalTable.idPT,
                countWarehouse = countWarehouse,
                onSaveClick = { },
                onUpdateCountWarehouse = {onUpdateCountWarehouse(it)},
                onConfirmation = { openKillDialog = !openKillDialog },
            )
        }
    }
}

@Composable
private fun DataCardOne(
    animalTable: AnimalEditUiState
) {
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
                valueString = animalTable.data
            )
            IconAndText(
                iconRes = R.drawable.baseline_calendar_month_24,//дата завода
                valueString = animalTable.data
            )
            if (!animalTable.groop) {
                IconAndText(
                    iconRes = R.drawable.baseline_add_card_24,
                    valueString = stringResource(
                        R.string.card_ruble_s,
                        animalTable.price.toFormatNumber()
                    )
                )
            }
            IconAndText(
                iconRes = R.drawable.baseline_calendar_month_24,//нужен мешок
                valueString = "${animalTable.foodDay} кг"
            )
        }
    }
}


@Composable
private fun DataCardTwo(
    isSoloAnimal: Boolean,
    countTable: DomainIndicatorsVM,
    weightTable: DomainIndicatorsVM,
    sizeTable: DomainIndicatorsVM,
    vaccinationsTable: DomainIndicatorsVM,
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
                    iconRes = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
                    valueString = if (weightTable.weight == "") stringResource(R.string.animal_card_screen_animal_card_no_weight)
                    else stringResource(R.string.card_kilogram_s, weightTable.weight),
                    onClick = { onNavigateIndicators(0) }
                )
                IconAndTextMore(
                    iconRes = R.drawable.height_24dp_000000_fill0_wght400_grad0_opsz24,
                    valueString = if (sizeTable.weight == "") stringResource(R.string.animal_card_screen_animal_card_no_height)
                    else stringResource(R.string.card_size_s, sizeTable.weight),
                    onClick = { onNavigateIndicators(1) }
                )
            }
            IconAndTextMore(
                iconRes = R.drawable.vaccines_24dp_000000_fill0_wght400_grad0_opsz24,
                valueString = if (vaccinationsTable.weight == "") stringResource(R.string.animal_card_screen_animal_card_no_vaccination)
                else "${vaccinationsTable.weight} ${vaccinationsTable.date}",
                onClick = { onNavigateIndicators(3) }
            )
        }
    }
}

@Composable
fun ButtonPanel(
    onSaleClick: () -> Unit,
    onKillClick: () -> Unit,
    onArchiveClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ButtonStandart(
            modifier = Modifier.weight(1f),
            onClick = onSaleClick,
            intRes = R.string.button_sale
        )
        ButtonStandart(
            modifier = Modifier.weight(1f),
            onClick = onKillClick,
            intRes = R.string.button_kill
        )
    }
    ButtonStandart(
        onClick = onArchiveClick,
        intRes = R.string.button_archive
    )
}
