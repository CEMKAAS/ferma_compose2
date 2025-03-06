package com.zaroslikov.fermacompose2.ui.writeOff

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.home.PairString
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.start.formatter
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

object WriteOffEntryDestination : NavigationDestination {
    override val route = "WriteOffEntry"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}


@Composable
fun WriteOffEntryProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: WriteOffEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val idProject = viewModel.itemId
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val titleUiState by viewModel.titleUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBarEdit(title = "Добавить Списания", navigateUp = navigateBack)
        }
    ) { innerPadding ->

        WriteOffEntryContainerProduct(
            modifier = Modifier
                .padding(innerPadding)
                .padding(5.dp)
                .verticalScroll(rememberScrollState()),
            titleList = titleUiState.animalList,
            saveInRoomSale = {
                coroutineScope.launch {
                    viewModel.saveItem(
                        WriteOffTable(
                            id = it.id,
                            title = it.title,
                            count = it.count,
                            day = it.day,
                            mount = it.mount,
                            year = it.year,
                            priceAll = it.priceAll,
                            suffix = it.suffix,
                            status = it.status,
                            idPT = idProject,
                            note = it.note
                        )
                    )
                    Toast.makeText(
                        context,
                        "Списано: ${it.title} ${it.count} ${it.suffix}",
                        Toast.LENGTH_SHORT
                    ).show()
                    onNavigateUp()
                }
            },
            countWarehouse = viewModel.itemUiState,
            updateCountWarehouse = {
                viewModel.updateUiState(it)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteOffEntryContainerProduct(
    modifier: Modifier,
    titleList: List<PairString>,
    saveInRoomSale: (WriteOffTableInsert) -> Unit,
    countWarehouse: Double,
    updateCountWarehouse: (Pair<String, Boolean>) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var count by rememberSaveable { mutableStateOf("") }
    var state by rememberSaveable { mutableStateOf(true) }
    var suffix by rememberSaveable { mutableStateOf("Шт.") }
    var priceAll by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }

    var expanded by rememberSaveable { mutableStateOf(false) }
    var expandedSuf by rememberSaveable { mutableStateOf(false) }

    var isErrorPrice by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    fun validatePrice(text: String) {
        isErrorPrice = text == ""
    }

    fun validateCount(text: String) {
        isErrorCount = text == ""
    }

    fun errorBoolean(): Boolean {
        isErrorPrice = title == ""
        isErrorCount = count == ""
        return !(isErrorCount || isErrorPrice)
    }

    //Календарь
    val format = SimpleDateFormat("dd.MM.yyyy")
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val formattedDate: String = format.format(calendar.timeInMillis)

    //Дата
    var openDialog by remember { mutableStateOf(false) }

    val stateDate = rememberDatePickerState(
        selectableDates = PastOrPresentSelectableDates,
        initialSelectedDateMillis = calendar.timeInMillis
    )

    var date1 by remember { mutableStateOf(formattedDate) }

    if (openDialog) {
        DatePickerDialogSample(stateDate, date1) { date ->
            date1 = date
            openDialog = false
        }
    }

    Column(modifier = modifier) {
        Box {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    label = { Text(text = "Товар") },
                    supportingText = {
                        Text("Выберите товар, который хотите списать")
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    titleList.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "${item.name} - ${item.type}",
                                    fontWeight = if (index == selectedItemIndex) FontWeight.Bold else null
                                )
                            },
                            onClick = {
                                selectedItemIndex = index
                                expanded = false
                                title = titleList[selectedItemIndex].name
                                updateCountWarehouse(Pair(title, if(titleList[selectedItemIndex].type=="Моя Продукция") true else false))
                            }
                        )
                    }
                }
            }
        }

        Text(text = "Сейчас на складе: ${formatter(countWarehouse)} $suffix", modifier = Modifier.padding(2.dp))
        
        Box {
            OutlinedTextField(
                value = count,
                onValueChange = {
                    count = it.replace(Regex("[^\\d.]"), "").replace(",", ".")
                    validateCount(count)
                },
                label = { Text("Количество") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                supportingText = {
                    if (isErrorCount) {
                        Text(
                            text = "Не указано кол-во товара",
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Укажите кол-во товара, которое хотите списать со склада")
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { expandedSuf = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Показать меню")
                    }
                },
                suffix = {
                    Text(text = suffix)
                },
                isError = isErrorCount,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(
                        FocusDirection.Down
                    )
                }
                )
            )
            DropdownMenu(
                expanded = expandedSuf,
                onDismissRequest = { expandedSuf = false },
                //todo чтобы был слева
            ) {
                DropdownMenuItem(
                    onClick = { suffix = "Шт." },
                    text = { Text("Шт.") }
                )
                DropdownMenuItem(
                    onClick = { suffix = "Кг." },
                    text = { Text("Кг.") }
                )
                DropdownMenuItem(
                    onClick = { suffix = "Л." },
                    text = { Text("Л.") }
                )
            }

        }

        OutlinedTextField(
            value = priceAll,
            onValueChange = {
                priceAll = it.replace(Regex("[^\\d.]"), "").replace(",", ".")
                validatePrice(it)
            },
            label = { Text("Стоимость") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            supportingText = {
                if (isErrorPrice) {
                    Text(
                        text = "Не указана стоимость за списанный товар",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text("Укажите стоимость за списанный товар")
                }
            },
            isError = isErrorPrice,
            suffix = { Text(text = "₽") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(
                    FocusDirection.Down
                )
            }
            )
        )

        OutlinedTextField(
            value = date1,
            onValueChange = {},
            readOnly = true,
            label = { Text("Дата") },
            supportingText = {
                Text("Выберите дату")
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
                .padding(bottom = 2.dp)
                .clickable {
                    openDialog = true
                }
        )

        OutlinedTextField(
            value = note,
            onValueChange = {
                note = it
            },
            label = { Text("Примечание") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            supportingText = {
                Text("Здесь может быть важная информация")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            )
        )


        Column(
            Modifier
                .selectableGroup()
                .fillMaxWidth()
                .padding(vertical = 10.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = state,
                    onClick = { state = true },
                    modifier = Modifier.semantics { contentDescription = "Localized Description" }
                )
                Text(text = "На собственные нужды")
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = !state,
                    onClick = { state = false },
                    modifier = Modifier.semantics { contentDescription = "Localized Description" },
                )
                Text(text = "На утилизацию")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (errorBoolean()) {
                        val formattedDateList = date1.split(".")

                        val status = if (state) 0
                        else 1

                        saveInRoomSale(
                            WriteOffTableInsert(
                                id = 0,
                                title = title,
                                count = count.replace(Regex("[^\\d.]"), "").replace(",", ".")
                                    .toDouble(),
                                formattedDateList[0].toInt(),
                                formattedDateList[1].toInt(),
                                formattedDateList[2].toInt(),
                                suffix = suffix,
                                priceAll = if (priceAll == "") 0.0 else priceAll.replace(Regex("[^\\d.]"), "").replace(",", ".")
                                    .toDouble(),
                                status = status,
                                note = note
                            )
                        )
                        val eventParameters: MutableMap<String, Any> = HashMap()
                        eventParameters["Имя"] = title
                        eventParameters["Кол-во"] = "$title $count $suffix $priceAll₽"
                        eventParameters["Статус"] = status
                        eventParameters["Примечание"] = note
                        AppMetrica.reportEvent("WriteOff Products", eventParameters);
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
            ) {
                Text(text = "Списать")
            }
        }
    }
}


data class WriteOffTableInsert(
    var id: Int,
    var title: String,
    var count: Double,
    var day: Int,
    var mount: Int,
    var year: Int,
    var status: Int,
    var priceAll: Double,
    var suffix: String,
    var note: String
)

