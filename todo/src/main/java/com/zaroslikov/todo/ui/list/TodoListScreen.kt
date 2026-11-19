package com.zaroslikov.todo.ui.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ViewAgenda
import androidx.compose.material.icons.rounded.ViewColumn
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import com.zaroslikov.todo.ui.base.showTodoMessage
import com.zaroslikov.todo.ui.base.TodoSnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.TodoListViewMode
import com.zaroslikov.todo.domain.model.TodoPriority
import com.zaroslikov.todo.ui.base.TodoUiEvent
import com.zaroslikov.todo.ui.components.TodoBottomSheet
import com.zaroslikov.todo.ui.components.TodoChip
import com.zaroslikov.todo.ui.components.TodoConfirmDialog
import com.zaroslikov.todo.ui.components.TodoFab
import com.zaroslikov.todo.ui.components.TodoTopBar
import com.zaroslikov.todo.ui.components.TodoDoneCircle
import com.zaroslikov.todo.ui.components.TodoInfoButton
import com.zaroslikov.todo.ui.components.TodoInlineTitleField
import com.zaroslikov.todo.ui.components.TodoReminderRow
import com.zaroslikov.todo.ui.components.animateTodoTextSize
import com.zaroslikov.todo.ui.components.TODO_ROW_TEXT_START
import com.zaroslikov.todo.ui.components.TODO_ROW_META_STYLE
import com.zaroslikov.todo.ui.components.TODO_ROW_TEXT_SPACING
import com.zaroslikov.todo.ui.components.TODO_ROW_TITLE_ACTIONS_WIDTH
import com.zaroslikov.todo.ui.components.TodoTaskTextEditor
import com.zaroslikov.todo.ui.components.marks
import com.zaroslikov.todo.ui.components.color
import com.zaroslikov.todo.ui.components.titleRes
import com.zaroslikov.todo.ui.lists.TodoTitleColorSheet
import com.zaroslikov.todo.ui.task_details.TodoTaskDetailsHost
import com.zaroslikov.todo.ui.task_details.TodoTaskDetailsIntent
import com.zaroslikov.todo.ui.task_details.TodoTaskDetailsViewModel
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.error_base
import com.zaroslikov.todo.ui.theme.gray_6
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.green_6
import com.zaroslikov.todo.ui.theme.grey_2
import com.zaroslikov.todo.ui.theme.textBold_14
import com.zaroslikov.todo.ui.theme.textBold_16
import com.zaroslikov.todo.ui.theme.text_16
import com.zaroslikov.todo.ui.theme.todoColor
import com.zaroslikov.todo.ui.theme.white
import com.zaroslikov.todo.util.TodoDayLabels
import kotlinx.coroutines.launch

/** Приоритеты в фильтре: от важного к неважному, «Нет» не фильтруется. */
private val FILTER_PRIORITIES = listOf(TodoPriority.HIGH, TodoPriority.MEDIUM, TodoPriority.LOW)

/** Отступ подзадачи под родителем. */
internal val SUBTASK_INDENT = 36.dp

/** Задачи создаются прямо в списке, всё остальное настраивается в шторке «Подробно» («ⓘ»). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    navigateBack: () -> Unit,
    viewModel: TodoListViewModel = hiltViewModel(),
    detailsViewModel: TodoTaskDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val searchScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var showMenu by rememberSaveable { mutableStateOf(false) }
    val accent = state.list?.let { todoColor(it.colorIndex) } ?: green_6
    val onIntent = viewModel::onIntent
    val clearSearch = {
        focusManager.clearFocus()
        onIntent(TodoListIntent.Search(""))
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    // События — только в RESUMED: «назад» снимает лишь такой экран (todoNavGraph), а событие
    // во время анимации перехода дождётся в Channel
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            launch {
                viewModel.openTask.collect {
                    detailsViewModel.onIntent(
                        TodoTaskDetailsIntent.Open(
                            it
                        )
                    )
                }
            }
            viewModel.events.collect { event ->
                when (event) {
                    TodoUiEvent.NavigateBack -> navigateBack()
                    is TodoUiEvent.Message -> snackbarHostState.showTodoMessage(context, event)
                }
            }
        }
    }

    val editor = state.editor
    // «Назад» во время ввода завершает его; новый столбец по «назад» не открывает ввод задачи
    BackHandler(enabled = state.query.isNotEmpty(), onBack = clearSearch)
    BackHandler(enabled = editor != null || state.sectionEditor != null) {
        if (editor != null) onIntent(TodoListIntent.EditorFinish(editor.taskId))
        if (state.sectionEditor != null) onIntent(TodoListIntent.SectionTitleFinish(startTask = false))
        focusManager.clearFocus()
    }

    Scaffold(
        // Тап по любому месту, которое само тап не обрабатывает, снимает фокус — поле ввода
        // сохраняет введённое по потере фокуса. Тапы по строкам, карточкам и кругам забирают
        // они сами: их действия сохраняют текущий ввод в ViewModel
        modifier = Modifier
            .nestedScroll(searchScrollBehavior.nestedScrollConnection)
            .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } },
        containerColor = white,
        topBar = {
            Column {
                TodoTopBar(
                    title = state.list?.title.orEmpty(),
                    navigateBack = if (state.query.isNotEmpty()) clearSearch else navigateBack,
                    actions = {
                        if (state.list != null) {
                            Box {
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = stringResource(R.string.todo_settings),
                                        tint = black_2
                                    )
                                }
                                TodoListMenu(
                                    expanded = showMenu,
                                    viewMode = state.viewMode,
                                    showCompleted = state.showCompleted,
                                    hasCompleted = state.completedCount > 0,
                                    onDismiss = { showMenu = false },
                                    onIntent = {
                                        showMenu = false
                                        onIntent(it)
                                    }
                                )
                            }
                        }
                    }
                )
                if (state.list != null)
                    TodoListSearchBar(
                        query = state.query,
                        accent = accent,
                        scrollBehavior = searchScrollBehavior,
                        onQueryChange = { onIntent(TodoListIntent.Search(it)) }
                    )
            }
        },
        floatingActionButton = {
            // Во время ввода кнопка не нужна — Enter сам открывает строку для следующей задачи
            if (state.list != null && editor == null && state.query.isBlank())
                TodoFab(
                    onClick = {
                        val first = state.columns.firstOrNull()?.section
                        if (state.viewMode == TodoListViewMode.COLUMNS && first != null)
                            onIntent(TodoListIntent.StartNewInSection(first.id))
                        else onIntent(TodoListIntent.StartNew)
                    },
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
                    .padding(innerPadding), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = accent)
            }
        else if (state.viewMode == TodoListViewMode.COLUMNS) TodoListColumns(
            state = state,
            accent = accent,
            onIntent = onIntent,
            header = {
                if (state.priorityFilter != null || state.tagFilter != null)
                    TodoActiveFilters(state = state, accent = accent, onIntent = onIntent)
            },
            modifier = Modifier.padding(innerPadding)
        )
        else TodoListBody(
            state = state,
            accent = accent,
            listState = listState,
            onIntent = onIntent,
            onTapEmptySpace = {
                when {
                    editor != null -> {
                        onIntent(TodoListIntent.EditorFinish(editor.taskId))
                        focusManager.clearFocus()
                    }

                    state.sectionEditor != null -> {
                        onIntent(TodoListIntent.SectionTitleFinish(startTask = false))
                        focusManager.clearFocus()
                    }

                    state.query.isNotBlank() -> focusManager.clearFocus()
                    else -> onIntent(TodoListIntent.StartNew)
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (state.showFilterSheet)
        TodoListFilterSheet(state = state, onIntent = onIntent)

    state.form?.let { form ->
        TodoTitleColorSheet(
            sheetTitle = stringResource(R.string.todo_lists_edit),
            form = form,
            onTitleChange = { onIntent(TodoListIntent.FormTitleChanged(it)) },
            onColorChange = { onIntent(TodoListIntent.FormColorChanged(it)) },
            onSave = { onIntent(TodoListIntent.SaveForm) },
            onDismiss = { onIntent(TodoListIntent.CloseForm) },
            onIconChange = { onIntent(TodoListIntent.FormIconChanged(it)) }
        )
    }

    state.deleteCandidate?.let {
        TodoConfirmDialog(
            title = stringResource(R.string.todo_card_delete_title),
            text = stringResource(R.string.todo_card_delete_text),
            confirmText = stringResource(R.string.todo_delete),
            onConfirm = { onIntent(TodoListIntent.ConfirmDeleteTask) },
            onDismiss = { onIntent(TodoListIntent.DismissDeleteTask) }
        )
    }

    if (state.showClearCompleted)
        TodoConfirmDialog(
            title = stringResource(R.string.todo_list_clear_title),
            text = stringResource(R.string.todo_list_clear_text),
            confirmText = stringResource(R.string.todo_delete),
            onConfirm = { onIntent(TodoListIntent.ConfirmClearCompleted) },
            onDismiss = { onIntent(TodoListIntent.DismissClearCompleted) }
        )

    state.sectionForm?.let { form ->
        TodoSectionSheet(
            form = form,
            onTitleChange = { onIntent(TodoListIntent.SectionFormTitleChanged(it)) },
            onSave = { onIntent(TodoListIntent.SaveSectionForm) },
            onDismiss = { onIntent(TodoListIntent.CloseSectionForm) }
        )
    }

    state.sectionToDelete?.let { section ->
        TodoConfirmDialog(
            title = stringResource(R.string.todo_section_delete_title),
            text = stringResource(R.string.todo_section_delete_text, section.title),
            confirmText = stringResource(R.string.todo_delete),
            onConfirm = { onIntent(TodoListIntent.ConfirmDeleteSection) },
            onDismiss = { onIntent(TodoListIntent.DismissDeleteSection) }
        )
    }

    TodoTaskDetailsHost(viewModel = detailsViewModel, snackbarHostState = snackbarHostState)

    val list = state.list
    if (state.showDeleteList && list != null)
        TodoConfirmDialog(
            title = stringResource(R.string.todo_lists_delete_title),
            text = stringResource(R.string.todo_lists_delete_text, list.title),
            confirmText = stringResource(R.string.todo_delete),
            onConfirm = { onIntent(TodoListIntent.ConfirmDeleteList) },
            onDismiss = { onIntent(TodoListIntent.DismissDeleteList) }
        )
}

/** Меню настроек: подсписок, вид, завершённые, фильтр, правка и удаление списка. */
@Composable
private fun TodoListMenu(
    expanded: Boolean,
    viewMode: TodoListViewMode,
    showCompleted: Boolean,
    hasCompleted: Boolean,
    onDismiss: () -> Unit,
    onIntent: (TodoListIntent) -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss, containerColor = white) {
        TodoMenuItem(
            text = stringResource(R.string.todo_section_new),
            icon = Icons.Rounded.CreateNewFolder,
            onClick = { onIntent(TodoListIntent.OpenNewSection) }
        )
        TodoMenuItem(
            text = stringResource(
                if (viewMode == TodoListViewMode.LIST) R.string.todo_list_menu_show_columns
                else R.string.todo_list_menu_show_list
            ),
            icon = if (viewMode == TodoListViewMode.LIST) Icons.Rounded.ViewColumn else Icons.Rounded.ViewAgenda,
            onClick = { onIntent(TodoListIntent.ToggleViewMode) }
        )
        TodoMenuItem(
            text = stringResource(
                if (showCompleted) R.string.todo_list_menu_hide_completed else R.string.todo_list_menu_show_completed
            ),
            icon = if (showCompleted) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
            onClick = { onIntent(TodoListIntent.ToggleShowCompleted) }
        )
        if (hasCompleted)
            TodoMenuItem(
                text = stringResource(R.string.todo_list_menu_clear_completed),
                icon = Icons.Rounded.DeleteSweep,
                onClick = { onIntent(TodoListIntent.RequestClearCompleted) }
            )
        TodoMenuItem(
            text = stringResource(R.string.todo_list_menu_filter),
            icon = Icons.Rounded.FilterList,
            onClick = { onIntent(TodoListIntent.OpenFilters) }
        )
        TodoMenuItem(
            text = stringResource(R.string.todo_lists_edit),
            icon = Icons.Rounded.Edit,
            onClick = { onIntent(TodoListIntent.OpenEditList) }
        )
        TodoMenuItem(
            text = stringResource(R.string.todo_list_menu_delete),
            icon = Icons.Rounded.DeleteOutline,
            color = error_base,
            onClick = { onIntent(TodoListIntent.RequestDeleteList) }
        )
    }
}

@Composable
private fun TodoMenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    color: Color = black_2
) {
    DropdownMenuItem(
        text = { Text(text = text, style = text_16, color = color) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null, tint = color) },
        onClick = onClick
    )
}

@Composable
private fun TodoListBody(
    state: TodoListState,
    accent: Color,
    listState: LazyListState,
    onIntent: (TodoListIntent) -> Unit,
    onTapEmptySpace: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.list == null) return
    val editor = state.editor
    val isAddingNew = editor != null && editor.taskId == null
    val searching = state.query.isNotBlank()
    val labels = TodoDayLabels(
        today = stringResource(R.string.todo_list_today),
        tomorrow = stringResource(R.string.todo_list_tomorrow),
        yesterday = stringResource(R.string.todo_list_yesterday)
    )
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val drag = remember { TodoDragState<TodoDragItem>() }
    val dragItem = drag.item
    val headerCount = if (state.priorityFilter != null || state.tagFilter != null) 1 else 0
    val entries = todoListEntries(state, dragItem, drag.target).let { entries ->
        if (searching) entries.filterNot { it is TodoListEntry.NewTask || it is TodoListEntry.NewSubtask }
        else entries
    }
    val currentState by rememberUpdatedState(state)
    val currentEntries by rememberUpdatedState(entries)
    SideEffect {
        drag.originOf = { if (currentState.query.isBlank()) currentState.dragOrigin(it) else null }
        // Сначала сохраняется ввод, затем поле теряет фокус — его закрытие уже ничего не пишет
        drag.onStart = {
            onIntent(TodoListIntent.DragStarted)
            focusManager.clearFocus()
        }
        drag.onDrop = { item, origin, target ->
            if (currentState.query.isBlank()) todoDropIntent(item, origin, target)?.let(onIntent)
        }
    }
    LaunchedEffect(searching) { if (searching) drag.cancel() }
    LaunchedEffect(state.query) { if (searching) listState.requestScrollToItem(0) }

    // Новая строка ввода всегда в зоне видимости: после каждой добавленной задачи она съезжает ниже
    val editorIndex = entries.indexOfFirst { it is TodoListEntry.Editor }
        .let { if (it < 0) -1 else it + headerCount }
    LaunchedEffect(isAddingNew, editor?.sectionId, editor?.parentId, state.rows.size) {
        if (!isAddingNew) return@LaunchedEffect
        withFrameNanos { }
        val info = listState.layoutInfo
        val visible = info.visibleItemsInfo.firstOrNull { it.key == TodoListEntry.KEY_NEW_EDITOR }
        when {
            visible == null -> if (editorIndex >= 0) listState.animateScrollToItem(editorIndex)
            visible.offset + visible.size > info.viewportEndOffset ->
                listState.animateScrollBy((visible.offset + visible.size - info.viewportEndOffset).toFloat())
        }
    }

    // Подсписки свернулись под перенос заголовка — заголовок возвращается под палец
    val draggedIndex = dragItem?.let { item -> entries.indexOfFirst { it.key == item.key } }
        ?.let { it + headerCount }
    // После переноса подсписки разворачиваются — прокрутка возвращается туда, где была до него
    val scrollBeforeSectionDrag = remember { intArrayOf(-1, 0) }
    LaunchedEffect(dragItem) {
        if (dragItem == null && scrollBeforeSectionDrag[0] >= 0) {
            listState.requestScrollToItem(scrollBeforeSectionDrag[0], scrollBeforeSectionDrag[1])
            scrollBeforeSectionDrag[0] = -1
        }
        if (dragItem !is TodoDragItem.Section || draggedIndex == null) return@LaunchedEffect
        scrollBeforeSectionDrag[0] = listState.firstVisibleItemIndex
        scrollBeforeSectionDrag[1] = listState.firstVisibleItemScrollOffset
        withFrameNanos { }
        // Плавная прокрутка, а не прыжок: прыжок обрывает исчезновение свёрнутых строк
        val visible = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == dragItem.key }
        if (visible != null) listState.scrollBy(visible.offset - drag.ghostOffset.y)
        else {
            listState.scrollToItem(draggedIndex)
            listState.scrollBy(-drag.ghostOffset.y)
        }
    }

    // Пока идёт перенос: каждый кадр — автопрокрутка у краёв и цель под «призраком»
    LaunchedEffect(dragItem != null) {
        if (dragItem == null) return@LaunchedEffect
        val edge = with(density) { TODO_AUTO_SCROLL_EDGE.toPx() }
        val maxStep = with(density) { TODO_AUTO_SCROLL_STEP.toPx() }
        while (true) {
            withFrameNanos { }
            val item = drag.item ?: break
            val step = todoAutoScrollStep(
                drag.pointer.y,
                0f,
                listState.layoutInfo.viewportSize.height.toFloat(),
                edge,
                maxStep
            )
            if (step != 0f) listState.scrollBy(step)
            val current = drag.target ?: break
            val bounds = listState.layoutInfo.visibleItemsInfo.map {
                TodoItemBounds(it.key, it.offset.toFloat(), (it.offset + it.size).toFloat())
            }
            val target = todoListDropTarget(
                currentState,
                currentEntries,
                bounds,
                item,
                drag.ghostCenter.y,
                current
            )
            if (target != current) {
                listState.keepScrollPosition()
                drag.target = target
            }
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .todoDragContainer(drag)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            if (state.priorityFilter != null || state.tagFilter != null)
                item(key = "filters") {
                    TodoActiveFilters(
                        state = state,
                        accent = accent,
                        onIntent = onIntent
                    )
                }

            items(entries, key = { it.key }) { entry ->
                // Переносимый элемент на своём будущем месте — пустой промежуток, виден «призрак»
                val isDragged = dragItem != null && entry.key == dragItem.key
                TodoPinWhile(isDragged)
                val itemModifier = Modifier
                    .animateItem()
                    .graphicsLayer { alpha = if (isDragged) 0f else 1f }
                when (entry) {
                    is TodoListEntry.Header -> TodoSectionHeader(
                        group = entry.group,
                        accent = accent,
                        onIntent = onIntent,
                        modifier = itemModifier,
                        editingTitle = state.sectionEditor?.takeIf { it.sectionId == entry.group.section?.id }?.title,
                        drag = drag,
                        canCollapse = !searching
                    )

                    is TodoListEntry.Task -> {
                        val row = entry.row
                        val task = row.item.task
                        val editing = editor?.takeIf { it.taskId == task.id }
                        val dragSource = rememberTodoDragSource(
                            drag = drag,
                            item = task.parentId?.let { TodoDragItem.Subtask(task.id, it) }
                                ?: TodoDragItem.Task(task.id),
                            enabled = editing == null
                        )
                        TodoTaskSwipeRow(
                            row = row,
                            swipeEnabled = editor == null && dragItem == null,
                            onDelete = { onIntent(TodoListIntent.RequestDeleteTask(task.id)) },
                            modifier = itemModifier,
                            contentModifier = dragSource
                        ) {
                            TodoTaskRow(
                                row = row,
                                accent = accent,
                                labels = labels,
                                editingText = editing?.text,
                                animateSize = dragItem == null,
                                editingNote = editing?.note.orEmpty(),
                                onNoteChange = { onIntent(TodoListIntent.EditorNoteChanged(it)) },
                                onToggle = { onIntent(TodoListIntent.ToggleDone(task.id)) },
                                onClick = { onIntent(TodoListIntent.StartRename(task.id)) },
                                onToggleCollapse = if (searching) null
                                else ({ onIntent(TodoListIntent.ToggleCollapse(task.id)) }),
                                onTextChange = { onIntent(TodoListIntent.EditorTextChanged(it)) },
                                onSubmit = { onIntent(TodoListIntent.EditorSubmit) },
                                onFinish = { onIntent(TodoListIntent.EditorFinish(task.id)) },
                                onInfo = { onIntent(TodoListIntent.EditorInfo) }
                            )
                        }
                    }

                    is TodoListEntry.Editor -> if (editor != null)
                        TodoInlineEditorRow(
                            text = editor.text,
                            note = editor.note,
                            onNoteChange = { onIntent(TodoListIntent.EditorNoteChanged(it)) },
                            accent = accent,
                            onTextChange = { onIntent(TodoListIntent.EditorTextChanged(it)) },
                            onSubmit = { onIntent(TodoListIntent.EditorSubmit) },
                            onFinish = { onIntent(TodoListIntent.EditorFinish(null)) },
                            onInfo = { onIntent(TodoListIntent.EditorInfo) },
                            modifier = itemModifier.padding(start = if (entry.parentId != null) SUBTASK_INDENT else 0.dp)
                        )

                    is TodoListEntry.NewSubtask -> TodoNewTaskRow(
                        onClick = { onIntent(TodoListIntent.StartNewSubtask(entry.parentId)) },
                        modifier = itemModifier,
                        isSubtask = true
                    )

                    // Пунктирная строка в конце подсписка (у свёрнутого — нет): тап — ввод новой задачи здесь
                    is TodoListEntry.NewTask -> TodoNewTaskRow(
                        onClick = {
                            val sectionId = entry.sectionId
                            onIntent(
                                if (sectionId == null) TodoListIntent.StartNew else TodoListIntent.StartNewInSection(
                                    sectionId
                                )
                            )
                        },
                        modifier = itemModifier
                    )
                }
            }

            // Пустое место под задачами: тап начинает добавление (или завершает ввод)
            item(key = "tail") {
                val empty = state.rows.isEmpty() && !isAddingNew &&
                        (searching || state.groups.none { it.section != null })
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (empty) Modifier.fillParentMaxHeight(0.7f) else Modifier.height(240.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onTapEmptySpace
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (empty)
                        Text(
                            text = stringResource(if (state.hasFilters) R.string.todo_list_not_found else R.string.todo_list_empty),
                            style = text_16.copy(fontSize = 17.sp),
                            color = gray_7
                        )
                }
            }
        }

        // «Призрак» под пальцем
        when (dragItem) {
            is TodoDragItem.Section -> state.groups.firstOrNull { it.section?.id == dragItem.sectionId }
                ?.let { group ->
                    TodoDragGhost(drag = drag, tilt = false) {
                        TodoSectionHeader(
                            group = group.copy(isCollapsed = true),
                            accent = accent,
                            onIntent = {},
                            modifier = Modifier.background(white)
                        )
                    }
                }

            is TodoDragItem.Task, is TodoDragItem.Subtask ->
                state.rows.firstOrNull { it.item.task.id == dragItem.key }?.let { row ->
                    TodoDragGhost(drag = drag, tilt = false) {
                        Box(Modifier.background(white)) {
                            TodoTaskRow(
                                row = row,
                                accent = accent,
                                labels = labels,
                                editingText = null,
                                animateSize = false,
                                onToggle = {},
                                onClick = {},
                                onToggleCollapse = {},
                                onTextChange = {},
                                onSubmit = {},
                                onFinish = {},
                                onInfo = {}
                            )
                        }
                    }
                }

            null -> Unit
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoListSearchBar(
    query: String,
    accent: Color,
    scrollBehavior: TopAppBarScrollBehavior,
    onQueryChange: (String) -> Unit
) {
    TopAppBar(
        title = { TodoSearchField(query, accent, onQueryChange) },
        windowInsets = WindowInsets(0),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = white,
            scrolledContainerColor = white
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun TodoSearchField(
    query: String,
    accent: Color,
    onQueryChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val label = stringResource(R.string.todo_list_search_hint)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(gray_6),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
            tint = gray_7,
            modifier = Modifier
                .padding(start = 10.dp)
                .size(20.dp)

        )
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = text_16.copy(color = black_2),
            cursorBrush = SolidColor(accent),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp)
                .semantics { contentDescription = label },
            decorationBox = { innerField ->
                Box(Modifier.padding(vertical = 12.dp)) {
                    if (query.isEmpty()) Text(text = label, style = text_16, color = gray_7)
                    innerField()
                }
            }
        )
        if (query.isNotEmpty())
            IconButton(onClick = { onQueryChange("") }) {
                Icon(
                    imageVector = Icons.Rounded.Clear,
                    contentDescription = stringResource(R.string.todo_clear),
                    tint = gray_7,
                    modifier = Modifier.size(20.dp)
                )
            }
    }
}

/** Активные фильтры строкой маленьких чипов; тап снимает фильтр. */
@Composable
internal fun TodoActiveFilters(
    state: TodoListState,
    accent: Color,
    onIntent: (TodoListIntent) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        state.priorityFilter?.let { priority ->
            TodoSmallFilterChip(
                text = "${priority.marks()} ${stringResource(priority.titleRes())}",
                color = accent,
                onRemove = { onIntent(TodoListIntent.SelectPriority(priority)) }
            )
        }
        state.tags.firstOrNull { it.id == state.tagFilter }?.let { tag ->
            TodoSmallFilterChip(
                text = "#${tag.title}",
                color = todoColor(tag.colorIndex),
                onRemove = { onIntent(TodoListIntent.SelectTag(tag.id)) }
            )
        }
    }
}

@Composable
private fun TodoSmallFilterChip(text: String, color: Color, onRemove: () -> Unit) {
    val description = stringResource(R.string.todo_list_filter_remove, text)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.12f))
            .clickable(role = Role.Button, onClick = onRemove)
            .clearAndSetSemantics { contentDescription = description }
            .padding(start = 10.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, style = textBold_14, color = color)
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .padding(start = 2.dp)
                .size(16.dp)
        )
    }
}

/** Свайп влево — красный фон «Удалить»; [contentModifier] — удержание и перенос строки. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoTaskSwipeRow(
    row: TodoListRow,
    swipeEnabled: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState()
    val scope = rememberCoroutineScope()
    SwipeToDismissBox(
        state = swipeState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        gesturesEnabled = swipeEnabled,
        onDismiss = {
            scope.launch {
                // LazyColumn сохраняет swipeState по ключу задачи даже после удаления строки.
                // Сбрасываем ДО удаления: иначе «Отменить» вернёт уже смахнутую строку
                // и onDismiss немедленно удалит восстановленную задачу повторно.
                swipeState.snapTo(SwipeToDismissBoxValue.Settled)
                onDelete()
            }
        },
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(error_base)
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(R.string.todo_delete),
                    style = textBold_16,
                    color = white
                )
            }
        }
    ) {
        Box(
            Modifier
                .background(white)
                .then(contentModifier)
        ) { content() }
    }
}

/** Строка задачи списка: у подзадачи — отступ, нажатие на прогресс раскрывает подзадачи. */
@Composable
private fun TodoTaskRow(
    row: TodoListRow,
    accent: Color,
    labels: TodoDayLabels,
    editingText: String?,
    animateSize: Boolean = true,
    editingNote: String = "",
    onNoteChange: (String) -> Unit = {},
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onToggleCollapse: (() -> Unit)?,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onFinish: () -> Unit,
    onInfo: () -> Unit
) {
    TodoReminderRow(
        item = row.item,
        isDone = row.isDone,
        accent = accent,
        labels = labels,
        onToggle = onToggle,
        onClick = onClick,
        indent = if (row.isSubtask) SUBTASK_INDENT else 0.dp,
        editingText = editingText,
        animateSize = animateSize,
        editingNote = editingNote,
        onNoteChange = onNoteChange,
        onTextChange = onTextChange,
        onSubmit = onSubmit,
        onFinish = onFinish,
        onInfo = onInfo,
        onToggleSubtasks = onToggleCollapse.takeIf { row.childCount > 0 },
        subtasksCollapsed = row.isCollapsed
    )
}

/** Новая задача: пустой круг и поле без рамки с фокусом, справа «ⓘ». */
@Composable
internal fun TodoInlineEditorRow(
    text: String,
    note: String,
    onNoteChange: (String) -> Unit,
    accent: Color,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onFinish: () -> Unit,
    onInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .animateTodoTextSize()
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(start = 7.dp, end = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            TodoDoneCircle(done = false, accent = accent, onToggle = {})
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 3.dp, top = 10.dp, bottom = 10.dp)
            ) {
                TodoTaskTextEditor(
                    title = text,
                    note = note,
                    noteTextStyle = TODO_ROW_META_STYLE,
                    noteSpacing = TODO_ROW_TEXT_SPACING,
                    accent = accent,
                    onTitleChange = onTextChange,
                    onNoteChange = onNoteChange,
                    onSubmit = onSubmit,
                    onFinish = onFinish,
                    titleTrailing = {
                        Row(
                            modifier = Modifier
                                .width(TODO_ROW_TITLE_ACTIONS_WIDTH - 7.dp)
                                .padding(end = 7.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TodoInfoButton(accent = accent, onClick = onInfo)
                        }
                    }
                )
            }
        }
        HorizontalDivider(
            thickness = 0.5.dp,
            color = grey_2,
            modifier = Modifier.padding(start = TODO_ROW_TEXT_START)
        )
    }
}

/** Шторка «Фильтр»: приоритеты и теги чипами; повторный тап снимает выбор. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoListFilterSheet(
    state: TodoListState,
    onIntent: (TodoListIntent) -> Unit
) {
    TodoBottomSheet(
        title = stringResource(R.string.todo_list_menu_filter),
        onDismiss = { onIntent(TodoListIntent.CloseFilters) }
    ) {
        Text(text = stringResource(R.string.todo_priority), style = textBold_16, color = black_2)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FILTER_PRIORITIES.forEach { priority ->
                TodoChip(
                    text = stringResource(priority.titleRes()),
                    selected = state.priorityFilter == priority,
                    onClick = { onIntent(TodoListIntent.SelectPriority(priority)) },
                    color = priority.color()
                )
            }
        }
        if (state.tags.isNotEmpty()) {
            Text(
                text = stringResource(R.string.todo_tags_title),
                style = textBold_16,
                color = black_2
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.tags.forEach { tag ->
                    TodoChip(
                        text = tag.title,
                        selected = state.tagFilter == tag.id,
                        onClick = { onIntent(TodoListIntent.SelectTag(tag.id)) },
                        color = todoColor(tag.colorIndex),
                        fillWithColor = true
                    )
                }
            }
        }
        if (state.priorityFilter != null || state.tagFilter != null)
            TextButton(
                onClick = {
                    onIntent(TodoListIntent.SelectPriority(null))
                    onIntent(TodoListIntent.SelectTag(null))
                }
            ) {
                Text(
                    text = stringResource(R.string.todo_list_filter_reset),
                    style = textBold_16,
                    color = error_base
                )
            }
    }
}
