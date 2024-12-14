package com.zaroslikov.fermacompose2.ui.new_year

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    viewModel: NewYearViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

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
            saleProject = viewModel.saleProject,
            expensesProject = viewModel.expensesProject,
            writeOffOwnNeedsProject = viewModel.writeOffOwnNeedsProject,
            writeOffScrapProject = viewModel.writeOffScrapProject,
            countAnimalProject = viewModel.countAnimalProject,
            bestSale = viewModel.bestSale.collectAsState().value.itemList,
            bestBuyer = viewModel.bestBuyer.collectAsState().value.itemList,
            bestExpenses = viewModel.bestExpenses.collectAsState().value.itemList,
            bestAdd = viewModel.bestAdd.collectAsState().value.itemList,
            countIncubator = viewModel.countIncubator,
            eggInIncubator = viewModel.eggInIncubator,
            chikenInIncubator = viewModel.chikenInIncubator,
            typeIncubator = viewModel.typeIncubator,
            bestProject = viewModel.bestProject,
            boolean = viewModel.itemBoolean
        )
    }
}

@Composable
fun NewYearAnalysisContainer(
    modifier: Modifier,
    saleProject: Double,
    expensesProject: Double,
    writeOffOwnNeedsProject: Double,
    writeOffScrapProject: Double,
    countAnimalProject: Double,
    bestSale: List<AnalysisSaleBuyerAllTime>,
    bestBuyer: List<AnalysisSaleBuyerAllTime>,
    bestExpenses: List<AnalysisSaleBuyerAllTime>,
    bestAdd: List<AnalysisSaleBuyerAllTime>,
    countIncubator: Int,
    eggInIncubator: Int,
    chikenInIncubator: Int,
    typeIncubator: String,
    bestProject: FinUiState,
    boolean: Boolean
) {
    val context = LocalContext.current

    val modifierCard = Modifier
        .fillMaxWidth()
        .padding(8.dp)

    val modifierHeading = Modifier
        .fillMaxWidth()
        .padding(6.dp)

    val modifierText = Modifier
        .fillMaxWidth()
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
                        "Желаем Вам в новом году здоровья, счастья и процветания! Пусть Ваше хозяйство приносит радость и доход! \n" +
                        // составили не большой топ!
                        "Вступайте в нашу группу ВКонтакте, чтобы быть в курсе всех новостей и полезных советов!",
                modifier = modifierText
            )
            TextButton(
                onClick = {
                    AppMetrica.reportEvent("Переход в группу из Нового года")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/myfermaapp"))
                    context.startActivity(intent)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Text(text = "Вступить в группу VK!")
            }

            Text(
                text = "С наилучшими пожеланиями,\n" +
                        "Команда \"Мое хозяйство\"",
                modifier = modifierText,
                textAlign = TextAlign.End
            )
        }

        CardNewYear(
            "Небольшая ферма",
            "На ферме находятся",
            "$countAnimalProject",
            modifierCard,
            modifierText,
            modifierHeading
        )

        PullOutCardNewYear(
            modifierCard = modifierCard,
            modifierHeading = modifierHeading,
            modifierText = modifierText,
            nomination = "Прям как на заводе!",
            note = "Больше всего продукции вы получили",
            list = bestAdd,
            itemToString = {
                "${if (it.buyer == "") "Не указано " else it.buyer} " +
                        "${formatter(it.resultCount)} ${it.suffix}"
            }
        )

        PullOutCardNewYear(
            modifierCard = modifierCard,
            modifierHeading = modifierHeading,
            modifierText = modifierText,
            nomination = "Без этого никак!",
            note = "Эти товары вы покупали чаще всего",
            list = bestExpenses,
            itemToString = {
                "${if (it.buyer == "") "Не указано " else it.buyer} " +
                        "${formatter(it.resultPrice)} ₽ за  ${formatter(it.resultCount)} ${it.suffix}"
            }
        )

        PullOutCardNewYear(
            modifierCard = modifierCard,
            modifierHeading = modifierHeading,
            modifierText = modifierText,
            nomination = "Лучший клиент",
            note = "Это ваши преданные клиенты, берегите их!",
            list = bestBuyer,
            itemToString = {
                "${if (it.buyer == "") "Не указано " else it.buyer} " +
                        "${formatter(it.resultPrice)} ₽"
            }
        )

        PullOutCardNewYear(
            modifierCard = modifierCard,
            modifierHeading = modifierHeading,
            modifierText = modifierText,
            nomination = "Хит Продаж!",
            note = "Это Ваш самымый популярный товар!",
            list = bestSale,
            itemToString = {
                "${if (it.buyer == "") "Не указано " else it.buyer} " +
                        "${formatter(it.resultPrice)} ₽ за  ${formatter(it.resultCount)} ${it.suffix}"
            }
        )

        if (!boolean) {
            CardNewYear(
                "Инкубатор!",
                "Вы запутили столько инкубаторов",
                "$countIncubator",
                modifierCard,
                modifierText,
                modifierHeading
            )

            CardNewYear(
                "Вкладываю в инкубатор, а в не крипту!",
                "Вы вложили в инкубатор",
                "$eggInIncubator яйц",
                modifierCard,
                modifierText,
                modifierHeading
            )

            CardNewYear(
                "Мать-наседка!",
                "Вы выростели",
                "$chikenInIncubator птенцов - это ${chikenInIncubator * 100 / eggInIncubator}%!",
                modifierCard,
                modifierText,
                modifierHeading
            )

            CardNewYear(
                "Итого!",
                "Чаще всего вы инкубировали яйца",
                "$typeIncubator",
                modifierCard,
                modifierText,
                modifierHeading
            )
        }

        CardNewYear(
            "Вот это прибыль!",
            "Ваша прибыль",
            "$saleProject",
            modifierCard,
            modifierText,
            modifierHeading
        )

        CardNewYear(
            "Расходы это не плохо",
            "Ваши расходы",
            "$expensesProject",
            modifierCard,
            modifierText,
            modifierHeading
        )

        CardNewYear(
            "Экономия превыше всего!",
            "Вы сэкономили!",
            "$writeOffOwnNeedsProject",
            modifierCard,
            modifierText,
            modifierHeading
        )

        CardNewYear(
            "Потеря потерь!",
            "Ваши потери",
            "$writeOffScrapProject",
            modifierCard,
            modifierText,
            modifierHeading
        )

        CardNewYear(
            "Итого!",
            "Вы супер, продолжайте в том же духе!",
            "${saleProject + writeOffOwnNeedsProject - expensesProject - writeOffScrapProject}",
            modifierCard,
            modifierText,
            modifierHeading
        )

    }
}

@Composable
fun CardNewYear(
    nomination: String,
    note: String,
    value: String,
    modifierCard: Modifier,
    modifierText: Modifier,
    modifierHeading: Modifier
) {
    Card(
        modifier = modifierCard
    ) {
        Text(
            text = nomination, modifier = modifierHeading,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
        Text(
            text = note,
            modifier = modifierText,
            textAlign = TextAlign.Center,
        )
        Text(
            text = value,
            modifier = modifierText,
            textAlign = TextAlign.Center,
        )
    }
}


@Composable
fun <T> PullOutCardNewYear(
    nomination: String,
    note: String,
    list: List<T>,
    itemToString: (T) -> String,
    modifierCard: Modifier,
    modifierHeading: Modifier,
    modifierText: Modifier,
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
                    text = "$nomination:", modifier = modifierHeading,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
                Text(
                    text = "$note:", modifier = modifierHeading,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )

                if (list.isNotEmpty()) {
                    for (i in list.indices) {
                        Text(
                            text = "${i + 1}) ${itemToString(list[i])}",
                            modifier = modifierText.fillMaxWidth(0.8f),
                            textAlign = TextAlign.Center,
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
