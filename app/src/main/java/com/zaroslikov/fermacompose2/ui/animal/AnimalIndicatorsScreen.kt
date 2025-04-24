package com.zaroslikov.fermacompose2.ui.animal


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.Domain.models.DomainIndicatorsVM
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.formatDateToLong
import com.zaroslikov.fermacompose2.supportFun.getVersionToButtonMessage
import com.zaroslikov.fermacompose2.supportFun.getVersionToImage
import com.zaroslikov.fermacompose2.supportFun.getVersionToMessageNoMessage
import com.zaroslikov.fermacompose2.supportFun.getVersionToStringError
import com.zaroslikov.fermacompose2.supportFun.getVersionToStringSuffix
import com.zaroslikov.fermacompose2.supportFun.getVersionToStringSup
import com.zaroslikov.fermacompose2.supportFun.getVersionToStringTitle
import com.zaroslikov.fermacompose2.supportFun.getVersionToSupportNoMessage
import com.zaroslikov.fermacompose2.supportFun.getVersionToTitleNoMessage
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorVersion
import com.zaroslikov.fermacompose2.supportFun.keyboardActionsClear
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNext
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNextNumber
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSampleNoLimit
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonDelete
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.CircularProgress
import com.zaroslikov.fermacompose2.ui.composeElement.FloatButton
import com.zaroslikov.fermacompose2.ui.composeElement.MessageNoData
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierBottomSheet
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_16
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_18
import kotlinx.coroutines.launch
import java.time.Instant


object AnimalIndicatorsDestination : NavigationDestination {
    override val route = "FinanceCategory"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemCategory"
    val routeWithArgs = "${AnimalIndicatorsDestination.route}/{$itemIdArg}/{$itemIdArgTwo}"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalIndicatorsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnimalIndicatorsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val isLoading by viewModel.isLoading.collectAsState()
    val indicatorsList = viewModel.indicatorsUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val addBottomSheet = remember { mutableStateOf(false) }
    val editBottomSheet = remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = getVersionToStringTitle(viewModel.indicators),
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = { FloatButton { addBottomSheet.value = true } }
    ) { innerPadding ->
        if (isLoading) {
            CircularProgress(
                modifier = modifier.padding(innerPadding),
            )
        } else {
            AnimalIndicatorsBody(
                modifier = Modifier
                    .modifierScreen(innerPadding),
                idAnimal = viewModel.itemId.toLong(),
                version = viewModel.indicators,
                itemList = indicatorsList.value.itemList,
                animalUiState = viewModel.animalUiState,
                addBottomSheet = addBottomSheet,
                editBottomSheet = editBottomSheet,
                saveInTable = {
                    coroutineScope.launch {
                        viewModel.saveItem(it)
                    }
                },
                updateInTable = {
                    coroutineScope.launch {
                        viewModel.updateItem(it)
                    }
                    editBottomSheet.value = !editBottomSheet.value
                },
                deleteInTable = {
                    coroutineScope.launch {
                        viewModel.deleteItem(it)
                    }
                    editBottomSheet.value = !editBottomSheet.value
                },
                onValueChange = viewModel::updateUiState,
            )
        }
    }
}


@Composable
private fun AnimalIndicatorsBody(
    modifier: Modifier = Modifier,
    idAnimal: Long,
    version: Int,
    animalUiState: DomainIndicatorsVM?,
    itemList: List<DomainIndicatorsVM>,

    addBottomSheet: MutableState<Boolean>,
    editBottomSheet: MutableState<Boolean>,

    saveInTable: (DomainIndicatorsVM) -> Unit,
    updateInTable: (DomainIndicatorsVM) -> Unit,
    deleteInTable: (DomainIndicatorsVM) -> Unit,

    onValueChange: (DomainIndicatorsVM) -> Unit = {},
) {

    VaccinationList(
        modifier = modifier,
        version = version,
        onValueChange = {
            onValueChange(it)
            editBottomSheet.value = !editBottomSheet.value
        },
        onAddClick = { addBottomSheet.value = !addBottomSheet.value },
        indicatorsList = itemList
    )

    if (addBottomSheet.value)
        IndicatorsBottomSheet(
            version = version,
            idAnimal = idAnimal,
            addBottomSheet = addBottomSheet,
            onSaveClick = { saveInTable(it) },
        )

    if (editBottomSheet.value)
        IndicatorsBottomSheet(
            version = version,
            idAnimal = idAnimal,
            domainIndicatorsVM = animalUiState,
            addBottomSheet = editBottomSheet,
            onUpdateClick = { updateInTable(it) },
            onDeleteClick = { deleteInTable(it) }
        )
}

@Composable
fun VaccinationList(
    modifier: Modifier,
    version: Int,
    onAddClick: () -> Unit,
    indicatorsList: List<DomainIndicatorsVM>,
    onValueChange: (DomainIndicatorsVM) -> Unit = {},
) {
    if (indicatorsList.isNotEmpty())
        LazyColumn(
            modifier = modifier
        ) {
            item { HeadingIndicators(version) }
            items(items = indicatorsList, key = { it.id }) {
                IndicatorsCard(
                    modifier = Modifier
                        .clickable {
                            onValueChange(it)
                        },
                    domainIndicatorsVM = it,
                    version = version
                )
            }
        } else MessageNoData(
        modifier = modifier,
        onClick = onAddClick,
        titleRes = getVersionToTitleNoMessage(version),
        messageRes = getVersionToMessageNoMessage(version),
        supportRes = getVersionToSupportNoMessage(version),
        buttonRes = getVersionToButtonMessage(version)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicatorsBottomSheet(
    version: Int,
    idAnimal: Long,
    addBottomSheet: MutableState<Boolean>,
    domainIndicatorsVM: DomainIndicatorsVM? = null,
    onSaveClick: (DomainIndicatorsVM) -> Unit = {},
    onUpdateClick: (DomainIndicatorsVM) -> Unit = {},
    onDeleteClick: (DomainIndicatorsVM) -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = { addBottomSheet.value = false },
        sheetState = sheetState
    ) {
        val focusManager = LocalFocusManager.current
        val suffixText = stringResource(getVersionToStringSuffix(version))
        var count by rememberSaveable { mutableStateOf(domainIndicatorsVM?.weight ?: "") }
        var suffix by rememberSaveable {
            mutableStateOf(
                if (version == 3) "" else domainIndicatorsVM?.suffix ?: suffixText
            )
        }
        var date by rememberSaveable { mutableStateOf(domainIndicatorsVM?.date ?: dateToday()) }
        var dateNext by rememberSaveable {
            mutableStateOf(
                if (version == 3) domainIndicatorsVM?.suffix ?: dateToday() else dateToday()
            )
        }
        var openDialog by remember { mutableStateOf(false) }
        var openDialogNext by remember { mutableStateOf(false) }
        var isErrorCount by rememberSaveable { mutableStateOf(false) }

        //Date
        val datePickerStateLimit = rememberDatePickerState(
            selectableDates = PastOrPresentSelectableDates,
            initialSelectedDateMillis = domainIndicatorsVM?.let { formatDateToLong(it.date) }
                ?: Instant.now()
                    .toEpochMilli()
        )
        val datePickerStateNoLimit =
            rememberDatePickerState(initialSelectedDateMillis = domainIndicatorsVM?.let {
                formatDateToLong(
                    if (version == 3) it.suffix else dateToday()
                )
            } ?: Instant.now().toEpochMilli())

        if (openDialog) {
            DatePickerDialogSample(datePickerStateLimit, date) {
                date = it
                openDialog = !openDialog
            }
        }
        if (openDialogNext) {
            DatePickerDialogSampleNoLimit(datePickerStateNoLimit, dateNext) {
                dateNext = it
                openDialogNext = !openDialogNext
            }
        }

        Column(modifier = Modifier.modifierBottomSheet()) {
            OutlinedTextCount(
                value = count,
                onValueChange = {
                    count = it
                    isErrorCount = it.isError()
                },
                onClick = { suffix = it },
                isWarehouseShow = false,
                intRes = getVersionToStringTitle(version),
                drawableRes = getVersionToImage(version),
                versionDropMenu = version,
                intResError = getVersionToStringError(version),
                isError = isErrorCount,
                suffix = suffix,
                intResSup = getVersionToStringSup(version),
                focusManager = focusManager,
                keyboardOptions = if (version == 3) keyboardOptionsNext() else keyboardOptionsNextNumber(),
                keyboardActions = keyboardActionsClear(focusManager)
            )
            OutlinedTextDate(
                value = date,
                intResSup = if (version == 3) R.string.support_text_data_vaccination else R.string.support_text_date,
                onValueChange = { openDialog = !openDialog }
            )
            if (version == 3)
                OutlinedTextDate(
                    value = dateNext,
                    intRes = R.string.outlined_text_date_next,
                    intResSup = R.string.support_text_data_next,
                    onValueChange = { openDialogNext = !openDialogNext }
                )
            if (domainIndicatorsVM == null) {
                ButtonStandart(
                    intRes = R.string.button_insert,
                    onClick = {
                        if (isErrorVersion(
                                title = count,
                                isErrorTitle = { isErrorCount = it }
                            )
                        ) {
                            focusManager.clearFocus()
                            onSaveClick(
                                DomainIndicatorsVM(
                                    weight = count,
                                    date = date,
                                    suffix = if (version == 3) dateNext else suffix,
                                    idAnimal = idAnimal.toInt()
                                )
                            )
                            scope.launch {
                                sheetState.hide()
                                addBottomSheet.value = false
                            }
                        }
                    }
                )
            } else {
                ButtonRefresh {
                    if (isErrorVersion(
                            title = count,
                            isErrorTitle = { isErrorCount = it }
                        )
                    ) {
                        focusManager.clearFocus()
                        onUpdateClick(
                            domainIndicatorsVM.copy(
                                weight = count,
                                date = date,
                                suffix = if (version == 3) dateNext else suffix
                            )
                        )
                        scope.launch {
                            sheetState.hide()
                            addBottomSheet.value = false
                        }
                    }
                }
                ButtonDelete {
                    onDeleteClick(domainIndicatorsVM)
                    scope.launch {
                        sheetState.hide()
                        addBottomSheet.value = false
                    }
                }
            }
        }
    }
}


@Composable
fun IndicatorsCard(
    modifier: Modifier = Modifier,
    version: Int,
    domainIndicatorsVM: DomainIndicatorsVM
) {
    CardField(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = if (version == 3) domainIndicatorsVM.weight else "${domainIndicatorsVM.weight.toFormatNumber()} ${domainIndicatorsVM.suffix}",
                style = textBold_16,
            )
            Text(
                modifier = Modifier.weight(1f),
                text = domainIndicatorsVM.date,
                style = textBold_16,
                textAlign = TextAlign.Center
            )
            if (version == 3)
                Text(
                    modifier = Modifier.weight(1f),
                    text = domainIndicatorsVM.suffix,
                    style = textBold_16,
                    textAlign = TextAlign.Center
                )
        }
    }
}


@Composable
fun HeadingIndicators(version: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(getVersionToStringTitle(version)),
            style = textBold_18,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.outlined_text_date),
            style = textBold_18,
            textAlign = TextAlign.Center
        )
        if (version == 3)
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.support_text_data_next_vaccination),
                style = textBold_18,
                textAlign = TextAlign.Center
            )
    }
}
