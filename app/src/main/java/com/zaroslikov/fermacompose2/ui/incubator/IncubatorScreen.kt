package com.zaroslikov.fermacompose2.ui.incubator

import android.database.Cursor
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.incubator.IncubatorAiring
import com.zaroslikov.fermacompose2.data.incubator.IncubatorDamp
import com.zaroslikov.fermacompose2.data.incubator.IncubatorTemp
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
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
    viewModel: IncubatorViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val temp = viewModel.tempState.collectAsState()
    val damp = viewModel.dampState.collectAsState()
    val over = viewModel.overState.collectAsState()
    val airng = viewModel.airingState.collectAsState()
    val project = viewModel.projectIncubatorUIList.collectAsState()

    val tempList = massList(temp.value.titleList)
    val dampList = massList(damp.value.titleList)
    val overList = massList(over.value.titleList)
    val airingList = massList(airng.value.titleList)

    Scaffold(
        topBar = {
            TopAppBarStart(
                title = "Инкубатор",
                true,
                navigateUp = navigateBack,
            )
        }) { innerPadding ->

        IncubatorContainer(
            modifier = Modifier.padding(innerPadding),
            incubatorTemp = tempList,
            incubatorDamp = dampList,
            incubatorOver = overList,
            incubatorAiring = airingList,
            projectTable = project.value.projectTable
        )
    }
}


@Composable
fun IncubatorContainer(
    modifier: Modifier,
    incubatorTemp: MutableList<String>,
    incubatorDamp: MutableList<String>,
    incubatorOver: MutableList<String>,
    incubatorAiring: MutableList<String>,
    projectTable: ProjectTable
) {
    var diff: Long = 0
    val calendar: Calendar = Calendar.getInstance()
    val dateBefore22: String = projectTable.data
    val dateBefore222: String =
        (calendar.get(Calendar.DAY_OF_MONTH) + 1).toString() + "." + (calendar.get(
            Calendar.MONTH
        ) + 1) + "." + calendar.get(Calendar.YEAR)
    val myFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy")

    try {
        val date1: Date = myFormat.parse(dateBefore22)
        val date2: Date = myFormat.parse(dateBefore222)
        diff = date2.time - date1.time
    } catch (e: ParseException) {
        throw RuntimeException(e)
    }
    val dateNow = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
//    dateIncubator =  "Идет " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toString() + " день "

    val scrollState = rememberLazyListState()

    LaunchedEffect(Unit) {
        val middleIndex = dateNow
        scrollState.scrollToItem(middleIndex)
    }


    LazyColumn(
        state = scrollState, modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(incubatorTemp.size) {
            MyRowIncubatorSettting(
                it,
                incubatorTemp[it],
                incubatorDamp[it],
                incubatorOver[it],
                incubatorAiring[it],
                modifier = Modifier
                    .padding(6.dp)
                    .clickable {
                    },
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
    modifier: Modifier = Modifier
//    ovos: Boolean,
//    ovosShowBottom: MutableState<Boolean>
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors()
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
//        if (ovos) {
//            TextButton(
//                onClick = { ovosShowBottom.value = true },
//                modifier = Modifier.fillMaxWidth()) {
//                Text(
//                    text = "Овоскопирование",
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier
//                )
//            }
//        }
    }
}

fun massList(temp: IncubatorUIList): MutableList<String> {
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