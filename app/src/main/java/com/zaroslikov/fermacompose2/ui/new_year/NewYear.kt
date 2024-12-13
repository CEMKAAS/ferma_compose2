package com.zaroslikov.fermacompose2.ui.new_year

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarCalendar
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.animal.AnimalTitSuff
import com.zaroslikov.fermacompose2.ui.finance.AnalysisSaleBuyerAllTime
import com.zaroslikov.fermacompose2.ui.finance.FinUiState
import com.zaroslikov.fermacompose2.ui.finance.FinanceAnalysisViewModel
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.start.add.incubator.AlertDialogExample
import com.zaroslikov.fermacompose2.ui.start.formatter
import io.appmetrica.analytics.AppMetrica
import java.text.SimpleDateFormat

object NewYearDestination : NavigationDestination {
    override val route = "NewYearAnalysis"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemProduct"
    val routeWithArgs =
        "$route/{$itemIdArg}/{$itemIdArgTwo}"
}

@Composable
fun NewYearAnalysis(
    navigateBack: () -> Unit,
    viewModel: FinanceAnalysisViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    var text by remember { mutableStateOf("все время") }

    Scaffold(topBar = {
        TopAppBarCalendar(
            title = "Итоги года!",
            false,
            navigateUp = navigateBack,
        )
    }) { innerPadding ->
        NewYearAnalysisContainer(
            modifier = Modifier
                .padding(innerPadding)
                .padding(5.dp)
                .verticalScroll(rememberScrollState()),
            text = text,
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
            analysisCostPriceAllTimeState = viewModel.analysisCostPriceTimeState.collectAsState().value.itemList,
        )
    }
}

@Composable
fun NewYearAnalysisContainer(
    modifier: Modifier,
    text: String,
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
    analysisSaleBuyerAllTimeState: List<AnalysisSaleBuyerAllTime>,
    analysisCostPriceAllTimeState: List<AnimalTitSuff>
) {

    var openAlertDialog by remember { mutableStateOf(false) }
    var openAlertDialogScrap by remember { mutableStateOf(false) }

    if (openAlertDialog) {
        AlertDialogExample(
            onDismissRequest = { openAlertDialog = false },
            onConfirmation = {
                openAlertDialog = false
                println("Confirmation registered") // Add logic here to handle confirmation.
            },
            dialogTitle = "Сэкономлено",
            dialogText = "Сэкономлено - это сумма, которую вы сберегли, используя свой товар для личных нужд, вместо того чтобы покупать его в магазине.",
            icon = Icons.Default.Info
        )
    }

    if (openAlertDialogScrap) {
        AlertDialogExample(
            onDismissRequest = { openAlertDialogScrap = false },
            onConfirmation = {
                openAlertDialogScrap = false
                println("Confirmation registered") // Add logic here to handle confirmation.
            },
            dialogTitle = "Потеряно",
            dialogText = "Потеряно - это сумма денег за товар, которую вы потеряли, по каким либо причинам.",
            icon = Icons.Default.Info
        )
    }

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
                text = "С Наступающим Новым Годом!", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = "Догорой друг! Мы благодарны Вам за то, что провели этот год вместе с нами. Ваша поддержка и доверие вдохновляют нашу команду на новые свершения.\n" +
                        "\n" +
                        "Желаем Вам в новом году здоровья, счастья и процветания! Пусть Ваше хозяйство приносит радость и доход! \n" +
                        "Не забудьте вступить в нашу группу ВКонтакте, чтобы быть в курсе всех новостей и полезных советов!\n" +
                        "\n" +
                        "С наилучшими пожеланиями,  \n" +
                        "Команда \"Мое хозяйство\"",
                modifier = modifierText
            )
        }

        Card(
            modifier = modifierCard
        ) {
            Text(
                text = "В этом году Вы:", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = "Получили: ${formatter(analysisAddAllTime.priceAll)} ${analysisAddAllTime.title}",
                modifier = modifierText
            )
            Text(
                text = "Потратили: ${formatter(analysisSaleAllTime.priceAll)} ${analysisSaleAllTime.title}",
                modifier = modifierText
            )
            Text(
                text = "Сэкономили: ${formatter(analysisWriteOffOwnNeedsAllTime.priceAll)} ${analysisWriteOffOwnNeedsAllTime.title}",
                modifier = modifierText
            )
            Text(
                text = "Потеряли: ${formatter(analysisWriteOffScrapAllTime.priceAll)} ${analysisWriteOffScrapAllTime.title}",
                modifier = modifierText
            )
            Text(
                text = "Итого: ${formatter(analysisAddAllTime.priceAll - analysisSaleAllTime.priceAll - analysisWriteOffAllTime.priceAll)} ${analysisAddAllTime.title}",
                modifier = modifierText
            )
        }

        Card(
            modifier = modifierCard
        ) {
            Text(
                text = "Животные:", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = "В Вашем Хозяйстве: ${formatter(analysisAddAllTime.priceAll)} ${analysisAddAllTime.title}",
                modifier = modifierText
            )
            Text(
                text = "Популярный тип: ${formatter(analysisSaleAllTime.priceAll)} ${analysisSaleAllTime.title}",
                modifier = modifierText
            )
        }


        PullOutCard(
            modifierCard = modifierCard,
            modifierHeading = modifierHeading,
            modifierText = modifierText,
            name = "Лучшие Животные:",
            list = analysisAddAnimalAllTimeState
        ) { analysisAddAnimalAllTimeState ->
            "${if (analysisAddAnimalAllTimeState.title == "") "Не указано " else analysisAddAnimalAllTimeState.title} ${
                formatter(
                    analysisAddAnimalAllTimeState.priceAll
                )
            } ${analysisAddAnimalAllTimeState.suffix}"
        }

        PullOutCard(
            modifierCard = modifierCard,
            modifierHeading = modifierHeading,
            modifierText = modifierText,
            name = "Лучшие покупатели:",
            list = analysisSaleBuyerAllTimeState
        ) { analysisSaleBuyerAllTimeState ->
            "${if (analysisSaleBuyerAllTimeState.buyer == "") "Не указано " else analysisSaleBuyerAllTimeState.buyer} ${
                formatter(
                    analysisSaleBuyerAllTimeState.resultPrice
                )
            } ₽ за ${formatter(analysisSaleBuyerAllTimeState.resultCount)} ${analysisSaleBuyerAllTimeState.suffix}"
        }

        PullOutCard(
            modifierCard = modifierCard,
            modifierHeading = modifierHeading,
            modifierText = modifierText,
            name = "Продаваемый товар:",
            list = analysisCostPriceAllTimeState
        ) { analysisCostPriceAllTimeState ->
            "${if (analysisCostPriceAllTimeState.title == "") "Не указано " else "От ${analysisCostPriceAllTimeState.title}"} ${
                formatter(
                    analysisCostPriceAllTimeState.priceAll
                )
            } ${analysisCostPriceAllTimeState.suffix}"
        }
        PullOutCard(
            modifierCard = modifierCard,
            modifierHeading = modifierHeading,
            modifierText = modifierText,
            name = "Без этого никак:",
            list = analysisCostPriceAllTimeState
        ) { analysisCostPriceAllTimeState ->
            "${if (analysisCostPriceAllTimeState.title == "") "Не указано " else "От ${analysisCostPriceAllTimeState.title}"} ${
                formatter(
                    analysisCostPriceAllTimeState.priceAll
                )
            } ${analysisCostPriceAllTimeState.suffix}"
        }
    }
}

@Composable
fun <T> PullOutCard(
    modifierCard: Modifier,
    modifierHeading: Modifier,
    modifierText: Modifier,
    name: String,
    list: List<T>,
    itemToString: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }
    val extraPadding by animateDpAsState(
        if (expanded) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )

    Card(
        modifier = modifierCard.padding(bottom = extraPadding.coerceAtLeast(0.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "$name:", modifier = modifierHeading,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                if (list.isNotEmpty()) {
                    for (i in list.indices) {
                        Text(
                            text = "${i + 1}) ${itemToString(list[i])}",
                            modifier = modifierText.fillMaxWidth(0.8f)
                        )
                        if (i == 2 && !expanded)
                            break
                    }
                } else {
                    Text(text = "Пока ничего нет :(", modifier = modifierText)
                }
            }

            if (list.size > 3) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Показать меню"
                    )
                }
            }
        }
    }
}
