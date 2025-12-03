@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.finance.finace2

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice
import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_11
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.IconFinance
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_36
import com.zaroslikov.fermacompose2.ui.finance.TransactionFinanceCard
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.formatNumber
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
                title = stringResource(R.string.finance_screen_title),
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
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
                currentBalance = state.currentBalance,
                colors = state.financeCategory.toColorList(),
                categoryList = state.financeCategoryList,
                productList = state.financeProductList,
                category = state.financeCategory
            )
    }
}

@Composable
private fun FinanceCategoryBody2(
    modifier: Modifier = Modifier,
    currentBalance: Double,
    categoryList: List<CategoryUi>,
    productList: List<ProductUi>,
    category: FinanceCategory,
    suffix: Suffix = Suffix.RUBLE,
    colors: List<Color>
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        CurrentBalanceCard(
            currentBalance = currentBalance,
            suffix = suffix,
            intRes = category.toResId(),
            icon = category.toDrawRes(),
            colors = colors,
        )
        when (category) {
            FinanceCategory.OWN_NEED -> {
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
            }

            FinanceCategory.SCRAP -> {
                WarningCard(
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
            }

            else -> {}
        }

        if (categoryList.isNotEmpty()) {
            CategoryCard(
                categoryList,
                color = colors.first()
            )
            ProductListCard(productList)
        }
    }
}

@Composable
private fun ProductListCard(
    list: List<ProductUi>
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
                stringResource(R.string.finance_category_income_all),
                style = text_16,
                color = black_2
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
                )
            }
        }
    }
}


@Composable
private fun CategoryCard(
    categoryList: List<CategoryUi>,
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
                        suffix = Suffix.RUBLE,
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
                Box(
                    modifier = Modifier
                        .background(color = color, shape = CircleShape)
                        .size(12.dp)
                )
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
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(gray_6)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percentFloat)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(color)
                )
            }
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
private fun WarningCard(
    colorBackground: Color,
    colorBorder: Color,
    colorIcon: Color,
    colorIconBackground: Color,
    colorTitle: Color,
    colorText: Color,
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
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            IconTransaction(
                icon = icon,
                color = colorIconBackground,
                colorIcon = colorIcon,
                sizeCard = 32.dp,
                sizeIcon = 16.dp
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
    @StringRes intRes: Int,
    @DrawableRes icon: Int,
    colors: List<Color>,
    suffix: Suffix
) {
    val shape2 = RoundedCornerShape(14.dp)
    val glowColor = blue_11
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                shadowElevation = 20f
                shape = shape2
                clip = false
                ambientShadowColor = glowColor.copy(alpha = 0.8f)
                spotShadowColor = glowColor.copy(alpha = 0.8f)
            }
            .drawBehind {
                val gradient = Brush.linearGradient(
                    colors = colors,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height) // диагональ
                )
                drawRoundRect(
                    brush = gradient,
                    cornerRadius = CornerRadius(14.dp.toPx()) // скругление здесь
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconFinance(
                            icon = icon,
                            color = Color(0x20FFFFFF)
                        )
                        Text(
                            text = stringResource(intRes),
                            style = text_14,
                            color = Color(0x70FFFFFF)
                        )
                    }
                }
                Text(
                    text = "${currentBalance.formatNumber()} ${stringResource(suffix.toResId())}",
                    style = text_36,
                    color = white
                )
            }
            /*  Column(
                  verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                  HorizontalDivider(thickness = 1.dp, color = Color(0x10FFFFFF))
                  Column(
                      verticalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                      Row(
                          modifier = Modifier.fillMaxWidth(),
                          verticalAlignment = Alignment.CenterVertically
                      ) {
                          Text(
                              modifier = Modifier.weight(1f),
                              textAlign = TextAlign.Start,
                              text = stringResource(monthToResString3(month)),
                              style = text_12,
                              color = Color(0x50FFFFFF)
                          )
                          Text(
                              modifier = Modifier.weight(1f),
                              textAlign = TextAlign.Start,
                              text = stringResource(R.string.finance_expenses),
                              style = text_12,
                              color = Color(0x50FFFFFF)
                          )
                      }
                      Row(
                          modifier = Modifier.fillMaxWidth(),
                          verticalAlignment = Alignment.CenterVertically,
                      ) {
                          Text(
                              modifier = Modifier.weight(1f),
                              textAlign = TextAlign.Start,
                              text = (if (positiveMount) "+" else "-") +
                                      currentBalanceMount.formatNumber() +
                                      " ${stringResource(suffix.toResId())}",
                              style = text_12,
                              color = white,
                          )
                          Text(
                              modifier = Modifier.weight(1f),
                              textAlign = TextAlign.Start,
                              text = expensesMount.formatNumber() +
                                      " ${stringResource(suffix.toResId())}",
                              style = text_12,
                              color = red_10,
                          )
                      }
                  }
              }*/
        }
    }
}