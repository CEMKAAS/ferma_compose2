package com.zaroslikov.fermacompose2.ui.incubator

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.MainActivity
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import io.appmetrica.analytics.AppMetrica
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
    val activity = LocalContext.current as Activity
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
        }
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
                    (activity as MainActivity)?.showAd()
                }
            },
            saveInNewProject = {
                coroutineScope.launch {
                    viewModel.saveNewProject(it.animalTable, it.count)
                    navigateStart()
                    (activity as MainActivity)?.showAd()
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
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var idProject by remember { mutableIntStateOf(1) }
    fun validateCount(text: String) {
        isErrorCount = text == ""
    }
    fun errorBoolean(): Boolean {
        isErrorCount = projectTable.eggAllEND == ""
        return !isErrorCount
    }

    Dialog(
        onDismissRequest = { openEndDialog.value = false },
    ) {
        Card(
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
                        fontSize = 19.sp, fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Мы сохранили инкубатор в архив, чтобы вы не забыли параметры!\nТакже Вы можете добавить птенцов в существующий проект или создать новый для дальнейшей работы.\n"
                                + "Вы заложили ${projectTable.eggAll} яиц.\nСколько птенцов у Вас вылупилось?",
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 10.dp),
                        fontSize = 15.sp,

                        )
                    OutlinedTextField(
                        value = projectTable.eggAllEND,
                        onValueChange = {
                            onValueChange(
                                projectTable.copy(
                                    eggAllEND = it.replace(
                                        Regex("[^\\d.]"),
                                        ""
                                    ).replace(",", ".")
                                )
                            )
                            validateCount(projectTable.eggAllEND)
                        },
                        label = { Text("Кол-во птенцов") },
                        modifier = Modifier.padding(top = 5.dp, bottom = 10.dp),
                        suffix = { Text(text = "Шт.") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        supportingText = {
                            if (isErrorCount) {
                                Text(
                                    text = "Не указано кол-во яиц",
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text("Укажите кол-во яиц, которых заложили в инкубатор")
                            }
                        },
                        isError = isErrorCount,
                    )

                    if (projectBoolean) {

                        val (selectedOption, onOptionSelected) = remember {
                            mutableStateOf(
                                projectList[0]
                            )
                        }
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
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (text == selectedOption),
                                        onClick = null
                                    )
                                    Text(
                                        text.titleProject,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
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
                                    if (errorBoolean()) {
                                        openEndDialog.value = false
                                        onValueChange(
                                            projectTable.copy(
                                                arhive = "1",
                                                dateEnd = dateEnd
                                            )
                                        )
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
                                        val eventParameters: MutableMap<String, Any> = HashMap()
                                        eventParameters["Тип"] = projectTable.type
                                        eventParameters["Кол-во"] = "${projectTable.eggAll} ${projectTable.eggAllEND}"
                                        eventParameters["В Проект"] = "Да"
                                        AppMetrica.reportEvent("End Incubator", eventParameters);

                                    }
                                },
                                modifier = Modifier.padding(4.dp),
                            ) {
                                Text("В проект")
                            }
                        }

                        TextButton(
                            onClick = {
                                if (errorBoolean()) {
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
                                    val eventParameters: MutableMap<String, Any> = HashMap()
                                    eventParameters["Тип"] = projectTable.type
                                    eventParameters["Кол-во"] = "${projectTable.eggAll} ${projectTable.eggAllEND}"
                                    eventParameters["В Проект"] = "Новый"
                                    AppMetrica.reportEvent("End Incubator", eventParameters);

                                }
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
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Сейчас еще рано,чтобы завершать инкубатор.\nВы можете удалить или добавить в архив, если Вам в будующем нужны будут эти данные",
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 10.dp),
                        fontSize = 15.sp,
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