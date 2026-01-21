package com.zaroslikov.fermacompose2.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.BookmarkDestination
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.BookmarkScreen
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry.EntryBookmarkDestination
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry.EntryBookmarkScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncubatorNavHost(
    navController: NavHostController,
    itemPT: Long,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "${BookmarkDestination.route}/${itemPT}",
        modifier = modifier
    ) {
        composable(
            route = BookmarkDestination.routeWithArgs,
            arguments = listOf(navArgument(BookmarkDestination.itemIdArg) {
                type = NavType.LongType
            })
        ) {
            BookmarkScreen(
                onClick = { navController.navigate("${EntryBookmarkDestination.route}/${-1/*it*/}") }
            )
        }

        composable(
            route = EntryBookmarkDestination.routeWithArgs,
            arguments = listOf(navArgument(EntryBookmarkDestination.itemIdArg) {
                type = NavType.LongType
            })
        ) {
            EntryBookmarkScreen()
        }
    }
}