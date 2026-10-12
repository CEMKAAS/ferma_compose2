package com.zaroslikov.crm.ui.tasks

import com.zaroslikov.crm.domain.model.DomainCrmTask
import com.zaroslikov.crm.util.parseIsoDate

fun crmTasksReduce(state: CrmTasksState, intent: CrmTasksIntent): CrmTasksState = when (intent) {
    is CrmTasksIntent.DataLoaded -> state.copy(
        isLoading = false,
        today = intent.today,
        activeTasks = intent.activeTasks,
        doneTasks = intent.doneTasks,
        clients = intent.clients,
        clientNames = intent.clientNames
    ).withGroups()

    is CrmTasksIntent.SelectTab -> state.copy(tab = intent.tab).withGroups()

    // Оптимистично переносим задачу между вкладками, не дожидаясь Flow из БД
    is CrmTasksIntent.ToggleDone -> {
        val moved = intent.task.copy(isDone = !intent.task.isDone)
        val active = state.activeTasks.filterNot { it.id == moved.id }
        val done = state.doneTasks.filterNot { it.id == moved.id }
        if (moved.isDone) state.copy(activeTasks = active, doneTasks = done + moved).withGroups()
        else state.copy(activeTasks = active + moved, doneTasks = done).withGroups()
    }

    CrmTasksIntent.OpenSheet -> state.copy(showSheet = true)
    CrmTasksIntent.CloseSheet -> state.copy(showSheet = false)
    is CrmTasksIntent.CreateTask -> state.copy(showSheet = false)

    is CrmTasksIntent.RequestDelete -> state.copy(taskToDelete = intent.task)
    CrmTasksIntent.DismissDelete -> state.copy(taskToDelete = null)
    CrmTasksIntent.ConfirmDelete -> {
        val id = state.taskToDelete?.id
        state.copy(
            taskToDelete = null,
            activeTasks = state.activeTasks.filterNot { it.id == id },
            doneTasks = state.doneTasks.filterNot { it.id == id }
        ).withGroups()
    }
}

private fun CrmTasksState.withGroups(): CrmTasksState = copy(
    groups = crmGroupTasks(
        tasks = if (tab == CrmTasksTab.ACTIVE) activeTasks else doneTasks,
        isDoneTab = tab == CrmTasksTab.DONE,
        today = today,
        clientNames = clientNames
    )
)

/**
 * Группирует задачи по сроку. Активные идут по возрастанию срока: «Просрочено», «Сегодня»,
 * «Завтра», затем по датам. Выполненные — по датам, свежие сверху.
 */
fun crmGroupTasks(
    tasks: List<DomainCrmTask>,
    isDoneTab: Boolean,
    today: String,
    clientNames: Map<Long, String>
): List<CrmTaskGroup> {
    val tomorrow = parseIsoDate(today)?.plusDays(1)?.toString()
    // ISO-даты сравниваются как строки
    val sorted = if (isDoneTab) tasks.sortedWith(compareByDescending<DomainCrmTask> { it.dueDate }.thenBy { it.id })
    else tasks.sortedWith(compareBy<DomainCrmTask> { it.dueDate }.thenBy { it.id })

    val groups = LinkedHashMap<Pair<CrmTaskGroupKind, String>, MutableList<CrmTaskItem>>()
    sorted.forEach { task ->
        val key = when {
            isDoneTab -> CrmTaskGroupKind.DATE to task.dueDate
            task.dueDate < today -> CrmTaskGroupKind.OVERDUE to ""
            task.dueDate == today -> CrmTaskGroupKind.TODAY to ""
            task.dueDate == tomorrow -> CrmTaskGroupKind.TOMORROW to ""
            else -> CrmTaskGroupKind.DATE to task.dueDate
        }
        groups.getOrPut(key) { mutableListOf() }
            .add(CrmTaskItem(task, task.clientId?.let { clientNames[it] }))
    }
    return groups.map { (key, items) -> CrmTaskGroup(key.first, key.second, items) }
}
