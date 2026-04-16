package com.zaroslikov.fermacompose2.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zaroslikov.fermacompose2.ui.add.AddProject
import com.zaroslikov.fermacompose2.ui.add.ProjectAddDestination
import com.zaroslikov.fermacompose2.ui.incubator_project.AddIncubator.AddIncubatorDestination
import com.zaroslikov.fermacompose2.ui.incubator_project.AddIncubator.AddIncubatorScreen
import com.zaroslikov.fermacompose2.ui.incubator_project.main_screen.MainIncubatorDestination
import com.zaroslikov.fermacompose2.ui.incubator_project.main_screen.MainIncubatorScreen
import com.zaroslikov.fermacompose2.ui.project.mainScreen.MainProjectScreen
import com.zaroslikov.fermacompose2.ui.project.mainScreen.MainProjectsDestination
import com.zaroslikov.fermacompose2.ui.start.first.FirstDestination
import com.zaroslikov.fermacompose2.ui.start.first.FirstScreen
import com.zaroslikov.fermacompose2.ui.start.aboutApp.AboutAppDestination
import com.zaroslikov.fermacompose2.ui.start.aboutApp.AboutAppScreen
import com.zaroslikov.fermacompose2.ui.start.profile.ProfileDestination
import com.zaroslikov.fermacompose2.ui.start.profile.ProfileScreen
import com.zaroslikov.fermacompose2.ui.start.settings.SettingsDestination
import com.zaroslikov.fermacompose2.ui.start.settings.SettingsScreen
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseEditDestination
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseEditScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    action: String?,
    projectId: Long
) {

    val startDestination = when {
        action == "OPEN_BOOKMARK_DETAIL" && projectId != -1L -> {
            "${MainIncubatorDestination.route}/${projectId}"
        }

        action == "OPEN_PROJECT_DETAIL" && projectId != -1L -> {
            "${MainProjectsDestination.route}/${projectId}"
        }

        else -> FirstDestination.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = FirstDestination.route) {
            FirstScreen(
                navigateToProfile = { navController.navigate(ProfileDestination.route) },
                navigateToAboutApp = { navController.navigate(AboutAppDestination.route) },
                navigateToSettings = { navController.navigate(SettingsDestination.route) },
                navigateToItemProject = { navController.navigate("${MainProjectsDestination.route}/${it}") },
                navigateToItemIncubator = { navController.navigate("${MainIncubatorDestination.route}/${it}") },
                navigateToProject = { navController.navigate("${WarehouseEditDestination.route}/${it}") },
                navigateToIncubator = { navController.navigate("${AddIncubatorDestination.route}/${it}") },
            )
        }
        composable(
            route = MainIncubatorDestination.routeWithArgs,
            arguments = listOf(navArgument(MainIncubatorDestination.itemIdArg) {
                type = NavType.LongType
            })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments!!.getLong(MainIncubatorDestination.itemIdArg)
            MainIncubatorScreen(navController, itemId)
        }
        composable(
            route = MainProjectsDestination.routeWithArgs,
            arguments = listOf(navArgument(MainProjectsDestination.itemIdArg) {
                type = NavType.LongType
            })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments!!.getLong(MainProjectsDestination.itemIdArg)
            MainProjectScreen(navController, itemId)
        }


        composable(route = ProjectAddDestination.route) {
            AddProject(
                navigateBack = { navController.popBackStack() },
                navigateToStart = { navController.navigate(FirstDestination.route) },
            )
        }

        composable(
            route = WarehouseEditDestination.routeWithArgs,
            arguments = listOf(navArgument(WarehouseEditDestination.itemIdArg) {
                type = NavType.LongType
            })
        ) {
            WarehouseEditScreen(
                navigateBack = navController::popBackStack
            )
        }

        composable(route = ProfileDestination.route) {
            ProfileScreen(onNavigateBack = navController::popBackStack)
        }

        composable(route = AboutAppDestination.route) {
            AboutAppScreen(
                onNavigateBack = navController::popBackStack
            )
        }

        composable(route = SettingsDestination.route) {
            SettingsScreen(
                onNavigateBack = navController::popBackStack
            )
        }

        composable(
            route = AddIncubatorDestination.routeWithArgs,
            arguments = listOf(navArgument(AddIncubatorDestination.itemIdArg) {
                type = NavType.LongType
            })
        ) {
            AddIncubatorScreen(
                navigateBack = navController::navigateUp
            )
        }
    }
}

