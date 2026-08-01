package com.zaroslikov.fermacompose2.ui.start.update

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_1
import com.zaroslikov.fermacompose2.gray_8
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.text_16
import kotlinx.coroutines.launch

@Composable
fun BaseUpdateScreen(
    countPage: Int,
    onSkipClick: () -> Unit,
    content: @Composable (Pair<Int, PaddingValues>) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { countPage })
    val scope = rememberCoroutineScope()
    val lastPage = pagerState.pageCount - 1
    Scaffold(
        topBar = {
            TitleAppBar(
                titleRes = R.string.training_screen_main_title_app_bar,
                onClick = onSkipClick,
                iconRes = R.drawable.ic_log,
                iconColor = price_green,
                backgroundColor = green_8
            )
        },
        bottomBar = {
            ButtonBar(
                isFirst = pagerState.currentPage == 0,
                isLast = pagerState.currentPage == lastPage,
                color = price_green,
                currentPage = pagerState.currentPage,
                onLastClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                onNextClick = {
                    scope.launch {
                        if (pagerState.currentPage == 4) onSkipClick()
                        else pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            )
        }
    ) { innerPadding ->
        HorizontalPager(
            modifier = Modifier.padding(innerPadding),
            state = pagerState
        ) { page ->
            content(page to innerPadding)
        }
    }
}

@Composable
private fun TitleAppBar(
    @StringRes titleRes: Int,
    @DrawableRes iconRes: Int,
    iconColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTraining(
                iconRes = iconRes,
                shape = RoundedCornerShape(10.dp),
                boxSize = 32.dp,
                iconSize = 32.dp,
                iconColor = iconColor,
                backgroundColor = backgroundColor,
                isShadow = true,
                isIcon = false
            )
            Text(stringResource(titleRes), style = textBold_16, color = black_1)
        }
        TextButton(onClick = onClick) {
            Text(stringResource(R.string.button_text_skip), style = text_16, color = marengo)
        }
    }
}

@Composable
private fun ButtonBar(
    isFirst: Boolean,
    isLast: Boolean,
    currentPage: Int,
    color: Color,
    onLastClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { index ->
                val isCurrent = currentPage == index
                Spacer(Modifier.padding(4.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (isCurrent) color else gray_8,
                            shape = if (isCurrent) RoundedCornerShape(99.dp) else CircleShape
                        )
                )
            }
        }
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(
                visible = !isFirst
            ) {
                Row {
                    BorderCard(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentWidth(),
                        onClick = onLastClick,
                        padding = PaddingValues(14.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painterResource(R.drawable.outline_keyboard_arrow_left_24),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                }
            }
            GradientButton(
                modifier = Modifier
                    .weight(1f)
                    .animateContentSize(),
                paddingValues = PaddingValues(vertical = 16.dp),
                colors = listOf(green_6, green_shamrock),
                text = stringResource(if (!isLast) R.string.button_further else R.string.button_begin_work),
                postfixIconRes = if (!isLast) R.drawable.outline_keyboard_arrow_right_24 else null
            ) { onNextClick() }
        }
    }
}
