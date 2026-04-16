package com.zaroslikov.fermacompose2.ui.elements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.BuildConfig
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.violet_5
import com.zaroslikov.fermacompose2.violet_6

@Composable
fun DrawerSheetNew(
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutAppClick: () -> Unit,
    onCloseClick: () -> Unit
) {

    val drawerItems = listOf(
        DrawerRow(
            titleRes = R.string.drawer_sheer_profile,
            supRes = R.string.drawer_sheer_profile_support,
            iconRes = R.drawable.baseline_person_24,
            colorIcon = orang_6,
            color = orang_5,
            onClick = onProfileClick
        ), DrawerRow(
            titleRes = R.string.drawer_sheer_settings,
            supRes = R.string.drawer_sheer_settings_support,
            iconRes = R.drawable.icon_setting,
            colorIcon = violet_6,
            color = violet_5,
            onClick = onSettingsClick
        ), DrawerRow(
            titleRes = R.string.drawer_sheer_about_app,
            supRes = R.string.drawer_sheer_about_app_support,
            iconRes = R.drawable.icon_info,
            colorIcon = blue_8,
            color = blue_9,
            onClick = onAboutAppClick
        )
    )

    ModalDrawerSheet(
        windowInsets = WindowInsets(0),
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.drawer_sheer_menu),
                            style = text_16,
                            color = black_2
                        )
                        IconButton(onClick = onCloseClick) {
                            Icon(
                                painterResource(R.drawable.baseline_clear_24),
                                contentDescription = null,
                                tint = marengo
                            )
                        }
                    }
                    HorizontalDivider(thickness = 1.dp, color = gray_6)
                }
                Column(
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    drawerItems.forEach { items ->
                        DrawerItemsNew(
                            titleRes = items.titleRes,
                            supRes = items.supRes,
                            iconRes = items.iconRes,
                            colorIcon = items.colorIcon,
                            color = items.color
                        ) { items.onClick() }
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(21.dp)
            ) {
                HorizontalDivider(thickness = 1.dp, color = gray_6)
                Text(
                    text = stringResource(R.string.drawer_sheer_app_version_s).format(BuildConfig.VERSION_NAME),
                    style = text_12,
                    color = grey,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 21.dp)
                )
            }
        }
    }
}

@Composable
fun DrawerItemsNew(
    @StringRes titleRes: Int,
    @StringRes supRes: Int,
    @DrawableRes iconRes: Int,
    colorIcon: Color,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconTransaction2(
            sizeCard = 36.dp,
            icon = iconRes,
            iconColor = colorIcon,
            boxColor = color
        )
        Column {
            Text(stringResource(titleRes), style = text_16, color = black_2)
            Text(stringResource(supRes), style = text_12, color = gray_7)
        }
    }
}

data class DrawerRow(
    val titleRes: Int,
    val supRes: Int,
    val iconRes: Int,
    val colorIcon: Color,
    val color: Color,
    val onClick: () -> Unit
)
