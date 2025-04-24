package com.zaroslikov.fermacompose2.ui.finance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.composeElement.CardFinanceRow
import com.zaroslikov.fermacompose2.ui.composeElement.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.composeElement.modifierScreen
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.warehouse.AnalysisNav
import io.appmetrica.analytics.AppMetrica

object FinanceCategoryDestination : NavigationDestination {
    override val route = "FinanceCategory"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemCategory"
    const val itemIdArgThree = "itemBoolean"
    const val itemIdArgFour = "itemDateBegin"
    const val itemIdArgFive = "itemDateEnd"
    val routeWithArgs =
        "${FinanceCategoryDestination.route}/{$itemIdArg}/{$itemIdArgTwo}/{$itemIdArgThree}/{$itemIdArgFour}/{$itemIdArgFive}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceCategoryScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    navigationToAnalysis: (AnalysisNav) -> Unit,
    viewModel: FinanceCategoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val financeCategoryState by viewModel.financeCategoryUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBarBack(title = viewModel.itemCategory, navigateUp = navigateBack)
        }
    ) { innerPadding ->
        FinanceCategoryInventoryList(
            modifier = Modifier
                .modifierScreen(innerPadding),
            itemList = financeCategoryState.itemList,
            navigationToAnalysis = {
                if (viewModel.itemBoolean) {
                    navigationToAnalysis(AnalysisNav(idProject = viewModel.itemId, name = it))
                    AppMetrica.reportEvent("Анализ через финансы")
                }
            }
        )
    }
}


@Composable
private fun FinanceCategoryInventoryList(
    modifier: Modifier = Modifier,
    itemList: List<Fin>,
    navigationToAnalysis: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        items(items = itemList) {
            CardFinanceRow(
                title = it.title?: "",
                value = it.priceAll,
                modifier = Modifier
                    .clickable { navigationToAnalysis(it.title?:"") }
            )
        }
    }
}
