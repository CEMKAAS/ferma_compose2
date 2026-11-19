package com.zaroslikov.todo.ui.tags

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
import androidx.compose.material.icons.rounded.LocalOffer
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
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.ui.base.TodoUiEvent
import com.zaroslikov.todo.ui.components.TodoCard
import com.zaroslikov.todo.ui.components.TodoColorDot
import com.zaroslikov.todo.ui.components.TodoConfirmDialog
import com.zaroslikov.todo.ui.components.TodoEmptyState
import com.zaroslikov.todo.ui.components.TodoFab
import com.zaroslikov.todo.ui.components.TodoTopBar
import com.zaroslikov.todo.ui.lists.TodoTitleColorSheet
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.green_6
import com.zaroslikov.todo.ui.theme.textBold_16
import com.zaroslikov.todo.ui.theme.todoColor

@Composable
fun TodoTagsScreen(
    navigateBack: () -> Unit,
    viewModel: TodoTagsViewModel = hiltViewModel()
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
        topBar = { TodoTopBar(title = stringResource(R.string.todo_tags_title), navigateBack = navigateBack) },
        snackbarHost = { TodoSnackbarHost(snackbarHostState) },
        floatingActionButton = {
            TodoFab(
                onClick = { viewModel.onIntent(TodoTagsIntent.OpenCreate) },
                contentDescription = stringResource(R.string.todo_tags_new)
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when {
            state.isLoading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = green_6)
            }

            state.tags.isEmpty() -> TodoEmptyState(
                icon = Icons.Rounded.LocalOffer,
                title = stringResource(R.string.todo_tags_empty_title),
                text = stringResource(R.string.todo_tags_empty_text),
                buttonText = stringResource(R.string.todo_tags_new),
                onButtonClick = { viewModel.onIntent(TodoTagsIntent.OpenCreate) },
                modifier = modifier
            )

            else -> LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.tags, key = { it.id }) { tag ->
                    TodoTagRow(
                        tag = tag,
                        onEdit = { viewModel.onIntent(TodoTagsIntent.OpenEdit(tag)) },
                        onDelete = { viewModel.onIntent(TodoTagsIntent.RequestDelete(tag)) }
                    )
                }
            }
        }
    }

    state.form?.let { form ->
        TodoTitleColorSheet(
            sheetTitle = stringResource(if (form.isNew) R.string.todo_tags_new else R.string.todo_tags_edit),
            form = form,
            onTitleChange = { viewModel.onIntent(TodoTagsIntent.FormTitleChanged(it)) },
            onColorChange = { viewModel.onIntent(TodoTagsIntent.FormColorChanged(it)) },
            onSave = { viewModel.onIntent(TodoTagsIntent.SaveForm) },
            onDismiss = { viewModel.onIntent(TodoTagsIntent.CloseForm) }
        )
    }

    state.tagToDelete?.let { tag ->
        TodoConfirmDialog(
            title = stringResource(R.string.todo_tags_delete_title),
            text = stringResource(R.string.todo_tags_delete_text, tag.title),
            confirmText = stringResource(R.string.todo_delete),
            onConfirm = { viewModel.onIntent(TodoTagsIntent.ConfirmDelete) },
            onDismiss = { viewModel.onIntent(TodoTagsIntent.DismissDelete) }
        )
    }
}

@Composable
private fun TodoTagRow(
    tag: DomainTodoTag,
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
            TodoColorDot(color = todoColor(tag.colorIndex), size = 16.dp)
            Text(
                text = tag.title,
                style = textBold_16,
                color = black_2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = stringResource(R.string.todo_edit_short),
                    tint = gray_7
                )
            }
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
