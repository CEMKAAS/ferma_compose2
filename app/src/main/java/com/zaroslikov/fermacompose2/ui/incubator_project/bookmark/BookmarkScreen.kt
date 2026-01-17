@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_15
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.ui.elements.BigColorCard
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.IconAndTextNew
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_30
import com.zaroslikov.fermacompose2.ui.elements.сompositions.Slider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.white


object BookmarkDestination : NavigationDestination {
    override val route = "bookmark"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun BookmarkScreen(
    viewModel: BookmarkViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            BookmarkContainer(
                modifier = Modifier.modifierScreen(innerPadding),
                state = state,
            )
    }
}

@Composable
private fun BookmarkContainer(
    modifier: Modifier,
    state: BookmarkState
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.End
    ) {
        MainCard()

    }
}

@Composable
private fun MainCard(
    currentDay: String,
    endDay: String,
    percent: String,
    percentFloat: Float,
    egg: Int,
    dateBegin: String,
    dateEnd: String
) {
    BigColorCard(
        glowColor = orang_15,
        colors = listOf(orang_9, orang_15),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    stringResource(R.string.book_mark_screen_day_incubator),
                    style = text_14,
                    color = white.copy(alpha = 80f)
                )
                Text("$currentDay/$endDay", style = text_30, color = white)
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.book_mark_screen_progress),
                    style = text_14,
                    color = white.copy(alpha = 80f)
                )
                Text("$percent%", style = text_14, color = white.copy(alpha = 80f))
            }
            Slider(
                percentFloat = percentFloat,
                color = white
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconAndTextNew(
                    iconRes = R.drawable.outline_egg_24,
                    valueString = egg.toString(),
                    iconColor = white, iconSize = 20.dp,
                    textColor = white,
                )
                IconAndTextNew(
                    iconRes = R.drawable.baseline_access_time_24,
                    valueString = "сколько-то дней до конца",
                    iconColor = white, iconSize = 20.dp,
                    textColor = white,
                )
            }
            HorizontalDivider(thickness = 1.dp, color = white.copy(20f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconAndTextNew(
                    iconRes = R.drawable.baseline_calendar_month_24,
                    valueString = dateBegin,
                    iconColor = white, textColor = white,
                )
                IconAndTextNew(
                    iconRes = R.drawable.baseline_calendar_month_24,
                    valueString = dateEnd,
                    iconColor = white, textColor = white,
                )
            }
        }
    }
}

@Composable
private fun CurrentParametrs(
    currentDay: String
) {
    CardFieldNew {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.bookmark_screen_current_parametrs_s).format(currentDay),
                style = text_16, color = black_2
            )
        }

    }
}

@Composable
private fun CurrentParametr(
    modifier: Modifier = Modifier,
    containerColor: Color,
    @DrawableRes iconRes: Int,
    iconColor: Color,
    iconBackgroundColor: Color,
    @StringRes titleRes: Int
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconTransaction2(
                    sizeCard = 32.dp,
                    icon = iconRes,
                    colorIcon = iconColor,
                    color = iconBackgroundColor
                )
                Text(stringResource(titleRes), style = text_14, color = marengo)
            }
        }
    }
}