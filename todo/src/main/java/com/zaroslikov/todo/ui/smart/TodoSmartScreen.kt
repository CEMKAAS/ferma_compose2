package com.zaroslikov.todo.ui.smart

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.TodoSmartList
import com.zaroslikov.todo.ui.base.TodoSnackbarHost
import com.zaroslikov.todo.ui.base.TodoUiEvent
import com.zaroslikov.todo.ui.base.showTodoMessage
import com.zaroslikov.todo.ui.components.TodoFab
import com.zaroslikov.todo.ui.components.TodoReminderRow
import com.zaroslikov.todo.ui.components.TodoTopBar
import com.zaroslikov.todo.ui.components.color
import com.zaroslikov.todo.ui.components.title
import com.zaroslikov.todo.ui.components.titleRes
import com.zaroslikov.todo.ui.list.TodoInlineEditorRow
import com.zaroslikov.todo.ui.list.TodoNewTaskRow
import com.zaroslikov.todo.ui.task_details.TodoTaskDetailsHost
import com.zaroslikov.todo.ui.task_details.TodoTaskDetailsIntent
import com.zaroslikov.todo.ui.task_details.TodoTaskDetailsViewModel
import com.zaroslikov.todo.ui.theme.ios_gray
import com.zaroslikov.todo.ui.theme.todoColor
import com.zaroslikov.todo.ui.theme.white
import com.zaroslikov.todo.util.TodoDayLabels
import kotlinx.coroutines.launch

private val TEXT_17 = TextStyle(fontSize = 17.sp, lineHeight = 22.sp)
private const val NEW_TASK_ROW_KEY = "new_task"

/**
 * Умный список как экран списка в «Напоминаниях»: белый фон, «‹», крупный заголовок цветом
 * плитки, секции по датам или спискам, общая строка задачи. Тап по строке — шторка «Подробно» задачи.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoSmartScreen(
    navigateBack: () -> Unit,
    viewModel: TodoSmartViewModel = hiltViewModel(),
    detailsViewModel: TodoTaskDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val accent = state.kind.color()
    val onIntent = viewModel::onIntent
    val title = stringResource(state.kind.titleRes())
    val defaultListTitle = stringResource(R.string.todo_lists_new)
    val focusManager = LocalFocusManager.current
    val finishNewTask = {
        onIntent(TodoSmartIntent.SubmitNewTask(defaultListTitle, NewTaskAction.FINISH))
        focusManager.clearFocus()
    }
    val goBack = {
        if (state.newTask != null) finishNewTask() else navigateBack()
    }
    BackHandler(enabled = state.newTask != null, onBack = goBack)

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            launch {
                viewModel.openTask.collect { detailsViewModel.onIntent(TodoTaskDetailsIntent.Open(it)) }
            }
            viewModel.events.collect { event ->
                when (event) {
                    TodoUiEvent.NavigateBack -> navigateBack()
                    is TodoUiEvent.Message -> snackbarHostState.showTodoMessage(context, event)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.pointerInput(focusManager) { detectTapGestures { focusManager.clearFocus() } },
        containerColor = white,
        topBar = { TodoTopBar(title = title, navigateBack = goBack) },
        floatingActionButton = {
            if (state.canAddTask && !state.isLoading && state.newTask == null)
                TodoFab(
                    onClick = { onIntent(TodoSmartIntent.OpenNewTask) },
                    contentDescription = stringResource(R.string.todo_task_new_title),
                    containerColor = accent
                )
        },
        snackbarHost = { TodoSnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (state.isLoading)
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = accent)
            }
        else TodoSmartBody(
            state = state,
            listState = listState,
            onIntent = onIntent,
            onTapTail = {
                if (state.newTask != null) finishNewTask()
                else if (state.canAddTask) onIntent(TodoSmartIntent.OpenNewTask)
            },
            onOpen = {
                if (state.newTask != null) finishNewTask()
                detailsViewModel.onIntent(TodoTaskDetailsIntent.Open(it))
            },
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding).imePadding()
        )
    }

    TodoTaskDetailsHost(viewModel = detailsViewModel, snackbarHostState = snackbarHostState)
}

@Composable
internal fun TodoSmartBody(
    state: TodoSmartState,
    listState: LazyListState,
    onIntent: (TodoSmartIntent) -> Unit,
    onTapTail: () -> Unit,
    onOpen: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val labels = TodoDayLabels(
        today = stringResource(R.string.todo_list_today),
        tomorrow = stringResource(R.string.todo_list_tomorrow),
        yesterday = stringResource(R.string.todo_list_yesterday)
    )
    // Без группировки по спискам у строки подписан её список
    val withList = state.kind == TodoSmartList.TODAY || state.kind == TodoSmartList.SCHEDULED
    val draft = state.newTask
    val newTaskIndex = state.sections.sumOf { it.tasks.size + (if (it.header == null) 0 else 1) }
    val defaultListTitle = stringResource(R.string.todo_lists_new)
    LaunchedEffect(draft != null, newTaskIndex) {
        if (draft == null) return@LaunchedEffect
        withFrameNanos { }
        val info = listState.layoutInfo
        val editor = info.visibleItemsInfo.firstOrNull { it.key == NEW_TASK_ROW_KEY }
        when {
            editor == null -> listState.animateScrollToItem(newTaskIndex)
            editor.offset + editor.size > info.viewportEndOffset ->
                listState.animateScrollBy((editor.offset + editor.size - info.viewportEndOffset).toFloat())
        }
    }
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        // Нижний отступ — чтобы FAB не закрывал последнюю задачу
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        state.sections.forEach { section ->
            section.header?.let { header ->
                item(key = "header_${section.key}") {
                    Text(
                        text = header.title(),
                        style = TextStyle(fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold),
                        color = header.color(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .animateItem()
                            .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 4.dp)
                    )
                }
            }
            items(section.tasks, key = { it.id }) { task ->
                TodoReminderRow(
                    item = task.item,
                    isDone = state.isDone(task),
                    accent = todoColor(task.listColorIndex),
                    labels = labels,
                    onToggle = {
                        if (draft != null)
                            onIntent(TodoSmartIntent.SubmitNewTask(defaultListTitle, NewTaskAction.FINISH))
                        onIntent(TodoSmartIntent.ToggleDone(task.id))
                    },
                    onClick = { onOpen(task.id) },
                    context = task.parentTitle?.let { stringResource(R.string.todo_smart_in_parent, it) },
                    listTitle = task.listTitle.takeIf { withList },
                    listIcon = task.listIcon,
                    modifier = Modifier.animateItem()
                )
            }
        }
        if (state.canAddTask) {
            item(key = NEW_TASK_ROW_KEY) {
                if (draft == null) TodoNewTaskRow(onClick = { onIntent(TodoSmartIntent.OpenNewTask) })
                else TodoInlineEditorRow(
                    text = draft.title,
                    note = draft.note,
                    accent = state.kind.color(),
                    onTextChange = { onIntent(TodoSmartIntent.NewTaskTitleChanged(it)) },
                    onNoteChange = { onIntent(TodoSmartIntent.NewTaskNoteChanged(it)) },
                    onSubmit = { onIntent(TodoSmartIntent.SubmitNewTask(defaultListTitle)) },
                    onFinish = { onIntent(TodoSmartIntent.SubmitNewTask(defaultListTitle, NewTaskAction.FINISH)) },
                    onInfo = { onIntent(TodoSmartIntent.SubmitNewTask(defaultListTitle, NewTaskAction.OPEN_DETAILS)) }
                )
            }
        }
        val empty = state.sections.isEmpty() && draft == null
        if (empty || state.canAddTask)
            item(key = "tail") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (empty) Modifier.fillParentMaxHeight(0.7f) else Modifier.height(240.dp))
                        .then(if (state.canAddTask) Modifier.clickable(
                            interactionSource = null, indication = null, onClick = onTapTail
                        ) else Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    if (empty) Text(text = stringResource(R.string.todo_smart_empty), style = TEXT_17, color = ios_gray)
                }
            }
    }
}
