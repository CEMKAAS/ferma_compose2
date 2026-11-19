package com.zaroslikov.todo.data.room.mapper

import com.zaroslikov.todo.data.room.dto.TodoLinkedTaskDto
import com.zaroslikov.todo.data.room.dto.TodoListSummaryDto
import com.zaroslikov.todo.data.room.dto.TodoProjectTaskItemDto
import com.zaroslikov.todo.data.room.dto.TodoTaskItemDto
import com.zaroslikov.todo.data.room.dto.TodoTaskTagDto
import com.zaroslikov.todo.data.room.table.TodoCommentTable
import com.zaroslikov.todo.data.room.table.TodoListTable
import com.zaroslikov.todo.data.room.table.TodoSectionTable
import com.zaroslikov.todo.data.room.table.TodoStatusTable
import com.zaroslikov.todo.data.room.table.TodoTagTable
import com.zaroslikov.todo.data.room.table.TodoTaskLinkTable
import com.zaroslikov.todo.data.room.table.TodoTaskTable
import com.zaroslikov.todo.data.room.table.TodoTaskTagTable
import com.zaroslikov.todo.domain.model.DomainTodoComment
import com.zaroslikov.todo.domain.model.DomainTodoLink
import com.zaroslikov.todo.domain.model.DomainTodoLinkedTask
import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoListSummary
import com.zaroslikov.todo.domain.model.DomainTodoProjectTask
import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.DomainTodoTaskTag
import com.zaroslikov.todo.domain.model.normalizeTodoAssignees
import com.zaroslikov.todo.domain.model.DomainTodoAssignee
import com.zaroslikov.todo.domain.model.TodoAssigneeProfile
import com.zaroslikov.todo.data.room.table.TodoAssigneeTable

fun TodoAssigneeTable.toDomain() =
    DomainTodoAssignee(idPT = idPT, name = name, profile = profile ?: TodoAssigneeProfile(firstName = name))

fun DomainTodoAssignee.toTable() =
    TodoAssigneeTable(idPT = idPT, nameKey = name.trim().lowercase(), name = name.trim(), profile = profile)

fun TodoListTable.toDomain() = DomainTodoList(
    id = id,
    idPT = idPT,
    title = title,
    colorIndex = colorIndex,
    position = position,
    createdAt = createdAt,
    viewMode = viewMode,
    icon = icon,
    isPinned = isPinned,
    isLastTaskDestination = isLastTaskDestination
)

fun DomainTodoList.toTable() = TodoListTable(
    id = id,
    idPT = idPT,
    title = title,
    colorIndex = colorIndex,
    position = position,
    createdAt = createdAt,
    viewMode = viewMode,
    icon = icon,
    isPinned = isPinned,
    isLastTaskDestination = isLastTaskDestination
)

fun TodoSectionTable.toDomain() = DomainTodoSection(
    id = id,
    listId = listId,
    title = title,
    position = position,
    isCollapsed = isCollapsed
)

fun DomainTodoSection.toTable() = TodoSectionTable(
    id = id,
    listId = listId,
    title = title,
    position = position,
    isCollapsed = isCollapsed
)

fun TodoListSummaryDto.toDomain() = DomainTodoListSummary(
    list = list.toDomain(),
    totalCount = totalCount,
    doneCount = doneCount,
    overdueCount = overdueCount
)

fun TodoStatusTable.toDomain() = DomainTodoStatus(
    id = id,
    idPT = idPT,
    title = title,
    colorIndex = colorIndex,
    position = position,
    defaultCode = defaultCode,
    isDone = isDone
)

fun DomainTodoStatus.toTable() = TodoStatusTable(
    id = id,
    idPT = idPT,
    title = title,
    colorIndex = colorIndex,
    position = position,
    defaultCode = defaultCode,
    isDone = isDone
)

fun TodoTagTable.toDomain() = DomainTodoTag(
    id = id,
    idPT = idPT,
    title = title,
    colorIndex = colorIndex
)

fun DomainTodoTag.toTable() = TodoTagTable(
    id = id,
    idPT = idPT,
    title = title,
    colorIndex = colorIndex
)

fun TodoTaskTable.toDomain() = DomainTodoTask(
    assignees = assignees,
    recurrenceGenerated = recurrenceGenerated,
    repeatPeriod = repeatPeriod,
    repeatEndDate = repeatEndDate,
    id = id,
    listId = listId,
    parentId = parentId,
    sectionId = sectionId,
    title = title,
    note = note,
    priority = priority,
    statusId = statusId,
    dueDate = dueDate,
    dueTime = dueTime,
    position = position,
    createdAt = createdAt,
    completedAt = completedAt
)

fun DomainTodoTask.toTable() = TodoTaskTable(
    assignees = normalizeTodoAssignees(assignees),
    recurrenceGenerated = recurrenceGenerated,
    repeatPeriod = repeatPeriod,
    repeatEndDate = repeatEndDate,
    id = id,
    listId = listId,
    parentId = parentId,
    sectionId = sectionId,
    title = title,
    note = note,
    priority = priority,
    statusId = statusId,
    dueDate = dueDate,
    dueTime = dueTime,
    position = position,
    createdAt = createdAt,
    completedAt = completedAt
)

fun TodoTaskTagTable.toDomain() = DomainTodoTaskTag(taskId = taskId, tagId = tagId)

fun DomainTodoTaskTag.toTable() = TodoTaskTagTable(taskId = taskId, tagId = tagId)

fun TodoTaskLinkTable.toDomain() = DomainTodoLink(
    id = id,
    taskId = taskId,
    linkedTaskId = linkedTaskId,
    type = type
)

fun DomainTodoLink.toTable() = TodoTaskLinkTable(
    id = id,
    taskId = taskId,
    linkedTaskId = linkedTaskId,
    type = type
)

fun TodoLinkedTaskDto.toDomain() = DomainTodoLinkedTask(
    link = link.toDomain(),
    task = task.toDomain(),
    status = status?.toDomain(),
    listTitle = listTitle,
    isOutgoing = isOutgoing,
    listIcon = listIcon
)

fun TodoCommentTable.toDomain() = DomainTodoComment(
    id = id,
    taskId = taskId,
    text = text,
    createdAt = createdAt,
    editedAt = editedAt
)

fun DomainTodoComment.toTable() = TodoCommentTable(
    id = id,
    taskId = taskId,
    text = text,
    createdAt = createdAt,
    editedAt = editedAt
)

/** Склейка двух запросов: задачи со счётчиками + теги задач, сгруппированные по `task_id`. */
fun List<TodoTaskItemDto>.toDomainItems(tags: List<TodoTaskTagDto>): List<DomainTodoTaskItem> {
    val tagsByTask = tags.groupBy({ it.taskId }, { it.tag.toDomain() })
    return map { it.toDomain(tagsByTask[it.task.id].orEmpty()) }
}

fun TodoTaskItemDto.toDomain(tags: List<DomainTodoTag>) = DomainTodoTaskItem(
    task = task.toDomain(),
    status = status?.toDomain(),
    tags = tags,
    subtaskCount = subtaskCount,
    subtaskDoneCount = subtaskDoneCount,
    commentCount = commentCount,
    linkCount = linkCount,
    blockingCount = blockingCount,
    blockedByCount = blockedByCount
)

fun List<TodoProjectTaskItemDto>.toDomainProjectTasks(tags: List<TodoTaskTagDto>): List<DomainTodoProjectTask> {
    val tagsByTask = tags.groupBy({ it.taskId }, { it.tag.toDomain() })
    return map {
        DomainTodoProjectTask(
            item = it.item.toDomain(tagsByTask[it.item.task.id].orEmpty()),
            listTitle = it.listTitle,
            listColorIndex = it.listColorIndex,
            parentTitle = it.parentTitle,
            listIcon = it.listIcon
        )
    }
}
