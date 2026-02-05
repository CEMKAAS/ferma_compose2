package com.zaroslikov.fermacompose2.ui.navigation

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.main.BookmarkDestination
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.main.BookmarkScreen
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry.EntryBookmarkDestination
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry.EntryBookmarkMainScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncubatorNavHost(
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
                }
            )
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
    }
}