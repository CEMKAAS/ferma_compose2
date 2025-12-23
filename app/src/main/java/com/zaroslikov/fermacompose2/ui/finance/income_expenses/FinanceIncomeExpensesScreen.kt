@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.finance.income_expenses

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.dto.shared.DomainCategoryPrice
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.CardFinanceRow
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy


@Composable
fun FinanceIncomeExpensesScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    navigationToAnalysis: (Pair<Long, String>) -> Unit,
    viewModel: FinanceIncomeExpensesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val financeProductState = state.financeProductList
    val financeCategoryState = state.financeCategoryList
//    val financeAnimalState by viewModel.aminalExpensesUIState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBarBack(
                scrollBehavior = scrollBehavior,
                intRes = if (state.isIncome) R.string.income_screen_title else R.string.expenses_my_screen_title,
                onNavigateBackClick = navigateBack
            )
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = modifier.padding(innerPadding),
            )
        else
            FinanceIncomeExpensesBody(
                modifier = Modifier
                    .modifierScreenLazy(innerPadding),
                boolean = state.isIncome,
                categoryList = financeCategoryState,
                productList = financeProductState,
//                animalList = financeAnimalState.itemList,
                navigationToAnalysis = { navigationToAnalysis(state.idPT to it) }
            )
    }
}

@Composable
private fun FinanceIncomeExpensesBody(
    modifier: Modifier = Modifier,
    boolean: Boolean,
    categoryList: List<DomainCategoryPrice>,
    productList: List<DomainTitleSuffixPrice>,
//    animalList: List<Fin>,
    navigationToAnalysis: (String) -> Unit,
) {
    if (productList.isEmpty() && categoryList.isEmpty()
    /*&& (boolean || animalList.isEmpty())*/) {
        //TODO Придумать текст или картинку
    } else {
        FinanceIncomeExpensesInventoryList(
            modifier = modifier,
            categoryList = categoryList,
            productList = productList,
//            animalList = animalList,
            boolean = boolean,
            navigationToAnalysis = navigationToAnalysis,
        )
    }
}

@Composable
private fun FinanceIncomeExpensesInventoryList(
    modifier: Modifier = Modifier,
    boolean: Boolean,
    categoryList: List<DomainCategoryPrice>,
    productList: List<DomainTitleSuffixPrice>,
//    animalList: List<Fin>,
    navigationToAnalysis: (String) -> Unit,
) {
    var productBoolean by rememberSaveable { mutableStateOf(true) }
    var categoryTable by rememberSaveable { mutableStateOf(true) }
    var animalBoolean by rememberSaveable { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        List3(
            titRes = R.string.finance_income_expenses_category,
            list = categoryList,
            onClickDetails = { categoryTable = !categoryTable },
            isDetails = categoryTable,
            titleProvider = { it.category },
            valueProvider = { it.price.toDouble() },
            isIncome = boolean,
        )
        List3(
            titRes = R.string.finance_income_expenses_product,
            list = productList,
            onClickDetails = { productBoolean = !productBoolean },
            isDetails = productBoolean,
            titleProvider = { it.title },
            valueProvider = { it.price },
            isIncome = boolean,
            onClickAnalysis = { navigationToAnalysis(it) }
        )
        /*if (!boolean) {
            if (animalList.isNotEmpty()) {
                item {
                    TextButtonWarehouse(
                        onClick = { animalBoolean = !animalBoolean },
                        boolean = animalBoolean,
                        intRes = R.string.outlined_text_animals
                    )
                }
                if (animalBoolean)
                    items(items = animalList) {
                        CardFinanceRow(
                            title = it.title ?: "",
                            value = it.priceAll
                        )
                    }
            }
        }*/
    }
}


private fun <T> LazyListScope.List3(
    @StringRes titRes: Int,
    list: List<T>,
    isIncome: Boolean,
    isDetails: Boolean,
    onClickDetails: () -> Unit,
    onClickAnalysis: (String) -> Unit = {},
    titleProvider: (T) -> String,
    valueProvider: (T) -> Double
) {
    if (list.isNotEmpty()) {
        item {
           /* TextButtonWarehouse(
                onClick = onClickDetails,
                boolean = isDetails,
                intRes = titRes
            )*/
        }
        if (isDetails)
            items(items = list) { item ->
                CardFinanceRow(
                    modifier = Modifier.clickable { if (isIncome) onClickAnalysis(titleProvider(item)) },// Navigate to title navigationToAnalysis(it.title)
                    title = titleProvider(item),
                    value = valueProvider(item),
                )
            }
    }
}

