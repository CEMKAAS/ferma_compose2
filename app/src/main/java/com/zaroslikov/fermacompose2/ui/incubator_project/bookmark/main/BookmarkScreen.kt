@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.main

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.TypeEgg
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
import com.zaroslikov.fermacompose2.green_12
import com.zaroslikov.fermacompose2.green_13
import com.zaroslikov.fermacompose2.green_5
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.green_g_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_12
import com.zaroslikov.fermacompose2.orang_15
import com.zaroslikov.fermacompose2.orang_16
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_8
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_12
import com.zaroslikov.fermacompose2.red_13
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.red_15
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BigButton
import com.zaroslikov.fermacompose2.ui.elements.BigColorCard
import com.zaroslikov.fermacompose2.ui.elements.BorderButton
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.EmptyBookmark
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.NeonGlowFab
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseOutlinedTextNew3
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_18
import com.zaroslikov.fermacompose2.ui.elements.text_20
import com.zaroslikov.fermacompose2.ui.elements.text_24
import com.zaroslikov.fermacompose2.ui.elements.text_36
import com.zaroslikov.fermacompose2.ui.elements.сompositions.BaseSlider
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.elements.AdsCard
import com.zaroslikov.fermacompose2.ui.incubator_project.journal.TranslucentCard
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard.NoteWidget
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_10
import com.zaroslikov.fermacompose2.violet_11
import com.zaroslikov.fermacompose2.violet_12
import com.zaroslikov.fermacompose2.violet_13
import com.zaroslikov.fermacompose2.violet_14
import com.zaroslikov.fermacompose2.violet_15
import com.zaroslikov.fermacompose2.violet_3
import com.zaroslikov.fermacompose2.violet_5
import com.zaroslikov.fermacompose2.violet_6
import com.zaroslikov.fermacompose2.violet_8
import com.zaroslikov.fermacompose2.white


object BookmarkDestination : NavigationDestination {
    override val route = "BookmarkScreen"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    val routeWithArgs = "$route/{$itemIdPT}"
}

@Composable
fun BookmarkScreen(
    onClick: (Long) -> Unit,
    onSettingsClick: (Pair<Long, Long>) -> Unit,
    navigateToBack: () -> Unit,
    viewModel: BookmarkViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                title = state.domainBookmark.title.ifBlank { stringResource(R.string.bookmark_screen_title) },
                onNavigateBackClick = navigateToBack,
                onSettingsClick = if (!state.isActivityBookmark || state.isArchive) null
                else {
                    {
                        state.idBookmark?.let { onSettingsClick(state.incubatorId to it) }
                    }
                },
                scrollBehavior = if (!state.isActivityBookmark) null else scrollBehavior
            )
        },
        floatingActionButton = {
            if (!state.isActivityBookmark && !state.isArchive)
                NeonGlowFab(colors = listOf(orang_9, orang_15)) {
                    onClick(state.incubatorId)
                }
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            if (state.isActivityBookmark)
                BookmarkContainer(
                    modifier = Modifier.modifierScreen(innerPadding),
                    state = state,
                    onIntent = viewModel::onIntent
                )
            else EmptyBookmark(
                modifier = Modifier.padding(innerPadding),
                iconRes = R.drawable.outline_egg_24,
                title = R.string.message_no_data_title_bookmark,
                supportText = R.string.message_no_data_message_bookmark,
                supportSecondText = if (state.isArchive) R.string.message_no_data_message_archive else null,
                iconColor = orang_2,
                backgroundColor = orang_8,
                plusColor = orang_12
            )
        Log.i("bookmark", "BookmarkScreen: ${state.isArchive}")
        if (state.isOpenBottomSheet)
            ParametersBottomSheet(
                currentDay = state.currentDay,
                parameterDayList = state.parameterDayList,
                isAutoAiring = state.domainBookmark.isAutoVentilation,
                isAutoOver = state.domainBookmark.isAutoRotation,
                onIntent = viewModel::onIntent
            )
        if (state.isOpenEditBottomSheet)
            EditParametersBottomSheet(
                state = state.editParameterDay,
                isAutoAiring = state.domainBookmark.isAutoVentilation,
                isAutoOver = state.domainBookmark.isAutoRotation,
                onIntent = viewModel::onIntent
            )
        if (state.isOpenOvoscopBottomSheet)
            OvoscopBottomSheet(
                isLantern = state.isLantern,
                ovoscopyState = state.currentParameterDay.ovoscopyState,
                onIntent = viewModel::onIntent
            )
        if (state.isOpenOvoscopEndBottomSheet)
            OvoscopEndBottomSheet(
                currentEgg = state.currentEgg,
                rejectedEgg = state.rejectedEgg,
                enabled = state.enabledOvoscopyButton(),
                onIntent = viewModel::onIntent
            )
        if (state.isOpenCompleteIncubationBottomSheet)
            CompleteIncubationBottomSheet(
                currentEgg = state.currentEgg,
                allEgg = state.domainBookmark.count,
                nameBookmark = state.domainBookmark.title,
                projectList = state.completeState.projectList,
                chicksBred = state.completeState.chicksBred,
                newNameProject = state.completeState.newNameProject,
                isChoiceProjectMode = state.completeState.isChoiceProjectMode,
                indexProject = state.completeState.indexProject,
                precent = state.completeState.precentCompleted,
                percentFloat = state.completeState.precentFloatCompleted,
                isErrorCompleted = state.completeState.isErrorCompleted,
                rejectedEgg = state.completeState.rejectedEggCompleted,
                enabled = state.completeState.isEnabledCompleteButton,
                enabledTwo = state.completeState.isEnabledCompleteButtonTwo,
                onIntent = viewModel::onIntent,
                chicksPrice = state.completeState.chicksPrice,
                currencySuffix = state.currencySuffix
            )
        if (state.isOpenEarlyCompleteIncubationBottomSheet)
            EarlyCompleteIncubationBottomSheet(
                currentDay = state.currentDay,
                daysToEnd = state.daysToEnd,
                reasonNote = state.reasonNote,
                onIntent = viewModel::onIntent,
            )
    }
}

@Composable
private fun BookmarkContainer(
    modifier: Modifier,
    state: BookmarkState,
    onIntent: (BookmarkIntent) -> Unit
) {
    Column(
        modifier = modifier.padding(bottom = 15.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.End
    ) {
        MainCard(
            currentDay = state.currentDay,
            typeEgg = state.domainBookmark.type,
            breed = state.domainBookmark.breed,
            numberDays = state.numberDays,
            daysToEnd = state.daysToEnd,
            percent = state.percent,
            percentFloat = state.percentFloat,
            egg = state.currentEgg,
            dateBegin = state.startDay,
            dateEnd = state.endDay,
            time = state.domainBookmark.time,
            note = state.domainBookmark.note
        )
        if (!state.isBookmarkCompleted) {
            CurrentParameters(
                currentDay = state.currentDay,
                domain = state.currentParameterDay,
                onIntent = onIntent,
                isAutoAiring = state.domainBookmark.isAutoVentilation,
                isAutoOver = state.domainBookmark.isAutoRotation,
            )
            NoteWidget(
                intRes = R.string.bookmark_screen_current_note,
                isArchive = false,
                note = state.currentParameterDay.note
            ) {
                onIntent(BookmarkIntent.NoteChanged(it))
            }
            if (state.currentParameterDay.ovoscopyState.isOvoscopyDay)
                OvoscopCard(state.currentParameterDay.ovoscopyState.supportText) {
                    onIntent(BookmarkIntent.OpenOvoscopBottomSheetClick(true))
                }
            AdsCard()
            if (!state.isCompleteModeEnd)
                TomorrowParameters(
                    isAutoAiring = state.domainBookmark.isAutoVentilation,
                    isAutoOver = state.domainBookmark.isAutoRotation,
                    domain = state.tomorrowParameterDay
                )
        } else CompleteCard()
        GradientButton(
            modifier = Modifier.fillMaxWidth(),
            colors = if (state.isCompleteModeEnd) listOf(green_6, green_5)
            else listOf(orang_15, red_13),
            text = stringResource(R.string.bookmark_screen_сomplete_incubation),
            prefixIconRes = R.drawable.icon_check,
            isShadow = true,
            paddingValues = PaddingValues(vertical = 14.dp)
        ) {
            onIntent(BookmarkIntent.OpenCompleteIncubationBottomSheetClick(true))
        }
    }
}


@Composable
private fun MainCard(
    currentDay: Int,
    typeEgg: TypeEgg,
    breed: String?,
    numberDays: Int,
    daysToEnd: Int,
    percent: Double,
    percentFloat: Float,
    egg: Int,
    dateBegin: String,
    dateEnd: String,
    time: String,
    note: String
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
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    stringResource(typeEgg.toResId()),
                    style = text_14,
                    color = white.copy(alpha = 0.9f)
                )
                breed?.let {
                    Text(
                        it,
                        style = text_14,
                        color = white.copy(alpha = 0.6f)
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    stringResource(R.string.book_mark_screen_day_incubator),
                    style = text_14,
                    color = white.copy(alpha = 0.8f)
                )
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        currentDay.toString(),
                        style = text_36.copy(fontWeight = FontWeight.Bold),
                        color = white,
                    )
                    Text("/${numberDays}", style = text_24, color = white.copy(0.6f))
                }
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
                Text(
                    "${percent.formatNumber()}%",
                    style = text_14,
                    color = white.copy(alpha = 0.8f)
                )
            }
            BaseSlider(
                percentFloat = percentFloat,
                color = white
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SupportCard(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.outline_egg_24,
                        value = egg.toString(),
                        supText = stringResource(R.string.bookmark_screen_eggs)
                    )
                    SupportCard(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.baseline_access_time_24,
                        value = daysToEnd.toString(),
                        supText = stringResource(R.string.bookmark_screen_before_withdrawal)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SupportCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        iconRes = R.drawable.baseline_calendar_month_24,
                        value = dateBegin,
                        time = time,
                        supText = stringResource(R.string.bookmark_screen_mortgaged)
                    )
                    SupportCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        iconRes = R.drawable.baseline_event_24,
                        value = dateEnd,
                        time = time,
                        supText = stringResource(R.string.bookmark_screen_waiting_conclusion)
                    )
                }
            }
            if (note.isNotBlank())
                SupportNoteCard(value = note)
        }
    }
}

@Composable
private fun SupportCard(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    value: String,
    time: String? = null,
    supText: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = white.copy(0.1f)
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = white,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(value, style = text_18, color = white)
                time?.let {
                    Text(it, style = text_12, color = white.copy(0.6f))
                }
                Text(supText, style = text_12, color = white.copy(0.6f))
            }
        }
    }
}

@Composable
private fun SupportNoteCard(
    modifier: Modifier = Modifier,
    value: String,
) {
    TranslucentCard(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_sticky_note_2_24),
                contentDescription = null,
                tint = white,
                modifier = Modifier.size(16.dp)
            )
            Text(value, style = text_12, color = white)
        }
    }
}

@Composable
private fun CurrentParameters(
    currentDay: Int,
    isAutoAiring: Boolean,
    isAutoOver: Boolean,
    domain: ParametersIncubatorUi,
    onIntent: (BookmarkIntent) -> Unit
) {
    Column(
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
            BorderButton(
                text = stringResource(R.string.bookmark_screen_schedule),
                iconRes = R.drawable.baseline_calendar_month_24,
                borderColor = orang_5,
                textColor = orang_12,
                iconColor = orang_12,
                paddingValues = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp),
            ) { onIntent(BookmarkIntent.OpenBottomSheetClick(true)) }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CurrentParameter(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    value = domain.tempFact,
                    onValueChange = { onIntent(BookmarkIntent.TempChanged(it)) },
                    standardValue = domain.temp,
                    containerColor = red_11,
                    iconRes = R.drawable.outline_device_thermostat_24,
                    iconColor = red_14,
                    iconBackgroundColor = red_15,
                    titleRes = R.string.bookmark_screen_current_temp,
                    suffix = "°C"
                )
                CurrentParameter(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    value = domain.dampFact,
                    onValueChange = { onIntent(BookmarkIntent.DampChanged(it)) },
                    standardValue = domain.damp,
                    containerColor = blue_3,
                    iconRes = R.drawable.outline_dew_point_24,
                    iconColor = blue_8,
                    iconBackgroundColor = blue_9,
                    titleRes = R.string.bookmark_screen_current_damp,
                    suffix = "%"
                )
            }
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CurrentParameter(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    value = domain.overFact,
                    onValueChange = { onIntent(BookmarkIntent.OverChanged(it)) },
                    standardValue = domain.over,
                    containerColor = violet_3,
                    iconRes = R.drawable.outline_cached_24,
                    iconColor = violet_6,
                    iconBackgroundColor = violet_5,
                    titleRes = R.string.bookmark_screen_current_over,
                    isAuto = isAutoOver,
                    suffix = "раза/день"
                )
                CurrentParameter(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    value = domain.airingFact,
                    onValueChange = { onIntent(BookmarkIntent.AiringChanged(it)) },
                    standardValue = domain.airing,
                    containerColor = green_g_2,
                    iconRes = R.drawable.outline_air_24,
                    iconColor = green_9,
                    iconBackgroundColor = green_11,
                    titleRes = R.string.bookmark_screen_current_airing,
                    isAuto = isAutoAiring,
                    suffix = "минут"
                )
            }
        }
    }
}

@Composable
private fun CurrentParameter(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    standardValue: String,
    suffix: String,
    containerColor: Color,
    @DrawableRes iconRes: Int,
    iconColor: Color,
    iconBackgroundColor: Color,
    @StringRes titleRes: Int,
    isAuto: Boolean = false,
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
                    iconColor = iconColor,
                    boxColor = iconBackgroundColor
                )
                Text(stringResource(titleRes), style = text_14, color = marengo)
            }
            BaseOutlinedTextNew3(
                value = value,
                onValueChange = onValueChange,
                intResSup = R.string.is_empty,
                dividerColor = iconBackgroundColor,
                readOnly = isAuto
            )
            Text(
                text = stringResource(R.string.bookmark_screen_current_standard)
                        + " $standardValue $suffix",
                color = gray_7,
                style = text_12
            )
        }
    }
}

@Composable
private fun OvoscopCard(
    ovoscopText: String,
    onClick: () -> Unit
) {
    BigColorCard(
        glowColor = violet_14,
        colors = listOf(violet_13, violet_14),
        padding = PaddingValues(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconTransaction2(
                    sizeCard = 48.dp,
                    icon = R.drawable.ic_ovos2,
                    iconColor = white,
                    boxColor = white.copy(alpha = 0.2f)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        stringResource(R.string.bookmark_screen_today_ovoscopy),
                        style = text_18,
                        color = white
                    )
                    Text(ovoscopText, style = text_14, color = white.copy(alpha = 0.8f))
                }
            }
            BigButton(
                shape = RoundedCornerShape(8.dp),
                icon = R.drawable.ic_ovos2,
                text = R.string.bookmark_screen_begin_ovoscopy,
                textColor = violet_15,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun TomorrowParameters(
    modifier: Modifier = Modifier,
    isAutoAiring: Boolean,
    isAutoOver: Boolean,
    domain: ParametersIncubatorUi,
) {
    val autoString = stringResource(R.string.entry_bookmark_auto)
    CardFieldNew(
        modifier = modifier.fillMaxWidth(),
        padding = PaddingValues(vertical = 20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                stringResource(R.string.bookmark_screen_tomorrow_days_s).format(domain.day),
                modifier = Modifier.padding(horizontal = 20.dp),
                style = text_16,
                color = black_2
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ColumnParameter(
                    value = domain.temp,
                    suffix = "°C",
                    iconRes = R.drawable.outline_device_thermostat_24,
                    iconColor = error_base,
                    iconBackgroundColor = red_12
                )
                ColumnParameter(
                    value = domain.damp,
                    suffix = "%",
                    iconRes = R.drawable.outline_dew_point_24,
                    iconColor = blue_1,
                    iconBackgroundColor = blue_13
                )
                ColumnParameter(
                    value = if (isAutoOver) autoString else domain.over,
                    suffix = if (isAutoOver) null else "раза/день",
                    iconRes = R.drawable.outline_cached_24,
                    iconColor = violet_1,
                    iconBackgroundColor = violet_8
                )
                ColumnParameter(
                    value = if (isAutoAiring) autoString else domain.airing,
                    suffix = if (isAutoAiring) null else "минут",
                    iconRes = R.drawable.outline_air_24,
                    iconColor = price_green,
                    iconBackgroundColor = green_8
                )
            }
            if (domain.note.isNotBlank())
                BorderCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    containerColor = orang_4,
                    padding = PaddingValues(12.dp),
                    shape = RoundedCornerShape(10.dp),
                    borderColor = orang_5
                ) {
                    Text(text = domain.note, style = text_12, color = orang_16)
                }
            if (domain.ovoscopyState.isOvoscopyDay)
                BorderCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    containerColor = violet_10,
                    padding = PaddingValues(12.dp),
                    shape = RoundedCornerShape(10.dp),
                    borderColor = violet_11
                ) {
                    Text(
                        text = domain.ovoscopyState.supportText,
                        style = text_12,
                        color = violet_12
                    )
                }
        }
    }
}

@Composable
private fun ColumnParameter(
    modifier: Modifier = Modifier,
    value: String,
    suffix: String? = null,
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
            iconColor = iconColor,
            boxColor = iconBackgroundColor
        )
        Text(text = value, style = text_20, color = black_2)
        suffix?.let {
            Text(text = it, style = text_12, color = gray_7)
        }
    }
}

@Composable
private fun CompleteCard() {
    BorderCard(
        containerColor = price_green_2,
        borderColor = green_12
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTransaction2(
                sizeCard = 52.dp,
                icon = R.drawable.outline_check_circle_24,
                iconColor = green_9,
                boxColor = green_11
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    stringResource(R.string.bookmark_screen_complete),
                    style = text_18,
                    color = green_13
                )
                Text(
                    stringResource(R.string.bookmark_screen_complete_support),
                    style = text_14,
                    color = green_9
                )
            }
        }
    }
}

@Composable
fun BottomPanel(
    enabled: Boolean,
    colors: List<Color> = listOf(green_6, green_5),
    @StringRes stringRes: Int = R.string.button_сomplete,
    @DrawableRes iconRes: Int = R.drawable.outline_check_circle_24,
    onCloseClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        BorderButton(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = white,
            text = stringResource(R.string.button_cancel),
            onClick = onCloseClick
        )
        GradientButton(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = colors,
            text = stringResource(stringRes),
            prefixIconRes = iconRes,
            enabled = enabled,
            onClick = onSaveClick
        )
    }
}