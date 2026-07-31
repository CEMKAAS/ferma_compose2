package com.zaroslikov.fermacompose2.ui.elements.empty_list

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.gray_8
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.white

@Composable
fun EmptyListNew(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes title: Int,
    @StringRes supportText: Int,
    list: List<Pair<Int, Int>>,
    isSupportIcon: Boolean = true,
    primeColor: Color = gray_8,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(
//                horizontal = dimensionResource(id = R.dimen.padding_medium),
                vertical = dimensionResource(R.dimen.padding_small)
            ),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconCard(iconRes, isSupportIcon)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                stringResource(title),
                style = text_16,
                color = black_2,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                stringResource(supportText),
                style = text_14,
                color = gray_7,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            list.forEach {
                Card2(it.first, it.second, primeColor)
            }
        }
    }
}


@Composable
private fun IconCard(
    @DrawableRes iconRes: Int,
    isSupportIcon: Boolean,
) {
    val shape = RoundedCornerShape(24.dp)
    Box {
        BorderCard(
            modifier = Modifier.align(Alignment.Center),
            shape = shape,
            containerColor = ghostly_white,
            padding = PaddingValues(20.dp),
            elevation = 1.dp,
            borderColor = gray_6
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = gray_8,
                modifier = Modifier.size(36.dp)
            )
        }
        if (isSupportIcon)
            BorderCard(
                modifier = Modifier.align(Alignment.BottomEnd),
                shape = CircleShape,
                containerColor = green_6,
                elevation = 1.dp,
                borderColor = white,
                padding = PaddingValues(5.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_add),
                    contentDescription = null,
                    tint = white,
                    modifier = Modifier.size(16.dp)
                )
            }
    }
}

@Composable
private fun Card2(
    @DrawableRes iconRes: Int,
    @StringRes stringRes: Int,
    color: Color = gray_8
) {
    BorderCard(
        shape = RoundedCornerShape(14.dp),
        containerColor = ghostly_white,
        elevation = 1.dp,
        borderColor = gray_6,
        padding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                stringResource(stringRes),
                style = text_12,
                color = gray_7,
                lineHeight = 12.sp
            )
        }
    }
}
