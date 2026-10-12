package com.zaroslikov.crm.ui.tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.crm.R
import com.zaroslikov.crm.ui.base.CrmUiEvent
import com.zaroslikov.crm.ui.components.CrmCard
import com.zaroslikov.crm.ui.components.CrmConfirmDialog
import com.zaroslikov.crm.ui.components.CrmEmptyState
import com.zaroslikov.crm.ui.components.CrmFab
import com.zaroslikov.crm.ui.components.CrmSegmentedButtons
import com.zaroslikov.crm.ui.components.CrmTopBar
import com.zaroslikov.crm.ui.theme.black_2
import com.zaroslikov.crm.ui.theme.error_base
import com.zaroslikov.crm.ui.theme.gray_7
import com.zaroslikov.crm.ui.theme.gray_8
import com.zaroslikov.crm.ui.theme.green_6
import com.zaroslikov.crm.ui.theme.green_shamrock
import com.zaroslikov.crm.ui.theme.red_light
import com.zaroslikov.crm.ui.theme.textBold_14
import com.zaroslikov.crm.ui.theme.textBold_16
import com.zaroslikov.crm.ui.theme.text_12
import com.zaroslikov.crm.ui.theme.text_14
import com.zaroslikov.crm.ui.theme.white
import com.zaroslikov.crm.util.isoToDisplay

@Composable
fun CrmTasksScreen(
    navigateBack: () -> Unit,
    navigateToClient: (Long) -> Unit,
    viewModel: CrmTasksViewModel = hiltViewModel()
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
        topBar = { CrmTopBar(title = stringResource(R.string.crm_tasks_title), navigateBack = navigateBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            CrmFab(
                onClick = { viewModel.onIntent(CrmTasksIntent.OpenSheet) },
                contentDescription = stringResource(R.string.crm_new_task)
            )
        }
    ) { innerPadding ->
        CrmTasksBody(
            state = state,
            onIntent = viewModel::onIntent,
            navigateToClient = navigateToClient,
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (state.showSheet)
        CrmTaskSheet(
            clients = state.clients,
            onDismiss = { viewModel.onIntent(CrmTasksIntent.CloseSheet) },
            onSave = { title, dueDate, clientId, note ->
                viewModel.onIntent(CrmTasksIntent.CreateTask(title, dueDate, clientId, note))
            }
        )

    state.taskToDelete?.let { task ->
        CrmConfirmDialog(
            title = stringResource(R.string.crm_delete_task_title),
            text = stringResource(R.string.crm_delete_task_text, task.title),
            confirmText = stringResource(R.string.crm_delete),
            onConfirm = { viewModel.onIntent(CrmTasksIntent.ConfirmDelete) },
            onDismiss = { viewModel.onIntent(CrmTasksIntent.DismissDelete) }
        )
    }
}

@Composable
private fun CrmTasksBody(
    state: CrmTasksState,
    onIntent: (CrmTasksIntent) -> Unit,
    navigateToClient: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        CrmSegmentedButtons(
            options = listOf(
                stringResource(R.string.crm_tasks_active),
                stringResource(R.string.crm_tasks_done)
            ),
            selectedIndex = state.tab.ordinal,
            onSelect = { onIntent(CrmTasksIntent.SelectTab(CrmTasksTab.entries[it])) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = green_6)
            }

            state.groups.isEmpty() -> {
                val isDone = state.tab == CrmTasksTab.DONE
                CrmEmptyState(
                    icon = if (isDone) Icons.Rounded.TaskAlt else Icons.Rounded.EventAvailable,
                    title = stringResource(if (isDone) R.string.crm_tasks_done_empty_title else R.string.crm_tasks_empty_title),
                    text = stringResource(if (isDone) R.string.crm_tasks_done_empty_text else R.string.crm_tasks_empty_text),
                    buttonText = if (isDone) null else stringResource(R.string.crm_new_task),
                    onButtonClick = if (isDone) null else ({ onIntent(CrmTasksIntent.OpenSheet) })
                )
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.groups.forEach { group ->
                    item(key = "header_${group.kind}_${group.date}") {
                        CrmTaskGroupHeader(group = group)
                    }
                    items(group.items, key = { it.task.id }) { item ->
                        CrmTaskRow(
                            item = item,
                            isOverdue = group.kind == CrmTaskGroupKind.OVERDUE,
                            onToggle = { onIntent(CrmTasksIntent.ToggleDone(item.task)) },
                            onClientClick = navigateToClient,
                            onDelete = { onIntent(CrmTasksIntent.RequestDelete(item.task)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CrmTaskGroupHeader(group: CrmTaskGroup) {
    val title = when (group.kind) {
        CrmTaskGroupKind.OVERDUE -> stringResource(R.string.crm_tasks_overdue)
        CrmTaskGroupKind.TODAY -> stringResource(R.string.crm_tasks_today)
        CrmTaskGroupKind.TOMORROW -> stringResource(R.string.crm_tasks_tomorrow)
        CrmTaskGroupKind.DATE -> isoToDisplay(group.date)
    }
    Text(
        text = title,
        style = textBold_16,
        color = if (group.kind == CrmTaskGroupKind.OVERDUE) error_base else black_2,
        modifier = Modifier.padding(top = 8.dp)
    )
}

/**
 * Строка задачи: чекбокс, название, срок, чип клиента и необязательная кнопка удаления.
 * Просроченная задача подсвечивается красным.
 */
@Composable
fun CrmTaskRow(
    item: CrmTaskItem,
    isOverdue: Boolean,
    onToggle: () -> Unit,
    onClientClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null
) {
    val task = item.task
    CrmCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = if (isOverdue) red_light else white,
        padding = PaddingValues(start = 4.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
        elevation = 2.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = green_6, uncheckedColor = gray_8)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = task.title,
                    style = textBold_16,
                    color = if (task.isDone) gray_7 else black_2,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = isoToDisplay(task.dueDate),
                        style = text_12,
                        color = if (isOverdue) error_base else gray_7
                    )
                    val clientId = task.clientId
                    if (clientId != null && item.clientName != null)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onClientClick(clientId) }
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                tint = green_shamrock,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = item.clientName,
                                style = textBold_14,
                                color = green_shamrock,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                }
                if (task.note.isNotBlank())
                    Text(
                        text = task.note,
                        style = text_14,
                        color = gray_7,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
            }
            if (onDelete != null)
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = stringResource(R.string.crm_delete),
                        tint = gray_7
                    )
                }
        }
    }
}
