@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.start.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarNew
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_13
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_6
import com.zaroslikov.fermacompose2.blue_7
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_11
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_17
import com.zaroslikov.fermacompose2.orang_18
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_13
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.red_15
import com.zaroslikov.fermacompose2.supportFun.file
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardNewWithTitle
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.TextMiniCard
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.navigation.EventFile
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard
import com.zaroslikov.fermacompose2.violet_3
import com.zaroslikov.fermacompose2.violet_5
import com.zaroslikov.fermacompose2.violet_6
import com.zaroslikov.fermacompose2.violet_8
import com.zaroslikov.fermacompose2.violet_9
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SettingsDestination : NavigationDestination {
    override val route = "settings_project"
    override val titleRes = R.string.app_name
}

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val eventFlow = viewModel.events
    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is EventFile.File -> file(context, event.value)
            }
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNew(
                title = stringResource(R.string.settings_screen_title),
                navigateBack = onNavigateBack,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        SettingsContainer(
            modifier = Modifier.modifierScreen(innerPadding),
            state = state,
            onIntent = viewModel::onIntent
        )
    }
    if (state.isOpenExportBottomSheet)
        ExportChoice(
            backupDataText = state.backupDataText,
            onDismissRequest = { viewModel.onIntent(SettingsIntent.OpenExportBottomSheetClick(false)) },
            onShapeFile = { viewModel.onIntent(SettingsIntent.ExportDatabasePress) }
        )

    if (state.isOpenImportBottomSheet)
        ImportWarning(
            onDismissRequest = { viewModel.onIntent(SettingsIntent.OpenImportBottomSheetClick(false)) },
            onImportDatabase = {
                viewModel.onIntent(SettingsIntent.ImportDatabasePress(it, context))
            }
        )
    if (state.isOpenDeleteBottomSheet)
        DeleteWarning(
            onDismissRequest = { viewModel.onIntent(SettingsIntent.OpenDeleteBottomSheetClick(false)) },
            onDeleteDatabaseClick = { viewModel.onIntent(SettingsIntent.DeleteDatabasePress) }
        )
}


@Composable
private fun SettingsContainer(
    modifier: Modifier = Modifier,
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        /*AppearanceCard()*/
        NotificationCard()
        /* LanguageCard()*/
        DataManagementCard(
            onExportDatabaseClick = { onIntent(SettingsIntent.OpenExportBottomSheetClick(true)) },
            onImportDatabaseClick = { onIntent(SettingsIntent.OpenImportBottomSheetClick(true)) },
            onDeleteDatabaseClick = { onIntent(SettingsIntent.OpenDeleteBottomSheetClick(true)) }
        )
        DataStorageCard()
    }
}

@Composable
private fun AppearanceCard() {
    var re by remember { mutableStateOf(true) }
    CardNewWithTitle(
        titleRes = R.string.settings_screen_appearance
    ) {
        SD(
            titleRes = R.string.settings_screen_dark_theme,
            supportText = stringResource(if (re) R.string.settings_screen_on else R.string.settings_screen_off),
            icon = R.drawable.outline_bedtime_24,
            color = blue_6,
            iconColor = blue_7
        ) {
            Switch(
                checked = re,
                onCheckedChange = { re = !re }
            )
        }
    }
}

@Composable
private fun NotificationCard() {

    val context = LocalContext.current

    var enabled by remember {
        mutableStateOf(
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        enabled = isGranted
    }

    CardNewWithTitle(
        titleRes = R.string.settings_screen_notification
    ) {
        SD(
            titleRes = R.string.settings_screen_push_notification,
            supportText = stringResource(
                if (enabled) R.string.settings_screen_on_s
                else R.string.settings_screen_off_s
            ),
            icon = R.drawable.baseline_notifications_none_24,
            color = orang_5,
            iconColor = orang_6
        ) {
            Switch(
                checked = enabled,
                onCheckedChange = {
                    if (enabled) {
                        openNotificationSettings(context)
                    } else {
                        if (Build.VERSION.SDK_INT >= 33) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            openNotificationSettings(context)
                        }
                    }
                }
            )
        }
    }
}

private fun openNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
    context.startActivity(intent)
}

@Composable
private fun LanguageCard() {
    CardNewWithTitle(
        titleRes = R.string.settings_screen_language
    ) {
        SD(
            titleRes = R.string.settings_screen_language_interface,
            supportText = stringResource(R.string.settings_screen_language_russian),
            icon = R.drawable.baseline_notifications_none_24,
            color = blue_9,
            iconColor = blue_8
        ) {
            TextMiniCard(
                value = "RU",
                textColor = blue_8,
                backgroundColor = blue_3
            )
        }
    }
}

@Composable
private fun DataManagementCard(
    onExportDatabaseClick: () -> Unit,
    onImportDatabaseClick: () -> Unit,
    onDeleteDatabaseClick: () -> Unit
) {
    CardNewWithTitle(
        titleRes = R.string.settings_screen_data_management
    ) {
        SD(
            titleRes = R.string.settings_screen_exporting_data,
            supportText = stringResource(R.string.settings_screen_upload_all_projects),
            icon = R.drawable.outline_upload_24,
            color = green_8,
            iconColor = green_9,
            onClick = onExportDatabaseClick
        )
        SD(
            titleRes = R.string.settings_screen_importing_data,
            supportText = stringResource(R.string.settings_screen_upload_projects_from_file),
            icon = R.drawable.outline_download_24,
            color = blue_9,
            iconColor = blue_8,
            onClick = onImportDatabaseClick
        )
        HorizontalDivider(thickness = 1.dp, color = gray_6)
        SD(
            titleRes = R.string.settings_screen_clear_all_data,
            supportText = stringResource(R.string.settings_screen_delete_all_projects_and_settings),
            icon = R.drawable.baseline_delete_24,
            color = red_15,
            iconColor = red_14,
            titleColor = error_base,
            onClick = onDeleteDatabaseClick
        )
    }
}

@Composable
private fun DataStorageCard() {
    WarningCard(
        colorBackground = violet_3,
        colorBorder = violet_5,
        colorIcon = violet_6,
        colorIconBackground = violet_8,
        colorTitle = violet_9,
        colorText = violet_6,
        icon = R.drawable.outline_database_24,
        title = R.string.settings_screen_data_storage,
        text = R.string.settings_screen_data_storage_text
    )
}

@Composable
fun SD(
    @StringRes titleRes: Int,
    supportText: String? = null,
    @DrawableRes icon: Int,
    color: Color,
    iconColor: Color,
    titleColor: Color = black_2,
    onClick: (() -> Unit)? = null,
    content: (@Composable RowScope.() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                onClick?.let {
                    Modifier
                        .clip(shape = RoundedCornerShape(10.dp))
                        .clickable { it() }
                } ?: Modifier
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTransaction2(
                sizeCard = 36.dp,
                icon = icon,
                iconColor = iconColor,
                boxColor = color
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(stringResource(titleRes), style = text_16, color = titleColor)
                supportText?.let {
                    Text(it, style = text_14, color = gray_7)
                }
            }
        }
        content?.let {
            it()
        }
    }
}

@Composable
private fun ExportChoice(
    backupDataText: String?,
    onDismissRequest: () -> Unit,
    onShapeFile: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            val text = backupDataText ?: return@let
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(text.toByteArray())
            }
        }
    }
    BaseBottomSheet(
        title = stringResource(R.string.settings_screen_export_data),
        supText = stringResource(R.string.settings_screen_choice_save_data),
        skipPartiallyExpanded = false,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ChoiceProjectCard(
                titleRes = R.string.settings_screen_save_in_device,
                supportText = R.string.settings_screen_save_in_device_data,
                icon = R.drawable.outline_file_save_24,
                color = green_8,
                colorIcon = green_9,
                borderColor = green_11,
                containerColor = price_green_2
            ) {
                launcher.launch("ferma_data.json")
            }
            ChoiceProjectCard(
                titleRes = R.string.settings_screen_send_file,
                supportText = R.string.settings_screen_send_file_data,
                icon = R.drawable.outline_share_24,
                color = blue_13,
                colorIcon = blue_8,
                borderColor = blue_13,
                containerColor = blue_3
            ) {
                onShapeFile()
            }
        }
    }
}


@Composable
fun ImportWarning(
    onDismissRequest: () -> Unit,
    onImportDatabase: (String?) -> Unit
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val loadLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            val importedText = try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader().readText()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            onImportDatabase(importedText)
        }
    }
    BaseBottomSheet(
        title = stringResource(R.string.settings_screen_warning),
        supText = stringResource(R.string.settings_screen_confirm_action),
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
                title = R.string.settings_screen_import_data_title,
                text = R.string.settings_screen_import_data_text
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.settings_screen_confirm_and_select_file),
                    onClick = { loadLauncher.launch("application/json") },
                    prefixIconRes = R.drawable.outline_check_circle_24,
                    paddingValues = PaddingValues(16.dp),
                    colors = listOf(Color(0xFF00A63E), Color(0xFF009966))
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
fun DeleteWarning(
    onDismissRequest: () -> Unit,
    onDeleteDatabaseClick: () -> Unit
) {
    BaseBottomSheet(
        title = stringResource(R.string.settings_screen_warning_delete),
        supText = stringResource(R.string.settings_screen_confirm_delete),
        skipPartiallyExpanded = true,
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
                title = R.string.settings_screen_delete_data_title,
                text = R.string.settings_screen_delete_data_text
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.settings_screen_delete_all_data),
                    onClick = { onDeleteDatabaseClick() },
                    prefixIconRes = R.drawable.baseline_delete_24,
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


@Composable
private fun ChoiceProjectCard(
    @StringRes titleRes: Int,
    @StringRes supportText: Int,
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
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconTransaction2(
                sizeCard = 48.dp,
                icon = icon,
                iconColor = colorIcon,
                boxColor = color
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(stringResource(titleRes), style = text_16, color = black_2)
                Text(stringResource(supportText), style = text_12, color = marengo)
            }
        }
    }
}
