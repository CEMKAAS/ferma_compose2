package com.zaroslikov.todo.ui.list

import com.zaroslikov.todo.ui.components.animateTodoTextSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DriveFileMove
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.todo.ui.components.TodoSubtaskProgress
import com.zaroslikov.todo.ui.components.TodoReminderRow
import com.zaroslikov.todo.ui.components.TodoPriorityMarks
import com.zaroslikov.todo.ui.components.TodoTaskMetadata
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.ui.components.TODO_ROW_TITLE_STYLE
import com.zaroslikov.todo.ui.components.TodoTaskTextEditor
import com.zaroslikov.todo.ui.components.TodoCard
import com.zaroslikov.todo.ui.components.TodoDoneCircle
import com.zaroslikov.todo.ui.components.TodoInlineTitleField
import com.zaroslikov.todo.ui.components.TodoInfoButton
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.error_base
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.gray_8
import com.zaroslikov.todo.ui.theme.text_16
import com.zaroslikov.todo.ui.theme.white
import com.zaroslikov.todo.util.TodoDayLabels

// Вид «столбцы»: подсписки горизонтальными столбцами, задачи — карточками, как столбцы
// в «Напоминаниях» iOS. Подзадачи раскрываются внутри карточки родителя.
// Карточки и столбцы переставляются удержанием и переносом (TodoListDrag.kt)

private val COLUMN_WIDTH = 280.dp

/** Как у [TodoCard]: пунктирная карточка новой задачи той же формы. */
private val CARD_SHAPE = RoundedCornerShape(14.dp)
private val COLUMN_TITLE_STYLE =
    TextStyle(fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
val CARD_META_STYLE = TextStyle(fontSize = 13.sp, lineHeight = 17.sp)

/** Ключ промежутка под карточку, которую переносят из другого столбца. */
private const val KEY_GAP = "gap"

/** Сколько карточек показывает «призрак» переносимого столбца. */
private const val GHOST_CARDS = 3

/** Столбец на экране для переноса: прокрутка его карточек и их границы в координатах контейнера. */
private class TodoColumnSlot(val listState: LazyListState) {
    var bounds: Rect = Rect.Zero
}

/**
 * Столбцы списка на белом фоне. [header] — активные фильтры над столбцами.
 * Тап по карточке — правка названия и заметки. Удержание карточки и перенос —
 * в другой столбец или на другое место, удержание заголовка — перестановка столбцов.
 */
@Composable
internal fun TodoListColumns(
    state: TodoListState,
    accent: Color,
    onIntent: (TodoListIntent) -> Unit,
    header: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    val labels = TodoDayLabels(
        today = stringResource(R.string.todo_list_today),
        tomorrow = stringResource(R.string.todo_list_tomorrow),
        yesterday = stringResource(R.string.todo_list_yesterday)
    )
    val rowState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val searching = state.query.isNotBlank()
    val drag = remember { TodoDragState<TodoDragItem>() }
    val dragItem = drag.item
    val slots = remember { mutableMapOf<Long?, TodoColumnSlot>() }
    val currentState by rememberUpdatedState(state)
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
    LaunchedEffect(state.query) { if (searching) rowState.requestScrollToItem(0) }

    // Во время переноса карточки виден и пустой столбец «Без подсписка» — туда тоже можно бросить.
    // Переносимый столбец стоит на месте цели (пустым промежутком), «Без подсписка» всегда первый
    val columns =
        (if (dragItem is TodoDragItem.Task) state.groups else state.columns).let { columns ->
            val target = drag.target
            val dragged =
                (dragItem as? TodoDragItem.Section)?.let { item -> columns.firstOrNull { it.section?.id == item.sectionId } }
            if (dragged == null || target == null) columns
            else (columns - dragged).toMutableList().apply {
                val first = count { it.section == null }
                add((first + target.index).coerceIn(first, size), dragged)
            }
        }

    // Столбец, где открыт ввод новой задачи (в т.ч. только что созданный из «Нового столбика»),
    // целиком в зоне видимости: набор столбцов меняется, и прокрутка может его обрезать
    val addingColumn =
        state.editor?.takeIf { it.taskId == null && it.parentId == null }?.let { editor ->
            state.columns.indexOfFirst { it.section?.id == editor.sectionId }
        } ?: -1
    LaunchedEffect(addingColumn) {
        if (addingColumn < 0) return@LaunchedEffect
        withFrameNanos { }
        val info = rowState.layoutInfo
        val visible = info.visibleItemsInfo.firstOrNull { it.index == addingColumn }
        if (visible == null || visible.offset < info.viewportStartOffset || visible.offset + visible.size > info.viewportEndOffset)
            rowState.animateScrollToItem(addingColumn)
    }

    // Пока идёт перенос: каждый кадр — автопрокрутка у краёв (столбцов по горизонтали, столбца
    // под пальцем по вертикали) и цель под «призраком»
    LaunchedEffect(dragItem != null) {
        if (dragItem == null) return@LaunchedEffect
        val edge = with(density) { TODO_AUTO_SCROLL_EDGE.toPx() }
        val maxStep = with(density) { TODO_AUTO_SCROLL_STEP.toPx() }
        while (true) {
            withFrameNanos { }
            val item = drag.item ?: break
            val width = drag.container?.size?.width?.toFloat() ?: break
            val step = todoAutoScrollStep(drag.pointer.x, 0f, width, edge, maxStep)
            if (step != 0f) rowState.scrollBy(step)
            val target = when (item) {
                is TodoDragItem.Task -> {
                    // Столбец под пальцем, иначе ближайший по горизонтали
                    val x = drag.pointer.x
                    val (sectionId, slot) = slots.entries
                        .filter { it.value.bounds.width > 0f }
                        .minByOrNull { (_, slot) ->
                            when {
                                x < slot.bounds.left -> slot.bounds.left - x
                                x > slot.bounds.right -> x - slot.bounds.right
                                else -> 0f
                            }
                        } ?: continue
                    val scroll = todoAutoScrollStep(
                        drag.pointer.y,
                        slot.bounds.top,
                        slot.bounds.bottom,
                        edge,
                        maxStep
                    )
                    if (scroll != 0f) slot.listState.scrollBy(scroll)
                    val cards = currentState.visibleCards(sectionId, item.taskId)
                    val info = slot.listState.layoutInfo
                    val visible = info.visibleItemsInfo.mapNotNull { visible ->
                        val index = (visible.key as? Long)?.let { cards.indexOf(it) } ?: -1
                        if (index < 0) null
                        else index to TodoItemBounds(
                            visible.key,
                            visible.offset.toFloat(),
                            (visible.offset + visible.size).toFloat()
                        )
                    }
                    // Смещения карточек — от начала содержимого, после отступа сверху
                    val y = drag.ghostCenter.y - slot.bounds.top + info.viewportStartOffset
                    TodoDropTarget(sectionId, todoColumnDropIndex(visible, cards.size, y))
                }

                is TodoDragItem.Section -> {
                    val order = currentState.visibleSections(item.sectionId)
                    val placed = slots.entries
                        .mapNotNull { (id, slot) ->
                            val index = if (id == null) -1 else order.indexOf(id)
                            if (id == null || index < 0 || slot.bounds.width <= 0f) null
                            else index to TodoItemBounds(id, slot.bounds.left, slot.bounds.right)
                        }
                        .sortedBy { it.first }
                    TodoDropTarget(
                        null,
                        todoColumnDropIndex(placed, order.size, drag.ghostCenter.x)
                    )
                }

                is TodoDragItem.Subtask -> drag.target
            }
            if (target != drag.target) {
                // Набор карточек и столбцов меняется — прокрутка остаётся на месте
                rowState.keepScrollPosition()
                slots.values.forEach { it.listState.keepScrollPosition() }
                drag.target = target
            }
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .background(white)
            .todoDragContainer(drag)
    ) {
        Column(Modifier.fillMaxSize()) {
            Column(content = header)
            if (searching && columns.none { it.cards.isNotEmpty() }) {
                // Вертикальный жест раскрывает поиск, даже если последний результат исчез.
                LazyColumn(Modifier.weight(1f).fillMaxWidth()) {
                    item {
                        Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.todo_list_not_found),
                                style = text_16.copy(fontSize = 17.sp),
                                color = gray_7
                            )
                        }
                    }
                }
                return@Column
            }
            LazyRow(
                state = rowState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(columns, key = { it.section?.id ?: -1L }) { group ->
                    val isDragged =
                        dragItem is TodoDragItem.Section && group.section?.id == dragItem.sectionId
                    TodoColumn(
                        group = group,
                        sections = state.sections,
                        editor = state.editor,
                        sectionEditor = state.sectionEditor,
                        accent = accent,
                        labels = labels,
                        drag = drag,
                        slots = slots,
                        searching = searching,
                        onIntent = onIntent,
                        modifier = Modifier
                            .width(COLUMN_WIDTH)
                            .fillParentMaxHeight()
                            .animateItem()
                            .graphicsLayer { alpha = if (isDragged) 0f else 1f }
                    )
                }
                if (!searching || state.sectionEditor?.let { it.sectionId == null } == true)
                    item(key = "add_section") {
                        val sectionEditor = state.sectionEditor
                        if (sectionEditor != null && sectionEditor.sectionId == null)
                            TodoColumnTitleRow(modifier = Modifier.width(COLUMN_WIDTH)) {
                                TodoColumnTitleField(
                                    text = sectionEditor.title,
                                    accent = accent,
                                    onIntent = onIntent
                                )
                            }
                        else TodoNewColumn(onClick = { onIntent(TodoListIntent.StartNewColumn) })
                    }
            }
        }

        // «Призрак» под пальцем: карточка или столбец, приподнятые и чуть повёрнутые
        when (dragItem) {
            is TodoDragItem.Task -> state.groups.flatMap { it.cards }
                .firstOrNull { it.item.task.id == dragItem.taskId }
                ?.let { row ->
                    TodoDragGhost(drag = drag, tilt = true) {
                        TodoTaskCardBody(
                            row = row,
                            accent = accent,
                            labels = labels,
                            onToggle = {},
                            onClick = null
                        )
                    }
                }

            is TodoDragItem.Section -> state.groups.firstOrNull { it.section?.id == dragItem.sectionId }
                ?.let { group ->
                    TodoDragGhost(drag = drag, tilt = true, width = COLUMN_WIDTH) {
                        TodoColumnGhost(group = group, accent = accent, labels = labels)
                    }
                }

            else -> Unit
        }
    }
}

@Composable
private fun TodoColumn(
    group: TodoListGroup,
    sections: List<DomainTodoSection>,
    editor: TodoInlineEditor?,
    sectionEditor: TodoSectionEditor?,
    accent: Color,
    labels: TodoDayLabels,
    drag: TodoDragState<TodoDragItem>,
    slots: MutableMap<Long?, TodoColumnSlot>,
    searching: Boolean,
    onIntent: (TodoListIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val section = group.section
    val sectionId = section?.id
    val isAdding =
        editor != null && editor.taskId == null && editor.parentId == null && editor.sectionId == sectionId
    val density = LocalDensity.current
    val listState = rememberLazyListState()
    val slot = remember(listState) { TodoColumnSlot(listState) }
    DisposableEffect(sectionId, slot) {
        onDispose { if (slots[sectionId] === slot) slots.remove(sectionId) }
    }

    // Переносимая карточка остаётся в своём столбце: здесь её место — промежуток, в другом
    // столбце она схлопнута до нуля, а там на месте цели — промежуток её высоты (null)
    val dragItem = drag.item
    val draggedId = (dragItem as? TodoDragItem.Task)?.taskId
    val target = drag.target
    val targetHere = draggedId != null && target != null && target.sectionId == sectionId
    val draggedRow = draggedId?.let { id -> group.cards.firstOrNull { it.item.task.id == id } }
    val cards: List<TodoListRow?> = when {
        target == null || !targetHere -> group.cards
        draggedRow != null -> (group.cards - draggedRow).toMutableList<TodoListRow?>()
            .apply { add(target.index.coerceIn(0, size), draggedRow) }

        else -> group.cards.toMutableList<TodoListRow?>()
            .apply { add(target.index.coerceIn(0, size), null) }
    }
    TodoPinWhile(draggedRow != null || (dragItem is TodoDragItem.Section && dragItem.sectionId == sectionId))

    Column(modifier) {
        TodoColumnHeader(
            section = section,
            editingTitle = sectionEditor?.takeIf { section != null && it.sectionId == section.id }?.title,
            accent = accent,
            drag = drag,
            onIntent = onIntent
        )
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    drag.container?.takeIf { it.isAttached }?.let { container ->
                        slot.bounds = container.localBoundingBoxOf(coordinates, clipBounds = false)
                        slots[sectionId] = slot
                    }
                },
            contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp, start = 2.dp, end = 2.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(cards, key = { it?.item?.task?.id ?: KEY_GAP }) { row ->
                if (row == null)
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(with(density) { drag.size.height.toDp() })
                            .animateItem()
                    )
                else {
                    val isDragged = row.item.task.id == draggedId
                    TodoPinWhile(isDragged)
                    TodoTaskCard(
                        row = row,
                        subtasks = group.cardRows.filter { it.item.task.parentId == row.item.task.id },
                        editor = editor,
                        sections = sections,
                        accent = accent,
                        labels = labels,
                        drag = drag,
                        searching = searching,
                        onIntent = onIntent,
                        modifier = Modifier
                            .animateItem()
                            .then(
                                when {
                                    !isDragged -> Modifier
                                    targetHere -> Modifier.graphicsLayer { alpha = 0f }
                                    // Ушла в другой столбец — место не занимает, но жест жив
                                    else -> Modifier
                                        .layout { measurable, constraints ->
                                            val placeable = measurable.measure(constraints)
                                            layout(placeable.width, 0) { placeable.place(0, 0) }
                                        }
                                        .graphicsLayer { alpha = 0f }
                                }
                            )
                    )
                }
            }
            if (isAdding || !searching)
                item(key = "add") {
                    if (isAdding) TodoNewCard(editor = editor, accent = accent, onIntent = onIntent)
                    else TodoNewTaskCard(
                        onClick = {
                            onIntent(
                                if (section == null) TodoListIntent.StartNew else TodoListIntent.StartNewInSection(
                                    section.id
                                )
                            )
                        }
                    )
                }
        }
    }
}

/**
 * Заголовок столбца: название подсписка (тап — переименование на месте, [editingTitle] — текст
 * поля) и меню «Переименовать / Удалить» — кнопкой или долгим тапом без движения; удержание
 * и перенос — перестановка столбцов ([drag]).
 */
@Composable
private fun TodoColumnHeader(
    section: DomainTodoSection?,
    editingTitle: String?,
    accent: Color,
    drag: TodoDragState<TodoDragItem>,
    onIntent: (TodoListIntent) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val dragSource = if (section == null) Modifier
    else rememberTodoDragSource(
        drag = drag,
        item = TodoDragItem.Section(section.id),
        enabled = editingTitle == null,
        onLongClick = { menuExpanded = true },
        longClickLabel = stringResource(R.string.todo_section_actions, section.title)
    )
    TodoColumnTitleRow(modifier = dragSource) {
        Box(Modifier.weight(1f)) {
            if (editingTitle != null) TodoColumnTitleField(
                text = editingTitle,
                accent = accent,
                onIntent = onIntent
            )
            else Text(
                text = section?.title ?: stringResource(R.string.todo_section_none),
                style = COLUMN_TITLE_STYLE,
                color = black_2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = if (section == null) Modifier
                else Modifier.clickable(onClickLabel = stringResource(R.string.todo_section_rename)) {
                    onIntent(TodoListIntent.StartRenameSection(section.id))
                }
            )
        }
        if (section != null)
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreHoriz,
                        contentDescription = stringResource(
                            R.string.todo_section_actions,
                            section.title
                        ),
                        tint = gray_7
                    )
                }
                TodoSectionMenu(
                    expanded = menuExpanded,
                    section = section,
                    onDismiss = { menuExpanded = false },
                    onIntent = onIntent
                )
            }
    }
}

/** «Призрак» переносимого столбца: заголовок и первые карточки на белом фоне. */
@Composable
private fun TodoColumnGhost(group: TodoListGroup, accent: Color, labels: TodoDayLabels) {
    Column(
        modifier = Modifier
            .background(white)
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TodoColumnTitleRow {
            Text(
                text = group.section?.title.orEmpty(),
                style = COLUMN_TITLE_STYLE,
                color = black_2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = group.cards.size.toString(),
                style = text_16,
                color = gray_7,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
        group.cards.take(GHOST_CARDS).forEach { row ->
            Box(Modifier.padding(horizontal = 4.dp)) {
                TodoTaskCardBody(
                    row = row,
                    accent = accent,
                    labels = labels,
                    onToggle = {},
                    onClick = null
                )
            }
        }
    }
}

/** Строка заголовка столбца: высота как у кнопки меню, чтобы поле и текст не сдвигали карточки. */
@Composable
private fun TodoColumnTitleRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(start = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

/** Поле названия столбца тем же стилем: Enter и потеря фокуса сохраняют, пустое — отмена. */
@Composable
private fun TodoColumnTitleField(text: String, accent: Color, onIntent: (TodoListIntent) -> Unit) {
    TodoInlineTitleField(
        text = text,
        accent = accent,
        onTextChange = { onIntent(TodoListIntent.SectionTitleChanged(it)) },
        onSubmit = { onIntent(TodoListIntent.SectionTitleFinish()) },
        onFinish = { onIntent(TodoListIntent.SectionTitleFinish()) },
        textStyle = COLUMN_TITLE_STYLE
    )
}

/**
 * Карточка задачи. Тап — правка названия и заметки, «ⓘ» — «Подробно»,
 * долгий тап без движения — «Переместить в…» и «Удалить»,
 * удержание и перенос — в другой столбец или на другое место ([drag]).
 */
@Composable
private fun TodoTaskCard(
    row: TodoListRow,
    subtasks: List<TodoListRow>,
    editor: TodoInlineEditor?,
    sections: List<DomainTodoSection>,
    accent: Color,
    labels: TodoDayLabels,
    drag: TodoDragState<TodoDragItem>,
    searching: Boolean,
    onIntent: (TodoListIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val task = row.item.task
    val editing = editor?.takeIf { it.taskId == task.id }
    var menu by remember { mutableStateOf(TodoCardMenu.HIDDEN) }
    val dragSource = rememberTodoDragSource(
        drag = drag,
        item = TodoDragItem.Task(row.item.task.id),
        enabled = editing == null,
        onLongClick = { menu = TodoCardMenu.ACTIONS },
        longClickLabel = stringResource(R.string.todo_columns_move_to)
    )
    Box(
        modifier
            .fillMaxWidth()
    ) {
        TodoTaskCardBody(
            row = row,
            accent = accent,
            labels = labels,
            onToggle = { onIntent(TodoListIntent.ToggleDone(row.item.task.id)) },
            onClick = if (editing == null) ({ onIntent(TodoListIntent.StartRename(task.id)) }) else null,
            animateSize = drag.item == null,
            headerModifier = dragSource,
            editorContent = editing?.let { current ->
                {
                    TodoTaskTextEditor(
                        title = current.text,
                        note = current.note,
                        accent = accent,
                        onTitleChange = { onIntent(TodoListIntent.EditorTextChanged(it)) },
                        onNoteChange = { onIntent(TodoListIntent.EditorNoteChanged(it)) },
                        onSubmit = { onIntent(TodoListIntent.EditorSubmit) },
                        onFinish = { onIntent(TodoListIntent.EditorFinish(task.id)) },
                        priority = task.priority,
                        titleTrailing = {
                            TodoInfoButton(accent = accent, onClick = { onIntent(TodoListIntent.EditorInfo) })
                        }
                    )
                }
            },
            onToggleCollapse = if (searching) null else ({
                if (editor != null && (editor.parentId == task.id || subtasks.any { it.item.task.id == editor.taskId }))
                    onIntent(TodoListIntent.EditorFinish(editor.taskId))
                onIntent(TodoListIntent.ToggleCollapse(task.id))
            })
        ) {
            subtasks.forEach { subtask ->
                key(subtask.item.task.id) {
                    TodoCardSubtask(
                        row = subtask,
                        editor = editor,
                        accent = accent,
                        labels = labels,
                        onIntent = onIntent
                    )
                }
            }
            val adding = editor?.takeIf { it.taskId == null && it.parentId == row.item.task.id }
            if (adding != null)
                TodoInlineEditorRow(
                    text = adding.text,
                    note = adding.note,
                    onNoteChange = { onIntent(TodoListIntent.EditorNoteChanged(it)) },
                    accent = accent,
                    onTextChange = { onIntent(TodoListIntent.EditorTextChanged(it)) },
                    onSubmit = { onIntent(TodoListIntent.EditorSubmit) },
                    onFinish = { onIntent(TodoListIntent.EditorFinish(null)) },
                    onInfo = { onIntent(TodoListIntent.EditorInfo) },
                    modifier = Modifier.padding(start = 8.dp)
                )
            else if (subtasks.isNotEmpty() && !searching)
                TodoNewTaskRow(
                    onClick = { onIntent(TodoListIntent.StartNewSubtask(row.item.task.id)) },
                    modifier = Modifier.padding(start = 8.dp)
                )
        }
        TodoCardMenus(
            menu = menu,
            row = row,
            sections = sections,
            onMenuChange = { menu = it },
            onIntent = onIntent
        )
    }
}

/** Подзадача в общей карточке: правка на месте, отметка и «Подробно». */
@Composable
private fun TodoCardSubtask(
    row: TodoListRow,
    editor: TodoInlineEditor?,
    accent: Color,
    labels: TodoDayLabels,
    onIntent: (TodoListIntent) -> Unit
) {
    val task = row.item.task
    val editing = editor?.takeIf { it.taskId == task.id }
    TodoReminderRow(
        item = row.item,
        isDone = row.isDone,
        accent = accent,
        labels = labels,
        indent = 8.dp,
        onToggle = { onIntent(TodoListIntent.ToggleDone(task.id)) },
        onClick = { onIntent(TodoListIntent.StartRename(task.id)) },
        editingText = editing?.text,
        editingNote = editing?.note.orEmpty(),
        onNoteChange = { onIntent(TodoListIntent.EditorNoteChanged(it)) },
        onTextChange = { onIntent(TodoListIntent.EditorTextChanged(it)) },
        onSubmit = { onIntent(TodoListIntent.EditorSubmit) },
        onFinish = { onIntent(TodoListIntent.EditorFinish(task.id)) },
        onInfo = { onIntent(TodoListIntent.EditorInfo) }
    )
}

/**
 * Вид карточки: приоритет, название, описание и метаданные, затем управление подзадачами.
 * [onClick] = null — без нажатия (карточка «призрака»).
 */
@Composable
private fun TodoTaskCardBody(
    row: TodoListRow,
    accent: Color,
    labels: TodoDayLabels,
    onToggle: () -> Unit,
    onClick: (() -> Unit)?,
    animateSize: Boolean = onClick != null,
    headerModifier: Modifier = Modifier,
    editorContent: (@Composable () -> Unit)? = null,
    onToggleCollapse: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    val item = row.item
    val task = item.task
    TodoCard(
        modifier = Modifier.fillMaxWidth(),
        padding = PaddingValues(0.dp),
        elevation = 2.dp,
        verticalArrangement = Arrangement.Top
    ) {
        // Анимируем всё содержимое внутри карточки, сохраняя её внешнюю тень.
        Column(
            Modifier
                .fillMaxWidth()
                .animateTodoTextSize(animateSize)
        ) {
            Row(
                modifier = headerModifier
                    .fillMaxWidth()
                    .then(if (onClick == null) Modifier else Modifier.clickable(onClick = onClick))
                    .padding(start = 2.dp, end = 12.dp, top = 2.dp, bottom = 10.dp),
                verticalAlignment = Alignment.Top
            ) {
                TodoDoneCircle(done = row.isDone, accent = accent, onToggle = onToggle)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 9.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (editorContent != null) {
                        editorContent()
                    } else {
                        Row {
                            TodoPriorityMarks(task.priority)
                            Text(
                                text = task.title,
                                style = TODO_ROW_TITLE_STYLE,
                                color = if (row.isDone) gray_7 else black_2,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (task.note.isNotBlank())
                            Text(
                                text = task.note,
                                style = CARD_META_STYLE,
                                color = gray_7,
                                modifier = Modifier.fillMaxWidth()
                            )
                    }
                    TodoCardMeta(row, labels)
                }
            }
            if (item.subtaskCount > 0)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 42.dp, end = 12.dp, bottom = 10.dp)
                ) {
                    TodoSubtaskProgress(
                        done = item.subtaskDoneCount,
                        total = item.subtaskCount,
                        accent = accent,
                        onToggleSubtasks = onToggleCollapse.takeIf { row.childCount > 0 },
                        isCollapsed = row.isCollapsed
                    )
                }
            content()
        }
    }
}

private enum class TodoCardMenu { HIDDEN, ACTIONS, MOVE }

/** Долгий тап: «Переместить в…» (второй шаг — выбор подсписка) и «Удалить». */
@Composable
private fun TodoCardMenus(
    menu: TodoCardMenu,
    row: TodoListRow,
    sections: List<DomainTodoSection>,
    onMenuChange: (TodoCardMenu) -> Unit,
    onIntent: (TodoListIntent) -> Unit
) {
    val task = row.item.task
    DropdownMenu(
        expanded = menu != TodoCardMenu.HIDDEN,
        onDismissRequest = { onMenuChange(TodoCardMenu.HIDDEN) },
        containerColor = white
    ) {
        if (menu == TodoCardMenu.MOVE) {
            val targets = listOf<DomainTodoSection?>(null) + sections
            targets.forEach { target ->
                val selected = task.sectionId == target?.id
                DropdownMenuItem(
                    text = {
                        Text(
                            text = target?.title ?: stringResource(R.string.todo_section_none),
                            style = text_16,
                            color = black_2
                        )
                    },
                    trailingIcon = if (selected) {
                        {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = stringResource(R.string.todo_selected)
                            )
                        }
                    } else null,
                    onClick = {
                        onMenuChange(TodoCardMenu.HIDDEN)
                        if (!selected) onIntent(TodoListIntent.MoveTask(task.id, target?.id))
                    }
                )
            }
        } else {
            DropdownMenuItem(
                text = {
                    Text(
                        stringResource(R.string.todo_card_subtask_quick),
                        style = text_16,
                        color = black_2
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = null,
                        tint = black_2
                    )
                },
                onClick = {
                    onMenuChange(TodoCardMenu.HIDDEN)
                    onIntent(TodoListIntent.StartNewSubtask(task.id))
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.todo_columns_move_to),
                        style = text_16,
                        color = black_2
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.DriveFileMove,
                        contentDescription = null,
                        tint = black_2
                    )
                },
                onClick = { onMenuChange(TodoCardMenu.MOVE) }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.todo_delete),
                        style = text_16,
                        color = error_base
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = null,
                        tint = error_base
                    )
                },
                onClick = {
                    onMenuChange(TodoCardMenu.HIDDEN)
                    onIntent(TodoListIntent.RequestDeleteTask(task.id))
                }
            )
        }
    }
}

@Composable
internal fun TodoCardMeta(row: TodoListRow, labels: TodoDayLabels) {
    TodoTaskMetadata(row.item, row.isDone, labels, style = CARD_META_STYLE)
}

/** Ввод новой задачи карточкой: Enter добавляет и оставляет поле для следующей. */
@Composable
private fun TodoNewCard(
    editor: TodoInlineEditor,
    accent: Color,
    onIntent: (TodoListIntent) -> Unit
) {
    TodoCard(
        modifier = Modifier.fillMaxWidth(),
        padding = PaddingValues(start = 2.dp, end = 12.dp),
        elevation = 2.dp,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.animateTodoTextSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TodoDoneCircle(done = false, accent = accent, onToggle = {})
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp)
            ) {
                TodoTaskTextEditor(
                    title = editor.text,
                    note = editor.note,
                    accent = accent,
                    onTitleChange = { onIntent(TodoListIntent.EditorTextChanged(it)) },
                    onNoteChange = { onIntent(TodoListIntent.EditorNoteChanged(it)) },
                    onSubmit = { onIntent(TodoListIntent.EditorSubmit) },
                    onFinish = { onIntent(TodoListIntent.EditorFinish(null)) }
                )
            }
        }
    }
}

/**
 * Пунктирная карточка без тени внизу столбца — место следующей задачи: пунктирный круг и
 * тусклое «Новая задача». Тап — карточка становится полем ввода.
 */
@Composable
private fun TodoNewTaskCard(onClick: () -> Unit) {
    val description = stringResource(R.string.todo_task_new_title)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val stroke = 1.dp.toPx()
                val radius = 14.dp.toPx()
                drawRoundRect(
                    color = gray_8,
                    topLeft = Offset(stroke / 2, stroke / 2),
                    size = Size(size.width - stroke, size.height - stroke),
                    cornerRadius = CornerRadius(radius, radius),
                    style = Stroke(
                        width = stroke,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(
                                6.dp.toPx(),
                                4.dp.toPx()
                            )
                        )
                    )
                )
            }
            .clip(CARD_SHAPE)
            .clickable(onClick = onClick)
            .clearAndSetSemantics {
                contentDescription = description
                role = Role.Button
            }
            .padding(start = 2.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TodoDashedCircle()
        Text(
            text = description,
            style = TODO_ROW_TITLE_STYLE,
            color = gray_7.copy(alpha = 0.6f),
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}

/** Последний столбец — тусклое «Новый столбик»: тап — на его месте поле названия нового подсписка. */
@Composable
private fun TodoNewColumn(onClick: () -> Unit) {
    TodoColumnTitleRow(
        modifier = Modifier
            .width(COLUMN_WIDTH)
            .clip(CARD_SHAPE)
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        Text(
            text = stringResource(R.string.todo_columns_new_column),
            style = COLUMN_TITLE_STYLE,
            color = gray_7.copy(alpha = 0.5f)
        )
    }
}
