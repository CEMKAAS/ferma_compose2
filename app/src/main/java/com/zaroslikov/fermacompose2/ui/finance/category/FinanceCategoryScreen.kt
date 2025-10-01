@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.finance.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.CardFinanceRow
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
//import com.zaroslikov.fermacompose2.ui.warehouse.AnalysisNav
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

@Composable
fun FinanceCategoryScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
//    navigationToAnalysis: (AnalysisNav) -> Unit,
    viewModel: FinanceCategoryViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBarBack(
                title = viewModel.itemCategory,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        FinanceCategoryContainer(
            modifier = Modifier
                .modifierScreenLazy(innerPadding),
            itemList = state.financeCategory,
            navigationToAnalysis = {
                if (viewModel.itemBoolean) {
//                    navigationToAnalysis(AnalysisNav(idProject = viewModel.itemId, name = it)) TODO Переход на анализ
                    AppMetrica.reportEvent("Анализ через финансы")
                }
            }
        )
    }
}


@Composable
private fun FinanceCategoryContainer(
    modifier: Modifier = Modifier,
    itemList: List<DomainTitleSuffixPrice>,
    navigationToAnalysis: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        items(items = itemList) {
            CardFinanceRow(
                title = it.title,
                value = it.price,
                modifier = Modifier
                    .clickable { navigationToAnalysis(it.title) }
            )
        }
    }
}
