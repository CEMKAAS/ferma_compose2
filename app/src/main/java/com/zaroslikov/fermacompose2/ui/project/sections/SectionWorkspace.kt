package com.zaroslikov.fermacompose2.ui.project.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.grey_3
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.text_10
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddScreen
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.AddViewModel
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.Page
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListScreen
import com.zaroslikov.fermacompose2.ui.project.sections.expenses.list_screen.ExpensesScreen
import com.zaroslikov.fermacompose2.ui.project.sections.sale.list_screen.SaleScreen
import com.zaroslikov.fermacompose2.ui.project.sections.writeOff.list_screen.WriteOffScreen
import com.zaroslikov.fermacompose2.white
import kotlinx.coroutines.launch

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun SectionWorkspaceScreen(
    navigateToItemCard: (Pair<Long, Long>) -> Unit,
    navigationToAnalysis: (Triple<Long, String, Suffix>) -> Unit,
    viewModel: AddViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val pages = Page.entries
    val pagerState = rememberPagerState(
        pageCount = { pages.size },
        initialPage = 2
    )
    Column {
        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        pagerState.currentPage,
                        matchContentSize = true
                    ),
                    width = Dp.Unspecified,
                    color = pages[pagerState.currentPage].toColorList()
                )
            }
        ) {
            pages.forEachIndexed { index, page ->
                val cardSetting = if (pagerState.currentPage == index)
                    Triple(
                        RoundedCornerShape(16.dp), CardDefaults.cardColors(
                            containerColor = white
                        ), BorderStroke(
                            width = 1.dp,
                            color = grey_2
                        )
                    ) else Triple(
                    CardDefaults.shape, CardDefaults.cardColors(
                        containerColor = Color.Transparent, // прозрачная карточка
                        contentColor = Color.Unspecified    // не изменяем цвет текста
                    ), null
                )
                Card(
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    shape = cardSetting.first,
                    colors = cardSetting.second,
                    border = cardSetting.third,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier.size(40.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (pagerState.currentPage == index) page.toColorList() else grey_2
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painterResource(page.toDrawRes()),
                                    modifier = Modifier.size(20.dp),
                                    contentDescription = null,
                                    tint = if (pagerState.currentPage == index) Color.White else grey_3
                                )
                            }
                        }
                        Spacer(Modifier.padding(3.dp))
                        Text(
                            text = stringResource(page.toResId()),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = text_10
                        )
                    }
                }
            }
        }
        HorizontalPager(
            state = pagerState
        ) { pageIndex ->
            when (pages[pageIndex]) {
                Page.ADD -> AddScreen(
                    navigationToAnalysis = { navigationToAnalysis(it) }
                )

                Page.SALE -> SaleScreen()
                Page.EXPENSES -> ExpensesScreen()
                Page.WRITE_OFF -> WriteOffScreen()
                Page.ANIMAL -> AnimalListScreen(navigateToItemCard = navigateToItemCard)
            }
        }
    }
}
