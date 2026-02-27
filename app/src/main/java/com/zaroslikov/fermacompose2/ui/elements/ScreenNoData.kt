package com.zaroslikov.fermacompose2.ui.elements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.white

@Composable
fun CircularProgress(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_medium),
                vertical = dimensionResource(R.dimen.padding_small)
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun MessageNoData(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
    @StringRes supportRes: Int,
    @StringRes buttonRes: Int,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(titleRes),
            style = text_20_center,
            modifier = Modifier.toOutlinedText()
        )
        Text(
            text = stringResource(messageRes),
            style = text_20_justify,
            modifier = Modifier.toOutlinedText(),
        )
        Text(
            text = stringResource(supportRes),
            style = text_20_center,
            modifier = Modifier.toOutlinedText()
        )
        if (onClick != null)
            ButtonStandart(onClick = onClick, intRes = buttonRes)
    }
}

@Composable
fun MessageNoData2(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
    @StringRes supportSecondText: Int? = null,
    @DrawableRes iconRes: Int,
    iconColor: Color,
    backgroundColor: Color
) {
    BorderCard(modifier) {
        EmptyBookmark(
            iconRes = iconRes,
            title = titleRes,
            supportText = messageRes,
            supportSecondText = supportSecondText,
            iconColor = iconColor,
            backgroundColor = backgroundColor,
            plusColor = iconColor
        )
    }
}

@Composable
fun EmptyBookmark(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes title: Int,
    @StringRes supportText: Int,
    @StringRes supportSecondText: Int? = null,
    iconColor: Color,
    backgroundColor: Color,
    plusColor: Color = white
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .shadow(6.dp, shape = CircleShape)
                .background(color = backgroundColor, shape = CircleShape)
                .size(128.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(64.dp),
                tint = iconColor
            )
        }
        Spacer(modifier = Modifier.padding(12.dp))
        Text(stringResource(title), style = text_24, color = black_2)
        Spacer(modifier = Modifier.padding(6.dp))
        Text(
            stringResource(supportText),
            style = text_16,
            color = gray_7,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (supportSecondText == null) {
                Text(
                    stringResource(R.string.message_no_date_empty_egg_2),
                    style = text_14,
                    color = grey
                )
                Text(
                    "+",
                    style = text_14,
                    color = plusColor,
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
                Text(
                    stringResource(R.string.message_no_date_empty_egg_3),
                    style = text_14,
                    color = grey
                )
            } else
                Text(
                    stringResource(supportSecondText),
                    style = text_14,
                    color = grey,
                    textAlign = TextAlign.Center
                )
        }
    }
}
