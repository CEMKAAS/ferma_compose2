package com.zaroslikov.fermacompose2.ui.finance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.formatter
import com.zaroslikov.fermacompose2.ui.warehouse.AnalysisNav
import com.zaroslikov.fermacompose2.ui.warehouse.TextButtonWarehouse

object FinanceIncomeExpensesDestination : NavigationDestination {
    override val route = "FinanceIncomeExpenses"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemCategory"
    val routeWithArgs =
        "${route}/{$itemIdArg}/{$itemIdArgTwo}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceIncomeExpensesScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    navigationToAnalysis: (AnalysisNav) -> Unit,
    viewModel: FinanceIncomeExpensesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val titleTopBar = if (viewModel.itemBoolean) "Мои Доходы" else "Мои Расходы"

    val financeCategoryState by viewModel.financeCategoryIEState.collectAsState()
    val financeProduuctState by viewModel.financeProductIEState.collectAsState()
    val financeAnimalState by viewModel.aminalExpensesUIState.collectAsState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarEdit(title = titleTopBar, navigateUp = navigateBack)
        }
    ) { innerPadding ->
        FinanceIncomeExpensesBody(
            itemList = financeCategoryState.itemList,
            productList = financeProduuctState.itemList,
            animalList = financeAnimalState.itemList,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
            navigationToAnalysis = {
                navigationToAnalysis(
                    AnalysisNav(idProject = viewModel.itemId, name = it)
                )
            },
            boolean = viewModel.itemBoolean
        )
    }
}

@Composable
private fun FinanceIncomeExpensesBody(
    itemList: List<Fin>,
    productList: List<Fin>,
    animalList: List<Fin>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navigationToAnalysis: (String) -> Unit,
    boolean: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (productList.isEmpty() && itemList.isEmpty() && (boolean || animalList.isEmpty())) {
            Text(
                text = stringResource(R.string.no_item_finance_edit),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {
            FinanceIncomeExpensesInventoryList(
                itemList = itemList,
                productList = productList,
                animalList = animalList,
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
                navigationToAnalysis = navigationToAnalysis,
                boolean = boolean
            )
        }
    }

}

@Composable
private fun FinanceIncomeExpensesInventoryList(
    itemList: List<Fin>,
    productList: List<Fin>,
    animalList: List<Fin>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    navigationToAnalysis: (String) -> Unit,
    boolean: Boolean
) {
    var productBoolean by rememberSaveable { mutableStateOf(true) }
    var animalBoolean by rememberSaveable { mutableStateOf(true) }
    var categoryTable by rememberSaveable { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {

        if (productList.isNotEmpty()) {
            item {
                TextButtonWarehouse(
                    onClick = { categoryTable = !categoryTable },
                    boolean = categoryTable,
                    title = "Категории"
                )
            }
            if (categoryTable) {
                items(items = itemList) { item ->
                    FinanceProductCard(
                        fin = item,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }


        if (productList.isNotEmpty()) {
            item {
                TextButtonWarehouse(
                    onClick = { productBoolean = !productBoolean },
                    boolean = productBoolean,
                    title = "Товар"
                )
            }
            if (productBoolean) {
                items(items = productList) { item ->
                    FinanceProductCard(
                        fin = item,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { if (boolean) navigationToAnalysis(item.title!!) }
                    )
                }
            }
        }

        if (!boolean) {
            if (animalList.isNotEmpty()) {
                item {
                    TextButtonWarehouse(
                        onClick = { animalBoolean = !animalBoolean },
                        boolean = animalBoolean,
                        title = "Животные"
                    )
                }
                if (animalBoolean) {
                    items(items = animalList) { item ->
                        FinanceProductCard(
                            fin = item,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun FinanceProductCard(
    fin: Fin,
    modifier: Modifier = Modifier
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
                text = fin.title ?: "",
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(6.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            Text(
                text = "${formatter(fin.priceAll)} ₽",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth(1f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }
    }
}

//@Composable
//fun FinanceIncomeExpensesProductCard(
//    fin: Fin,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        modifier = modifier,
//        elevation = CardDefaults.cardElevation(2.dp),
//        colors = CardDefaults.cardColors()
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//
//            Column(
//                modifier = Modifier.fillMaxWidth(0.7f)
//            ) {
//                Text(
//                    text = fin.title ?: "",
//                    modifier = Modifier
//                        .wrapContentSize()
//                        .padding(6.dp),
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 16.sp
//                )
//            }
//
//            Text(
//                text = "${formatter(fin.priceAll)} ₽",
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .padding(6.dp)
//                    .fillMaxWidth(1f),
//                fontWeight = FontWeight.SemiBold,
//                fontSize = 18.sp
//            )
//        }
//    }
//}

