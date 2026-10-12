package com.zaroslikov.crm.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zaroslikov.crm.ui.client_card.CrmClientCardScreen
import com.zaroslikov.crm.ui.client_edit.CrmClientEditScreen
import com.zaroslikov.crm.ui.clients.CrmClientsScreen
import com.zaroslikov.crm.ui.dashboard.CrmDashboardScreen
import com.zaroslikov.crm.ui.tasks.CrmTasksScreen

/** Новый клиент: редактор открывается с этим id. */
const val CRM_NEW_CLIENT_ID = -1L

/**
 * Экраны CRM. Подключается внутрь `ProjectNavHost` приложения, вход — `crm/{itemIdPT}`.
 * Все аргументы — path-параметры типа Long, читаются во ViewModel из SavedStateHandle.
 */
fun NavGraphBuilder.crmNavGraph(navController: NavHostController) {
    composable(
        route = CrmDestination.routeWithArgs,
        arguments = listOf(navArgument(CrmDestination.itemIdPT) { type = NavType.LongType })
    ) { entry ->
        val idPT = entry.arguments?.getLong(CrmDestination.itemIdPT) ?: 0L
        CrmDashboardScreen(
            navigateBack = navController::popBackStack,
            navigateToClients = {
                navController.navigate("${CrmClientsDestination.route}/$idPT")
            },
            navigateToClient = { clientId ->
                navController.navigate("${CrmClientCardDestination.route}/$clientId")
            },
            navigateToClientEdit = { clientId ->
                navController.navigate("${CrmClientEditDestination.route}/$idPT/$clientId")
            },
            navigateToTasks = {
                navController.navigate("${CrmTasksDestination.route}/$idPT")
            }
        )
    }

    composable(
        route = CrmClientsDestination.routeWithArgs,
        arguments = listOf(navArgument(CrmClientsDestination.itemIdPT) { type = NavType.LongType })
    ) { entry ->
        val idPT = entry.arguments?.getLong(CrmClientsDestination.itemIdPT) ?: 0L
        CrmClientsScreen(
            navigateBack = navController::popBackStack,
            navigateToClient = { clientId ->
                navController.navigate("${CrmClientCardDestination.route}/$clientId")
            },
            navigateToClientEdit = { clientId ->
                navController.navigate("${CrmClientEditDestination.route}/$idPT/$clientId")
            }
        )
    }

    composable(
        route = CrmClientCardDestination.routeWithArgs,
        arguments = listOf(navArgument(CrmClientCardDestination.clientId) { type = NavType.LongType })
    ) {
        CrmClientCardScreen(
            navigateBack = navController::popBackStack,
            navigateToClientEdit = { clientId ->
                // У карточки нет idPT в роуте: редактор существующего клиента берёт idPT из самого клиента
                navController.navigate("${CrmClientEditDestination.route}/0/$clientId")
            }
        )
    }

    composable(
        route = CrmClientEditDestination.routeWithArgs,
        arguments = listOf(
            navArgument(CrmClientEditDestination.itemIdPT) { type = NavType.LongType },
            navArgument(CrmClientEditDestination.clientId) { type = NavType.LongType }
        )
    ) {
        CrmClientEditScreen(navigateBack = navController::popBackStack)
    }

    composable(
        route = CrmTasksDestination.routeWithArgs,
        arguments = listOf(navArgument(CrmTasksDestination.itemIdPT) { type = NavType.LongType })
    ) {
        CrmTasksScreen(
            navigateBack = navController::popBackStack,
            navigateToClient = { clientId ->
                navController.navigate("${CrmClientCardDestination.route}/$clientId")
            }
        )
    }
}
