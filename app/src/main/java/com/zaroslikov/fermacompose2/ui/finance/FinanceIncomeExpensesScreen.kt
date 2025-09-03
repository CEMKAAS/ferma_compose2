//package com.zaroslikov.fermacompose2.ui.finance
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.zaroslikov.fermacompose2.R
//import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
//import com.zaroslikov.fermacompose2.ui.elements.CardFinanceRow
//import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
//import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
//import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
//import com.zaroslikov.fermacompose2.ui.warehouse.TextButtonWarehouse
//
//object FinanceIncomeExpensesDestination : NavigationDestination {
//    override val route = "FinanceIncomeExpenses"
//    override val titleRes = R.string.app_name
//    const val itemIdArg = "itemId"
//    const val itemIdArgTwo = "itemCategory"
//    val routeWithArgs =
//        "${route}/{$itemIdArg}/{$itemIdArgTwo}"
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FinanceIncomeExpensesScreen(
//    navigateBack: () -> Unit,
//    modifier: Modifier = Modifier,
//    navigationToAnalysis: (Pair<Int, String>) -> Unit,
//    viewModel: FinanceIncomeExpensesViewModel = viewModel(factory = AppViewModelProvider.Factory)
//) {
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
//
//    val financeCategoryState by viewModel.financeCategoryIEState.collectAsState()
//    val financeProductState by viewModel.financeProductIEState.collectAsState()
//    val financeAnimalState by viewModel.aminalExpensesUIState.collectAsState()
//
//    Scaffold(
////        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
//        topBar = {
//            TopAppBarBack(
//                intRes = if (viewModel.itemBoolean) R.string.income_screen_title else R.string.expenses_my_screen_title,
//                navigateUp = navigateBack
//            )
//        }
//    ) { innerPadding ->
//        FinanceIncomeExpensesBody(
//            modifier = Modifier
//                .modifierScreen(innerPadding),
//            boolean = viewModel.itemBoolean,
//            itemList = financeCategoryState.itemList,
//            productList = financeProductState.itemList,
//            animalList = financeAnimalState.itemList,
//            navigationToAnalysis = {
//                navigationToAnalysis(
//                    Pair(viewModel.itemId, it)
//                )
//            }
//        )
//    }
//}
//
//@Composable
//private fun FinanceIncomeExpensesBody(
//    modifier: Modifier = Modifier,
//    boolean: Boolean,
//    itemList: List<Fin>,
//    productList: List<Fin>,
//    animalList: List<Fin>,
//    navigationToAnalysis: (String) -> Unit,
//) {
//    if (productList.isEmpty() && itemList.isEmpty() && (boolean || animalList.isEmpty())) {
//        //TODO Придумать текст или картинку
//    } else {
//        FinanceIncomeExpensesInventoryList(
//            modifier = modifier,
//            itemList = itemList,
//            productList = productList,
//            animalList = animalList,
//            boolean = boolean,
//            navigationToAnalysis = navigationToAnalysis,
//        )
//    }
//}
//
//@Composable
//private fun FinanceIncomeExpensesInventoryList(
//    modifier: Modifier = Modifier,
//    boolean: Boolean,
//    itemList: List<Fin>,
//    productList: List<Fin>,
//    animalList: List<Fin>,
//    navigationToAnalysis: (String) -> Unit,
//) {
//    var productBoolean by rememberSaveable { mutableStateOf(true) }
//    var animalBoolean by rememberSaveable { mutableStateOf(true) }
//    var categoryTable by rememberSaveable { mutableStateOf(true) }
//
//    LazyColumn(
//        modifier = modifier,
//        verticalArrangement = Arrangement.Top
//    ) {
//        if (productList.isNotEmpty()) {
//            item {
//                TextButtonWarehouse(
//                    onClick = { categoryTable = !categoryTable },
//                    boolean = categoryTable,
//                    intRes = R.string.outlined_text_field_category
//                )
//            }
//            if (categoryTable)
//                items(items = itemList) {
//                    CardFinanceRow(
//                        title = it.title ?: "",
//                        value = it.priceAll
//                    )
//                }
//        }
//        if (productList.isNotEmpty()) {
//            item {
//                TextButtonWarehouse(
//                    onClick = { productBoolean = !productBoolean },
//                    boolean = productBoolean,
//                    intRes = R.string.outlined_text_product
//                )
//            }
//            if (productBoolean)
//                items(items = productList) {
//                    CardFinanceRow(
//                        title = it.title ?: "",
//                        value = it.priceAll,
//                        modifier = Modifier
//                            .clickable { if (boolean) navigationToAnalysis(it.title ?: "") }
//                    )
//                }
//        }
//        if (!boolean) {
//            if (animalList.isNotEmpty()) {
//                item {
//                    TextButtonWarehouse(
//                        onClick = { animalBoolean = !animalBoolean },
//                        boolean = animalBoolean,
//                        intRes = R.string.outlined_text_animals
//                    )
//                }
//                if (animalBoolean)
//                    items(items = animalList) {
//                        CardFinanceRow(
//                            title = it.title ?: "",
//                            value = it.priceAll
//                        )
//                    }
//            }
//        }
//    }
//}
//
