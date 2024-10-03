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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarEdit
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.start.formatter
import com.zaroslikov.fermacompose2.ui.warehouse.AnalysisNav

object FinanceCategoryDestination : NavigationDestination {
    override val route = "FinanceCategory"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val itemIdArgTwo = "itemCategory"
    const val itemIdArgThree = "itemBoolean"
    const val itemIdArgFour = "itemDateBegin"
    const val itemIdArgFive = "itemDateEnd"
    val routeWithArgs =
        "${route}/{$itemIdArg}/{$itemIdArgTwo}/{$itemIdArgThree}/${itemIdArgFour}/${itemIdArgFive}"
}

/**
 * Entry route for Home screen
 */
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
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarEdit(title = viewModel.itemCategory, navigateUp = navigateBack)
        }
    ) { innerPadding ->
        FinanceCategoryBody(
            itemList = financeCategoryState.itemList,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
            navigationToAnalysis = {
                navigationToAnalysis(
                    AnalysisNav(idProject = viewModel.itemId, name = it)
                )
            }

        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FinanceCategoryBody(
    itemList: List<Fin>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navigationToAnalysis: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        FinanceCategoryInventoryList(
            itemList = itemList,
            contentPadding = contentPadding,
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
            navigationToAnalysis = navigationToAnalysis
        )
    }

}

@Composable
private fun FinanceCategoryInventoryList(
    itemList: List<Fin>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    navigationToAnalysis: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = itemList) { item ->
            FinanceCategoryProductCard(fin = item,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { navigationToAnalysis(item.title!!) }
            )
        }
    }
}

@Composable
fun FinanceCategoryProductCard(
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

            Column(
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text(
                    text = fin.title?:"",
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(6.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

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