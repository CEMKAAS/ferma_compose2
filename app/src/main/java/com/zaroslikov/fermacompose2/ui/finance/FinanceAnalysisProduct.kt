package com.zaroslikov.fermacompose2.ui.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
    val routeWithArgs = "$route/{$itemIdArg}"
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
        analysisSaleBuyerAllTimeState = viewModel.analysisSaleBuyerAllTimeState.collectAsState().value.itemList
        )
    }
}

@Composable
fun FinanceAnalysisContainer(
    modifier: Modifier,
    analysisAddAllTime:Double,
    analysisSaleAllTime:Double,
    analysisWriteOffAllTime: Double,
    analysisWriteOffOwnNeedsAllTime:Double,
    analysisWriteOffScrapAllTime:Double,
    analysisSaleSoldAllTime:Double,
    analysisWriteOffOwnNeedsMoneyAllTime:Double,
    analysisWriteOffScrapMoneyAllTime:Double,
    analysisAddAverageValueAllTime:Double,
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
            Text(text = "Получено: ${animalTable.type}", modifier = modifierText)
            Text(text = "В среднем за текущий год: ${animalTable.data}", modifier = modifierText)
            Text(text = "Проданно: ${animalTable.data}", modifier = modifierText)
            Text(text = "Списано : ${animalTable.data}", modifier = modifierText)
            Text(text = "  На собственные нужды: ${animalTable.data}", modifier = modifierText)
            Text(text = "  На утилизацию: ${animalTable.data}", modifier = modifierText)
            Text(text = "На скаде: ${animalTable.data}", modifier = modifierText)
        }

        Card(
            modifier = modifierCard
        ) {
            Text(
                text = "Данные по финансам за все время", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(text = "Получено прибыли: ${animalTable.data}", modifier = modifierText)
            Text(text = "Сэкономлено: ${animalTable.data}", modifier = modifierText)
            Text(text = "Потери: ${animalTable.data}", modifier = modifierText)
            Text(text = "Итого: ${animalTable.data}", modifier = modifierText)
        }

        Card(
            modifier = modifierCard
        ) {
            Text(
                text = "Животные:", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            var i = 1
            if (animalProductTable.isNotEmpty()) {
                animalProductTable.forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "${i++}) ${it.Title}",
                            modifier = modifierText.fillMaxWidth(0.6f)
                        )
                        Text(
                            text = "${formatter(it.priceAll)} ${it.suffix}",
                            modifier = Modifier.padding(vertical = 3.dp, horizontal = 15.dp),
                            textAlign = TextAlign.End
                        )
                    }
                }
            } else {
                Text(text = "Пока ничего нет :(", modifier = modifierText)
            }

            Card(
                modifier = modifierCard
            ) {
                Text(
                    text = "Покупатели:", modifier = modifierHeading,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                var i = 1
                if (animalProductTable.isNotEmpty()) {
                    animalProductTable.forEach {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "${i++}) ${it.Title}",
                                modifier = modifierText.fillMaxWidth(0.6f)
                            )
                            Text(
                                text = "${formatter(it.priceAll)} ${it.suffix}",
                                modifier = Modifier.padding(vertical = 3.dp, horizontal = 15.dp),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                } else {
                    Text(text = "Пока ничего нет :(", modifier = modifierText)
                }


        }

    }

}