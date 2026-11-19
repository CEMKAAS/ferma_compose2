package com.zaroslikov.todo.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

// Умные списки главного экрана, как в «Напоминаниях»: «Сегодня», «Запланировано», «Все», «Завершено».
// Считаются задачи И подзадачи — каждая строка умного списка отдельное напоминание, поэтому
// число на плитке всегда равно числу строк на её экране. Отметка задачи передаётся функцией
// [isDone]: экран подставляет оптимистичную отметку, пока БД не вернула то же самое

/** Вид умного списка; [code] — стабильный код в маршруте `todo_smart/{itemIdPT}/{kind}`. */
enum class TodoSmartList(val code: String) {
    TODAY("today"),
    SCHEDULED("scheduled"),
    ALL("all"),
    COMPLETED("completed");

    companion object {
        fun fromCode(code: String?): TodoSmartList? = entries.firstOrNull { it.code == code }
    }
}

private val defaultIsDone: (DomainTodoProjectTask) -> Boolean = { it.item.isDone }

/**
 * Попадает ли задача в умный список: «Сегодня» — незавершённые с сегодняшней датой срока,
 * «Запланировано» — незавершённые с любым сроком, «Все» — незавершённые,
 * «Завершено» — завершённые.
 */
fun DomainTodoProjectTask.inSmartList(
    kind: TodoSmartList,
    now: LocalDateTime,
    done: Boolean = item.isDone
): Boolean = when (kind) {
    TodoSmartList.TODAY -> !done && parseTodoDate(item.task.dueDate) == now.toLocalDate()
    TodoSmartList.SCHEDULED -> !done && parseTodoDate(item.task.dueDate) != null
    TodoSmartList.ALL -> !done
    TodoSmartList.COMPLETED -> done
}

/** Число на плитке умного списка — столько же строк покажет его экран. */
fun todoSmartCount(
    tasks: List<DomainTodoProjectTask>,
    kind: TodoSmartList,
    now: LocalDateTime,
    isDone: (DomainTodoProjectTask) -> Boolean = defaultIsDone
): Int = tasks.count { it.inSmartList(kind, now, isDone(it)) }

/** Незавершённые задачи и подзадачи по спискам — число справа в «Мои списки». */
fun todoIncompleteCountByList(
    tasks: List<DomainTodoProjectTask>,
    isDone: (DomainTodoProjectTask) -> Boolean = defaultIsDone
): Map<Long, Int> = tasks.filterNot(isDone).groupingBy { it.listId }.eachCount()

/** Заголовок секции умного списка или поиска. */
sealed interface TodoSmartHeader {
    data object Overdue : TodoSmartHeader
    data object Today : TodoSmartHeader
    data object Tomorrow : TodoSmartHeader

    /** Конкретный день, `yyyy-MM-dd`. */
    data class Day(val date: String) : TodoSmartHeader

    data class ListTitle(val listId: Long, val title: String, val colorIndex: Int) : TodoSmartHeader
}

/** Секция строк; [header] = null — плоский список без заголовка («Сегодня»). */
data class TodoSmartSection(
    val header: TodoSmartHeader?,
    val tasks: List<DomainTodoProjectTask>
) {
    /** Стабильный ключ секции для LazyColumn. */
    val key: String
        get() = when (header) {
            null -> "flat"
            TodoSmartHeader.Overdue -> "overdue"
            TodoSmartHeader.Today -> "today"
            TodoSmartHeader.Tomorrow -> "tomorrow"
            is TodoSmartHeader.Day -> "day_${header.date}"
            is TodoSmartHeader.ListTitle -> "list_${header.listId}"
        }
}

/**
 * Секции экрана умного списка. Задача видна, если попадает в список с отметкой [isDone],
 * или [keep] её удерживает (только что отмеченная — ещё ~1,5 с на экране, как в iOS).
 * - «Сегодня»: плоский список задач с сегодняшней датой срока, упорядоченный по времени;
 * - «Запланировано»: «Просрочено», «Сегодня», «Завтра», затем по дням, внутри — по времени;
 * - «Все»: по спискам, в порядке списка «задача → её подзадачи»;
 * - «Завершено»: по спискам, сначала недавно завершённые.
 * Пустые секции не попадают.
 */
fun todoSmartSections(
    tasks: List<DomainTodoProjectTask>,
    kind: TodoSmartList,
    now: LocalDateTime,
    isDone: (DomainTodoProjectTask) -> Boolean = defaultIsDone,
    keep: (DomainTodoProjectTask) -> Boolean = { false }
): List<TodoSmartSection> {
    val shown = tasks.filter {
        it.inSmartList(kind, now, isDone(it)) ||
            (keep(it) && (kind != TodoSmartList.TODAY || it.inSmartList(kind, now, done = false)))
    }
    if (shown.isEmpty()) return emptyList()
    return when (kind) {
        TodoSmartList.TODAY -> listOf(TodoSmartSection(header = null, tasks = shown.sortedWith(byDue(tasks))))

        TodoSmartList.SCHEDULED -> {
            val today = now.toLocalDate()
            shown.sortedWith(byDue(tasks))
                .groupBy { dayHeader(it, today, now) }
                .entries
                .sortedBy { (header, _) -> header.order(today) }
                .map { (header, list) -> TodoSmartSection(header, list) }
        }

        TodoSmartList.ALL -> groupByList(shown, treeOrder(shown))

        TodoSmartList.COMPLETED -> groupByList(
            shown,
            // Недавно завершённые сверху; без даты завершения — в конце, по порядку из БД
            shown.sortedWith(compareByDescending<DomainTodoProjectTask> { it.item.task.completedAt.orEmpty() })
        )
    }
}

/** Срок задачи прошёл — без учёта отметки: только что отмеченная просрочка остаётся на месте. */
private fun DomainTodoProjectTask.isDueOverdue(now: LocalDateTime): Boolean =
    isTodoOverdue(item.task.dueDate, item.task.dueTime, isDone = false, now = now)

/** По дате, затем по времени (задачи на весь день — перед задачами со временем), затем порядок из БД. */
private fun byDue(all: List<DomainTodoProjectTask>): Comparator<DomainTodoProjectTask> {
    val index = all.withIndex().associate { (i, task) -> task.id to i }
    return compareBy<DomainTodoProjectTask>(
        { it.item.task.dueDate.orEmpty() },
        { it.item.task.dueTime.orEmpty() },
        { index[it.id] ?: Int.MAX_VALUE }
    )
}

private fun dayHeader(task: DomainTodoProjectTask, today: LocalDate, now: LocalDateTime): TodoSmartHeader {
    val date = parseTodoDate(task.item.task.dueDate)
    return when {
        date == null -> TodoSmartHeader.Day(task.item.task.dueDate.orEmpty())
        task.isDueOverdue(now) -> TodoSmartHeader.Overdue
        date == today -> TodoSmartHeader.Today
        date == today.plusDays(1) -> TodoSmartHeader.Tomorrow
        else -> TodoSmartHeader.Day(date.toString())
    }
}

/** Порядок секций «Запланировано»; дни после «Завтра» — по дате (строки ISO сравниваются как даты). */
private fun TodoSmartHeader.order(today: LocalDate): String = when (this) {
    TodoSmartHeader.Overdue -> "0"
    TodoSmartHeader.Today -> "1"
    TodoSmartHeader.Tomorrow -> "2"
    is TodoSmartHeader.Day -> "3$date"
    is TodoSmartHeader.ListTitle -> "4"
}

/**
 * Секции по спискам в порядке первого появления в [tasks] (из БД — по позиции списков);
 * строки внутри — в порядке [ordered].
 */
private fun groupByList(
    tasks: List<DomainTodoProjectTask>,
    ordered: List<DomainTodoProjectTask>
): List<TodoSmartSection> {
    val listOrder = tasks.map { it.listId }.distinct()
    val byList = ordered.groupBy { it.listId }
    return listOrder.mapNotNull { listId ->
        val rows = byList[listId] ?: return@mapNotNull null
        val first = rows.first()
        TodoSmartSection(TodoSmartHeader.ListTitle(listId, first.listTitle, first.listColorIndex), rows)
    }
}

/** Порядок «задача → её подзадачи» внутри каждого списка (`buildTodoTaskTree`), списки — как в [tasks]. */
private fun treeOrder(tasks: List<DomainTodoProjectTask>): List<DomainTodoProjectTask> {
    val byId = tasks.associateBy { it.id }
    return tasks.groupBy { it.listId }.values.flatMap { inList ->
        buildTodoTaskTree(inList.map { it.item }).flatMap { node ->
            listOf(node.item) + node.subtasks
        }.mapNotNull { byId[it.task.id] }
    }
}
