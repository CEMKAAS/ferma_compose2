package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.dto.add.DomainFastAddProduct
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_13
import com.zaroslikov.fermacompose2.blue_14
import com.zaroslikov.fermacompose2.blue_15
import com.zaroslikov.fermacompose2.blue_16
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_4
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.green_1
import com.zaroslikov.fermacompose2.green_2
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_g_1
import com.zaroslikov.fermacompose2.green_g_3
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.red_3
import com.zaroslikov.fermacompose2.red_4
import com.zaroslikov.fermacompose2.red_5
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogStandard
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.BorderShowAllButton
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.EmptyBookmark
import com.zaroslikov.fermacompose2.ui.elements.IconAndTextNew
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.TextMiniCard
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.BaseSlider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.formatNumber
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
    navigateToNote: (Long) -> Unit,
    navigationToNewYear: (Pair<Boolean, Int>) -> Unit,
    navigationToAnalysis: (Triple<Long, String, Suffix>) -> Unit,
    viewModel: WarehouseViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.warehouse_screen_title,
                scrollBehavior = scrollBehavior,
                onNavigateBackClick = navigateToStart,
                onSettingsClick = if (state.isArchive) null else {
                    { navigateToEdit(state.idPT) }
                },
                onNoteClick = { navigateToNote(state.idPT) }
            )
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            if (state.productList.isEmpty() && state.expensesList.isEmpty() && state.fastAddList.isEmpty())
                EmptyBookmark(
                    modifier = Modifier.padding(innerPadding),
                    iconRes = R.drawable.baseline_warehouse_24,
                    title = R.string.warehouse_screen_welcome,
                    supportText = R.string.warehouse_screen_welcome_support_text,
                    supportSecondText = R.string.warehouse_screen_welcome_support_second_text,
                    iconColor = blue_4,
                    backgroundColor = blue_3,
                )
            else
                WarehouseContainer(
                    modifier = Modifier.modifierScreen(innerPadding),
                    state = state,
                    onFastAddClick = { viewModel.onIntent(WarehouseIntent.FastAddClicked(it)) },
                    onShowFastAddClick = { viewModel.onIntent(WarehouseIntent.ShowFastAddClicked(it)) },
                    onAnalysisNavClick = {
                        navigationToAnalysis(Triple(state.idPT, it.first, it.second))
                    },
                    onWriteOffClick = {
                        viewModel.onIntent(
                            WarehouseIntent.OpenWarningWriteOffAlterDialogClicked(it)
                        )
                    }
                )

        if (state.isOpenWarningWriteOffAlterDialog)
            WarningWriteOffAlterDialog(
                currentFoodOnWriteOff = state.currentFoodOnWriteOff,
                onDismissRequest = {
                    viewModel.onIntent(
                        WarehouseIntent.OpenWarningWriteOffAlterDialogClicked(null)
                    )
                },
                onClick = {
                    viewModel.onIntent(WarehouseIntent.FoodOnWriteOffChange)
                }
            )
    }
}

@Composable
private fun WarehouseContainer(
    modifier: Modifier = Modifier,
    state: WarehouseState,
    onAnalysisNavClick: (Pair<String, Suffix>) -> Unit,
    onShowFastAddClick: (Boolean) -> Unit,
    onFastAddClick: (DomainFastAddProduct) -> Unit,
    onWriteOffClick: (Long) -> Unit
) {
    Column(
        modifier = modifier
            .padding(bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (!state.isArchive)
            FastAdd(
                isShowFastAddProduct = state.isShowFastAddProduct, list = state.fastAddList,
                onShowClick = { onShowFastAddClick(it) },
                onClick = { onFastAddClick(it) }
            )
        if (state.productList.isEmpty() && state.expensesList.isEmpty())
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                EmptyBookmark(
                    iconRes = R.drawable.icon_open_box,
                    title = R.string.warehouse_screen_warehouse_empty,
                    supportText = R.string.warehouse_screen_warehouse_empty_support_text,
                    supportSecondText = R.string.warehouse_screen_warehouse_empty_support_second_text,
                    iconColor = blue_4,
                    backgroundColor = blue_3,
                )
            }
        else {
            if (state.productList.isNotEmpty())
                WarehouseSection(
                    titleRes = R.string.add_screen_title2,
                    iconRes = R.drawable.icon_add_product,
                    list = state.productList,
                    textColor = green_2,
                    borderColor = green_1,
                    iconColor = green_shamrock,
                    backgroundMiniColor = green_g_1,
                ) { item ->
                    ProductCard(
                        title = item.title,
                        value = item.count,
                        suffix = item.suffix
                    ) {
                        onAnalysisNavClick(item.title to item.suffix)
                        AppMetrica.reportEvent("Переход в полный анализ продукта со склада")
                    }
                }
            if (state.foodList.isNotEmpty())
                WarehouseSection(
                    titleRes = R.string.warehouse_screen_warehouse_foods,
                    iconRes = R.drawable.wheat_24dp_000000_fill0_wght400_grad0_opsz24,
                    list = state.foodList,
                    textColor = orang_6,
                    borderColor = orang_5,
                    iconColor = orang_2,
                    backgroundMiniColor = orang_4,
                ) { item ->
                    SliderFood(
                        title = item.title,
                        daysEnd = item.daysEnd,
                        weightAll = item.weightAll,
                        weightSuffix = item.weightSuffix,
                        percentFloat = item.percentFloat,
                        onWriteOffClick = if (state.isArchive) null else {
                            { onWriteOffClick(item.id) }
                        }
                    )
                }
            if (state.expensesList.isNotEmpty())
                WarehouseSection(
                    titleRes = R.string.warehouse_screen_products,
                    iconRes = R.drawable.icon_expenses,
                    list = state.expensesList,
                    textColor = blue_8,
                    borderColor = blue_16,
                    iconColor = blue_1,
                    backgroundMiniColor = blue_3,
                ) { item ->
                    ProductCard(
                        title = item.title,
                        value = item.count,
                        suffix = item.suffix
                    ) { }
                }
        }
    }
}

@Composable
private fun ProductCard(
    modifier: Modifier = Modifier,
    title: String,
    value: Double,
    suffix: Suffix,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ghostly_white),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = text_14, color = black_2)
            Text(
                "${value.formatNumber()} " + stringResource(suffix.toResId()),
                style = text_14,
                color = black_2
            )
        }
    }
}


@Composable
private fun SliderFood(
    title: String,
    daysEnd: Int,
    weightAll: Double,
    weightSuffix: Suffix,
    percentFloat: Float,
    onWriteOffClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ghostly_white)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        style = text_14,
                        color = black_2,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                    Row {
                        IconAndTextNew(
                            iconRes = R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24,
                            valueString = "${weightAll.formatNumber()} " +
                                    stringResource(weightSuffix.toResId()),
                            iconColor = blue_1,
                            iconSize = 14.dp,
                            textColor = black_2,
                            textStyle = text_12
                        )
                        Text(" / ", style = text_12)
                        IconAndTextNew(
                            iconRes = R.drawable.baseline_access_time_24,
                            valueString = "${daysEnd.formatNumber()} ${stringResource(R.string.expenses_screen_days)}",
                            iconColor = violet_1,
                            iconSize = 14.dp,
                            textColor = black_2,
                            textStyle = text_12
                        )
                    }
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
            onWriteOffClick?.let {
                IconButton(onClick = onWriteOffClick) {
                    Icon(
                        painter = painterResource(R.drawable.icon_trash),
                        contentDescription = null,
                        tint = error_base
                    )
                }
            }
        }
    }
}


@Composable
private fun <T> WarehouseSection(
    @StringRes titleRes: Int,
    @DrawableRes iconRes: Int,
    list: List<T>,
    iconColor: Color,
    backgroundMiniColor: Color,
    textColor: Color,
    borderColor: Color,
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
                TextMiniCard(
                    "${list.size} " +
                            stringResource(R.string.warehouse_screen_positions),
                    textColor = textColor,
                    backgroundColor = backgroundMiniColor
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (list.isNotEmpty()) {
                    for (i in list.indices) {
                        itemCard(list[i])
                        if (i == 4 && !expanded)
                            break
                    }
                }
            }
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = expanded
            ) {
                if (list.size > 4)
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
private fun FastAdd(
    isShowFastAddProduct: Boolean,
    list: List<DomainFastAddProduct>,
    onShowClick: (Boolean) -> Unit,
    onClick: (DomainFastAddProduct) -> Unit
) {
    val icon =
        if (isShowFastAddProduct) R.drawable.icon_keyboard_arrow_up else R.drawable.icon_keyboard_arrow_down

    Card(
        onClick = { onShowClick(!isShowFastAddProduct) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = blue_3
        ),
        border = BorderStroke(
            width = 1.dp,
            color = blue_9
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.baseline_electric_bolt_24),
                        contentDescription = null,
                        tint = blue_1
                    )
                    Text(
                        stringResource(R.string.warehouse_screen_fast_add),
                        style = text_16,
                        color = blue_14
                    )
                }
                Icon(
                    painterResource(icon),
                    contentDescription = null,
                    tint = blue_1
                )
            }
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = isShowFastAddProduct
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    list.forEach {
                        FastAddCard(
                            title = it.title,
                            count = it.count,
                            suffix = it.suffix,
                            category = it.category,
                            animal = it.animalName,
                            onClick = { onClick(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FastAddCard(
    title: String,
    count: Double,
    suffix: Suffix,
    animal: String?,
    category: String?,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = white
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconTransaction2(
                    icon = R.drawable.icon_add,
                    iconColor = blue_1,
                    boxColor = blue_13,
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(title, style = text_14, color = black_2)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        animal?.let {
                            TextMiniCard(
                                it,
                                textColor = blue_15,
                                backgroundColor = green_g_3
                            )
                        }
                        if (animal != null && category != null) Text(
                            "•",
                            style = text_12,
                            color = grey
                        )
                        category?.let {
                            Text(it, style = text_12, color = marengo)
                        }
                    }
                }
            }
            Text(
                "${count.formatNumber()} " + stringResource(suffix.toResId()),
                style = text_14,
                color = black_2
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