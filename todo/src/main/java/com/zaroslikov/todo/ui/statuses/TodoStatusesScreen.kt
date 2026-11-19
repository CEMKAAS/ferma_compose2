package com.zaroslikov.todo.ui.statuses

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import com.zaroslikov.todo.ui.base.showTodoMessage
import com.zaroslikov.todo.ui.base.TodoSnackbarHost
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.ui.base.TodoUiEvent
import com.zaroslikov.todo.ui.components.TodoCard
import com.zaroslikov.todo.ui.components.TodoColorDot
import com.zaroslikov.todo.ui.components.TodoConfirmDialog
import com.zaroslikov.todo.ui.components.TodoFab
import com.zaroslikov.todo.ui.components.TodoTopBar
import com.zaroslikov.todo.ui.components.color
import com.zaroslikov.todo.ui.components.displayTitle
import com.zaroslikov.todo.ui.lists.TodoTitleColorSheet
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.green_6
import com.zaroslikov.todo.ui.theme.textBold_16
import com.zaroslikov.todo.ui.theme.text_12

@Composable
fun TodoStatusesScreen(
    navigateBack: () -> Unit,
    viewModel: TodoStatusesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    // События — только в RESUMED: «назад» снимает лишь такой экран (todoNavGraph), а событие
    // во время анимации перехода дождётся в Channel
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.events.collect { event ->
                when (event) {
                    TodoUiEvent.NavigateBack -> navigateBack()
                    is TodoUiEvent.Message -> snackbarHostState.showTodoMessage(context, event)
                }
            }
        }
    }

    Scaffold(
        topBar = { TodoTopBar(title = stringResource(R.string.todo_statuses_title), navigateBack = navigateBack) },
        snackbarHost = { TodoSnackbarHost(snackbarHostState) },
        floatingActionButton = {
            TodoFab(
                onClick = { viewModel.onIntent(TodoStatusesIntent.OpenCreate) },
                contentDescription = stringResource(R.string.todo_statuses_new)
            )
        }
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
        else LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.statuses, key = { it.id }) { status ->
                TodoStatusRow(
                    status = status,
                    onEdit = { viewModel.onIntent(TodoStatusesIntent.OpenEdit(status)) },
                    onDelete = { viewModel.onIntent(TodoStatusesIntent.RequestDelete(status)) }
                )
            }
        }
    }

    state.form?.let { form ->
        TodoTitleColorSheet(
            sheetTitle = stringResource(if (form.isNew) R.string.todo_statuses_new else R.string.todo_statuses_edit),
            form = form,
            onTitleChange = { viewModel.onIntent(TodoStatusesIntent.FormTitleChanged(it)) },
            onColorChange = { viewModel.onIntent(TodoStatusesIntent.FormColorChanged(it)) },
            onSave = { viewModel.onIntent(TodoStatusesIntent.SaveForm) },
            onDismiss = { viewModel.onIntent(TodoStatusesIntent.CloseForm) },
            lockedTitle = state.editingStatus?.displayTitle() ?: form.title
        )
    }

    state.statusToDelete?.let { status ->
        TodoConfirmDialog(
            title = stringResource(R.string.todo_statuses_delete_title),
            text = stringResource(R.string.todo_statuses_delete_text, status.displayTitle()),
            confirmText = stringResource(R.string.todo_delete),
            onConfirm = { viewModel.onIntent(TodoStatusesIntent.ConfirmDelete) },
            onDismiss = { viewModel.onIntent(TodoStatusesIntent.DismissDelete) }
        )
    }
}

/** Строка статуса: цвет, название, пометка «по умолчанию»; удалить можно только пользовательский. */
@Composable
private fun TodoStatusRow(
    status: DomainTodoStatus,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    TodoCard(
        modifier = Modifier.fillMaxWidth(),
        padding = PaddingValues(start = 16.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
        elevation = 2.dp,
        onClick = onEdit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TodoColorDot(color = status.color(), size = 16.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = status.displayTitle(),
                    style = textBold_16,
                    color = black_2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (status.isDefault)
                    Text(text = stringResource(R.string.todo_status_default), style = text_12, color = gray_7)
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = stringResource(R.string.todo_edit_short),
                    tint = gray_7
                )
            }
            if (!status.isDefault)
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = stringResource(R.string.todo_delete),
                        tint = gray_7
                    )
                }
        }
    }
}
