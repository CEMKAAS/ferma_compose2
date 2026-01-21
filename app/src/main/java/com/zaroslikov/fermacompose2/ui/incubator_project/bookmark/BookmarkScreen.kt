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
import androidx.compose.material3.Button
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
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_13
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_11
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.green_g_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_15
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_12
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.red_15
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.elements.BigColorCard
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.IconAndTextNew
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseOutlinedTextNew3
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_20
import com.zaroslikov.fermacompose2.ui.elements.text_30
import com.zaroslikov.fermacompose2.ui.elements.сompositions.Slider
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard.NoteWidget
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_3
import com.zaroslikov.fermacompose2.violet_5
import com.zaroslikov.fermacompose2.violet_6
import com.zaroslikov.fermacompose2.violet_8
import com.zaroslikov.fermacompose2.white


object BookmarkDestination : NavigationDestination {
    override val route = "BookmarkScreen"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun BookmarkScreen(
    onClick: () -> Unit,
    viewModel: BookmarkViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val isEmptyBookmark = false

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.add_incubator_screen_title,
                onNavigateBackClick = { },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            if (isEmptyBookmark)
                BookmarkContainer(
                    modifier = Modifier.modifierScreen(innerPadding),
                    state = state,
                )
            else EmptyBookmark(
                modifier = Modifier.modifierScreen(innerPadding),
                onClick = onClick
            )
    }
}

@Composable
private fun BookmarkContainer(
    modifier: Modifier,
    state: BookmarkState
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.End
    ) {
        MainCard(
            currentDay = "19",
            endDay = "21",
            percent = "2",
            percentFloat = 1f,
            egg = 1,
            dateBegin = dateToday(),
            dateEnd = dateToday()
        )
        CurrentParameters("19")
        NoteWidget(intRes = R.string.bookmark_screen_current_note, note = "") { }
        TomorrowParameters(containerColor = white)
    }
}

@Composable
private fun EmptyBookmark(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Сейчас нет активных закладок, добавить?")
        Button(onClick = onClick) {
            Text("Добавить")
        }
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
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    stringResource(R.string.book_mark_screen_day_incubator),
                    style = text_14,
                    color = white.copy(alpha = 0.8f)
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
                    color = white.copy(alpha = 0.8f)
                )
                Text("$percent%", style = text_14, color = white.copy(alpha = 0.8f))
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
private fun CurrentParameters(
    currentDay: String
) {
    CardFieldNew(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CurrentParameter(
                    modifier = Modifier.weight(1f),
                    containerColor = red_11,
                    iconRes = R.drawable.baseline_add_card_24,
                    iconColor = red_14,
                    iconBackgroundColor = red_15,
                    titleRes = R.string.bookmark_screen_current_temp
                )
                CurrentParameter(
                    modifier = Modifier.weight(1f),
                    containerColor = blue_3,
                    iconRes = R.drawable.baseline_add_card_24,
                    iconColor = blue_8,
                    iconBackgroundColor = blue_9,
                    titleRes = R.string.bookmark_screen_current_damp
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CurrentParameter(
                    modifier = Modifier.weight(1f),
                    containerColor = violet_3,
                    iconRes = R.drawable.baseline_add_card_24,
                    iconColor = violet_6,
                    iconBackgroundColor = violet_5,
                    titleRes = R.string.bookmark_screen_current_over
                )
                CurrentParameter(
                    modifier = Modifier.weight(1f),
                    containerColor = green_g_2,
                    iconRes = R.drawable.baseline_add_card_24,
                    iconColor = green_9,
                    iconBackgroundColor = green_11,
                    titleRes = R.string.bookmark_screen_current_airing
                )
            }
        }
    }
}

@Composable
private fun CurrentParameter(
    modifier: Modifier = Modifier,
    containerColor: Color,
    @DrawableRes iconRes: Int,
    iconColor: Color,
    iconBackgroundColor: Color,
    @StringRes titleRes: Int
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
            BaseOutlinedTextNew3(
                value = "23,4",
                onValueChange = {},
                intResSup = R.string.is_empty,
                dividerColor = iconBackgroundColor
            )
            Text(text = "Норма: 37-38°C", color = gray_7, style = text_12)
        }
    }
}

@Composable
private fun TomorrowParameters(
    modifier: Modifier = Modifier,
    containerColor: Color,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ColumnParameter(
                value = "35",
                suffix = "Раз",
                iconRes = R.drawable.baseline_add_card_24,
                iconColor = error_base,
                iconBackgroundColor = red_12
            )
            ColumnParameter(
                value = "35",
                suffix = "Раз",
                iconRes = R.drawable.baseline_add_card_24,
                iconColor = blue_1,
                iconBackgroundColor = blue_13
            )
            ColumnParameter(
                value = "35",
                suffix = "Раз",
                iconRes = R.drawable.baseline_add_card_24,
                iconColor = violet_1,
                iconBackgroundColor = violet_8
            )
            ColumnParameter(
                value = "35",
                suffix = "Раз",
                iconRes = R.drawable.baseline_add_card_24,
                iconColor = price_green,
                iconBackgroundColor = green_8
            )
        }
    }
}

@Composable
private fun ColumnParameter(
    modifier: Modifier = Modifier,
    value: String,
    suffix: String,
    @DrawableRes iconRes: Int,
    iconColor: Color,
    iconBackgroundColor: Color
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconTransaction2(
            sizeCard = 36.dp,
            icon = iconRes,
            colorIcon = iconColor,
            color = iconBackgroundColor
        )
        Text(text = value, style = text_20, color = black_2)
        Text(text = suffix, style = text_16, color = gray_7)
    }
}

