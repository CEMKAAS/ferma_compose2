package com.zaroslikov.fermacompose2.ui.animal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.incubator.endInc
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSampleNoLimit
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.start.dateLong
import com.zaroslikov.fermacompose2.ui.start.formatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object AnimalIndicatorsDestination : NavigationDestination {
    override val route = "FinanceCategory"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemCategory"
    val routeWithArgs = "${AnimalIndicatorsDestination.route}/{$itemIdArg}/{$itemIdArgTwo}"
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalIndicatorsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnimalIndicatorsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val indicatorsList = viewModel.indicatorsUiState.collectAsState()
    val vaccinationList = viewModel.vaccinationtUiState.collectAsState()

    val editUiState = viewModel.animalUiState
    val vacUiTable = viewModel.vaccinationState

    val coroutineScope = rememberCoroutineScope()
    val addBottomSheet = remember { mutableStateOf(false) }
    val editBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )


    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarEdit(title = viewModel.indicators, navigateUp = navigateBack)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { addBottomSheet.value = true },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(
                    end = WindowInsets.safeDrawing.asPaddingValues()
                        .calculateEndPadding(LocalLayoutDirection.current)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.item_entry_title) // TODO Преименовать
                )
            }
        }
    ) { innerPadding ->
        AnimalIndicatorsBody(
            id = viewModel.itemId,
            indicators = viewModel.indicators,
            itemList = indicatorsList.value.itemList,
            itemVaccinationList = vaccinationList.value.itemList,
            modifier = modifier.padding(innerPadding),
            addBottomSheet = addBottomSheet,
            editBottomSheet = editBottomSheet,
            saveInTable = {
                coroutineScope.launch {
                    viewModel.saveItem(it)
                }
            },
            editUiState = editUiState,
            saveVaccinationt = {
                coroutineScope.launch {
                    viewModel.saveVaccinationt(it)
                }
            },
            updateInTable = {
                coroutineScope.launch {
                    viewModel.updateItem(it)
                }
            },
            onValueChange = viewModel::updateUiState,
            deleteInTable = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                }
            },
            updateVaccinationt = {
                coroutineScope.launch {
                    viewModel.updateVaccinationt(it)
                }
            },
            deleteVaccinationt = {
                coroutineScope.launch {
                    viewModel.deleteVaccinationt()
                }
            },
            editVaccinationtUiState = vacUiTable,
            onValueChangeVaccinationt = viewModel::updatevaccinationUiState,
            sheetState = sheetState
        )


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimalIndicatorsBody(
    id: Int,
    indicators: String,
    itemList: List<AnimalIndicatorsVM>,
    itemVaccinationList: List<AnimalVaccinationTable>,
    modifier: Modifier = Modifier,

    addBottomSheet: MutableState<Boolean>,
    editBottomSheet: MutableState<Boolean>,

    saveInTable: (AnimalIndicatorsVM) -> Unit,
    updateInTable: (AnimalIndicatorsVM) -> Unit,
    deleteInTable: () -> Unit,

    saveVaccinationt: (AnimalVaccinationTable) -> Unit,
    updateVaccinationt: (AnimalVaccinationTable) -> Unit,
    deleteVaccinationt: () -> Unit,

    editUiState: AnimalIndicatorsVM,
    onValueChange: (AnimalIndicatorsVM) -> Unit = {},

    editVaccinationtUiState: AnimalVaccinationTable,
    onValueChangeVaccinationt: (AnimalVaccinationTable) -> Unit = {},

    sheetState:SheetState

) {

    val suff = when (indicators) {
        "Количество" -> "шт."
        "Вес" -> "кг."
        "Размер" -> "м."

        else -> {
            ""
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {

//        Text(
//            text = ("Нет данных в категории $indicators\nНажмите + чтобы добавить."),
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.titleLarge,
//            modifier = Modifier.padding(contentPadding),
//        )

        LazyColumn {
            if (indicators == "Прививки") {
                if (itemVaccinationList.isNotEmpty()) {
                    item { HeadingIndicators(indicators = indicators) }
                    items(items = itemVaccinationList) { item ->
                        AnimalVaccinatioCard(
                            animalVaccinationTable = item,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    onValueChangeVaccinationt(item)
                                    editBottomSheet.value = true
                                }
                        )
                    }
                } else {
                    item {
                        Text(
                            text = "Здесь Вы можете ввести информацию о прививках вашего животного\nНажмите на кнопку «+», чтобы добавить данные.",
                            modifier = Modifier.fillMaxWidth().padding(1.dp),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                if (itemList.isNotEmpty()) {
                    item { HeadingIndicators(indicators = indicators) }
                    items(items = itemList) { item ->
                        AnimalIndicatorsCard(
                            indicators = suff,
                            indicatorsData = item, modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    onValueChange(item)
                                    editBottomSheet.value = true
                                }
                        )
                    }
                } else {
                    item {
                        Text(
                            text = "Введите информацию о вашем животном.\nНажмите на кнопку «+», чтобы добавить данные.",
                            modifier = Modifier.fillMaxWidth().padding(1.dp),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        if (editBottomSheet.value) {
            if (indicators == "Прививки") {
                EditVacIndicatorsBottomSheet(
                    animalVaccinationTable = editVaccinationtUiState,
                    editBottomSheet = editBottomSheet,
                    updateInTable = updateVaccinationt,
                    deleteInTable = deleteVaccinationt,
                    sheetState = sheetState
                )
            } else {
                EditIndicatorsBottomSheet(
                    suff = suff,
                    indicators = indicators,
                    animalIndicatorsVM = editUiState,
                    editBottomSheet = editBottomSheet,
                    updateInTable = updateInTable,
                    deleteInTable = deleteInTable,
                    sheetState = sheetState
                )
            }
        }

        if (addBottomSheet.value) {
            AddIndicatorsBottomSheet(
                id = id,
                suff = suff,
                indicators = indicators,
                addBottomSheet = addBottomSheet,
                saveInTable = saveInTable,
                saveVaccinationt = saveVaccinationt,
                sheetState = sheetState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVacIndicatorsBottomSheet(
    animalVaccinationTable: AnimalVaccinationTable,
    editBottomSheet: MutableState<Boolean>,
    updateInTable: (AnimalVaccinationTable) -> Unit,
    deleteInTable: () -> Unit,
    sheetState: SheetState
) {

    ModalBottomSheet(onDismissRequest = { editBottomSheet.value = false },
        sheetState = sheetState ) {
        var count by rememberSaveable { mutableStateOf(animalVaccinationTable.vaccination) }
        var date1 by rememberSaveable { mutableStateOf(animalVaccinationTable.date) }
        var date2 by rememberSaveable { mutableStateOf(animalVaccinationTable.nextVaccination) }

        //Дата
        var openDialog by remember { mutableStateOf(false) }
        var openDialog2 by remember { mutableStateOf(false) }

        val datePickerStateLimit = rememberDatePickerState(
            selectableDates = PastOrPresentSelectableDates,
            initialSelectedDateMillis = dateLong(date1)
        )
        val datePickerStateNoLimit = rememberDatePickerState(
            initialSelectedDateMillis = dateLong(date2)
        )

        if (openDialog) {
            DatePickerDialogSample(datePickerStateLimit, date1) { date ->
                date1 = date
                openDialog = false
            }
        }

        if (openDialog2) {
            DatePickerDialogSampleNoLimit(datePickerStateNoLimit, date2) { date ->
                date2 = date
                openDialog2 = false
            }
        }

        var isErrorCount by rememberSaveable { mutableStateOf(false) }

        fun validateCount(text: String) {
            isErrorCount = text == ""
        }

        Column(modifier = Modifier.padding(5.dp, 5.dp)) {

            OutlinedTextField(
                value = count,
                onValueChange = {
                    count = it
                    validateCount(count)
                },
                label = { Text("Название прививки") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                supportingText = {
                    if (isErrorCount) {
                        Text(
                            text = "Не указана название прививки",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Укажите название прививки")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                isError = isErrorCount
            )

            OutlinedTextField(
                value = date1,
                onValueChange = {},
                readOnly = true,
                label = { Text("Дата") },
                supportingText = {
                    Text("Выберите вакцинации")
                },
                trailingIcon = {
                    IconButton(onClick = { openDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_calendar_month_24),
                            contentDescription = "Показать меню"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        openDialog = true
                    }
                    .padding(bottom = 10.dp),
            )

            OutlinedTextField(
                value = date2,
                onValueChange = {},
                readOnly = true,
                label = { Text("Дата следующей ") },
                supportingText = {
                    Text("Выберите дату следующей вакцинации")
                },
                trailingIcon = {
                    IconButton(onClick = { openDialog2 = true }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_calendar_month_24),
                            contentDescription = "Показать меню"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        openDialog2 = true
                    }
                    .padding(bottom = 10.dp),
            )

            Button(
                onClick = {
                    updateInTable(
                        AnimalVaccinationTable(
                            id = animalVaccinationTable.id,
                            vaccination = count,
                            date = date1,
                            nextVaccination = date2,
                            idAnimal = animalVaccinationTable.idAnimal
                        )
                    )
                    editBottomSheet.value = false
                }, modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_create_24),
                    contentDescription = " Обновить "
                )
                Text(text = " Обновить ")
            }

            OutlinedButton(
                onClick = {
                    deleteInTable()
                    editBottomSheet.value = false
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 35.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_delete_24),
                    contentDescription = "Удалить"
                )
                Text(text = " Удалить ")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIndicatorsBottomSheet(
    suff: String,
    indicators: String,
    animalIndicatorsVM: AnimalIndicatorsVM,
    editBottomSheet: MutableState<Boolean>,
    updateInTable: (AnimalIndicatorsVM) -> Unit,
    deleteInTable: () -> Unit,
    sheetState: SheetState
) {

    ModalBottomSheet(onDismissRequest = { editBottomSheet.value = false },
        sheetState = sheetState ) {
        var count by rememberSaveable { mutableStateOf(animalIndicatorsVM.weight) }
        var date1 by rememberSaveable { mutableStateOf(animalIndicatorsVM.date) }

        //Дата
        var openDialog by remember { mutableStateOf(false) }

        val datePickerStateLimit = rememberDatePickerState(
            selectableDates = PastOrPresentSelectableDates,
            initialSelectedDateMillis = dateLong(date1)
        )

        if (openDialog) {
            DatePickerDialogSample(datePickerStateLimit, date1) { date ->
                date1 = date
                openDialog = false
            }
        }

        var isErrorCount by rememberSaveable { mutableStateOf(false) }

        fun validateCount(text: String) {
            isErrorCount = text == ""
        }

        Column(modifier = Modifier.padding(5.dp, 5.dp)) {

            OutlinedTextField(
                value = count,
                onValueChange = {
                    count = it
                    validateCount(count)
                },
                label = { Text(indicators) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                supportingText = {
                    if (isErrorCount) {
                        Text(
                            text = "Не указано данные",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Укажите данные")
                    }
                },
                suffix = { Text(text = suff) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                isError = isErrorCount
            )

            OutlinedTextField(
                value = date1,
                onValueChange = {},
                readOnly = true,
                label = { Text("Дата") },
                supportingText = {
                    Text("Выберите дату ")
                },
                trailingIcon = {
                    IconButton(onClick = { openDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_calendar_month_24),
                            contentDescription = "Показать меню"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        openDialog = true
                    }
                    .padding(bottom = 10.dp),
            )


            Button(
                onClick = {
                    updateInTable(
                        AnimalIndicatorsVM(
                            id = animalIndicatorsVM.id,
                            weight = count,
                            date = date1,
                            idAnimal = animalIndicatorsVM.idAnimal
                        )
                    )
                    editBottomSheet.value = false
                }, modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_create_24),
                    contentDescription = " Обновить "
                )
                Text(text = " Обновить ")
            }

            OutlinedButton(
                onClick = {
                    deleteInTable()
                    editBottomSheet.value = false
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 35.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_delete_24),
                    contentDescription = "Удалить"
                )
                Text(text = " Удалить ")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIndicatorsBottomSheet(
    id: Int,
    suff: String,
    indicators: String,
    addBottomSheet: MutableState<Boolean>,
    saveInTable: (AnimalIndicatorsVM) -> Unit,
    saveVaccinationt: (AnimalVaccinationTable) -> Unit,
    sheetState: SheetState
) {

    ModalBottomSheet(onDismissRequest = { addBottomSheet.value = false },sheetState = sheetState) {
        val format = SimpleDateFormat("dd.MM.yyyy")
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val formattedDate: String = format.format(calendar.timeInMillis)

        var count by rememberSaveable { mutableStateOf("") }
        var date1 by rememberSaveable { mutableStateOf(formattedDate) }
        var date2 by rememberSaveable { mutableStateOf(formattedDate) }

        //Дата
        var openDialog by remember { mutableStateOf(false) }
        var openDialog2 by remember { mutableStateOf(false) }
        val datePickerStateLimit = rememberDatePickerState(
            selectableDates = PastOrPresentSelectableDates,
            initialSelectedDateMillis = calendar.timeInMillis
        )
        val datePickerStateNoLimit =
            rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)

        if (openDialog) {
            DatePickerDialogSample(datePickerStateLimit, date1) { date ->
                date1 = date
                openDialog = false
            }
        }

        if (openDialog2) {
            DatePickerDialogSampleNoLimit(datePickerStateNoLimit, date2) { date ->
                date2 = date
                openDialog2 = false
            }
        }

        var isErrorCount by rememberSaveable { mutableStateOf(false) }

        fun validateCount(text: String) {
            isErrorCount = text == ""
        }

        val keyboardOptions = if (indicators == "Прививки") {
            KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            )
        } else {
            KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number,
                capitalization = KeyboardCapitalization.Sentences
            )
        }

        Column(modifier = Modifier.padding(5.dp, 5.dp)) {

            OutlinedTextField(
                value = count,
                onValueChange = {
                    count = it
                    validateCount(count)
                },
                label = { Text(indicators) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                supportingText = {
                    if (isErrorCount) {
                        Text(
                            text = "Не указаны данные",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Укажите данные")
                    }
                },
                suffix = {
                    Text(text = suff)
                },
                keyboardOptions = keyboardOptions,
                isError = isErrorCount,
            )

            OutlinedTextField(
                value = date1,
                onValueChange = {},
                readOnly = true,
                label = { Text("Дата") },
                supportingText = {
                    Text("Выберите дату ")
                },
                trailingIcon = {
                    IconButton(onClick = { openDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_calendar_month_24),
                            contentDescription = "Показать меню"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        openDialog = true
                    }
                    .padding(bottom = 10.dp),
            )

            if (indicators == "Прививки") {
                OutlinedTextField(
                    value = date2,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Следующая прививка") },
                    supportingText = {
                        Text("Выберите дату ")
                    },
                    trailingIcon = {
                        IconButton(onClick = { openDialog2 = true }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_calendar_month_24),
                                contentDescription = "Показать меню"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            openDialog2 = true
                        }
                        .padding(bottom = 10.dp),
                )

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        if (indicators == "Прививки") {
                            saveVaccinationt(
                                AnimalVaccinationTable(
                                    id = 0,
                                    vaccination = count,
                                    date = date1,
                                    nextVaccination = date2,
                                    idAnimal = id
                                )
                            )
                        } else {
                            saveInTable(
                                AnimalIndicatorsVM(
                                    id = 0,
                                    weight = count,
                                    date = date1,
                                    idAnimal = id
                                )
                            )
                        }
                        addBottomSheet.value = false
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 35.dp)
                ) {
                    Text(text = "Добавить")
                }
            }
        }
    }
}


@Composable
fun AnimalIndicatorsCard(
    indicators: String,
    indicatorsData: AnimalIndicatorsVM, modifier: Modifier = Modifier

) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${formatter(indicatorsData.weight.toDouble())} $indicators",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = indicatorsData.date,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun AnimalVaccinatioCard(
    animalVaccinationTable: AnimalVaccinationTable, modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = animalVaccinationTable.vaccination,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(0.2f)
            )

            Text(
                text = animalVaccinationTable.date,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Text(
                text = animalVaccinationTable.nextVaccination,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun HeadingIndicators(indicators: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = indicators,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
        Text(
            text = "Дата",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
        if (indicators == "Прививки") {
            Text(
                text = "Дата\nследующей",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}
