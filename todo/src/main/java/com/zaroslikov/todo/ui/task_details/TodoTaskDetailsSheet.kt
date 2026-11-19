package com.zaroslikov.todo.ui.task_details

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import com.zaroslikov.todo.ui.base.showTodoMessage
import com.zaroslikov.todo.ui.base.TodoSnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.ui.base.TodoUiEvent
import com.zaroslikov.todo.ui.components.TodoConfirmDialog
import com.zaroslikov.todo.ui.lists.TodoTitleColorSheet
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.green_shamrock
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.textBold_20
import com.zaroslikov.todo.ui.theme.text_14
import com.zaroslikov.todo.ui.theme.gray_6
import com.zaroslikov.todo.ui.theme.gray_8
import com.zaroslikov.todo.ui.theme.white
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.selects.select


/**
 * Шторка «Подробно» на экране-хосте (список, умный список). Открывается интентом
 * [TodoTaskDetailsIntent.Open] у [viewModel]; сообщения при открытой шторке показываются
 * внутри неё, после закрытия — в [snackbarHostState] экрана.
 */
@Composable
fun TodoTaskDetailsHost(
    viewModel: TodoTaskDetailsViewModel,
    snackbarHostState: SnackbarHostState
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val sheetSnackbarHostState = remember { SnackbarHostState() }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.events.collect { event ->
                when (event) {
                    // Шторка — не экран навигации: закрывается состоянием, а не «назад»
                    TodoUiEvent.NavigateBack -> Unit
                    is TodoUiEvent.Message -> {
                        if (event.onUndo != null && viewModel.state.value.isOpen) {
                            val sheetClosed = coroutineScope {
                                val message = async { sheetSnackbarHostState.showTodoMessage(context, event) }
                                val closed = async { viewModel.state.first { !it.isOpen } }
                                try {
                                    select {
                                        message.onAwait { false }
                                        closed.onAwait { true }
                                    }
                                } finally {
                                    message.cancel()
                                    closed.cancel()
                                }
                            }
                            // Отмена остаётся доступна и после закрытия шторки.
                            if (sheetClosed) snackbarHostState.showTodoMessage(context, event)
                        } else {
                            val host = if (viewModel.state.value.isOpen) sheetSnackbarHostState else snackbarHostState
                            host.showTodoMessage(context, event)
                        }
                    }
                }
            }
        }
    }

    if (state.isOpen)
        TodoTaskDetailsSheet(
            state = state,
            onIntent = viewModel::onIntent,
            snackbarHostState = sheetSnackbarHostState
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoTaskDetailsSheet(
    state: TodoTaskDetailsState,
    onIntent: (TodoTaskDetailsIntent) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val data = state.data
    val accent = green_shamrock
    val canGoBack = state.backStack.isNotEmpty()
    val hasNestedDialog = state.repeatDraft != null || state.showTagsSheet || state.newTag != null || state.linkSheet != null || state.editingComment != null ||
        state.showDeleteDialog || state.commentToDelete != null
    val handleTaskBack = canGoBack && !hasNestedDialog
    val goBack: () -> Unit = {
        focusManager.clearFocus()
        onIntent(TodoTaskDetailsIntent.Back)
    }

    // Снятие фокуса само сохраняет черновик поля, Close — всё остальное
    val close: () -> Unit = {
        focusManager.clearFocus()
        scope.launch { sheetState.hide() }.invokeOnCompletion { onIntent(TodoTaskDetailsIntent.Close) }
    }

    TodoAssigneeFocusHost { outsideAssigneeFocusModifier ->
    ModalBottomSheet(
        onDismissRequest = { onIntent(TodoTaskDetailsIntent.Close) },
        // Развёрнутая шторка не заходит под статус-бар
        modifier = Modifier.statusBarsPadding().then(outsideAssigneeFocusModifier),
        sheetState = sheetState,
        // Назад внутри истории меняет задачу, не скрывая саму шторку.
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = !handleTaskBack),
        containerColor = white,
        dragHandle = { BottomSheetDefaults.DragHandle(color = gray_8) },
        // Отступы под клавиатуру и навигацию — у прокручиваемого содержимого, а не у всей шторки
        contentWindowInsets = { WindowInsets(0) }
    ) {
        BackHandler(enabled = handleTaskBack, onBack = goBack)
        Column(Modifier.fillMaxSize()) {
            TodoDetailsHeader(
                parent = data?.parent,
                accent = accent,
                canGoBack = canGoBack,
                onBack = goBack,
                onParent = { parent -> onIntent(TodoTaskDetailsIntent.NavigateToTask(parent.id)) },
                onDone = close
            )
            HorizontalDivider(color = gray_6)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(white)
            ) {
                if (data == null)
                    CircularProgressIndicator(color = accent, modifier = Modifier.align(Alignment.Center))
                else
                    // Своя прокрутка у каждой задачи: после переключения шторка начинается сверху
                    key(data.task.id) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .imePadding()
                                .verticalScroll(rememberScrollState())
                                .navigationBarsPadding()
                                .padding(horizontal = 16.dp)
                                .padding(top = 16.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TodoTaskDetailsContent(state = state, data = data, accent = accent, onIntent = onIntent)
                        }
                    }
                TodoSnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .imePadding()
                )
            }
        }

        if (data != null) {
            state.repeatDraft?.let { draft ->
                TodoRepeatSheet(draft = draft, dueDate = data.task.dueDate.orEmpty(), onIntent = onIntent)
            }
            if (state.showTagsSheet)
                TodoTagsSheet(data = data, selectedTagIds = state.tagSelection!!.selectedIds, onIntent = onIntent)
            state.newTag?.let { form ->
                TodoTitleColorSheet(
                    sheetTitle = stringResource(R.string.todo_tags_new),
                    form = form,
                    onTitleChange = { onIntent(TodoTaskDetailsIntent.NewTagChange(it)) },
                    onColorChange = { onIntent(TodoTaskDetailsIntent.NewTagColorChange(it)) },
                    onSave = { onIntent(TodoTaskDetailsIntent.SubmitNewTag) },
                    onDismiss = { onIntent(TodoTaskDetailsIntent.CancelNewTag) }
                )
            }
            state.linkSheet?.let { draft ->
                TodoLinkSheet(
                    draft = draft,
                    candidates = state.linkCandidates,
                    lists = state.linkSearchLists,
                    statuses = data.statuses.associateBy { it.id },
                    accent = accent,
                    onIntent = onIntent
                )
            }
            state.editingComment?.let { draft ->
                TodoCommentEditSheet(draft = draft, accent = accent, onIntent = onIntent)
            }
            if (state.showDeleteDialog)
                TodoConfirmDialog(
                    title = stringResource(R.string.todo_card_delete_title),
                    text = stringResource(R.string.todo_card_delete_text),
                    confirmText = stringResource(R.string.todo_delete),
                    onConfirm = { onIntent(TodoTaskDetailsIntent.ConfirmDelete) },
                    onDismiss = { onIntent(TodoTaskDetailsIntent.DismissDeleteDialog) }
                )
            if (state.commentToDelete != null)
                TodoConfirmDialog(
                    title = stringResource(R.string.todo_card_comment_delete_title),
                    text = stringResource(R.string.todo_card_comment_delete_text),
                    confirmText = stringResource(R.string.todo_delete),
                    onConfirm = { onIntent(TodoTaskDetailsIntent.ConfirmDeleteComment) },
                    onDismiss = { onIntent(TodoTaskDetailsIntent.DismissDeleteComment) }
                )
        }
    }
    }
}

/** Заголовок повторяет BaseBottomSheet приложения; родитель — отдельная строка. */
@Composable
private fun TodoDetailsHeader(
    parent: DomainTodoTask?,
    accent: Color,
    canGoBack: Boolean,
    onBack: () -> Unit,
    onParent: (DomainTodoTask) -> Unit,
    onDone: () -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (canGoBack)
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.todo_back),
                        tint = gray_7
                    )
                }
            Text(
                text = stringResource(R.string.todo_details_title), style = textBold_20,
                color = black_2, modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDone) {
                Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.todo_details_close), tint = gray_7)
            }
        }
        if (parent != null) {
            TextButton(onClick = { onParent(parent) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, tint = accent)
                Text(
                    text = parent.title, style = text_14, color = accent, maxLines = 2,
                    overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f).padding(start = 8.dp)
                )
            }
        }
    }
}
