package com.zaroslikov.todo.domain.repository

import com.zaroslikov.todo.domain.model.DomainTodoAssignee
import com.zaroslikov.todo.domain.model.DomainTodoProjectTask
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoAssigneeProfile
import kotlinx.coroutines.flow.Flow

interface TodoTaskRepository {

    /** Сохранённые имена исполнителей только указанного проекта, включая не назначенных задачам. */
    fun getProjectAssignees(idPT: Long): Flow<List<String>>

    fun getProjectAssigneeProfiles(idPT: Long): Flow<List<DomainTodoAssignee>>

    /** Сохраняет анкету. При добавлении без имени использует свободное название на основе [fallbackName]. */
    suspend fun saveProjectAssigneeProfile(
        idPT: Long,
        originalName: String?,
        profile: TodoAssigneeProfile,
        fallbackName: String
    ): Boolean

    /** Удаляет исполнителя из справочника и снимает его назначения в проекте. */
    suspend fun deleteProjectAssignee(idPT: Long, name: String)

    // upsert, setStatus и toggleDone при переходе в завершённый статус атомарно завершают
    // всю цепочку связей обоих типов и направлений. Связанные задачи получают «Завершённые»;
    // уже завершённые сохраняют статус и дату. Повторное открытие меняет только выбранную задачу.

    /** Задачи верхнего уровня списка по позиции. */
    fun getTasks(listId: Long): Flow<List<DomainTodoTaskItem>>

    /**
     * Все задачи списка вместе с подзадачами (со счётчиками и тегами, как [getTasks]) одним
     * потоком, по позиции. Дерево «задача → подзадачи» собирает `buildTodoTaskTree`.
     */
    fun getAllTasks(listId: Long): Flow<List<DomainTodoTaskItem>>

    fun getSubtasks(parentId: Long): Flow<List<DomainTodoTaskItem>>

    fun getTask(id: Long): Flow<DomainTodoTaskItem?>

    suspend fun getTaskTagIds(id: Long): Set<Long>

    /**
     * В одной транзакции сохраняет задачу и заменяет её теги на [tagIds]. `id = 0` — новая
     * задача в конец своего уровня; `completedAt` выставляется по итоговому статусу. Статус
     * не из проекта задачи заменяется на «В планах» проекта, из [tagIds] привязываются только
     * существующие теги проекта. Подзадача получает список родителя; подзадачу подзадачи
     * создать нельзя. Подсписок новой задачи проверяется (только подсписок её списка, у подзадачи —
     * никакого), у существующей не меняется — для этого [setSection]. Возвращает id задачи.
     */
    suspend fun upsert(task: DomainTodoTask, tagIds: Set<Long>): Long

    /**
     * Все задачи и подзадачи проекта одним потоком: со счётчиками и тегами, как [getAllTasks],
     * плюс название и цвет списка и название родителя. По позиции списка, затем задачи.
     * Отбор и группировку для плиток, умных списков и поиска делают чистые функции `TodoSmartLists.kt`.
     */
    fun getProjectTasks(idPT: Long): Flow<List<DomainTodoProjectTask>>

    /**
     * Быстрое добавление: задача верхнего уровня с названием [title] (обрезается), статусом
     * «В планах» проекта, в конец списка; [dueDate] (`yyyy-MM-dd`) — срок без времени, например
     * «сегодня» из умного списка; [sectionId] — подсписок этого же списка (чужой или удалённый
     * подсписок — задача без подсписка). Пустое название — ошибка. Возвращает id.
     */
    suspend fun quickAdd(listId: Long, title: String, dueDate: String? = null, sectionId: Long? = null, note: String = ""): Long

    /** Добавляет задачу туда, где последней создали задачу проекта. При отсутствии списков создаёт первый. */
    suspend fun quickAddToProject(idPT: Long, title: String, dueDate: String, defaultListTitle: String, note: String = ""): Long

    /** Правка текста без изменения статуса, тегов, порядка и остальных настроек задачи. */
    suspend fun updateText(id: Long, title: String, note: String)

    /** Заменяет исполнителей задачи или подзадачи, обрезая имена и убирая пустые и повторы. */
    suspend fun setAssignees(taskId: Long, names: List<String>)

    /**
     * Переносит задачу верхнего уровня в подсписок [sectionId] её списка (null — «без подсписка»)
     * в конец ([moveTask]); если задача уже в нём — ничего. Подзадачи и подсписки чужого списка игнорируются.
     */
    suspend fun setSection(taskId: Long, sectionId: Long?)

    /** Переносит задачу с подзадачами в конец другого списка того же проекта, без подсписка.
     * Подзадача отдельно, отсутствующий список и список другого проекта игнорируются. */
    suspend fun setList(taskId: Long, listId: Long)

    /**
     * Перенос в одной транзакции: задача верхнего уровня [taskId] встаёт в подсписок [sectionId]
     * своего списка (null — «без подсписка») на место [index] среди его задач верхнего уровня
     * (без самой задачи; ограничивается). Позиции задач верхнего уровня целевого и исходного
     * подсписков перенумеровываются подряд с нуля — как `withTodoTaskMoved` на экране.
     * Подзадачи, подсписки чужого списка и удалённые подсписки игнорируются.
     */
    suspend fun moveTask(taskId: Long, sectionId: Long?, index: Int)

    /**
     * Порядок подзадач [parentId]: позиции по порядку [orderedIds] подряд с нуля; подзадачи,
     * которых нет в [orderedIds], идут следом в прежнем порядке, чужие id игнорируются.
     */
    suspend fun reorderSubtasks(parentId: Long, orderedIds: List<Long>)

    /** Переименование на месте; пустое после обрезки название игнорируется. */
    suspend fun rename(id: Long, title: String)

    /**
     * «Очистить» в списке: в одной транзакции удаляет завершённые задачи верхнего уровня
     * (с их подзадачами каскадом) и завершённые подзадачи незавершённых задач.
     */
    suspend fun deleteCompleted(listId: Long)

    /** Статус с `isDone` выставляет `completedAt`, остальные его сбрасывают. Статус чужого проекта игнорируется. */
    suspend fun setStatus(taskId: Long, statusId: Long)

    /** Чекбокс: незавершённая → «Завершённые», завершённая → «В планах» проекта задачи. */
    suspend fun toggleDone(taskId: Long)

    /** Явная отметка из UI: повторное завершение уже завершённой задачи ничего не меняет. */
    suspend fun setDone(taskId: Long, done: Boolean)

    /** Меняет выполнение задачи проекта [idPT]. Снятие выполнения возвращает её в «В планах». */
    suspend fun setTaskCompleted(idPT: Long, taskId: Long, completed: Boolean)

    /** Подзадачи, теги, связи и комментарии удаляются каскадом. */
    suspend fun delete(id: Long)

    /** Удаление со снимком всех данных для одноразовой атомарной отмены. */
    suspend fun deleteWithUndo(id: Long): TodoTaskDeletion?

    /** Задачи проекта из всех списков, название содержит [query] без учёта регистра. */
    fun searchProjectTasks(idPT: Long, query: String): Flow<List<DomainTodoTask>>
}
