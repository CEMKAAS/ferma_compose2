package com.zaroslikov.fermacompose2.ui.start.update

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_1
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.gray_8
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.textBold_16
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.white
import kotlinx.coroutines.launch

@Composable
fun BaseUpdateScreen(
    pagerState: PagerState,
    colors: List<Color>? = null,
    onSkipClick: () -> Unit,
    content: @Composable (Pair<Int, PaddingValues>) -> Unit
) {
    val scope = rememberCoroutineScope()
    val lastPage = pagerState.pageCount - 1
    Scaffold(
        modifier = Modifier.then(
                if (colors != null)
                    Modifier.background(
                        brush = Brush.linearGradient(
                            colors = colors,
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                else Modifier.background(white)
            )
            .systemBarsPadding(),
        containerColor = Color.Transparent,
        topBar = {
            TitleAppBar(
                titleRes = R.string.training_screen_main_title_app_bar,
                onClick = onSkipClick,
                isWhiteStyle = colors != null,
                iconRes = R.drawable.ic_log,
                iconColor = price_green,
                backgroundColor = green_8
            )
        },
        bottomBar = {
            ButtonBar(
                countPage = pagerState.pageCount,
                isFirst = pagerState.currentPage == 0,
                isLast = pagerState.currentPage == lastPage,
                color = price_green,
                isWhiteStyle = colors != null,
                currentPage = pagerState.currentPage,
                onLastClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage - 1,
                            animationSpec = tween(durationMillis = 500)
                        )
                    }
                },
                onNextClick = {
                    scope.launch {
                        if (pagerState.currentPage == lastPage) onSkipClick()
                        else pagerState.animateScrollToPage(
                            pagerState.currentPage + 1,
                            animationSpec = tween(durationMillis = 500)
                        )
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
    isWhiteStyle: Boolean,
    iconColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit,
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
            Text(
                stringResource(titleRes),
                style = textBold_16,
                color = if (isWhiteStyle) white else black_1
            )
        }
        TextButton(onClick = onClick) {
            Text(
                stringResource(R.string.button_text_skip),
                style = text_16,
                color = if (isWhiteStyle) white else marengo
            )
        }
    }
}

@Composable
private fun ButtonBar(
    countPage: Int,
    isFirst: Boolean,
    isLast: Boolean,
    currentPage: Int,
    color: Color,
    isWhiteStyle: Boolean,
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
            repeat(countPage) { index ->
                val isCurrent = currentPage == index
                Spacer(Modifier.padding(4.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (isCurrent) if (isWhiteStyle) white else color else gray_8,
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
                        containerColor = white.copy(alpha = if (isWhiteStyle) 0.2f else 1f),
                        borderColor = if (isWhiteStyle) white.copy(alpha = 0.2f) else grey_2,
                        onClick = onLastClick,
                        padding = PaddingValues(14.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painterResource(R.drawable.outline_keyboard_arrow_left_24),
                                tint = if (isWhiteStyle) white else black_2,
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
                textColor = if (isWhiteStyle) black_2 else white,
                colors = if (isWhiteStyle) listOf(white, white)
                else listOf(green_6, green_shamrock),
                text = stringResource(if (!isLast) R.string.button_further else R.string.button_begin_work),
                postfixIconRes = if (!isLast) R.drawable.outline_keyboard_arrow_right_24 else null
            ) { onNextClick() }
        }
    }
}
