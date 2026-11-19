package com.zaroslikov.todo.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zaroslikov.todo.domain.model.TodoSmartList
import com.zaroslikov.todo.ui.assignees.TodoAssigneesScreen
import com.zaroslikov.todo.ui.list.TodoListScreen
import com.zaroslikov.todo.ui.lists.TodoListsScreen
import com.zaroslikov.todo.ui.smart.TodoSmartScreen
import com.zaroslikov.todo.ui.statuses.TodoStatusesScreen
import com.zaroslikov.todo.ui.tags.TodoTagsScreen

/**
 * Экраны задач. Задача — не отдельный экран, а шторка «Подробно» поверх списка или умного
 * списка (`ui/task_details`). «Назад» срабатывает только у экрана в RESUMED ([dropUnlessResumed]):
 * двойной тап или закрытие шторки во время анимации перехода не снимает лишний экран.
 * Подключается внутрь `ProjectNavHost` приложения, вход — `todo/{itemIdPT}`.
 * Все аргументы — path-параметры типа Long (кроме строкового `kind` умного списка),
 * читаются во ViewModel из SavedStateHandle.
 */
fun NavGraphBuilder.todoNavGraph(navController: NavHostController) {
    // Переход только из экрана в RESUMED — как dropUnlessResumed, но с аргументом: двойной тап
    // во время анимации не открывает экран дважды
    fun <T> NavBackStackEntry.ifResumed(block: (T) -> Unit): (T) -> Unit = { value ->
        if (lifecycle.currentState == Lifecycle.State.RESUMED) block(value)
    }

    composable(
        route = TodoDestination.routeWithArgs,
        arguments = listOf(navArgument(TodoDestination.itemIdPT) { type = NavType.LongType })
    ) { entry ->
        val idPT = entry.arguments?.getLong(TodoDestination.itemIdPT) ?: 0L
        TodoDestinationContent {
            TodoListsScreen(
                navigateBack = dropUnlessResumed { navController.popBackStack() },
                navigateToList = entry.ifResumed { listId: Long ->
                    navController.navigate("${TodoListDestination.route}/$listId") { launchSingleTop = true }
                },
                navigateToSmart = entry.ifResumed { kind: TodoSmartList ->
                    navController.navigate("${TodoSmartDestination.route}/$idPT/${kind.code}") { launchSingleTop = true }
                },
                navigateToStatuses = dropUnlessResumed {
                    navController.navigate("${TodoStatusesDestination.route}/$idPT")
                },
                navigateToTags = dropUnlessResumed {
                    navController.navigate("${TodoTagsDestination.route}/$idPT")
                },
                navigateToAssignees = dropUnlessResumed {
                    navController.navigate("${TodoAssigneesDestination.route}/$idPT") { launchSingleTop = true }
                }
            )
        }
    }

    composable(
        route = TodoSmartDestination.routeWithArgs,
        arguments = listOf(
            navArgument(TodoSmartDestination.itemIdPT) { type = NavType.LongType },
            navArgument(TodoSmartDestination.kind) { type = NavType.StringType }
        )
    ) {
        TodoDestinationContent {
            TodoSmartScreen(navigateBack = dropUnlessResumed { navController.popBackStack() })
        }
    }

    composable(
        route = TodoListDestination.routeWithArgs,
        arguments = listOf(navArgument(TodoListDestination.listId) { type = NavType.LongType })
    ) {
        TodoDestinationContent {
            TodoListScreen(navigateBack = dropUnlessResumed { navController.popBackStack() })
        }
    }

    composable(
        route = TodoStatusesDestination.routeWithArgs,
        arguments = listOf(navArgument(TodoStatusesDestination.itemIdPT) { type = NavType.LongType })
    ) {
        TodoDestinationContent {
            TodoStatusesScreen(navigateBack = dropUnlessResumed { navController.popBackStack() })
        }
    }

    composable(
        route = TodoTagsDestination.routeWithArgs,
        arguments = listOf(navArgument(TodoTagsDestination.itemIdPT) { type = NavType.LongType })
    ) {
        TodoDestinationContent {
            TodoTagsScreen(navigateBack = dropUnlessResumed { navController.popBackStack() })
        }
    }

    composable(
        route = TodoAssigneesDestination.routeWithArgs,
        arguments = listOf(navArgument(TodoAssigneesDestination.itemIdPT) { type = NavType.LongType })
    ) {
        TodoDestinationContent {
            TodoAssigneesScreen(navigateBack = dropUnlessResumed { navController.popBackStack() })
        }
    }
}

@Composable
private fun TodoDestinationContent(content: @Composable () -> Unit) {
    // Корневой экран уже отступил от системной панели под нижней навигацией проекта.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
    ) {
        content()
    }
}
