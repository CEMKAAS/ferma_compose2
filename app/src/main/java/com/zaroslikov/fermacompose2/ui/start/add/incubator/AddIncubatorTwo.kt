package com.zaroslikov.fermacompose2.ui.start.add.incubator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditState
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination


object AddIncubatorTwoDestination : NavigationDestination {
    override val route = "AddIncubatorTwo"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

//@Composable
//fun AddIncubatorTwo(
//    navigateBack: () -> Unit,
//    navController: NavController,
//    projectIncubatorList: AddIncubatorList,
//    viewModel: AddIncubatorTwoViewModel = viewModel(factory = AppViewModelProvider.Factory)
//) {
//
//    val list = setAutoIncubator(
//        setIncubator(projectIncubatorList.typeBirds),
//        projectIncubatorList.checkedStateAiring,
//        projectIncubatorList.checkedStateOver
//    )
//    // Доставка из ахива
////    val project = viewModel.incubatorFromArchive(projectIncubatorList.typeBirds).collectAsState()
////
////    val projectCopy = project.value.itemList
////
////    val countProject = viewModel.incubatorFromArchive2(projectIncubatorList.typeBirds)
////    val openEndDialog = remember { mutableStateOf(false) }
////
////
////    if (countProject == 0) {
////        openEndDialog.value = false
////    } else {
////        openEndDialog.value = true
////    }
//
//    val activity = LocalContext.current as Activity
//    val scope = rememberCoroutineScope()
//    Scaffold(
//        topBar = {
//            TopAppBarEdit(title = "Инкубатор", navigateUp = navigateBack)
//        }
//    ) { innerPadding ->
//        AddIncubatorTwoContainer(
//            modifier = Modifier
//                .padding(innerPadding),
//            navigateContinue = {
//                scope.launch {
//
//                    val idPT = viewModel.savaProject(projectIncubatorList.toIncubatorData())
//
//                    viewModel.saveIncubator(
//                        setIdPT(it, idPT)
//                    )
//                    val eventParameters: MutableMap<String, Any> = HashMap()
//                    eventParameters["Имя"] = projectIncubatorList.title
//                    eventParameters["Тип"] = projectIncubatorList.typeBirds
//                    eventParameters["Кол-во"] = projectIncubatorList.count
//                    eventParameters["АвтоОхл"] = projectIncubatorList.checkedStateAiring
//                    eventParameters["АвтоПрев"] = projectIncubatorList.checkedStateAiring
//                    AppMetrica.reportEvent("Incubator", eventParameters);
//
//                    navController.navigate(StartDestination.route)
//                }
//            },
//            list = list,
////            projectList = projectCopy,
////            openEndDialog = openEndDialog
////            incubatorArh= {
////                    scope.launch {
////                       // val idPT = viewModel.savaProject(projectIncubatorList.toIncubatorData())
////                        list = viewModel.saveIncubator3(it).toMutableList()
//////                        viewModel.saveIncubator2(it, idPT)
////                        openEndDialog.value = false
//////                        (activity as MainActivity)?.showAd()
////
//////                        navController.navigate(StartDestination.route)
////                    }
////            },
//
//        )
//        DialogExamples()
//    }
//}


@Composable
fun AddIncubatorContainerTwo(
    navigateBack: () -> Unit,
    navigateContinue: (MutableList<Incubator>) -> Unit,
    list: MutableList<Incubator>,
//    onUpdate: (List<Incubator>) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBarEdit(title = "Инкубатор", navigateUp = navigateBack)
        }
    ) { innerPadding ->

        AddIncubatorTwoContaine(
            modifier = Modifier
                .padding(innerPadding),
//                .verticalScroll(rememberScrollState()),
            list = list,
            navigateContinue = navigateContinue,
//            onUpdate = onUpdate

        )
    }
}


@Composable
fun AddIncubatorTwoContaine(
    modifier: Modifier,
    navigateContinue: (MutableList<Incubator>) -> Unit,
//    incubatorArh: (Int) -> Unit,
    list: MutableList<Incubator>,
//    onUpdate: (List<Incubator>) -> Unit = {}
//    projectList: List<ProjectTable>,
//    openEndDialog: MutableState<Boolean>,
) {

    LazyColumn(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Row(
                modifier = Modifier
                    .height(40.dp)
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
        items(list.size) {
            MyRowIncubatorAdd(
                list[it],
//                onUpdate = onUpdate
            )
        }

        item {
            Button(
                onClick = { navigateContinue(list) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 8.dp)
            ) {
                Text(text = "Запустить")
            }
        }
    }

//    if (openEndDialog.value) {
//        ArhivIncubatorChoice(
//            openDialog = openEndDialog,
//            projectList = projectList
//                    incubatorArh = incubatorArh
//        )
//    }
}


@Composable
fun MyRowIncubatorAdd(
    incubator: Incubator,
//    onUpdate: (List<Incubator>) -> Unit = {}
) {
    var tempDay by remember { mutableStateOf(incubator.temp) }
    var dampDay by remember { mutableStateOf(incubator.damp) }
    var overDay by remember { mutableStateOf(incubator.over) }
    var airingDay by remember { mutableStateOf(incubator.airing) }

    val focusManager = LocalFocusManager.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(35.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = incubator.day.toString(),
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
            value = incubator.temp,
            onValueChange = {
                tempDay = it.replace(Regex("[^\\d.]"), "").replace(",", ".")
//                onUpdate(listOf( incubator.copy(temp = it)))
                incubator.temp = it
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
                dampDay = it.replace(Regex("[^\\d.]"), "").replace(",", ".")
                incubator.damp = it
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
                incubator.over = it
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
                incubator.airing = it
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


fun setIncubator(typeIncubator: String): MutableList<Incubator> {
    val incubator: MutableList<Incubator> = mutableListOf()
    when (typeIncubator) {
        "Курицы" -> {
            incubator.add(
                Incubator(
                    day = 1,
                    temp = "37.5",
                    damp = "60",
                    over = "2-3",
                    airing = "2 раза по 5 минут",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 2,
                    temp = "37.5",
                    damp = "60",
                    over = "2-3",
                    airing = "2 раза по 5 минут",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 3,
                    temp = "37.0",
                    damp = "70",
                    over = "2-3",
                    airing = "2 раза по 5 минут",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 4,
                    temp = "37.0",
                    damp = "70",
                    over = "2-3",
                    airing = "2 раза по 5 минут",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 5,
                    temp = "37.0",
                    damp = "70",
                    over = "2-3",
                    airing = "2 раза по 5 минут",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 6,
                    temp = "37.9",
                    damp = "66",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 7,
                    temp = "37.9",
                    damp = "66",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 8,
                    temp = "37.9",
                    damp = "66",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 9,
                    temp = "37.9",
                    damp = "66",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 10,
                    temp = "37.9",
                    damp = "66",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 11,
                    temp = "37.5",
                    damp = "60",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 12,
                    temp = "37.5",
                    damp = "60",
                    over = "2-3",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 13,
                    temp = "37.5",
                    damp = "60",
                    over = "2-3",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 14,
                    temp = "37.5",
                    damp = "60",
                    over = "2-3",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 15,
                    temp = "37.5",
                    damp = "60",
                    over = "2-3",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 16,
                    temp = "37.5",
                    damp = "60",
                    over = "2-3",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 17,
                    temp = "37.3",
                    damp = "60",
                    over = "2-3",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 18,
                    temp = "37.3",
                    damp = "60",
                    over = "2-3",
                    airing = "2 раза по 20 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 19,
                    temp = "37.0",
                    damp = "70",
                    over = "нет",
                    airing = "2 раза по 20 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 20,
                    temp = "37.0",
                    damp = "70",
                    over = "нет",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 21,
                    temp = "37.0",
                    damp = "70",
                    over = "нет",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            return incubator
        }

        "Гуси" -> {
            incubator.add(
                Incubator(
                    day = 1,
                    temp = "38.0",
                    damp = "65",
                    over = "3-4",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 2,
                    temp = "37.8",
                    damp = "65",
                    over = "6",
                    airing = "1 раз по 20 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 3,
                    temp = "37.8",
                    damp = "65",
                    over = "6",
                    airing = "1 раз по 20 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 4,
                    temp = "37.6",
                    damp = "70",
                    over = "6",
                    airing = "1 раз по 20 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 5,
                    temp = "37.6",
                    damp = "70",
                    over = "6",
                    airing = "1 раз по 20 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 6,
                    temp = "37.6",
                    damp = "70",
                    over = "6",
                    airing = "2 раза по 20 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 7,
                    temp = "37.6",
                    damp = "70",
                    over = "6",
                    airing = "2 раза по 20 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 8,
                    temp = "37.6",
                    damp = "70",
                    over = "6",
                    airing = "2 раза по 20 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 9,
                    temp = "37.6",
                    damp = "70",
                    over = "10",
                    airing = "2 раз по 20 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 10,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 11,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 12,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 13,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 14,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 15,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 16,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 17,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 18,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 19,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 20,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 21,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 22,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 23,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 24,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 25,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 26,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 27,
                    temp = "37.3",
                    damp = "75",
                    over = "10",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 28,
                    temp = "37.3",
                    damp = "75",
                    over = "нет",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 29,
                    temp = "37.3",
                    damp = "75",
                    over = "нет",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 30,
                    temp = "37.3",
                    damp = "75",
                    over = "нет",
                    airing = "3 раза по 45 мин",
                    idPT = 0
                )
            )
            return incubator
        }


        "Перепела" -> {
            incubator.add(
                Incubator(
                    day = 1,
                    temp = "38.0",
                    damp = "55",
                    over = "3-6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 2,
                    temp = "38.0",
                    damp = "55",
                    over = "3-6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 3,
                    temp = "37.7",
                    damp = "55",
                    over = "3-6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 4,
                    temp = "37.7",
                    damp = "55",
                    over = "3-6",
                    airing = "1 раз по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 5,
                    temp = "37.7",
                    damp = "55",
                    over = "3-6",
                    airing = "1 раз по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 6,
                    temp = "37.7",
                    damp = "55",
                    over = "3-6",
                    airing = "1 раз по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 7,
                    temp = "37.7",
                    damp = "55",
                    over = "3-6",
                    airing = "1 раз по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 8,
                    temp = "37.7",
                    damp = "55",
                    over = "3-6",
                    airing = "1 раз по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 9,
                    temp = "37.7",
                    damp = "55",
                    over = "3-6",
                    airing = "1 раз по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 10,
                    temp = "37.7",
                    damp = "55",
                    over = "3-6",
                    airing = "1 раз по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 11,
                    temp = "37.7",
                    damp = "55",
                    over = "3-6",
                    airing = "1 раз по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 12,
                    temp = "37.7",
                    damp = "55",
                    over = "3-6",
                    airing = "1 раз по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 13,
                    temp = "37.7",
                    damp = "55",
                    over = "3-6",
                    airing = "1 раз по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 14,
                    temp = "37.7",
                    damp = "55",
                    over = "3-6",
                    airing = "1 раз по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 15,
                    temp = "37.5",
                    damp = "37,5",
                    over = "3-6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 16,
                    temp = "37.5",
                    damp = "37,5",
                    over = "нет",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 17,
                    temp = "37.5",
                    damp = "37,5",
                    over = "нет",
                    airing = "нет",
                    idPT = 0
                )
            )
            return incubator
        }

        "Индюки" -> {
            incubator.add(
                Incubator(
                    day = 1,
                    temp = "38.0",
                    damp = "60",
                    over = "6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 2,
                    temp = "38.0",
                    damp = "60",
                    over = "6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 3,
                    temp = "38.0",
                    damp = "60",
                    over = "6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 4,
                    temp = "38.0",
                    damp = "60",
                    over = "6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 5,
                    temp = "38.0",
                    damp = "60",
                    over = "6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 6,
                    temp = "38.0",
                    damp = "60",
                    over = "6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 7,
                    temp = "38.0",
                    damp = "60",
                    over = "6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 8,
                    temp = "37.7",
                    damp = "45",
                    over = "6",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 9,
                    temp = "37.7",
                    damp = "45",
                    over = "6",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 10,
                    temp = "37.7",
                    damp = "45",
                    over = "6",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 11,
                    temp = "37.7",
                    damp = "45",
                    over = "6",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 12,
                    temp = "37.7",
                    damp = "45",
                    over = "6",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 13,
                    temp = "37.7",
                    damp = "45",
                    over = "6",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 14,
                    temp = "37.7",
                    damp = "65",
                    over = "6",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 15,
                    temp = "37.5",
                    damp = "65",
                    over = "4",
                    airing = "4 раза по 10 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 16,
                    temp = "37.5",
                    damp = "65",
                    over = "4",
                    airing = "4 раза по 10 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 17,
                    temp = "37.5",
                    damp = "65",
                    over = "4",
                    airing = "4 раза по 10 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 18,
                    temp = "37.5",
                    damp = "65",
                    over = "4",
                    airing = "4 раза по 10 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 19,
                    temp = "37.5",
                    damp = "65",
                    over = "4",
                    airing = "4 раза по 10 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 20,
                    temp = "37.5",
                    damp = "65",
                    over = "4",
                    airing = "4 раза по 10 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 21,
                    temp = "37.5",
                    damp = "65",
                    over = "4",
                    airing = "4 раза по 10 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 22,
                    temp = "37.5",
                    damp = "65",
                    over = "4",
                    airing = "4 раза по 10 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 23,
                    temp = "37.5",
                    damp = "65",
                    over = "4",
                    airing = "4 раза по 10 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 24,
                    temp = "37.5",
                    damp = "65",
                    over = "4",
                    airing = "4 раза по 10 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 25,
                    temp = "37.5",
                    damp = "65",
                    over = "4",
                    airing = "4 раза по 10 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 26,
                    temp = "37.5",
                    damp = "65",
                    over = "нет",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 27,
                    temp = "37.5",
                    damp = "65",
                    over = "нет",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 28,
                    temp = "37.5",
                    damp = "65",
                    over = "нет",
                    airing = "нет",
                    idPT = 0
                )
            )
            return incubator
        }

        "Утки" -> {
            incubator.add(
                Incubator(
                    day = 1,
                    temp = "38.0",
                    damp = "75",
                    over = "4",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 2,
                    temp = "38.0",
                    damp = "75",
                    over = "4",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 3,
                    temp = "38.0",
                    damp = "75",
                    over = "4",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 4,
                    temp = "38.0",
                    damp = "75",
                    over = "4",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 5,
                    temp = "38.0",
                    damp = "75",
                    over = "4",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 6,
                    temp = "38.0",
                    damp = "75",
                    over = "4",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 7,
                    temp = "37.8",
                    damp = "60",
                    over = "4",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 8,
                    temp = "37.8",
                    damp = "60",
                    over = "4-6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 9,
                    temp = "37.8",
                    damp = "60",
                    over = "4-6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 10,
                    temp = "37.8",
                    damp = "60",
                    over = "4-6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 11,
                    temp = "37.8",
                    damp = "60",
                    over = "4-6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 12,
                    temp = "37.8",
                    damp = "60",
                    over = "4-6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 13,
                    temp = "37.8",
                    damp = "60",
                    over = "4-6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 14,
                    temp = "37.8",
                    damp = "60",
                    over = "4-6",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 15,
                    temp = "37.8",
                    damp = "60",
                    over = "6",
                    airing = "2 раза по 15 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 16,
                    temp = "37.8",
                    damp = "60",
                    over = "6",
                    airing = "2 раза по 15 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 17,
                    temp = "37.8",
                    damp = "60",
                    over = "6",
                    airing = "2 раза по 15 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 18,
                    temp = "37.8",
                    damp = "60",
                    over = "6",
                    airing = "2 раза по 15 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 19,
                    temp = "37.8",
                    damp = "60",
                    over = "6",
                    airing = "2 раза по 15 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 20,
                    temp = "37.8",
                    damp = "60",
                    over = "6",
                    airing = "2 раза по 15 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 21,
                    temp = "37.8",
                    damp = "60",
                    over = "6",
                    airing = "2 раза по 15 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 22,
                    temp = "37.8",
                    damp = "60",
                    over = "6",
                    airing = "2 раза по 15 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 23,
                    temp = "37.8",
                    damp = "60",
                    over = "6",
                    airing = "2 раза по 15 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 24,
                    temp = "37.8",
                    damp = "60",
                    over = "6",
                    airing = "2 раза по 15 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 25,
                    temp = "37.5",
                    damp = "90",
                    over = "6",
                    airing = "2 раза по 15 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 26,
                    temp = "37.5",
                    damp = "90",
                    over = "нет",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 27,
                    temp = "37.5",
                    damp = "90",
                    over = "нет",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 28,
                    temp = "37.5",
                    damp = "90",
                    over = "нет",
                    airing = "нет",
                    idPT = 0
                )
            )
            return incubator
        }

        else -> {
            incubator.add(
                Incubator(
                    day = 1,
                    temp = "37.9",
                    damp = "66",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 2,
                    temp = "37.9",
                    damp = "66",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 3,
                    temp = "37.9",
                    damp = "66",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 4,
                    temp = "37.9",
                    damp = "66",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 5,
                    temp = "37.9",
                    damp = "66",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 6,
                    temp = "37.9",
                    damp = "66",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 7,
                    temp = "37.9",
                    damp = "66",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 8,
                    temp = "37.9",
                    damp = "66",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 9,
                    temp = "37.9",
                    damp = "66",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 10,
                    temp = "37.9",
                    damp = "66",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 11,
                    temp = "37.5",
                    damp = "60",
                    over = "2-3",
                    airing = "нет",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 12,
                    temp = "37.5",
                    damp = "60",
                    over = "2-3",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 13,
                    temp = "37.5",
                    damp = "60",
                    over = "2-3",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 14,
                    temp = "37.5",
                    damp = "60",
                    over = "2-3",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 15,
                    temp = "37.5",
                    damp = "60",
                    over = "2-3",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 16,
                    temp = "37.5",
                    damp = "60",
                    over = "2-3",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 17,
                    temp = "37.3",
                    damp = "47",
                    over = "2-3",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 18,
                    temp = "37.3",
                    damp = "47",
                    over = "2-3",
                    airing = "2 раза по 20 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 19,
                    temp = "37.0",
                    damp = "70",
                    over = "2-3",
                    airing = "2 раза по 20 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 20,
                    temp = "37.0",
                    damp = "70",
                    over = "2-3",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            incubator.add(
                Incubator(
                    day = 21,
                    temp = "37.0",
                    damp = "70",
                    over = "2-3",
                    airing = "2 раза по 5 мин",
                    idPT = 0
                )
            )
            return incubator
        }
    }
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

fun setIdPT(list: MutableList<Incubator>, idPT: Long): MutableList<Incubator> {
    list.forEach {
        it.idPT = idPT
    }
    return list
}

fun setAutoIncubator(
    list: MutableList<Incubator>,
    airing: Boolean,
    over: Boolean
): MutableList<Incubator> {

    if (airing) {
        list.forEach {
            it.airing = "Авто"
        }
    }
    if (over) {
        list.forEach {
            it.over = "Авто"
        }
    }
    return list
}


@Composable
fun DialogExamples() {
    val openAlertDialog = remember { mutableStateOf(true) }
    when {
        openAlertDialog.value -> {
            AlertDialogExample(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false
                },
                dialogTitle = "Справка",
                dialogText = "Если ваш инкубатор имеет погрешность, вы можете установить необходимую температуру здесь или воспользоваться рекомендованными значениями, которые можно изменять в процессе. \nРекомендуем вести ежедневный журнал и тщательно записывать данные, чтобы в будущем иметь возможность обратиться к этой информации из архива.",
                icon = Icons.Default.Info
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText, textAlign = TextAlign.Justify)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Отлично!")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArhivIncubatorChoice(
    openDialog: () -> Unit,
    projectList: List<ProjectTable>,
    incubatorArh: (Int) -> Unit
) {
    var idProject by remember { mutableIntStateOf(1) }

    AlertDialog(
        onDismissRequest = openDialog,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(20.dp))
    ) {

        Column(
            modifier = Modifier
                .background(color = Color.LightGray)
                .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Выбрать данные из архива?",
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
                fontSize = 19.sp
            )
            Text(
                "Данные которые Вы ввели не изменятся, температура, влажность, поворот и проветривание, будут добавлены из выбраного архива",
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 10.dp)
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))

            val (selectedOption, onOptionSelected) = remember { mutableStateOf(projectList[0]) }
            idProject = selectedOption.id
            Column(Modifier.selectableGroup()) {
                projectList.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = { onOptionSelected(text) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = null
                        )
                        Text(text.titleProject)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextButton(
                    onClick = openDialog, modifier = Modifier.padding(8.dp)
                ) {
                    Text("Отмена")
                }
                if (projectList[0].titleProject != "Ищем инкубаторы... Подождите пожалуйста") {
                    TextButton(
                        onClick = {
                            incubatorArh(idProject)
                        }, modifier = Modifier.padding(8.dp)
                    )
                    { Text("Выбрать") }
                }
            }
        }
    }
}


//
//private fun setIncubator1(typeIncubator: String): IncubatorTemp{
//    when (typeIncubator) {
//        "Курицы" -> {
//            return IncubatorList(
//                massTemp = mutableListOf(
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.3",
//                    "37.3",
//                    "37.0",
//                    "37.0",
//                    "37.0",
//                    ),
//                massDamp = mutableListOf(
//                    "66",
//                    "66",
//                    "66",
//                    "66",
//                    "66",
//                    "66",
//                    "66",
//                    "66",
//                    "66",
//                    "66",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "47",
//                    "47",
//                    "70",
//                    "70",
//                    "70"
//                ),
//                massAiring = mutableListOf(
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 20 мин",
//                    "2 раза по 20 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин"
//                ),
//                massOver = mutableListOf(
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "0"
//                )
//            )
//        }
//
//        "Гуси" -> {
//            return IncubatorList(
//                massTemp = mutableListOf(
//                    "38.0",
//                    "37.8",
//                    "37.8",
//                    "37.6",
//                    "37.6",
//                    "37.6",
//                    "37.6",
//                    "37.6",
//                    "37.6",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3",
//                    "37.3"
//                ),
//                massDamp = mutableListOf(
//                    "65",
//                    "65",
//                    "65",
//                    "70",
//                    "70",
//                    "70",
//                    "70",
//                    "70",
//                    "70",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75"
//                ),
//                massAiring = mutableListOf(
//                    "нет",
//                    "1 раз по 20 мин",
//                    "1 раз по 20 мин",
//                    "1 раз по 20 мин",
//                    "1 раз по 20 мин",
//                    "2 раз по 20 мин",
//                    "2 раз по 20 мин",
//                    "2 раз по 20 мин",
//                    "2 раз по 20 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин",
//                    "3 раза по 45 мин"
//                ),
//                massOver = mutableListOf(
//                    "3-4",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "10",
//                    "0",
//                    "0",
//                    "0"
//                )
//            )
//        }
//
//        "Перепела" -> {
//            return IncubatorList(
//                massTemp = mutableListOf(
//                    "38.0",
//                    "38.0",
//                    "37.7",
//                    "37.7",
//                    "37.7",
//                    "37.7",
//                    "37.7",
//                    "37.7",
//                    "37.7",
//                    "37.7",
//                    "37.7",
//                    "37.7",
//                    "37.7",
//                    "37.7",
//                    "37.5",
//                    "37.5",
//                    "37.5"
//                ),
//                massDamp = mutableListOf(
//                    "55",
//                    "55",
//                    "55",
//                    "55",
//                    "55",
//                    "55",
//                    "55",
//                    "55",
//                    "55",
//                    "55",
//                    "55",
//                    "55",
//                    "55",
//                    "55",
//                    "37.5",
//                    "37.5",
//                    "37.5"
//                ),
//                massAiring = mutableListOf(
//                    "нет",
//                    "нет",
//                    "нет",
//                    "1 раз по 5 мин",
//                    "1 раз по 5 мин",
//                    "1 раз по 5 мин",
//                    "1 раз по 5 мин",
//                    "1 раз по 5 мин",
//                    "1 раз по 5 мин",
//                    "1 раз по 5 мин",
//                    "1 раз по 5 мин",
//                    "1 раз по 5 мин",
//                    "1 раз по 5 мин",
//                    "1 раз по 5 мин",
//                    "нет",
//                    "нет",
//                    "нет"
//                ),
//                massOver = mutableListOf(
//                    "3-6",
//                    "3-6",
//                    "3-6",
//                    "3-6",
//                    "3-6",
//                    "3-6",
//                    "3-6",
//                    "3-6",
//                    "3-6",
//                    "3-6",
//                    "3-6",
//                    "3-6",
//                    "3-6",
//                    "3-6",
//                    "3-6",
//                    "нет",
//                    "нет"
//                )
//            )
//        }
//
//        "Индюки" -> {
//            return IncubatorList(
//                massTemp = mutableListOf(
//                    "38.0",
//                    "38.0",
//                    "38.0",
//                    "38.0",
//                    "38.0",
//                    "38.0",
//                    "38.0",
//                    "37.7",
//                    "37.7",
//                    "37.7",
//                    "37.7",
//                    "37.7",
//                    "37.7",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5"
//                ),
//                massDamp = mutableListOf(
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "45",
//                    "45",
//                    "45",
//                    "45",
//                    "45",
//                    "45",
//                    "65",
//                    "65",
//                    "65",
//                    "65",
//                    "65",
//                    "65",
//                    "65",
//                    "65",
//                    "65",
//                    "65",
//                    "65",
//                    "65",
//                    "65",
//                    "65",
//                    "65"
//                ),
//                massAiring = mutableListOf(
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "4 раза по 10 мин",
//                    "4 раза по 10 мин",
//                    "4 раза по 10 мин",
//                    "4 раза по 10 мин",
//                    "4 раза по 10 мин",
//                    "4 раза по 10 мин",
//                    "4 раза по 10 мин",
//                    "4 раза по 10 мин",
//                    "4 раза по 10 мин",
//                    "4 раза по 10 мин",
//                    "4 раза по 10 мин",
//                    "нет",
//                    "нет",
//                    "нет"
//                ),
//                massOver = mutableListOf(
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "4",
//                    "4",
//                    "4",
//                    "4",
//                    "4",
//                    "4",
//                    "4",
//                    "4",
//                    "4",
//                    "4",
//                    "4",
//                    "нет",
//                    "нет",
//                    "нет"
//                )
//            )
//        }
//
//        "Утки" -> {
//            return IncubatorList(
//                massTemp = mutableListOf(
//                    "38.0",
//                    "38.0",
//                    "38.0",
//                    "38.0",
//                    "38.0",
//                    "38.0",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.8",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5"
//                ),
//                massDamp = mutableListOf(
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "75",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "90",
//                    "90",
//                    "90",
//                    "90"
//                ),
//                massAiring = mutableListOf(
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "2 раза по 15 мин",
//                    "2 раза по 15 мин",
//                    "2 раза по 15 мин",
//                    "2 раза по 20 мин",
//                    "2 раза по 20 мин",
//                    "2 раза по 15 мин",
//                    "2 раза по 15 мин",
//                    "2 раза по 15 мин",
//                    "2 раза по 15 мин",
//                    "2 раза по 15 мин",
//                    "2 раза по 15 мин",
//                    "нет",
//                    "нет",
//                    "нет"
//                ),
//                massOver = mutableListOf(
//                    "4",
//                    "4",
//                    "4",
//                    "4",
//                    "4",
//                    "4",
//                    "4",
//                    "4-6",
//                    "4-6",
//                    "4-6",
//                    "4-6",
//                    "4-6",
//                    "4-6",
//                    "4-6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "6",
//                    "нет",
//                    "нет",
//                    "нет"
//                )
//            )
//        }
//
//        else -> {
//            return IncubatorList(
//                massTemp = mutableListOf(
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.9",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.5",
//                    "37.3",
//                    "37.3",
//                    "37.0",
//                    "37.0",
//                    "37.0"
//                ),
//                massDamp = mutableListOf(
//                    "66",
//                    "66",
//                    "66",
//                    "66",
//                    "66",
//                    "66",
//                    "66",
//                    "66",
//                    "66",
//                    "66",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "60",
//                    "47",
//                    "47",
//                    "70",
//                    "70",
//                    "70"
//                ),
//                massAiring = mutableListOf(
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "нет",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 20 мин",
//                    "2 раза по 20 мин",
//                    "2 раза по 5 мин",
//                    "2 раза по 5 мин"
//                ),
//                massOver = mutableListOf(
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "2-3",
//                    "0"
//                )
//            )
//        }
//    }
//}