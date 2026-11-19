package com.zaroslikov.todo

import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.buildTodoSectionTree
import com.zaroslikov.todo.domain.model.todoInsertIndex
import com.zaroslikov.todo.domain.model.todoMoved
import com.zaroslikov.todo.domain.model.todoOrdered
import com.zaroslikov.todo.domain.model.todoTopLevelIds
import com.zaroslikov.todo.domain.model.withTodoSectionMoved
import com.zaroslikov.todo.domain.model.withTodoSubtaskMoved
import com.zaroslikov.todo.domain.model.withTodoTaskMoved
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

/** Чистые правила перестановки задач и подсписков переносом. */
class TodoReorderTest {

    private val known = setOf(5L, 6L)

    private fun item(id: Long, sectionId: Long? = null, position: Int = id.toInt(), parentId: Long? = null) =
        DomainTodoTaskItem(
            task = DomainTodoTask(
                id = id, listId = 10, parentId = parentId, sectionId = sectionId,
                title = "T$id", statusId = 1, position = position
            ),
            status = null
        )

    // Без подсписка: 1, 2; «5»: 3, 4 (с подзадачами 41, 42); «6»: пусто
    private val tasks = listOf(
        item(1), item(2),
        item(3, sectionId = 5, position = 0), item(4, sectionId = 5, position = 1),
        item(41, parentId = 4, position = 0), item(42, parentId = 4, position = 1)
    )

    private val sections = listOf(
        DomainTodoSection(id = 5, listId = 10, title = "A", position = 0),
        DomainTodoSection(id = 6, listId = 10, title = "B", position = 1)
    )

    /** Порядок, как его покажет экран: (подсписок → задачи верхнего уровня). */
    private fun List<DomainTodoTaskItem>.layout(): List<Pair<Long?, List<Long>>> =
        buildTodoSectionTree(sections, this).map { group -> group.section?.id to group.nodes.map { it.item.task.id } }

    private fun List<DomainTodoTaskItem>.positions(vararg ids: Long): List<Int> =
        ids.map { id -> single { it.task.id == id }.task.position }

    @Test
    fun `todoMoved — убирает со старого места и ограничивает индекс`() {
        assertEquals(listOf(2L, 1L, 3L), listOf(1L, 2L, 3L).todoMoved(1L, 1))
        assertEquals(listOf(2L, 3L, 1L), listOf(1L, 2L, 3L).todoMoved(1L, 99))
        assertEquals(listOf(3L, 1L, 2L), listOf(1L, 2L, 3L).todoMoved(3L, -5))
        // Элемента не было — просто вставка
        assertEquals(listOf(1L, 9L, 2L), listOf(1L, 2L).todoMoved(9L, 1))
    }

    @Test
    fun `todoInsertIndex — место среди всех по месту среди видимых`() {
        // 2 и 4 скрыты (завершены)
        val full = listOf(1L, 2L, 3L, 4L, 5L)
        val visible = listOf(1L, 3L, 5L)
        assertEquals(0, todoInsertIndex(full, visible, 0))
        assertEquals(2, todoInsertIndex(full, visible, 1))
        assertEquals(4, todoInsertIndex(full, visible, 2))
        // За последним видимым — сразу после него, скрытые дальше не перескакиваются
        assertEquals(3, todoInsertIndex(full, listOf(1L, 3L), 2))
        assertEquals(5, todoInsertIndex(full, visible, 3))
        // Видимых нет — в конец
        assertEquals(5, todoInsertIndex(full, emptyList(), 0))
        assertEquals(0, todoInsertIndex(emptyList(), emptyList(), 0))
    }

    @Test
    fun `перенос внутри подсписка перенумеровывает позиции подряд`() {
        val moved = tasks.withTodoTaskMoved(4, 5, 0, known)
        assertEquals(listOf(null to listOf(1L, 2L), 5L to listOf(4L, 3L), 6L to emptyList()), moved.layout())
        assertEquals(listOf(0, 1), moved.positions(4, 3))
        // Подзадачи остаются под родителем и со своими позициями
        assertEquals(listOf(0, 1), moved.positions(41, 42))
    }

    @Test
    fun `перенос между подсписками и в пустой подсписок`() {
        val between = tasks.withTodoTaskMoved(1, 5, 1, known)
        assertEquals(listOf(null to listOf(2L), 5L to listOf(3L, 1L, 4L), 6L to emptyList()), between.layout())
        assertEquals(listOf(0, 1, 2), between.positions(3, 1, 4))
        // Исходный подсписок — без дыры
        assertEquals(listOf(0), between.positions(2))

        val toEmpty = tasks.withTodoTaskMoved(4, 6, 3, known)
        assertEquals(listOf(null to listOf(1L, 2L), 5L to listOf(3L), 6L to listOf(4L)), toEmpty.layout())
        assertEquals(listOf(0), toEmpty.positions(4))
    }

    @Test
    fun `перенос в «без подсписка» и обратно, граничные индексы`() {
        val out = tasks.withTodoTaskMoved(3, null, -1, known)
        assertEquals(listOf(null to listOf(3L, 1L, 2L), 5L to listOf(4L), 6L to emptyList()), out.layout())
        val back = out.withTodoTaskMoved(3, 5, Int.MAX_VALUE, known)
        assertEquals(listOf(null to listOf(1L, 2L), 5L to listOf(4L, 3L), 6L to emptyList()), back.layout())
        assertEquals(listOf(4L, 3L), back.todoTopLevelIds(5, known))
    }

    @Test
    fun `подзадача, неизвестная задача и чужой подсписок — без изменений`() {
        assertSame(tasks, tasks.withTodoTaskMoved(41, 5, 0, known))
        assertSame(tasks, tasks.withTodoTaskMoved(99, 5, 0, known))
        assertSame(tasks, tasks.withTodoTaskMoved(1, 77, 0, known))
    }

    @Test
    fun `подзадачи переставляются только среди своих`() {
        val moved = tasks.withTodoSubtaskMoved(42, 0)
        assertEquals(listOf(1, 0), moved.positions(41, 42))
        assertSame(tasks, tasks.withTodoSubtaskMoved(4, 0))
    }

    @Test
    fun `перестановка подсписков`() {
        val moved = sections.withTodoSectionMoved(6, 0)
        assertEquals(listOf(6L to 0, 5L to 1), moved.sortedBy { it.position }.map { it.id to it.position })
        assertEquals(sections, sections.withTodoSectionMoved(5, -3))
        assertEquals(listOf(5L to 1, 6L to 0), sections.withTodoSectionMoved(5, 99).map { it.id to it.position })
        assertSame(sections, sections.withTodoSectionMoved(99, 0))
    }

    @Test
    fun `todoOrdered — сначала заданный порядок, затем остальные, чужие id игнорируются`() {
        assertEquals(listOf(3L, 1L, 2L), todoOrdered(listOf(1L, 2L, 3L), listOf(3L, 1L)))
        assertEquals(listOf(2L, 1L), todoOrdered(listOf(1L, 2L), listOf(9L, 2L, 2L)))
    }
}
