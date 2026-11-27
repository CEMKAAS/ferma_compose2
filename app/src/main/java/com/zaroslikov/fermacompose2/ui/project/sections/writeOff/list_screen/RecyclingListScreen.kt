package com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.WarehouseCard
import com.zaroslikov.domain.models.table.DomainRecyclingSummary
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.dateBuilder
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.empty_list.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.empty_list.EmptyBookmark
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.project.sections.recycling.RecyclingMinuteRefresh
import com.zaroslikov.fermacompose2.ui.project.sections.recycling.RecyclingProductsBrief
import com.zaroslikov.fermacompose2.ui.project.sections.recycling.RecyclingRawLine
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseSectionSummary
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseSummaryMetric
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_3

internal object RecyclingListDestination {
    const val route = "warehouse_recycling"
    val routeWithArgs = "$route/{${WriteOffDestination.itemIdArg}}"
}

@Composable
internal fun RecyclingListScreen(
    navigateBack: () -> Unit,
    viewModel: WriteOffViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val summary by viewModel.recyclingSummary.collectAsStateWithLifecycle(initialValue = null)
    RecyclingMinuteRefresh(viewModel::refreshRecycling)

    Scaffold(topBar = {
        TopAppBarBack(intRes = R.string.recycling_detail_title, onNavigateBackClick = navigateBack)
    }) { innerPadding ->
        val counts = summary
        if (state.idPT == 0L || counts == null) {
            CircularProgress(Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "summary") { RecyclingSummary(counts) }
                if (state.recyclingItems.isEmpty()) {
                    item(key = "empty") {
                        EmptyBookmark(
                            modifier = Modifier.fillParentMaxHeight(0.65f),
                            iconRes = R.drawable.baseline_autorenew_24,
                            title = R.string.warehouse_recycling_empty,
                            supportText = R.string.warehouse_recycling_empty_support,
                            iconColor = violet_1,
                            backgroundColor = violet_3
                        )
                    }
                }
                items(state.recyclingItems, key = { it.recycling.id }) { item ->
                    val batch = item.recycling
                    BorderCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.onIntent(WriteOffListIntent.OpenRecyclingDetail(batch.id)) }
                    ) {
                        Text(
                            text = dateBuilder(batch.day, stringResource(monthToResString(batch.month)), batch.year),
                            style = textBold_16
                        )
                        RecyclingRawLine(item)
                        RecyclingProductsBrief(item)
                    }
                }
            }
        }
        WriteOffEntrySheets(state, viewModel::onIntent, viewModel::onTemplateIntent)
    }
}

@Composable
private fun RecyclingSummary(counts: DomainRecyclingSummary) {
    WarehouseSectionSummary(
        card = WarehouseCard.RECYCLING,
        metrics = listOf(
            WarehouseSummaryMetric(
                label = stringResource(R.string.warehouse_recycling_active_batches),
                value = counts.activeBatches.formatNumber()
            ),
            WarehouseSummaryMetric(
                label = stringResource(R.string.warehouse_recycling_active_positions),
                value = counts.activePositions.formatNumber()
            ),
            WarehouseSummaryMetric(
                label = stringResource(R.string.warehouse_recycling_completed_batches),
                value = counts.completedBatches.formatNumber()
            ),
            WarehouseSummaryMetric(
                label = stringResource(R.string.warehouse_recycling_interrupted_batches),
                value = counts.interruptedBatches.formatNumber(),
                note = stringResource(R.string.warehouse_recycling_interrupted_note)
            )
        )
    )
}
