package com.zaroslikov.todo

import com.zaroslikov.todo.data.room.converter.TodoConverters
import com.zaroslikov.todo.data.room.repository.sanitizedForImport
import com.zaroslikov.todo.domain.model.DomainTodoBackup
import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoListViewMode
import com.zaroslikov.todo.domain.model.buildTodoSectionTree
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Подсписки: раскладка дерева, вид списка и бэкап. */
class TodoSectionsTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun item(id: Long, sectionId: Long? = null, parentId: Long? = null, position: Int = id.toInt()) =
        DomainTodoTaskItem(
            task = DomainTodoTask(
                id = id, listId = 1, parentId = parentId, sectionId = sectionId,
                title = "T$id", statusId = 1, position = position
            ),
            status = null
        )

    private val garden = DomainTodoSection(id = 5, listId = 1, title = "Грядки", position = 1)
    private val greenhouse = DomainTodoSection(id = 6, listId = 1, title = "Теплица", position = 0)

    @Test
    fun sectionTree_withoutSectionFirstThenSectionsByPosition() {
        val tree = buildTodoSectionTree(
            sections = listOf(garden, greenhouse),
            items = listOf(
                item(1, sectionId = 5),
                item(2),
                item(3, sectionId = 6),
                item(4, sectionId = 99), // неизвестный подсписок
                item(5, sectionId = 5, position = 0)
            )
        )
        assertEquals(listOf(null, 6L, 5L), tree.map { it.section?.id })
        assertEquals(listOf(2L, 4L), tree[0].nodes.map { it.item.task.id })
        assertEquals(listOf(3L), tree[1].nodes.map { it.item.task.id })
        assertEquals(listOf(5L, 1L), tree[2].nodes.map { it.item.task.id })
    }

    @Test
    fun sectionTree_subtasksStayUnderParentIgnoringOwnSection() {
        val tree = buildTodoSectionTree(
            sections = listOf(garden),
            items = listOf(item(1, sectionId = 5), item(11, parentId = 1, sectionId = null), item(12, parentId = 1, sectionId = 5))
        )
        assertEquals(emptyList<Long>(), tree[0].nodes.map { it.item.task.id })
        val node = tree[1].nodes.single()
        assertEquals(1L, node.item.task.id)
        assertEquals(listOf(11L, 12L), node.subtasks.map { it.task.id })
    }

    @Test
    fun sectionTree_emptySectionsAreKept() {
        val tree = buildTodoSectionTree(listOf(garden), emptyList())
        assertEquals(listOf(null, 5L), tree.map { it.section?.id })
        assertEquals(0, tree.sumOf { it.nodes.size })
    }

    @Test
    fun viewMode_codeRoundTripAndUnknownIsList() {
        val converters = TodoConverters()
        TodoListViewMode.entries.forEach { assertEquals(it, converters.toViewMode(converters.fromViewMode(it))) }
        assertEquals("columns", converters.fromViewMode(TodoListViewMode.COLUMNS))
        assertEquals(TodoListViewMode.LIST, converters.toViewMode("kanban"))
    }

    @Test
    fun backup_oldFormatWithoutSectionsAndViewModeImportsWithDefaults() {
        val old = """
            {"lists":[{"id":1,"idPT":7,"title":"Огород"}],
             "tasks":[{"id":10,"listId":1,"title":"Полить","statusId":1}]}
        """.trimIndent()
        val backup = json.decodeFromString(DomainTodoBackup.serializer(), old)
        assertEquals(TodoListViewMode.LIST, backup.lists.single().viewMode)
        assertEquals(emptyList<DomainTodoSection>(), backup.sections)
        assertNull(backup.tasks.single().sectionId)
    }

    @Test
    fun backup_sectionsAndViewModeSurviveSerialization() {
        val backup = DomainTodoBackup(
            lists = listOf(DomainTodoList(id = 1, idPT = 7, title = "Огород", viewMode = TodoListViewMode.COLUMNS)),
            sections = listOf(garden.copy(isCollapsed = true)),
            tasks = listOf(DomainTodoTask(id = 10, listId = 1, sectionId = 5, title = "Полить", statusId = 1))
        )
        val encoded = json.encodeToString(DomainTodoBackup.serializer(), backup)
        assertEquals(backup, json.decodeFromString(DomainTodoBackup.serializer(), encoded))
    }

    @Test
    fun sanitize_sectionsOfSurvivingListsAndValidTaskSections() {
        val backup = DomainTodoBackup(
            lists = listOf(
                DomainTodoList(id = 1, idPT = 7, title = "Свой"),
                DomainTodoList(id = 2, idPT = 7, title = "Свой 2"),
                DomainTodoList(id = 3, idPT = 99, title = "Чужой")
            ),
            sections = listOf(
                DomainTodoSection(id = 5, listId = 1, title = "А"),
                DomainTodoSection(id = 5, listId = 1, title = "Дубликат id"),
                DomainTodoSection(id = 6, listId = 2, title = "Б"),
                DomainTodoSection(id = 7, listId = 3, title = "Чужой список"),
                DomainTodoSection(id = 8, listId = 42, title = "Нет списка"),
                DomainTodoSection(id = 0, listId = 1, title = "Без id")
            ),
            tasks = listOf(
                DomainTodoTask(id = 10, listId = 1, sectionId = 5, title = "Своя", statusId = 1),
                DomainTodoTask(id = 11, listId = 1, sectionId = 6, title = "Подсписок другого списка", statusId = 1),
                DomainTodoTask(id = 12, listId = 1, sectionId = 8, title = "Нет подсписка", statusId = 1),
                DomainTodoTask(id = 13, listId = 1, parentId = 10, sectionId = 5, title = "Подзадача", statusId = 1),
                DomainTodoTask(id = 14, listId = 2, sectionId = 6, title = "Своя 2", statusId = 1)
            )
        )
        val clean = backup.sanitizedForImport(projectIds = setOf(7))
        assertEquals(listOf(5L, 6L), clean.sections.map { it.id })
        assertEquals("А", clean.sections.first().title)
        val sections = clean.tasks.associate { it.id to it.sectionId }
        assertEquals(5L, sections[10L])
        assertNull(sections[11L])
        assertNull(sections[12L])
        assertNull(sections[13L])
        assertEquals(6L, sections[14L])
    }
}
