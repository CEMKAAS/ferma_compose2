@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.table.DomainBookmark
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_4
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_11
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.orang_8
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.red_13
import com.zaroslikov.fermacompose2.ui.elements.BorderButton
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.ButtonForGroupButtons
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.IconText
import com.zaroslikov.fermacompose2.ui.elements.TextField.BaseOutlinedTextNew4
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.GroupButton
import com.zaroslikov.fermacompose2.ui.project.finance.analysis.FinanceAnalysisEnum
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard
import com.zaroslikov.fermacompose2.violet_4
import com.zaroslikov.fermacompose2.white
import io.appmetrica.analytics.impl.L
import kotlinx.coroutines.launch
import kotlin.collections.forEach

@Composable
fun EntryBookmarkTwoScreen(
    modifier: Modifier = Modifier,
    bookmarkList: List<DomainBookmark>,
    indexBookmark: Long,
    isTemplatesPlan: Boolean,
    isAutoOver: Boolean,
    isAutoAiring: Boolean,
    parametersDayList: List<ParameterDay>,
    onIntent: (EntryBookmarkIntent) -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 20.dp),
    ) {
        if (bookmarkList.isNotEmpty())
            TemplatesCard(indexBookmark, isTemplatesPlan, bookmarkList, onIntent)
        WarningCard(indexBookmark)
        Spacer(Modifier.padding(vertical = 8.dp))
        CardFieldNew(padding = PaddingValues()) {
            TitleRow()
            parametersDayList.forEachIndexed { index, parameter ->
                IncubatorRow(
                    index = index,
                    isTemplatesPlan = isTemplatesPlan,
                    isAutoAiring = isAutoAiring,
                    isAutoOver = isAutoOver,
                    parameterDay = parameter,
                    onIntent = onIntent
                )
            }
        }
    }
}

@Composable
private fun TemplatesCard(
    /* isShowFastAddProduct: Boolean,
     list: List<DomainFastAddProduct>,
     onShowClick: (Boolean) -> Unit,
     onClick: (DomainFastAddProduct) -> Unit*/
    indexBookmark: Long,
    isTemplatesPlan: Boolean,
    bookmarkList: List<DomainBookmark>,
    onIntent: (EntryBookmarkIntent) -> Unit
) {
    var isShowFastAddProduct by rememberSaveable { mutableStateOf(false) }
    val icon =
        if (isShowFastAddProduct) R.drawable.icon_keyboard_arrow_up else R.drawable.icon_keyboard_arrow_down
    BorderCard(
        borderColor = blue_9,
        containerColor = blue_3,
        shape = RoundedCornerShape(14.dp),
        onClick = { isShowFastAddProduct = !isShowFastAddProduct }
    ) {
        Column {
            Column {
                Spacer(Modifier.padding(vertical = 2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.entry_bookmark_templates_previous_bookmarks),
                        style = text_16,
                        color = black_2
                    )
                    Icon(
                        painterResource(icon),
                        contentDescription = null,
                        tint = gray_7
                    )
                }
            }
            AnimatedVisibility(
                visible = isShowFastAddProduct
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Spacer(Modifier.padding(vertical = 2.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                stringResource(R.string.entry_bookmark_select_template),
                                style = text_12,
                                color = gray_7
                            )
                            BorderButton(
                                text = stringResource(R.string.button_by_default),
                                iconRes = R.drawable.outline_refresh_24,
                                textColor = marengo,
                                iconColor = marengo,
                                paddingValues = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) { onIntent(EntryBookmarkIntent.ByDefaultTemplatesClicked) }
                        }
                        HorizontalDivider(thickness = 1.dp, color = blue_9)
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bookmarkList.forEach { bookmark ->
                            BookmarkCard(
                                enabled = indexBookmark == bookmark.id,
                                isTemplatesPlan = isTemplatesPlan,
                                bookmark = bookmark,
                                onClick = {
                                    onIntent(EntryBookmarkIntent.IndexChoiceBookmarkClicked(bookmark.id))
                                },
                                onTemplatesClick = {
                                    onIntent(EntryBookmarkIntent.ChoiceTemplatesBookmarkClicked)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarkCard(
    enabled: Boolean,
    isTemplatesPlan: Boolean,
    bookmark: DomainBookmark,
    onClick: () -> Unit,
    onTemplatesClick: () -> Unit
) {
    val (borderColor, containerColor) = if (enabled) green_6 to price_green_2 else blue_9 to white
    val textColor = if (enabled) green_9 else black_2

    BorderCard(
        shape = RoundedCornerShape(10.dp),
        borderColor = borderColor,
        containerColor = containerColor,
        padding = PaddingValues(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(bookmark.title, style = text_14, color = textColor)
                Text(
                    stringResource(R.string.entry_bookmark_completed_s).format(bookmark.endDate),
                    style = text_12,
                    color = gray_7
                )
            }
            if (enabled)
                GroupButton {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val (textColor, backgroundColor) = if (isTemplatesPlan) white to blue_4
                        else marengo to gray_6
                        val (textColorTwo, backgroundColorTwo) = if (!isTemplatesPlan) white to green_6
                        else marengo to gray_6
                        ButtonForGroupButtons(
                            text = R.string.entry_bookmark_plan,
                            backgroundColor = backgroundColor,
                            textColor = textColor
                        ) { onTemplatesClick() }
                        ButtonForGroupButtons(
                            text = R.string.entry_bookmark_fact,
                            backgroundColor = backgroundColorTwo,
                            textColor = textColorTwo
                        ) { onTemplatesClick() }
                    }
                }
        }
    }
}

@Composable
private fun TitleRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(grey_2)
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconToggle(
            stringRes = R.string.entry_bookmark_day_tooltip,
            iconRes = R.drawable.baseline_calendar_month_24,
            iconColor = dark
        )
        IconToggle(
            stringRes = R.string.entry_bookmark_temp_tooltip,
            iconRes = R.drawable.outline_device_thermostat_24,
            iconColor = red_13
        )
        IconToggle(
            stringRes = R.string.entry_bookmark_damp_tooltip,
            iconRes = R.drawable.outline_dew_point_24,
            iconColor = blue_4
        )
        IconToggle(
            stringRes = R.string.entry_bookmark_over_tooltip,
            iconRes = R.drawable.outline_cached_24,
            iconColor = violet_4
        )
        IconToggle(
            stringRes = R.string.entry_bookmark_airing_tooltip,
            iconRes = R.drawable.outline_air_24,
            iconColor = green_6
        )
    }
}

@Composable
private fun IncubatorRow(
    index: Int,
    isTemplatesPlan: Boolean,
    isAutoAiring: Boolean,
    isAutoOver: Boolean,
    parameterDay: ParameterDay,
    onIntent: (EntryBookmarkIntent) -> Unit
) {
    HorizontalDivider(thickness = 1.dp, color = gray_6)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Box(
            modifier = Modifier.weight(1f),
        ) {
            IconText(
                number = parameterDay.day.toString(),
                colorBackground = orang_8,
                colorText = orang_6,
                sizeCard = 40.dp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        BaseOutlinedTextNew4(
            modifier = Modifier.weight(1f),
            value = if (isTemplatesPlan) parameterDay.temp else parameterDay.tempFact,
            onValueChange = { onIntent(EntryBookmarkIntent.TempChanged(index, it)) },
            intResSup = R.string.is_empty,
        )
        BaseOutlinedTextNew4(
            modifier = Modifier.weight(1f),
            value = if (isTemplatesPlan) parameterDay.damp else parameterDay.dampFact,
            onValueChange = { onIntent(EntryBookmarkIntent.DampChanged(index, it)) },
            intResSup = R.string.is_empty
        )
        BaseOutlinedTextNew4(
            modifier = Modifier.weight(1f),
            value = if (isAutoOver) stringResource(R.string.entry_bookmark_auto) else if (isTemplatesPlan) parameterDay.over else parameterDay.overFact,
            onValueChange = { onIntent(EntryBookmarkIntent.OverChanged(index, it)) },
            intResSup = R.string.is_empty,
            readOnly = isAutoOver
        )
        BaseOutlinedTextNew4(
            modifier = Modifier.weight(1f),
            value = if (isAutoAiring) stringResource(R.string.entry_bookmark_auto) else if (isTemplatesPlan) parameterDay.airing else parameterDay.airingFact,
            onValueChange = { onIntent(EntryBookmarkIntent.AiringChanged(index, it)) },
            intResSup = R.string.is_empty,
            readOnly = isAutoAiring
        )
    }
}

@Composable
fun IconToggle(
    @StringRes stringRes: Int,
    @DrawableRes iconRes: Int,
    iconColor: Color,
    size: Dp = 24.dp
) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()
    TooltipBox(
        positionProvider = rememberTooltipPositionProvider(positioning = TooltipAnchorPosition.Above),
        tooltip = {
            PlainTooltip {
                Text(
                    text = stringResource(stringRes),
                    style = text_14
                )
            }
        },
        state = tooltipState
    ) {
        IconButton(onClick = { scope.launch { tooltipState.show() } }) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(size)
            )
        }
    }
}

@Composable
private fun WarningCard(
    indexBookmark: Long
) {
    AnimatedVisibility(
        visible = indexBookmark == 0L
    ) {
        Column {
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            WarningCard(
                colorBackground = orang_4,
                colorBorder = orang_5,
                colorIcon = orang_2,
                colorIconBackground = Color(0xFFFEF3C6),
                colorTitle = Color(0xFF7B3306),
                colorText = orang_6,
                icon = R.drawable.icon_warning,
                title = R.string.entry_bookmark_warning_title,
                text = R.string.entry_bookmark_warning_text
            )
        }
    }
}