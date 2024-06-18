package com.zaroslikov.fermacompose2.ui.incubator

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.fermacompose2.TopAppBarEdit
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.incubator.IncubatorAiring
import com.zaroslikov.fermacompose2.data.incubator.IncubatorDamp
import com.zaroslikov.fermacompose2.data.incubator.IncubatorOver
import com.zaroslikov.fermacompose2.data.incubator.IncubatorTemp
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.StartDestination
import com.zaroslikov.fermacompose2.ui.start.add.incubator.IncubatorList
import com.zaroslikov.fermacompose2.ui.start.add.incubator.toIncubatorAiring
import com.zaroslikov.fermacompose2.ui.start.add.incubator.toIncubatorDamp
import com.zaroslikov.fermacompose2.ui.start.add.incubator.toIncubatorData
import com.zaroslikov.fermacompose2.ui.start.add.incubator.toIncubatorOver
import com.zaroslikov.fermacompose2.ui.start.add.incubator.toIncubatorTemp
import kotlinx.coroutines.launch


object IncubatorEditDayScreenDestination : NavigationDestination {
    override val route = "IncubatorEditDayScreen"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemCategory"
    val routeWithArgs = "$route/{$itemIdArg}/{$itemIdArgTwo}"
}

@Composable
fun IncubatorEditDayScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: IncubatorEditDayViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val idPT = viewModel.itemId
    val day = viewModel.day
    val temp = viewModel.tempStateList
    val damp = viewModel.dampStateList
    val over = viewModel.overStateList
    val airng = viewModel.airingStateList


    val tempList = editMassList(temp)
    val dampList = editMassList(damp)
    val overList = editMassList(over)
    val airingList = editMassList(airng)

    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBarEdit(
                title = "Инкубатор",
                navigateUp = navigateBack,
            )
        }) { innerPadding ->

        IncubatorEditDayContainer(
            day = day,
            temp = tempList,
            damp = dampList,
            over = overList,
            airing = airingList,
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp),
            saveDay = {
                scope.launch {
                    viewModel.saveIncubator(
                        it.toEditIncubatorTemp(temp.id,idPT),
                        it.toEditIncubatorDamp(damp.id,idPT),
                        it.toEditIncubatorOver(over.id,idPT),
                        it.toEditIncubatorAiring(airng.id,idPT)
                    )
                    onNavigateUp()
                }
            }
        )
    }

}

@Composable
fun IncubatorEditDayContainer(
    day: Int,
    temp: MutableList<String>,
    damp: MutableList<String>,
    over: MutableList<String>,
    airing: MutableList<String>,
    modifier: Modifier = Modifier,
    saveDay: (IncubatorList) -> Unit
) {
    var tempEdit by rememberSaveable { mutableStateOf("") }
    var dampEdit by rememberSaveable { mutableStateOf("") }
    var overEdit by rememberSaveable { mutableStateOf("") }
    var airingEdit by rememberSaveable { mutableStateOf("") }

    tempEdit = temp[day]
    dampEdit = damp[day]
    overEdit = over[day]
    airingEdit = airing[day]

    Column(
        modifier = modifier.padding(5.dp, 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "День ${day + 1}",
            fontSize = 25.sp, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tempEdit,
            onValueChange = {
                tempEdit = it
                temp[day] = it
            },
            label = { Text("Температура") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text("Укажите температуру")
            },
            suffix = { Text(text = "°C") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            //            isError = () TODO
        )
        OutlinedTextField(
            value = dampEdit,
            onValueChange = {
                dampEdit = it
                damp[day] = it
            },
            label = { Text("Влажность") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text("Укажите влажность")
            },
            suffix = { Text(text = "%") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            isError = () TODO
        )
        OutlinedTextField(
            value = overEdit,
            onValueChange = {
                overEdit = it
                over[day] = it
            },
            label = { Text("Переворот") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text("Укажите кол-во переворачиваний")
            },
            suffix = { },
//            isError = () TODO
        )
        OutlinedTextField(
            value = airingEdit,
            onValueChange = {
                airingEdit = it
                airing[day] = it
            },
            label = { Text("Проветривание") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text("Укажите кол-во проветриваний")
            },
//            isError = () TODO
        )

        Button(
            onClick = { saveDay(IncubatorList(temp, damp, over, airing)) },
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text(text = "Обновить")
            //TODO Изображение
        }
    }
}


fun editMassList(temp: IncubatorState): MutableList<String> {
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
    mass.add(temp.day18)
    mass.add(temp.day19)
    mass.add(temp.day20)
    mass.add(temp.day21)
    mass.add(temp.day22)
    mass.add(temp.day23)
    mass.add(temp.day24)
    mass.add(temp.day25)
    mass.add(temp.day26)
    mass.add(temp.day27)
    mass.add(temp.day28)
    mass.add(temp.day29)
    mass.add(temp.day30)
    return mass
}


fun IncubatorList.toEditIncubatorTemp(id: Int,idPT: Int): IncubatorTemp = IncubatorTemp(
    id = id,
    day1 = massTemp[0],
    day2 = massTemp[1],
    day3 = massTemp[2],
    day4 = massTemp[3],
    day5 = massTemp[4],
    day6 = massTemp[5],
    day7 = massTemp[6],
    day8 = massTemp[7],
    day9 = massTemp[8],
    day10 = massTemp[9],
    day11 = massTemp[10],
    day12 = massTemp[11],
    day13 = massTemp[12],
    day14 = massTemp[13],
    day15 = massTemp[14],
    day16 = massTemp[15],
    day17 = massTemp[16],
    day18 = massTemp[17],
    day19 = massTemp[18],
    day20 = massTemp[19],
    day21 = massTemp[20],
    day22 = massTemp[21],
    day23 = massTemp[22],
    day24 = massTemp[23],
    day25 = massTemp[24],
    day26 = massTemp[25],
    day27 = massTemp[26],
    day28 = massTemp[27],
    day29 = massTemp[28],
    day30 = massTemp[29],
    idPT = idPT
)

fun IncubatorList.toEditIncubatorDamp(id: Int,idPT: Int): IncubatorDamp = IncubatorDamp(
    id = id,
    day1 = massDamp[0],
    day2 = massDamp[1],
    day3 = massDamp[2],
    day4 = massDamp[3],
    day5 = massDamp[4],
    day6 = massDamp[5],
    day7 = massDamp[6],
    day8 = massDamp[7],
    day9 = massDamp[8],
    day10 = massDamp[9],
    day11 = massDamp[10],
    day12 = massDamp[11],
    day13 = massDamp[12],
    day14 = massDamp[13],
    day15 = massDamp[14],
    day16 = massDamp[15],
    day17 = massDamp[16],
    day18 = massDamp[17],
    day19 = massDamp[18],
    day20 = massDamp[19],
    day21 = massDamp[20],
    day22 = massDamp[21],
    day23 = massDamp[22],
    day24 = massDamp[23],
    day25 = massDamp[24],
    day26 = massDamp[25],
    day27 = massDamp[26],
    day28 = massDamp[27],
    day29 = massDamp[28],
    day30 = massDamp[29],
    idPT = idPT
)

fun IncubatorList.toEditIncubatorOver(id: Int,idPT: Int): IncubatorOver = IncubatorOver(
    id = id,
    day1 = massOver[0],
    day2 = massOver[1],
    day3 = massOver[2],
    day4 = massOver[3],
    day5 = massOver[4],
    day6 = massOver[5],
    day7 = massOver[6],
    day8 = massOver[7],
    day9 = massOver[8],
    day10 = massOver[9],
    day11 = massOver[10],
    day12 = massOver[11],
    day13 = massOver[12],
    day14 = massOver[13],
    day15 = massOver[14],
    day16 = massOver[15],
    day17 = massOver[16],
    day18 = massOver[17],
    day19 = massOver[18],
    day20 = massOver[19],
    day21 = massOver[20],
    day22 = massOver[21],
    day23 = massOver[22],
    day24 = massOver[23],
    day25 = massOver[24],
    day26 = massOver[25],
    day27 = massOver[26],
    day28 = massOver[27],
    day29 = massOver[28],
    day30 = massOver[29],
    idPT = idPT

)

fun IncubatorList.toEditIncubatorAiring(id: Int, idPT: Int): IncubatorAiring = IncubatorAiring(
    id = id,
    day1 = massAiring[0],
    day2 = massAiring[1],
    day3 = massAiring[2],
    day4 = massAiring[3],
    day5 = massAiring[4],
    day6 = massAiring[5],
    day7 = massAiring[6],
    day8 = massAiring[7],
    day9 = massAiring[8],
    day10 = massAiring[9],
    day11 = massAiring[10],
    day12 = massAiring[11],
    day13 = massAiring[12],
    day14 = massAiring[13],
    day15 = massAiring[14],
    day16 = massAiring[15],
    day17 = massAiring[16],
    day18 = massAiring[17],
    day19 = massAiring[18],
    day20 = massAiring[19],
    day21 = massAiring[20],
    day22 = massAiring[21],
    day23 = massAiring[22],
    day24 = massAiring[23],
    day25 = massAiring[24],
    day26 = massAiring[25],
    day27 = massAiring[26],
    day28 = massAiring[27],
    day29 = massAiring[28],
    day30 = massAiring[29],
    idPT = idPT
)

