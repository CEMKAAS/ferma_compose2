package com.zaroslikov.fermacompose2.ui.finance

import android.widget.ImageButton
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.animal.AnimalTitSuff
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.formatter

object FinanceAnalysisDestination : NavigationDestination {
    override val route = "financeAnalysis"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemProduct"
    val routeWithArgs =
        "$route/{$itemIdArg}/{$itemIdArgTwo}"
}

@Composable
fun FinanceAnalysisProduct(
    navigateBack: () -> Unit,
    viewModel: FinanceAnalysisViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    Scaffold(topBar = {
        TopAppBarStart(
            title = viewModel.name,
            false,
            navigateUp = navigateBack
        )
    }) { innerPadding ->
        FinanceAnalysisContainer(
            modifier = Modifier
                .padding(innerPadding)
                .padding(5.dp)
                .verticalScroll(rememberScrollState()),
            analysisAddAllTime = viewModel.analysisAddAllTime,
            analysisSaleAllTime = viewModel.analysisSaleAllTime,
            analysisWriteOffAllTime = viewModel.analysisWriteOffAllTime,
            analysisWriteOffOwnNeedsAllTime = viewModel.analysisWriteOffOwnNeedsAllTime,
            analysisWriteOffScrapAllTime = viewModel.analysisWriteOffScrapAllTime,
            analysisSaleSoldAllTime = viewModel.analysisSaleSoldAllTime,
            analysisWriteOffOwnNeedsMoneyAllTime = viewModel.analysisWriteOffOwnNeedsMoneyAllTime,
            analysisWriteOffScrapMoneyAllTime = viewModel.analysisWriteOffScrapMoneyAllTime,
            analysisAddAverageValueAllTime = viewModel.analysisAddAverageValueAllTime,
            analysisAddAnimalAllTimeState = viewModel.analysisAddAnimalAllTimeState.collectAsState().value.itemList,
            analysisSaleBuyerAllTimeState = viewModel.analysisSaleBuyerAllTimeState.collectAsState().value.itemList,
        )
    }
}

@Composable
fun FinanceAnalysisContainer(
    modifier: Modifier,
    analysisAddAllTime: FinUiState,
    analysisSaleAllTime: FinUiState,
    analysisWriteOffAllTime: FinUiState,
    analysisWriteOffOwnNeedsAllTime: FinUiState,
    analysisWriteOffScrapAllTime: FinUiState,
    analysisSaleSoldAllTime: Double,
    analysisWriteOffOwnNeedsMoneyAllTime: Double,
    analysisWriteOffScrapMoneyAllTime: Double,
    analysisAddAverageValueAllTime: FinUiState,
    analysisAddAnimalAllTimeState: List<AnimalTitSuff>,
    analysisSaleBuyerAllTimeState: List<AnalysisSaleBuyerAllTime>
) {

    val modifierCard = Modifier
        .fillMaxWidth()
        .padding(8.dp)

    val modifierHeading = Modifier
        .wrapContentSize()
        .padding(6.dp)

    val modifierText = Modifier
        .wrapContentSize()
        .padding(vertical = 3.dp, horizontal = 6.dp)

    Column(modifier = modifier) {
        Card(
            modifier = modifierCard
        ) {
            Text(
                text = "Данные по продукции за все время", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(text = "Получено: ${analysisAddAllTime.priceAll} ${analysisAddAllTime.title}", modifier = modifierText)
            Text(
                text = "В среднем за текущий год: ${analysisAddAverageValueAllTime.priceAll} ${analysisAddAverageValueAllTime.title}",
                modifier = modifierText
            )
            Text(text = "Проданно: ${analysisSaleAllTime.priceAll} ${analysisSaleAllTime.title}", modifier = modifierText)
            Text(text = "Списано : ${analysisWriteOffAllTime.priceAll} ${analysisWriteOffAllTime.title}", modifier = modifierText)
            Text(
                text = "  На собственные нужды: ${analysisWriteOffOwnNeedsAllTime.priceAll} ${analysisWriteOffOwnNeedsAllTime.title}",
                modifier = modifierText
            )
            Text(
                text = "  На утилизацию: ${analysisWriteOffScrapAllTime.priceAll} ${analysisWriteOffScrapAllTime.title}",
                modifier = modifierText
            )
            Text(
                text = "На скаде: ${analysisAddAllTime.priceAll - analysisSaleAllTime.priceAll - analysisWriteOffAllTime.priceAll} ${analysisAddAllTime.title}",
                modifier = modifierText
            )
        }

        Card(
            modifier = modifierCard
        ) {
            Text(
                text = "Данные по финансам за все время", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(text = "Получено прибыли: ${analysisSaleSoldAllTime} ₽", modifier = modifierText)
            Text(
                text = "Сэкономлено: ${analysisWriteOffOwnNeedsMoneyAllTime} ₽",
                modifier = modifierText
            )
            Text(text = "Потери: ${analysisWriteOffScrapMoneyAllTime} ₽", modifier = modifierText)
            Text(
                text = "Итого: ${analysisSaleSoldAllTime + analysisWriteOffOwnNeedsMoneyAllTime - analysisWriteOffScrapMoneyAllTime} ₽",
                modifier = modifierText
            )
        }

        var expanded by remember { mutableStateOf(false) }
        val extraCount = if (expanded) 2 else analysisAddAnimalAllTimeState.size
        val extraPadding by animateDpAsState(
            if (expanded) 2.dp else 0.dp,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        Card(
            modifier = modifierCard.padding(bottom = extraPadding.coerceAtLeast(0.dp))
        ) {
            Text(
                text = "Животные:", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            if (analysisAddAnimalAllTimeState.isNotEmpty()) {
                for (n in 0..extraCount) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "$n) ${analysisAddAnimalAllTimeState[n].Title}",
                                modifier = modifierText.fillMaxWidth(0.6f)
                            )
                            Text(
                                text = "${formatter(analysisAddAnimalAllTimeState[n].priceAll)} ${analysisAddAnimalAllTimeState[n].suffix}",
                                modifier = Modifier.padding(vertical = 3.dp, horizontal = 15.dp),
                                textAlign = TextAlign.End
                            )
                        }
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                contentDescription = "Показать меню"
                            )
                        }
                    }
                }
            } else {
                Text(text = "Пока ничего нет :(", modifier = modifierText)
            }

            var expandedBuyer by remember { mutableStateOf(false) }
            val extraCountBuyer = if (expanded) 2 else analysisAddAnimalAllTimeState.size
            val extraPaddingBuyer by animateDpAsState(
                if (expanded) 2.dp else 0.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

            Card(
                modifier = modifierCard.padding(bottom = extraPaddingBuyer.coerceAtLeast(0.dp))
            ) {
                Text(
                    text = "Покупатели:", modifier = modifierHeading,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                if (analysisSaleBuyerAllTimeState.isNotEmpty()) {
                  for (i in 0..extraCountBuyer ) {
                     Row (verticalAlignment = Alignment.CenterVertically,
                         horizontalArrangement = Arrangement.SpaceBetween,){
                          Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.SpaceBetween,
                              modifier = Modifier
                                  .fillMaxWidth()
                          ) {
                              Text(
                                  text = "$i) ${analysisSaleBuyerAllTimeState[i].buyer} ${
                                      formatter(
                                          analysisSaleBuyerAllTimeState[i].resultPrice
                                      )
                                  }  ₽",
                                  modifier = modifierText.fillMaxWidth(0.6f)
                              )
                              Text(
                                  text = "${analysisSaleBuyerAllTimeState[i].resultCount} ${analysisSaleBuyerAllTimeState[i].suffix}",
                                  modifier = Modifier.padding(vertical = 3.dp, horizontal = 15.dp),
                                  textAlign = TextAlign.End
                              )
                          }
                          IconButton(onClick = { expandedBuyer = !expandedBuyer }) {
                              Icon(
                                  if (expandedBuyer) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                  contentDescription = "Показать меню"
                              )
                          }
                      }
                    }
                } else {
                    Text(text = "Пока ничего нет :(", modifier = modifierText)
                }
            }
        }
    }
}