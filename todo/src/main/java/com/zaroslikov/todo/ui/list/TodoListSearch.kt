package com.zaroslikov.todo.ui.list

import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.TodoLinkType
import com.zaroslikov.todo.domain.model.TodoPriority
import com.zaroslikov.todo.domain.model.TodoRepeatPeriod
import com.zaroslikov.todo.domain.model.isTodoOverdue
import com.zaroslikov.todo.ui.components.titleRes
import com.zaroslikov.todo.util.TodoDayLabels
import com.zaroslikov.todo.util.dueToSmartDisplay
import com.zaroslikov.todo.util.isoDateTimeToDisplay
import com.zaroslikov.todo.util.isoToDisplay

private val searchWhitespace = Regex("\\s+")

internal fun todoSearchTerms(query: String): List<String> =
    if (query.isBlank()) emptyList()
    else query.trim().searchNormalized().split(searchWhitespace)

private fun String.searchNormalized(): String = lowercase().replace('ё', 'е')

internal fun todoSearchLabelIds(): Set<Int> = buildSet {
    TodoPriority.entries.forEach { add(it.titleRes()) }
    TodoDefaultStatus.entries.forEach { add(it.titleRes()) }
    TodoRepeatPeriod.entries.forEach { add(it.titleRes()) }
    TodoLinkType.entries.forEach {
        add(it.titleRes(isOutgoing = true))
        add(it.titleRes(isOutgoing = false))
    }
    add(R.string.todo_repeat_forever)
    add(R.string.todo_section_none)
    add(R.string.todo_list_today)
    add(R.string.todo_list_tomorrow)
    add(R.string.todo_list_yesterday)
    add(R.string.todo_overdue)
}

internal fun TodoListState.matchesTodoSearch(
    item: DomainTodoTaskItem,
    terms: List<String>,
    sectionTitle: String?
): Boolean {
    val task = item.task
    val fields = buildList {
        add(task.title)
        add(task.note)
        addAll(task.assignees)
        item.tags.forEach { add("#${it.title}") }
        item.status?.let { status ->
            add(status.title)
            status.defaultCode?.let { searchLabels[it.titleRes()]?.let(::add) }
        }
        list?.let { add(it.title) }
        (sectionTitle ?: searchLabels[R.string.todo_section_none])?.let(::add)
        searchLabels[task.priority.titleRes()]?.let(::add)
        searchLabels[task.repeatPeriod.titleRes()]?.let(::add)
        if (task.repeatPeriod != TodoRepeatPeriod.NONE && task.repeatEndDate == null) {
            searchLabels[R.string.todo_repeat_forever]?.let(::add)
        }
        listOfNotNull(task.dueDate, task.repeatEndDate).forEach { date ->
            add(date)
            add(isoToDisplay(date))
        }
        task.dueTime?.let(::add)
        listOfNotNull(task.createdAt, task.completedAt).forEach { dateTime ->
            add(dateTime)
            add(isoDateTimeToDisplay(dateTime))
        }
        searchNow?.let { now ->
            val days = TodoDayLabels(
                today = searchLabels[R.string.todo_list_today].orEmpty(),
                tomorrow = searchLabels[R.string.todo_list_tomorrow].orEmpty(),
                yesterday = searchLabels[R.string.todo_list_yesterday].orEmpty()
            )
            add(dueToSmartDisplay(task.dueDate, task.dueTime, days, now.toLocalDate()))
            if (isTodoOverdue(task.dueDate, task.dueTime, isDone(item), now)) {
                searchLabels[R.string.todo_overdue]?.let(::add)
            }
        }
        addAll(searchText[task.id].orEmpty())
    }.map { it.searchNormalized() }
    return terms.all { term -> fields.any { term in it } }
}
