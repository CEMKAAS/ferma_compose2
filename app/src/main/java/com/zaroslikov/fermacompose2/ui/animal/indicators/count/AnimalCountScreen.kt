@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators.count

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice
import com.zaroslikov.domain.models.dto.sale.DomainBuyerPrice
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.animal.indicators.size.HeadingIndicators
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.IconAndText
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData
import com.zaroslikov.fermacompose2.ui.elements.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextBuyer
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextCountAnimal2
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.elements.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.textBold_18
import com.zaroslikov.fermacompose2.ui.elements.textBuildIndicatorsAnnotated2
import com.zaroslikov.fermacompose2.ui.elements.textBuildIndicatorsAnnotated3
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.ButtonPanel
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.navigation.navNull
import com.zaroslikov.fermacompose2.ui.sections.expenses.entry.ExpensesEntryDestination
import com.zaroslikov.fermacompose2.ui.sections.sale.entry.SaleEntryDestination
import com.zaroslikov.fermacompose2.ui.sections.writeOff.entry.WriteOffEntryDestination
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlin.math.absoluteValue

object AnimalCountDestination : NavigationDestination {
    override val route = "animalCount"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}

@Composable
fun AnimalCountScreen(
    navigateBack: () -> Unit,
    navigate: (String) -> Unit,
    viewModel: AnimalCountViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state = viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.height_screen_title,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {

        }
    ) { innerPadding ->
        if (state.value.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            AnimalCountContainer(
                modifier = Modifier.modifierScreenLazy(innerPadding),
                state = state.value,
                navigate = { navigate(navigate2(it)) },
                onIntent = viewModel::onIntent
            )
        if (state.value.isOpenDialog)
            CountBottomSheet(
                state = state.value.domainAnimalCountPrice,
                onIntent = viewModel::onIntent,
                error = state.value.error,
                isEntry = state.value.isEntry,
                buyerList = emptyList() // TODO заменить на нормальный лист
            )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MyFabMenu(
    onAction1: () -> Unit,
    onAction2: () -> Unit,
    onAction3: () -> Unit
) {
    var fabExpanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButtonMenu(
            modifier = Modifier.align(Alignment.BottomEnd),
            expanded = fabExpanded,
            button = {
                ToggleFloatingActionButton(
                    checked = fabExpanded,
                    onCheckedChange = { fabExpanded = it },
                    containerSize = ToggleFloatingActionButtonDefaults.containerSizeLarge()
                ) {
                    val icon = if (fabExpanded) Icons.Default.Close else Icons.Default.Add
                    Icon(
                        painter = rememberVectorPainter(icon),
                        contentDescription = null,
                        modifier = Modifier.animateIcon({ checkedProgress })
                    )
                }
            }
        ) {
            FloatingActionButtonMenuItem(
                onClick = {
                    fabExpanded = false
                    onAction1()
                },
                icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                text = { Text("Edit") }
            )
            FloatingActionButtonMenuItem(
                onClick = {
                    fabExpanded = false
                    onAction2()
                },
                icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                text = { Text("Delete") }
            )
            FloatingActionButtonMenuItem(
                onClick = {
                    fabExpanded = false
                    onAction3()
                },
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                text = { Text("Sell") }
            )
        }
    }
}


@Composable
private fun CountBottomSheet(
    state: DomainAnimalCountPrice,
    error: AnimalCountState.Error,
    buyerList: List<String>,
    isEntry: Boolean,
    onIntent: (AnimalCountIntent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val priceShowList = setOf(
        AnimalCountVersion.SALE,
        AnimalCountVersion.EXPENSES,
        AnimalCountVersion.WRITE_OFF
    )
    ModalBottomSheet(
        onDismissRequest = { onIntent(AnimalCountIntent.EndDialogClicked) },
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.modifierBottomSheet()) {
            IconAndText(
                modifier = Modifier.fillMaxWidth(),
                iconRes = state.version?.toDrawRes() ?: AnimalCountVersion.ADD.toDrawRes(),
                valueString = stringResource(
                    state.version?.toResId() ?: AnimalCountVersion.ADD.toResId()
                ),
                horizontalArrangement = Arrangement.Center,
                textStyle = textBold_18
            )
            OutlinedTextCountAnimal2(
                value = state.count,
                onValueChange = {
                    onIntent(AnimalCountIntent.CountChanged(it))
                },
                suffix = state.suffix,
                isError = error.isErrorCount,
                isErrorCountZero = error.isErrorCountZero,
                intRes = R.string.count_screen_title,
            )
            if (state.price != null && state.version in priceShowList)
                OutlinedPriceInput(
                    price = state.price.toString(),
                    onPriceChange = {
                        onIntent(AnimalCountIntent.PriceClicked(it))
                    },
                    priceAll = state.priceAll.toString(),
                    isError = error.isErrorPrice,
                    isAutoCalculate = state.priceAll != null,
                    onAutoCalculate = {
                        onIntent(AnimalCountIntent.AutoPriceClicked(it))
                    },
                    tooltipTextResAutoCal = R.string.expenses_entry_screen_auto_calculate,
                    isManyCount = true,
                    isNecessarily = true
                )
            if (state.buyer != null && state.version == AnimalCountVersion.SALE)
                OutlinedTextBuyer(
                    value = state.buyer.toString(),
                    onValueChange = {
                        onIntent(AnimalCountIntent.BuyerChanged(it))
                    },
                    onTrailingChance = {
                        onIntent(AnimalCountIntent.BuyerClearClicked)
                    },
                    list = buyerList
                )
            OutlinedTextDate(
                value = state.date,
                onValueChange = { onIntent(AnimalCountIntent.DateClicked(it)) },
                isCardBorder = false
            )
            OutlinedTextNote(
                value = state.note,
                onValueChange = { onIntent(AnimalCountIntent.NoteChanged(it)) },
                cardBorder = false
            )
            ButtonPanel(
                isEntry = isEntry,
                entryButton = R.string.button_add,
                onClickInsert = { onIntent(AnimalCountIntent.InsertPressed) },
                onClickUpdate = { onIntent(AnimalCountIntent.UpdatePressed) },
                onClickDelete = { onIntent(AnimalCountIntent.DeletePressed) }
            )
        }
    }
}

@Composable
private fun AnimalCountContainer(
    modifier: Modifier = Modifier,
    state: AnimalCountState,
    navigate: (DomainAnimalCountPrice) -> Unit,
    onIntent: (AnimalCountIntent) -> Unit
) {
    if (state.countList.isNotEmpty())
        VaccinationList2(
            modifier = modifier,
            indicatorsList = state.countList,
            navigate = navigate,
            onEditClick = { onIntent(AnimalCountIntent.OpenDialogClicked(it)) }
        )
    else MessageNoData(
        modifier = modifier,
        titleRes = R.string.message_no_date_title_count,
        messageRes = R.string.message_no_date_message_count,
        supportRes = R.string.message_no_date_support_count,
        buttonRes = R.string.button_sale_message_no_count
    )
}

@Composable
private fun VaccinationList2(
    modifier: Modifier,
    indicatorsList: List<DomainAnimalCountPrice>,
    navigate: (DomainAnimalCountPrice) -> Unit,
    onEditClick: (DomainAnimalCountPrice) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        item { HeadingIndicators(R.string.count_screen_title) }
        itemsIndexed(items = indicatorsList, key = { index, _ -> index }) { index, item ->
            val previousItem =
                if (index < indicatorsList.size - 1) indicatorsList[index + 1] else null
            CountCard(
                modifier = Modifier.clickable {
                    onEditClick(item)
                },
                domainAnimalCount = item,
                previousDomainAnimalCount = previousItem
            )
        }
    }
}

@Composable
private fun CountCard(
    modifier: Modifier = Modifier,
    domainAnimalCount: DomainAnimalCountPrice,
    previousDomainAnimalCount: DomainAnimalCountPrice?
) {
    var details by rememberSaveable { mutableStateOf(false) }
    val extraPadding by animateDpAsState(
        if (details) 0.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    CardField(
        modifier = modifier.padding(bottom = extraPadding.coerceAtLeast(0.dp)),
        row = false
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.weight(0.2f),
                painter = painterResource(
                    domainAnimalCount.version?.toDrawRes() ?: AnimalCountVersion.ADD.toDrawRes()
                ),
                contentDescription = null
            )
            Text(
                modifier = Modifier.weight(1f),
                text = "${domainAnimalCount.count.toFormatNumber()} " +
                        stringResource(domainAnimalCount.suffix.toResId()),
                style = textBold_16,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.weight(1f),
                text = domainAnimalCount.date,
                style = textBold_16,
                textAlign = TextAlign.Center
            )
            IconButton(
                modifier = Modifier.weight(0.25f),
                onClick = { details = !details }
            ) {
                Icon(
                    imageVector = if (details) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }
        if (details)
            DetailsCount(
                domainAnimalCount,
                previousDomainAnimalCount
            )
    }
}

@Composable
private fun DetailsCount(
    domainAnimalCount: DomainAnimalCountPrice,
    previousDomainAnimalCount: DomainAnimalCountPrice?
) {
    val context = LocalContext.current

    val count = domainAnimalCount.count.toConvertZeroDouble()
    val previousCount = previousDomainAnimalCount?.count?.toConvertZeroDouble()

    val price = domainAnimalCount.price
    val buyer = domainAnimalCount.buyer

    val suffix = domainAnimalCount.suffix
    val note = if (domainAnimalCount.note != "") "\n${domainAnimalCount.note}" else ""

    val totalValue = when {
        count == previousCount || previousCount == null -> count.formatNumber()
        else -> (count - previousCount).absoluteValue.formatNumber()
    }

    Row {
        Text(
            modifier = Modifier.weight(1f),
            text = when (domainAnimalCount.version) {
                AnimalCountVersion.SALE ->
                    textBuildIndicatorsAnnotated3(
                        context = context,
                        intRes = R.string.animal_card_screen_sale_note_expenses,
                        totalValue = totalValue,
                        price = price,
                        suffix = suffix,
                        buyer = buyer
                            ?: stringResource(R.string.animal_card_screen_sale_note_no_buyer),
                        isPlus = false,
                        note = note
                    )

                AnimalCountVersion.KILL -> textBuildIndicatorsAnnotated2(
                    context = context,
                    intRes = R.string.animal_card_screen_kill_note,
                    totalValue = totalValue,
                    suffix = suffix,
                    isPlus = true,
                    note = note
                )

                AnimalCountVersion.WRITE_OFF -> textBuildIndicatorsAnnotated3(
                    context = context,
                    intRes = if (price == null) R.string.animal_card_screen_write_off_note_count else R.string.animal_card_screen_write_off_note_expenses,
                    totalValue = totalValue,
                    price = price,
                    suffix = suffix,
                    isPlus = false,
                    note = note
                )

                AnimalCountVersion.EXPENSES ->
                    textBuildIndicatorsAnnotated3(
                        context = context,
                        intRes = R.string.animal_card_screen_add_note_expenses,
                        totalValue = totalValue,
                        suffix = suffix,
                        price = price,
                        isPlus = true,
                        note = note
                    )

                AnimalCountVersion.ADD -> textBuildIndicatorsAnnotated2(
                    context = context,
                    intRes = R.string.animal_card_screen_add_note_count,
                    totalValue = totalValue,
                    suffix = suffix,
                    isPlus = true,
                    note = note
                )

                else -> textBuildIndicatorsAnnotated2(
                    context = context,
                    intRes = R.string.animal_indicators_animals_count_increased_note,
                    totalValue = totalValue,
                    suffix = suffix,
                    isPlus = true,
                    note = note
                )
            },
            style = text_16,
            textAlign = TextAlign.Start
        )
    }
}

private fun navigate2(countPrice: DomainAnimalCountPrice): String {
    val idPT = countPrice.idPT.toString()
    val id = countPrice.tableId.toString()
    val version = countPrice.version
    return when (version) {
        AnimalCountVersion.SALE -> navNull(
            route = SaleEntryDestination.route,
            itemOne = idPT,
            itemTwo = id
        )

        AnimalCountVersion.EXPENSES -> navNull(
            route = ExpensesEntryDestination.route,
            itemOne = idPT,
            itemTwo = id
        )

        AnimalCountVersion.WRITE_OFF -> navNull(
            route = WriteOffEntryDestination.route,
            itemOne = idPT,
            itemTwo = id
        )

        else -> navNull(
            route = WriteOffEntryDestination.route,
            itemOne = idPT,
            itemTwo = id
        )
    }
}
