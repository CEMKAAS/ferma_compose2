package com.zaroslikov.crm.ui.clients

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.crm.R
import com.zaroslikov.crm.domain.model.CrmClientSource
import com.zaroslikov.crm.ui.base.CrmUiEvent
import com.zaroslikov.crm.ui.components.CrmAvatar
import com.zaroslikov.crm.ui.components.CrmBadge
import com.zaroslikov.crm.ui.components.CrmCard
import com.zaroslikov.crm.ui.components.CrmChip
import com.zaroslikov.crm.ui.components.CrmConfirmDialog
import com.zaroslikov.crm.ui.components.CrmEmptyState
import com.zaroslikov.crm.ui.components.CrmFab
import com.zaroslikov.crm.ui.components.CrmTextField
import com.zaroslikov.crm.ui.components.CrmTopBar
import com.zaroslikov.crm.ui.components.color
import com.zaroslikov.crm.ui.navigation.CRM_NEW_CLIENT_ID
import com.zaroslikov.crm.ui.theme.black_2
import com.zaroslikov.crm.ui.theme.error_base
import com.zaroslikov.crm.ui.theme.gray_7
import com.zaroslikov.crm.ui.theme.green_6
import com.zaroslikov.crm.ui.theme.green_shamrock
import com.zaroslikov.crm.ui.theme.orang_9
import com.zaroslikov.crm.ui.theme.price_green
import com.zaroslikov.crm.ui.theme.purple
import com.zaroslikov.crm.ui.theme.textBold_14
import com.zaroslikov.crm.ui.theme.textBold_16
import com.zaroslikov.crm.ui.theme.text_12
import com.zaroslikov.crm.ui.theme.text_16
import com.zaroslikov.crm.util.crmFormat
import com.zaroslikov.crm.util.crmMoney
import com.zaroslikov.crm.util.isoToDisplay
import com.zaroslikov.crm.util.toCrmResId
import com.zaroslikov.crm.util.toResId

@Composable
fun CrmClientsScreen(
    navigateBack: () -> Unit,
    navigateToClient: (Long) -> Unit,
    navigateToClientEdit: (Long) -> Unit,
    viewModel: CrmClientsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showSortMenu by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                CrmUiEvent.NavigateBack -> navigateBack()
                is CrmUiEvent.Message -> snackbarHostState.showSnackbar(context.getString(event.resId))
            }
        }
    }

    Scaffold(
        topBar = {
            CrmTopBar(
                title = stringResource(R.string.crm_clients_title),
                navigateBack = navigateBack,
                actions = {
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Sort,
                                contentDescription = stringResource(R.string.crm_sort_title),
                                tint = black_2
                            )
                        }
                        CrmSortMenu(
                            expanded = showSortMenu,
                            selected = state.sort,
                            onSelect = {
                                showSortMenu = false
                                viewModel.onIntent(CrmClientsIntent.SelectSort(it))
                            },
                            onDismiss = { showSortMenu = false }
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            CrmFab(
                onClick = { navigateToClientEdit(CRM_NEW_CLIENT_ID) },
                icon = Icons.Rounded.PersonAdd,
                contentDescription = stringResource(R.string.crm_action_add_client)
            )
        }
    ) { innerPadding ->
        CrmClientsBody(
            state = state,
            onIntent = viewModel::onIntent,
            navigateToClient = navigateToClient,
            navigateToClientEdit = navigateToClientEdit,
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (state.clientToDelete != null)
        CrmConfirmDialog(
            title = stringResource(R.string.crm_delete_forever_title),
            text = stringResource(R.string.crm_delete_forever_text),
            confirmText = stringResource(R.string.crm_delete_forever),
            onConfirm = { viewModel.onIntent(CrmClientsIntent.ConfirmDelete) },
            onDismiss = { viewModel.onIntent(CrmClientsIntent.DismissDelete) }
        )
}

@Composable
private fun CrmSortMenu(
    expanded: Boolean,
    selected: CrmClientsSort,
    onSelect: (CrmClientsSort) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        CrmClientsSort.entries.forEach { sort ->
            DropdownMenuItem(
                text = { Text(text = stringResource(sort.toResId()), style = text_16, color = black_2) },
                onClick = { onSelect(sort) },
                trailingIcon = if (sort == selected) {
                    { Icon(imageVector = Icons.Rounded.Check, contentDescription = null, tint = green_shamrock) }
                } else null
            )
        }
    }
}

@Composable
private fun CrmClientsBody(
    state: CrmClientsState,
    onIntent: (CrmClientsIntent) -> Unit,
    navigateToClient: (Long) -> Unit,
    navigateToClientEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        CrmTextField(
            value = state.query,
            onValueChange = { onIntent(CrmClientsIntent.Search(it)) },
            label = stringResource(R.string.crm_search),
            placeholder = stringResource(R.string.crm_search_hint),
            leadingIcon = Icons.Rounded.Search,
            trailingIcon = if (state.query.isNotEmpty()) {
                {
                    IconButton(onClick = { onIntent(CrmClientsIntent.Search("")) }) {
                        Icon(imageVector = Icons.Rounded.Clear, contentDescription = stringResource(R.string.crm_clear))
                    }
                }
            } else null,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CrmClientsFilter.entries.forEach { filter ->
                CrmChip(
                    text = stringResource(filter.toResId()),
                    selected = state.filter == filter,
                    onClick = { onIntent(CrmClientsIntent.SelectFilter(filter)) }
                )
            }
        }

        val isArchive = state.filter == CrmClientsFilter.ARCHIVE
        val hasClients = if (isArchive) state.archivedRows.isNotEmpty() else state.rows.isNotEmpty()
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = green_6)
            }

            !hasClients && isArchive -> CrmEmptyState(
                icon = Icons.Rounded.Inventory2,
                title = stringResource(R.string.crm_archive_empty_title),
                text = stringResource(R.string.crm_archive_empty_text)
            )

            !hasClients -> CrmEmptyState(
                icon = Icons.Rounded.PersonAdd,
                title = stringResource(R.string.crm_clients_empty_title),
                text = stringResource(R.string.crm_clients_empty_text),
                buttonText = stringResource(R.string.crm_action_add_client),
                onButtonClick = { navigateToClientEdit(CRM_NEW_CLIENT_ID) }
            )

            state.visible.isEmpty() -> CrmEmptyState(
                icon = Icons.Rounded.SearchOff,
                title = stringResource(R.string.crm_clients_not_found_title),
                text = stringResource(R.string.crm_clients_not_found_text)
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.visible, key = { it.client.id }) { row ->
                    CrmClientRowCard(
                        row = row,
                        currency = stringResource(state.currency.toCrmResId()),
                        isArchive = isArchive,
                        onClick = { navigateToClient(row.client.id) },
                        onToggleFavorite = { onIntent(CrmClientsIntent.ToggleFavorite(row.client)) },
                        onRestore = { onIntent(CrmClientsIntent.Restore(row.client)) },
                        onDeleteForever = { onIntent(CrmClientsIntent.RequestDeleteForever(row.client)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CrmClientRowCard(
    row: CrmClientRow,
    currency: String,
    isArchive: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRestore: () -> Unit,
    onDeleteForever: () -> Unit
) {
    val client = row.client
    CrmCard(
        modifier = Modifier.fillMaxWidth(),
        padding = PaddingValues(start = 14.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
        elevation = 2.dp,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        onClick = if (isArchive) null else onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CrmAvatar(name = client.name)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = client.name,
                    style = textBold_16,
                    color = black_2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CrmBadge(text = stringResource(row.activity.toResId()), color = row.activity.color())
                    if (client.isVip)
                        CrmBadge(text = stringResource(R.string.crm_vip), color = purple)
                }
                Text(
                    text = stringResource(R.string.crm_orders_value, row.orders.crmFormat()) +
                            (row.lastDate?.let { " · " + isoToDisplay(it) } ?: ""),
                    style = text_12,
                    color = gray_7,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = row.total.crmMoney(currency),
                    style = textBold_14,
                    color = price_green,
                    maxLines = 1
                )
                if (!isArchive)
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (client.isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                            contentDescription = stringResource(
                                if (client.isFavorite) R.string.crm_remove_favorite else R.string.crm_add_favorite
                            ),
                            tint = if (client.isFavorite) orang_9 else gray_7
                        )
                    }
            }
        }
        if (isArchive)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                // Клиент из продаж вернулся бы при синхронизации — навсегда удаляются только ручные
                if (client.source == CrmClientSource.MANUAL)
                    TextButton(onClick = onDeleteForever) {
                        Text(text = stringResource(R.string.crm_delete_forever), style = textBold_14, color = error_base)
                    }
                OutlinedButton(onClick = onRestore) {
                    Text(text = stringResource(R.string.crm_restore), style = textBold_14, color = green_shamrock)
                }
            }
    }
}

private fun CrmClientsFilter.toResId(): Int = when (this) {
    CrmClientsFilter.ALL -> R.string.crm_filter_all
    CrmClientsFilter.FAVORITE -> R.string.crm_filter_favorite
    CrmClientsFilter.VIP -> R.string.crm_filter_vip
    CrmClientsFilter.NEW -> R.string.crm_filter_new
    CrmClientsFilter.INACTIVE -> R.string.crm_filter_inactive
    CrmClientsFilter.ARCHIVE -> R.string.crm_filter_archive
}

private fun CrmClientsSort.toResId(): Int = when (this) {
    CrmClientsSort.REVENUE -> R.string.crm_sort_revenue
    CrmClientsSort.NAME -> R.string.crm_sort_name
    CrmClientsSort.LAST_PURCHASE -> R.string.crm_sort_last_purchase
}
