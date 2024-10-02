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
import com.zaroslikov.fermacompose2.ui.start.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.start.add.incubator.AlertDialogExample
import com.zaroslikov.fermacompose2.ui.start.formatter
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
            settingUp = { openCalendarDialog = true }
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
    analysisSaleBuyerAllTimeState: List<AnalysisSaleBuyerAllTime>
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
                text = "Данные по продукции за $text:", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = "Получено: ${formatter(analysisAddAllTime.priceAll)} ${analysisAddAllTime.title}",
                modifier = modifierText
            )
            Text(
                text = "В среднем за текущий год: ${formatter(analysisAddAverageValueAllTime.priceAll)} ${analysisAddAverageValueAllTime.title} за день",
                modifier = modifierText
            )
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
            name = "Животные",
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
            name = "Покупатели",
            list = analysisSaleBuyerAllTimeState
        ) { analysisSaleBuyerAllTimeState ->
            "${if (analysisSaleBuyerAllTimeState.buyer == "") "Не указано " else analysisSaleBuyerAllTimeState.buyer} ${
                formatter(
                    analysisSaleBuyerAllTimeState.resultPrice
                )
            } ₽ за ${formatter(analysisSaleBuyerAllTimeState.resultCount)} ${analysisSaleBuyerAllTimeState.suffix}"
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
                            modifier = modifierText.fillMaxWidth(0.6f)
                        )
                        if (i == 2 && !expanded)
                            break
                    }
                } else {
                    Text(text = "Пока ничего нет :(", modifier = modifierText)
                }
            }

            if (list.size >= 3) {
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
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
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
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select date range"
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


//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MonthYearPicker(
//    selectedMonth: String,
//    selectedYear: String,
//    onMonthSelected: (Int) -> Unit,
//    onYearSelected: (Int) -> Unit,
//    currentYear: Int = 2024,
//    yearRange: IntRange = (2020..currentYear),
//    onDismissRequest: () -> Unit,
//) {
//    var expanded by remember { mutableStateOf(false) }
//    var expandedYear by remember { mutableStateOf(false) }
//
//    val months = listOf(
//        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
//        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь", "За весь год"
//    )
//
//    Dialog(onDismissRequest = { onDismissRequest() }) {
//        // Draw a rectangle shape with rounded corners inside the dialog
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(375.dp)
//                .padding(16.dp),
//            shape = RoundedCornerShape(16.dp),
//        ) {
//
//
//            Column {
//
//                Text(text = selectedMonth + selectedYear)
//
//                // Month Picker
//                ExposedDropdownMenuBox(
//                    expanded = expanded,
//                    onExpandedChange = { expanded = !expanded }
//                ) {
//                    TextField(
//                        value = selectedMonth,
//                        onValueChange = {},
//                        readOnly = true,
//                        label = { Text("Выберети месяц") },
//                        trailingIcon = {
//                            Icon(Icons.Filled.ArrowDropDown, null)
//                        },
//                        modifier = Modifier
//                            .menuAnchor()
//                            .fillMaxWidth()
//                            .padding(bottom = 10.dp),
//                    )
//                    ExposedDropdownMenu(
//                        expanded = expanded,
//                        onDismissRequest = {
//                            expanded = false
//                        }
//                    ) {
//                        months.forEach { month ->
//                            DropdownMenuItem(
//                                text = { Text(text = month) },
//                                onClick = {
//                                    onMonthSelected(SetMountRevers(month))
//                                    expanded = false
//                                }
//                            )
//                        }
//                    }
//                }
//
//                ExposedDropdownMenuBox(
//                    expanded = expandedYear,
//                    onExpandedChange = { expandedYear = !expandedYear }
//                ) {
//                    TextField(
//                        value = selectedYear,
//                        onValueChange = {},
//                        readOnly = true,
//                        label = { Text("Выберети год") },
//                        trailingIcon = {
//                            Icon(Icons.Filled.ArrowDropDown, null)
//                        }
//                    )
//                    ExposedDropdownMenu(
//                        expanded = expanded,
//                        onDismissRequest = { expanded = false }
//                    ) {
//                        yearRange.toList().forEach { year ->
//                            DropdownMenuItem(
//                                text = { Text(text = year.toString()) },
//                                onClick = {
//                                    onYearSelected(year)
//                                    expandedYear = false
//                                }
//                            )
//                        }
//                    }
//                }
//
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Center,
//                ) {
//                    TextButton(
//                        onClick = { onDismissRequest() },
//                        modifier = Modifier.padding(8.dp),
//                    ) {
//                        Text("Отмена")
//                    }
//                    TextButton(
//                        onClick = { },
//                        modifier = Modifier.padding(8.dp),
//                    ) {
//                        Text("Принять")
//                    }
//                }
//
//            }
//        }
//    }
//}
//
//fun SetMount(mount: Int): String {
//    return when (mount) {
//        1 -> "Январь"
//        2 -> "Февраль"
//        3 -> "Март"
//        4 -> "Апрель"
//        5 -> "Май"
//        6 -> "Июнь"
//        7 -> "Июль"
//        8 -> "Август"
//        9 -> "Сентябрь"
//        10 -> "Октябрь"
//        11 -> "Ноябрь"
//        12 -> "Декабрь"
//        else -> "За весь год"
//    }
//}
//
//fun SetMountRevers(mount: String): Int {
//    return when (mount) {
//        "Январь" -> 1
//        "Февраль" -> 2
//        "Март" -> 3
//        "Апрель" -> 4
//        "Май" -> 5
//        "Июнь" -> 6
//        "Июль" -> 7
//        "Август" -> 8
//        "Сентябрь" -> 9
//        "Октябрь" -> 10
//        "Ноябрь" -> 11
//        "Декабрь" -> 12
//        else -> 0
//
//    }
//}


//
//@Composable
//fun PullOutCardText(it: AnimalTitSuff){
//    Text(
//        text = "${i++}) ${it.title} ${formatter(it.priceAll)} ${it.suffix}",
//        modifier = modifierText.fillMaxWidth(0.6f)
//    )
//}
//@Composable
//fun PullOutCardText(it:AnalysisSaleBuyerAllTime){
//    Text(
//        text = "${i++}) ${it.title} ${formatter(it.priceAll)} ${it.suffix}",
//        modifier = modifierText.fillMaxWidth(0.6f)
//    )
//}
