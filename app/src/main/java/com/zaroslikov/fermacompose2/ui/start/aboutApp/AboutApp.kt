@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.start.aboutApp

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.BuildConfig
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarNew
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_13
import com.zaroslikov.fermacompose2.blue_18
import com.zaroslikov.fermacompose2.blue_19
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.red_15
import com.zaroslikov.fermacompose2.red_16
import com.zaroslikov.fermacompose2.red_17
import com.zaroslikov.fermacompose2.red_18
import com.zaroslikov.fermacompose2.red_3
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CardNewWithTitle
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.TextMiniCard
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.white
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.net.toUri
import io.appmetrica.analytics.AppMetrica

object AboutAppDestination : NavigationDestination {
    override val route = "About_App"
    override val titleRes = R.string.app_name
}

@Composable
fun AboutAppScreen(
    onNavigateBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarNew(
                title = stringResource(R.string.about_app_screen_title),
                navigateBack = onNavigateBack,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        AboutAppContainer(modifier = Modifier.modifierScreen(innerPadding))
    }
}

@Composable
private fun AboutAppContainer(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        MainCard()
        HelpUsCard()
        ContactCard()
        LoveCard()
    }
}

@Composable
private fun MainCard() {
    val buildDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        .format(Date(BuildConfig.BUILD_TIME))

    CardFieldNew {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .size(80.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = stringResource(R.string.app_name), style = text_16, color = black_2)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextMiniCard(
                        BuildConfig.VERSION_NAME,
                        textColor = green_9,
                        backgroundColor = price_green_2
                    )
                    Text("•", style = text_12, color = gray_7)
                    Text(buildDate, style = text_12, color = gray_7)
                }
                Text(
                    stringResource(R.string.about_app_screen_developer_zaroslikov),
                    style = text_12,
                    color = gray_7
                )
            }
        }
    }
}

@Composable
private fun HelpUsCard() {
    CardNewWithTitle(
        titleRes = R.string.about_app_screen_help_us_title
    ) {
        Text(
            stringResource(R.string.about_app_screen_help_us_text),
            style = text_14,
            color = marengo,
            textAlign = TextAlign.Justify
        )
    }
}

@Composable
private fun ContactCard() {
    val context = LocalContext.current
    CardNewWithTitle(
        titleRes = R.string.about_app_screen_contacts
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Contact(
                titleRes = R.string.about_app_screen_email,
                supportText = "s.zaroslikov@yandex.ru",
                icon = R.drawable.baseline_email_24,
                color = red_15,
                colorIcon = red_14
            ) {
                AppMetrica.reportEvent("Переход в почту")
            }
            /*Contact(
                titleRes = R.string.about_app_screen_telegram,
                supportText = "@myfarmingapp",
                icon = R.drawable.ic_telegram,
                color = blue_19,
                colorIcon = white
            ) {
                AppMetrica.reportEvent("Переход в телеграмм")
            }*/
            Contact(
                titleRes = R.string.about_app_screen_vk,
                supportText = "vk.com/myfermaapp",
                icon = R.drawable.vk_logo_white,
                color = blue_13,
                colorIcon = blue_18
            ) {
                val intent = Intent(Intent.ACTION_VIEW, "https://vk.com/myfermaapp".toUri())
                context.startActivity(intent)
                AppMetrica.reportEvent("Переход в группу ВК")
            }
        }
    }
}

@Composable
private fun Contact(
    @StringRes titleRes: Int,
    supportText: String,
    @DrawableRes icon: Int,
    color: Color,
    colorIcon: Color,
    /*borderColor: Color,
    containerColor: Color,*/
    onClick: () -> Unit
) {
    BorderCard(
        borderColor = grey_2,
        containerColor = ghostly_white,
        padding = PaddingValues(12.dp),
        onClick = onClick
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
                    iconColor = colorIcon,
                    boxColor = color
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(stringResource(titleRes), style = text_16, color = black_2)
                    Text(supportText, style = text_12, color = gray_7)
                }
            }
            Icon(
                painterResource(R.drawable.baseline_chevron_right_24),
                contentDescription = null,
                tint = grey
            )
        }
    }
}

@Composable
private fun LoveCard() {
    BorderCard(
        borderColor = red_16,
        containerColor = red_3,
        padding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.about_app_screen_love_part_1),
                style = text_14,
                color = red_17
            )
            Icon(
                painterResource(R.drawable.baseline_favorite_24), contentDescription = null,
                tint = red_18,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
            Text(
                stringResource(R.string.about_app_screen_love_part_2),
                style = text_14,
                color = red_17
            )
        }
    }
}