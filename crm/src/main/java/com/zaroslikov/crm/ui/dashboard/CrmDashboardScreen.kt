package com.zaroslikov.crm.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingFlat
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AddTask
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.crm.R
import com.zaroslikov.crm.ui.base.CrmUiEvent
import com.zaroslikov.crm.ui.components.CrmBarChart
import com.zaroslikov.crm.ui.components.CrmBigCard
import com.zaroslikov.crm.ui.components.CrmCard
import com.zaroslikov.crm.ui.components.CrmEmptyState
import com.zaroslikov.crm.ui.components.CrmHorizontalBar
import com.zaroslikov.crm.ui.components.CrmIconCircle
import com.zaroslikov.crm.ui.components.CrmKpiTile
import com.zaroslikov.crm.ui.components.CrmSectionTitle
import com.zaroslikov.crm.ui.components.CrmTopBar
import com.zaroslikov.crm.ui.components.color
import com.zaroslikov.crm.ui.components.icon
import com.zaroslikov.crm.ui.navigation.CRM_NEW_CLIENT_ID
import com.zaroslikov.crm.ui.tasks.CrmTaskRow
import com.zaroslikov.crm.ui.tasks.CrmTaskSheet
import com.zaroslikov.crm.ui.theme.black_2
import com.zaroslikov.crm.ui.theme.blue_10
import com.zaroslikov.crm.ui.theme.blue_11
import com.zaroslikov.crm.ui.theme.blue_12
import com.zaroslikov.crm.ui.theme.blue_4
import com.zaroslikov.crm.ui.theme.crmAvatarPalette
import com.zaroslikov.crm.ui.theme.gray_7
import com.zaroslikov.crm.ui.theme.green_6
import com.zaroslikov.crm.ui.theme.green_7
import com.zaroslikov.crm.ui.theme.green_shamrock
import com.zaroslikov.crm.ui.theme.orang_8
import com.zaroslikov.crm.ui.theme.orang_9
import com.zaroslikov.crm.ui.theme.purple
import com.zaroslikov.crm.ui.theme.red_7
import com.zaroslikov.crm.ui.theme.textBold_14
import com.zaroslikov.crm.ui.theme.textBold_20
import com.zaroslikov.crm.ui.theme.textBold_28
import com.zaroslikov.crm.ui.theme.text_12
import com.zaroslikov.crm.ui.theme.text_14
import com.zaroslikov.crm.ui.theme.white
import com.zaroslikov.crm.util.crmFormat
import com.zaroslikov.crm.util.crmMoney
import com.zaroslikov.crm.util.isoToDisplay
import com.zaroslikov.crm.util.monthShortRu
import com.zaroslikov.crm.util.toCrmResId
import com.zaroslikov.crm.util.toResId
import kotlin.math.roundToInt

@Composable
fun CrmDashboardScreen(
    navigateBack: () -> Unit,
    navigateToClients: () -> Unit,
    navigateToClient: (Long) -> Unit,
    navigateToClientEdit: (Long) -> Unit,
    navigateToTasks: () -> Unit,
    viewModel: CrmDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                CrmUiEvent.NavigateBack -> navigateBack()
                is CrmUiEvent.Message -> snackbarHostState.showSnackbar(context.getString(event.resId))
            }
        }
    }

    Scaffold(
        topBar = { CrmTopBar(title = stringResource(R.string.crm_title), navigateBack = navigateBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (state.isLoading)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = green_6)
            }
        else
            CrmDashboardBody(
                state = state,
                onIntent = viewModel::onIntent,
                navigateToClients = navigateToClients,
                navigateToClient = navigateToClient,
                navigateToClientEdit = navigateToClientEdit,
                navigateToTasks = navigateToTasks,
                modifier = Modifier.padding(innerPadding)
            )
    }

    if (state.showTaskSheet)
        CrmTaskSheet(
            clients = state.clients,
            onDismiss = { viewModel.onIntent(CrmDashboardIntent.CloseTaskSheet) },
            onSave = { title, dueDate, clientId, note ->
                viewModel.onIntent(CrmDashboardIntent.CreateTask(title, dueDate, clientId, note))
            }
        )
}

@Composable
private fun CrmDashboardBody(
    state: CrmDashboardState,
    onIntent: (CrmDashboardIntent) -> Unit,
    navigateToClients: () -> Unit,
    navigateToClient: (Long) -> Unit,
    navigateToClientEdit: (Long) -> Unit,
    navigateToTasks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val data = state.data
    val currency = stringResource(data.currency.toCrmResId())
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (data.salesWithoutBuyer > 0)
            CrmCard(containerColor = orang_8, elevation = 0.dp) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Rounded.Info, contentDescription = null, tint = orang_9)
                    Text(
                        text = stringResource(R.string.crm_banner_sales_without_buyer, data.salesWithoutBuyer),
                        style = text_14,
                        color = black_2
                    )
                }
            }

        CrmRevenueCard(data = data, currency = currency)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CrmKpiTile(
                icon = Icons.Rounded.People,
                label = stringResource(R.string.crm_kpi_clients_total),
                value = data.clientsCount.crmFormat(),
                color = blue_4,
                modifier = Modifier.weight(1f),
                onClick = navigateToClients
            )
            CrmKpiTile(
                icon = Icons.Rounded.Star,
                label = stringResource(R.string.crm_kpi_new_month),
                value = data.newThisMonth.crmFormat(),
                color = purple,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CrmKpiTile(
                icon = Icons.Rounded.Receipt,
                label = stringResource(R.string.crm_kpi_avg_check),
                value = data.avgCheck.crmMoney(currency),
                color = green_shamrock,
                modifier = Modifier.weight(1f)
            )
            CrmKpiTile(
                icon = Icons.Rounded.ShoppingCart,
                label = stringResource(R.string.crm_kpi_orders_month),
                value = data.ordersMonth.crmFormat(),
                color = orang_9,
                modifier = Modifier.weight(1f)
            )
        }

        CrmQuickActions(
            onClients = navigateToClients,
            onAddClient = { navigateToClientEdit(CRM_NEW_CLIENT_ID) },
            onNewTask = { onIntent(CrmDashboardIntent.OpenTaskSheet) }
        )

        CrmCard {
            CrmSectionTitle(title = stringResource(R.string.crm_revenue_by_month))
            Text(text = stringResource(R.string.crm_revenue_by_month_sup), style = text_12, color = gray_7)
            if (data.months.all { it.total <= 0.0 })
                Text(
                    text = stringResource(R.string.crm_chart_empty),
                    style = text_14,
                    color = gray_7,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    textAlign = TextAlign.Center
                )
            else
                CrmBarChart(
                    values = data.months.map { it.total.toFloat() },
                    labels = data.months.map { monthShortRu(it.month) },
                    suffix = currency,
                    highlightIndex = data.months.lastIndex
                )
        }

        if (data.clientsCount == 0)
            CrmCard {
                CrmEmptyState(
                    icon = Icons.Rounded.PersonAdd,
                    title = stringResource(R.string.crm_clients_empty_title),
                    text = stringResource(R.string.crm_clients_empty_text),
                    buttonText = stringResource(R.string.crm_action_add_client),
                    onButtonClick = { navigateToClientEdit(CRM_NEW_CLIENT_ID) }
                )
            }
        else
            CrmCard {
                CrmSectionTitle(
                    title = stringResource(R.string.crm_top_clients),
                    onAction = navigateToClients
                )
                if (data.topClients.isEmpty())
                    Text(text = stringResource(R.string.crm_top_clients_empty), style = text_14, color = gray_7)
                val max = data.topClients.maxOfOrNull { it.total } ?: 0.0
                data.topClients.forEachIndexed { index, top ->
                    CrmHorizontalBar(
                        label = top.name,
                        valueText = top.total.crmMoney(currency),
                        fraction = if (max > 0.0) (top.total / max).toFloat() else 0f,
                        color = crmAvatarPalette[index % crmAvatarPalette.size],
                        onClick = { navigateToClient(top.clientId) }
                    )
                }
            }

        CrmCard {
            CrmSectionTitle(
                title = stringResource(R.string.crm_upcoming_tasks),
                actionText = stringResource(R.string.crm_all_tasks),
                onAction = navigateToTasks
            )
            if (data.upcomingTasks.isEmpty())
                Text(text = stringResource(R.string.crm_tasks_empty_title), style = text_14, color = gray_7)
            data.upcomingTasks.forEach { item ->
                CrmTaskRow(
                    item = item,
                    isOverdue = item.task.dueDate < state.today,
                    onToggle = { onIntent(CrmDashboardIntent.ToggleTask(item.task)) },
                    onClientClick = navigateToClient
                )
            }
        }

        CrmCard {
            CrmSectionTitle(title = stringResource(R.string.crm_recent_interactions))
            if (data.recentInteractions.isEmpty())
                Text(text = stringResource(R.string.crm_no_interactions), style = text_14, color = gray_7)
            data.recentInteractions.forEach { item ->
                CrmInteractionRow(item = item, onClick = { navigateToClient(item.interaction.clientId) })
            }
        }
    }
}

/** Градиентная карточка: выручка за месяц, динамика, клиенты и активные. */
@Composable
private fun CrmRevenueCard(data: CrmDashboardData, currency: String) {
    CrmBigCard(glowColor = blue_10, colors = listOf(blue_11, blue_10, blue_12)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.crm_revenue_month),
                style = text_14,
                color = white.copy(alpha = 0.7f)
            )
            Text(
                text = data.revenueMonth.crmMoney(currency),
                style = textBold_28,
                color = white,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val dynamics = data.revenueDynamics
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (dynamics != null) {
                    val (icon, color) = when {
                        dynamics > 0.0 -> Icons.AutoMirrored.Rounded.TrendingUp to green_7
                        dynamics < 0.0 -> Icons.AutoMirrored.Rounded.TrendingDown to red_7
                        else -> Icons.AutoMirrored.Rounded.TrendingFlat to white
                    }
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                    val rounded = (dynamics * 10).roundToInt() / 10.0
                    Text(
                        text = stringResource(
                            R.string.crm_revenue_dynamics,
                            (if (rounded > 0) "+" else "") + rounded.crmFormat() + "%"
                        ),
                        style = text_12,
                        color = white.copy(alpha = 0.85f)
                    )
                } else
                    Text(
                        text = stringResource(R.string.crm_revenue_dynamics_none),
                        style = text_12,
                        color = white.copy(alpha = 0.7f)
                    )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            CrmBigCardStat(label = stringResource(R.string.crm_clients_count), value = data.clientsCount.crmFormat())
            CrmBigCardStat(label = stringResource(R.string.crm_active_90), value = data.activeCount.crmFormat())
        }
    }
}

@Composable
private fun CrmBigCardStat(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = label, style = text_12, color = white.copy(alpha = 0.7f))
        Text(text = value, style = textBold_20, color = white)
    }
}

@Composable
private fun CrmQuickActions(
    onClients: () -> Unit,
    onAddClient: () -> Unit,
    onNewTask: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CrmSectionTitle(title = stringResource(R.string.crm_quick_actions))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CrmQuickAction(
                icon = Icons.Rounded.People,
                text = stringResource(R.string.crm_action_clients),
                color = blue_4,
                onClick = onClients,
                modifier = Modifier.weight(1f)
            )
            CrmQuickAction(
                icon = Icons.Rounded.PersonAdd,
                text = stringResource(R.string.crm_action_add_client),
                color = green_shamrock,
                onClick = onAddClient,
                modifier = Modifier.weight(1f)
            )
            CrmQuickAction(
                icon = Icons.Rounded.AddTask,
                text = stringResource(R.string.crm_action_new_task),
                color = orang_9,
                onClick = onNewTask,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CrmQuickAction(
    icon: ImageVector,
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CrmCard(
        modifier = modifier,
        padding = PaddingValues(horizontal = 8.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        onClick = onClick
    ) {
        CrmIconCircle(icon = icon, color = color, modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(
            text = text,
            style = textBold_14,
            color = black_2,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CrmInteractionRow(item: CrmInteractionItem, onClick: () -> Unit) {
    val interaction = item.interaction
    CrmCard(
        padding = PaddingValues(12.dp),
        elevation = 0.dp,
        containerColor = white,
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CrmIconCircle(
                icon = interaction.type.icon(),
                color = interaction.type.color(),
                contentDescription = stringResource(interaction.type.toResId())
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = item.clientName,
                    style = textBold_14,
                    color = black_2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = interaction.note.ifBlank { stringResource(interaction.type.toResId()) },
                    style = text_12,
                    color = gray_7,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(text = isoToDisplay(interaction.date), style = text_12, color = gray_7)
        }
    }
}
