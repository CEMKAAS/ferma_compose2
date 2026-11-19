package com.zaroslikov.todo.ui.list

import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoListIcon
import com.zaroslikov.todo.domain.model.TodoPriority
import java.time.LocalDateTime

sealed interface TodoListIntent {
    /** [tasks] — все задачи списка вместе с подзадачами. */
    data class DataLoaded(
        val list: DomainTodoList,
        val tasks: List<DomainTodoTaskItem>,
        val tags: List<DomainTodoTag>,
        val sections: List<DomainTodoSection> = emptyList(),
        val searchLabels: Map<Int, String> = emptyMap()
    ) : TodoListIntent

    // Отметка выполнения
    data class ToggleDone(val taskId: Long) : TodoListIntent
    /** Истекла пауза после отметки — строка уходит из списка скрытых завершённых. */
    data class HideLingering(val taskId: Long) : TodoListIntent
    /** Запись отметки в БД не удалась — откат. */
    data class ToggleDoneFailed(val taskId: Long) : TodoListIntent
    data object ToggleShowCompleted : TodoListIntent
    data class ToggleCollapse(val taskId: Long) : TodoListIntent

    // Ввод прямо в списке
    /** Новая задача без подсписка. */
    data object StartNew : TodoListIntent
    data class StartNewSubtask(val parentId: Long) : TodoListIntent
    /** Пунктирная строка подсписка или карточка столбца: новая задача в конец подсписка (свёрнутый разворачивается). */
    data class StartNewInSection(val sectionId: Long) : TodoListIntent
    data class StartRename(val taskId: Long) : TodoListIntent
    data class EditorTextChanged(val text: String) : TodoListIntent
    data class EditorNoteChanged(val note: String) : TodoListIntent
    /** Enter на клавиатуре: новая задача создаётся, поле остаётся для следующей. */
    data object EditorSubmit : TodoListIntent
    /** Потеря фокуса, тап вне строки, «назад»; [taskId] — чья строка закрывается (null — новая). */
    data class EditorFinish(val taskId: Long?) : TodoListIntent
    /** «ⓘ»: сохранить введённое и открыть карточку задачи. */
    data object EditorInfo : TodoListIntent
    /** Откат только той правки, которая не записалась; более новую не затрагивает. */
    data class EditorSaveFailed(val taskId: Long, val text: TodoTaskText) : TodoListIntent
    data class EditorSaveSucceeded(val taskId: Long, val text: TodoTaskText) : TodoListIntent

    // Удаление задач
    data class RequestDeleteTask(val taskId: Long) : TodoListIntent
    data object ConfirmDeleteTask : TodoListIntent
    data object DismissDeleteTask : TodoListIntent
    data class DeleteTaskFailed(val taskId: Long) : TodoListIntent
    data object RequestClearCompleted : TodoListIntent
    data object ConfirmClearCompleted : TodoListIntent
    data object DismissClearCompleted : TodoListIntent

    // Поиск и фильтры
    data class Search(val query: String) : TodoListIntent
    data class SearchMetadataLoaded(val text: Map<Long, List<String>>, val now: LocalDateTime) : TodoListIntent
    data object OpenFilters : TodoListIntent
    data object CloseFilters : TodoListIntent
    /** null — все приоритеты; повторный выбор того же приоритета снимает фильтр. */
    data class SelectPriority(val priority: TodoPriority?) : TodoListIntent
    /** null — все теги; повторный выбор того же тега снимает фильтр. */
    data class SelectTag(val tagId: Long?) : TodoListIntent
    data object ResetFilters : TodoListIntent

    // Подсписки
    data class ToggleSectionCollapse(val sectionId: Long) : TodoListIntent
    data object OpenNewSection : TodoListIntent
    /** «Новый столбик»: название нового подсписка вводится на месте последнего столбца. */
    data object StartNewColumn : TodoListIntent
    /** Тап по названию подсписка (заголовок списка или столбца) или «Переименовать»: правка на месте. */
    data class StartRenameSection(val sectionId: Long) : TodoListIntent
    data class SectionTitleChanged(val title: String) : TodoListIntent
    /**
     * Enter, потеря фокуса или тап вне поля: непустое название сохраняется, пустое — отмена.
     * [startTask] — после создания нового подсписка открыть ввод первой задачи (не при «назад»).
     */
    data class SectionTitleFinish(val startTask: Boolean = true) : TodoListIntent
    /** Подсписок из «Нового столбика» записан — в нём сразу открывается ввод первой задачи. */
    data class ColumnCreated(val sectionId: Long) : TodoListIntent
    data class SectionFormTitleChanged(val title: String) : TodoListIntent
    data object SaveSectionForm : TodoListIntent
    data object CloseSectionForm : TodoListIntent
    data class RequestDeleteSection(val sectionId: Long) : TodoListIntent
    data object ConfirmDeleteSection : TodoListIntent
    data object DismissDeleteSection : TodoListIntent
    /** «Переместить в…»: [sectionId] = null — «Без подсписка». */
    data class MoveTask(val taskId: Long, val sectionId: Long?) : TodoListIntent

    // Перенос удержанием (оба вида). Индексы — среди видимых на экране элементов без переносимого
    /** Начался перенос: введённое сохраняется, строки ввода закрываются. */
    data object DragStarted : TodoListIntent
    /** Задача верхнего уровня — в подсписок [sectionId] (null — «без подсписка») на место [index]. */
    data class DropTask(val taskId: Long, val sectionId: Long?, val index: Int) : TodoListIntent
    /** Подзадача — на место [index] среди подзадач своего родителя. */
    data class DropSubtask(val taskId: Long, val index: Int) : TodoListIntent
    /** Подсписок — на место [index] среди подсписков. */
    data class DropSection(val sectionId: Long, val index: Int) : TodoListIntent
    /** Запись порядка не удалась — подсписки и позиции возвращаются к снимку до переноса. */
    data class ReorderFailed(
        val tasks: List<DomainTodoTaskItem>,
        val sections: List<DomainTodoSection>
    ) : TodoListIntent

    /** «Показать столбцами» / «Показать списком». */
    data object ToggleViewMode : TodoListIntent

    // Сам список
    data object OpenEditList : TodoListIntent
    data class FormTitleChanged(val title: String) : TodoListIntent
    data class FormColorChanged(val colorIndex: Int) : TodoListIntent
    data class FormIconChanged(val icon: TodoListIcon) : TodoListIntent
    data object CloseForm : TodoListIntent
    data object SaveForm : TodoListIntent

    data object RequestDeleteList : TodoListIntent
    data object DismissDeleteList : TodoListIntent
    data object ConfirmDeleteList : TodoListIntent
}
