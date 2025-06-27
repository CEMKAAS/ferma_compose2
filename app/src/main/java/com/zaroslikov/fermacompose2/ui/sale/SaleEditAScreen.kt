@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.sale

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
import com.zaroslikov.fermacompose2.Domain.models.DomainSaleTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.PairData
import com.zaroslikov.fermacompose2.supportFun.formatDateToString
import com.zaroslikov.fermacompose2.supportFun.isError
import com.zaroslikov.fermacompose2.supportFun.isErrorSale
import com.zaroslikov.fermacompose2.supportFun.isErrorSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.supportFun.toastShort
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonDelete
import com.zaroslikov.fermacompose2.ui.composeElement.ButtonRefresh
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextBuyer
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCategory
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextCount
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextDateEdit
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextNote
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextPrice
import com.zaroslikov.fermacompose2.ui.composeElement.OutlinedTextTitleSale
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object SaleEditDestination : NavigationDestination {
    override val route = "SaleEdit"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemAddId"
    val routeWithArgs = "$route/{$itemIdArg}/{$itemIdArgTwo}"
}


@ExperimentalMaterial3Api
@Composable
fun SaleEditProduct(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: SaleEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
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

        SaleEditContainerProduct(
            modifier = Modifier
                .modifierScreen(innerPadding)
                .verticalScroll(rememberScrollState()),
            titleList = titleUiState.list,
            categoryList = categoryUiState.list,
            buyerList = buyerUiState.list,
            saleTable = viewModel.itemUiState,
            onValueChange = viewModel::updateUiState,
            countWarehouse = viewModel.countWarehouseUiState,
            updateCountWarehouse = {
                viewModel.updateCountWarehouseUiState(it)
            },
            onClickSave = {
                coroutineScope.launch {
                    viewModel.saveItem()
                }
                onNavigateUp()
            },
            onClickDelete = {
                coroutineScope.launch {
                    viewModel.deleteItem()
                }
                onNavigateUp()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleEditContainerProduct(
    modifier: Modifier,
    saleTable: DomainSaleTable,
    titleList: List<PairData>,
    categoryList: List<String>,
    buyerList: List<String>,
    onValueChange: (DomainSaleTable) -> Unit = {},
    countWarehouse: DomainPairDataDoubleSting,
    updateCountWarehouse: (Pair<String, String>) -> Unit,
    onClickSave: () -> Unit,
    onClickDelete: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var selectedItemIndex by remember { mutableIntStateOf(0) }
    var countWarehouseBoolean by rememberSaveable { mutableStateOf(false) }

    //Error
    var isErrorTitle by rememberSaveable { mutableStateOf(false) }
    var isErrorSlash by rememberSaveable { mutableStateOf(false) }
    var isErrorCount by rememberSaveable { mutableStateOf(false) }
    var isErrorPrice by rememberSaveable { mutableStateOf(false) }

    val toastText = stringResource(
        R.string.toast_refresh_s_s,
        "${saleTable.title} ${saleTable.count} ${saleTable.suffix}",
        saleTable.priceAll,
        stringResource(R.string.currency_ruble)
    )

    //date
    var date = formatDateToString(saleTable.day, saleTable.mount, saleTable.year)

    Column(modifier = modifier) {
        OutlinedTextTitleSale(
            value = saleTable.title,
            onValueChange = {
                onValueChange(saleTable.copy(title = it.trim()))
                isErrorTitle = it.isError()
                isErrorSlash = it.isErrorSlash()
            },
            onValueChoice = {
                selectedItemIndex = it.first
                onValueChange(saleTable.copy(title = it.second))
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
            value = saleTable.count,
            onValueChange = {
                onValueChange(saleTable.copy(count = it.toConvertDb()))
                isErrorCount = it.isError()
            },
            onClick = { onValueChange(saleTable.copy(suffix = it)) },
            isError = isErrorCount,
            suffix = saleTable.suffix,
            countWarehouse = countWarehouse.first,
            countWarehouseSuffix = countWarehouse.second,
            intResSup = R.string.support_text_count_product_sale,
            focusManager = focusManager
        )
        OutlinedTextPrice(
            value = saleTable.priceAll,
            onValueChange = {
                onValueChange(saleTable.copy(priceAll = it))
                isErrorPrice = it.isError()
            },
            isError = isErrorPrice,
            intSupportText = R.string.support_text_price_sale,
            focusManager = focusManager
        )
        OutlinedTextCategory(
            value = saleTable.category,
            onValueChange = { onValueChange(saleTable.copy(category = it.trim())) },
            titleList = categoryList,
            focusManager = focusManager
        )
        OutlinedTextDateEdit (
            value = date,
            onValueChange = {
                date = it
                val dateList = it.split(".")
                onValueChange(
                    saleTable.copy(
                        day = dateList[0].toInt(),
                        mount = dateList[1].toInt(),
                        year = dateList[2].toInt()
                    )
                )
            }
        )
        OutlinedTextBuyer(
            value = saleTable.buyer,
            onValueChange = { onValueChange(saleTable.copy(buyer = it)) },
            list = buyerList,
            focusManager = focusManager
        )
        OutlinedTextNote(
            value = saleTable.note,
            onValueChange = {
                onValueChange(saleTable.copy(note = it))
            },
            focusManager = focusManager
        )
        ButtonRefresh {
            if (isErrorSale(title = saleTable.title, count = saleTable.count,
                    price = saleTable.count,
                    isErrorTitle = { isErrorTitle = it },
                    isErrorCount = { isErrorCount = it },
                    isErrorSlash = { isErrorSlash = it },
                    isErrorPrice = { isErrorPrice = it })
            ) {
                focusManager.clearFocus()
                onClickSave()
                toastShort(
                    context = context,
                    text = toastText
                )
            }
        }
        ButtonDelete { onClickDelete() }
    }
}