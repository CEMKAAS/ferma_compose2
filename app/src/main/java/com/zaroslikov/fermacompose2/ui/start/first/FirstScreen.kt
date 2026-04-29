@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.start.first

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarStart2
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.gray_8
import com.zaroslikov.fermacompose2.green_11
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.green_g_1
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_15
import com.zaroslikov.fermacompose2.orang_17
import com.zaroslikov.fermacompose2.orang_18
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_3
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.orang_8
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_13
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.red_15
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardClips
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.DrawerSheetNew
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.IconAndTextNew
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenuEdit
import com.zaroslikov.fermacompose2.ui.elements.modifierScreenLazy
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.navigation.UiNotification
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard
import com.zaroslikov.fermacompose2.ui.project.sections.EmptyState
import com.zaroslikov.fermacompose2.ui.project.sections.InventoryBody
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.launch

object FirstDestination : NavigationDestination {
    override val route = "First"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun FirstScreen(
    navigateToItemProject: (Long) -> Unit,
    navigateToItemIncubator: (Long) -> Unit,
    navigateToProject: (Long) -> Unit,
    navigateToIncubator: (Long) -> Unit,
    navigateToProfile: () -> Unit,
    navigateToAboutApp: () -> Unit,
    navigateToSettings: () -> Unit,
    viewModel: FirstViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val notificationFlow = viewModel.notification
    val colors = listOf(price_green, green_9)
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var showBottomSheet by remember { mutableStateOf(false) }
    val drawerClose = {
        scope.launch {
            drawerState.apply {
                if (isClosed) open() else close()
            }
        }
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
    if (state.isLoading)
        CircularProgress(
            modifier = Modifier,
        )
    else
        if (state.appSettings.isFirstLaunch)
            TrainingScreen { viewModel.onIntent(FirstIntent.SkipTrainingClicked) }
        else
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    DrawerSheetNew(
                        onProfileClick = {
                            navigateToProfile()
                            AppMetrica.reportEvent("Переход в профиль")
                        },
                        onSettingsClick = {
                            navigateToSettings()
                            AppMetrica.reportEvent("Переход в настройки")
                        },
                        onAboutAppClick = {
                            navigateToAboutApp()
                            AppMetrica.reportEvent("Переход в о приложении")
                        },
                        onCloseClick = { drawerClose() }
                    )
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBarStart2(
                            title = R.string.start_screen_title,
                            isArchive = state.isArchive,
                            infoBottomSheet = { drawerClose() },
                            onArchiveClick = { viewModel.onIntent(FirstIntent.ArchiveModeClicked) }
                        )
                    },
                    floatingActionButton = {
                        NeonGlowFab(
                            colors = colors,
                            onClick = { showBottomSheet = true })
                    }
                ) { innerPadding ->
                    if (state.isLoading)
                        CircularProgress(
                            modifier = Modifier.padding(innerPadding),
                        )
                    else
                        StartScreenContainer2(
                            modifier = Modifier.modifierScreenLazy(innerPadding),
                            itemList = state.list,
                            brieflyList = state.archiveList,
                            isArchiveMode = state.isArchive,
                            onEditProjectClick = { navigateToProject(it) },
                            onEditIncubatorClick = { navigateToIncubator(it) },
                            onArchiveClick = { viewModel.onIntent(FirstIntent.ArchiveClicked(it)) },
                            onArchiveIncubatorClick = {
                                viewModel.onIntent(
                                    FirstIntent.OpenArchiveIncubatorBottomSheetClicked(true, it)
                                )
                            },
                            onUnarchiveClick = { viewModel.onIntent(FirstIntent.UnarchiveClicked(it)) },
                            onDeleteClick = {
                                viewModel.onIntent(
                                    FirstIntent.OpenDeleteBottomSheetClicked(
                                        true,
                                        it
                                    )
                                )
                            },
                            onNavigationProject = { navigateToItemProject(it) },
                            onNavigationIncubator = { navigateToItemIncubator(it) })
                    if (showBottomSheet)
                        ChoiceProjectBottomSheet(
                            onDismissRequest = { showBottomSheet = false },
                            onIncubatorProject = { navigateToIncubator(-1) },
                            onAddProject = { navigateToProject(-1) }
                        )

                    if (state.isOpenArchiveIncubatorBottomSheet)
                        WarningArchiveBottomSheet(
                            onDismissRequest = {
                                viewModel.onIntent(
                                    FirstIntent.OpenArchiveIncubatorBottomSheetClicked(
                                        false
                                    )
                                )
                            },
                            onArchiveIncubatorClick = {
                                viewModel.onIntent(FirstIntent.ArchiveClicked(null))
                            }
                        )

                    if (state.isOpenDeleteBottomSheet)
                        WarningDeleteBottomSheet(
                            isProject = state.currentProjectTable?.mode ?: true,
                            onDismissRequest = {
                                viewModel.onIntent(FirstIntent.OpenDeleteBottomSheetClicked(false))
                            },
                            onDeleteDatabaseClick = { viewModel.onIntent(FirstIntent.DeleteClicked) }
                        )
                }
            }
}

@Composable
private fun StartScreenContainer2(
    modifier: Modifier = Modifier,
    isArchiveMode: Boolean,
    itemList: List<DomainProjectTable>,
    brieflyList: List<DomainProjectTable>,
    onEditProjectClick: (Long) -> Unit,
    onEditIncubatorClick: (Long) -> Unit,
    onDeleteClick: (DomainProjectTable) -> Unit,
    onArchiveClick: (DomainProjectTable) -> Unit,
    onArchiveIncubatorClick: (DomainProjectTable) -> Unit,
    onNavigationProject: (Long) -> Unit,
    onNavigationIncubator: (Long) -> Unit,
    onUnarchiveClick: (DomainProjectTable) -> Unit
) {
    InventoryBody(
        modifier = modifier,
        details = !isArchiveMode,
        itemList = itemList,
        searchList = itemList,
        brieflyList = brieflyList,
        detailCard = { index, item ->
            ProjectCard(
                projectTable = item,
                onEditClick = {
                    if (item.mode) onEditProjectClick(item.id)
                    else onEditIncubatorClick(item.id)

                    Log.i("edit", "StartScreenContainer2: ${item.id}")
                },
                onArchiveClick = {
                    if (item.mode) onArchiveClick(item)
                    else onArchiveIncubatorClick(item)
                },
                onDeleteClick = { onDeleteClick(item) },
                onNavigationProject = {
                    if (item.mode) onNavigationProject(item.id)
                    else onNavigationIncubator(item.id)
                }
            )
        },
        brieflyCard = { item ->
            ProjectCard(
                projectTable = item,
                onUnarchiveClick = { onUnarchiveClick(item) },
                onDeleteClick = { onDeleteClick(item) },
                onNavigationProject = {
                    if (item.mode) onNavigationProject(item.id)
                    else onNavigationIncubator(item.id)
                }
            )
        },
        detailEmptyState = EmptyState(
            title = R.string.start_screen_no_data_title,
            message = R.string.start_screen_no_data_message,
            icon = R.drawable.ic_stat_name
        ),
        brieflyEmptyState = EmptyState(
            title = R.string.start_screen_no_data_archive_title,
            message = R.string.start_screen_no_data_archive_message,
            support = R.string.is_empty,
            icon = R.drawable.baseline_archive_24
        ),
        iconColor = green_shamrock,
        backgroundColor = green_g_1,
        isArchive = false,
        isBorderCard = false, searchBrieflyList = brieflyList,
    )
}

@Composable
private fun ProjectCard(
    projectTable: DomainProjectTable,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: () -> Unit,
    onArchiveClick: (() -> Unit)? = null,
    onUnarchiveClick: (() -> Unit)? = null,
    onNavigationProject: () -> Unit
) {
    val (iconProject, iconColor) = if (projectTable.mode) R.drawable.livestock to price_green_2 else R.drawable.outline_egg_24 to orang_8

    val colors = if (projectTable.archive) listOf(grey, gray_7) else
        if (projectTable.mode) listOf(green_6, green_shamrock)
        else listOf(orang_9, orang_15)

    CardFieldNew(
        onClick = onNavigationProject,
        colors = colors
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconTransaction2(
                    imagePath = projectTable.imagePath,
                    currentIcon = projectTable.currentIcon ?: iconProject,
                    color = iconColor,
                    isArchive = projectTable.archive,
                    sizeCard = 64.dp,
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = projectTable.title,
                        style = text_16,
                        color = black_2
                    )
                    CardClips(
                        colorBackground = if (projectTable.archive) gray_6 else if (projectTable.mode) price_green_2 else orang_4,
                        colorBorder = if (projectTable.archive) gray_8 else if (projectTable.mode) green_11 else orang_5,
                        colorText = if (projectTable.archive) marengo else if (projectTable.mode) green_9 else orang_6,
                        value = stringResource(if (projectTable.mode) R.string.start_screen_common_project else R.string.bottom_bar_incubator),
                        icon = if (projectTable.archive) R.drawable.baseline_archive_24 else if (projectTable.mode) R.drawable.outline_work_24 else R.drawable.outline_egg_24,
                        colorIcon = if (projectTable.archive) marengo else if (projectTable.mode) green_9 else orang_6
                    )
                    IconAndTextNew(
                        iconRes = R.drawable.baseline_calendar_month_24,
                        valueString = projectTable.date,
                        iconColor = gray_7
                    )
                }
            }
            DropdownMenuEdit(
                onEditClick = onEditClick,
                onArchiveClick = onArchiveClick,
                onUnarchiveClick = onUnarchiveClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
private fun ChoiceProjectBottomSheet(
    onDismissRequest: () -> Unit,
    onAddProject: () -> Unit,
    onIncubatorProject: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.start_screen_create_new_project),
                    style = text_16,
                    color = black_2
                )
                Text(
                    stringResource(R.string.start_screen_choice_type_project),
                    style = text_14,
                    color = gray_7
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChoiceProjectCard(
                    titleRes = R.string.start_screen_common_project,
                    supportText = stringResource(R.string.start_screen_common_project_support),
                    icon = R.drawable.outline_work_24,
                    color = green_8,
                    colorIcon = green_9,
                    borderColor = green_11,
                    containerColor = price_green_2
                ) { onAddProject() }
                ChoiceProjectCard(
                    titleRes = R.string.start_screen_incubator_project,
                    supportText = stringResource(R.string.start_screen_incubator_project_support),
                    icon = R.drawable.outline_egg_24,
                    color = orang_5,
                    colorIcon = orang_6,
                    borderColor = orang_5,
                    containerColor = orang_3
                ) { onIncubatorProject() }
            }
        }
    }
}

@Composable
private fun ChoiceProjectCard(
    @StringRes titleRes: Int,
    supportText: String,
    @DrawableRes icon: Int,
    color: Color,
    colorIcon: Color,
    borderColor: Color,
    containerColor: Color,
    onClick: () -> Unit
) {
    BorderCard(
        borderColor = borderColor,
        containerColor = containerColor,
        padding = PaddingValues(16.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconTransaction2(
                    sizeCard = 48.dp,
                    icon = icon,
                    iconColor = colorIcon,
                    boxColor = color
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(stringResource(titleRes), style = text_16, color = black_2)
                    Text(
                        supportText, style = text_12, color = gray_7
                    )
                }
            }
            Icon(
                painterResource(R.drawable.baseline_chevron_right_24),
                contentDescription = null,
                tint = grey,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Composable
private fun WarningArchiveBottomSheet(
    onDismissRequest: () -> Unit,
    onArchiveIncubatorClick: () -> Unit
) {
    BaseBottomSheet(
        title = stringResource(R.string.settings_screen_warning),
        supText = stringResource(R.string.start_screen_archive_incubator),
        skipPartiallyExpanded = false,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WarningCard(
                colorBackground = orang_4,
                colorBorder = orang_5,
                colorIcon = orang_18,
                colorTitle = orang_18,
                colorText = orang_6,
                icon = R.drawable.outline_info_24,
                title = R.string.start_screen_archive_incubator_title,
                text = R.string.start_screen_archive_incubator_text
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.start_screen_in_archive),
                    onClick = { onArchiveIncubatorClick() },
                    paddingValues = PaddingValues(16.dp),
                    colors = listOf(orang_2, orang_2)
                )
                GradientButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.button_text_cancel),
                    onClick = onDismissRequest,
                    paddingValues = PaddingValues(16.dp),
                    colors = listOf(gray_6, gray_6), textColor = dark
                )
            }
        }
    }
}


@Composable
private fun WarningDeleteBottomSheet(
    isProject: Boolean,
    onDismissRequest: () -> Unit,
    onDeleteDatabaseClick: () -> Unit
) {
    BaseBottomSheet(
        title = stringResource(R.string.settings_screen_warning_delete),
        supText = stringResource(if (isProject) R.string.start_screen_delete_project else R.string.start_screen_delete_incubator),
        skipPartiallyExpanded = false,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WarningCard(
                colorBackground = red_11,
                colorBorder = red_15,
                colorIcon = orang_17,
                colorTitle = orang_17,
                colorText = red_14,
                icon = R.drawable.baseline_delete_24,
                title = R.string.start_screen_delete_title,
                text = if (isProject) R.string.start_screen_delete_project_text else R.string.start_screen_delete_incubator_text
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.start_screen_delete),
                    onClick = { onDeleteDatabaseClick() },
                    paddingValues = PaddingValues(16.dp),
                    colors = listOf(red_13, error_base)
                )
                GradientButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.button_text_cancel),
                    onClick = onDismissRequest,
                    paddingValues = PaddingValues(16.dp),
                    colors = listOf(gray_6, gray_6), textColor = dark
                )
            }
        }
    }
}