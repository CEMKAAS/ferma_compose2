package com.zaroslikov.fermacompose2.ui.elements.сompositions

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.white


@Composable
fun GroupButton(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
    content: @Composable RowScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = gray_6
        ),
        shape = RoundedCornerShape(10.dp),
    ) {
        Row(
            modifier = modifier
                .padding(paddingValues),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            content()
        }
    }
}

@Composable
fun GroupButtonWitchAnimate(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    itemCount: Int = 0,
    paddingValues: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
    content: @Composable RowScope.() -> Unit
) {
    BoxWithConstraints {
        val width = maxWidth / itemCount

        val offset by animateDpAsState(
            targetValue = width * selectedIndex,
            animationSpec = tween(
                durationMillis = 220,
                easing = FastOutSlowInEasing
            ),
            label = ""
        )
        var rowHeight by remember { mutableStateOf(0.dp) }
        val density = LocalDensity.current
        Card(
            colors = CardDefaults.cardColors(containerColor = gray_6),
            shape = RoundedCornerShape(10.dp)
        ) {
            Box(
                modifier = modifier.padding(paddingValues)
            ) {
                Card(
                    modifier = Modifier
                        .offset(x = offset)
                        .width(width - 8.dp)
                        .height(rowHeight),
                    colors = CardDefaults.cardColors(white),
                    elevation = CardDefaults.cardElevation(1.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {}
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged {
                            rowHeight = with(density) { it.height.toDp() }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    content = content
                )
            }
        }
    }
}
