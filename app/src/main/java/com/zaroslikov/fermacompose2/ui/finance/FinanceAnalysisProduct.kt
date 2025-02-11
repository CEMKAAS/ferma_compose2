package com.zaroslikov.fermacompose2.ui.finance

import android.widget.ImageButton
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ScopeUpdateScope
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarCalendar
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.animal.AnimalTitSuff
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.add.incubator.AlertDialogExample
import com.zaroslikov.fermacompose2.ui.start.AlertDialogInfo
import com.zaroslikov.fermacompose2.ui.start.formatter
import io.appmetrica.analytics.AppMetrica
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone


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
    var openCalendarDialog by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("все время") }

    if (openCalendarDialog) {
        DateRangePickerModal(
            onDateRangeSelected = {
                Pair(it.first?.let { it1 -> viewModel.updateDateBegin(it1) },
                    it.second?.let { it1 -> viewModel.updateDateEnd(it1) })
            },
            onDismiss = { openCalendarDialog = false },
            dateBegin = viewModel.dateBegin,
            dateEnd = viewModel.dateEnd,
            upAnalisis = {
                viewModel.upAnalisis()
                val format = SimpleDateFormat("dd.MM.yyyy")
                text = "${format.format(viewModel.dateBegin)} - ${
                        format.format(viewModel.dateEnd)}"
            }
        )
    }

    Scaffold(topBar = {
        TopAppBarCalendar(
            title = viewModel.name,
            true,
            navigateUp = navigateBack,
            settingUp = { openCalendarDialog = true
                AppMetrica.reportEvent("Диапазон Анализ")
            }
        )
    }) { innerPadding ->
        FinanceAnalysisContainer(
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
fun FinanceAnalysisContainer(
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
    analysisCostPriceAllTimeState: List<Fin>
) {

    var openAlertDialog by remember { mutableStateOf(false) }
    var openAlertDialogScrap by remember { mutableStateOf(false) }

    if (openAlertDialog) {
        AlertDialogInfo(
            onConfirmation = { openAlertDialog = false },
            dialogTitle = "Что такое \"Сэкономлено\"?",
            dialogText = "Сэкономлено - это сумма, которую вы сберегли, используя свой товар для личных нужд, вместо того чтобы покупать его в магазине. Указывается в разделе \"Мои Списания\" -> \"На собственные нужды\"",
        )
    }

    if (openAlertDialogScrap) {
        AlertDialogInfo(
            onConfirmation = { openAlertDialogScrap = false },
            dialogTitle = "Что такое \"Потеряно\"?",
            dialogText = "Потеряно - это сумма денег за товар, которую вы потеряли, по каким либо причинам. Указывается в разделе \"Мои Списания\" -> \"На утилизацию\"",
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
                text = "Данные по продукции за $text:", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = "Получено: ${formatter(analysisAddAllTime.priceAll)} ${analysisAddAllTime.title}",
                modifier = modifierText
            )
//            Text(
//                text = "В среднем за текущий год: ${formatter(analysisAddAverageValueAllTime.priceAll)} ${analysisAddAverageValueAllTime.title} за день",
//                modifier = modifierText
//            )
            Text(
                text = "Проданно: ${formatter(analysisSaleAllTime.priceAll)} ${analysisSaleAllTime.title}",
                modifier = modifierText
            )
            Text(
                text = "Списано : ${formatter(analysisWriteOffAllTime.priceAll)} ${analysisWriteOffAllTime.title}",
                modifier = modifierText
            )
            Text(
                text = "  На собственные нужды: ${formatter(analysisWriteOffOwnNeedsAllTime.priceAll)} ${analysisWriteOffOwnNeedsAllTime.title}",
                modifier = modifierText
            )
            Text(
                text = "  На утилизацию: ${formatter(analysisWriteOffScrapAllTime.priceAll)} ${analysisWriteOffScrapAllTime.title}",
                modifier = modifierText
            )
            if (text == "все время") {
                Text(
                    text = "На скаде: ${formatter(analysisAddAllTime.priceAll - analysisSaleAllTime.priceAll - analysisWriteOffAllTime.priceAll)} ${analysisAddAllTime.title}",
                    modifier = modifierText
                )
            }
        }

        Card(
            modifier = modifierCard
        ) {
            Text(
                text = "Данные по финансам за $text:", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = "Получено прибыли: ${formatter(analysisSaleSoldAllTime)} ₽",
                modifier = modifierText
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { openAlertDialog = !openAlertDialog }) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Показать меню"
                    )
                }
                Text(
                    text = "Сэкономлено: ${formatter(analysisWriteOffOwnNeedsMoneyAllTime)} ₽",
                    modifier = modifierText
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { openAlertDialogScrap = !openAlertDialogScrap }) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Показать меню"
                    )
                }
                Text(
                    text = "Потери: ${formatter(analysisWriteOffScrapMoneyAllTime)} ₽",
                    modifier = modifierText
                )
            }
            Text(
                text = "Итого: ${formatter(analysisSaleSoldAllTime + analysisWriteOffOwnNeedsMoneyAllTime - analysisWriteOffScrapMoneyAllTime)} ₽",
                modifier = modifierText
            )
        }

        PullOutCard(
            modifierCard = modifierCard,
            modifierHeading = modifierHeading,
            modifierText = modifierText,
            name = "Продукция от животных",
            list = analysisAddAnimalAllTimeState,
            dialogTitle = "Что такое \"Продукция от животных\"?",
            dialogText = "Этот блок показывает, сколько продукции произвело каждое животное. Данные появляются при добавлении животного при внесении продукции в \"Моя Продукция\"."
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
            name = "Покупатели",
            list = analysisSaleBuyerAllTimeState,
            dialogTitle = "Что такое \"Покупатели\"?",
            dialogText = "Этот блок отображает, кто купил вашу продукцию, сколько было приобретено и на какую сумму за выбранный период. Данные появляются, если в разделе \"Мои Продажи\" выбрать данный товар и указать покупателя. "
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
            name = "Себестоимость в расчете за 1 единицу",
            list = analysisCostPriceAllTimeState,
            dialogTitle = "Что такое \"Себестоимость в расчете за 1 единицу\"?",
            dialogText = "Этот блок показывает, сколько затрат было произведено для получения 1 единицы товара. Расчет выполняется через животное: в разделе \"Мои Покупки\" укажите товар, выберите \"Корм\" или \"Распределить расходы по животным\" и назначьте животное, которое принесет данный товар. Затем в разделе \"Моя Продукция\" укажите данный товар и выберите это животное, чтобы в дальнейшем увидеть себестоимость продукции с учетом затрат."
        ) { analysisCostPriceAllTimeState ->
            "${if (analysisCostPriceAllTimeState.title == "") "Не указано " else "От ${analysisCostPriceAllTimeState.title}"} ${
                formatter(
                    analysisCostPriceAllTimeState.priceAll
                )
            } ₽"
        }
    }
}

@Composable
fun <T> PullOutCard(
    modifierCard: Modifier,
    modifierHeading: Modifier,
    modifierText: Modifier,
    name: String,
    dialogTitle:String,
    dialogText:String,
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
    var openAlertDialog by remember { mutableStateOf(false) }

    if (openAlertDialog) {
        AlertDialogInfo(
            onConfirmation = { openAlertDialog = false },
            dialogTitle = dialogTitle,
            dialogText = dialogText
        )
    }

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
                Row (verticalAlignment = Alignment.CenterVertically){
                    IconButton(onClick = { openAlertDialog =! openAlertDialog }) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Показать меню"
                        )
                    }
                Text(
                    text = "$name:", modifier = modifierHeading,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit,
    upAnalisis: () -> Unit,
    dateBegin: Long,
    dateEnd: Long
) {
    val dateRangePickerState = rememberDateRangePickerState(
        selectableDates = PastOrPresentSelectableDates,
        initialSelectedStartDateMillis = dateBegin,
        initialSelectedEndDateMillis = dateEnd
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    upAnalisis()
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Назад")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Выберите период"
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}
