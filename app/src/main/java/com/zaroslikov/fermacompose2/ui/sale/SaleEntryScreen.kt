@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.sale

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.supportFun.PairData
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorSale
import com.zaroslikov.fermacompose2.supportFun.isErrorSlash
import com.zaroslikov.fermacompose2.supportFun.metricaSale
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.supportFun.toastShort
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.add.DatePickerDialogSample
import com.zaroslikov.fermacompose2.ui.add.PastOrPresentSelectableDates
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextBuyer
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCategory
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDate
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextPrice
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleSale
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import kotlinx.coroutines.launch
import java.time.Instant


object SaleEntryDestination : NavigationDestination {
    override val route = "SaleEntry"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}


@Composable
fun SaleEntryProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: SaleEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val titleUiState by viewModel.titleUiState.collectAsState()
    val categoryUiState by viewModel.categoryUiState.collectAsState()
    val buyerUiState by viewModel.buyerUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBarBack(intRes = R.string.sale_screen_title, navigateUp = navigateBack)
        }
    ) { innerPadding ->

        SaleEntryContainerProduct(
            modifier = Modifier
                .modifierScreen(innerPadding)
                .verticalScroll(rememberScrollState()),
            titleList = titleUiState.animalList,
            categoryList = categoryUiState.list,
            buyerList = buyerUiState.list,
            saveInRoomSale = {
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
fun SaleEntryContainerProduct(
    modifier: Modifier,
    titleList: List<PairData>,
    categoryList: List<String>,
    buyerList: List<String>,
    saveInRoomSale: (SaleTable) -> Unit,
    idProject: Int, // TODO convet to Long
    countWarehouse: Double,
    updateCountWarehouse: (Pair<String, String>) -> Unit
) {

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var title by rememberSaveable { mutableStateOf("") }
    var count by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf(dateToday()) }
    var category by rememberSaveable { mutableStateOf("Без категории") }
    var suffix by rememberSaveable { mutableStateOf("Шт.") }
    var priceAll by rememberSaveable { mutableStateOf("") }
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    var buyer by rememberSaveable { mutableStateOf("Неизвестный") }
    var note by rememberSaveable { mutableStateOf("") }
    var openDialog by remember { mutableStateOf(false) }
    var countWarehouseBoolean by rememberSaveable { mutableStateOf(false) }

    //Error
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorSlash by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorPrice by rememberSaveable { mutableStateOf(false) }

    val toastText = stringResource(
        R.string.toast_sale_s,
        "$title $count $suffix",
        priceAll,
        stringResource(R.string.currency_ruble)
    )

    //Дата
    val state = rememberDatePickerState(
        selectableDates = PastOrPresentSelectableDates,
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )

    if (openDialog) {
        DatePickerDialogSample(state, date) {
            date = it
            openDialog = !openDialog
        }
    }

    Column(modifier = modifier) {

        OutlinedTextTitleSale(
            value = title,
            onValueChange = {
                title = it.trim()
                isErrorTitle = it.isError()
                isErrorSlash = it.isErrorSlash()
            },
            onValueChoice = {
                selectedItemIndex = it.first
                title = it.second
                countWarehouseBoolean = it.third == "Моя Продукция" || it.third == "Купленный товар"
                updateCountWarehouse(Pair(it.second, it.third))
            },
            selectedItemIndex = selectedItemIndex,
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
            intResSup = R.string.support_text_count_product_sale,
            countWarehouse = countWarehouse,
            focusManager = focusManager
        )

        OutlinedTextPrice(
            value = priceAll,
            onValueChange = {
                priceAll = it.toConvertDb()
                isErrorPrice = it.isError()
            },
            isError = isErrorPrice,
            intSupportText = R.string.support_text_price_sale,
            focusManager = focusManager
        )

        OutlinedTextCategory(
            value = category,
            onValueChange = { category = it.trim() },
            titleList = categoryList,
            focusManager = focusManager
        )

        OutlinedTextDate(
            value = date,
            onValueChange = { openDialog = !openDialog }
        )

        OutlinedTextBuyer(
            value = buyer,
            onValueChange = { buyer = it },
            list = buyerList,
            focusManager = focusManager
        )

        OutlinedTextNote(
            value = note,
            onValueChange = { note = it },
            focusManager = focusManager
        )

        ButtonStandart(
            intRes = R.string.button_sale,
            onClick = {

                if (isErrorSale(title = title, count = count,
                        price = priceAll,
                        isErrorTitle = { isErrorTitle = it },
                        isErrorCount = { isErrorCount = it },
                        isErrorSlash = { isErrorSlash = it },
                        isErrorPrice = { isErrorPrice = it })
                ) {
                    focusManager.clearFocus()
                    val formattedDateList = date.split(".")
                    saveInRoomSale(
                        SaleTable(
                            id = 0,
                            title = title,
                            count = count.toDouble(),
                            day = formattedDateList[0].toInt(),
                            mount = formattedDateList[1].toInt(),
                            year = formattedDateList[2].toInt(),
                            suffix = suffix,
                            category = category,
                            priceAll = priceAll.toDouble(),
                            buyer = buyer,
                            note = note,
                            idPT = idProject
                        )
                    )
                    toastShort(
                        context = context,
                        text = toastText
                    )
                    metricaSale(
                        title, count, priceAll, category, buyer, note
                    )
                }
            }
        )
    }
}


