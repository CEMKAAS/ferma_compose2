package com.zaroslikov.fermacompose2.ui.incubator

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
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
    navigateOvos: (IncubatorOvosNav) -> Unit,
    navigateStart: () -> Unit,
    viewModel: IncubatorViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val incubator by viewModel.incubatorUiState.collectAsState()
    val projectState by viewModel.homeUiState.collectAsState()
    val project = viewModel.itemUiState
    val projectList by viewModel.projectListAct.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    // В приниципе все только доделать переход
    Scaffold(
        topBar = {
            TopAppBarStart(
                title = projectState.project.titleProject,
                true,
                navigateUp = navigateBack,
                settingUp = { navigateProjectEdit(project.id) }
            )
        },
//        bottomBar = {
//            Banner(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .wrapContentHeight()
//            )
//        }
    ) { innerPadding ->

        IncubatorContainer(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp),
//                .verticalScroll(rememberScrollState()),
            incubator = incubator.itemList,
            projectTable = project,
            navigateDayEdit = navigateDayEdit,
            navigateOvos = navigateOvos,
            projectList = projectList.itemList,
            onValueChange = viewModel::updateUiState,
            saveInArh = {
                coroutineScope.launch {
                    viewModel.saveItem()
                    navigateStart()
                }
            },
            deleteInc = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                    navigateStart()
                }
            },
            saveInProject = {
                coroutineScope.launch {
                    viewModel.saveItem()
                    viewModel.saveProject(it.animalTable, it.count)
                    navigateStart()
                }
            },
            saveInNewProject = {
                coroutineScope.launch {
                    viewModel.saveNewProject(it.animalTable, it.count)
                    navigateStart()
                }
            },
            save = {
                coroutineScope.launch {
                    viewModel.saveNewProjectArh(it)
                }
            }
        )
    }
}


@SuppressLint("SimpleDateFormat")
@Composable
fun IncubatorContainer(
    modifier: Modifier,
    incubator: List<Incubator>,
    projectTable: IncubatorProjectEditState,
    navigateDayEdit: (IncubatorEditNav) -> Unit,
    navigateOvos: (IncubatorOvosNav) -> Unit,
    projectList: List<ProjectTable>,
    onValueChange: (IncubatorProjectEditState) -> Unit = {},
    saveInArh: () -> Unit,
    deleteInc: () -> Unit,
    saveInProject: (IncubatorAnimalInProject) -> Unit,
    saveInNewProject: (IncubatorAnimalInProject) -> Unit,
    save: (project: ProjectTable) -> Unit
) {

    val scrollState = rememberLazyListState()

    val openEndDialog = remember { mutableStateOf(false) }
    var endBoolean by remember { mutableStateOf(false) }

    if (openEndDialog.value) {
        EndIncubator(
            openEndDialog = openEndDialog,
            endAdvance = endBoolean,
            projectBoolean = projectList.isNotEmpty(),
            projectList = projectList,
            projectTable = projectTable,
            onValueChange = onValueChange,
            saveInArh = saveInArh,
            deleteInc = deleteInc,
            saveInProject = saveInProject,
            saveInNewProject = saveInNewProject,
            save = save
        )
    }

    var day by rememberSaveable { mutableIntStateOf(0) }

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
            endBoolean = endInc(projectTable.type, day, openEndDialog)
        }
    }


    LazyColumn(
        state = scrollState, modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(incubator.size) {

            val borderStroke = if (day == it) BorderStroke(2.dp, Color.Black) else null

            MyRowIncubatorSettting(
                incubator[it],
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
                typeBird = projectTable.type,
                navigateOvos = {
                    navigateOvos(
                        IncubatorOvosNav(
                            day = it + 1,
                            typeBirds = projectTable.type
                        )
                    )
                }
            )
        }
        item {
            Button(
                onClick = { openEndDialog.value = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Text(text = "Завершить")
            }
        }
    }

}


@Composable
fun MyRowIncubatorSettting(
    incubator: Incubator,
    modifier: Modifier = Modifier,
    borderStroke: BorderStroke?,
    typeBird: String,
    navigateOvos: () -> Unit,
) {

    var ovoscop by rememberSaveable { mutableStateOf(false) }
    ovoscop = setOvoskop(typeBird, incubator.day)

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(),
        border = borderStroke
    ) {
        Text(
            text = "День ${incubator.day}",
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
                text = "Температура ${incubator.temp}°C",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
            )
            Text(
                text = "Влажность ${incubator.damp}%",
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
                text = "Переворачивать ${incubator.over}",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
            )
            Text(
                text = "Проветривать ${incubator.airing}",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
            )
        }
        if (ovoscop) {
            TextButton(
                onClick = navigateOvos,
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

fun endInc(typeBird: String, day: Int, openEndDialog: MutableState<Boolean>): Boolean {
    return if ((typeBird == "Курицы") && day < 21) {
        false
    } else if ((typeBird == "Индюки") && day < 28) {
        false
    } else if ((typeBird == "Гуси") && day < 30) {
        false
    } else if ((typeBird == "Утки") && day < 28) {
        false
    } else if ((typeBird == "Перепела") && day < 17) {
        false
    } else {
        openEndDialog.value = true
        true
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EndIncubator(
    openEndDialog: MutableState<Boolean>,
    endAdvance: Boolean,
    projectBoolean: Boolean,
    projectList: List<ProjectTable>,
    projectTable: IncubatorProjectEditState,
    onValueChange: (IncubatorProjectEditState) -> Unit = {},
    saveInArh: () -> Unit,
    deleteInc: () -> Unit,
    saveInProject: (IncubatorAnimalInProject) -> Unit,
    saveInNewProject: (IncubatorAnimalInProject) -> Unit,
    save: (project: ProjectTable) -> Unit
) {
    val format = SimpleDateFormat("dd.MM.yyyy")
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val dateEnd: String = format.format(calendar.timeInMillis)

    var idProject by remember { mutableIntStateOf(1) }

    AlertDialog(
        onDismissRequest = { openEndDialog.value = false },
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

            if (endAdvance) {
                Text(
                    "Поздравлем с появлением птенцов!",
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
                    fontSize = 19.sp
                )
                Text(
                    "Мы сохранили Ваши данные в архив, чтобы вы не забыли параметры! Также Вы можете добавить птенцов в существующий проект или создать новый для дальнейшей работы"
                            + "Вы заложили ${projectTable.eggAll} яиц Сколько птенцов у Вас вылупилось?",
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 10.dp)
                )
                Spacer(modifier = Modifier.padding(vertical = 10.dp))

                OutlinedTextField(
                    value = projectTable.eggAllEND,
                    onValueChange = {
                        onValueChange(projectTable.copy(eggAllEND = it))
                    },
                    label = { Text("Кол-во птенцов") }
                )

                if (projectBoolean) {

                    val (selectedOption, onOptionSelected) = remember { mutableStateOf(projectList[0]) }
                    idProject = selectedOption.id

                    Column(Modifier.selectableGroup()) {
                        projectList.forEach { text ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (text == selectedOption
                                                ),
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
                }


                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {

                    TextButton(
                        onClick = {
                            openEndDialog.value = false
                            onValueChange(projectTable.copy(arhive = "1", dateEnd = dateEnd))
                            saveInArh()
                        },
                        modifier = Modifier.padding(4.dp),
                    ) {
                        Text("Завершить")
                    }

                    if (projectBoolean) {
                        TextButton(
                            onClick = {
                                openEndDialog.value = false
                                onValueChange(projectTable.copy(arhive = "1", dateEnd = dateEnd))
                                saveInProject(
                                    IncubatorAnimalInProject(
                                        AnimalTable(
                                            name = projectTable.titleProject,
                                            type = projectTable.type,
                                            data = dateEnd,
                                            groop = true,
                                            sex = "Мужской",
                                            note = "",
                                            image = "",
                                            arhiv = false,
                                            idPT = idProject
                                        ), count = projectTable.eggAllEND
                                    )
                                )
                            },
                            modifier = Modifier.padding(4.dp),
                        ) {
                            Text("В проект")
                        }
                    }

                    TextButton(
                        onClick = {
                            openEndDialog.value = false
                            save(projectTable.toProjectTable())

                            onValueChange(
                                projectTable.copy(
                                    id = 0,
                                    arhive = "0",
                                    dateEnd = dateEnd,
                                    mode = 1
                                )
                            )
                            saveInNewProject(
                                IncubatorAnimalInProject(
                                    AnimalTable(
                                        name = projectTable.titleProject,
                                        type = projectTable.type,
                                        data = dateEnd,
                                        groop = true,
                                        sex = "Мужской",
                                        note = "",
                                        image = "",
                                        arhiv = false,
                                        idPT = idProject
                                    ),
                                    projectTable.eggAllEND
                                )
                            )

                        },
                        modifier = Modifier.padding(4.dp),
                    ) {
                        Text("Новый проект")
                    }


                }

            } else {
                Text(
                    "Завершить ${projectTable.titleProject}?",
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
                    fontSize = 19.sp
                )
                Text(
                    "Вы уверены, что хотите завершить ${projectTable.titleProject}? Еще слишком рано завершать, удалим или добавим в архив?",
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 10.dp)
                )
                Spacer(modifier = Modifier.padding(vertical = 10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {

                    TextButton(
                        onClick = {
                            openEndDialog.value = false
                            deleteInc()
                        },
                        modifier = Modifier.padding(8.dp),

                        ) {
                        Text("Удалить")
                    }

                    TextButton(
                        onClick = {
                            openEndDialog.value = false
                            onValueChange(projectTable.copy(arhive = "1", dateEnd = dateEnd))
                            saveInArh()
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("В архив")
                    }
                }
            }
        }
    }
}


fun setOvoskop(typeBird: String, day: Int): Boolean {
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

data class IncubatorOvosNav(
    val day: Int,
    val typeBirds: String
)

data class IncubatorAnimalInProject(
    val animalTable: AnimalTable,
    val count: String
)