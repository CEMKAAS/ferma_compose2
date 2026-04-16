package com.zaroslikov.fermacompose2


import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.zaroslikov.fermacompose2.ui.navigation.InventoryNavHost
import com.zaroslikov.fermacompose2.utils.ObserveAsEvents
import com.zaroslikov.fermacompose2.utils.SnackbarController
import kotlinx.coroutines.launch

@Composable
fun InventoryApp(
    navController: NavHostController = rememberNavController(),
    action: String?,
    projectId: Long
) {
    val focusManager = LocalFocusManager.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(
        flow = SnackbarController.events,
        snackbarHostState
    ) { event ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()

            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.action?.name,
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed) {
                event.action?.action?.invoke()
            }
        }
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
//            Banner(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .wrapContentHeight()
//            )
        }
    ) {
        InventoryNavHost(
            navController = navController,
            modifier = Modifier.padding(it),
            action = action, projectId = projectId
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarStart2(
    @StringRes title: Int,
    infoBottomSheet: () -> Unit,
    onArchiveClick: () -> Unit,
    isArchive: Boolean,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = { Text(text = stringResource(title)) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = infoBottomSheet) {
                Icon(
                    painterResource(R.drawable.icon_menu),
                    contentDescription = "Информация"
                )
            }
        },
        windowInsets = WindowInsets(0),
        actions = {
            IconButton(onClick = onArchiveClick) {
                Icon(
                    painter = painterResource(id = if (isArchive) R.drawable.baseline_unarchive_24 else R.drawable.baseline_archive_24),
                    contentDescription = "Localized description",
                )
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarEdit(
    title: String, navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    painterResource(R.drawable.icon_setting),
                    contentDescription = "Назад"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarNew(
    title: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateBack: (() -> Unit)? = null,
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        windowInsets = WindowInsets(0),
        navigationIcon = {
            navigateBack?.let {
                IconButton(onClick = it) {
                    Icon(
                        painterResource(R.drawable.baseline_arrow_back_24),
                        contentDescription = "Назад"
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarNewFilter(
    title: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateBack: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        contentPadding = PaddingValues(
            end = dimensionResource(id = R.dimen.padding_medium),
            top = 16.dp,
            bottom = 8.dp
        ),
        scrollBehavior = scrollBehavior,
        windowInsets = WindowInsets(0),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    navigateBack?.let {
                        Icon(
                            painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = "Назад",
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .clickable(onClick = it)
                                .padding(vertical = 4.dp)
                                .padding(end = 4.dp),
                        )
                    }
                    Text(
                        title, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                content()
            }
        }
    )
}