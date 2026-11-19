package com.zaroslikov.todo.ui.smart

import com.zaroslikov.todo.domain.model.DomainTodoProjectTask
import com.zaroslikov.todo.domain.model.TodoSmartList
import com.zaroslikov.todo.domain.model.TodoSmartSection
import com.zaroslikov.todo.domain.model.isDone
import java.time.LocalDateTime

data class TodoSmartState(
    val kind: TodoSmartList,
    val isLoading: Boolean = true,
    /** Все задачи и подзадачи проекта, без фильтров. */
    val tasks: List<DomainTodoProjectTask> = emptyList(),
    val now: LocalDateTime = LocalDateTime.now(),
    /** Оптимистичные отметки: id → завершена ли, пока БД не вернула то же самое. */
    val doneOverrides: Map<Long, Boolean> = emptyMap(),
    /** Только что отмеченные, которые уже не подходят виду: видны ~1,5 с, затем исчезают. */
    val lingering: Set<Long> = emptySet(),
    val newTask: TodoSmartTaskDraft? = null,
    /** Готовые к показу секции. */
    val sections: List<TodoSmartSection> = emptyList()
) {
    /** Новая задача создаётся только из «Сегодня» — со сроком сегодня. */
    val canAddTask: Boolean get() = kind == TodoSmartList.TODAY

    fun isDone(task: DomainTodoProjectTask): Boolean = doneOverrides[task.id] ?: task.item.isDone
}

data class TodoSmartTaskDraft(
    val title: String = "",
    val note: String = ""
)
