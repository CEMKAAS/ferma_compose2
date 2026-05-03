package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_11
import com.zaroslikov.fermacompose2.green_13
import com.zaroslikov.fermacompose2.green_5
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.green_g_5
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_15
import com.zaroslikov.fermacompose2.orang_17
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_13
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.red_15
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedNumberNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.textBold_28
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_18
import com.zaroslikov.fermacompose2.ui.elements.text_24
import com.zaroslikov.fermacompose2.ui.elements.сompositions.SliderGradient
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry.IconToggle
import com.zaroslikov.fermacompose2.white
import kotlin.collections.forEach

@Composable
fun CompleteIncubationBottomSheet(
    chicksBred: String,
    chicksPrice: String,
    allEgg: Int,
    rejectedEgg: Int,
    nameBookmark: String,
    isChoiceProjectMode: Boolean?,
    currentEgg: Int,
    enabled: Boolean,
    enabledTwo: Boolean,
    newNameProject: String,
    indexProject: Long,
    precent: Double,
    percentFloat: Float,
    isErrorCompleted: Boolean,
    currencySuffix: Suffix,
    projectList: List<DomainProjectTable>,
    onIntent: (BookmarkIntent) -> Unit
) {
    BaseBottomSheet(
        skipPartiallyExpanded = true,
        title = stringResource(R.string.bookmark_screen_сomplete_incubation),
        onDismissRequest = { onIntent(BookmarkIntent.OpenCompleteIncubationBottomSheetClick(false)) },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BorderCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = orang_5,
                containerColor = orang_4,
                padding = PaddingValues(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    IconTransaction2(
                        sizeCard = 48.dp,
                        icon = R.drawable.outline_egg_24,
                        iconColor = orang_6,
                        boxColor = orang_5
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(nameBookmark, style = text_14, color = marengo)
                        Text(
                            stringResource(R.string.bookmark_screen_s_eggs_laid).format(currentEgg),
                            style = text_18,
                            color = black_2
                        )
                    }
                }
            }
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = chicksBred.isNotBlank()
            ) {
                BorderCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = green_11,
                    containerColor = price_green_2,
                    padding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_check_circle_24),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = green_9
                            )
                            Text(
                                stringResource(R.string.bookmark_screen_egg_success),
                                style = text_14,
                                color = green_13
                            )
                        }
                        Text("${precent.formatNumber()}%", style = textBold_28, color = green_9)
                    }
                    SliderGradient(
                        percentFloat = percentFloat,
                        colors = listOf(green_6, green_5)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        IconText(
                            stringRes = R.string.bookmark_screen_toggle_all_egg,
                            iconRes = R.drawable.outline_egg_24,
                            iconColor = orang_2,
                            value = allEgg.toString()
                        )
                        IconText(
                            stringRes = R.string.bookmark_screen_chicks_been_bred,
                            iconRes = R.drawable.outline_raven_24,
                            iconColor = green_g_5,
                            value = chicksBred
                        )
                        IconText(
                            stringRes = R.string.bookmark_screen_rejected_egg,
                            iconRes = R.drawable.outline_cancel_24,
                            iconColor = gray_7,
                            value = rejectedEgg.toString()
                        )
                    }
                }
            }
            OutlinedNumberNew(
                value = chicksBred,
                onValueChange = { onIntent(BookmarkIntent.ChicksBredChanged(it)) },
                intRes = R.string.bookmark_screen_chicks_been_bred,
                intResSup = R.string.bookmark_screen_support_count_chicks,
                intResError = R.string.entry_bookmark_error_reject_count,
                isError = isErrorCompleted,
                drawableRes = R.drawable.outline_check_circle_24
            )
            OutlinedNumberNew(
                value = chicksPrice,
                onValueChange = { onIntent(BookmarkIntent.ChicksPriceChanged(it)) },
                intRes = R.string.bookmark_screen_price_chick,
                intResSup = R.string.bookmark_screen_support_price_chick,
                drawableRes = R.drawable.icon_money,
                suffix = currencySuffix
            )
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible = enabled
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            stringResource(R.string.bookmark_screen_where_add_chicks),
                            style = text_16,
                            color = black_2
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Cards(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                enabled = isChoiceProjectMode == true,
                                iconRes = R.drawable.icon_add,
                                stringRes = R.string.bookmark_screen_new_project
                            ) {
                                onIntent(BookmarkIntent.ChoiceProjectModeClick(true))
                            }
                            if (projectList.isNotEmpty())
                                Cards(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    enabled = isChoiceProjectMode == false,
                                    iconRes = R.drawable.outline_work_24,
                                    stringRes = R.string.bookmark_screen_existing_project
                                ) {
                                    onIntent(BookmarkIntent.ChoiceProjectModeClick(false))
                                }
                        }
                    }
                    when (isChoiceProjectMode) {
                        true -> OutlinedTextNew(
                            value = newNameProject,
                            onValueChange = { onIntent(BookmarkIntent.NameProjectChanged(it)) },
                            labelIntRes = R.string.warehouse_edit_screen_name_new_project,
                            supportingText = R.string.warehouse_edit_screen_name_project_support
                        )

                        false -> {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    stringResource(R.string.bookmark_screen_choice_project),
                                    style = text_14,
                                    color = dark
                                )
                                projectList.forEach { projectTable ->
                                    ProjectCard(
                                        indexProject == projectTable.id,
                                        nameProject = projectTable.title,
                                        dateBegin = projectTable.date
                                    ) {
                                        onIntent(BookmarkIntent.IndexChoiceProjectClick(projectTable.id))
                                    }
                                }
                            }
                        }

                        null -> {}
                    }
                }
            }
            BottomPanel(
                enabled = enabled && enabledTwo,
                onCloseClick = {
                    onIntent(
                        BookmarkIntent.OpenCompleteIncubationBottomSheetClick(
                            false
                        )
                    )
                },
                onSaveClick = { onIntent(BookmarkIntent.CompleteIncubatorClick) }
            )
        }
    }
}

@Composable
private fun IconText(
    @StringRes stringRes: Int,
    @DrawableRes iconRes: Int,
    iconColor: Color,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconToggle(
            stringRes = stringRes,
            iconRes = iconRes,
            iconColor = iconColor
        )
        Text(value, style = text_12, color = iconColor)
    }
}


@Composable
fun EarlyCompleteIncubationBottomSheet(
    currentDay: Int,
    daysToEnd: Int,
    reasonNote: String,
    onIntent: (BookmarkIntent) -> Unit
) {
    BaseBottomSheet(
        skipPartiallyExpanded = true,
        title = stringResource(R.string.bookmark_screen_early_completion_incubation),
        onDismissRequest = { onIntent(BookmarkIntent.OpenCompleteIncubationBottomSheetClick(false)) },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BorderCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = red_15,
                containerColor = red_11,
                padding = PaddingValues(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        IconTransaction2(
                            sizeCard = 48.dp,
                            icon = R.drawable.icon_warning,
                            iconColor = red_14,
                            boxColor = red_15
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                stringResource(R.string.animal_count_screen_warning_title),
                                style = text_18,
                                color = orang_17
                            )
                            Text(
                                stringResource(R.string.bookmark_screen_incubation_end_early),
                                style = text_14,
                                color = red_14
                            )
                        }
                    }
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = white
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Rowww(
                                stringRes = R.string.bookmark_screen_current_day,
                                value = currentDay.toString()
                            )
                            Rowww(
                                stringRes = R.string.bookmark_screen_days_to_end,
                                value = daysToEnd.toString(),
                                style = text_18,
                                color = black_2
                            )
                        }
                    }
                }
            }
            OutlinedTextNoteNew(
                value = reasonNote,
                onValueChange = { onIntent(BookmarkIntent.ReasonNoteChanged(it)) },
                labelIntRes = R.string.bookmark_screen_reason_incubation_end_early,
                supportingText = R.string.bookmark_screen_reason_sup_incubation_end_early,
            )
            BottomPanel(
                enabled = true,
                colors = listOf(orang_15, red_13),
                iconRes = R.drawable.outline_cancel_24,
                onCloseClick = {
                    onIntent(BookmarkIntent.OpenCompleteIncubationBottomSheetClick(false))
                },
                onSaveClick = { onIntent(BookmarkIntent.EarlyCompleteIncubatorClick) }
            )
        }
    }
}

@Composable
private fun Rowww(
    value: String,
    @StringRes stringRes: Int,
    style: TextStyle = text_24.copy(fontWeight = FontWeight.Bold),
    color: Color = red_14
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            stringResource(stringRes),
            style = text_14,
            color = marengo
        )
        Text(
            value,
            style = style,
            color = color
        )
    }
}

@Composable
private fun ProjectCard(
    enabled: Boolean,
    nameProject: String,
    dateBegin: String,
    onClick: () -> Unit
) {
    val (borderColor, containerColor) = if (enabled) green_6 to price_green_2 else grey_2 to white
    val (iconBackgroundColor, iconColor) = if (enabled) green_11 to green_9 else gray_6 to marengo

    BorderCard(
        padding = PaddingValues(17.dp),
        onClick = onClick,
        borderColor = borderColor,
        containerColor = containerColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                IconTransaction2(
                    sizeCard = 32.dp,
                    icon = R.drawable.outline_work_24,
                    iconColor = iconColor,
                    boxColor = iconBackgroundColor
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(nameProject, style = text_16, color = black_2)
                    Text(dateBegin, style = text_12, color = gray_7)
                }
            }
            if (enabled)
                Icon(
                    painter = painterResource(R.drawable.outline_check_circle_24),
                    contentDescription = null,
                    tint = price_green,
                    modifier = Modifier.size(20.dp)
                )
        }
    }
}

@Composable
private fun Cards(
    modifier: Modifier = Modifier,
    enabled: Boolean?,
    @DrawableRes iconRes: Int,
    @StringRes stringRes: Int,
    onClick: () -> Unit
) {
    val (borderColor, iconColor, containerColor) = if (enabled == true) Triple(
        green_6, price_green, price_green_2
    ) else Triple(
        grey_2, grey, white
    )

    BorderCard(
        modifier = modifier,
        padding = PaddingValues(17.dp),
        onClick = onClick,
        borderColor = borderColor,
        containerColor = containerColor
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = iconColor
            )
            Text(
                stringResource(stringRes),
                style = text_14,
                color = marengo
            )
        }
    }
}