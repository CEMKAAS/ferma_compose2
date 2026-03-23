@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.incubator_project.journal

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_13
import com.zaroslikov.fermacompose2.blue_21
import com.zaroslikov.fermacompose2.blue_4
import com.zaroslikov.fermacompose2.blue_8
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_7
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.orang_10
import com.zaroslikov.fermacompose2.orang_12
import com.zaroslikov.fermacompose2.orang_15
import com.zaroslikov.fermacompose2.orang_17
import com.zaroslikov.fermacompose2.orang_18
import com.zaroslikov.fermacompose2.orang_19
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_20
import com.zaroslikov.fermacompose2.orang_21
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.orang_8
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_12
import com.zaroslikov.fermacompose2.red_13
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.red_4
import com.zaroslikov.fermacompose2.supportFun.toCardColor
import com.zaroslikov.fermacompose2.supportFun.toColor
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.AlertDialog.AlertDialogStandard
import com.zaroslikov.fermacompose2.ui.elements.BigColorCard
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardClips
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.TextField.DropdownMenuEdit
import com.zaroslikov.fermacompose2.ui.elements.TextMiniCard
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_20
import com.zaroslikov.fermacompose2.ui.elements.text_24
import com.zaroslikov.fermacompose2.ui.elements.сompositions.IconTitle
import com.zaroslikov.fermacompose2.ui.elements.сompositions.BaseSlider
import com.zaroslikov.fermacompose2.ui.elements.сompositions.SliderGradient
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard
import com.zaroslikov.fermacompose2.white


object JournalDestination : NavigationDestination {
    override val route = "JournalScreen"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    val routeWithArgs = "$route/{$itemIdPT}"
}

@Composable
fun JournalScreen(
    navigateToEdit: (Long) -> Unit,
    viewModel: JournalViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.journal_screen_title,
                onSettingsClick = { navigateToEdit(state.idPT) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            JournalContainer(
                modifier = Modifier.modifierScreen(innerPadding),
                state = state,
                onIntent = viewModel::onIntent
            )
        if (state.isShowDeleteAlertDialog)
            DeleteBookmarkAlertDialog(
                title = state.choiceNameBookmark,
                onClick = {
                    viewModel.onIntent(JournalIntent.ShowDeleteBookmarkClicked(true))
                },
                onDismissRequest = {
                    viewModel.onIntent(JournalIntent.ShowDeleteBookmarkClicked(false))
                }
            )
        if (state.isShowActiveAlertDialog)
            ActivateBookmarkAlertDialog(
                title = state.choiceNameBookmark,
                onClick = {
                    viewModel.onIntent(JournalIntent.ShowActiveBookmarkClicked(true))
                },
                onDismissRequest = {
                    viewModel.onIntent(JournalIntent.ShowActiveBookmarkClicked(false))
                }
            )
    }
}

@Composable
private fun JournalContainer(
    modifier: Modifier,
    state: JournalState,
    onIntent: (JournalIntent) -> Unit
) {
    Column(
        modifier = modifier.padding(bottom = 15.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        MainCard(
            title = state.domainIncubator.title,
            brand = state.domainIncubator.brand,
            model = state.domainIncubator.model,
            capacity = state.domainIncubator.capacity,
            bookmarkCount = state.domainBookmarkList.size,
            bredEggs = state.domainIncubator.egg,
            hatchedEggs = state.domainIncubator.rejectedEgg,
            percent = state.domainIncubator.percent,
            percentFloat = state.domainIncubator.percentFloat,
            note = state.domainIncubator.note
        )
        StatisticsCard(
            typeList = state.typeList,
            breedList = state.breedList,
            onChoiceClick = { onIntent(JournalIntent.ChoiceTypeClicked(it)) },
            onResetClick = { onIntent(JournalIntent.ResetTypeClicked) }
        )
        HistoryConclusions(
            list = state.domainBookmarkList,
            onClick = { },
            onActiveClick = { onIntent(JournalIntent.ActiveBookmarkClicked(it.first, it.second)) },
            onDeleteClick = { onIntent(JournalIntent.DeleteBookmarkClicked(it.first, it.second)) },
        )
    }
}


@Composable
private fun MainCard(
    title: String,
    brand: String?,
    model: String?,
    capacity: Int,
    bookmarkCount: Int,
    bredEggs: Int,
    hatchedEggs: Int,
    percent: Double,
    percentFloat: Float,
    note: String
) {
    BigColorCard(
        glowColor = blue_4,
        colors = listOf(blue_21, blue_4),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconTransaction2(
                    sizeCard = 56.dp,
                    icon = R.drawable.outline_egg_24,
                    colorIcon = white,
                    color = white.copy(alpha = 0.2f)
                )
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        title,
                        style = text_20,
                        color = white
                    )
                    Text("$brand • $model", style = text_12, color = white.copy(alpha = 0.8f))
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                JournalDoubleCard(
                    iconResOne = R.drawable.outline_egg_24,
                    stringResOne = R.string.add_incubator_screen_capacity,
                    valueOne = capacity.toString(),
                    iconResTwo = R.drawable.baseline_calendar_month_24,
                    stringResTwo = R.string.journal_screen_bookmarks,
                    valueTwo = bookmarkCount.toString()
                )
                if (bookmarkCount > 0)
                    JournalDoubleCard(
                        iconResOne = R.drawable.outline_check_circle_24,
                        stringResOne = R.string.journal_screen_chicks,
                        valueOne = bredEggs.toString(),
                        iconResTwo = R.drawable.outline_cancel_24,
                        stringResTwo = R.string.journal_screen_hatch,
                        valueTwo = hatchedEggs.toString()
                    )
            }
            if (bookmarkCount > 0)
                SliderOverallEfficiency(
                    percent = percent,
                    percentFloat = percentFloat
                )
            if (note.isNotBlank())
                SupportNoteCard(Modifier.fillMaxHeight(), note)
        }
    }
}

@Composable
private fun SliderOverallEfficiency(
    percent: Double,
    percentFloat: Float
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.journal_screen_overall_efficiency),
                style = text_12,
                color = white.copy(alpha = 0.8f)
            )
            Text(
                "${percent.formatNumber()}%",
                style = text_14,
                color = white
            )
        }
        BaseSlider(
            percentFloat = percentFloat,
            color = white
        )
    }
}

@Composable
private fun JournalDoubleCard(
    @DrawableRes iconResOne: Int,
    @StringRes stringResOne: Int,
    valueOne: String,
    @DrawableRes iconResTwo: Int,
    @StringRes stringResTwo: Int,
    valueTwo: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        JournalSupportCard(
            modifier = Modifier.weight(1f),
            iconRes = iconResOne,
            stringRes = stringResOne,
            value = valueOne
        )
        JournalSupportCard(
            modifier = Modifier.weight(1f),
            iconRes = iconResTwo,
            stringRes = stringResTwo,
            value = valueTwo
        )
    }
}

@Composable
private fun JournalSupportCard(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes stringRes: Int,
    value: String
) {
    TranslucentCard(modifier) {
        Column(
            modifier = Modifier.padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = white,
                    modifier = Modifier.size(16.dp)
                )
                Text(stringResource(stringRes), style = text_12, color = white.copy(0.8f))
            }
            Text(value, style = text_24, color = white)
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
fun TranslucentCard(
    modifier: Modifier = Modifier,
    container: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = white.copy(0.1f)
        ),
        border = BorderStroke(1.dp, white.copy(0.2f)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier,
    ) {
        container()
    }
}

@Composable
private fun StatisticsCard(
    typeList: List<TypeUi>,
    breedList: List<BreedUi>,
    onChoiceClick: (TypeEgg) -> Unit,
    onResetClick: () -> Unit
) {
    if (typeList.isNotEmpty())
        CardFieldNew(
            padding = PaddingValues(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconTitle(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.outline_analytics_24,
                        sizeIcon = 32.dp,
                        colorIcon = orang_2,
                        colorBackgroundIcon = orang_8,
                        textTitle = stringResource(R.string.journal_screen_statistics),
                        styleTitle = text_14,
                        colorTitle = black_2,
                        textSup = stringResource(R.string.journal_screen_types_and_breeds),
                        styleSup = text_12,
                        colorSup = gray_7
                    )
                    if (breedList.isNotEmpty())
                        TextButton(onClick = onResetClick) {
                            Text(
                                stringResource(R.string.button_reset),
                                style = text_12,
                                color = orang_2,
                            )
                        }
                }
                StatisticList(
                    title = R.string.journal_screen_types,
                    list = typeList
                ) { type ->
                    SliderStatistics(
                        title = stringResource(type.typeEgg.toResId()),
                        eggCount = type.count,
                        percent = type.percent,
                        percentFloat = type.percentFloat,
                        colors = type.typeEgg.toColor(),
                        cardColor = type.typeEgg.toCardColor(),
                        isBorder = true,
                        isChoice = type.isChoice,
                        onClick = { onChoiceClick(type.typeEgg) }
                    )
                }
                AnimatedVisibility(
                    modifier = Modifier.fillMaxWidth(),
                    visible = breedList.isNotEmpty()
                ) {
                    StatisticList(
                        title = R.string.journal_screen_breeds,
                        list = breedList
                    ) { breed ->
                        SliderStatistics(
                            title = breed.breed,
                            eggCount = breed.count,
                            percent = breed.percent,
                            percentFloat = breed.percentFloat,
                            colors = listOf(orang_10, orang_15),
                        )
                    }
                }
            }
        }
}

@Composable
private fun <T> StatisticList(
    @StringRes title: Int,
    list: List<T>,
    sliderStatistic: @Composable (T) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            stringResource(title),
            style = text_12,
            color = gray_7
        )
        list.forEach { sliderStatistic(it) }
    }
}

@Composable
private fun SliderStatistics(
    title: String,
    eggCount: Int,
    percent: Double,
    percentFloat: Float,
    colors: List<Color>,
    cardColor: Pair<Color, Color> = grey_2 to white,
    isBorder: Boolean = false,
    isChoice: Boolean = false,
    onClick: () -> Unit = {}
) {
    val slider: @Composable () -> Unit = {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_egg_24),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = colors.last()
                    )
                    Text(title, style = text_12, color = black_2)
                    if (isBorder && isChoice)
                        Icon(
                            painter = painterResource(R.drawable.outline_check_circle_24),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = colors.last()
                        )
                }
                Row {
                    Text(
                        "$eggCount ${stringResource(Suffix.PIECES.toResId())}",
                        style = text_12,
                        color = black_2
                    )
                    if (isBorder)
                        Icon(
                            painter = painterResource(
                                if (isChoice) R.drawable.icon_keyboard_arrow_down
                                else R.drawable.baseline_chevron_right_24
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(14.dp),
                            tint = colors.last()
                        )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SliderGradient(
                    modifier = Modifier.weight(1f),
                    percentFloat = percentFloat,
                    colors = colors
                )
                Text(
                    "${percent.formatNumber()}%",
                    style = text_12,
                    color = black_2,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.15f)
                )
            }
        }
    }
    val (containerColor, borderColor) = if (isChoice) cardColor else white to grey_2
    if (isBorder) BorderCard(
        borderColor = borderColor,
        containerColor = containerColor,
        shape = RoundedCornerShape(10.dp),
        padding = PaddingValues(9.dp),
        onClick = onClick
    ) { slider() } else slider()
}

@Composable
private fun HistoryConclusions(
    list: List<BookmarkUi>,
    onClick: () -> Unit,
    onActiveClick: (Pair<Long, String>) -> Unit,
    onDeleteClick: (Pair<Long, String>) -> Unit,
) {
    if (list.isNotEmpty())
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.journal_screen_history_conclusions),
                    style = text_16,
                    color = black_2
                )
                TextMiniCard(
                    value = list.size.toString(),
                    textColor = blue_8,
                    backgroundColor = blue_13
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                list.forEach { history ->
                    HistoryCard(
                        title = history.title,
                        textSup = history.textSup,
                        isEarlyCompletionStatus = history.isEarlyCompletionStatus,
                        percent = history.percent,
                        percentFloat = history.percentFloat,
                        startDate = history.startDate,
                        endDate = history.endDate,
                        egg = history.egg,
                        rejectedEgg = history.rejectedEgg,
                        onClick = onClick,
                        onActiveClick = { onActiveClick(history.id to history.title) },
                        onDeleteClick = { onDeleteClick(history.id to history.title) }
                    )
                }
            }
        }
}

@Composable
private fun HistoryCard(
    title: String,
    textSup: String,
    isEarlyCompletionStatus: Boolean,
    percent: Double,
    percentFloat: Float,
    startDate: String,
    endDate: String,
    egg: Int,
    rejectedEgg: Int,
    onClick: () -> Unit,
    onActiveClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    CardFieldNew(
        padding = PaddingValues(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val (backgroundColor, iconColor, icon) =
                if (isEarlyCompletionStatus)
                    Triple(orang_20, orang_21, R.drawable.outline_info_24)
                else
                    Triple(blue_13, blue_8, R.drawable.outline_check_circle_24)
            IconTitle(
                modifier = Modifier.weight(1f),
                iconRes = icon,
                sizeIcon = 36.dp,
                colorIcon = iconColor,
                colorBackgroundIcon = backgroundColor,
                textTitle = title,
                styleTitle = text_14,
                colorTitle = black_2,
                textSup = textSup,
                styleSup = text_12,
                colorSup = gray_7
            )
            Row(
                modifier = Modifier.weight(0.45f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                CompleteStatus(isEarlyCompletionStatus)
                DropdownMenuEdit(
                    onActiveClick = { onActiveClick() },
                    onDeleteClick = { onDeleteClick() }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconText(
                iconRes = R.drawable.baseline_calendar_month_24,
                value = "$startDate > $endDate",
                iconColor = orang_9,
                textColor = gray_7,
                textStyle = text_12
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconText(
                    iconRes = R.drawable.outline_check_circle_24,
                    value = egg.toString(),
                    iconColor = green_6,
                    textColor = price_green,
                )
                IconText(
                    iconRes = R.drawable.outline_info_24,
                    value = rejectedEgg.toString(),
                    iconColor = red_13,
                    textColor = error_base,
                )
            }
        }
        if (!isEarlyCompletionStatus)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SliderGradient(
                    modifier = Modifier.weight(1f),
                    percentFloat = percentFloat,
                    colors = listOf(green_7, price_green)
                )
                Text(
                    "${percent.formatNumber()}%",
                    style = text_12,
                    color = black_2,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(0.15f)
                )
            }
    }
}

@Composable
private fun CompleteStatus(
    isEarlyCompletionStatus: Boolean
) {
    val (backgroundColor, borderColor) = if (isEarlyCompletionStatus) orang_20 to orang_19 else blue_13 to blue_9
    val (textColor, statusText) = if (isEarlyCompletionStatus) orang_21 to R.string.journal_screen_early_completed
    else blue_8 to R.string.journal_screen_completed
    CardClips(
        colorBackground = backgroundColor,
        colorBorder = borderColor,
        colorText = textColor,
        value = stringResource(statusText)
    )
}

@Composable
private fun IconText(
    @DrawableRes iconRes: Int,
    value: String,
    iconColor: Color,
    textColor: Color,
    textStyle: TextStyle = text_14
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(14.dp)
        )
        Text(value, style = textStyle, color = textColor)
    }
}

@Composable
private fun ActivateBookmarkAlertDialog(
    title: String,
    onClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialogStandard(
        titleRes = R.string.journal_screen_active_bookmark,
        iconRes = R.drawable.outline_info_24,
        titleBackgroundColor = orang_4,
        onDismissRequest = onDismissRequest,
        textButtonRes = R.string.button_confirm,
        onClick = onClick,
        colors = listOf(orang_9, orang_15)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.journal_screen_activate_this_bookmark_now_s).format(title),
                color = dark,
                style = text_16
            )
            WarningCard(
                colorBackground = red_11,
                colorBorder = red_12,
                colorIcon = error_base,
                colorTitle = orang_17,
                colorText = red_14,
                icon = R.drawable.outline_info_24,
                title = R.string.journal_screen_activate_bookmark_end,
                text = R.string.journal_screen_activate_bookmark_end_sup,
            )
            WarningCard(
                colorBackground = orang_4,
                colorBorder = orang_8,
                colorIcon = orang_12,
                colorTitle = orang_18,
                colorText = orang_6,
                icon = R.drawable.baseline_calendar_month_24,
                title = R.string.journal_screen_new_date,
                text = R.string.journal_screen_new_date_sup
            )
        }
    }
}


@Composable
private fun DeleteBookmarkAlertDialog(
    title: String,
    onClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialogStandard(
        titleRes = R.string.journal_screen_delete_bookmark,
        iconRes = R.drawable.baseline_delete_24,
        titleBackgroundColor = red_11,
        onDismissRequest = onDismissRequest,
        textButtonRes = R.string.button_delete,
        onClick = onClick,
        colors = listOf(red_13, red_4)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.journal_screen_delete_this_bookmark_now_s).format(title),
                color = dark,
                style = text_16
            )
            WarningCard(
                colorBackground = red_11,
                colorBorder = red_12,
                colorIcon = error_base,
                colorTitle = orang_17,
                colorText = red_14,
                icon = R.drawable.outline_info_24,
                title = R.string.journal_screen_delete_title,
                text = R.string.journal_screen_delete_title_sup,
            )
        }
    }
}