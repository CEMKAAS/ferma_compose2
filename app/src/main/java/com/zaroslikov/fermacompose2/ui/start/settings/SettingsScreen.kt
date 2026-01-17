@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.start.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarNew
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_6
import com.zaroslikov.fermacompose2.blue_7
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.red_15
import com.zaroslikov.fermacompose2.ui.elements.CardNewWithTitle
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.TextMiniCard
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard
import com.zaroslikov.fermacompose2.violet_3
import com.zaroslikov.fermacompose2.violet_5
import com.zaroslikov.fermacompose2.violet_6
import com.zaroslikov.fermacompose2.violet_8
import com.zaroslikov.fermacompose2.violet_9

object SettingsDestination : NavigationDestination {
    override val route = "settings_project"
    override val titleRes = R.string.app_name
}

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
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
        SettingsContainer(modifier = Modifier.modifierScreen(innerPadding))
    }
}

@Composable
private fun SettingsContainer(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        AppearanceCard()
        NotificationCard()
        LanguageCard()
        DataManagementCard()
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
    var re by remember { mutableStateOf(true) }
    CardNewWithTitle(
        titleRes = R.string.settings_screen_notification
    ) {
        SD(
            titleRes = R.string.settings_screen_push_notification,
            supportText = stringResource(if (re) R.string.settings_screen_on_s else R.string.settings_screen_off_s),
            icon = R.drawable.baseline_notifications_none_24,
            color = orang_5,
            iconColor = orang_6
        ) {
            Switch(
                checked = re,
                onCheckedChange = { re = !re }
            )
        }
    }
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
private fun DataManagementCard() {
    CardNewWithTitle(
        titleRes = R.string.settings_screen_data_management
    ) {
        SD(
            titleRes = R.string.settings_screen_exporting_data,
            supportText = stringResource(R.string.settings_screen_upload_all_projects),
            icon = R.drawable.outline_download_24,
            color = green_8,
            iconColor = green_9
        )
        SD(
            titleRes = R.string.settings_screen_importing_data,
            supportText = stringResource(R.string.settings_screen_upload_projects_from_file),
            icon = R.drawable.outline_upload_24,
            color = blue_9,
            iconColor = blue_8
        )
        HorizontalDivider(thickness = 1.dp, color = gray_6)
        SD(
            titleRes = R.string.settings_screen_clear_all_data,
            supportText = stringResource(R.string.settings_screen_delete_all_projects_and_settings),
            icon = R.drawable.baseline_delete_24,
            color = red_15,
            iconColor = red_14,
            titleColor = error_base
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
private fun SD(
    @StringRes titleRes: Int,
    supportText: String,
    @DrawableRes icon: Int,
    color: Color,
    iconColor: Color,
    titleColor: Color = black_2,
    content: (@Composable RowScope.() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTransaction2(
                sizeCard = 36.dp,
                icon = icon,
                colorIcon = iconColor,
                color = color
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(stringResource(titleRes), style = text_16, color = titleColor)
                Text(supportText, style = text_14, color = gray_7)
            }
        }
        content?.let {
            it()
        }
    }
}