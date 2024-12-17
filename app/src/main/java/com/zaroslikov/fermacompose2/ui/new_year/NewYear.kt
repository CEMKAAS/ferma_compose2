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
    countAnimalProject: Int,
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

    val total = saleProject + writeOffOwnNeedsProject - expensesProject - writeOffScrapProject

    Column(modifier = modifier) {


        Card(
            modifier = modifierCard
        ) {
            Text(
                text = "С Наступающим Новым Годом!", modifier = modifierHeading,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
            Text(
                text = "Догорой друг!\nМы благодарны Вам за то, что провели этот год вместе с нами. Ваша поддержка и доверие вдохновляют нашу команду на новые свершения.\n" +
                        "Желаем Вам в новом году здоровья, счастья и процветания! Пусть Ваше хозяйство приносит радость и доход!\n" +
                        "Вступайте в нашу группу ВКонтакте, чтобы быть в курсе всех новостей и полезных советов!",
                modifier = modifierText
            )
            TextButton(
                onClick = {
                    AppMetrica.reportEvent("Переход в группу из Нового года")
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/myfermaapp"))
                    context.startActivity(intent)
                }, modifier = Modifier.fillMaxWidth()
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

        Card(
            modifier = modifierCard
        ) {
            Text(
                text = if (boolean) "Представляем Вам небольшую подборку ключевых событий и достижений,  связанных с Вашим проектом, за прошедший год!"
                else "Представляем Вам небольшую подборку ключевых событий и достижений,  связанных со всеми Вашими проектами, за прошедший год!",
                textAlign = TextAlign.Center,
                modifier = modifierText
            )
        }

        if (countAnimalProject != 0) {
            CardNewYear(
                if (countAnimalProject > 10) "Звериный магнат" else "Ферма в миниатюре",
                "Животных на Вашей ферме",
                "$countAnimalProject",
                modifierCard,
                modifierText,
                modifierHeading
            )
        }

        PullOutCardNewYear(
            modifierCard = modifierCard,
            modifierHeading = modifierText,
            modifierText = modifierText,
            nomination = "Сельскохозяйственный гигант",
            note = "Было произведено",
            list = bestAdd,
            itemToString = {
                "${if (it.buyer == "") "Не указано " else it.buyer} " +
                        "${formatter(it.resultPrice)} ${it.suffix}"
            }
        )

        PullOutCardNewYear(
            modifierCard = modifierCard,
            modifierHeading = modifierText,
            modifierText = modifierText,
            nomination = "Грандиозный вклад",
            note = "Значительные финансовые вклады в развитие своего ЛПХ",
            list = bestExpenses,
            itemToString = {
                "${if (it.buyer == "") "Не указано " else it.buyer} " +
                        "${formatter(it.resultPrice)} ₽ за  ${formatter(it.resultCount)} ${it.suffix}"
            }
        )

        PullOutCardNewYear(
            modifierCard = modifierCard,
            modifierHeading = modifierText,
            modifierText = modifierText,
            nomination = "Лучший клиент",
            note = "Преданные клиенты, берегите их!",
            list = bestBuyer,
            itemToString = {
                "${if (it.buyer == "") "Не указано " else it.buyer} " +
                        "${formatter(it.resultPrice)} ₽"
            }
        )

        PullOutCardNewYear(
            modifierCard = modifierCard,
            modifierHeading = modifierText,
            modifierText = modifierText,
            nomination = "Хит Продаж!",
            note = "Самые продаваемые товары!",
            list = bestSale,
            itemToString = {
                "${if (it.buyer == "") "Не указано " else it.buyer} " +
                        "${formatter(it.resultPrice)} ₽ за  ${formatter(it.resultCount)} ${it.suffix}"
            }
        )

        if (!boolean) {
            if (countIncubator != 0) {
                CardNewYear(
                    if (countIncubator > 3) "Инкубаторный гуру " else "Птичий старт-ап",
                    "Вы запустили столько инкубаторов:",
                    "$countIncubator шт.",
                    modifierCard,
                    modifierText,
                    modifierHeading
                )
            }

            if (eggInIncubator != 0) {
                CardNewYear(
                    "Вкладываю в инкубатор, а не в крипту!",
                    "Вы вложили в инкубатор:",
                    "$eggInIncubator яйц",
                    modifierCard,
                    modifierText,
                    modifierHeading
                )
            }
            if (chikenInIncubator != 0) {
                CardNewYear(
                    "Мать-наседка!",
                    "У Вас вылупилось:",
                    "$chikenInIncubator птенцов- это ${chikenInIncubator * 100 / eggInIncubator}%",
                    modifierCard,
                    modifierText,
                    modifierHeading
                )
            }
            if (typeIncubator == "") {
                CardNewYear(
                    "Мастер узкого профиля",
                    "Вы специализируетесь на определенном типе птиц:",
                    typeIncubator,
                    modifierCard,
                    modifierText,
                    modifierHeading
                )
            }
        }

        CardNewYear(
            "Вот это прибыль!",
            "Вы заработали:",
            "${formatter(saleProject)} ₽",
            modifierCard,
            modifierText,
            modifierHeading
        )

        CardNewYear(
            "Расходы это не плохо!",
            "Ваши расходы составили:",
            "${formatter(expensesProject)} ₽",
            modifierCard,
            modifierText,
            modifierHeading
        )

        CardNewYear(
            "Экономия превыше всего!",
            "Вы сэкономили:",
            "${formatter(writeOffOwnNeedsProject)} ₽",
            modifierCard,
            modifierText,
            modifierHeading
        )

        CardNewYear(
            "Потеря потерь!",
            "Ваши потери составили:",
            "${formatter(writeOffScrapProject)} ₽",
            modifierCard,
            modifierText,
            modifierHeading
        )

        CardNewYear(
            if (total > 0) "Агропредприниматель года!" else "На пути к успеху",
            if (total > 0) "Вы супер, продолжайте в том же духе! Ваша рентабельность соcтавила:"
            else "Не все сезоны бывают удачными, но важно видеть возможности для роста и улучшения! Ваша рентабельность составила:",
            "${formatter(total)} ₽",
            modifierCard,
            modifierText,
            modifierHeading
        )

//        CardNewYear(
//            "Лучший Проект!",
//            "${bestProject.title} - ${formatter(bestProject.priceAll)}₽",
//            "Лучше и быть не может!",
//            modifierCard,
//            modifierText,
//            modifierHeading
//        )

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
    if (list.isNotEmpty()) {
        Card(
            modifier = modifierCard
        ) {
            Column {
                Text(
                    text = nomination, modifier = modifierHeading,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
                Text(
                    text = note, modifier = modifierHeading,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )

                list.forEachIndexed { index, it ->
                    Text(
                        text = "${index + 1}. ${itemToString(it)}",
                        modifier = modifierText.fillMaxWidth(0.8f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

