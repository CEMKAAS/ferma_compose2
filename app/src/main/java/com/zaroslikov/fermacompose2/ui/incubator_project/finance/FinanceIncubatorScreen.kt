@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.incubator_project.finance

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.dto.incubator.DomainFinanceIncubatorHistory
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_11
import com.zaroslikov.fermacompose2.green_4
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.green_g_2
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BigColorCard
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.ColorCard
import com.zaroslikov.fermacompose2.ui.elements.EmptyBookmark
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.TextMiniCard
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_18
import com.zaroslikov.fermacompose2.ui.elements.text_24
import com.zaroslikov.fermacompose2.ui.elements.text_30
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.elements.AdsCard
import com.zaroslikov.fermacompose2.ui.incubator_project.journal.TranslucentCard
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.white

object FinanceIncubatorDestination : NavigationDestination {
    override val route = "finance_incubator_screen"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    val routeWithArgs = "$route/{$itemIdPT}"
}

@Composable
fun FinanceIncubatorScreen(
    viewModel: FinanceIncubatorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.finance_incubator_screen_title,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            if (state.domainFinanceIncubatorHistory.isNotEmpty())
                FinanceIncubatorContainer(
                    modifier = Modifier.modifierScreen(innerPadding),
                    state = state
                )
            else
                EmptyBookmark(
                    modifier = Modifier.padding(innerPadding),
                    iconRes = R.drawable.icon_money,
                    title = R.string.message_no_date_empty,
                    supportText = R.string.finance_incubator_screen_message_no_date_empty,
                    iconColor = green_9,
                    backgroundColor = green_4,
                    supportSecondText = R.string.finance_incubator_screen_second_message_no_date_empty
                )
    }
}


@Composable
private fun FinanceIncubatorContainer(
    modifier: Modifier,
    state: FinanceIncubatorState,
) {
    Column(
        modifier = modifier.padding(bottom = 15.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        MainCard(
            currentBalance = state.domainFinanceIncubatorMain.profit,
            income = state.domainFinanceIncubatorMain.income,
            expenditure = state.domainFinanceIncubatorMain.expenses,
            incubatorPrice = state.domainFinanceIncubatorMain.incubator,
            eggExpenses = state.domainFinanceIncubatorMain.eggsPrice,
            eggPosted = state.domainFinanceIncubatorMain.postedEgg,
            eggLosses = state.domainFinanceIncubatorMain.lossesEgg,
            postedPrice = state.domainFinanceIncubatorMain.postedPrice,
            lossesPrice = state.domainFinanceIncubatorMain.lossesPrice,
            averagePrice = state.domainFinanceIncubatorMain.averageEggPrice,
            priceSuffix = state.priceSuffix,
            averageChicksPrice = state.domainFinanceIncubatorMain.averageChicksPrice,
            costChicksPrice = state.domainFinanceIncubatorMain.costChicksPrice,
        )
        AdsCard()
        FinanceBookmarkCardList(
            list = state.domainFinanceIncubatorHistory,
            priceSuffix = state.priceSuffix
        )
    }
}

@Composable
private fun MainCard(
    currentBalance: Double,
    income: Double,
    expenditure: Double,
    incubatorPrice: Double,
    eggExpenses: Double,
    eggPosted: Int,
    eggLosses: Int,
    postedPrice: Double,
    lossesPrice: Double,
    averagePrice: Double,
    averageChicksPrice: Double,
    costChicksPrice: Double,
    priceSuffix: Suffix
) {
    val suffix = stringResource(priceSuffix.toResId())
    BigColorCard(
        glowColor = green_shamrock,
        colors = listOf(green_6, green_shamrock),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconTransaction2(
                    sizeCard = 56.dp,
                    icon = R.drawable.icon_money,
                    iconColor = white,
                    boxColor = white.copy(alpha = 0.2f)
                )
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        stringResource(R.string.support_text_current_balance),
                        style = text_14,
                        color = white.copy(alpha = 0.7f)
                    )
                    Text(
                        "${currentBalance.formatNumber()} $suffix",
                        style = text_30,
                        color = white
                    )
                }
            }
            HorizontalDivider(thickness = 1.dp, color = white.copy(0.2f))
        }
        FinanceDoubleCard(
            valueOne = income.formatNumber(),
            supportValueOne = "${eggPosted.formatNumber()} ${stringResource(R.string.suffix_chicks)}",
            valueTwo = expenditure.formatNumber(),
            supportValueTwo = stringResource(R.string.finance_incubator_screen_incubator_plus_eggs),
            suffix = suffix
        )
        IncubatorCard(incubatorPrice, suffix)
        EggCard(
            eggExpenses = eggExpenses,
            eggPosted = eggPosted,
            eggLosses = eggLosses,
            postedPrice = postedPrice,
            lossesPrice = lossesPrice,
            averagePrice = averagePrice,
            suffix = Suffix.PIECES,
            priceSuffix = suffix
        )
        FinanceDoubleChicksCard(
            averageChicksPrice = averageChicksPrice,
            costPrice = costChicksPrice,
            priceSuffix = priceSuffix
        )
    }
}

@Composable
private fun FinanceDoubleCard(
    valueOne: String,
    supportValueOne: String,
    valueTwo: String,
    supportValueTwo: String,
    suffix: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FinanceSupportCard(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.icon_arrow_up,
            stringRes = R.string.card_income,
            value = "$valueOne $suffix",
            supportValue = supportValueOne
        )
        FinanceSupportCard(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.icon_arrow_down,
            stringRes = R.string.card_expenditure,
            value = "$valueTwo $suffix",
            supportValue = supportValueTwo
        )
    }
}

@Composable
private fun IncubatorCard(
    incubatorPrice: Double,
    suffix: String
) {
    TranslucentCard {
        Column(
            modifier = Modifier.padding(13.dp),
            verticalArrangement = Arrangement.SpaceBetween
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
                        painter = painterResource(R.drawable.icon_add_product),
                        contentDescription = null,
                        tint = white,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        stringResource(R.string.start_screen_incubator_project),
                        style = text_12,
                        color = white.copy(0.8f)
                    )
                }
                Text("${incubatorPrice.formatNumber()} $suffix", style = text_18, color = white)
            }
        }
    }
}

@Composable
private fun EggCard(
    eggExpenses: Double,
    eggPosted: Int,
    eggLosses: Int,
    postedPrice: Double,
    lossesPrice: Double,
    averagePrice: Double,
    suffix: Suffix,
    priceSuffix: String,
) {
    TranslucentCard {
        Column(
            modifier = Modifier.padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RowCard(
                iconRes = R.drawable.outline_egg_24,
                titleRes = stringResource(R.string.finance_incubator_screen_egg_expenses),
                value = "${eggExpenses.formatNumber()} $priceSuffix"
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                VerticalDivider(color = white.copy(0.2f), modifier = Modifier.fillMaxHeight())
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RowCard(
                        iconRes = R.drawable.outline_check_circle_24,
                        titleRes = stringResource(R.string.finance_incubator_screen_posted_s_s).format(
                            eggPosted, stringResource(suffix.toResId())
                        ),
                        value = "${postedPrice.formatNumber()} $priceSuffix",
                        isSupportRow = true
                    )
                    RowCard(
                        iconRes = R.drawable.outline_info_24,
                        titleRes = stringResource(R.string.finance_incubator_screen_losses_s_s).format(
                            eggLosses, stringResource(suffix.toResId())
                        ),
                        value = "${lossesPrice.formatNumber()} $priceSuffix",
                        isSupportRow = true
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                HorizontalDivider(color = white.copy(0.2f))
                RowCard(
                    titleRes = stringResource(R.string.finance_incubator_screen_average_price_egg),
                    value = "${averagePrice.formatNumber()} $priceSuffix"
                )
            }
        }
    }
}


@Composable
private fun RowCard(
    @DrawableRes iconRes: Int? = null,
    titleRes: String,
    value: String,
    isSupportRow: Boolean = false
) {
    val (textAlpha, valueAlpha, iconSize) = if (isSupportRow) Triple(0.7f, 0.9f, 14.dp)
    else Triple(0.8f, 1f, 16.dp)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            iconRes?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = null,
                    tint = white,
                    modifier = Modifier.size(iconSize)
                )
            }
            Text(
                titleRes,
                style = text_12,
                color = white.copy(textAlpha)
            )
        }
        Text(value, style = text_18, color = white.copy(alpha = valueAlpha))
    }
}

@Composable
private fun FinanceDoubleChicksCard(
    averageChicksPrice: Double,
    costPrice: Double,
    priceSuffix: Suffix,
) {
    val suffix = stringResource(priceSuffix.toResId())
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FinanceChicksCard(
            modifier = Modifier.weight(1f),
            stringRes = R.string.finance_incubator_screen_average_chicks,
            value = "${averageChicksPrice.formatNumber()} $suffix"
        )
        FinanceChicksCard(
            modifier = Modifier.weight(1f),
            stringRes = R.string.finance_incubator_screen_chicks_price,
            value = "${costPrice.formatNumber()} $suffix"
        )
    }
}

@Composable
private fun FinanceChicksCard(
    modifier: Modifier = Modifier,
    @StringRes stringRes: Int,
    value: String
) {
    TranslucentCard(modifier) {
        Column(
            modifier = Modifier.padding(11.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                stringResource(stringRes),
                style = text_12,
                color = white.copy(0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                value,
                style = text_18,
                color = white,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FinanceSupportCard(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes stringRes: Int,
    value: String,
    supportValue: String
) {
    TranslucentCard(modifier) {
        Column(
            modifier = Modifier.padding(13.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        tint = white,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(stringResource(stringRes), style = text_12, color = white.copy(0.8f))
                }
                Text(value, style = text_24, color = white)
            }
            Text(supportValue, style = text_12, color = white.copy(0.6f))
        }
    }
}

@Composable
private fun FinanceBookmarkCardList(
    list: List<DomainFinanceIncubatorHistory>,
    priceSuffix: Suffix
) {
    if (list.isNotEmpty())
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.finance_incubator_screen_finance_on_bookmark),
                    style = text_16,
                    color = black_2
                )
                TextMiniCard(
                    value = list.size.toString(),
                    textColor = green_9,
                    backgroundColor = green_g_2
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                list.forEach { history ->
                    FinanceBookmarkCard(
                        title = history.title,
                        typeEgg = history.type,
                        breed = history.breed,
                        egg = history.countEgg,
                        profit = history.profit,
                        income = history.income,
                        expenses = history.expenses,
                        chicken = history.chicks,
                        averagePrice = history.priceOneEgg,
                        priceSuffix = priceSuffix,
                        postedPrice = history.postedPrice,
                        lossesPrice = history.lossesPrice,
                        postedEgg = history.postedEgg,
                        lossesEgg = history.lossesEgg,
                        suffix = Suffix.PIECES
                    )
                }
            }
        }
}

@Composable
private fun FinanceBookmarkCard(
    title: String,
    typeEgg: TypeEgg,
    breed: String?,
    egg: Int,
    profit: Double,
    income: Double,
    expenses: Double,
    chicken: Int,
    averagePrice: Double,
    priceSuffix: Suffix,
    postedPrice: Double,
    lossesPrice: Double,
    postedEgg: Int,
    lossesEgg: Int,
    suffix: Suffix,
) {
    CardFieldNew(
        padding = PaddingValues(16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(title, style = text_14, color = black_2)
                    Text(
                        "${stringResource(typeEgg.toResId())} ${breed?.let { "• $it " }}• " +
                                "$egg ${stringResource(R.string.suffix_eggs)}",
                        style = text_12,
                        color = gray_7
                    )
                }
                val (textColor, backgroundColor) = if (profit >= 0) green_9 to price_green_2 else red_14 to red_11
                TextMiniCard(
                    "${profit.formatNumber()} ${stringResource(priceSuffix.toResId())} ",
                    textColor = textColor,
                    backgroundColor = backgroundColor
                )
            }
            BorderSupportDoubleCard(
                income = income,
                expenses = expenses,
                chicken = chicken,
                averagePrice = averagePrice,
                priceSuffix = priceSuffix
            )
            SupportDoubleCard(
                postedPrice = postedPrice,
                lossesPrice = lossesPrice,
                postedEgg = postedEgg,
                lossesEgg = lossesEgg,
                priceSuffix = priceSuffix,
                suffix = suffix
            )
        }
    }
}

@Composable
private fun BorderSupportDoubleCard(
    income: Double,
    expenses: Double,
    chicken: Int,
    averagePrice: Double,
    priceSuffix: Suffix,
) {
    val suffix = stringResource(priceSuffix.toResId())
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BorderSupportCard(
            modifier = Modifier.weight(1f),
            titleRes = R.string.card_income,
            value = "${income.formatNumber()} $suffix",
            valueSupport = "$chicken ${stringResource(R.string.suffix_chicks)}",
            borderColor = green_11,
            containerColor = price_green_2
        )
        BorderSupportCard(
            modifier = Modifier.weight(1f),
            titleRes = R.string.card_expenditure,
            value = "${expenses.formatNumber()} $suffix",
            valueSupport = "${averagePrice.formatNumber()} $suffix/${stringResource(R.string.suffix_egg)}",
            borderColor = grey_2,
            containerColor = ghostly_white
        )
    }
}

@Composable
private fun SupportDoubleCard(
    postedPrice: Double,
    lossesPrice: Double,
    postedEgg: Int,
    lossesEgg: Int,
    priceSuffix: Suffix,
    suffix: Suffix,
) {
    val suffixString = stringResource(suffix.toResId())
    val priceSuffixString = stringResource(priceSuffix.toResId())
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SupportCard(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.outline_check_circle_24,
            titleRes = R.string.finance_incubator_screen_posted,
            value = "${postedPrice.formatNumber()} $priceSuffixString",
            valueSupport = "${postedEgg.formatNumber()} $suffixString",
            containerColor = price_green_2,
            textColor = green_9,
            iconColor = price_green,
        )
        SupportCard(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.outline_info_24,
            titleRes = R.string.finance_incubator_screen_losses,
            value = "${lossesPrice.formatNumber()} $priceSuffixString",
            valueSupport = "${lossesEgg.formatNumber()} $suffixString",
            containerColor = red_11,
            textColor = red_14,
            iconColor = error_base
        )
    }
}

@Composable
private fun BorderSupportCard(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    value: String,
    valueSupport: String,
    borderColor: Color,
    containerColor: Color
) {
    BorderCard(
        modifier = modifier,
        containerColor = containerColor,
        borderColor = borderColor,
        padding = PaddingValues(9.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = stringResource(titleRes), style = text_12, color = gray_7)
            Text(text = value, style = text_16, color = black_2)
            Text(text = valueSupport, style = text_12, color = gray_7)
        }
    }
}

@Composable
private fun SupportCard(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    @StringRes iconRes: Int,
    value: String,
    valueSupport: String,
    containerColor: Color,
    textColor: Color,
    iconColor: Color
) {
    ColorCard(
        modifier = modifier,
        containerColor = containerColor,
        padding = PaddingValues(9.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(12.dp)
                )
                Text(text = stringResource(titleRes), style = text_12, color = gray_7)
            }
            Text(text = value, style = text_16, color = black_2)
            Text(text = valueSupport, style = text_12, color = textColor)
        }
    }
}