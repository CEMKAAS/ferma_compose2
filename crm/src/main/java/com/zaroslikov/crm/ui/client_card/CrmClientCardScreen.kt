package com.zaroslikov.crm.ui.client_card

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.crm.R
import com.zaroslikov.crm.domain.model.CrmClientSource
import com.zaroslikov.crm.domain.model.CrmContactType
import com.zaroslikov.crm.domain.model.CrmInteractionType
import com.zaroslikov.crm.domain.model.DomainCrmContact
import com.zaroslikov.crm.domain.model.DomainCrmInteraction
import com.zaroslikov.crm.domain.model.DomainCrmTask
import com.zaroslikov.crm.ui.base.CrmUiEvent
import com.zaroslikov.crm.ui.components.CrmAvatar
import com.zaroslikov.crm.ui.components.CrmBadge
import com.zaroslikov.crm.ui.components.CrmBottomSheet
import com.zaroslikov.crm.ui.components.CrmCard
import com.zaroslikov.crm.ui.components.CrmConfirmDialog
import com.zaroslikov.crm.ui.components.CrmDatePickerField
import com.zaroslikov.crm.ui.components.CrmHorizontalBar
import com.zaroslikov.crm.ui.components.CrmIconCircle
import com.zaroslikov.crm.ui.components.CrmKpiTile
import com.zaroslikov.crm.ui.components.CrmLineChart
import com.zaroslikov.crm.ui.components.CrmSectionTitle
import com.zaroslikov.crm.ui.components.CrmSegmentedButtons
import com.zaroslikov.crm.ui.components.CrmTextField
import com.zaroslikov.crm.ui.components.CrmTopBar
import com.zaroslikov.crm.ui.components.color
import com.zaroslikov.crm.ui.components.icon
import com.zaroslikov.crm.ui.components.icon as sharedIcon
import com.zaroslikov.crm.ui.theme.black_2
import com.zaroslikov.crm.ui.theme.blue_4
import com.zaroslikov.crm.ui.theme.error_base
import com.zaroslikov.crm.ui.theme.gray_6
import com.zaroslikov.crm.ui.theme.gray_7
import com.zaroslikov.crm.ui.theme.gray_8
import com.zaroslikov.crm.ui.theme.green_6
import com.zaroslikov.crm.ui.theme.green_shamrock
import com.zaroslikov.crm.ui.theme.grey_2
import com.zaroslikov.crm.ui.theme.marengo
import com.zaroslikov.crm.ui.theme.orang_9
import com.zaroslikov.crm.ui.theme.price_green
import com.zaroslikov.crm.ui.theme.purple
import com.zaroslikov.crm.ui.theme.red_7
import com.zaroslikov.crm.ui.theme.textBold_12
import com.zaroslikov.crm.ui.theme.textBold_14
import com.zaroslikov.crm.ui.theme.textBold_16
import com.zaroslikov.crm.ui.theme.textBold_24
import com.zaroslikov.crm.ui.theme.text_12
import com.zaroslikov.crm.ui.theme.text_14
import com.zaroslikov.crm.ui.theme.white
import com.zaroslikov.crm.util.copyCrmText
import com.zaroslikov.crm.util.crmClientActivity
import com.zaroslikov.crm.util.crmFormat
import com.zaroslikov.crm.util.crmMoney
import com.zaroslikov.crm.util.isoToDisplay
import com.zaroslikov.crm.util.monthShortRu
import com.zaroslikov.crm.util.CrmContactOpenResult
import com.zaroslikov.crm.util.openCrmContactResult
import com.zaroslikov.crm.util.toCrmResId
import com.zaroslikov.crm.util.toResId
import com.zaroslikov.crm.util.today
import com.zaroslikov.domain.models.DomainSaleTable
import kotlinx.coroutines.launch
import java.time.DateTimeException
import java.time.LocalDate

/** Иконка типа контакта для редактора клиента; сама таблица иконок — в `components`. */
internal fun CrmContactType.icon(): ImageVector = sharedIcon()

@Composable
fun CrmClientCardScreen(
    navigateBack: () -> Unit,
    navigateToClientEdit: (Long) -> Unit,
    viewModel: CrmClientCardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                CrmUiEvent.NavigateBack -> navigateBack()
                is CrmUiEvent.Message -> snackbarHostState.showSnackbar(context.getString(event.resId))
            }
        }
    }

    val showMessage: (Int) -> Unit = { resId ->
        scope.launch { snackbarHostState.showSnackbar(context.getString(resId)) }
    }

    Scaffold(
        topBar = {
            CrmTopBar(
                title = stringResource(R.string.crm_client_card_title),
                navigateBack = navigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = gray_6
    ) { innerPadding ->
        val data = state.data
        if (state.isLoading || data == null)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                // Загрузка упала до первых данных — без вечного индикатора
                if (state.isLoading) CircularProgressIndicator(color = green_6)
                else Text(text = stringResource(R.string.crm_msg_load_error), style = text_14, color = gray_7)
            }
        else
            CrmClientCardBody(
                state = state,
                data = data,
                onIntent = viewModel::onIntent,
                onEdit = { navigateToClientEdit(data.client.id) },
                onContactClick = { contact ->
                    when (context.openCrmContactResult(contact.type, contact.value)) {
                        CrmContactOpenResult.OPENED -> Unit
                        CrmContactOpenResult.NO_APP -> showMessage(R.string.crm_msg_no_app)
                        CrmContactOpenResult.COPIED -> showMessage(R.string.crm_msg_copied)
                    }
                },
                onContactCopy = { contact ->
                    context.copyCrmText(contact.value)
                    showMessage(R.string.crm_msg_copied)
                },
                modifier = Modifier.padding(innerPadding)
            )
    }

    if (state.showArchiveDialog)
        CrmConfirmDialog(
            title = stringResource(R.string.crm_archive_confirm_title),
            text = stringResource(R.string.crm_archive_confirm_text),
            confirmText = stringResource(R.string.crm_to_archive),
            onConfirm = { viewModel.onIntent(CrmClientCardIntent.ConfirmArchive) },
            onDismiss = { viewModel.onIntent(CrmClientCardIntent.DismissArchiveDialog) }
        )

    if (state.interactionToDelete != null)
        CrmConfirmDialog(
            title = stringResource(R.string.crm_delete_interaction_title),
            text = stringResource(R.string.crm_delete_interaction_text),
            confirmText = stringResource(R.string.crm_delete),
            onConfirm = { viewModel.onIntent(CrmClientCardIntent.ConfirmDeleteInteraction) },
            onDismiss = { viewModel.onIntent(CrmClientCardIntent.DismissDeleteInteraction) }
        )

    state.interactionSheet?.let { draft ->
        CrmInteractionSheet(draft = draft, onIntent = viewModel::onIntent)
    }

    state.taskSheet?.let { draft ->
        CrmClientTaskSheet(draft = draft, onIntent = viewModel::onIntent)
    }
}

@Composable
private fun CrmClientCardBody(
    state: CrmClientCardState,
    data: CrmClientCardData,
    onIntent: (CrmClientCardIntent) -> Unit,
    onEdit: () -> Unit,
    onContactClick: (DomainCrmContact) -> Unit,
    onContactCopy: (DomainCrmContact) -> Unit,
    modifier: Modifier = Modifier
) {
    val currency = stringResource(data.currency.toCrmResId())

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { CrmClientHeader(data = data, onIntent = onIntent, onEdit = onEdit) }
        item {
            CrmContactsCard(
                contacts = data.contacts,
                onContactClick = onContactClick,
                onContactCopy = onContactCopy,
                onAddContact = onEdit
            )
        }
        item { CrmClientKpi(data = data, currency = currency) }
        item { CrmClientChart(data = data, currency = currency) }
        item {
            CrmPurchasesCard(
                sales = data.sales,
                showAll = state.showAllSales,
                currency = currency,
                onToggleShowAll = { onIntent(CrmClientCardIntent.ToggleShowAllSales) }
            )
        }
        if (data.products.isNotEmpty())
            item { CrmTopProductsCard(data = data, currency = currency) }
        item { CrmInteractionsCard(interactions = data.interactions, onIntent = onIntent) }
        item { CrmClientTasksCard(tasks = data.tasks, onIntent = onIntent) }
        item { CrmNoteCard(note = data.client.note, onEdit = onEdit) }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun CrmClientHeader(
    data: CrmClientCardData,
    onIntent: (CrmClientCardIntent) -> Unit,
    onEdit: () -> Unit
) {
    val client = data.client
    val activity = crmClientActivity(data.stats?.firstDate, data.stats?.lastDate)

    CrmCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CrmAvatar(name = client.name, size = 64.dp)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = client.name,
                    style = textBold_24,
                    color = black_2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CrmBadge(text = stringResource(activity.toResId()), color = activity.color())
                    if (client.isVip)
                        CrmBadge(text = stringResource(R.string.crm_vip), color = purple)
                    if (client.isArchived)
                        CrmBadge(text = stringResource(R.string.crm_archived), color = red_7)
                }
            }
            IconButton(onClick = { onIntent(CrmClientCardIntent.ToggleFavorite) }) {
                Icon(
                    imageVector = if (client.isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = stringResource(
                        if (client.isFavorite) R.string.crm_remove_favorite else R.string.crm_add_favorite
                    ),
                    tint = if (client.isFavorite) orang_9 else gray_7
                )
            }
        }

        Text(
            text = stringResource(
                if (client.source == CrmClientSource.SALES) R.string.crm_source_sales
                else R.string.crm_source_manual
            ) + " · " + stringResource(R.string.crm_client_since, isoToDisplay(client.createdDate)),
            style = text_12,
            color = gray_7
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Узкие кнопки: уменьшенные отступы, иначе подписи не помещаются на половине ширины
            val buttonPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
            Button(
                onClick = onEdit,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                contentPadding = buttonPadding,
                colors = ButtonDefaults.buttonColors(containerColor = green_6, contentColor = white)
            ) {
                Icon(imageVector = Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = stringResource(R.string.crm_edit_short),
                    style = textBold_14,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (!client.isArchived)
                OutlinedButton(
                    onClick = { onIntent(CrmClientCardIntent.ShowArchiveDialog) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = buttonPadding
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Archive,
                        contentDescription = null,
                        tint = error_base,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = stringResource(R.string.crm_to_archive),
                        style = textBold_14,
                        color = error_base,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CrmContactsCard(
    contacts: List<DomainCrmContact>,
    onContactClick: (DomainCrmContact) -> Unit,
    onContactCopy: (DomainCrmContact) -> Unit,
    onAddContact: () -> Unit
) {
    CrmCard {
        CrmSectionTitle(title = stringResource(R.string.crm_contacts))
        if (contacts.isEmpty()) {
            Text(text = stringResource(R.string.crm_no_contacts), style = text_14, color = gray_7)
            OutlinedButton(onClick = onAddContact, shape = RoundedCornerShape(12.dp)) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = green_shamrock)
                Spacer(modifier = Modifier.size(6.dp))
                Text(text = stringResource(R.string.crm_add_contact), style = text_14, color = green_shamrock)
            }
        } else
            contacts.sortedByDescending { it.isPrimary }.forEach { contact ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onContactClick(contact) },
                            onLongClick = { onContactCopy(contact) }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CrmIconCircle(icon = contact.type.icon(), color = green_shamrock, size = 40.dp)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = contact.value,
                            style = textBold_14,
                            color = black_2,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(contact.type.toResId()) +
                                    if (contact.isPrimary) " · " + stringResource(R.string.crm_primary_contact) else "",
                            style = text_12,
                            color = gray_7
                        )
                    }
                    IconButton(onClick = { onContactCopy(contact) }) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = stringResource(R.string.crm_copy),
                            tint = gray_7,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
    }
}

@Composable
private fun CrmClientKpi(data: CrmClientCardData, currency: String) {
    val stats = data.stats
    val revenue = stats?.total ?: 0.0
    val orders = stats?.orders ?: 0
    val avgCheck = if (orders > 0) revenue / orders else 0.0
    val dash = stringResource(R.string.crm_dash)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CrmKpiTile(
                icon = Icons.Rounded.Payments,
                label = stringResource(R.string.crm_kpi_revenue),
                value = revenue.crmMoney(currency),
                color = green_6,
                modifier = Modifier.weight(1f)
            )
            CrmKpiTile(
                icon = Icons.Rounded.ShoppingCart,
                label = stringResource(R.string.crm_kpi_orders),
                value = orders.crmFormat(),
                color = blue_4,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CrmKpiTile(
                icon = Icons.Rounded.Receipt,
                label = stringResource(R.string.crm_kpi_avg_check),
                value = avgCheck.crmMoney(currency),
                color = purple,
                modifier = Modifier.weight(1f)
            )
            CrmKpiTile(
                icon = Icons.Rounded.Event,
                label = stringResource(R.string.crm_last_purchase),
                value = stats?.lastDate?.let(::isoToDisplay) ?: dash,
                color = orang_9,
                caption = stringResource(R.string.crm_first_purchase) + ": " +
                        (stats?.firstDate?.let(::isoToDisplay) ?: dash),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CrmClientChart(data: CrmClientCardData, currency: String) {
    CrmCard {
        CrmSectionTitle(title = stringResource(R.string.crm_client_revenue_chart))
        Text(text = stringResource(R.string.crm_revenue_by_month_sup), style = text_12, color = gray_7)
        if (data.monthly.all { it.total == 0.0 })
            Text(
                text = stringResource(R.string.crm_chart_empty),
                style = text_14,
                color = gray_7,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            )
        else
            CrmLineChart(
                values = data.monthly.map { it.total.toFloat() },
                labels = data.monthly.map { monthShortRu(it.month) },
                suffix = currency
            )
    }
}

@Composable
private fun CrmPurchasesCard(
    sales: List<DomainSaleTable>,
    showAll: Boolean,
    currency: String,
    onToggleShowAll: () -> Unit
) {
    CrmCard(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CrmSectionTitle(title = stringResource(R.string.crm_purchases))
        if (sales.isEmpty()) {
            Text(text = stringResource(R.string.crm_no_purchases), style = text_14, color = gray_7)
            return@CrmCard
        }
        CrmTableRow(
            cells = listOf(
                stringResource(R.string.crm_col_product),
                stringResource(R.string.crm_col_count),
                stringResource(R.string.crm_col_sum),
                stringResource(R.string.crm_col_date)
            ),
            isHeader = true
        )
        HorizontalDivider(color = grey_2)
        val visible = if (showAll) sales else sales.take(CRM_CARD_SALES_PREVIEW)
        visible.forEach { sale ->
            CrmTableRow(
                cells = listOf(
                    sale.title,
                    "${sale.count.crmFormat()} ${stringResource(sale.countSuffix.toCrmResId())}".trim(),
                    (sale.priceAll ?: sale.price).crmMoney(currency),
                    sale.displayDate()
                ),
                isHeader = false
            )
        }
        if (sales.size > CRM_CARD_SALES_PREVIEW)
            TextButton(onClick = onToggleShowAll, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(
                    text = if (showAll) stringResource(R.string.crm_show_less)
                    else stringResource(R.string.crm_show_all, sales.size),
                    style = text_14,
                    color = green_shamrock
                )
            }
    }
}

/** Дата продажи `dd.MM.yyyy`; битая дата в БД не роняет экран — показываем прочерк. */
private fun DomainSaleTable.displayDate(): String =
    try {
        isoToDisplay(LocalDate.of(year, month, day).toString())
    } catch (e: DateTimeException) {
        "—"
    }

private val TABLE_WEIGHTS = listOf(1.6f, 1f, 1.2f, 1.1f)

@Composable
private fun CrmTableRow(cells: List<String>, isHeader: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        cells.forEachIndexed { index, text ->
            Text(
                text = text,
                style = if (isHeader) textBold_12 else text_12,
                color = when {
                    isHeader -> gray_7
                    index == 2 -> price_green
                    else -> black_2
                },
                fontWeight = if (!isHeader && index == 2) FontWeight.SemiBold else null,
                textAlign = if (index == 0) TextAlign.Start else TextAlign.End,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(TABLE_WEIGHTS[index])
            )
        }
    }
}

@Composable
private fun CrmTopProductsCard(data: CrmClientCardData, currency: String) {
    val max = data.products.maxOf { it.total }.takeIf { it > 0 } ?: 1.0
    CrmCard {
        CrmSectionTitle(title = stringResource(R.string.crm_top_products))
        data.products.take(5).forEach { product ->
            val suffix = stringResource(product.suffix.toCrmResId())
            CrmHorizontalBar(
                label = product.title,
                valueText = "${product.count.crmFormat()} $suffix".trim() + " · " + product.total.crmMoney(currency),
                fraction = (product.total / max).toFloat(),
                color = green_6
            )
        }
    }
}

@Composable
private fun CrmInteractionsCard(
    interactions: List<DomainCrmInteraction>,
    onIntent: (CrmClientCardIntent) -> Unit
) {
    CrmCard {
        CrmSectionTitle(
            title = stringResource(R.string.crm_interactions),
            actionText = stringResource(R.string.crm_add),
            onAction = { onIntent(CrmClientCardIntent.OpenInteractionSheet(today())) }
        )
        if (interactions.isEmpty())
            Text(text = stringResource(R.string.crm_no_interactions), style = text_14, color = gray_7)
        else
            interactions.forEach { interaction ->
                Row(verticalAlignment = Alignment.Top) {
                    CrmIconCircle(
                        icon = interaction.type.icon(),
                        color = interaction.type.color(),
                        size = 36.dp
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = stringResource(interaction.type.toResId()) + " · " + isoToDisplay(interaction.date),
                            style = text_12,
                            color = gray_7
                        )
                        Text(text = interaction.note, style = text_14, color = black_2)
                    }
                    IconButton(onClick = { onIntent(CrmClientCardIntent.AskDeleteInteraction(interaction)) }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = stringResource(R.string.crm_delete),
                            tint = gray_8,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
    }
}

@Composable
private fun CrmClientTasksCard(
    tasks: List<DomainCrmTask>,
    onIntent: (CrmClientCardIntent) -> Unit
) {
    val today = today()
    CrmCard(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        CrmSectionTitle(
            title = stringResource(R.string.crm_client_tasks),
            actionText = stringResource(R.string.crm_add),
            onAction = { onIntent(CrmClientCardIntent.OpenTaskSheet(today)) }
        )
        if (tasks.isEmpty())
            Text(text = stringResource(R.string.crm_no_tasks), style = text_14, color = gray_7)
        else
            tasks.sortedWith(compareBy({ it.isDone }, { it.dueDate })).forEach { task ->
                // ISO-даты сравниваются как строки
                val isOverdue = !task.isDone && task.dueDate < today
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = task.isDone,
                        onCheckedChange = { onIntent(CrmClientCardIntent.ToggleTaskDone(task)) },
                        colors = CheckboxDefaults.colors(checkedColor = green_6, uncheckedColor = gray_8)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            style = textBold_14,
                            color = if (task.isDone) gray_7 else black_2,
                            textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarMonth,
                                contentDescription = null,
                                tint = if (isOverdue) error_base else gray_7,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = " " + isoToDisplay(task.dueDate) +
                                        if (isOverdue) " · " + stringResource(R.string.crm_tasks_overdue) else "",
                                style = text_12,
                                color = if (isOverdue) error_base else gray_7
                            )
                        }
                        if (task.note.isNotBlank())
                            Text(text = task.note, style = text_12, color = marengo)
                    }
                }
            }
    }
}

@Composable
private fun CrmNoteCard(note: String, onEdit: () -> Unit) {
    CrmCard {
        CrmSectionTitle(
            title = stringResource(R.string.crm_note),
            actionText = stringResource(R.string.crm_edit),
            onAction = onEdit
        )
        Text(
            text = note.ifBlank { stringResource(R.string.crm_no_note) },
            style = text_14,
            color = if (note.isBlank()) gray_7 else black_2
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CrmInteractionSheet(
    draft: CrmInteractionDraft,
    onIntent: (CrmClientCardIntent) -> Unit
) {
    val types = CrmInteractionType.entries
    CrmBottomSheet(
        title = stringResource(R.string.crm_new_interaction),
        onDismiss = { onIntent(CrmClientCardIntent.CloseInteractionSheet) }
    ) {
        Text(text = stringResource(R.string.crm_field_interaction_type), style = text_14, color = marengo)
        CrmSegmentedButtons(
            options = types.map { stringResource(it.toResId()) },
            selectedIndex = types.indexOf(draft.type),
            onSelect = { onIntent(CrmClientCardIntent.InteractionTypeChange(types[it])) }
        )
        CrmDatePickerField(
            value = draft.date,
            onValueChange = { onIntent(CrmClientCardIntent.InteractionDateChange(it)) },
            label = stringResource(R.string.crm_field_date),
            isError = draft.dateError,
            errorText = stringResource(R.string.crm_error_date_empty)
        )
        CrmTextField(
            value = draft.note,
            onValueChange = { onIntent(CrmClientCardIntent.InteractionNoteChange(it)) },
            label = stringResource(R.string.crm_field_note),
            singleLine = false,
            minLines = 3,
            isError = draft.noteError,
            errorText = stringResource(R.string.crm_error_note_empty),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
        CrmSheetSaveButton(onClick = { onIntent(CrmClientCardIntent.SaveInteraction) })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CrmClientTaskSheet(
    draft: CrmTaskDraft,
    onIntent: (CrmClientCardIntent) -> Unit
) {
    CrmBottomSheet(
        title = stringResource(R.string.crm_new_task),
        onDismiss = { onIntent(CrmClientCardIntent.CloseTaskSheet) }
    ) {
        CrmTextField(
            value = draft.title,
            onValueChange = { onIntent(CrmClientCardIntent.TaskTitleChange(it)) },
            label = stringResource(R.string.crm_field_task_title),
            isError = draft.titleError,
            errorText = stringResource(R.string.crm_error_title_empty),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
        CrmDatePickerField(
            value = draft.dueDate,
            onValueChange = { onIntent(CrmClientCardIntent.TaskDateChange(it)) },
            label = stringResource(R.string.crm_field_due_date),
            isError = draft.dateError,
            errorText = stringResource(R.string.crm_error_date_empty)
        )
        CrmTextField(
            value = draft.note,
            onValueChange = { onIntent(CrmClientCardIntent.TaskNoteChange(it)) },
            label = stringResource(R.string.crm_field_note),
            singleLine = false,
            minLines = 2,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
        CrmSheetSaveButton(onClick = { onIntent(CrmClientCardIntent.SaveTask) })
    }
}

@Composable
private fun CrmSheetSaveButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = green_6, contentColor = white)
    ) {
        Text(text = stringResource(R.string.crm_save), style = textBold_16, modifier = Modifier.padding(vertical = 4.dp))
    }
}
