package com.zaroslikov.fermacompose2.ui.animal


import android.content.Context
import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.Domain.models.DomainIndicatorsVM
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.data.room.table.animal.AnimalTable
import com.zaroslikov.fermacompose2.supportFun.calculatePriceAll
import com.zaroslikov.fermacompose2.supportFun.convertSize
import com.zaroslikov.fermacompose2.supportFun.convertWeight
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.dateTodayNextYear
import com.zaroslikov.fermacompose2.supportFun.formatDateToLong
import com.zaroslikov.fermacompose2.supportFun.getImageAnimalCount
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
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNext
import com.zaroslikov.fermacompose2.supportFun.keyboardOptionsNextNumber
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.supportFun.toConvertDbOnlyInt
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroString
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroTooOneDouble
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.composeElement.AlertDialog.AlertDialogGroupToSolo
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonDelete
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.CardField
import com.zaroslikov.fermacompose2.ui.composeElement.CircularProgress
import com.zaroslikov.fermacompose2.ui.composeElement.FloatButton
import com.zaroslikov.fermacompose2.ui.composeElement.MessageNoData
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDateNoLimit
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierBottomSheet
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_16
import com.zaroslikov.fermacompose2.ui.composeElement.textBold_18
import com.zaroslikov.fermacompose2.ui.composeElement.textBuildIndicatorsAnnotated
import com.zaroslikov.fermacompose2.ui.composeElement.text_16
import com.zaroslikov.fermacompose2.ui.navigation.navNull
import com.zaroslikov.fermacompose2.ui.sections.expenses.entry.ExpensesEntryDestination
import com.zaroslikov.fermacompose2.ui.sections.sale.entry.SaleEntryDestination
import com.zaroslikov.fermacompose2.ui.sections.writeOff.entry.WriteOffEntryDestination
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


object AnimalIndicatorsDestination : NavigationDestination {
    override val route = "FinanceCategory"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemCategory"
    const val itemIdArgTree = "itemProject"
    val routeWithArgs =
        "$route/{$itemIdArg}/{$itemIdArgTwo}/{$itemIdArgTree}"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalIndicatorsScreen(
    navigateBack: () -> Unit,
    navigateSection: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnimalIndicatorsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val isLoading by viewModel.isLoading.collectAsState()
    val indicatorsList = viewModel.indicatorsUiState.collectAsState()

    val context = LocalContext.current
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
                    .modifierScreenLazy(innerPadding),
                idAnimal = viewModel.itemId.toLong(),
                version = viewModel.indicators,
                itemList = indicatorsList.value.itemList,
                indicationUiState = viewModel.indicationUiState,
                priceCount = viewModel.priceCount,
                animalUiState = viewModel.animalUiState,
                animalCountUiState = viewModel.animalCountUiState,
                addBottomSheet = addBottomSheet,
                editBottomSheet = editBottomSheet,
                saveInTable = {
                    viewModel.saveItem(it.first, it.second, it.third, context)
                },
                updateInTable = {
                    viewModel.updateItem(it.first, it.second, it.third, context)
                },
                updateAnimalGroup = {
                    coroutineScope.launch {
                        viewModel.updateAnimalGroup(it)
                        navigateBack()
                    }
                },
                deleteInTable = {
                    viewModel.deleteItem(it)
                    editBottomSheet.value = !editBottomSheet.value
                },
                onValueChange = {
                    if (it.version != null) {
                        navigateSection(navigate(it.version, it._id.toString(), it.idPT.toString()))
                    } else {
                        coroutineScope.launch {
                            viewModel.updateUiState(it)
                            editBottomSheet.value = !editBottomSheet.value
                        }
                    }
                },
            )
        }
    }
}

private fun navigate(version: Int, id: String, idPT: String): String {
    return when (version) {
        0 -> navNull(
            route = SaleEntryDestination.route,
            itemOne = idPT,
            itemTwo = id
        )

        1 -> navNull(
            route = ExpensesEntryDestination.route,
            itemOne = idPT,
            itemTwo = id
        )

        else -> navNull(
            route = WriteOffEntryDestination.route,
            itemOne = idPT,
            itemTwo = id
        )
    }
}

@Composable
private fun AnimalIndicatorsBody(
    modifier: Modifier = Modifier,
    idAnimal: Long,
    version: Int,
    indicationUiState: DomainIndicatorsVM?,
    priceCount: Pair<Double, Double>?,
    animalUiState: AnimalTable?,
    animalCountUiState: DomainIndicatorsVM?,
    itemList: List<DomainIndicatorsVM>,
    addBottomSheet: MutableState<Boolean>,
    editBottomSheet: MutableState<Boolean>,
    saveInTable: (Triple<DomainIndicatorsVM, String?, Double>) -> Unit,
    updateInTable: (Triple<DomainIndicatorsVM, String?, Double>) -> Unit,
    updateAnimalGroup: (String) -> Unit,
    deleteInTable: (DomainIndicatorsVM) -> Unit,
    onValueChange: (DomainIndicatorsVM) -> Unit = {},
) {
    var isLastCount by rememberSaveable { mutableStateOf(false) }
    var isFirstCount by rememberSaveable { mutableStateOf(false) }
    val previousItem = if (itemList.isEmpty()) null else itemList.first()
    println("previousItem: $previousItem")
    VaccinationList(
        modifier = modifier,
        version = version,
        onValueChange = {
            onValueChange(it.first)
            isLastCount = it.second
            isFirstCount = it.third
        },
        onAddClick = { addBottomSheet.value = !addBottomSheet.value },
        indicatorsList = itemList
    )
    if (addBottomSheet.value)
        IndicatorsBottomSheet(
            version = version,
            idAnimal = idAnimal,
            animalUiState = animalUiState,
            animalCountUiState = animalCountUiState,
            previousItem = previousItem,
            addBottomSheet = addBottomSheet,
            onSaveClick = saveInTable,
            onUpdateAnimalGroupClick = updateAnimalGroup
        )
    if (editBottomSheet.value)
        IndicatorsBottomSheet(
            version = version,
            idAnimal = idAnimal,
            isLastCount = isLastCount,
            isFirstCount = isFirstCount,
            animalCountUiState = animalCountUiState,
            indicationUiState = indicationUiState,
            priceCount = priceCount,
            animalUiState = animalUiState,
            addBottomSheet = editBottomSheet,
            onUpdateClick = updateInTable,
            onDeleteClick = deleteInTable,
            onUpdateAnimalGroupClick = updateAnimalGroup
        )
}

@Composable
fun VaccinationList(
    modifier: Modifier,
    version: Int,
    onAddClick: () -> Unit,
    indicatorsList: List<DomainIndicatorsVM>,
    onValueChange: (Triple<DomainIndicatorsVM, Boolean, Boolean>) -> Unit = {},
) {
    if (indicatorsList.isNotEmpty()) {

        val cumulativeList = if (version == 2) indicatorsList
            .asReversed()
            .runningReduce { acc, item ->
                Log.i("Sum", "Acc: ${acc.weight}  item: ${item.weight}")
                item.copy(weight = sum(item, acc))
            }
            .asReversed()
        else indicatorsList

        LazyColumn(
            modifier = modifier
        ) {
            item { HeadingIndicators(version) }
            itemsIndexed(items = cumulativeList, key = { index, _ -> index }) { index, item ->
                val previousItem =
                    if (index < cumulativeList.size - 1) cumulativeList[index + 1] else null
                IndicatorsCard(
                    modifier = Modifier
                        .clickable {
                            println("index: $index")
                            onValueChange(
                                Triple(item, index == (cumulativeList.size - 1), index == 0)
                            )
                        },
                    domainIndicatorsVM = item,
                    previousDomainIndicatorsVM = previousItem,
                    version = version
                )
            }
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
    isFirstCount: Boolean = false,
    isLastCount: Boolean = true,
    addBottomSheet: MutableState<Boolean>,
    indicationUiState: DomainIndicatorsVM? = null,
    animalCountUiState: DomainIndicatorsVM? = null,
    previousItem: DomainIndicatorsVM? = null,
    priceCount: Pair<Double, Double>? = null,
    animalUiState: AnimalTable? = null,
    onSaveClick: (Triple<DomainIndicatorsVM, String?, Double>) -> Unit = {},
    onUpdateClick: (Triple<DomainIndicatorsVM, String?, Double>) -> Unit = {},
    onUpdateAnimalGroupClick: (String) -> Unit,
    onDeleteClick: (DomainIndicatorsVM) -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var openDialogGroup by rememberSaveable { mutableStateOf(false) }
    println("version $version")
    ModalBottomSheet(
        onDismissRequest = { addBottomSheet.value = false },
        sheetState = sheetState
    ) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val focusManager = LocalFocusManager.current

        val suffixText = stringResource(getVersionToStringSuffix(version))
        val suffixFromDb = if (version == 3) "" else indicationUiState?.suffix ?: suffixText
        val priceFromDb = priceCount?.first?.toString() ?: ""
        val noteFromDb = indicationUiState?.note ?: ""
        val countFromDb = indicationUiState?.weight ?: ""
        val countAnimalFromDb = priceCount?.second?.toString() ?: ""
        val dateFromDb = indicationUiState?.date ?: dateToday()
        val dateNextFromDb =
            if (version == 3) indicationUiState?.suffix ?: dateTodayNextYear() else dateToday()


        var note by rememberSaveable { mutableStateOf(noteFromDb) }
        var count by rememberSaveable { mutableStateOf(countFromDb) }
        var countAnimal by rememberSaveable { mutableStateOf(countAnimalFromDb) }
        var price by rememberSaveable { mutableStateOf(priceFromDb) }
        var suffix by rememberSaveable { mutableStateOf(suffixFromDb) }
        var date by rememberSaveable { mutableStateOf(dateFromDb) }
        var dateNext by rememberSaveable { mutableStateOf(dateNextFromDb) }
        var isAutoCalculate by rememberSaveable { mutableStateOf(false) }

        //Error
        var isErrorCount by rememberSaveable { mutableStateOf(false) }
        var isErrorCountAnimal by rememberSaveable { mutableStateOf(false) }
        var isErrorPrice by rememberSaveable { mutableStateOf(false) }

        Column(modifier = Modifier.modifierBottomSheet()) {
            OutlinedTextCount(
                value = count,
                onValueChange = {
                    count = if (version == 3) it else it.toConvertDb()
                    isErrorCount = it.isError()
                },
                onSuffixChange = { suffix = it },
                isWarehouseShow = false,
                intRes = getVersionToStringTitle(version),
                drawableRes = getVersionToImage(version),
                versionDropMenu = version,
                intResError = getVersionToStringError(version),
                isError = isErrorCount,
                suffix = suffix,
                intResSup = getVersionToStringSup(version),
                keyboardOptions = if (version == 3) keyboardOptionsNext() else keyboardOptionsNextNumber(),
//                keyboardActions = keyboardActionsClear(focusManager)
            )
            if (version == 3 || version == 2) {
                if (version == 3 && animalUiState?.group == true)
                    OutlinedTextCount(
                        value = countAnimal,
                        onValueChange = {
                            countAnimal = it.toConvertDb()
                            isErrorCountAnimal = it.isError()
                        },
                        isDropMenuShow = false,
                        isWarehouseShow = false,
                        isAnimal = true,
                        intRes = R.string.count_screen_title,
                        drawableRes = R.drawable.baseline_spoke_24,
                        isError = isErrorCountAnimal,
                        countWarehouse = animalCountUiState?.weight ?: "",
                        suffix = animalCountUiState?.suffix ?: "",
                    )
                OutlinedPriceInput(
                    price = price,
                    onPriceChange = {
                        price = it
                        isErrorPrice = it.isError()
                    },
                    count = count,
                    isAutoCalculate = isAutoCalculate,
                    onAutoCalculate = { isAutoCalculate = it },
                    isManyCount = animalUiState?.group == true,
                    isError = isErrorPrice,
                    supportTextRes = if (animalUiState?.group == true) R.string.support_text_price_animals else R.string.support_text_price_animal,
                    supportTextResAutoCal = R.string.support_text_price_one_animals,
                )
            }
            OutlinedTextDate(
                value = date,
                initialSelectedDateMilli = indicationUiState?.let { formatDateToLong(it.date) },
                intResSup = if (version == 3) R.string.support_text_data_vaccination else R.string.support_text_date,
                onValueChange = { date = it }
            )
            if (version == 3)
                OutlinedTextDateNoLimit(
                    value = dateNext,
                    initialSelectedDateMilli = indicationUiState?.let { formatDateToLong(it.suffix) },
                    intRes = R.string.outlined_text_date_next,
                    intResSup = R.string.support_text_data_next,
                    onValueChange = { dateNext = it }
                )
            OutlinedTextNote(
                value = note,
                onValueChange = { note = it },
            )
        }

        if (indicationUiState == null) {
            ButtonStandart(
                intRes = R.string.button_insert,
                onClick = {
                    if (isErrorVersion(
                            version = version,
                            title = count,
                            countAll = countAnimal,
                            isAnimalGroup = animalUiState?.group == true,
                            isErrorTitle = { isErrorCount = it },
                            isErrorCountAnimal = { isErrorCountAnimal = it }
                        )
                    ) {
                        focusManager.clearFocus()
                        val (countAnimalForDb, priceForDb, noteForDb) = prepareForDb(
                            version = version,
                            price = price,
                            countAnimal = count,
                            countAnimalVaccination = countAnimal,
                            suffix = suffix,
                            note = note,
                            isAutoCalculate = isAutoCalculate,
                            isAnimalGroup = animalUiState?.group ?: false,
                            context = context,
                            animalCountUiState = animalCountUiState,
                            previousItem = previousItem,
                        )
                        println("id: ${idAnimal.toInt()}")
                        onSaveClick(
                            Triple(
                                DomainIndicatorsVM(
                                    weight = count,
                                    date = date,
                                    suffix = if (version == 3) dateNext else suffix,
                                    idAnimal = idAnimal.toInt(),
                                    note = noteForDb
                                ),
                                priceForDb,
                                countAnimalForDb
                            )
                        )
                        if (version == 2 && count.toConvertZeroDouble() == 1.0) {
                            openDialogGroup = true

                        } else {
                            scope.launch {
                                sheetState.hide()
                                addBottomSheet.value = false
                            }
                        }
                    }
                }
            )
        } else {
            ButtonRefresh {
                if (isErrorVersion(
                        version = version,
                        title = count,
                        countAll = countAnimal,
                        isAnimalGroup = animalUiState?.group == true,
                        isErrorTitle = { isErrorCount = it },
                        isErrorCountAnimal = { isErrorCountAnimal = it }
                    )
                ) {
                    focusManager.clearFocus()
                    val (countAnimalForDb, priceForDb, noteForDb) = prepareForDb(
                        version = version,
                        price = price,
                        countAnimal = count,
                        countAnimalVaccination = countAnimal,
                        suffix = suffix,
                        note = note,
                        isAutoCalculate = isAutoCalculate,
                        isAnimalGroup = animalUiState?.group ?: false,
                        context = context,
                        animalCountUiState = animalCountUiState,
                        previousItem = previousItem,
                    )
                    onUpdateClick(
                        Triple(
                            indicationUiState.copy(
                                weight = count,
                                date = date,
                                suffix = if (version == 3) dateNext else suffix,
                                note = noteForDb
                            ),
                            priceForDb,
                            countAnimalForDb
                        )
                    )
                    println("version: $version")
                    println("isFirstCount : $isFirstCount")
                    println("count: ${count.toConvertZeroDouble()}")
                    println(count.toConvertZeroDouble() == 1.0)
                    println(version == 2 && isFirstCount && count.toConvertZeroDouble() == 1.0)

                    if (version == 2 && isFirstCount && count.toConvertZeroDouble() == 1.0) {
                        openDialogGroup = true
                    } else {
                        scope.launch {
                            sheetState.hide()
                            addBottomSheet.value = false
                        }
                    }
                }
            }
            println("isLastCount: $isLastCount")
            if (!(version == 2 && isLastCount))
                ButtonDelete {
                    onDeleteClick(indicationUiState)
                    scope.launch {
                        sheetState.hide()
                        addBottomSheet.value = false
                    }
                }
        }
        if (openDialogGroup)
            AlertDialogGroupToSolo(
                onConfirmation = {
                    openDialogGroup = !openDialogGroup
                    scope.launch {
                        sheetState.hide()
                        addBottomSheet.value = false
                    }
                },
                onUpdateClick = { onUpdateAnimalGroupClick(it) }
            )
    }
}


@Composable
fun IndicatorsCard(
    modifier: Modifier = Modifier,
    version: Int,
    domainIndicatorsVM: DomainIndicatorsVM,
    previousDomainIndicatorsVM: DomainIndicatorsVM?
) {
    var details by rememberSaveable { mutableStateOf(false) }
    val extraPadding by animateDpAsState(
        if (details) 0.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    CardField(
        modifier = modifier.padding(bottom = extraPadding.coerceAtLeast(0.dp)),
        row = false
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (version == 2)
                if (domainIndicatorsVM.version != null)
                    Icon(
                        modifier = Modifier.weight(0.2f),
                        painter = painterResource(getImageAnimalCount(domainIndicatorsVM.version)),
                        contentDescription = null
                    ) else Spacer(modifier = Modifier.weight(0.2f))
            Text(
                modifier = Modifier.weight(1f),
                text = if (version == 3) domainIndicatorsVM.weight else "${domainIndicatorsVM.weight.toFormatNumber()} ${domainIndicatorsVM.suffix}",
                style = textBold_16,
                textAlign = if (version == 3) TextAlign.Start else TextAlign.Center
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
            IconButton(
                modifier = Modifier.weight(0.25f),
                onClick = { details = !details }) {
                Icon(
                    imageVector = if (details) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }
        if (details)
            Row {
                Text(
                    modifier = Modifier.weight(1f),
                    text = lastValue(
                        version = version,
                        domainIndicatorsVM,
                        previousDomainIndicatorsVM
                    ),
                    style = text_16,
                    textAlign = TextAlign.Start
                )
            }

    }
}

@Composable
fun lastValue(
    version: Int,
    domainIndicatorsVM: DomainIndicatorsVM,
    previousDomainIndicatorsVM: DomainIndicatorsVM?,
): AnnotatedString {
    val count = domainIndicatorsVM.weight.toConvertZeroDouble()
    val previousCount = previousDomainIndicatorsVM?.weight?.toConvertZeroDouble() ?: 0.0

    val price = domainIndicatorsVM.price
    val buyer = domainIndicatorsVM.buyer

    val suffix = domainIndicatorsVM.suffix
    val suffixPrevious = previousDomainIndicatorsVM?.suffix ?: ""
    val note = if (domainIndicatorsVM.note != "") "\n${domainIndicatorsVM.note}" else ""
    val versionSection = domainIndicatorsVM.version

    val (fromSuffix, toSuffix, convertFunc) = when (version) {
        0 -> Triple(suffix, "г.", Double::convertWeight)
        1 -> Triple(suffix, "мм.", Double::convertSize)
        else -> Triple(suffix, suffix) { d: Double, _: String, _: String -> d } // identity
    }

    val countConverted = convertFunc(count, fromSuffix, toSuffix)
    val previousCountConverted = convertFunc(previousCount, suffixPrevious, toSuffix)

    val totalValueDouble =
        convertFunc(countConverted - previousCountConverted, toSuffix, fromSuffix)
    val totalValue = totalValueDouble.absoluteValue.formatNumber()


    return when (version) {
        // вес
        0 -> {
            if (count > previousCount) {
                textBuildIndicatorsAnnotated(
                    intRes = R.string.animal_indicators_weight_increased_s,
                    totalValue = totalValue,
                    suffix = suffix,
                    isPlus = true,
                    note = note
                )
            } else if (count == previousCount) {
                buildAnnotatedString {
                    append(stringResource(R.string.animal_indicators_weight_not_changed_s, suffix))
                    append(note)
                }
            } else {
                textBuildIndicatorsAnnotated(
                    intRes = R.string.animal_indicators_weight_decreased_s,
                    totalValue = totalValue,
                    suffix = suffix,
                    isPlus = false,
                    note = note
                )
            }
        }
        // размер
        1 -> {
            if (count > previousCount) {
                textBuildIndicatorsAnnotated(
                    intRes = R.string.animal_indicators_size_increased_s,
                    totalValue = totalValue,
                    suffix = suffix,
                    isPlus = true,
                    note = note
                )
            } else if (count == previousCount) {
                buildAnnotatedString {
                    append(stringResource(R.string.animal_indicators_size_not_changed_s))
                    append(note)
                }
            } else {
                textBuildIndicatorsAnnotated(
                    intRes = R.string.animal_indicators_size_decreased_s,
                    totalValue = totalValue,
                    suffix = suffix,
                    isPlus = false,
                    note = note
                )
            }
        }
        //Количество
        2 -> {
            when {
                count > previousCount -> {
                    when (versionSection) {
                        1 -> {
                            Log.i("Count", "lastValue: $domainIndicatorsVM")
                            textBuildIndicatorsAnnotated(
                                intRes = R.string.animal_card_screen_add_note_expenses,
                                totalValue = totalValue,
                                suffix = suffix,
                                price = price,
                                isPlus = false,
                                note = note
                            )
                        }

                        4 -> textBuildIndicatorsAnnotated(
                            intRes = R.string.animal_card_screen_add_note_count,
                            totalValue = totalValue,
                            suffix = suffix,
                            isPlus = true,
                            note = note
                        )

                        else -> textBuildIndicatorsAnnotated(
                            intRes = R.string.animal_indicators_animals_count_increased_note,
                            totalValue = totalValue,
                            suffix = suffix,
                            isPlus = true,
                            note = note
                        )
                    }
                }

                count == previousCount -> {
                    buildAnnotatedString {
                        append(stringResource(R.string.animal_indicators_size_not_changed_s))
                        append(note)
                    }
                }

                else -> {
                    when (versionSection) {
                        0 -> textBuildIndicatorsAnnotated(
                            intRes = R.string.animal_card_screen_sale_note_expenses,
                            totalValue = totalValue,
                            price = price,
                            suffix = suffix,
                            buyer = buyer,
                            isPlus = true,
                            note = note
                        )

                        2 -> textBuildIndicatorsAnnotated(
                            intRes = R.string.animal_card_screen_kill_note,
                            totalValue = totalValue,
                            suffix = suffix,
                            isPlus = true,
                            note = note
                        )

                        3 -> textBuildIndicatorsAnnotated(
                            intRes = if (price == null) R.string.animal_card_screen_write_off_note_count else R.string.animal_card_screen_write_off_note_expenses,
                            totalValue = totalValue,
                            price = price,
                            suffix = suffix,
                            isPlus = false,
                            note = note
                        )

                        else -> textBuildIndicatorsAnnotated(
                            intRes = R.string.animal_indicators_animals_count_reduced_note,
                            totalValue = totalValue,
                            suffix = suffix,
                            isPlus = false,
                            note = note
                        )
                    }
                }
            }
        }

        else -> {
            buildAnnotatedString {
                append(
                    if (domainIndicatorsVM.note != "")
                        domainIndicatorsVM.note else stringResource(R.string.animal_indicators_screen_no_note)
                )
            }
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
        Spacer(modifier = Modifier.weight(0.25f))
    }
}


fun prepareForDb(
    version: Int,
    price: String,
    countAnimal: String,
    countAnimalVaccination: String,

    suffix: String,
    note: String,

    isAutoCalculate: Boolean,
    isAnimalGroup: Boolean,

    context: Context,
    addButton: Boolean = true,
    animalCountUiState: DomainIndicatorsVM?,
    previousItem: DomainIndicatorsVM? = null,

    ): Triple<Double, String?, String> {
    val countAnimalForSectionDb = countAnimalForDb(
        version = version,
        countAnimal = countAnimal,
        countAnimalVaccination = countAnimalVaccination
    )
    val priceForDb = priceForDb(
        version = version,
        isAnimalGroup = isAnimalGroup,
        isAutoCalculate = isAutoCalculate,
        price = price,
        countAnimal = countAnimalForSectionDb.toString()
    )
    val noteForDb = if (addButton) noteForDb(
        version = version,
        price = priceForDb,
        note = note,
        countAnimal = countAnimalForSectionDb.toString(),
        suffix = suffix,
        suffixCountAnimal = animalCountUiState?.suffix.toString(),
        isAnimalGroup = isAnimalGroup,
        context = context,
        previousItem = previousItem
    ) else note

    return Triple(countAnimalForSectionDb, priceForDb, noteForDb)
}


private fun countAnimalForDb(
    version: Int,
    countAnimal: String,
    countAnimalVaccination: String,
): Double {
    return when (version) {
        3 -> countAnimalVaccination.toConvertZeroTooOneDouble()
        2 -> countAnimal.toConvertZeroDouble()
        else -> 1.0
    }
}


private fun priceForDb(
    version: Int,
    isAutoCalculate: Boolean,
    isAnimalGroup: Boolean,
    price: String,
    countAnimal: String
): String? {
    return when (version) {
        3 -> {
            if (price == "") null
            else {
                if (isAutoCalculate && isAnimalGroup) calculatePriceAll(price, countAnimal)
                else price
            }
        }

        2 -> {
            if (price == "") null
            else {
                if (isAutoCalculate && isAnimalGroup) calculatePriceAll(price, countAnimal)
                else price
            }
        }

        else -> null
    }
}


private fun noteForDb(
    context: Context,
    version: Int,
    note: String,
    countAnimal: String,
    suffix: String,
    suffixCountAnimal: String,
    price: String?,
    isAnimalGroup: Boolean,
    previousItem: DomainIndicatorsVM?
): String {
    val notePlus = if (note == "") "" else "\n$note"
    return when (version) {
        2 -> {
            if (previousItem == null) {
                context.getString(
                    R.string.animal_indicators_animals_count_add_note,
                    countAnimal.toFormatNumber(), suffix
                ) + notePlus
            } else {
                val previousCount = previousItem.weight.toConvertZeroDouble()
                val countNow = countAnimal.toConvertZeroDouble()
                val difference = (countNow - previousCount).absoluteValue.formatNumber()
                if (countNow > previousCount) {
                    context.getString(
                        R.string.animal_indicators_animals_count_increased_note,
                        difference, suffix
                    ) + notePlus
                } else if (previousCount == countNow) {
                    context.getString(
                        R.string.animal_indicators_animals_count_not_changed_note
                    ) + notePlus
                } else {
                    context.getString(
                        R.string.animal_indicators_animals_count_reduced_note,
                        difference, suffix
                    ) + notePlus
                }
            }
        }

        3 -> {
            if (price != "" && price != null) {
                if (isAnimalGroup) {
                    context.getString(
                        R.string.animal_indicators_animals_vaccination_price_s,
                        countAnimal, suffixCountAnimal, price.toFormatNumber()
                    ) + " " + context.getString(R.string.currency_ruble) + notePlus
                } else {
                    context.getString(
                        R.string.animal_indicators_vaccination_price_s,
                        price.toFormatNumber()
                    ) + " " + context.getString(R.string.currency_ruble) + notePlus
                }
            } else {
                if (isAnimalGroup) {
                    context.getString(
                        R.string.animal_indicators_animals_vaccination_no_price_s,
                        countAnimal.toFormatNumber(), suffixCountAnimal
                    ) + notePlus
                } else note
            }
        }

        else -> note
    }

//    if (version == 3) {
//        if (price != "" && price != null) {
//            if (isAnimalGroup) {
//                context.getString(
//                    R.string.animal_indicators_animals_vaccination_price_s,
//                    countAnimal, suffixCountAnimal, price.toFormatNumber()
//                ) + " " + context.getString(R.string.currency_ruble) + notePlus
//            } else {
//                context.getString(
//                    R.string.animal_indicators_vaccination_price_s,
//                    price.toFormatNumber()
//                ) + " " + context.getString(R.string.currency_ruble) + notePlus
//            }
//        } else {
//            if (isAnimalGroup) {
//                context.getString(
//                    R.string.animal_indicators_animals_vaccination_no_price_s,
//                    countAnimal.toFormatNumber(), suffixCountAnimal
//                ) + notePlus
//            } else note
//        }
//    } else note
}

fun sum(
    previousItem: DomainIndicatorsVM, domainIndicatorsVM: DomainIndicatorsVM
): String {
    return when (previousItem.version) {
        1, 4 -> animalCountSum(previousItem.weight, domainIndicatorsVM.weight)
        0, 2, 3 -> animalCountDifference(domainIndicatorsVM.weight, previousItem.weight)
        else -> domainIndicatorsVM.weight
    }
}

private fun animalCountSum(
    previousCountAll: String,
    count: String,
): String {
    return (previousCountAll.toConvertZeroString()
        .toConvertDbOnlyInt() + count.toConvertZeroString()
        .toConvertDbOnlyInt()).toString()
}

private fun animalCountDifference(
    previousCountAll: String,
    count: String,
): String {
    return (previousCountAll.toConvertZeroString()
        .toConvertDbOnlyInt() - count.toConvertZeroString()
        .toConvertDbOnlyInt()).toString()
}
