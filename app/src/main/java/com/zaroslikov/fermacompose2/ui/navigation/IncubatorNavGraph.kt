package com.zaroslikov.fermacompose2.ui.navigation

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zaroslikov.fermacompose2.ui.incubator_project.AddIncubator.AddIncubatorDestination
import com.zaroslikov.fermacompose2.ui.incubator_project.AddIncubator.AddIncubatorScreen
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.main.BookmarkDestination
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.main.BookmarkScreen
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry.EntryBookmarkDestination
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry.EntryBookmarkMainScreen
import com.zaroslikov.fermacompose2.ui.incubator_project.finance.FinanceIncubatorScreen
import com.zaroslikov.fermacompose2.ui.incubator_project.finance.FinanceIncubatorDestination
import com.zaroslikov.fermacompose2.ui.incubator_project.journal.JournalDestination
import com.zaroslikov.fermacompose2.ui.incubator_project.journal.JournalScreen
import com.zaroslikov.fermacompose2.ui.start.first.FirstDestination


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncubatorNavHost(
    rootNavController: NavController,
    navController: NavHostController,
    itemPT: Long,
    modifier: Modifier = Modifier
) {
    Log.i("bookmark", "IncubatorNavHost itemPTNavHost: $itemPT ")
    NavHost(
        navController = navController,
        startDestination = "${BookmarkDestination.route}/${itemPT}",
        modifier = modifier
    ) {
        composable(
            route = BookmarkDestination.routeWithArgs,
            arguments = listOf(navArgument(BookmarkDestination.itemIdPT) {
                type = NavType.LongType
            })
        ) {
            BookmarkScreen(
                onClick = {
                    navController.navigate(
                        navNull(
                            EntryBookmarkDestination.route,
                            itemOne = it.toString(),
                            itemTwo = "-1"
                        )
                    )
                },
                onSettingsClick = {
                    navController.navigate(
                        navNull(
                            EntryBookmarkDestination.route,
                            itemOne = it.first.toString(),
                            itemTwo = it.second.toString()
                        )
                    )
                },
                navigateToBack = { rootNavController.navigate(FirstDestination.route) }
            )
        }
        composable(
            route = JournalDestination.routeWithArgs,
            arguments = listOf(navArgument(JournalDestination.itemIdPT) {
                type = NavType.LongType
            })
        ) {
            JournalScreen(
                navigateToEdit = {
                    navController.navigate(
                        nav(
                            AddIncubatorDestination.route,
                            itemOne = it.toString()
                        )
                    )
                }
            )
        }

        composable(
            route = FinanceIncubatorDestination.routeWithArgs,
            arguments = listOf(navArgument(FinanceIncubatorDestination.itemIdPT) {
                type = NavType.LongType
            })
        ) {
            FinanceIncubatorScreen()
        }
        composable(
            route = EntryBookmarkDestination.routeWithArgs,
            arguments = listOf(navArgument(EntryBookmarkDestination.itemIdPT) {
                type = NavType.LongType
            }, navArgument(EntryBookmarkDestination.itemId) {
                type = NavType.LongType
            })
        ) {
            EntryBookmarkMainScreen(
                navigateBack = navController::navigateUp
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