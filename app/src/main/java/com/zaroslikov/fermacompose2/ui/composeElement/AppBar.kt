@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun TopAppBarNavigation(
    @StringRes title: Int,
    scope: CoroutineScope,
    drawerState: DrawerState,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(text = stringResource(title))
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.apply {
                        if (isClosed) open() else close()
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Меню"
                )
            }
        }
    )
}


@Composable
fun TopAppBarBack(
    @StringRes intRes: Int,
   navigateUp: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(text = stringResource(intRes))
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад"
                )
            }
        }
    )
}