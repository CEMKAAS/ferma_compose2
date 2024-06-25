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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesTableUiState
import com.zaroslikov.fermacompose2.ui.finance.FinTit
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.add.DatePickerDialogSample
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

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

    Scaffold(modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        TopAppBarEdit(title = viewModel.indicators, navigateUp = navigateBack)
    }, floatingActionButton = {
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
    }) { innerPadding ->
        AnimalIndicatorsBody(
            id = viewModel.itemId,
            indicators = viewModel.indicators,
            itemList = indicatorsList.value.itemList,
            itemVaccinationList = vaccinationList.value.itemList,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
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
            deleteVaccinationt = { coroutineScope.launch {
                viewModel.deleteVaccinationt()
            }},
            editVaccinationtUiState = vacUiTable,
            onValueChangeVaccinationt =  viewModel::updatevaccinationUiState
        )


    }
}

@Composable
private fun AnimalIndicatorsBody(
    id: Int,
    indicators: String,
    itemList: List<AnimalIndicatorsVM>,
    itemVaccinationList: List<AnimalVaccinationTable>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),

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

) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (itemVaccinationList.isNotEmpty()||itemList.isNotEmpty()) {
            LazyColumn(
                modifier = modifier, contentPadding = contentPadding
            ) {
                if (indicators == "Прививки") {
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
                    items(items = itemList) { item ->
                        AnimalIndicatorsCard(
                            indicators = item, modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    onValueChange(item)
                                    editBottomSheet.value = true
                                }
                        )

                    }
                }
            }
        }else{
            Text(
                text = ("Нет данных по $indicators"),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )

        }

        if (editBottomSheet.value) {
            if (indicators == "Прививки") {
                EditVacIndicatorsBottomSheet(
                    animalVaccinationTable = editVaccinationtUiState,
                    editBottomSheet = editBottomSheet,
                    updateInTable = updateVaccinationt,
                    deleteInTable = deleteVaccinationt
                )
            }else{
                EditdIndicatorsBottomSheet(
                    animalIndicatorsVM = editUiState,
                    editBottomSheet = editBottomSheet,
                    updateInTable = updateInTable,
                    deleteInTable = deleteInTable
                )
            }
        }

        if (addBottomSheet.value) {
            AddIndicatorsBottomSheet(
                id = id,
                indicators = indicators,
                addBottomSheet = addBottomSheet,
                saveInTable = saveInTable,
                saveVaccinationt = saveVaccinationt
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
    deleteInTable: () -> Unit
) {

    ModalBottomSheet(onDismissRequest = { editBottomSheet.value = false }) {
        var count by rememberSaveable { mutableStateOf(animalVaccinationTable.vaccination) }
        var date1 by rememberSaveable { mutableStateOf(animalVaccinationTable.date) }
        var date2 by rememberSaveable { mutableStateOf(animalVaccinationTable.nextVaccination) }

        //Дата
        var openDialog by remember { mutableStateOf(false) }
        var openDialog2 by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()

        if (openDialog) {
            DatePickerDialogSample(datePickerState, date1) { date ->
                date1 = date
                openDialog = false
            }
        }

        if (openDialog2) {
            DatePickerDialogSample(datePickerState, date2) { date ->
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
                label = { Text("Товар") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                supportingText = {
                    if (isErrorCount) {
                        Text(
                            text = "Не указано кол-во товара",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Укажите кол-во товара, которое хотите сохранить на склад")
                    }
                },
                suffix = {
                    Text(text = "Кг")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    .padding(bottom = 2.dp),
            )

            OutlinedTextField(
                value = date2,
                onValueChange = {},
                readOnly = true,
                label = { Text("Дата") },
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
                    .padding(bottom = 2.dp),
            )

            Button(
                onClick = {
                    updateInTable(
                        AnimalVaccinationTable(
                            id =  animalVaccinationTable.id,
                            vaccination = count,
                            date = date1,
                            nextVaccination = date2,
                            idAnimal =  animalVaccinationTable.idAnimal
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
                },     modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
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
fun EditdIndicatorsBottomSheet(
    animalIndicatorsVM: AnimalIndicatorsVM,
    editBottomSheet: MutableState<Boolean>,
    updateInTable: (AnimalIndicatorsVM) -> Unit,
    deleteInTable: () -> Unit
) {

    ModalBottomSheet(onDismissRequest = { editBottomSheet.value = false }) {
        var count by rememberSaveable { mutableStateOf(animalIndicatorsVM.weight) }
        var date1 by rememberSaveable { mutableStateOf(animalIndicatorsVM.date) }

        //Дата
        var openDialog by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()

        if (openDialog) {
            DatePickerDialogSample(datePickerState, date1) { date ->
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
                label = { Text("Товар") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                supportingText = {
                    if (isErrorCount) {
                        Text(
                            text = "Не указано кол-во товара",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Укажите кол-во товара, которое хотите сохранить на склад")
                    }
                },
                suffix = {
                    Text(text = "Кг")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    .padding(bottom = 2.dp),
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
                },     modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
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
    indicators: String,
    addBottomSheet: MutableState<Boolean>,
    saveInTable: (AnimalIndicatorsVM) -> Unit,
    saveVaccinationt: (AnimalVaccinationTable) -> Unit,
) {

    ModalBottomSheet(onDismissRequest = { addBottomSheet.value = false }) {
        val format = SimpleDateFormat("dd.MM.yyyy")
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val formattedDate: String = format.format(calendar.timeInMillis)

        var count by rememberSaveable { mutableStateOf("") }
        var date1 by rememberSaveable { mutableStateOf(formattedDate) }
        var date2 by rememberSaveable { mutableStateOf(formattedDate) }

        //Дата
        var openDialog by remember { mutableStateOf(false) }
        var openDialog2 by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()

        if (openDialog) {
            DatePickerDialogSample(datePickerState, date1) { date ->
                date1 = date
                openDialog = false
            }
        }

        if (openDialog2) {
            DatePickerDialogSample(datePickerState, date2) { date ->
                date2 = date
                openDialog = false
            }
        }


        var isErrorCount by rememberSaveable { mutableStateOf(false) }

        fun validateCount(text: String) {
            isErrorCount = text == ""
        }


        val keyboardOptions = if (indicators == "Прививки") {
            KeyboardOptions(keyboardType = KeyboardType.Text)
        } else {
            KeyboardOptions(keyboardType = KeyboardType.Number)
        }

        Column(modifier = Modifier.padding(5.dp, 5.dp)) {

            OutlinedTextField(
                value = count,
                onValueChange = {
                    count = it
                    validateCount(count)
                },
                label = { Text("Товар") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                supportingText = {
                    if (isErrorCount) {
                        Text(
                            text = "Не указано кол-во товара",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Укажите кол-во товара, которое хотите сохранить на склад")
                    }
                },
                suffix = {
                    Text(text = "Кг")
                },
                keyboardOptions = keyboardOptions,
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
                    .padding(bottom = 2.dp),
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
                        .padding(bottom = 2.dp),
                )

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
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
                            AnimalIndicatorsVM(id = 0, weight = count, date = date1, idAnimal = id)
                        )
                    }
                    addBottomSheet.value = false
                }) {
                    Text(text = "Добавить")
                }
            }
        }
    }
}


@Composable
fun AnimalIndicatorsCard(
    indicators: AnimalIndicatorsVM, modifier: Modifier = Modifier

) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = indicators.weight,
                modifier = Modifier.padding(6.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = indicators.date,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(6.dp),
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
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = animalVaccinationTable.vaccination,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(6.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            Text(
                text = animalVaccinationTable.date,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
                    .wrapContentSize(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Text(
                text = animalVaccinationTable.nextVaccination,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
                    .wrapContentSize(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }
    }
}

