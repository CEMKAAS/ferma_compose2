package com.zaroslikov.todo.ui.list

import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.TodoPriority
import com.zaroslikov.todo.domain.model.TodoRepeatPeriod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime

class TodoListSearchTest {
    private val list = DomainTodoList(id = 10, idPT = 1, title = "Хозяйство")
    private val section = DomainTodoSection(id = 5, listId = list.id, title = "Теплица", position = 0)
    private val status = DomainTodoStatus(id = 1, idPT = 1, title = "Проверка")
    private val tag = DomainTodoTag(id = 1, idPT = 1, title = "Расходники")

    private fun task(id: Long, title: String, parentId: Long? = null) = DomainTodoTask(
        id = id,
        listId = list.id,
        parentId = parentId,
        title = title,
        statusId = status.id,
        position = id.toInt()
    )

    private fun load(vararg tasks: DomainTodoTaskItem, sections: List<DomainTodoSection> = listOf(section)) =
        todoListReduce(TodoListState(), TodoListIntent.DataLoaded(list, tasks.toList(), listOf(tag), sections))

    private fun TodoListState.search(query: String) = todoListReduce(this, TodoListIntent.Search(query))

    private fun List<TodoListRow>.taskIds() = map { it.item.task.id }

    @Test
    fun `поиск находит задачу по заметке исполнителю тегу статусу подсписку и сроку`() {
        val matching = task(1, "Проверить инвентарь").copy(
            note = "Коробки на стеллаже",
            sectionId = section.id,
            dueDate = "2026-11-18",
            dueTime = "08:45",
            assignees = listOf("Алёна")
        )
        val other = task(2, "Полить грядки")
        val loaded = load(DomainTodoTaskItem(matching, status, listOf(tag)), DomainTodoTaskItem(other, null))

        for (query in listOf("стеллаже", "АЛЕН", "Расходники", "#Расходники", "Проверка", "Теплица", "18.11.2026", "08:45")) {
            assertEquals(query, listOf(1L), loaded.search(query).rows.taskIds())
        }
    }

    @Test
    fun `все слова запроса могут встретиться в разных полях одной задачи`() {
        val matching = task(1, "Проверить инвентарь").copy(note = "Коробки на стеллаже")
        val other = task(2, "Коробки для овощей")
        val loaded = load(DomainTodoTaskItem(matching, status), DomainTodoTaskItem(other, status))

        assertEquals(listOf(1L), loaded.search("  КОРОБКИ \t инвен  ").rows.taskIds())
        assertEquals(emptyList<Long>(), loaded.search("коробки овощей инвентарь").rows.taskIds())
        assertEquals(listOf(1L, 2L), loaded.search(" \t ").rows.taskIds())
    }

    @Test
    fun `поиск показывает завершённую подзадачу в свёрнутом подсписке и под свёрнутым родителем`() {
        val done = status.copy(id = 2, title = "Выполнено", isDone = true)
        val parent = task(1, "Обслужить насос").copy(sectionId = section.id)
        val child = task(2, "Подтянуть хомут", parentId = parent.id).copy(statusId = done.id)
        val sibling = task(3, "Проверить шланг", parentId = parent.id)
        val loaded = load(
            DomainTodoTaskItem(parent, status),
            DomainTodoTaskItem(child, done),
            DomainTodoTaskItem(sibling, status),
            sections = listOf(section.copy(isCollapsed = true))
        )
        val collapsed = todoListReduce(loaded, TodoListIntent.ToggleCollapse(parent.id))

        val found = collapsed.search("хомут")

        assertEquals(listOf(1L, 2L), found.rows.taskIds())
        assertEquals(listOf(1L, 2L), found.columns.single { it.section?.id == section.id }.cardRows.taskIds())
        assertEquals(emptyList<Long>(), found.search("").rows.taskIds())
    }

    @Test
    fun `поиск применяется вместе с фильтрами приоритета и тега`() {
        val loaded = load(
            DomainTodoTaskItem(task(1, "Сменить масло").copy(priority = TodoPriority.HIGH), status, listOf(tag)),
            DomainTodoTaskItem(task(2, "Купить масло").copy(priority = TodoPriority.LOW), status, listOf(tag)),
            DomainTodoTaskItem(task(3, "Слить масло").copy(priority = TodoPriority.HIGH), status),
            DomainTodoTaskItem(task(4, "Подтянуть винты").copy(priority = TodoPriority.HIGH), status, listOf(tag))
        )
        val byPriority = todoListReduce(loaded, TodoListIntent.SelectPriority(TodoPriority.HIGH))
        val filtered = todoListReduce(byPriority, TodoListIntent.SelectTag(tag.id))

        assertEquals(listOf(1L), filtered.search("масло").rows.taskIds())
    }

    @Test
    fun `поиск находит русские названия встроенных статуса приоритета и повтора`() {
        val planned = status.copy(title = "planned", defaultCode = TodoDefaultStatus.PLANNED)
        val matching = task(1, "Проверить инвентарь").copy(
            priority = TodoPriority.HIGH,
            dueDate = "2026-11-18",
            repeatPeriod = TodoRepeatPeriod.WEEK,
            repeatEndDate = "2026-12-31"
        )
        val loaded = todoListReduce(
            TodoListState(),
            TodoListIntent.DataLoaded(
                list,
                listOf(DomainTodoTaskItem(matching, planned)),
                emptyList(),
                searchLabels = mapOf(
                    R.string.todo_status_planned to "В планах",
                    R.string.todo_priority_high to "Высокий",
                    R.string.todo_repeat_week to "Каждую неделю"
                )
            )
        )

        for (query in listOf("в планах", "высокий", "каждую неделю", "31.12.2026")) {
            assertEquals(query, listOf(1L), loaded.search(query).rows.taskIds())
        }
    }

    @Test
    fun `выдача по комментариям и связанным задачам обновляется при изменении их текста`() {
        val loaded = load(DomainTodoTaskItem(task(1, "Полить грядки"), status))
        val data = TodoListIntent.SearchMetadataLoaded(
            mapOf(1L to listOf("Дождеватель починен", "Установить фильтр")),
            LocalDateTime.of(2026, 11, 18, 8, 30)
        )
        val indexed = todoListReduce(loaded.search("дождеватель"), data)

        assertEquals(listOf(1L), indexed.search("дождеватель фильтр").rows.taskIds())
        val refreshed = todoListReduce(indexed.search("дождеватель"), data.copy(text = emptyMap()))
        assertEquals(emptyList<Long>(), refreshed.rows.taskIds())
        assertEquals(loaded.dataRevision, refreshed.dataRevision)

        val cleared = indexed.search("")
        assertEquals(emptyMap<Long, List<String>>(), cleared.searchText)
        assertNull(cleared.searchNow)
        assertEquals(cleared, todoListReduce(cleared, data))
        assertEquals(emptyList<Long>(), cleared.search("дождеватель").rows.taskIds())
    }

    @Test
    fun `поиск по просрочке обновляется по мере наступления срока`() {
        val timed = task(1, "Полить грядки").copy(dueDate = "2026-11-18", dueTime = "08:45")
        val loaded = todoListReduce(
            TodoListState(),
            TodoListIntent.DataLoaded(
                list,
                listOf(DomainTodoTaskItem(timed, status)),
                emptyList(),
                searchLabels = mapOf(R.string.todo_overdue to "Просрочено")
            )
        )
        val opened = loaded.search("просрочено")
        val before = LocalDateTime.of(2026, 11, 18, 8, 44)
        val first = todoListReduce(opened, TodoListIntent.SearchMetadataLoaded(emptyMap(), before))
        val after = todoListReduce(first, TodoListIntent.SearchMetadataLoaded(emptyMap(), before.plusMinutes(2)))

        assertEquals(emptyList<Long>(), first.rows.taskIds())
        assertEquals(listOf(1L), after.rows.taskIds())
    }
}
