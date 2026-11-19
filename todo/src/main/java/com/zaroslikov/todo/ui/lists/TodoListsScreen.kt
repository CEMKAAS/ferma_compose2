package com.zaroslikov.todo.ui.lists

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import com.zaroslikov.todo.ui.base.showTodoMessage
import com.zaroslikov.todo.ui.base.TodoSnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.TodoSmartList
import com.zaroslikov.todo.domain.model.todoMoved
import com.zaroslikov.todo.ui.base.TodoUiEvent
import com.zaroslikov.todo.ui.components.TodoCard
import com.zaroslikov.todo.ui.components.TodoConfirmDialog
import com.zaroslikov.todo.ui.components.TodoFab
import com.zaroslikov.todo.ui.components.TodoListBadge
import com.zaroslikov.todo.ui.components.TodoTopBar
import com.zaroslikov.todo.ui.components.color
import com.zaroslikov.todo.ui.components.icon
import com.zaroslikov.todo.ui.components.imageVector
import com.zaroslikov.todo.ui.components.titleRes
import com.zaroslikov.todo.ui.list.TODO_AUTO_SCROLL_EDGE
import com.zaroslikov.todo.ui.list.TODO_AUTO_SCROLL_STEP
import com.zaroslikov.todo.ui.list.TodoDragGhost
import com.zaroslikov.todo.ui.list.TodoDragState
import com.zaroslikov.todo.ui.list.TodoDropTarget
import com.zaroslikov.todo.ui.list.TodoItemBounds
import com.zaroslikov.todo.ui.list.TodoPinWhile
import com.zaroslikov.todo.ui.list.rememberTodoDragSource
import com.zaroslikov.todo.ui.list.todoAutoScrollStep
import com.zaroslikov.todo.ui.list.todoColumnDropIndex
import com.zaroslikov.todo.ui.list.todoDragContainer
import com.zaroslikov.todo.ui.list.todoGridDropIndex
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.ios_blue
import com.zaroslikov.todo.ui.theme.ios_gray
import com.zaroslikov.todo.ui.theme.ios_red
import com.zaroslikov.todo.ui.theme.ios_separator
import com.zaroslikov.todo.ui.theme.orang_9
import com.zaroslikov.todo.ui.theme.text_16
import com.zaroslikov.todo.ui.theme.todoColor
import com.zaroslikov.todo.ui.theme.white
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val TEXT_17 = TextStyle(fontSize = 17.sp, lineHeight = 22.sp)

/** Ширина одной кнопки действия, открытой свайпом строки списка (их три). */
private val SWIPE_ACTION_WIDTH = 72.dp

/** Отступ разделителя строк «Мои списки» — от начала названия. */
private val LIST_TEXT_START = 54.dp

/**
 * Главный экран задач проекта по мотивам «Моих списков» из «Напоминаний» iOS: плитки умных
 * списков 2×2, закреплённые списки плитками и карточка «Мои списки»; новый список — FAB.
 */
@Composable
fun TodoListsScreen(
    navigateBack: () -> Unit,
    navigateToList: (Long) -> Unit,
    navigateToSmart: (TodoSmartList) -> Unit,
    navigateToStatuses: () -> Unit,
    navigateToTags: () -> Unit,
    navigateToAssignees: () -> Unit,
    viewModel: TodoListsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showMenu by rememberSaveable { mutableStateOf(false) }
    val onIntent = viewModel::onIntent

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
        containerColor = white,
        topBar = {
            TodoTopBar(
                title = stringResource(R.string.todo_lists_title),
                navigateBack = navigateBack,
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = stringResource(R.string.todo_settings),
                                tint = black_2
                            )
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, containerColor = white) {
                            TodoMenuItem(
                                text = stringResource(R.string.todo_statuses_title),
                                icon = Icons.Rounded.Flag,
                                onClick = {
                                    showMenu = false
                                    navigateToStatuses()
                                }
                            )
                            TodoMenuItem(
                                text = stringResource(R.string.todo_tags_title),
                                icon = Icons.Rounded.LocalOffer,
                                onClick = {
                                    showMenu = false
                                    navigateToTags()
                                }
                            )
                            TodoMenuItem(
                                text = stringResource(R.string.todo_assignees),
                                icon = Icons.Rounded.Person,
                                onClick = {
                                    showMenu = false
                                    navigateToAssignees()
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            TodoFab(
                onClick = { onIntent(TodoListsIntent.OpenCreate) },
                text = stringResource(R.string.todo_lists_new)
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
                CircularProgressIndicator(color = ios_blue)
            }
        else TodoListsContent(
            state = state,
            onIntent = onIntent,
            navigateToList = navigateToList,
            navigateToSmart = navigateToSmart,
            modifier = Modifier.padding(innerPadding)
        )
    }

    state.form?.let { form ->
        TodoTitleColorSheet(
            sheetTitle = stringResource(if (form.isNew) R.string.todo_lists_new else R.string.todo_lists_edit),
            form = form,
            onTitleChange = { onIntent(TodoListsIntent.FormTitleChanged(it)) },
            onColorChange = { onIntent(TodoListsIntent.FormColorChanged(it)) },
            onSave = { onIntent(TodoListsIntent.SaveForm) },
            onDismiss = { onIntent(TodoListsIntent.CloseForm) },
            onIconChange = { onIntent(TodoListsIntent.FormIconChanged(it)) }
        )
    }

    state.listToDelete?.let { list ->
        TodoConfirmDialog(
            title = stringResource(R.string.todo_lists_delete_title),
            text = stringResource(R.string.todo_lists_delete_text, list.title),
            confirmText = stringResource(R.string.todo_delete),
            onConfirm = { onIntent(TodoListsIntent.ConfirmDelete) },
            onDismiss = { onIntent(TodoListsIntent.DismissDelete) }
        )
    }
}

/**
 * Плитки (умные списки и закреплённые) и карточка «Мои списки». Строки карточки и закреплённые
 * плитки переставляются удержанием тем же механизмом, что задачи в экране списка
 * (`ui/list/TodoListDrag.kt`), каждые среди своих: переносимый элемент — пустой промежуток
 * на месте цели, сам он — «призрак» под пальцем, у краёв — автопрокрутка. Умные плитки
 * не двигаются и целью не бывают. Бросок — [TodoListsIntent.DropList].
 */
@Composable
private fun TodoListsContent(
    state: TodoListsState,
    onIntent: (TodoListsIntent) -> Unit,
    navigateToList: (Long) -> Unit,
    navigateToSmart: (TodoSmartList) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val listState = rememberLazyListState()
    val drag = remember { TodoDragState<Long>() }
    val dragId = drag.item
    val unpinned = state.unpinnedLists
    val pinned = state.pinnedLists
    // Во время переноса строка или плитка стоит на месте цели — там рисуется промежуток
    fun List<DomainTodoList>.duringDrag(): List<DomainTodoList> = dragId?.let { id ->
        val dragged = firstOrNull { it.id == id }
        val target = drag.target
        if (dragged == null || target == null) null else todoMoved(dragged, target.index)
    } ?: this
    val rows = unpinned.duringDrag()
    val pinnedTiles = pinned.duringDrag()
    // Границы строк в координатах контейнера переноса — по ним считается цель
    val rowBounds = remember { mutableMapOf<Long, TodoItemBounds>() }
    // Границы ячеек закреплённых плиток по их месту в сетке (не по спискам): ячейки при
    // перестановке стоят, поэтому цель не скачет, пока плитки ещё не перерисовались
    val tileSlots = remember { mutableMapOf<Int, Rect>() }
    val currentUnpinned by rememberUpdatedState(unpinned)
    val currentPinned by rememberUpdatedState(pinned)
    val currentRows by rememberUpdatedState(rows)
    SideEffect {
        drag.originOf = { id ->
            (currentPinned.indexOfFirst { it.id == id }.takeIf { it >= 0 }
                ?: currentUnpinned.indexOfFirst { it.id == id }.takeIf { it >= 0 })
                ?.let { TodoDropTarget(null, it) }
        }
        drag.onDrop = { id, _, target -> onIntent(TodoListsIntent.DropList(id, target.index)) }
    }

    // Пока идёт перенос: каждый кадр — автопрокрутка у краёв и место под «призраком»
    LaunchedEffect(dragId != null) {
        if (dragId == null) return@LaunchedEffect
        val edge = with(density) { TODO_AUTO_SCROLL_EDGE.toPx() }
        val maxStep = with(density) { TODO_AUTO_SCROLL_STEP.toPx() }
        while (true) {
            withFrameNanos { }
            val id = drag.item ?: break
            val step = todoAutoScrollStep(drag.pointer.y, 0f, listState.layoutInfo.viewportSize.height.toFloat(), edge, maxStep)
            if (step != 0f) listState.scrollBy(step)
            val current = drag.target ?: break
            val index = if (currentPinned.any { it.id == id })
                todoGridDropIndex(List(currentPinned.size) { tileSlots[it] }, drag.ghostCenter) ?: current.index
            else {
                val others = currentRows.filter { it.id != id }
                val visible = others.mapIndexedNotNull { index, list -> rowBounds[list.id]?.let { index to it } }
                todoColumnDropIndex(visible, others.size, drag.ghostCenter.y)
            }
            if (index != current.index) drag.target = current.copy(index = index)
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .todoDragContainer(drag)
    ) {
        // Нижний отступ — чтобы FAB не закрывал последний список
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp)
        ) {
            item(key = "tiles") {
                // Сетка — один элемент списка: держим её в композиции, пока плитка переносится
                TodoPinWhile(dragId != null)
                TodoTiles(
                    smartCounts = state.smartCounts,
                    pinned = pinnedTiles,
                    listCounts = state.listCounts,
                    drag = drag,
                    onSlotPlaced = { index, bounds -> tileSlots[index] = bounds },
                    onOpenSmart = navigateToSmart,
                    onOpenList = navigateToList,
                    onIntent = onIntent
                )
            }
            // Все списки закреплены — карточки нет: пустая «Нет списков» сбивала бы с толку
            if (state.lists.isEmpty() || unpinned.isNotEmpty()) {
                item(key = "my_lists_title") {
                    Text(
                        text = stringResource(R.string.todo_lists_my_lists),
                        style = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold),
                        color = black_2,
                        modifier = Modifier.padding(start = 4.dp, top = 24.dp, bottom = 8.dp)
                    )
                }
                item(key = "my_lists") {
                    // Карточка — один элемент списка: держим её в композиции, пока строка переносится
                    TodoPinWhile(dragId != null)
                    TodoCard(padding = PaddingValues(0.dp), verticalArrangement = Arrangement.Top) {
                        if (state.lists.isEmpty())
                            Text(
                                text = stringResource(R.string.todo_lists_no_lists),
                                style = TEXT_17,
                                color = ios_gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        rows.forEachIndexed { index, list ->
                            // key — чтобы свайп-состояние строки не переезжало к соседу после удаления
                            key(list.id) {
                                val isDragged = list.id == dragId
                                TodoListRow(
                                    list = list,
                                    count = state.listCounts[list.id] ?: 0,
                                    isLast = index == rows.lastIndex,
                                    drag = drag,
                                    onClick = { navigateToList(list.id) },
                                    onPin = { onIntent(TodoListsIntent.SetPinned(list.id, true)) },
                                    onEdit = { onIntent(TodoListsIntent.OpenEdit(list)) },
                                    onDelete = { onIntent(TodoListsIntent.RequestDelete(list)) },
                                    modifier = Modifier
                                        .onGloballyPositioned { coordinates ->
                                            val container = drag.container
                                            if (container != null && container.isAttached && coordinates.isAttached) {
                                                val top = container.localPositionOf(coordinates, Offset.Zero).y
                                                rowBounds[list.id] = TodoItemBounds(list.id, top, top + coordinates.size.height)
                                            }
                                        }
                                        .graphicsLayer { alpha = if (isDragged) 0f else 1f }
                                )
                            }
                        }
                    }
                }
            }
        }

        dragId?.let { id -> state.lists.firstOrNull { it.id == id } }?.let { list ->
            val count = state.listCounts[list.id] ?: 0
            if (list.isPinned)
                TodoDragGhost(drag = drag, tilt = true) {
                    TodoTile(
                        color = todoColor(list.colorIndex),
                        icon = list.icon.imageVector(),
                        title = list.title,
                        count = count,
                        onClick = {}
                    )
                }
            else TodoDragGhost(drag = drag, tilt = false) {
                TodoListRowContent(list = list, count = count, modifier = Modifier.background(white))
            }
        }
    }
}

@Composable
private fun TodoMenuItem(text: String, icon: ImageVector, onClick: () -> Unit, color: Color = black_2) {
    DropdownMenuItem(
        text = { Text(text = text, style = text_16, color = color) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null, tint = color) },
        onClick = onClick
    )
}

/** Плитка сетки: умный список или закреплённый список. */
private sealed interface TodoTileItem {
    data class Smart(val kind: TodoSmartList) : TodoTileItem
    data class Pinned(val list: DomainTodoList) : TodoTileItem
}

/**
 * Плитки по две в ряд: сначала умные списки, за ними закреплённые — как в «Напоминаниях».
 * Последняя нечётная плитка — на половину ширины. У «Завершено» числа нет — как в iOS.
 * Все плитки — дети одной раскладки: плитка, переехавшая при переносе в другой ряд, остаётся
 * той же (по key) и не обрывает жест. [onSlotPlaced] — границы ячейки закреплённой плитки
 * (место среди закреплённых) в координатах контейнера переноса.
 */
@Composable
private fun TodoTiles(
    smartCounts: Map<TodoSmartList, Int>,
    pinned: List<DomainTodoList>,
    listCounts: Map<Long, Int>,
    drag: TodoDragState<Long>,
    onSlotPlaced: (index: Int, bounds: Rect) -> Unit,
    onOpenSmart: (TodoSmartList) -> Unit,
    onOpenList: (Long) -> Unit,
    onIntent: (TodoListsIntent) -> Unit
) {
    Layout(
        content = {
            TODO_SMART_TILES.forEach { kind ->
                key(kind) {
                    TodoTile(
                        color = kind.color(),
                        icon = kind.icon(),
                        title = stringResource(kind.titleRes()),
                        count = if (kind == TodoSmartList.COMPLETED) null else smartCounts[kind] ?: 0,
                        onClick = { onOpenSmart(kind) }
                    )
                }
            }
            pinned.forEachIndexed { index, list ->
                key(list.id) {
                    TodoPinnedTile(
                        list = list,
                        count = listCounts[list.id] ?: 0,
                        drag = drag,
                        onClick = { onOpenList(list.id) },
                        onIntent = onIntent,
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                val container = drag.container
                                if (container != null && container.isAttached && coordinates.isAttached)
                                    onSlotPlaced(index, container.localBoundingBoxOf(coordinates, clipBounds = false))
                            }
                            .graphicsLayer { alpha = if (list.id == drag.item) 0f else 1f }
                    )
                }
            }
        }
    ) { measurables, constraints ->
        val gap = 12.dp.roundToPx()
        val cell = ((constraints.maxWidth - gap) / 2).coerceAtLeast(0)
        val rows = measurables.map { it.measure(Constraints.fixedWidth(cell)) }.chunked(2)
        val heights = rows.map { row -> row.maxOf { it.height } }
        val height = heights.sum() + gap * (rows.size - 1).coerceAtLeast(0)
        layout(constraints.maxWidth, height) {
            var y = 0
            rows.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { column, placeable -> placeable.place(column * (cell + gap), y) }
                y += heights[rowIndex] + gap
            }
        }
    }
}

/**
 * Закреплённый список плиткой; долгий тап без движения — «Открепить / Изменить / Удалить»,
 * удержание с переносом — перестановка закреплённых ([drag]).
 */
@Composable
private fun TodoPinnedTile(
    list: DomainTodoList,
    count: Int,
    drag: TodoDragState<Long>,
    onClick: () -> Unit,
    onIntent: (TodoListsIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val dragSource = rememberTodoDragSource(
        drag = drag,
        item = list.id,
        enabled = true,
        onLongClick = { showMenu = true },
        longClickLabel = stringResource(R.string.todo_list_menu)
    )
    Box(modifier) {
        TodoTile(
            color = todoColor(list.colorIndex),
            icon = list.icon.imageVector(),
            title = list.title,
            count = count,
            onClick = onClick,
            dragSource = dragSource
        )
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, containerColor = white) {
            TodoMenuItem(
                text = stringResource(R.string.todo_lists_unpin),
                icon = Icons.Rounded.PushPin,
                onClick = {
                    showMenu = false
                    onIntent(TodoListsIntent.SetPinned(list.id, false))
                }
            )
            TodoMenuItem(
                text = stringResource(R.string.todo_edit_short),
                icon = Icons.Rounded.Edit,
                onClick = {
                    showMenu = false
                    onIntent(TodoListsIntent.OpenEdit(list))
                }
            )
            TodoMenuItem(
                text = stringResource(R.string.todo_delete),
                icon = Icons.Rounded.DeleteOutline,
                color = ios_red,
                onClick = {
                    showMenu = false
                    onIntent(TodoListsIntent.RequestDelete(list))
                }
            )
        }
    }
}

/**
 * Плитка: цветной круг с картинкой, число справа (null — без числа), название внизу.
 * [dragSource] — удержание закреплённой плитки (`rememberTodoDragSource`).
 */
@Composable
private fun TodoTile(
    color: Color,
    icon: ImageVector,
    title: String,
    count: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dragSource: Modifier = Modifier
) {
    // Карточка без своего клика: тап и удержание ловит содержимое, Card обрезает его по своей форме
    TodoCard(modifier = modifier, padding = PaddingValues(0.dp), verticalArrangement = Arrangement.Top) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(dragSource)
                .clickable(onClick = onClick)
                .padding(start = 12.dp, end = 14.dp, top = 10.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = white, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.weight(1f))
                // Плитка без числа держит ту же высоту, что и соседняя с числом
                Text(
                    text = count?.toString().orEmpty(),
                    style = TextStyle(fontSize = 28.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold),
                    color = black_2
                )
            }
            Text(
                text = title,
                style = TextStyle(fontSize = 16.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold),
                color = ios_gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Строка карточки «Мои списки»: значок списка в круге его цвета, название, число незавершённых
 * и «›». Свайп влево открывает «Закрепить», «Изменить» и «Удалить»; долгий тап без движения —
 * те же действия меню; удержание с переносом — перестановка списков ([drag]).
 */
@Composable
private fun TodoListRow(
    list: DomainTodoList,
    count: Int,
    isLast: Boolean,
    drag: TodoDragState<Long>,
    onClick: () -> Unit,
    onPin: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val revealPx = with(LocalDensity.current) { (SWIPE_ACTION_WIDTH * 3).toPx() }
    val offset = remember { Animatable(0f) }
    var showMenu by remember { mutableStateOf(false) }
    fun close() {
        scope.launch { offset.animateTo(0f) }
    }
    val dragSource = rememberTodoDragSource(
        drag = drag,
        item = list.id,
        enabled = true,
        onLongClick = { showMenu = true },
        longClickLabel = stringResource(R.string.todo_list_menu)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Действия под строкой: рисуются, только пока строка сдвинута влево, — иначе
        // красный край просвечивает в скруглённых углах карточки
        if (offset.value < 0f) Row(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
        ) {
            TodoSwipeAction(text = stringResource(R.string.todo_lists_pin), icon = Icons.Rounded.PushPin, color = orang_9) {
                close()
                onPin()
            }
            TodoSwipeAction(text = stringResource(R.string.todo_edit_short), icon = Icons.Rounded.Edit, color = ios_gray) {
                close()
                onEdit()
            }
            TodoSwipeAction(text = stringResource(R.string.todo_delete), icon = Icons.Rounded.DeleteOutline, color = ios_red) {
                close()
                onDelete()
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offset.value.roundToInt(), 0) }
                .background(white)
                .then(dragSource)
                .draggable(
                    orientation = Orientation.Horizontal,
                    enabled = drag.item == null,
                    state = rememberDraggableState { delta ->
                        scope.launch { offset.snapTo((offset.value + delta).coerceIn(-revealPx, 0f)) }
                    },
                    onDragStopped = { velocity ->
                        val open = offset.value < -revealPx / 2 || velocity < -1500f
                        offset.animateTo(if (open) -revealPx else 0f)
                    }
                )
                .clickable { if (offset.value != 0f) close() else onClick() }
        ) {
            TodoListRowContent(list = list, count = count)
            if (!isLast)
                HorizontalDivider(thickness = 0.5.dp, color = ios_separator, modifier = Modifier.padding(start = LIST_TEXT_START))
            Box {
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, containerColor = white) {
                    TodoMenuItem(
                        text = stringResource(R.string.todo_lists_pin),
                        icon = Icons.Rounded.PushPin,
                        onClick = {
                            showMenu = false
                            onPin()
                        }
                    )
                    TodoMenuItem(
                        text = stringResource(R.string.todo_edit_short),
                        icon = Icons.Rounded.Edit,
                        onClick = {
                            showMenu = false
                            onEdit()
                        }
                    )
                    TodoMenuItem(
                        text = stringResource(R.string.todo_delete),
                        icon = Icons.Rounded.DeleteOutline,
                        color = ios_red,
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

/** Содержимое строки списка — и в карточке, и в «призраке» переноса. */
@Composable
private fun TodoListRowContent(list: DomainTodoList, count: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(start = 12.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TodoListBadge(icon = list.icon, color = todoColor(list.colorIndex))
        Text(
            text = list.title,
            style = TEXT_17,
            color = black_2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, end = 8.dp)
        )
        Text(text = count.toString(), style = TEXT_17, color = ios_gray)
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = ios_separator,
            modifier = Modifier.padding(start = 2.dp)
        )
    }
}

/** Действие свайпа: значок и подпись под ним. */
@Composable
private fun TodoSwipeAction(text: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(SWIPE_ACTION_WIDTH)
            .fillMaxHeight()
            .background(color)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = white, modifier = Modifier.size(20.dp))
        Text(text = text, style = TEXT_17.copy(fontSize = 12.sp, lineHeight = 14.sp), color = white, maxLines = 1)
    }
}
