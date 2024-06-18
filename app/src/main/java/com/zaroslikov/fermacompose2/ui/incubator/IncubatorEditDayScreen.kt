package com.zaroslikov.fermacompose2.ui.incubator

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.incubator.IncubatorList
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
    val day = viewModel.day + 1
    val temp = viewModel.tempStateList
    val damp = viewModel.dampStateList
    val over = viewModel.overStateList
    val airng = viewModel.airingStateList

    val tempList = editMassList(temp)
    val dampList = editMassList(damp)
    val overList = editMassList(over)
    val airingList = editMassList(airng)

    val coroutineScope = rememberCoroutineScope()

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
                coroutineScope.launch {
                    viewModel.saveItem()
                    onNavigateUp()
                }
            },
            onValueTempChange = viewModel ::updateTempUiState,
            onValueDampChange = viewModel::updateDampUiState,
            onValueOverChange = viewModel::updateOverUiState,
            onValueAiringChange = viewModel::updateAiringUiState
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
    saveDay: () -> Unit,
    onValueTempChange: (IncubatorState) -> Unit = {},
    onValueDampChange: (IncubatorState) -> Unit = {},
    onValueOverChange: (IncubatorState) -> Unit = {},
    onValueAiringChange: (IncubatorState) -> Unit = {},
) {


    Column(
        modifier = modifier.padding(5.dp, 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "День ${day}",
            fontSize = 25.sp, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = temp[day],
            onValueChange = {
                temp[day] = it
                onValueTempChange(IncubatorEditDayList(massTemp = temp).toEditIncubatorState().copy())
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
            value = damp[day],
            onValueChange = {
                damp[day] = it
                onValueDampChange(IncubatorEditDayList(massTemp = damp).toEditIncubatorState().copy())
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
            value = over[day],
            onValueChange = {
                over[day] = it
                onValueOverChange(IncubatorEditDayList(massTemp = over).toEditIncubatorState().copy())
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
            value = airing[day],
            onValueChange = {
                airing[day] = it
                onValueAiringChange(IncubatorEditDayList(massTemp = airing).toEditIncubatorState().copy())
            },
            label = { Text("Проветривание") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text("Укажите кол-во проветриваний")
            },
//            isError = () TODO
        )

        Button(
            onClick = { saveDay() },
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text(text = "Обновить")
            //TODO Изображение
        }
    }
}


fun editMassList(temp: IncubatorState): MutableList<String> {
    val mass = mutableListOf<String>()
    mass.add(temp.id.toString())
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
    mass.add(temp.idPT.toString())
    return mass
}


data class IncubatorEditDayList(
    var massTemp: MutableList<String>
)


fun IncubatorEditDayList.toEditIncubatorState(): IncubatorState = IncubatorState(
    id = massTemp[0].toInt(),
    day1 = massTemp[1],
    day2 = massTemp[2],
    day3 = massTemp[3],
    day4 = massTemp[4],
    day5 = massTemp[5],
    day6 = massTemp[6],
    day7 = massTemp[7],
    day8 = massTemp[8],
    day9 = massTemp[9],
    day10 = massTemp[10],
    day11 = massTemp[11],
    day12 = massTemp[12],
    day13 = massTemp[13],
    day14 = massTemp[14],
    day15 = massTemp[15],
    day16 = massTemp[16],
    day17 = massTemp[17],
    day18 = massTemp[18],
    day19 = massTemp[19],
    day20 = massTemp[20],
    day21 = massTemp[21],
    day22 = massTemp[22],
    day23 = massTemp[23],
    day24 = massTemp[24],
    day25 = massTemp[25],
    day26 = massTemp[26],
    day27 = massTemp[27],
    day28 = massTemp[28],
    day29 = massTemp[29],
    day30 = massTemp[30],
    idPT =  massTemp[31].toInt(),
)
