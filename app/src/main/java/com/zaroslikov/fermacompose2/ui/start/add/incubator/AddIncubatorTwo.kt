package com.zaroslikov.fermacompose2.ui.start.add.incubator

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.zaroslikov.fermacompose2.TopAppBarStart
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.ferma.IncubatorAiring
import com.zaroslikov.fermacompose2.data.ferma.IncubatorDamp
import com.zaroslikov.fermacompose2.data.ferma.IncubatorOver
import com.zaroslikov.fermacompose2.data.ferma.IncubatorTemp
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.StartDestination
import kotlinx.coroutines.launch

object AddIncubatorTwoDestination : NavigationDestination {
    override val route = "AddIncubatorTwo"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun AddIncubatorTwo(
    navigateBack: () -> Unit,
    navController: NavController,
    projectIncubatorList: AddIncubatorList,
    viewModel: AddIncubatorTwoViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
//    val projectIncubatorList = viewModel.itemId
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBarEdit(title = "Инкубатор", navigateUp = navigateBack)
        },
    ) { innerPadding ->
        AddIncubatorTwoContainer(
            modifier = Modifier
                .padding(innerPadding),
            navigateContinue = {
                scope.launch {

                     val idPT =  viewModel.savaProject(projectIncubatorList.toIncubatorData())

                    viewModel.saveIncubator(
                        it.toIncubatorTemp(idPT),
                        it.toIncubatorDamp(idPT),
                        it.toIncubatorOver(idPT),
                        it.toIncubatorAiring(idPT)
                    )
                    navController.navigate(StartDestination.route)
                }
            },
            typeBird = projectIncubatorList.typeBirds,
            airing = projectIncubatorList.checkedStateAiring,
            over = projectIncubatorList.checkedStateOver,
        )
    }
}

@Composable
fun AddIncubatorTwoContainer(
    modifier: Modifier, navigateContinue: (IncubatorList) -> Unit,
    typeBird: String, airing: Boolean, over: Boolean

) {

    val list = setAutoIncubator(setIncubator(typeBird), airing, over)

    LazyColumn(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .background(color = Color(red = 238, green = 243, blue = 220))
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "День",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.16f)
                        .padding(6.dp)
                )
                Divider(
                    color = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                Text(
                    text = "°C",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.16f)
                        .padding(6.dp)
                )
                Divider(
                    color = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                Text(
                    text = "%",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.16f)
                        .padding(6.dp)
                )
                Divider(
                    color = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                Text(
                    text = "Поворот",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .padding(6.dp)
                )
                Divider(
                    color = Color.DarkGray,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                Text(
                    text = "Проветривание",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                )
            }
        }
        item { Divider(color = Color.DarkGray, thickness = 1.dp) }
        item { Divider(color = Color.DarkGray, thickness = 1.dp) }
        items(list.massTemp.size) {
            MyRowIncubatorAdd(
                it,
                list.massTemp,
                list.massDamp,
                list.massOver,
                list.massAiring
            )
        }
        item {
            Button(
                onClick = {
                    setMassToSql(
                        typeBird,
                        list.massTemp,
                        list.massDamp,
                        list.massOver,
                        list.massAiring
                    )
                    navigateContinue(list)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 8.dp)
            ) {
                Text(text = "Запустить")
            }
        }
    }
}


@Composable
fun MyRowIncubatorAdd(
    day: Int,
    temp: MutableList<String>,
    damp: MutableList<String>,
    over: MutableList<String>,
    airing: MutableList<String>

) {

    val text by rememberSaveable { mutableStateOf("${day + 1}") }
    var tempDay by remember { mutableStateOf(temp[day]) }
    var dampDay by remember { mutableStateOf(damp[day]) }
    var overDay by remember { mutableStateOf(over[day]) }
    var airingDay by remember { mutableStateOf(airing[day]) }

    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(35.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.16f)
                .padding(6.dp),
            )
        Divider(
            color = Color.DarkGray,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        BasicTextField(
            value = tempDay,
            onValueChange = {
                tempDay = it
                temp[day] = it
            },
            textStyle = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth(0.16f)
                .padding(6.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            }
            )
        )
        Divider(
            color = Color.DarkGray,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )

        BasicTextField(
            value = dampDay,
            onValueChange = {
                dampDay = it
                damp[day] = it
            },
            textStyle = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth(0.16f)
                .padding(6.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number,
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            }
            )
        )
        Divider(
            color = Color.DarkGray,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )

        BasicTextField(
            value = overDay,
            onValueChange = {
                overDay = it
                over[day] = it
            },
            textStyle = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .padding(6.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            }
            )
        )
        Divider(
            color = Color.DarkGray,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        BasicTextField(
            value = airingDay,
            onValueChange = {
                airingDay = it
                airing[day] = it
            },
            textStyle = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            }
            )
        )

    }
    Divider(color = Color.DarkGray, thickness = 1.dp)
}

fun AddIncubatorList.toIncubatorData(): ProjectTable = ProjectTable(
    id = 0,
    titleProject = title,
    type = typeBirds,
    data = date1,
    eggAll = count,
    eggAllEND = count,
    airing = checkedStateAiring.toString(), //todo че было раньше
    over = checkedStateOver.toString(),
    arhive = "0",
    dateEnd = "0",
    time1 = time1,
    time2 = time2,
    time3 = time3,
    mode = 0
)


fun IncubatorList.toIncubatorTemp(idPT: Int): IncubatorTemp = IncubatorTemp(
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

fun IncubatorList.toIncubatorDamp(idPT: Int): IncubatorDamp = IncubatorDamp(
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

fun IncubatorList.toIncubatorOver(idPT: Int): IncubatorOver = IncubatorOver(
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

fun IncubatorList.toIncubatorAiring(idPT: Int): IncubatorAiring = IncubatorAiring(
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


data class IncubatorList(
    var massTemp: MutableList<String>,
    var massDamp: MutableList<String>,
    var massAiring: MutableList<String>,
    var massOver: MutableList<String>
)

private fun setMassToSql(
    typeIncubator: String,
    massTemp: MutableList<String>,
    massDamp: MutableList<String>,
    massOver: MutableList<String>,
    massAiring: MutableList<String>
) {
    when (typeIncubator) {
        "Курицы" -> {
            endMass(9, massTemp)
            endMass(9, massDamp)
            endMass(9, massOver)
            endMass(9, massAiring)
        }

        "Индюки", "Утки" -> {
            endMass(2, massTemp)
            endMass(2, massDamp)
            endMass(2, massOver)
            endMass(2, massAiring)
        }

        "Перепела" -> {
            endMass(13, massTemp)
            endMass(13, massDamp)
            endMass(13, massOver)
            endMass(13, massAiring)
        }
    }

}

private fun endMass(day: Int, mass: MutableList<String>) {
    for (i in 1..day) {
        mass.add("0")
    }
}

private fun setAutoIncubator(list: IncubatorList, airing: Boolean, over: Boolean): IncubatorList {

    val airingAuto = mutableListOf<String>()
    val overAuto = mutableListOf<String>()

    if (airing) {
        for (i in 0..list.massTemp.size) {
            airingAuto.add("Авто")
        }
    }

    if (over) {
        for (i in 0..list.massTemp.size) {
            overAuto.add("Авто")
        }
    }

    return if (airing && over) {
        list.copy(massAiring = airingAuto, massOver = overAuto)
    } else if (airing) {
        list.copy(massAiring = airingAuto)
    } else if (over) {
        list.copy(
            massOver = overAuto
        )
    } else list
}


private fun setIncubator(typeIncubator: String): IncubatorList {
    when (typeIncubator) {
        "Курицы" -> {
            return IncubatorList(
                massTemp = mutableListOf(
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.3",
                    "37.3",
                    "37.0",
                    "37.0",
                    "37.0",

                    ),
                massDamp = mutableListOf(
                    "66",
                    "66",
                    "66",
                    "66",
                    "66",
                    "66",
                    "66",
                    "66",
                    "66",
                    "66",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "47",
                    "47",
                    "70",
                    "70",
                    "70"
                ),
                massAiring = mutableListOf(
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "2 раза по 20 мин",
                    "2 раза по 20 мин",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин"
                ),
                massOver = mutableListOf(
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "0"
                )
            )
        }

        "Гуси" -> {
            return IncubatorList(
                massTemp = mutableListOf(
                    "38.0",
                    "37.8",
                    "37.8",
                    "37.6",
                    "37.6",
                    "37.6",
                    "37.6",
                    "37.6",
                    "37.6",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3",
                    "37.3"
                ),
                massDamp = mutableListOf(
                    "65",
                    "65",
                    "65",
                    "70",
                    "70",
                    "70",
                    "70",
                    "70",
                    "70",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75"
                ),
                massAiring = mutableListOf(
                    "нет",
                    "1 раз по 20 мин",
                    "1 раз по 20 мин",
                    "1 раз по 20 мин",
                    "1 раз по 20 мин",
                    "2 раз по 20 мин",
                    "2 раз по 20 мин",
                    "2 раз по 20 мин",
                    "2 раз по 20 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин",
                    "3 раза по 45 мин"
                ),
                massOver = mutableListOf(
                    "3-4",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "10",
                    "10",
                    "10",
                    "10",
                    "10",
                    "10",
                    "10",
                    "10",
                    "10",
                    "10",
                    "10",
                    "10",
                    "10",
                    "10",
                    "10",
                    "10",
                    "10",
                    "10",
                    "10",
                    "0",
                    "0",
                    "0"
                )
            )
        }

        "Перепела" -> {
            return IncubatorList(
                massTemp = mutableListOf(
                    "38.0",
                    "38.0",
                    "37.7",
                    "37.7",
                    "37.7",
                    "37.7",
                    "37.7",
                    "37.7",
                    "37.7",
                    "37.7",
                    "37.7",
                    "37.7",
                    "37.7",
                    "37.7",
                    "37.5",
                    "37.5",
                    "37.5"
                ),
                massDamp = mutableListOf(
                    "55",
                    "55",
                    "55",
                    "55",
                    "55",
                    "55",
                    "55",
                    "55",
                    "55",
                    "55",
                    "55",
                    "55",
                    "55",
                    "55",
                    "37.5",
                    "37.5",
                    "37.5"
                ),
                massAiring = mutableListOf(
                    "нет",
                    "нет",
                    "нет",
                    "1 раз по 5 мин",
                    "1 раз по 5 мин",
                    "1 раз по 5 мин",
                    "1 раз по 5 мин",
                    "1 раз по 5 мин",
                    "1 раз по 5 мин",
                    "1 раз по 5 мин",
                    "1 раз по 5 мин",
                    "1 раз по 5 мин",
                    "1 раз по 5 мин",
                    "1 раз по 5 мин",
                    "нет",
                    "нет",
                    "нет"
                ),
                massOver = mutableListOf(
                    "3-6",
                    "3-6",
                    "3-6",
                    "3-6",
                    "3-6",
                    "3-6",
                    "3-6",
                    "3-6",
                    "3-6",
                    "3-6",
                    "3-6",
                    "3-6",
                    "3-6",
                    "3-6",
                    "3-6",
                    "нет",
                    "нет"
                )
            )
        }

        "Индюки" -> {
            return IncubatorList(
                massTemp = mutableListOf(
                    "38.0",
                    "38.0",
                    "38.0",
                    "38.0",
                    "38.0",
                    "38.0",
                    "38.0",
                    "37.7",
                    "37.7",
                    "37.7",
                    "37.7",
                    "37.7",
                    "37.7",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5"
                ),
                massDamp = mutableListOf(
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "45",
                    "45",
                    "45",
                    "45",
                    "45",
                    "45",
                    "65",
                    "65",
                    "65",
                    "65",
                    "65",
                    "65",
                    "65",
                    "65",
                    "65",
                    "65",
                    "65",
                    "65",
                    "65",
                    "65",
                    "65"
                ),
                massAiring = mutableListOf(
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "4 раза по 10 мин",
                    "4 раза по 10 мин",
                    "4 раза по 10 мин",
                    "4 раза по 10 мин",
                    "4 раза по 10 мин",
                    "4 раза по 10 мин",
                    "4 раза по 10 мин",
                    "4 раза по 10 мин",
                    "4 раза по 10 мин",
                    "4 раза по 10 мин",
                    "4 раза по 10 мин",
                    "нет",
                    "нет",
                    "нет"
                ),
                massOver = mutableListOf(
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "4",
                    "4",
                    "4",
                    "4",
                    "4",
                    "4",
                    "4",
                    "4",
                    "4",
                    "4",
                    "4",
                    "нет",
                    "нет",
                    "нет"
                )
            )
        }

        "Утки" -> {
            return IncubatorList(
                massTemp = mutableListOf(
                    "38.0",
                    "38.0",
                    "38.0",
                    "38.0",
                    "38.0",
                    "38.0",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.8",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5"
                ),
                massDamp = mutableListOf(
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "75",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "90",
                    "90",
                    "90",
                    "90"
                ),
                massAiring = mutableListOf(
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "2 раза по 15 мин",
                    "2 раза по 15 мин",
                    "2 раза по 15 мин",
                    "2 раза по 20 мин",
                    "2 раза по 20 мин",
                    "2 раза по 15 мин",
                    "2 раза по 15 мин",
                    "2 раза по 15 мин",
                    "2 раза по 15 мин",
                    "2 раза по 15 мин",
                    "2 раза по 15 мин",
                    "нет",
                    "нет",
                    "нет"
                ),
                massOver = mutableListOf(
                    "4",
                    "4",
                    "4",
                    "4",
                    "4",
                    "4",
                    "4",
                    "4-6",
                    "4-6",
                    "4-6",
                    "4-6",
                    "4-6",
                    "4-6",
                    "4-6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "6",
                    "нет",
                    "нет",
                    "нет"
                )
            )
        }

        else -> {
            return IncubatorList(
                massTemp = mutableListOf(
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.9",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.5",
                    "37.3",
                    "37.3",
                    "37.0",
                    "37.0",
                    "37.0"
                ),
                massDamp = mutableListOf(
                    "66",
                    "66",
                    "66",
                    "66",
                    "66",
                    "66",
                    "66",
                    "66",
                    "66",
                    "66",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "60",
                    "47",
                    "47",
                    "70",
                    "70",
                    "70"
                ),
                massAiring = mutableListOf(
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "нет",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин",
                    "2 раза по 20 мин",
                    "2 раза по 20 мин",
                    "2 раза по 5 мин",
                    "2 раза по 5 мин"
                ),
                massOver = mutableListOf(
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "2-3",
                    "0"
                )
            )
        }
    }
}