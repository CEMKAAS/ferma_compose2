@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry

import android.app.Notification
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_2
import com.zaroslikov.fermacompose2.green_3
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.CardNewWithTitle
import com.zaroslikov.fermacompose2.ui.elements.CircularProgress
import com.zaroslikov.fermacompose2.ui.elements.OutlineIconButtonNew
import com.zaroslikov.fermacompose2.ui.elements.ProductKillCard
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedNumberNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextCountNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDateNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextDropdownMenuNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextNoteNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedTextTitleAddNew
import com.zaroslikov.fermacompose2.ui.elements.TextField.TimeOutlinedTextFieldNew
import com.zaroslikov.fermacompose2.ui.elements.TopAppBarBack
import com.zaroslikov.fermacompose2.ui.elements.modifierScreen
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.incubator_project.AddIncubator.AddIncubatorIntent
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.AnimalCountIntent
import com.zaroslikov.fermacompose2.white
import kotlinx.coroutines.launch

object EntryBookmarkDestination : NavigationDestination {
    override val route = "EntryBookmarkScreen"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun EntryBookmarkScreen(
    viewModel: EntryBookmarkViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarBack(
                intRes = R.string.add_incubator_screen_title,
                onNavigateBackClick = { },
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
                state = state
            )
    }
}

@Composable
private fun EntryBookmarkContainer(
    modifier: Modifier = Modifier,
    state: EntryBookmarkState
) {
    val coroutineScope = rememberCoroutineScope()
    val pages = Pages.entries
    val pagerState = rememberPagerState(
        pageCount = { pages.size },
        initialPage = 0
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
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
            state = pagerState
        ) { pageIndex ->
            when (pages[pageIndex]) {
                Pages.INFORMATION -> EnterValue(state = state.currentProduct) { }
                Pages.DAY_OF_PARAMETERS -> Table()
            }
        }
    }
}


@Composable
private fun EnterValue(
    modifier: Modifier = Modifier,
    state: EntryBookmark,
    onIntent: (AddIncubatorIntent) -> Unit
) {
    Column(modifier.padding(horizontal = 16.dp)) {
        CardFieldNew(
            padding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextNew(
                value = state.title,
                onValueChange = { onIntent(AddIncubatorIntent.TitleChanged(it)) },
                isError = state.error.isErrorTitle,
                labelIntRes = R.string.entry_bookmark_title,
                supportingText = R.string.entry_bookmark_title_support,
                isBorderCard = false
            )
            OutlinedTextDropdownMenuNew(
                value = state.type,
                onValueChange = { onIntent(AddIncubatorIntent.BrandChanged(it)) },
                titleList = emptyList(),
                labelIntRes = R.string.entry_bookmark_type,
                intResSup = R.string.is_empty,
                isBorderCard = false
            )
            OutlinedTextDropdownMenuNew(
                value = state.breed,
                onValueChange = { onIntent(AddIncubatorIntent.BrandChanged(it)) },
                titleList = emptyList(),
                labelIntRes = R.string.entry_bookmark_breed,
                intResSup = R.string.is_empty,
                isBorderCard = false
            )
            OutlinedNumberNew(
                value = state.countEgg,
                onValueChange = { onIntent(AddIncubatorIntent.CapacityChanged(it)) },
                intRes = R.string.entry_bookmark_count_egg,
                intResSup = R.string.is_empty,
                isError = state.error.isErrorCount,
                suffix = Suffix.PIECES,
                isBorderCard = false
            )
            OutlinedNumberNew(
                value = state.price,
                onValueChange = { onIntent(AddIncubatorIntent.PriceChanged(it)) },
                intRes = R.string.entry_bookmark_price_egg,
                intResSup = R.string.entry_bookmark_price_support,
                suffix = Suffix.PIECES,
                isBorderCard = false
            )
            OutlinedTextDateNew(
                value = state.date,
                onValueChange = { onIntent(AddIncubatorIntent.DateClicked(it)) },
                intRes = R.string.entry_bookmark_data,
                isBorderCard = false
            )
            OutlinedTextNoteNew(
                value = state.note,
                onValueChange = { onIntent(AddIncubatorIntent.NoteClicked(it)) },
                labelIntRes = R.string.add_incubator_screen_note,
                supportingText = R.string.add_incubator_screen_note_support,
                isBorderCard = false
            )
        }
        NotificationCard()
    }
}

@Composable
private fun NotificationCard(
    isEntry: Boolean = true
) {
    CardNewWithTitle(
        titleRes = R.string.entry_bookmark_notification
    ) {
        /* productKillFilter.forEachIndexed { index, product ->
             ProductKillCard(
                 number = index + 1,
                 name = product.title,
                 value = product.countProduct.toFormatNumber(),
                 suffix = product.suffixProduct,
                 isEditMode = product.isEntry,
                 onClick = { onIntent(AnimalCountIntent.ChoiceProductKillChanged(index)) },
             ) { onIntent(AnimalCountIntent.RemoveProductKillChanged(index)) }
         }*/
        /*BorderCard(
            containerColor = ghostly_white,
            padding = PaddingValues(18.dp)
        ) {*/
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (/*!state.currentProduct.isEntry*/ true)
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.animal_count_screen_edit_mode),
                            style = text_14,
                            color = orang_6
                        )
                        TextButton(
                            onClick = { /*onIntent(AnimalCountIntent.CancelProductKillChanged)*/ }
                        ) {
                            Text(
                                stringResource(R.string.button_text_cancel_2),
                                style = text_12,
                                color = gray_7
                            )
                        }
                    }
                    HorizontalDivider(thickness = 1.dp, color = orang_5)
                }

            TimeOutlinedTextFieldNew(
                time = "12:30",
                onValueChange = { },
                isBorderCard = false
            )
            OutlinedTextNoteNew(
                value = "",
                onValueChange = { },
                isBorderCard = false
            )
        }
        OutlineIconButtonNew(
            enabled = true,
            isEntry = false
        ) { }
        /* }*/
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