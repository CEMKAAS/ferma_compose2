package com.zaroslikov.todo.ui.assignees

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.zaroslikov.todo.R
import com.zaroslikov.todo.ui.base.TodoSnackbarHost
import com.zaroslikov.todo.ui.base.TodoUiEvent
import com.zaroslikov.todo.ui.base.showTodoMessage
import com.zaroslikov.todo.ui.components.TodoCard
import com.zaroslikov.todo.ui.components.TodoConfirmDialog
import com.zaroslikov.todo.ui.components.TodoEmptyState
import com.zaroslikov.todo.ui.components.TodoFab
import com.zaroslikov.todo.ui.components.TodoTopBar
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.green_6
import com.zaroslikov.todo.ui.theme.textBold_16

@Composable
internal fun TodoAssigneesScreen(
    navigateBack: () -> Unit,
    viewModel: TodoAssigneesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.events.collect { event ->
                if (event is TodoUiEvent.Message) snackbarHostState.showTodoMessage(context, event)
            }
        }
    }

    TodoAssigneesContent(
        state = state,
        onIntent = viewModel::onIntent,
        navigateBack = navigateBack,
        snackbarHost = { TodoSnackbarHost(snackbarHostState) }
    )
}

@Composable
internal fun TodoAssigneesContent(
    state: TodoAssigneesState,
    onIntent: (TodoAssigneesIntent) -> Unit,
    navigateBack: () -> Unit,
    snackbarHost: @Composable () -> Unit = {}
) {
    val openCreate = { onIntent(TodoAssigneesIntent.OpenForm()) }
    Scaffold(
        topBar = { TodoTopBar(title = stringResource(R.string.todo_assignees), navigateBack = navigateBack) },
        snackbarHost = snackbarHost,
        floatingActionButton = {
            if (state.assignees.isNotEmpty()) {
                TodoFab(onClick = openCreate, contentDescription = stringResource(R.string.todo_assignee_add))
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when {
            state.isLoading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = green_6)
            }

            state.assignees.isEmpty() -> TodoEmptyState(
                icon = Icons.Rounded.Person,
                title = stringResource(R.string.todo_assignees_empty),
                text = stringResource(R.string.todo_assignees_empty_text),
                buttonText = stringResource(R.string.todo_assignee_add),
                onButtonClick = openCreate,
                modifier = modifier
            )

            else -> LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.assignees, key = { it.name }) { assignee ->
                    TodoAssigneeRow(
                        name = assignee.name,
                        enabled = !state.isSaving,
                        onOpen = { onIntent(TodoAssigneesIntent.OpenProfile(assignee.name)) },
                        onEdit = { onIntent(TodoAssigneesIntent.OpenForm(assignee.name)) },
                        onDelete = { onIntent(TodoAssigneesIntent.RequestDelete(assignee.name)) }
                    )
                }
            }
        }
    }

    if (state.form != null) {
        TodoAssigneeFormSheet(form = state.form, isSaving = state.isSaving, onIntent = onIntent)
    } else {
        state.viewedAssignee?.let { assignee ->
            TodoAssigneeProfileSheet(
                assignee = assignee,
                onEdit = { onIntent(TodoAssigneesIntent.OpenForm(assignee.name)) },
                onDismiss = { onIntent(TodoAssigneesIntent.Dismiss) }
            )
        }
    }
    state.nameToDelete?.let { name ->
        TodoConfirmDialog(
            title = stringResource(R.string.todo_assignee_delete_title),
            text = stringResource(R.string.todo_assignee_delete_text, name),
            confirmText = stringResource(R.string.todo_delete),
            onConfirm = { onIntent(TodoAssigneesIntent.Delete) },
            onDismiss = { onIntent(TodoAssigneesIntent.Dismiss) }
        )
    }
}

@Composable
private fun TodoAssigneeRow(
    name: String,
    enabled: Boolean,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    TodoCard(
        modifier = Modifier.fillMaxWidth(),
        padding = PaddingValues(start = 16.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
        elevation = 2.dp,
        onClick = if (enabled) onOpen else null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Rounded.Person, contentDescription = null, tint = gray_7)
            Text(
                text = name,
                style = textBold_16,
                color = black_2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEdit, enabled = enabled) {
                Icon(Icons.Rounded.Edit, contentDescription = stringResource(R.string.todo_assignee_edit_name, name))
            }
            IconButton(onClick = onDelete, enabled = enabled) {
                Icon(
                    Icons.Rounded.DeleteOutline,
                    contentDescription = stringResource(R.string.todo_assignee_delete_name, name)
                )
            }
        }
    }
}
