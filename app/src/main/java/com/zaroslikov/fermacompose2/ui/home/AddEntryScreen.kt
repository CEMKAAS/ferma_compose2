@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.supportFun.TripleData
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorAdd
import com.zaroslikov.fermacompose2.supportFun.isErrorSlash
import com.zaroslikov.fermacompose2.supportFun.metricaAdd
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.supportFun.toastShort
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextAnimal
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCategory
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleAdd
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import kotlinx.coroutines.launch


object AddEntryDestination : NavigationDestination {
    override val route = "AddEntry"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}


@Composable
fun AddEntryProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: AddEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val titleUiState by viewModel.titleUiState.collectAsState()
    val categoryUiState by viewModel.categoryUiState.collectAsState()
    val animalUiState by viewModel.animalUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarBack(intRes = R.string.add_screen_title, navigateUp = navigateBack)
        }
    ) { innerPadding ->

        AddEntryContainerProduct(
            modifier = Modifier
                .modifierScreen(innerPadding)
                .verticalScroll(rememberScrollState()),
            titleList = titleUiState.list,
            categoryList = categoryUiState.list,
            animalList = animalUiState.animalList,
            saveInRoomAdd = {
                coroutineScope.launch {
                    viewModel.saveItem(it)
                }
                onNavigateUp()
            },
            countWarehouse = viewModel.itemUiState,
            idProject = viewModel.itemId,
            updateCountWarehouse = {
                viewModel.updateUiState(it)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryContainerProduct(
    modifier: Modifier,
    titleList: List<String>,
    categoryList: List<String>,
    animalList: List<TripleData>,
    saveInRoomAdd: (AddTable) -> Unit,
    idProject: Int, // TODO convet to Long
    countWarehouse: DomainPairDataDoubleSting,
    updateCountWarehouse: (String) -> Unit
) {

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    //Values
    var title by remember { mutableStateOf("") }
    var count by rememberSaveable { mutableStateOf("") }
    var date by remember { mutableStateOf(dateToday()) }
    var category by remember { mutableStateOf("Без категории") }
    var suffix by remember { mutableStateOf("Шт.") }
    var animal by remember { mutableStateOf("") }
    var selectedAnimalIndex by remember { mutableIntStateOf(0) }
    var note by remember { mutableStateOf("") }

    //Error
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorSlash by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }

    val toastText = stringResource(R.string.toast_add_s, "$title $count $suffix")

    //Date
//    val state = rememberDatePickerState(
//        selectableDates = PastOrPresentSelectableDates,
//        initialSelectedDateMillis = Instant.now().toEpochMilli()
//    )
//
//    if (openDialog) {
//        DatePickerDialogSample(state, date) {
//            date = it
//            openDialog = !openDialog
//        }
//    }

    Column(modifier = modifier) {

        OutlinedTextTitleAdd(
            value = title,
            onValueChange = {
                title = it.trim()
                isErrorTitle = it.isError()
                isErrorSlash = it.isErrorSlash()
                updateCountWarehouse(title)
            },
            titleList = titleList,
            isErrorTitle = isErrorTitle,
            isErrorSlash = isErrorSlash,
            focusManager = focusManager
        )

        OutlinedTextCount(
            value = count,
            onValueChange = {
                count = it.toConvertDb()
                isErrorCount = it.isError()
            },
            onClick = { suffix = it },
            isError = isErrorCount,
            suffix = suffix,
            intResSup = R.string.support_text_count_product,
            countWarehouse = countWarehouse.first,
            countWarehouseSuffix = countWarehouse.second,
            focusManager = focusManager
        )

        OutlinedTextCategory(
            value = category,
            onValueChange = { category = it },
            titleList = categoryList,
            focusManager = focusManager
        )

        OutlinedTextDate(
            value = date,
            onValueChange = { date = it }
        )

        if (animalList.isNotEmpty()) {
            OutlinedTextAnimal(
                value = animal,
                onValueChange = {
                    selectedAnimalIndex = it.first
                    animal = animalList[selectedAnimalIndex].second
                },
                selectedAnimalIndex = selectedAnimalIndex,
                onClickClear = { animal = it },
                animalList = animalList,
                focusManager = focusManager
            )
        }

        OutlinedTextNote(
            value = note,
            onValueChange = { note = it },
            focusManager = focusManager
        )

        ButtonStandart(
            intRes = R.string.button_add,
            onClick = {
                if (isErrorAdd(title = title, count = count,
                        isErrorTitle = { isErrorTitle = it },
                        isErrorCount = { isErrorCount = it },
                        isErrorSlash = { isErrorSlash = it })
                ) {
                    focusManager.clearFocus()
                    val formattedDateList = date.split(".")
                    saveInRoomAdd(
                        AddTable(
                            title = title,
                            count = count.toDouble(),
                            day = formattedDateList[0].toInt(),
                            mount = formattedDateList[1].toInt(),
                            year = formattedDateList[2].toInt(),
                            suffix = suffix,
                            category = category,
                            idAnimal = if (animal != "") animalList[selectedAnimalIndex].first else 0,
                            animal = animal,
                            note = note,
                            priceAll = 0.0,
                            idPT = idProject
                        )
                    )
                    toastShort(
                        context = context,
                        text = toastText
                    )
                    metricaAdd(
                        title, count, category, animal, note
                    )
                }
            }
        )
    }
}



