package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.WarehouseCard
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_4
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_12
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.red_3
import com.zaroslikov.fermacompose2.red_4
import com.zaroslikov.fermacompose2.red_5
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogStandard
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.BorderShowAllButton
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.empty_list.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.empty_list.EmptyBookmark
import com.zaroslikov.fermacompose2.ui.elements.IconAndTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextMiniCard
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.BaseSlider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.elements.AdsCard
import com.zaroslikov.fermacompose2.ui.project.sections.recycling.RecyclingMinuteRefresh
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseViewModel
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.white
import io.appmetrica.analytics.AppMetrica

object WarehouseDestination : NavigationDestination {
    override val route = "warehouse"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehouseScreen(
    navigateToStart: () -> Unit,
    navigateToEdit: (Long) -> Unit,
    navigateToCard: (WarehouseCard) -> Unit,
    viewModel: WarehouseViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val recordCounts by viewModel.recordCounts.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var editing by rememberSaveable { mutableStateOf(false) }
    val canChangeLayout = !state.isLoading
    val layout = remember(state.pendingCardCodes, state.settings.warehouseCardCodes) {
        state.visibleLayout
    }

    RecyclingMinuteRefresh(viewModel::refreshRecycling)
    BackHandler(enabled = editing) { editing = false }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = white,
        topBar = {
            if (editing) {
                WarehouseEditHeader(
                    hiddenCards = WarehouseCard.defaultOrder - layout.cards.toSet(),
                    canChangeLayout = canChangeLayout,
                    onRestore = { viewModel.onIntent(WarehouseIntent.ChangeCards(layout.restore(it))) },
                    onDone = { editing = false }
                )
            } else {
                TopAppBarBack(
                    intRes = R.string.warehouse_screen_title,
                    scrollBehavior = scrollBehavior,
                    onNavigateBackClick = navigateToStart,
                    onSettingsClick = if (state.isLoading || state.isArchive) null else {
                        { navigateToEdit(state.idPT) }
                    },
                    settingsDescription = R.string.warehouse_project_settings,
                    titleStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    ) { innerPadding ->
        if (state.isLoading) {
            CircularProgress(Modifier.padding(innerPadding))
        } else {
            WarehouseCards(
                layout = layout,
                counts = recordCounts.counts + mapOf(
                    WarehouseCard.PRODUCTION to state.productList.count { it.countOfWarehouse == CountOfWarehouse.Have },
                    WarehouseCard.FEED to state.foodList.size,
                    WarehouseCard.GOODS to state.expensesList.count { it.countOfWarehouse == CountOfWarehouse.Have }
                ),
                tasksToday = recordCounts.tasksToday,
                editing = editing,
                canChangeLayout = canChangeLayout,
                onStartEditing = { editing = true },
                onOpenCard = navigateToCard,
                onLayoutChange = { viewModel.onIntent(WarehouseIntent.ChangeCards(it)) },
                modifier = Modifier.padding(innerPadding)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .widthIn(max = 600.dp),
                footerContent = {
                    if (state.productList.isNotEmpty() || state.expensesList.isNotEmpty()) AdsCard()
                }
            ) {
                val preview by viewModel.calendarPreview.collectAsStateWithLifecycle()
                WarehouseCalendarPreviewCardContent(preview)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WarehouseEditHeader(
    hiddenCards: List<WarehouseCard>,
    canChangeLayout: Boolean,
    onRestore: (WarehouseCard) -> Unit,
    onDone: () -> Unit
) {
    var showHidden by remember { mutableStateOf(false) }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = 600.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val compact = maxWidth < 320.dp || LocalDensity.current.fontScale >= 1.3f
        val title: @Composable () -> Unit = {
            Text(
                text = stringResource(R.string.warehouse_cards_edit_title),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = gray_7
            )
        }
        val actions: @Composable () -> Unit = {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box {
                    Button(
                        onClick = { showHidden = true },
                        enabled = canChangeLayout,
                        modifier = Modifier.heightIn(min = 48.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ghostly_white,
                            contentColor = black_2
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(
                            text = stringResource(R.string.warehouse_cards_show_hidden),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showHidden,
                        onDismissRequest = { showHidden = false }
                    ) {
                        if (hiddenCards.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.warehouse_cards_no_hidden)) },
                                onClick = {},
                                enabled = false
                            )
                        }
                        for (card in hiddenCards) {
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(R.string.warehouse_cards_restore, card.localizedWarehouseLabel()))
                                },
                                enabled = canChangeLayout,
                                onClick = {
                                    showHidden = false
                                    onRestore(card)
                                }
                            )
                        }
                    }
                }
                Button(
                    onClick = onDone,
                    modifier = Modifier.heightIn(min = 48.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = black_2, contentColor = white),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(stringResource(R.string.warehouse_cards_done))
                }
            }
        }
        if (compact) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                title()
                actions()
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.padding(end = 8.dp)) { title() }
                actions()
            }
        }
    }
}

internal object WarehouseBalanceDestination {
    const val route = "warehouse_balance"
    const val cardArg = "card"
    val routeWithArgs = "$route/{${WarehouseDestination.itemIdArg}}/{$cardArg}"
}

@Composable
internal fun WarehouseBalanceScreen(
    card: WarehouseCard,
    navigateBack: () -> Unit,
    navigateToAnalysis: (Triple<Long, String, Suffix>) -> Unit,
    viewModel: WarehouseViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val titleRes = when (card) {
        WarehouseCard.PRODUCTION -> R.string.add_screen_title2
        WarehouseCard.FEED -> R.string.warehouse_screen_warehouse_foods
        WarehouseCard.GOODS -> R.string.warehouse_screen_products
        else -> error("Для карточки нет экрана остатков: ${card.code}")
    }

    RecyclingMinuteRefresh(viewModel::refreshRecycling)

    Scaffold(
        containerColor = white,
        topBar = { TopAppBarBack(intRes = titleRes, onNavigateBackClick = navigateBack) }
    ) { innerPadding ->
        if (state.isLoading) {
            CircularProgress(Modifier.padding(innerPadding))
        } else {
            Box(Modifier.padding(innerPadding)) {
                when (card) {
                    WarehouseCard.PRODUCTION -> {
                        BalanceList(
                            items = state.productList,
                            showAll = state.isShowAllProduct,
                            onShowAll = { viewModel.onIntent(WarehouseIntent.ShowAllProductClicked(it)) },
                            onItemClick = {
                                navigateToAnalysis(Triple(state.idPT, it.title, it.suffix))
                                AppMetrica.reportEvent("Переход в полный анализ продукта со склада")
                            }
                        )
                    }
                    WarehouseCard.FEED -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item(key = "feed_summary") { WarehouseFeedSummaryCard(state.feedSummary) }
                            if (state.foodList.isEmpty()) {
                                item(key = "feed_empty") { EmptyBalance(Modifier.padding(vertical = 24.dp)) }
                            }
                            items(state.foodList, key = { it.id }) { item ->
                                SliderFood(
                                    title = item.title,
                                    daysEnd = item.daysEnd,
                                    weightAll = item.weightAll,
                                    weightSuffix = item.weightSuffix,
                                    percentFloat = item.percentFloat,
                                    onWriteOffClick = if (state.isArchive) null else {
                                        { viewModel.onIntent(WarehouseIntent.OpenWarningWriteOffAlterDialogClicked(item.id)) }
                                    }
                                )
                            }
                        }
                    }
                    WarehouseCard.GOODS -> BalanceList(
                        items = state.expensesList,
                        showAll = state.isShowAllExpenses,
                        onShowAll = { viewModel.onIntent(WarehouseIntent.ShowAllExpensesClicked(it)) },
                    )
                    else -> Unit
                }
            }
        }

        if (card == WarehouseCard.FEED && state.isOpenWarningWriteOffAlterDialog) {
            WarningWriteOffAlterDialog(
                currentFoodOnWriteOff = state.currentFoodOnWriteOff,
                onDismissRequest = { viewModel.onIntent(WarehouseIntent.OpenWarningWriteOffAlterDialogClicked(null)) },
                onClick = { viewModel.onIntent(WarehouseIntent.FoodOnWriteOffChange) }
            )
        }
    }
}

@Composable
private fun BalanceList(
    items: List<WarehouseItem>,
    showAll: Boolean,
    onShowAll: (Boolean) -> Unit,
    onItemClick: ((WarehouseItem) -> Unit)? = null
) {
    val inStock = remember(items) { items.filter { it.countOfWarehouse == CountOfWarehouse.Have } }
    val visibleItems = if (showAll) items else inStock
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.warehouse_balance_visible_count, visibleItems.size.formatNumber()),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = gray_7
            )
            if (inStock.size < items.size) {
                IconToggleButton(checked = showAll, onCheckedChange = onShowAll) {
                    Icon(
                        painter = painterResource(
                            if (showAll) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                        ),
                        contentDescription = stringResource(
                            if (showAll) R.string.warehouse_balance_hide_nonpositive
                            else R.string.warehouse_balance_show_nonpositive
                        ),
                        tint = gray_7,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        when {
            items.isEmpty() -> EmptyBalance()
            visibleItems.isEmpty() -> Text(
                text = stringResource(R.string.warehouse_balance_no_positive),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = gray_7,
                textAlign = TextAlign.Center
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(visibleItems) { item ->
                    WarehouseBalanceRow(item, onItemClick?.let { { it(item) } })
                }
            }
        }
    }
}

@Composable
private fun WarehouseBalanceRow(item: WarehouseItem, onClick: (() -> Unit)?) {
    val (textColor, backgroundColor, borderColor) = when (item.countOfWarehouse) {
        CountOfWarehouse.Have -> Triple(black_2, white, grey_2)
        CountOfWarehouse.Zero -> Triple(marengo, gray_6, grey_2)
        CountOfWarehouse.Minus -> Triple(red_14, red_11, red_12)
    }
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            item.title,
            modifier = Modifier.weight(1.6f),
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
        Text(
            "${item.count.formatNumber()} ${stringResource(item.suffix.toResId())}",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            color = textColor
        )
    }
}

@Composable
private fun EmptyBalance(modifier: Modifier = Modifier) {
    EmptyBookmark(
        modifier = modifier,
        iconRes = R.drawable.icon_open_box,
        title = R.string.warehouse_screen_warehouse_empty,
        supportText = R.string.warehouse_screen_warehouse_empty_support_text,
        supportSecondText = R.string.warehouse_screen_warehouse_empty_support_second_text,
        iconColor = blue_4,
        backgroundColor = blue_3
    )
}

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    title: String,
    value: Double,
    suffix: Suffix,
    countOfWarehouse: CountOfWarehouse,
    onClick: (() -> Unit)? = null
) {
    val (colorText, containerColor, borderColor) = when (countOfWarehouse) {
        CountOfWarehouse.Have -> Triple(black_2, ghostly_white, ghostly_white)
        CountOfWarehouse.Zero -> Triple(grey, gray_6, grey_2)
        CountOfWarehouse.Minus -> Triple(error_base, red_11, red_12)
    }
    CardFieldNew(
        modifier = modifier,
        padding = PaddingValues(12.dp),
        shape = RoundedCornerShape(8.dp),
        containerColor = containerColor,
        elevation = 2.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = text_14, color = colorText, modifier = Modifier.weight(1f))
            Text(
                "${value.formatNumber()} " + stringResource(suffix.toResId()),
                style = text_16,
                color = colorText,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
private fun SliderFood(
    title: String,
    daysEnd: Int?,
    weightAll: Double,
    weightSuffix: Suffix,
    percentFloat: Float,
    onWriteOffClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = white),
        border = androidx.compose.foundation.BorderStroke(1.dp, grey_2)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = if (onWriteOffClick == null) 14.dp else 4.dp)
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = black_2
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconAndTextNew(
                        iconRes = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
                        valueString = "${weightAll.formatNumber()} " +
                                stringResource(weightSuffix.toResId()),
                        iconColor = blue_1,
                        iconSize = 14.dp,
                        textColor = gray_7,
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    IconAndTextNew(
                        iconRes = R.drawable.baseline_access_time_24,
                        valueString = feedForecastLabel(daysEnd, R.string.warehouse_feed_consumption_not_set),
                        iconColor = violet_1,
                        iconSize = 14.dp,
                        textColor = gray_7,
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
                BaseSlider(
                    percentFloat = percentFloat,
                    color = when {
                        percentFloat > 0.45f -> green_6
                        percentFloat < 0.45f && percentFloat > 0.25f -> orang_9
                        else -> error_base
                    }
                )
            }
            onWriteOffClick?.let { writeOff ->
                IconButton(onClick = writeOff) {
                    Icon(
                        painter = painterResource(R.drawable.icon_trash),
                        contentDescription = stringResource(R.string.warehouse_screen_warehouse_food_write_off),
                        tint = error_base,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun <T> WarehouseSection(
    @StringRes titleRes: Int,
    @DrawableRes iconRes: Int,
    list: List<T>,
    iconColor: Color,
    backgroundMiniColor: Color,
    textColor: Color,
    borderColor: Color,
    isShowAllProduct: Boolean = false,
    onClick: ((Boolean) -> Unit)? = null,
    itemCard: @Composable (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    CardFieldNew {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painterResource(iconRes), contentDescription = null, tint = iconColor)
                    Text(stringResource(titleRes), style = text_16, color = black_2)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextMiniCard(
                        "${list.size} " +
                                stringResource(R.string.warehouse_screen_positions),
                        textColor = textColor,
                        backgroundColor = backgroundMiniColor
                    )
                    if (onClick != null)
                        Box(
                            Modifier
                                .background(color = gray_6, shape = RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(onClick = { onClick(!isShowAllProduct) })
                                .padding(8.dp),
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(14.dp),
                                painter = painterResource(
                                    if (isShowAllProduct) R.drawable.ic_visibility_off
                                    else R.drawable.ic_visibility
                                ),
                                contentDescription = null,
                                tint = grey
                            )
                        }
                }
            }
            Column(
                modifier = Modifier
                    .animateContentSize()
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (list.isNotEmpty())
                    for (i in list.indices) {
                        itemCard(list[i])
                        if (i == 3 && !expanded)
                            break
                    }
                else
                    if (onClick != null)
                        EmptyList(onClick = { onClick(true) })
            }
        }
        AnimatedVisibility(
            visible = list.size > 4
        ) {
            Column {
                Spacer(Modifier.padding(vertical = 8.dp))
                BorderShowAllButton(
                    listSize = list.size,
                    textColor = textColor,
                    borderColor = borderColor,
                    isShowMore = expanded
                ) { expanded = !expanded }
            }
        }
    }
}

@Composable
private fun EmptyList(onClick: () -> Unit) {
    BorderCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        padding = PaddingValues(16.dp),
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                stringResource(R.string.warehouse_screen_warehouse_item_empty_list),
                style = text_14,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp,
                color = gray_7,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                stringResource(R.string.warehouse_screen_warehouse_item_empty_list_text),
                style = text_12,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.sp,
                color = grey,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
private fun WarningWriteOffAlterDialog(
    currentFoodOnWriteOff: FoodListUi?,
    onDismissRequest: () -> Unit, onClick: () -> Unit
) {
    AlertDialogStandard(
        titleRes = R.string.warehouse_screen_warehouse_write_off_food,
        iconRes = R.drawable.baseline_edit_note_24,
        titleBackgroundColor = red_3,
        onDismissRequest = onDismissRequest,
        onClick = onClick,
        colors = listOf(red_4, red_5),
        textButtonRes = R.string.button_write_off
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(
                    R.string.warehouse_screen_warehouse_write_off_food_text
                ),
                style = text_14,
                color = marengo
            )
            currentFoodOnWriteOff?.let {
                WarningFoodOnWriteOffCard(
                    title = it.title,
                    weightAll = it.weightAll,
                    weightSuffix = it.weightSuffix,
                    percentFloat = it.percentFloat
                )
            }
        }
    }
}

@Composable
fun WarningFoodOnWriteOffCard(
    title: String,
    weightAll: Double,
    weightSuffix: Suffix,
    percentFloat: Float
) {
    BorderCard(
        containerColor = orang_4,
        borderColor = orang_5,
        padding = PaddingValues(17.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = text_14,
                    color = black_2
                )
                TextMiniCard(
                    value = "${weightAll.formatNumber()} " +
                            stringResource(weightSuffix.toResId()),
                    textColor = white,
                    backgroundColor = orang_2
                )
            }
            BaseSlider(
                percentFloat = percentFloat,
                color = when {
                    percentFloat > 0.45f -> green_6
                    percentFloat < 0.45f && percentFloat > 0.25f -> orang_9
                    else -> error_base
                }
            )
        }
    }
}

@Composable
fun TextButtonWarehouse(
    onClick: () -> Unit,
    boolean: Boolean,
    title: String = "",
    @StringRes intRes: Int = R.string.support_text_count_warehouse_s
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.wrapContentHeight()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.weight(1f) // линия слева занимает свободное место
            )
            Icon(
                painterResource(if (boolean) R.drawable.icon_keyboard_arrow_up else R.drawable.icon_keyboard_arrow_down),
                contentDescription = "Показать меню",
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = stringResource(intRes),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(end = 10.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.weight(1f) // линия справа занимает свободное место
            )
        }
    }
}
