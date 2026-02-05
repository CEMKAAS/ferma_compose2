@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import kotlinx.coroutines.launch

object EntryBookmarkDestination : NavigationDestination {
    override val route = "EntryBookmarkScreen"
    override val titleRes = R.string.app_name
    const val itemIdPT = "itemIdPT"
    const val itemId = "itemId"
    val routeWithArgs = "$route?$itemIdPT={$itemIdPT}&$itemId={$itemId}"
}

@Composable
fun EntryBookmarkMainScreen(
    navigateBack: () -> Unit,
    viewModel: EntryBookmarkViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val eventFlow = viewModel.navigation
    val title =
        if (state.isEntry) R.string.entry_bookmark_screen_add_title
        else R.string.entry_bookmark_screen_edit_title

    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> navigateBack()
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = title,
                onNavigateBackClick = navigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (state.isLoading)
            CircularProgress(
                modifier = Modifier.padding(innerPadding),
            )
        else
            EntryBookmarkContainer(
                modifier = Modifier.modifierScreen(innerPadding, horizontalPaddingValues = 0.dp),
                state = state,
                onIntent = viewModel::onIntent
            )
    }
}

@Composable
private fun EntryBookmarkContainer(
    modifier: Modifier = Modifier,
    state: EntryBookmarkState,
    onIntent: (EntryBookmarkIntent) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val pages = Pages.entries
    val pagerState = rememberPagerState(
        pageCount = { pages.size },
        initialPage = 0
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage, indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        pagerState.currentPage
                    ),
                    color = orang_2
                )
            }
        ) {
            pages.forEachIndexed { index, page ->
                val color = if (pagerState.currentPage == index) orang_2 else gray_7
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(page.iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = color
                            )
                            Text(
                                text = stringResource(page.titleRes),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = text_14,
                                color = color
                            )
                        }
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) { pageIndex ->
            when (pages[pageIndex]) {
                Pages.INFORMATION -> EntryBookmarkOneScreen(
                    isEntry = state.isEntry,
                    state = state.currentProduct,
                    onIntent = onIntent
                )

                Pages.DAY_OF_PARAMETERS -> EntryBookmarkTwoScreen(
                    isAutoOver = state.currentProduct.autoRotation,
                    isAutoAiring = state.currentProduct.autoVentilation,
                    bookmarkList = state.currentProduct.templatesBookmarkList,
                    parametersDayList = state.currentProduct.parameterDayList,
                    indexBookmark = state.currentProduct.indexBookmark,
                    onIntent = onIntent,
                    isTemplatesPlan = state.currentProduct.isTemplatesPlan
                )
            }
        }
    }
}

enum class Pages(
    val iconRes: Int,
    val titleRes: Int,
) {
    INFORMATION(
        iconRes = R.drawable.icon_info,
        titleRes = R.string.entry_bookmark_information,
    ),
    DAY_OF_PARAMETERS(
        iconRes = R.drawable.outline_device_thermostat_24,
        titleRes = R.string.entry_bookmark_parameters_for_day
    )
}