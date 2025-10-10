@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.animal.indicators.count

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.animal.indicators.size.HeadingIndicators
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogGroupToSolo
import com.zaroslikov.fermacompose2.ui.elements.CardField
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.MessageNoData
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.textBuildIndicatorsAnnotated2
import com.zaroslikov.fermacompose2.ui.elements.textBuildIndicatorsAnnotated3
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlin.math.absoluteValue

object AnimalCountDestination : NavigationDestination {
    override val route = "animalCount"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}

@ExperimentalMaterial3ExpressiveApi
@Composable
fun AnimalCountScreen(
    navigateBack: () -> Unit,
    viewModel: AnimalCountViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.count_screen_title,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            val items =
                listOf(
                    R.drawable.icon_add to "add",
                    R.drawable.baseline_edit_note_24 to "edit",
                    R.drawable.icons8__meat30 to "meat",
                    R.drawable.baseline_add_shopping_cart_24 to "expenses",
                    R.drawable.baseline_add_card_24 to "sale"
                )
            var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
            FloatingActionButtonMenu(
                expanded = fabMenuExpanded,
                button = {
                    ToggleFloatingActionButton(
                        modifier =
                            Modifier
                                .semantics {
                                    traversalIndex = -1f
                                    stateDescription = if (fabMenuExpanded) "Expanded" else "Collapsed"
                                    contentDescription = "Toggle menu"
                                }
                                .animateFloatingActionButton(
                                    visible = fabMenuExpanded,
                                    alignment = Alignment.BottomEnd,
                                ) ,
                        checked = fabMenuExpanded,
                        onCheckedChange = { fabMenuExpanded = !fabMenuExpanded },
                    ) {
                        val imageVector by remember {
                            derivedStateOf {
                                if (checkedProgress > 0.5f) R.drawable.baseline_clear_24 else R.drawable.icon_add

                            }
                        }
                        Icon(
                            painter = painterResource(imageVector),
                            contentDescription = null,
                            modifier = Modifier.animateIcon({ checkedProgress }),
                        )
                    }
                },
                content = {
                    items.forEachIndexed { i, item ->
                        FloatingActionButtonMenuItem(
                            modifier =
                                Modifier
                                    .semantics {
                                        isTraversalGroup = true
                                        // Add a custom a11y action to allow closing the menu when focusing
                                        // the last menu item, since the close button comes before the first
                                        // menu item in the traversal order.
                                        if (i == items.size - 1) {
                                            customActions =
                                                listOf(
                                                    CustomAccessibilityAction(
                                                        label = "Close menu",
                                                        action = {
                                                            fabMenuExpanded = false
                                                            true
                                                        },
                                                    )
                                                )
                                        }
                                    }
                                    .then(
                                        if (i == 0) {
                                            Modifier.onKeyEvent {
                                                // Navigating back from the first item should go back to the
                                                // FAB menu button.
                                                if (
                                                    it.type == KeyEventType.KeyDown &&
                                                    (it.key == Key.DirectionUp ||
                                                            (it.isShiftPressed && it.key == Key.Tab))
                                                ) {
//                                                    focusRequester.requestFocus()
                                                    return@onKeyEvent true
                                                }
                                                return@onKeyEvent false
                                            }
                                        } else {
                                            Modifier
                                        }
                                    ),
                            onClick = { fabMenuExpanded = false },
                            icon = { Icon(painterResource(item.first), contentDescription = null) },
                            text = { Text(text = item.second) },
                        )
                    }
                }
            )
            /*   FabMenu(
                   onAddClick = {
                       viewModel.onIntent(
                           AnimalCountIntent.DialogClicked(
                               true,
                               DomainAnimalCountPrice(
                                   version = AnimalCountVersion.ADD,
                                   date = dateToday()
                               )
                           )
                       )
                   },
                   onWriteOffClick = {
                       viewModel.onIntent(
                           AnimalCountIntent.DialogClicked(
                               true,
                               DomainAnimalCountPrice(
                                   version = AnimalCountVersion.WRITE_OFF,
                                   date = dateToday()
                               )
                           )
                       )
                   },
                   onSellClick = {
                       viewModel.onIntent(
                           AnimalCountIntent.DialogClicked(
                               true,
                               DomainAnimalCountPrice(
                                   version = AnimalCountVersion.SALE,
                                   date = dateToday()
                               )
                           )
                       )
                   },
                   onExpensesClick = {
                       viewModel.onIntent(
                           AnimalCountIntent.DialogClicked(
                               true,
                               DomainAnimalCountPrice(
                                   version = AnimalCountVersion.EXPENSES,
                                   date = dateToday()
                               )
                           )
                       )
                   },
                   onKillClick = {
                       viewModel.onIntent(
                           AnimalCountIntent.DialogClicked(
                               true,
                               DomainAnimalCountPrice(
                                   version = AnimalCountVersion.KILL,
                                   date = dateToday()
                               )
                           )
                       )
                   }
               )*/
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            AnimalCountContainer(
                modifier = Modifier.modifierScreenLazy(innerPadding),
                state = state,
                onIntent = viewModel::onIntent
            )

        if (state.isOpenDialog) {
            when (state.domainAnimalCountPrice.version) {
                AnimalCountVersion.SALE ->
                    BottomSheetSaleAnimal(
                        state = state.domainAnimalCountPrice,
                        errorState = state.error,
                        onIntent = viewModel::onIntent,
                        isEntry = state.isEntry,
                        isAnimalGroup = state.animal.group,
                        isAutoPrice = state.isAutoPrice,
                        countAllAnimal = state.currentAnimal.count,
                        buyerList = state.buyerList,
                    )

                AnimalCountVersion.EXPENSES ->
                    BottomSheetExpensesAnimal(
                        state = state.domainAnimalCountPrice,
                        errorState = state.error,
                        isEntry = state.isEntry,
                        isAnimalGroup = state.animal.group,
                        isAutoPrice = state.isAutoPrice,
                        countAllAnimal = state.currentAnimal.count,
                        countSuffix = state.currentAnimal.suffix,
                        onIntent = viewModel::onIntent
                    )

                AnimalCountVersion.KILL ->
                    BottomSheetKillAnimal(
                        state = state.domainAnimalCountPrice,
                        onIntent = viewModel::onIntent,
                        errorState = state.error,
                        productKill = state.productKill,
                        titleList = state.titleList,
                        isAnimalGroup = true,
                        countAnimalAll = state.currentAnimal.count,
                        countSuffix = state.currentAnimal.suffix,
                        weight = state.weight,
                        isEntry = state.isEntry
                    )

                AnimalCountVersion.WRITE_OFF ->
                    BottomSheetWriteOffAnimal(
                        state = state.domainAnimalCountPrice,
                        errorState = state.error,
                        isEntry = state.isEntry,
                        isAnimalGroup = state.animal.group,
                        isAutoPrice = state.isAutoPrice,
                        countAllAnimal = state.currentAnimal.count,
                        countSuffix = state.currentAnimal.suffix,
                        onIntent = viewModel::onIntent
                    )

                AnimalCountVersion.ADD ->
                    BottomSheetAddAnimal(
                        state = state.domainAnimalCountPrice,
                        errorState = state.error,
                        isEntry = state.isEntry,
                        countAllAnimal = state.currentAnimal.count,
                        onIntent = viewModel::onIntent
                    )


                AnimalCountVersion.INCUBATOR -> {
                    TODO()
                }

                null -> {
                    TODO()
                }
            }
        }
        if (state.openSoloDialog)
            AlertDialogGroupToSolo(
                sex = state.animal.sex,
                onUpdateSex = { viewModel.onIntent(AnimalCountIntent.SexClicked(it)) },
                onConfirmation = { viewModel.onIntent(AnimalCountIntent.DialogSoloClicked(false)) },
                onSave = { viewModel.onIntent(AnimalCountIntent.SaveGroupPressed) }
            )
        if (state.openWarningDialog) {
            val textWarning = stringResource(
                when (state.openWarningDeleteAllDialog) {
                    true -> R.string.animal_count_screen_warning_minus_and_delete_text
                    false -> R.string.animal_count_screen_warning_product_delete_all_text
                    null -> R.string.animal_count_screen_warning_text
                }
            )
            AlertDialogWarningAnimal(
                textWarning = textWarning,
                onConfirmationClick = { viewModel.onIntent(AnimalCountIntent.WarningConrPressed) },
                onDismissClick = { viewModel.onIntent(AnimalCountIntent.WarningEndDialogClicked) }
            )
        }
//        if (state.openWarningDeleteDialog) {
//            AlertDialogWarningAnimal(
//                textWarning = stringResource(R.string.animal_count_screen_warning_product_delete_text),
//                onConfirmationClick = { viewModel.onIntent(AnimalCountIntent.WarningDeleteConrPressed) },
//                onDismissClick = { viewModel.onIntent(AnimalCountIntent.WarningDeleteEndDialogClicked) }
//            )
//        }
    }
}

@SuppressLint("RememberInComposition")
@ExperimentalMaterial3ExpressiveApi
@Composable
fun FabMenu2() {

}

/*@Composable
fun FabMenu(
    onAddClick: () -> Unit = {},
    onWriteOffClick: () -> Unit = {},
    onSellClick: () -> Unit = {},
    onKillClick: () -> Unit = {},
    onExpensesClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Кнопки-меню
        Column(
            Modifier.padding(bottom = 72.dp, end = 16.dp),
            Arrangement.spacedBy(12.dp),
            Alignment.End
        ) {
            AnimatedVisibility(visible = expanded) {
                SmallFloatingActionButton(
                    onClick = onAddClick,
                ) { Icon(Icons.Default.Add, contentDescription = "Добавить") }
            }

            AnimatedVisibility(visible = expanded) {
                SmallFloatingActionButton(
                    onClick = onWriteOffClick,
                ) { Icon(Icons.Default.Edit, contentDescription = "Cписать") }
            }

            AnimatedVisibility(visible = expanded) {
                SmallFloatingActionButton(
                    onClick = onSellClick,
                ) { Icon(Icons.Default.ShoppingCart, contentDescription = "Продать") }
            }

            AnimatedVisibility(visible = expanded) {
                SmallFloatingActionButton(
                    onClick = onExpensesClick,
                ) {
                    Icon(
                        painterResource(R.drawable.baseline_add_card_24),
                        contentDescription = "Покупать"
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                SmallFloatingActionButton(
                    onClick = onKillClick,
                ) {
                    Icon(
                        painterResource(R.drawable.icons8__meat100),
                        contentDescription = "Забой"
                    )
                }
            }
        }

        // Основная FAB
        FloatingActionButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                if (expanded) Icons.Default.Close else Icons.Default.Menu,
                contentDescription = "Меню"
            )
        }
    }
}*/

@Composable
private fun AnimalCountContainer(
    modifier: Modifier = Modifier,
    state: AnimalCountState,
    onIntent: (AnimalCountIntent) -> Unit
) {
    if (state.countList.isNotEmpty())
        VaccinationList2(
            modifier = modifier,
            indicatorsList = state.countList,
            onEditClick = { onIntent(AnimalCountIntent.DialogClicked(false, it)) }
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
                    painterResource(if (details) R.drawable.icon_keyboard_arrow_up else R.drawable.icon_keyboard_arrow_down),
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

    //TODO Примечание для животных
    /* val reasonNote = resourceProvider.getString(
         if (state.note.isBlank())
             R.string.animal_card_screen_add_no_note_reason
         else
             R.string.animal_card_screen_add_note_reason
     ).format(state.note)
     */

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
