package com.zaroslikov.fermacompose2.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.black
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.grey_3
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.project.mainScreen.Destination
import com.zaroslikov.fermacompose2.white

@Composable
fun BottomBarButton(
    destination: Destination,
    index: Int,
    onClick: () -> Unit
) {
    val cardSetting = if (destination.ordinal == index)
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
    val gradient = Brush.linearGradient(
        colors = destination.toColorList(),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    Box(
        modifier = Modifier.clickable(
            onClick = onClick
        )
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .then(
                        if (destination.ordinal == index) Modifier
                            .shadow(
                                elevation = 5.dp,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .background(brush = gradient, shape = RoundedCornerShape(14.dp))
                        else
                            Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(destination.toDrawRes()),
                    modifier = Modifier.size(20.dp),
                    contentDescription = null,
                    tint = if (destination.ordinal == index) Color.White else grey_3
                )
            }
            Spacer(Modifier.padding(4.dp))
            Text(
                text = stringResource(destination.toResId()),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (destination.ordinal == index) black else grey_3,
                style = text_12
            )
        }
    }
}