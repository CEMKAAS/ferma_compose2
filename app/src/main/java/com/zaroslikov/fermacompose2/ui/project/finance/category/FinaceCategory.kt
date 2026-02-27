@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.finance.category

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.FilterDate
import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_1
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_13
import com.zaroslikov.fermacompose2.blue_14
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.supportFun.toBorderCard
import com.zaroslikov.fermacompose2.supportFun.toColorIconBorderSecond
import com.zaroslikov.fermacompose2.supportFun.toColorIconSecond
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.supportFun.toTitleResId
import com.zaroslikov.fermacompose2.supportFun.toTitleSecondResId
import com.zaroslikov.fermacompose2.supportFun.toTransactionDrawRes
import com.zaroslikov.fermacompose2.ui.elements.BigColorCard
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CategoryBorderCard
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.CountColorCard
import com.zaroslikov.fermacompose2.ui.elements.DateRangePickerModal
import com.zaroslikov.fermacompose2.ui.elements.IconAndTextNew
import com.zaroslikov.fermacompose2.ui.elements.IconFinance
import com.zaroslikov.fermacompose2.ui.elements.IconText
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_18
import com.zaroslikov.fermacompose2.ui.elements.text_24
import com.zaroslikov.fermacompose2.ui.elements.text_36
import com.zaroslikov.fermacompose2.ui.elements.сompositions.CircleShape
import com.zaroslikov.fermacompose2.ui.elements.сompositions.FilterDateElement
import com.zaroslikov.fermacompose2.ui.elements.сompositions.Slider
import com.zaroslikov.fermacompose2.ui.finance.TransactionFinanceCard
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.monthToResString
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.white


object FinanceIncomeExpensesDestination : NavigationDestination {
    override val route = "FinanceIncomeExpenses"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemCategory"
    val routeWithArgs = "${route}?$itemIdArg={$itemIdArg}&$itemIdArgTwo={$itemIdArgTwo}"
}

@Composable
fun FinanceCategoryScreen2(
    navigateBack: () -> Unit,
    viewModel: FinanceCategoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                title = stringResource(state.financeCategory.toTitleResId()),
                scrollBehavior = scrollBehavior,
                onNavigateBackClick = navigateBack
            )
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            FinanceCategoryBody2(
                modifier = Modifier
                    .modifierScreen(innerPadding),
                balanceStructure = state.currentBalance,
                filterDate = state.filterDate,
                colors = state.financeCategory.toColorList(),
                categoryList = state.financeCategoryList,
                productList = state.financeProductList,
                category = state.financeCategory,
                currentPeriod = state.currentPeriod,
                suffix = state.suffixPrice,
                onDetailClick = {
                    viewModel.onIntent(FinanceCategoryIntent.OpenBottomSheetGroup(it))
                },
                onFilterClick = {
                    viewModel.onIntent(FinanceCategoryIntent.FilterDateClicked(it))
                }
            )
        if (state.isOpenBottomSheetGroup)
            BottomSheet(
                list = state.financeGroupList,
                category = state.financeCategory,
                currentTitleProduct = state.currentProduct.first,
                currentBalanceProduct = state.currentProduct.second,
                suffixPrice = state.suffixPrice,
                onDismissRequest = {
                    viewModel.onIntent(FinanceCategoryIntent.OpenBottomSheetGroup())
                }
            )
        if (state.isOpenCalendarDialog)
            DateRangePickerModal(
                dateBegin = state.dateBegin.second,
                dateEnd = state.dateEnd.second,
                onDateRangeSelected = {
                    viewModel.onIntent(FinanceCategoryIntent.CurrentPeriodClicked(it))
                },
                onDismiss = {
                    viewModel.onIntent(FinanceCategoryIntent.OpenCalendarDialogClicked(false))
                }
            )
    }
}

@Composable
private fun FinanceCategoryBody2(
    modifier: Modifier = Modifier,
    filterDate: FilterDate,
    currentPeriod: String,
    balanceStructure: BalanceStructure,
    categoryList: List<CategoryUi>,
    productList: List<ProductUi>,
    category: FinanceCategory,
    suffix: Suffix,
    colors: List<Color>,
    onDetailClick: (Pair<String, Double>) -> Unit,
    onFilterClick: (FilterDate) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        FilterDateElement(
            value = filterDate,
            currentPeriod = currentPeriod,
            onValueChange = { onFilterClick(it) },
        )
        CurrentBalanceCard(
            currentBalance = balanceStructure.currentBalance,
            suffix = suffix,
            colors = colors,
            isGroup = false,
            category = category
        )
        WarningCardByFinanceCategory(
            category = category,
            balanceStructure = balanceStructure,
            suffix = suffix
        )
        if (categoryList.isNotEmpty()) {
            CategoryCard(
                categoryList,
                suffixPrice = suffix,
                color = colors.first()
            )
            ProductListCard(productList, onDetailClick = { onDetailClick(it) }, suffix = suffix)
        }
    }
}

@Composable
private fun ProductListCard(
    list: List<ProductUi>,
    suffix: Suffix,
    onDetailClick: (Pair<String, Double>) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.finance_category_note_all),
                style = text_16,
                color = black_2
            )
            IconText(
                number = list.size.toString(),
                colorBackground = Color(0xFFF3F4F6),
                colorText = dark
            )
        }
        Column(
            modifier = Modifier.padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            list.forEach {
                TransactionFinanceCard(
                    title = it.title,
                    suffix = Suffix.RUBLE,
                    category = it.category,
                    price = it.price,
                    positive = it.positive,
                    onDetailClick = { onDetailClick(it.title to it.price) },
                    suffixCurrency = suffix
                )
            }
        }
    }
}


@Composable
private fun CategoryCard(
    categoryList: List<CategoryUi>,
    suffixPrice: Suffix,
    color: Color
) {
    CardFieldNew {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                stringResource(R.string.finance_category_for_category),
                style = text_16,
                color = black_2
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                categoryList.forEach { item ->
                    CategorySlider(
                        title = item.category,
                        price = item.price,
                        percentFloat = item.percentFloat,
                        percentDouble = item.percentDouble,
                        suffix = suffixPrice,
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
private fun CategorySlider(
    title: String,
    price: Double,
    percentFloat: Float,
    percentDouble: Double,
    suffix: Suffix,
    color: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleShape(color)
                Text(title, style = text_14, color = dark)
            }
            Text(
                price.formatNumber() + " ${stringResource(suffix.toResId())}",
                style = text_14,
                color = black_2
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Slider(
                modifier = Modifier.weight(1f),
                percentFloat = percentFloat,
                color = color
            )
            Text(
                "${percentDouble.formatNumber()} %",
                style = text_12,
                color = gray_7,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(0.15f)
            )
        }
    }
}

@Composable
fun WarningCard(
    colorBackground: Color,
    colorBorder: Color,
    colorIcon: Color,
    colorIconBackground: Color,
    colorTitle: Color,
    colorText: Color,
    isShowIconBackground: Boolean = true,
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes text: Int
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = colorBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            if (isShowIconBackground)
                IconTransaction2(
                    icon = icon,
                    color = colorIconBackground,
                    colorIcon = colorIcon,
                    sizeCard = 32.dp
                )
            else Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = colorIcon,
                modifier = Modifier.size((20).dp)
            )
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(stringResource(title), style = text_14, color = colorTitle)
                    Text(
                        stringResource(text),
                        style = text_12,
                        color = colorText,
                        textAlign = TextAlign.Justify
                    )
                }
        }
    }
}

@Composable
private fun CurrentBalanceCard(
    currentBalance: Double,
    totalOperation: Int? = null,
    category: FinanceCategory,
    colors: List<Color>,
    suffix: Suffix,
    isGroup: Boolean
) {
    BigColorCard(
        glowColor = colors.first(),
        colors = colors,
        padding = PaddingValues(if (!isGroup) 24.dp else 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(if (!isGroup) 8.dp else 13.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isGroup)
                    IconFinance(
                        icon = category.toDrawRes(),
                        color = Color(0x20FFFFFF)
                    )
                Text(
                    text = stringResource(
                        if (!isGroup) category.toResId()
                        else R.string.finance_category_total_sum
                    ),
                    style = if (!isGroup) text_16 else text_12,
                    color = Color(0x80CCF3DD)
                )
            }
            Text(
                text = "${currentBalance.formatNumber()} ${stringResource(suffix.toResId())}",
                style = if (!isGroup) text_36 else text_24,
                color = white
            )
            totalOperation?.let {
                Text(
                    text = stringResource(R.string.finance_category_total_operation) +
                            " $it",
                    style = text_12,
                    color = Color(0x70FFFFFF)
                )
            }
        }
    }
}

@Composable
private fun BottomSheet(
    modifier: Modifier = Modifier,
    list: List<ProductUi2>,
    currentTitleProduct: String,
    currentBalanceProduct: Double,
    category: FinanceCategory,
    suffixPrice: Suffix = Suffix.RUBLE,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier.modifierBottomSheet(false),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconTransaction2(
                            icon = category.toTransactionDrawRes(),
                            color = category.toColorIconBorderSecond(),
                            colorIcon = category.toColorIconSecond(),
                        )
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                currentTitleProduct,
                                style = /*if (isEntry) text_20 else*/ text_16,
                                color = black_1
                            )
                            /* Text(
                                 stringResource(R.string.animal_indicators_mode_edit),
                                 style = text_12,
                                 color = gray_7
                             )*/
                        }
                    }
                    IconButton(
                        onClick = onDismissRequest,
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = marengo
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = gray_6
                )
                CurrentBalanceCard(
                    currentBalance = currentBalanceProduct,
                    totalOperation = list.size,
                    suffix = suffixPrice,
                    colors = category.toColorList(),
                    category = category,
                    isGroup = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(category.toTitleSecondResId()),
                        style = text_16,
                        color = black_2
                    )
                    IconText(
                        number = list.size.toString(),
                        colorBackground = Color(0xFFF3F4F6),
                        colorText = dark
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    list.forEach {
                        TransitionGroupCard(
                            count = it.value,
                            suffix = it.suffix,
                            price = it.price,
                            priceAll = it.priceAll,
                            priceSuffix = Suffix.RUBLE,
                            category = it.category,
                            statusWriteOff = it.status,
                            animal = null,
                            date = it.data,
                            buyer = it.buyer,
                            categoryFinance = it.categoryFinance
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransitionGroupCard(
    count: Double,
    suffix: Suffix,
    price: Double,
    priceAll: Double? = null,
    priceSuffix: Suffix = Suffix.PIECES,
    category: String? = null,
    statusWriteOff: Boolean? = null,
    animal: String? = null,
    date: String,
    buyer: String? = null,
    categoryFinance: FinanceCategory
) {
    val priceFinal = priceAll ?: price
    val categoryNoNull = category ?: stringResource(R.string.support_text_no_category)
    val (colorPrice, positive) = when (categoryFinance) {
        FinanceCategory.SALE, FinanceCategory.OWN_NEED -> price_green to "+"
        else -> error_base to "-"
    }
    val dateList = date.split(".")

    BorderCard(
        padding = PaddingValues(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    CategoryBorderCard(categoryNoNull)
                    CountColorCard(count, suffix, suffix.toColorList())
                }
                Text(
                    text = "$positive${priceFinal.formatNumber()} " +
                            stringResource(priceSuffix.toResId()),
                    style = text_18,
                    color = colorPrice
                )
            }
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconAndTextNew(
                    iconRes = R.drawable.baseline_calendar_month_24,
                    valueString = "${dateList[0]} ${stringResource(monthToResString(dateList[1].toInt()))} ${dateList[2]}",
                    iconColor = grey
                )
                statusWriteOff?.let { status ->
                    val (iconRes, valueString) = if (status)
                        R.drawable.baseline_cottage_24 to R.string.ration_button_own_needs
                    else
                        R.drawable.baseline_delete_24 to R.string.ration_button_disposal
                    IconAndTextNew(
                        iconRes = iconRes,
                        valueString = stringResource(valueString),
                        iconColor = if (status) violet_1 else error_base,
                    )
                }
                IconAndTextNew(
                    iconRes = R.drawable.baseline_currency_ruble_24,
                    valueString = "${price.formatNumber()} " +
                            "${stringResource(priceSuffix.toResId())}/" +
                            stringResource(suffix.toResId()),
                    iconColor = grey
                )
                buyer?.let {
                    IconAndTextNew(
                        iconRes = R.drawable.baseline_person_24,
                        valueString = it,
                        iconColor = grey
                    )
                }
            }
        }
    }
}


@Composable
private fun BalanceStructureCard(
    income: Double,
    expenses: Double,
    ownNeed: Double,
    scrap: Double,
    price: Double,
    profit: Double,
    suffix: Suffix
) {
    CardFieldNew {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                stringResource(R.string.finance_category_balance_structure),
                style = text_16,
                color = black_2
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FinanceCategoryCard(
                    category = FinanceCategory.SALE,
                    price = income,
                    suffix = suffix
                )
                FinanceCategoryCard(
                    category = FinanceCategory.EXPENSES,
                    price = expenses,
                    suffix = suffix
                )
                FinanceCategoryCard(
                    category = FinanceCategory.OWN_NEED,
                    price = ownNeed,
                    suffix = suffix
                )
                FinanceCategoryCard(
                    category = FinanceCategory.SCRAP,
                    price = scrap,
                    suffix = suffix
                )
                HorizontalDivider(thickness = 1.dp, color = grey_2)
                FinanceCategoryCard(
                    category = FinanceCategory.PROFIT,
                    price = profit,
                    suffix = suffix
                )
            }
        }
    }
}

@Composable
private fun FinanceCategoryCard(
    category: FinanceCategory,
    price: Double,
    suffix: Suffix
) {
    val positive = when (category) {
        FinanceCategory.SALE, FinanceCategory.OWN_NEED -> "+"
        FinanceCategory.EXPENSES, FinanceCategory.SCRAP -> "-"
        FinanceCategory.PROFIT -> {
            if (price > 0) "" else "-"
        }
    }
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = category.toColorIconBorderSecond()
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconTransaction2(
                    icon = category.toDrawRes(),
                    color = category.toBorderCard(),
                    colorIcon = category.toColorIconSecond()
                )
                Text(stringResource(category.toTitleSecondResId()), style = text_16, color = dark)
            }
            Text(
                "$positive${price.formatNumber()} ${stringResource(suffix.toResId())}",
                style = text_16,
                color = category.toColorIconSecond()
            )
        }
    }
}

@Composable
private fun WarningCardByFinanceCategory(
    category: FinanceCategory,
    balanceStructure: BalanceStructure,
    suffix: Suffix
) {
    when (category) {
        FinanceCategory.OWN_NEED ->
            WarningCard(
                colorBackground = orang_4,
                colorBorder = orang_5,
                colorIcon = orang_2,
                colorIconBackground = Color(0xFFFEF3C6),
                colorTitle = Color(0xFF7B3306),
                colorText = orang_6,
                icon = category.toDrawRes(),
                title = R.string.finance_category_own_need_title,
                text = R.string.finance_category_own_need_text
            )

        FinanceCategory.SCRAP -> WarningCard(
            colorBackground = Color(0xFFFEF2F2),
            colorBorder = Color(0xFFFFC9C9),
            colorIcon = error_base,
            colorIconBackground = Color(0xFFFFE2E2),
            colorTitle = Color(0xFF82181A),
            colorText = Color(0xFFC10007),
            icon = category.toDrawRes(),
            title = R.string.finance_category_scrap_title,
            text = R.string.finance_category_scrap_text
        )

        FinanceCategory.PROFIT -> {
            WarningCard(
                colorBackground = blue_3,
                colorBorder = blue_9,
                colorIcon = blue_1,
                colorIconBackground = blue_13,
                colorTitle = blue_14,
                colorText = blue_8,
                icon = category.toDrawRes(),
                title = R.string.finance_category_profit_title,
                text = R.string.finance_category_profit_text
            )
            BalanceStructureCard(
                income = balanceStructure.income,
                expenses = balanceStructure.expenses,
                ownNeed = balanceStructure.ownNeed,
                scrap = balanceStructure.scrap,
                price = balanceStructure.currentBalance,
                profit = balanceStructure.profit,
                suffix = suffix
            )
        }

        FinanceCategory.SALE -> WarningCard(
            colorBackground = Color(0xFFF0FDF4),
            colorBorder = Color(0xFFB9F8CF),
            colorIcon = price_green,
            colorIconBackground = Color(0xFFDCFCE7),
            colorTitle = Color(0xFF008236),
            colorText = Color(0xFF008236),
            icon = category.toDrawRes(),
            title = R.string.finance_category_income_title,
            text = R.string.finance_category_income_text
        )

        FinanceCategory.EXPENSES -> WarningCard(
            colorBackground = Color(0xFFFEF2F2),
            colorBorder = Color(0xFFFFC9C9),
            colorIcon = error_base,
            colorIconBackground = Color(0xFFFFE2E2),
            colorTitle = Color(0xFF82181A),
            colorText = Color(0xFFC10007),
            icon = category.toDrawRes(),
            title = R.string.finance_category_expenses_title,
            text = R.string.finance_category_expenses_text
        )
    }
}