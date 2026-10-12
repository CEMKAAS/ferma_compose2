package com.zaroslikov.crm.ui.tasks

import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmTask
import com.zaroslikov.crm.util.today

/** Задача с именем привязанного клиента (если он есть). */
data class CrmTaskItem(
    val task: DomainCrmTask,
    val clientName: String? = null
)

enum class CrmTasksTab { ACTIVE, DONE }

/** Вид группы: просроченные, сегодня, завтра или конкретная дата. */
enum class CrmTaskGroupKind { OVERDUE, TODAY, TOMORROW, DATE }

/** Группа задач; [date] — `yyyy-MM-dd` для [CrmTaskGroupKind.DATE], иначе пустая строка. */
data class CrmTaskGroup(
    val kind: CrmTaskGroupKind,
    val date: String,
    val items: List<CrmTaskItem>
)

data class CrmTasksState(
    val isLoading: Boolean = true,
    val today: String = today(),
    val tab: CrmTasksTab = CrmTasksTab.ACTIVE,
    val activeTasks: List<DomainCrmTask> = emptyList(),
    val doneTasks: List<DomainCrmTask> = emptyList(),
    /** Неархивные клиенты — для выбора в листе создания задачи. */
    val clients: List<DomainCrmClient> = emptyList(),
    /** Имена всех клиентов проекта, включая архивных, — задача могла остаться за ними. */
    val clientNames: Map<Long, String> = emptyMap(),
    val groups: List<CrmTaskGroup> = emptyList(),
    val showSheet: Boolean = false,
    val taskToDelete: DomainCrmTask? = null
)
