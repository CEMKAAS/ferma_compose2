package com.zaroslikov.todo

import com.zaroslikov.todo.data.room.repository.sanitizedForImport
import com.zaroslikov.todo.domain.model.DomainTodoBackup
import com.zaroslikov.todo.domain.model.DomainTodoComment
import com.zaroslikov.todo.domain.model.DomainTodoLink
import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskTag
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TodoBackupSanitizeTest {

    private fun task(
        id: Long,
        listId: Long,
        parentId: Long? = null,
        statusId: Long = 1,
        completedAt: String? = null
    ) = DomainTodoTask(
        id = id,
        listId = listId,
        parentId = parentId,
        title = "T$id",
        statusId = statusId,
        createdAt = "2026-01-01T09:00",
        completedAt = completedAt
    )

    private val backup = DomainTodoBackup(
        lists = listOf(
            DomainTodoList(id = 1, idPT = 7, title = "Свой"),
            DomainTodoList(id = 1, idPT = 7, title = "Дубликат id"),
            DomainTodoList(id = 2, idPT = 8, title = "Свой 2"),
            DomainTodoList(id = 3, idPT = 99, title = "Чужой проект"),
            DomainTodoList(id = 0, idPT = 7, title = "Без id")
        ),
        statuses = listOf(
            DomainTodoStatus(id = 1, idPT = 7, title = "", defaultCode = TodoDefaultStatus.PLANNED),
            DomainTodoStatus(id = 2, idPT = 7, title = "", defaultCode = TodoDefaultStatus.PLANNED),
            DomainTodoStatus(id = 3, idPT = 7, title = "", defaultCode = TodoDefaultStatus.DONE, isDone = false),
            DomainTodoStatus(id = 4, idPT = 99, title = ""),
            DomainTodoStatus(id = -1, idPT = 7, title = "Без id")
        ),
        tasks = listOf(
            task(10, 1),
            task(11, 1, parentId = 10),
            task(12, 1, parentId = 11), // подзадача подзадачи
            task(13, 2, parentId = 10), // родитель в другом списке
            task(14, 3), // список чужого проекта
            task(15, 1, parentId = 999), // висячий родитель
            task(20, 2),
            task(0, 1), // без id
            task(21, 1, statusId = 3), // завершённая без даты завершения
            task(22, 1, statusId = 1, completedAt = "2026-01-02T10:00"), // незавершённая с датой
            task(23, 1, statusId = 3, completedAt = "2026-01-03T10:00")
        ),
        tags = listOf(
            DomainTodoTag(id = 1, idPT = 7, title = " Срочно "),
            DomainTodoTag(id = 2, idPT = 7, title = "Срочно"),
            DomainTodoTag(id = 3, idPT = 7, title = "  "),
            DomainTodoTag(id = 4, idPT = 8, title = "Чужой"),
            DomainTodoTag(id = 0, idPT = 7, title = "Без id")
        ),
        taskTags = listOf(
            DomainTodoTaskTag(10, 1),
            DomainTodoTaskTag(10, 1),
            DomainTodoTaskTag(10, 2), // дубликат названия — сливается с тегом 1
            DomainTodoTaskTag(11, 2), // дубликат названия — задача получает тег 1
            DomainTodoTaskTag(10, 4), // тег другого проекта
            DomainTodoTaskTag(14, 1)
        ),
        links = listOf(
            DomainTodoLink(id = 1, taskId = 10, linkedTaskId = 11),
            DomainTodoLink(id = 2, taskId = 11, linkedTaskId = 10), // обратная пара
            DomainTodoLink(id = 3, taskId = 10, linkedTaskId = 10), // сама с собой
            DomainTodoLink(id = 4, taskId = 10, linkedTaskId = 20), // разные проекты
            DomainTodoLink(id = 5, taskId = 10, linkedTaskId = 14),
            DomainTodoLink(id = 0, taskId = 10, linkedTaskId = 21)
        ),
        comments = listOf(
            DomainTodoComment(id = 1, taskId = 11, text = "ок", createdAt = "2026-01-01T10:00"),
            DomainTodoComment(id = 2, taskId = 14, text = "чужой", createdAt = "2026-01-01T10:00"),
            DomainTodoComment(id = 0, taskId = 11, text = "без id", createdAt = "2026-01-01T10:00")
        )
    )

    private val clean = backup.sanitizedForImport(projectIds = setOf(7, 8))

    @Test
    fun lists_ownProjectsWithoutDuplicates() {
        assertEquals(listOf(1L, 2L), clean.lists.map { it.id })
        assertEquals("Свой", clean.lists.first().title)
    }

    @Test
    fun statuses_oneDefaultOfEachKindAndDoneFlagByCode() {
        assertEquals(listOf(1L, 3L), clean.statuses.map { it.id })
        assertEquals(true, clean.statuses.single { it.id == 3L }.isDone)
    }

    @Test
    fun tasks_oneLevelOfSubtasksInSameList() {
        assertEquals(setOf(10L, 11L, 20L, 21L, 22L, 23L), clean.tasks.map { it.id }.toSet())
    }

    @Test
    fun tasks_completedAtFollowsStatus() {
        val completedAt = clean.tasks.associate { it.id to it.completedAt }
        assertEquals("2026-01-01T09:00", completedAt[21L])
        assertNull(completedAt[22L])
        assertEquals("2026-01-03T10:00", completedAt[23L])
    }

    @Test
    fun tags_trimmedUniqueNonEmpty() {
        assertEquals(listOf(1L, 4L), clean.tags.map { it.id })
        assertEquals("Срочно", clean.tags.first().title)
        assertEquals(listOf(DomainTodoTaskTag(10, 1), DomainTodoTaskTag(11, 1)), clean.taskTags)
    }

    @Test
    fun links_withoutSelfReverseAndCrossProject() {
        assertEquals(listOf(1L), clean.links.map { it.id })
    }

    @Test
    fun comments_onlyForSurvivingTasks() {
        assertEquals(listOf(1L), clean.comments.map { it.id })
    }
}
