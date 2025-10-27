package com.zaroslikov.fermacompose2.ui.sections

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.fermacompose2.ui.sections.add.list_screen.AddScreen
import com.zaroslikov.fermacompose2.ui.sections.add.list_screen.AddViewModel
import com.zaroslikov.fermacompose2.ui.sections.add.list_screen.Page

@Composable
fun SectionWorkspace(
    viewModel: AddViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pages = Page.entries
    val pagerState = rememberPagerState(
        pageCount = { pages.size },
        initialPage = 0
    )
    HorizontalPager(
        state = pagerState
    ) { pageIndex ->
        when (pages[pageIndex]) {
            Page.ADD -> AddScreen(state)
            Page.SALE -> {}
            Page.WRITE_OFF -> {

            }
            Page.EXPENSES -> {

            }
        }
    }
}
