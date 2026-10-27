package com.zaroslikov.assistant.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zaroslikov.assistant.ui.chat.AssistantChatScreen

/** Экран чата помощника. Аргумент — id проекта, читается во ViewModel из SavedStateHandle. */
object AssistantDestination {
    const val route = "assistant"
    const val itemIdPT = "itemIdPT"
    const val routeWithArgs = "assistant/{itemIdPT}"

    fun routeFor(idPT: Long): String = "$route/$idPT"
}

/** Подключается внутрь `ProjectNavHost` приложения, вход — `assistant/{itemIdPT}`. */
fun NavGraphBuilder.assistantNavGraph(navController: NavHostController) {
    composable(
        route = AssistantDestination.routeWithArgs,
        arguments = listOf(navArgument(AssistantDestination.itemIdPT) { type = NavType.LongType })
    ) {
        AssistantChatScreen(navigateBack = navController::popBackStack)
    }
}
