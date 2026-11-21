package com.zaroslikov.calendar.ui

import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

internal const val calendarProjectArg = "itemIdPT"

fun calendarRoute(projectId: Long): String = "calendar/$projectId"

fun NavGraphBuilder.calendarNavGraph(navController: NavHostController) {
    composable(
        route = "calendar/{$calendarProjectArg}",
        arguments = listOf(navArgument(calendarProjectArg) { type = NavType.LongType })
    ) {
        CalendarScreen(navigateBack = dropUnlessResumed { navController.popBackStack() })
    }
}
