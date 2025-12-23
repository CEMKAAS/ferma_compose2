package com.zaroslikov.fermacompose2.ui.project.mainScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.toNav
import com.zaroslikov.fermacompose2.ui.elements.BottomBarButton
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.navigation.ProjectNavHost


object MainProjectsDestination : NavigationDestination {
    override val route = "MainProject"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun MainProjectScreen(
    itemPT: Long
) {
    val projectNavController = rememberNavController()
    val startDestination = Destination.WAREHOUSE
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Destination.entries.forEachIndexed { index, destination ->
                        BottomBarButton(
                            destination = destination,
                            index = selectedDestination,
                            onClick = {
                                projectNavController.navigate(route = destination.toNav(itemPT)) {
                                    popUpTo(projectNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                selectedDestination = index
                            }
                        )
                    }
                }
            }
        }
    ) { contentPadding ->
        ProjectNavHost(
            navController = projectNavController,
            itemPT = itemPT,
            modifier = Modifier.padding(contentPadding)
        )
    }
}


enum class Destination {
    MAGAZINE, WAREHOUSE, FINANCE
}