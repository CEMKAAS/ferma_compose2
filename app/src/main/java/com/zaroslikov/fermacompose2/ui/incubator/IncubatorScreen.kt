package com.zaroslikov.fermacompose2.ui.incubator

import android.annotation.SuppressLint
import android.os.Parcelable
import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.incubator.IncubatorList
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit


object IncubatorScreenDestination : NavigationDestination {
    override val route = "IncubatorScreen"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun IncubatorScreen(
    navigateBack: () -> Unit,
    navigateDayEdit: (IncubatorEditNav) -> Unit,
    navigateProjectEdit: (Int) -> Unit,
    viewModel: IncubatorViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val temp = viewModel.tempStateList
    val damp = viewModel.dampStateList
    val over = viewModel.overStateList
    val airng = viewModel.airingStateList
    val project = viewModel.projectState

    val tempList = massList(temp)
    val dampList = massList(damp)
    val overList = massList(over)
    val airingList = massList(airng)


    Scaffold(
        topBar = {
            TopAppBarStart(
                title = "Инкубатор",
                true,
                navigateUp = navigateBack,
                settingUp = { navigateProjectEdit(project.id) }
            )
        }) { innerPadding ->

        IncubatorContainer(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp),
            incubatorTemp = tempList,
            incubatorDamp = dampList,
            incubatorOver = overList,
            incubatorAiring = airingList,
            projectTable = project,
            navigateDayEdit = navigateDayEdit
        )
    }
}


@SuppressLint("SimpleDateFormat")
@Composable
fun IncubatorContainer(
    modifier: Modifier,
    incubatorTemp: MutableList<String>,
    incubatorDamp: MutableList<String>,
    incubatorOver: MutableList<String>,
    incubatorAiring: MutableList<String>,
    projectTable: IncubatorProjectState,
    navigateDayEdit: (IncubatorEditNav) -> Unit
) {

    val scrollState = rememberLazyListState()

    var day by rememberSaveable { mutableIntStateOf(0) }

    var ovoscop = false

    if (projectTable.data != "") {
        LaunchedEffect(Unit) {
            var diff: Long = 0
            val calendar: Calendar = Calendar.getInstance()
            val dateBefore22: String = projectTable.data
            val dateBefore222: String =
                (calendar.get(Calendar.DAY_OF_MONTH)).toString() + "." + (calendar.get(
                    Calendar.MONTH
                ) + 1) + "." + calendar.get(Calendar.YEAR)
            val myFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
            val date1: Date = myFormat.parse(dateBefore22)
            val date2: Date = myFormat.parse(dateBefore222)
            diff = date2.time - date1.time
            day = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
            scrollState.scrollToItem(day)
            ovoscop = setOvoskop(projectTable.type, day)
        }
    }


    LazyColumn(
        state = scrollState, modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(incubatorTemp.size) {

            val borderStroke = if (day == it) BorderStroke(2.dp, Color.Black) else null

            MyRowIncubatorSettting(
                it,
                incubatorTemp[it],
                incubatorDamp[it],
                incubatorOver[it],
                incubatorAiring[it],
                modifier = Modifier
                    .padding(6.dp)
                    .clickable {
                        navigateDayEdit(
                            IncubatorEditNav(
                                idPT = projectTable.id,
                                day = it
                            )
                        )
                    },
                borderStroke = borderStroke,
                ovoscop
            )
        }
    }
}

@Composable
fun MyRowIncubatorSettting(
    n: Int,
    temp: String,
    damp: String,
    over: String,
    airing: String,
    modifier: Modifier = Modifier,
    borderStroke: BorderStroke?,
    ovos: Boolean,
//    ovosShowBottom: MutableState<Boolean>
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(),
        border = borderStroke
    ) {
        Text(
            text = "День ${n + 1}",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Температура ${temp}°C",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
            )
            Text(
                text = "Влажность ${damp}%",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Переворачивать ${over}",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
            )
            Text(
                text = "Проветривать ${airing}",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
            )
        }
        if (ovos) {
            TextButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Овоскопирование",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
            }
        }
    }
}

fun setOvoskop(typeBird: String, day: Int): Boolean {
    //todo доделать овоскоп
    when (typeBird) {
        "Курицы" -> {
            return when (day) {
                7 -> true
                11 -> true
                16 -> true
                else -> {
                    false
                }
            }
        }

        "Индюки", "Утки" -> {
            return when (day) {
                8 -> true
                14 -> true
                25 -> true
                else -> {
                    false
                }
            }
        }

        "Гуси" -> {
            return when (day) {
                9 -> true
                15 -> true
                21 -> true
                else -> {
                    false
                }
            }
        }

        "Перепела" -> {
            return when (day) {
                6 -> true
                13 -> true
                else -> {
                    false
                }
            }
        }

        else -> {
            return false
        }
    }

}

data class IncubatorEditNav(
    val idPT: Int,
    var day: Int,
)

fun massList(temp: IncubatorState): MutableList<String> {
    val mass = mutableListOf<String>()
    mass.add(temp.day1)
    mass.add(temp.day2)
    mass.add(temp.day3)
    mass.add(temp.day4)
    mass.add(temp.day5)
    mass.add(temp.day6)
    mass.add(temp.day7)
    mass.add(temp.day8)
    mass.add(temp.day9)
    mass.add(temp.day10)
    mass.add(temp.day11)
    mass.add(temp.day12)
    mass.add(temp.day13)
    mass.add(temp.day14)
    mass.add(temp.day15)
    mass.add(temp.day16)
    mass.add(temp.day17)
    if (temp.day18 == "0") return mass
    else {
        mass.add(temp.day18)
        mass.add(temp.day19)
        mass.add(temp.day20)
        mass.add(temp.day21)
        return if (temp.day22 == "0") mass else {
            mass.add(temp.day22)
            mass.add(temp.day23)
            mass.add(temp.day24)
            mass.add(temp.day25)
            mass.add(temp.day26)
            mass.add(temp.day27)
            mass.add(temp.day28)
            if (temp.day29 == "0") mass else {
                mass.add(temp.day29)
                mass.add(temp.day30)
                mass
            }
        }
    }
}