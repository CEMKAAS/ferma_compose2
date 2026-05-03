@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.start.profile

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_13
import com.zaroslikov.fermacompose2.blue_14
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_13
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.grey_3
import com.zaroslikov.fermacompose2.orang_17
import com.zaroslikov.fermacompose2.orang_18
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.orang_8
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_12
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_24
import com.zaroslikov.fermacompose2.ui.elements.text_30
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard

object ProfileDestination : NavigationDestination {
    override val route = "profile_screen"
    override val titleRes = R.string.app_name
}

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                title = stringResource(R.string.profile_screen_title),
                onNavigateBackClick = onNavigateBack,
                scrollBehavior = scrollBehavior,
                onSettingsClick = { viewModel.onIntent(ProfileIntent.EditModeClick) }
            )
        },
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            ProfileContainer(
                modifier = Modifier.modifierScreen(innerPadding),
                state = state,
                onChoiceClick = { viewModel.onIntent(ProfileIntent.ChoiceCurrency(it)) },
                onValueChange = { viewModel.onIntent(ProfileIntent.NameChanged(it)) }
            )
    }
}

@Composable
private fun ProfileContainer(
    modifier: Modifier,
    state: ProfileState,
    onValueChange: (String) -> Unit,
    onChoiceClick: (Suffix) -> Unit
) {
    val currentFinanceCurrency = state.currentFinanceCurrency
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ProfileCard(state.name, isEditMode = state.isEditMode, onValueChange = onValueChange)
        FinanceCard(
            currencyList = state.currencyList,
            currentFinanceCurrency = state.currentFinanceCurrency
        ) {
            onChoiceClick(it)
        }
        currentFinanceCurrency.projectFinance?.let { projectFinance ->
            ProjectSection(
                income = projectFinance.income,
                expenses = projectFinance.expenses,
                scrap = projectFinance.scrap,
                ownNeed = projectFinance.ownNeed,
                priceSuffix = currentFinanceCurrency.priceSuffix,
            )
        }
        currentFinanceCurrency.incubatorFinance?.let { incubatorFinance ->
            IncubatorSection(
                bredEggs = incubatorFinance.bredEggs,
                hatchedEggs = incubatorFinance.hatchedEggs,
                income = incubatorFinance.income,
                expenses = incubatorFinance.expenses,
                priceSuffix = currentFinanceCurrency.priceSuffix
            )
        }
        WarningCard()
    }
}


@Composable
private fun WarningCard() {
    WarningCard(
        colorBackground = orang_4,
        colorBorder = orang_5,
        colorIcon = orang_2,
        colorIconBackground = orang_8,
        colorTitle = orang_18,
        colorText = orang_6,
        icon = R.drawable.outline_info_24,
        title = R.string.profile_screen_warning_title,
        text = R.string.profile_screen_warning_text
    )
}

@Composable
private fun ProfileCard(name: String, isEditMode: Boolean, onValueChange: (String) -> Unit) {
    CardFieldNew(
        padding = PaddingValues(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isEditMode) {
                OutlinedTextNew(
                    value = name,
                    onValueChange = onValueChange,
                    isBorderCard = false,
                    labelIntRes = R.string.profile_screen_name,
                    supportingText = R.string.profile_screen_name_support
                )
            } else {
                Text(name, style = text_16, color = black_2)
                Text(
                    stringResource(R.string.profile_screen_owner_of_farms),
                    style = text_14,
                    color = gray_7
                )
            }
        }
    }
}


@Composable
private fun ProjectSection(
    income: Double,
    expenses: Double,
    scrap: Double,
    ownNeed: Double,
    priceSuffix: Suffix
) {
    SectionBase(
        titleRes = R.string.profile_screen_analysis_project,
        container = {
            QualdhjCard(
                firstContainerColor = price_green_2,
                firstTitleRes = R.string.card_income,
                firstTitleColor = green_9,
                firstIconRes = R.drawable.baseline_arrow_outward_24,
                fistValue = income,
                firstValueSuffix = priceSuffix,
                firstValueColor = green_13,
                firstBoxColor = green_8,
                secondContainerColor = red_11,
                secondTitleRes = R.string.card_expenditure,
                secondTitleColor = red_14,
                secondIconRes = R.drawable.icon_arrow_down,
                secondValue = expenses,
                secondValueSuffix = priceSuffix,
                secondValueColor = orang_17,
                secondBoxColor = red_12,

                thirdContainerColor = orang_4,
                thirdTitleRes = R.string.card_own_need,
                thirdTitleColor = orang_6,
                thirdIconRes = R.drawable.outline_savings_24,
                thirdValue = ownNeed,
                thirdValueSuffix = priceSuffix,
                thirdValueColor = orang_18,
                thirdBoxColor = orang_8,
                fourthContainerColor = ghostly_white,
                fourthTitleRes = R.string.card_scrap,
                fourthTitleColor = grey_3,
                fourthIconRes = R.drawable.baseline_delete_24,
                fourthValue = scrap,
                fourthValueSuffix = priceSuffix,
                fourthValueColor = gray_7,
                fourthBoxColor = gray_6
            )
        }
    )
}

@Composable
private fun FinanceCard(
    currencyList: List<ProfileFinance>,
    currentFinanceCurrency: ProfileFinance,
    onChoiceClick: (Suffix) -> Unit
) {
    val currencyFilterList = currencyList.filter { !it.isSelected }
    CardFieldNew(
        containerColor = blue_3
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconTransaction2(
                    sizeCard = 32.dp,
                    icon = R.drawable.icon_money,
                    iconColor = blue_8,
                    boxColor = blue_13
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        stringResource(R.string.profile_screen_current_balance),
                        style = text_14,
                        color = blue_8
                    )
                    Text(
                        "${currentFinanceCurrency.currentBalance.formatNumber()} " +
                                stringResource(currentFinanceCurrency.priceSuffix.toResId()),
                        style = text_30,
                        color = blue_14
                    )
                }
            }
            if (currencyFilterList.isNotEmpty())
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = blue_13,
                    thickness = 1.dp
                )
            currencyFilterList.forEachIndexed { index, finance ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FinanceRow(
                        finance.currentBalance,
                        finance.priceSuffix
                    ) { onChoiceClick(finance.priceSuffix) }
                    if (index != currencyFilterList.lastIndex)
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            color = blue_13,
                            thickness = 1.dp
                        )
                }
            }
        }
    }
}

@Composable
private fun FinanceRow(
    value: Double,
    valueSuffix: Suffix,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "${value.formatNumber()} ${stringResource(valueSuffix.toResId())}",
            style = text_30,
            color = blue_14
        )
        Icon(
            painter = painterResource(R.drawable.baseline_chevron_right_24),
            contentDescription = null,
            tint = grey,
        )
    }
}


@Composable
private fun IncubatorSection(
    bredEggs: Double,
    hatchedEggs: Double,
    income: Double,
    expenses: Double,
    priceSuffix: Suffix
) {
    SectionBase(
        titleRes = R.string.profile_screen_analysis_incubator,
        container = {
            QualdhjCard(
                firstContainerColor = price_green_2,
                firstTitleRes = R.string.card_income,
                firstTitleColor = green_9,
                firstIconRes = R.drawable.baseline_arrow_outward_24,
                fistValue = income,
                firstValueSuffix = priceSuffix,
                firstValueColor = green_13,
                firstBoxColor = green_8,
                secondContainerColor = red_11,
                secondTitleRes = R.string.card_expenditure,
                secondTitleColor = red_14,
                secondIconRes = R.drawable.icon_arrow_down,
                secondValue = expenses,
                secondValueSuffix = priceSuffix,
                secondValueColor = orang_17,
                secondBoxColor = red_12,
                thirdContainerColor = orang_4,
                thirdTitleRes = R.string.journal_screen_chicks,
                thirdTitleColor = orang_6,
                thirdIconRes = R.drawable.outline_check_circle_24,
                thirdValue = bredEggs,
                thirdValueSuffix = Suffix.PIECES,
                thirdValueColor = orang_18,
                thirdBoxColor = orang_8,
                fourthContainerColor = ghostly_white,
                fourthTitleRes = R.string.journal_screen_hatch,
                fourthTitleColor = grey_3,
                fourthIconRes = R.drawable.outline_info_24,
                fourthValue = hatchedEggs,
                fourthValueSuffix = Suffix.PIECES,
                fourthValueColor = gray_7,
                fourthBoxColor = gray_6
            )
        })
}

@Composable
private fun SectionBase(
    @StringRes titleRes: Int,
    container: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            stringResource(titleRes),
            style = text_16,
            color = black_2
        )
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            container()
        }
    }
}


@Composable
private fun QualdhjCard(
    firstContainerColor: Color,
    @StringRes firstTitleRes: Int,
    firstTitleColor: Color,
    @DrawableRes firstIconRes: Int,
    fistValue: Double,
    firstValueSuffix: Suffix,
    firstValueColor: Color,
    firstBoxColor: Color,
    secondContainerColor: Color,
    @StringRes secondTitleRes: Int,
    secondTitleColor: Color,
    @DrawableRes secondIconRes: Int,
    secondValue: Double,
    secondValueSuffix: Suffix,
    secondValueColor: Color,
    secondBoxColor: Color,

    thirdContainerColor: Color,
    @StringRes thirdTitleRes: Int,
    thirdTitleColor: Color,
    @DrawableRes thirdIconRes: Int,
    thirdValue: Double,
    thirdValueSuffix: Suffix,
    thirdValueColor: Color,
    thirdBoxColor: Color,
    fourthContainerColor: Color,
    @StringRes fourthTitleRes: Int,
    fourthTitleColor: Color,
    @DrawableRes fourthIconRes: Int,
    fourthValue: Double,
    fourthValueSuffix: Suffix,
    fourthValueColor: Color,
    fourthBoxColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DoubleParameterCard(
            firstContainerColor = firstContainerColor,
            firstTitleRes = firstTitleRes,
            firstTitleColor = firstTitleColor,
            firstIconRes = firstIconRes,
            fistValue = fistValue,
            firstValueSuffix = firstValueSuffix,
            firstValueColor = firstValueColor,
            firstBoxColor = firstBoxColor,
            secondContainerColor = secondContainerColor,
            secondTitleRes = secondTitleRes,
            secondTitleColor = secondTitleColor,
            secondIconRes = secondIconRes,
            secondValue = secondValue,
            secondValueSuffix = secondValueSuffix,
            secondValueColor = secondValueColor,
            secondBoxColor = secondBoxColor
        )
        DoubleParameterCard(
            firstContainerColor = thirdContainerColor,
            firstTitleRes = thirdTitleRes,
            firstTitleColor = thirdTitleColor,
            firstIconRes = thirdIconRes,
            fistValue = thirdValue,
            firstValueSuffix = thirdValueSuffix,
            firstValueColor = thirdValueColor,
            firstBoxColor = thirdBoxColor,
            secondContainerColor = fourthContainerColor,
            secondTitleRes = fourthTitleRes,
            secondTitleColor = fourthTitleColor,
            secondIconRes = fourthIconRes,
            secondValue = fourthValue,
            secondValueSuffix = fourthValueSuffix,
            secondValueColor = fourthValueColor,
            secondBoxColor = fourthBoxColor
        )
    }
}

@Composable
private fun DoubleParameterCard(
    firstContainerColor: Color,
    @StringRes firstTitleRes: Int,
    firstTitleColor: Color,
    @DrawableRes firstIconRes: Int,
    fistValue: Double,
    firstValueSuffix: Suffix,
    firstValueColor: Color,
    firstBoxColor: Color,
    secondContainerColor: Color,
    @StringRes secondTitleRes: Int,
    secondTitleColor: Color,
    @DrawableRes secondIconRes: Int,
    secondValue: Double,
    secondValueSuffix: Suffix,
    secondValueColor: Color,
    secondBoxColor: Color
) {
    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ParameterCard(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            containerColor = firstContainerColor,
            titleRes = firstTitleRes,
            titleColor = firstTitleColor,
            iconRes = firstIconRes,
            boxColor = firstBoxColor,
            value = fistValue,
            valueSuffix = firstValueSuffix,
            valueColor = firstValueColor
        )
        ParameterCard(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            containerColor = secondContainerColor,
            titleRes = secondTitleRes,
            titleColor = secondTitleColor,
            iconRes = secondIconRes,
            boxColor = secondBoxColor,
            value = secondValue,
            valueSuffix = secondValueSuffix,
            valueColor = secondValueColor
        )
    }
}


@Composable
private fun ParameterCard(
    modifier: Modifier = Modifier,
    containerColor: Color,
    @StringRes titleRes: Int,
    titleColor: Color,
    @DrawableRes iconRes: Int,
    boxColor: Color,
    value: Double,
    valueSuffix: Suffix,
    valueColor: Color
) {
    CardFieldNew(
        modifier = modifier,
        containerColor = containerColor,
        padding = PaddingValues(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconTransaction2(
                    sizeCard = 32.dp,
                    icon = iconRes,
                    iconColor = titleColor,
                    boxColor = boxColor
                )
                Text(stringResource(titleRes), color = titleColor, style = text_12)
            }
            Text(
                text = "${value.formatNumber()} ${stringResource(valueSuffix.toResId())}",
                style = text_24,
                color = valueColor
            )
        }
    }
}