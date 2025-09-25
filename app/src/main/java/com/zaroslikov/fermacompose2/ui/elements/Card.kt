package com.zaroslikov.fermacompose2.ui.elements

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.start.formatNumber


@Composable
fun CardField(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    isNecessarily: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    row: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit,
) {
    val border = when {
        isError -> BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.error)
        isNecessarily -> BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
        else -> null
    }
    Card(
        modifier = modifier.padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(),
        border = border
    ) {
        if (row) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = horizontalArrangement,
                verticalAlignment = verticalAlignment
            ) {
                content()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = verticalArrangement
            ) {
                content()
            }
        }
    }
}

//@RequiresApi(Build.VERSION_CODES.S)
//@Composable
//fun CardField(
//    modifier: Modifier = Modifier,
//    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
//    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
//    row: Boolean = true,
//    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
//    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
//    content: @Composable () -> Unit,
//) {
//    val blurEffect = RenderEffect.createBlurEffect(
//        20f, 20f, Shader.TileMode.CLAMP
//    ).asComposeRenderEffect()
//
//    Box(
//        modifier = modifier
//            .clip(
//                RoundedCornerShape(16.dp)
//            )
//            .background(Color.Transparent)
//    ) {
//        Box(
////            modifier = Modifier
////                .graphicsLayer {
////                    renderEffect = blurEffect
////                }
////                .background(Color.White.copy(alpha = 0.75f)) // зелёный с прозрачностью
//            modifier = Modifier
//                .matchParentSize()
//                .clip(RoundedCornerShape(16.dp))
//                .background(Color.White.copy(alpha = 0.15f))
//                .blur(radius = 16.dp)
//        )
//        Box(
//            modifier = Modifier
//
//                .padding(
//                    16.dp
//                )
//        ) {
//            if (row) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    horizontalArrangement = horizontalArrangement,
//                    verticalAlignment = verticalAlignment
//                ) {
//                    content()
//                }
//            } else {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    horizontalAlignment = horizontalAlignment,
//                    verticalArrangement = verticalArrangement
//                ) {
//                    content()
//                }
//            }
//        }
//    }
//}

@Composable
fun CardOutlined(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    OutlinedCard(
        modifier = modifier.padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(3.dp, CardDefaults.cardColors().containerColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}


@Composable
fun CardFinance(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @StringRes titleRes: Int,
    value: Double
) {
    CardField(
        modifier = modifier.clickable {
            onClick()
        }
    ) {
        TextColumn(
            titleRes = titleRes,
            value = value
        )
    }
}

@Composable
fun CardFinance(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    titleRes: String,
    value: Double
) {
    CardField(
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        TextColumn(
            titleRes = titleRes,
            value = value
        )
    }
}

@Composable
fun CardFinanceOutlined(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    value: Double
) {
    CardOutlined(
        modifier = modifier
    ) {
        TextColumn(
            titleRes = titleRes,
            value = value
        )
    }
}

@Composable
fun CardFinanceOutlinedRow(
    modifier: Modifier = Modifier,
    @StringRes oneIntRes: Int,
    @StringRes twoIntRes: Int,
    oneValue: Double,
    twoValue: Double,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CardFinanceOutlined(
            modifier = modifier.weight(1f),
            titleRes = oneIntRes,
            value = oneValue,
        )
        CardFinanceOutlined(
            modifier = modifier.weight(1f),
            titleRes = twoIntRes,
            value = twoValue,
        )
    }
}

@SuppressLint("NewApi")
@Composable
fun CardFinanceRow(
    modifier: Modifier = Modifier,
    title: String,
    value: Double
) {
    CardField(
        modifier = modifier
    ) {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth(0.7f),
            style = textBold_16
        )
        Text(
            text = stringResource(R.string.card_ruble_s, value.formatNumber()),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(1f),
            style = textBold_16
        )
    }
}
