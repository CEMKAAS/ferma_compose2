package com.zaroslikov.fermacompose2.ui.project.mainScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toNav
import com.zaroslikov.fermacompose2.supportFun.toResId
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
    rootNavController: NavController,
    itemPT: Long
) {
    val projectNavController = rememberNavController()
    val startDestination = Destination.WAREHOUSE
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        bottomBar = {
            Column {
                HorizontalDivider(thickness = 1.dp, color = gray_6)
                NavigationBar {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Destination.entries.forEachIndexed { index, destination ->
                            BottomBarButton(
                                destination = destination.ordinal,
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
                                },
                                colors = destination.toColorList(),
                                drawableRes = destination.toDrawRes(),
                                stringRes = destination.toResId()
                            )
                        }
                    }
                }
            }
        }
    ) { contentPadding ->
        ProjectNavHost(
            rootNavController = rootNavController,
            navController = projectNavController,
            itemPT = itemPT,
            modifier = Modifier.padding(contentPadding)
        )
    }
}


enum class Destination {
    MAGAZINE, WAREHOUSE, FINANCE
}