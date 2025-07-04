@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.sections.sale

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.Domain.models.DomainSaleTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.PairData
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorSale
import com.zaroslikov.fermacompose2.supportFun.isErrorSlash
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonDelete
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonStandart
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedPriceInput
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextBuyer
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCategory
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDateEdit
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleSale
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent


object SaleEntryDestination : NavigationDestination {
    override val route = "SaleEntry"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}


@Composable
fun SaleEntryProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: SaleEntryViewModel = hiltViewModel()
) {
    val eventFlow = viewModel.eventFlow

    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navigateBack()
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBarBack(intRes = R.string.sale_screen_title, navigateUp = navigateBack)
        }
    ) { innerPadding ->
        SaleEntryContainerProduct(
            modifier = Modifier.modifierScreen(innerPadding),
            domainSaleTable = viewModel.saleUiState,
            isEntry = viewModel.isEntry,
            isIndicationValue = viewModel.isIndicatorsValue,
            isAutoCalculate = viewModel.isAutoCalculate,
            titleList = viewModel.titleUiState.collectAsState().value.list,
            categoryList = viewModel.categoryUiState.collectAsState().value.list,
            buyerList = viewModel.buyerUiState.collectAsState().value.list,
            onValueChange = viewModel::updateUiState,
            onClickInsert = viewModel::insertItem,
            onClickUpdate = viewModel::updateItem,
            onClickDelete = viewModel::deleteItem,
            countWarehouse = viewModel.itemUiState,
            updateCountWarehouse = viewModel::updateWarehouseUiState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleEntryContainerProduct(
    modifier: Modifier,
    domainSaleTable: DomainSaleTable,
    isEntry: Boolean,
    isIndicationValue: Boolean,
    isAutoCalculate: MutableState<Boolean>,
    titleList: List<PairData>,
    categoryList: List<String>,
    buyerList: List<String>,
    countWarehouse: DomainPairDataDoubleSting,
    updateCountWarehouse: (Pair<String, String>) -> Unit,
    onValueChange: (DomainSaleTable) -> Unit,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    var date by rememberSaveable {
        mutableStateOf(
            formatDateToString(
                domainSaleTable.day,
                domainSaleTable.mount,
                domainSaleTable.year
            )
        )
    }
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    var countWarehouseBoolean by rememberSaveable { mutableStateOf(false) }

    //Error
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorSlash by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorPrice by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {

        OutlinedTextTitleSale(
            value = domainSaleTable.title,
            onValueChange = {
                onValueChange(domainSaleTable.copy(title = it))
                isErrorTitle = it.isError()
                isErrorSlash = it.isErrorSlash()
            },
            onValueChoice = {
                selectedItemIndex = it.first
                onValueChange(domainSaleTable.copy(title = it.second))
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
            value = domainSaleTable.count,
            onValueChange = {
                onValueChange(domainSaleTable.copy(count = it))
                isErrorCount = it.isError()
            },
            onClick = { onValueChange(domainSaleTable.copy(suffix = it)) },
            isError = isErrorCount,
            suffix = domainSaleTable.suffix,
            intResSup = R.string.support_text_count_product_sale,
            countWarehouse = countWarehouse.first,
            countWarehouseSuffix = countWarehouse.second,
            focusManager = focusManager
        )
        OutlinedPriceInput(
            price = domainSaleTable.priceAll,
            onPriceChange = {
                onValueChange(domainSaleTable.copy(priceAll = it))
                isErrorPrice = it.isError()
            },
            count = domainSaleTable.count,
            isError = isErrorPrice,
            isAutoCalculate = isAutoCalculate.value,
            onAutoCalculate = { isAutoCalculate.value = it },
            isManyCount = true,
            focusManager = focusManager
        )
        OutlinedTextCategory(
            value = domainSaleTable.category,
            onValueChange = { onValueChange(domainSaleTable.copy(category = it)) },
            titleList = categoryList,
            focusManager = focusManager
        )
        OutlinedTextDateEdit(
            value = date,
            onValueChange = {
                date = it
                val dateList = it.split(".")
                onValueChange(
                    domainSaleTable.copy(
                        day = dateList[0].toInt(),
                        mount = dateList[1].toInt(),
                        year = dateList[2].toInt()
                    )
                )
            }
        )
        OutlinedTextBuyer(
            value = domainSaleTable.buyer,
            onValueChange = { domainSaleTable.copy(buyer = it) },
            list = buyerList,
            focusManager = focusManager
        )
        OutlinedTextNote(
            value = domainSaleTable.note,
            onValueChange = { onValueChange(domainSaleTable.copy(note = it)) },
            focusManager = focusManager
        )
        ButtonPanel(
            title = domainSaleTable.title,
            count = domainSaleTable.count,
            priceAll = domainSaleTable.priceAll,
            isEntry = isEntry,
            focusManager = focusManager,
            isErrorTitle = { isErrorTitle = it },
            isErrorCount = { isErrorCount = it },
            isErrorSlash = { isErrorSlash = it },
            isErrorPrice = { isErrorPrice = it },
            onClickInsert = { onClickInsert() },
            onClickUpdate = { onClickUpdate() },
            onClickDelete = { onClickDelete() }
        )
    }
}

@Composable
private fun ButtonPanel(
    title: String,
    count: String,
    priceAll: String,
    isEntry: Boolean,
    focusManager: FocusManager,
    isErrorTitle: (Boolean) -> Unit,
    isErrorCount: (Boolean) -> Unit,
    isErrorSlash: (Boolean) -> Unit,
    isErrorPrice: (Boolean) -> Unit,
    onClickInsert: () -> Unit,
    onClickUpdate: () -> Unit,
    onClickDelete: () -> Unit
) {
    if (isEntry)
        ButtonStandart(
            intRes = R.string.button_sale,
            onClick = {
                onClickButton(
                    title = title,
                    count = count,
                    priceAll = priceAll,
                    focusManager = focusManager,
                    isErrorTitle = isErrorTitle,
                    isErrorCount = isErrorCount,
                    isErrorSlash = isErrorSlash,
                    isErrorPrice = isErrorPrice,
                    onClick = onClickInsert
                )
            }
        )
    else {
        ButtonRefresh {
            onClickButton(
                title = title,
                count = count,
                priceAll = priceAll,
                focusManager = focusManager,
                isErrorTitle = isErrorTitle,
                isErrorCount = isErrorCount,
                isErrorSlash = isErrorSlash,
                isErrorPrice = isErrorPrice,
                onClick = onClickUpdate
            )
        }
        ButtonDelete { onClickDelete() }
    }
}

private fun onClickButton(
    title: String,
    count: String,
    priceAll: String,
    focusManager: FocusManager,
    isErrorTitle: (Boolean) -> Unit,
    isErrorCount: (Boolean) -> Unit,
    isErrorSlash: (Boolean) -> Unit,
    isErrorPrice: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    if (isErrorSale(
            title = title, count = count,
            price = priceAll,
            isErrorTitle = { isErrorTitle(it) },
            isErrorCount = { isErrorCount(it) },
            isErrorSlash = { isErrorSlash(it) },
            isErrorPrice = { isErrorPrice(it) })
    ) {
        focusManager.clearFocus()
        onClick()
    }
}

//ButtonStandart(
//intRes = R.string.button_sale,
//onClick = {
//    if (isErrorSale(
//            title = title, count = count,
//            price = priceAll,
//            isErrorTitle = { isErrorTitle = it },
//            isErrorCount = { isErrorCount = it },
//            isErrorSlash = { isErrorSlash = it },
//            isErrorPrice = { isErrorPrice = it })
//    ) {
//        focusManager.clearFocus()
//        val formattedDateList = date.split(".")
//        saveInRoomSale(
//            SaleTable(
//                id = 0,
//                title = title,
//                count = count.toDouble(),
//                day = formattedDateList[0].toInt(),
//                mount = formattedDateList[1].toInt(),
//                year = formattedDateList[2].toInt(),
//                suffix = suffix,
//                category = category,
//                priceAll = priceAll.toDouble(),
//                buyer = buyer,
//                note = note,
//                idPT = idProject.toLong()
//            )
//        )
//        toastShort(
//            context = context,
//            text = toastText
//        )
//        metricaSale(
//            title, count, priceAll, category, buyer, note
//        )
//    }
//}
//)