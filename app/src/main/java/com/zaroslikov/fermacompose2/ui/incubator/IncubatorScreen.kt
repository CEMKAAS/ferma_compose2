package com.zaroslikov.fermacompose2.ui.incubator

import android.database.Cursor
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.zaroslikov.fermacompose2.data.incubator.IncubatorTemp
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination


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

   val tempList =  massd(temp.value.titleList)


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
            incubatorTemp = tempList
        )
    }
}

fun massd(temp: IncubatorTemp): MutableList<String> {
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

@Composable
fun IncubatorContainer(
    modifier: Modifier,
    incubatorTemp: MutableList<String>
) {

    LazyColumn(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        items(incubatorTemp.size) {
            MyRowIncubatorSettting(
                it,
                incubatorTemp[it]
            )
        }
    }
}

@Composable
fun MyRowIncubatorSettting(
    n: Int,
    temp: String
//    ovos: Boolean,
//    ovosShowBottom: MutableState<Boolean>
) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .clickable {
            },
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors()
    ) {

        //clikable
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
                text = "Влажность 60%",
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
                text = "Переворачивать 2-3 раза",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
            )
            Text(
                text = "Проветривать 2 раза по 5 минут",
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
