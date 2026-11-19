package com.zaroslikov.todo.ui.navigation

// Все аргументы — path, NavType.LongType (кроме строкового `kind` умного списка),
// читаются во ViewModel из SavedStateHandle

/** Вход в модуль — списки задач проекта. */
object TodoDestination {
    const val route = "todo"
    const val itemIdPT = "itemIdPT"
    const val routeWithArgs = "todo/{itemIdPT}"
}

/** Умный список проекта; `kind` — строковый код `TodoSmartList` (today / scheduled / all / completed). */
object TodoSmartDestination {
    const val route = "todo_smart"
    const val itemIdPT = "itemIdPT"
    const val kind = "kind"
    const val routeWithArgs = "todo_smart/{itemIdPT}/{kind}"
}

object TodoListDestination {
    const val route = "todo_list"
    const val listId = "listId"
    const val routeWithArgs = "todo_list/{listId}"
}

object TodoStatusesDestination {
    const val route = "todo_statuses"
    const val itemIdPT = "itemIdPT"
    const val routeWithArgs = "todo_statuses/{itemIdPT}"
}

object TodoTagsDestination {
    const val route = "todo_tags"
    const val itemIdPT = "itemIdPT"
    const val routeWithArgs = "todo_tags/{itemIdPT}"
}

object TodoAssigneesDestination {
    const val route = "todo_assignees"
    const val itemIdPT = "itemIdPT"
    const val routeWithArgs = "todo_assignees/{itemIdPT}"
}
