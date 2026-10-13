package com.zaroslikov.fermacompose2.ui.project.mainScreen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.zaroslikov.fermacompose2.ui.incubator_project.main_screen.MainIncubatorDestination
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.navigation.ProjectNavHost


object MainProjectsDestination : NavigationDestination {
    override val route = "MainProject"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    const val templateArg = "template"
    val routeWithArgs = "$route/{$itemIdArg}?$templateArg={$templateArg}"
}

@Composable
fun MainProjectScreen(
    rootNavController: NavController,
    itemPT: Long,
    ioOpenTemplate: Boolean
) {
    val projectNavController = rememberNavController()

    val startDestination = if (ioOpenTemplate) Destination.MAGAZINE else Destination.WAREHOUSE
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
    // Экран внутри проекта (доска продукции) может спрятать нижний бар — режим «на весь экран»
    val hideBottomBar = remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            AnimatedVisibility(
                visible = !hideBottomBar.value,
                enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
            ) {
            Column {
                HorizontalDivider(thickness = 1.dp, color = gray_6)
                NavigationBar(
                    windowInsets = WindowInsets(0)
                ) {
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
        }
    ) { contentPadding ->
        CompositionLocalProvider(LocalHideProjectBottomBar provides hideBottomBar) {
            ProjectNavHost(
                modifier = Modifier.padding(contentPadding),
                isOpenTemplate = ioOpenTemplate,
                rootNavController = rootNavController,
                navController = projectNavController,
                itemPT = itemPT
            )
        }
    }
}

/**
 * Флаг «спрятать нижний бар проекта». Экран внутри [ProjectNavHost] (доска продукции в режиме
 * «на весь экран») выставляет его в true и обязан сбросить при уходе с экрана.
 */
val LocalHideProjectBottomBar = compositionLocalOf<MutableState<Boolean>?> { null }


enum class Destination {
    MAGAZINE, WAREHOUSE, FINANCE
}