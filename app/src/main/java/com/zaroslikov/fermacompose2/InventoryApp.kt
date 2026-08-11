package com.zaroslikov.fermacompose2


import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogBase
import com.zaroslikov.fermacompose2.ui.elements.empty_list.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.empty_list.CircularProgressWitchText
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.navigation.InventoryNavHost
import com.zaroslikov.fermacompose2.ui.navigation.UiNotification
import com.zaroslikov.fermacompose2.ui.start.first.FirstIntent
import com.zaroslikov.fermacompose2.ui.start.update.TrainingScreen
import com.zaroslikov.fermacompose2.ui.start.update.UpdateScreen
import com.zaroslikov.fermacompose2.utils.ObserveAsEvents
import com.zaroslikov.fermacompose2.utils.SnackbarController
import kotlinx.coroutines.launch

@Composable
fun InventoryApp(
    intent: Intent?,
    navController: NavHostController = rememberNavController(),
    viewModel: InventoryAppViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val notificationFlow = viewModel.notification
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val activity = LocalActivity.current

    var showSplash by rememberSaveable { mutableStateOf(true) }
    var adFinished by rememberSaveable { mutableStateOf(false) }

    val startDestination = viewModel.startDestination

    LaunchedEffect(intent) {
        viewModel.resolveStartDestination(intent)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    LaunchedEffect(Unit) {
        notificationFlow.collect { event ->
            when (event) {
                UiNotification.Notification ->
                    if (Build.VERSION.SDK_INT >= 33 && state.isNotificationAsked)
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

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

    if (state.isLoading)
        CircularProgress(modifier = Modifier.background(white))
    else
        when {
            state.appSettings.isFirstLaunch ->
                TrainingScreen { viewModel.onIntent(Event.SkipTrainingClicked) }

            state.isFirstLaunchUpdate ->
                UpdateScreen { viewModel.onIntent(Event.SkipTrainingClicked) }

            else -> Scaffold(
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
            ) {
                if (activity != null && (state.isShowAds && showSplash)
                    && !BuildConfig.BUILD_TYPE.contentEquals("debug")
                )
                    SplashScreen(
                        innerPadding = it,
                        activity = activity,
                        onFinished = {
                            showSplash = false
                            adFinished = true
                        }
                    )
                else
                    if (startDestination != null)
                        InventoryNavHost(
                            navController = navController,
                            modifier = Modifier.padding(it),
                            startDestination = startDestination,
                        )

                LaunchedEffect(adFinished) {
                    if (adFinished) {
                        viewModel.update()
                    }
                }
                if (state.isOpenDownloadingUpdate)
                    LoadUpdate(state.percentUpdateDownload)
            }
        }
}

@Composable
fun LoadUpdate(
    progress: Float
) {
    AlertDialogBase(
        onDismissRequest = { }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    stringResource(R.string.update_app_load_update),
                    style = text_14,
                    color = black_2,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
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