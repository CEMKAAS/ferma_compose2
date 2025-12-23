@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.elements

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.outlined.MarkEmailUnread
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.elements.TextField.SearchBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun TopAppBarNavigation(
    @StringRes title: Int,
    scope: CoroutineScope,
    drawerState: DrawerState,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = stringResource(title))
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.apply {
                        if (isClosed) open() else close()
                    }
                }
            }) {
                Icon(
                    painterResource(R.drawable.icon_menu),
                    contentDescription = "Меню"
                )
            }
        }
    )
}

@Composable
fun TopAppBarNavigationNew(
    value: String,
    isGroup: Boolean,
    onValueChange: (String) -> Unit,
    onClick: (Boolean) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        contentPadding = PaddingValues(
            end = dimensionResource(id = R.dimen.padding_medium),
            top = 16.dp,
            bottom = 8.dp
        ),
        title = {
            SearchBar(
                value = value,
                onValueChange = onValueChange,
                onClick = onClick,
                isGroup = isGroup,
                iconRes = if (isGroup) R.drawable.icon_group else R.drawable.icon_list
            )
        },
        windowInsets = WindowInsets(0),
        scrollBehavior = scrollBehavior
    )
}

/*
@Composable
fun TopAppBarNavigationNew2(
    @StringRes title: Int,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    (
        title = {
            */
/* Text(text = stringResource(title))*//*

            SearchBar(
                value = "searchText",
                onValueChange = { */
/*onSearchChange*//*
 },
                onClick = { */
/*details = !details*//*
 },
                iconRes = if (true*/
/*details*//*
) R.drawable.icon_group else R.drawable.icon_list
            )
        },
        scrollBehavior = scrollBehavior
    )
}
*/



@Composable
fun TopAppBarBack(
    @StringRes intRes: Int? = null,
    title: String = "",
    onNavigateBackClick: ((() -> Unit)?) = {},
    onCalendarClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = if (intRes != null) stringResource(intRes) else title, maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            onNavigateBackClick?.let {
                IconButton(onClick = onNavigateBackClick) {
                    Icon(
                        painterResource(R.drawable.baseline_arrow_back_24),
                        contentDescription = "Назад"
                    )
                }
            }
        },
        windowInsets = WindowInsets(0),
        scrollBehavior = scrollBehavior,
        actions = {
            onCalendarClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        painterResource(R.drawable.icon_date_range),
                        contentDescription = "Date Range"
                    )
                }
            }
            onSettingsClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        painterResource(R.drawable.icon_setting),
                        contentDescription = "Date Range"
                    )
                }
            }
        }
    )
}

@Composable
fun TopAppBarBackAndFilter(
    @StringRes intRes: Int? = null,
    title: String = "",
    navigateUp: () -> Unit = {},
    calendarClick: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.largeTopAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (intRes != null) stringResource(intRes) else title, maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                OutlinedTextNote(
                    value = "TODO()",
                    onValueChange = { TODO() }
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    painterResource(R.drawable.baseline_arrow_back_24),
                    contentDescription = "Назад"
                )
            }
        },
        windowInsets = WindowInsets(0),
        scrollBehavior = scrollBehavior,
        actions = {
            if (calendarClick != null) {
                IconButton(onClick = calendarClick) {
                    Icon(
                        painterResource(R.drawable.icon_date_range),
                        contentDescription = "Date Range"
                    )
                }
            }
        }
    )
}

@Composable
fun TopAppBarBack2(
    @StringRes intRes: Int? = null,
    title: String = "",
    navigateUp: () -> Unit = {},
    calendarClick: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val sizeClass = currentWindowAdaptiveInfo().windowSizeClass
// Material guidelines state 3 items max in compact, and 5 items max elsewhere.
// To test this, try a resizable emulator, or a phone in landscape and portrait orientation.
    val maxItemCount =
        if (sizeClass.minWidthDp >= WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) {
            5
        } else {
            3
        }
    val icons =
        listOf(
            Icons.Filled.Attachment,
            Icons.Filled.Edit,
            Icons.Outlined.Star,
            Icons.Filled.Snooze,
            Icons.Outlined.MarkEmailUnread,
        )
    val items = listOf("Attachment", "Edit", "Star", "Snooze", "Mark unread")
    TopAppBar(
        title = {
            Text(text = if (intRes != null) stringResource(intRes) else title)
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    painterResource(R.drawable.baseline_arrow_back_24),
                    contentDescription = "Назад"
                )
            }
        },
        windowInsets = WindowInsets(),
        scrollBehavior = scrollBehavior,
        actions = {
            AppBarRow(
                maxItemCount = maxItemCount,
                overflowIndicator = {
                    TooltipBox(
                        positionProvider =
                            TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above
                            ),
                        tooltip = { PlainTooltip { Text("Overflow") } },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(onClick = { it.show() }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Overflow",
                            )
                        }
                    }
                },
            ) {
                items.forEachIndexed { index, item ->
                    clickableItem(
                        onClick = {},
                        icon = {
                            Icon(imageVector = icons[index], contentDescription = item)
                        },
                        label = item,
                    )
                }
            }
        },
    )

}