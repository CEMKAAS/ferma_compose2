package com.zaroslikov.fermacompose2.ui.elements


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black
import com.zaroslikov.fermacompose2.gray_4
import com.zaroslikov.fermacompose2.green_2
import com.zaroslikov.fermacompose2.green_3
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.white

@Composable
fun ButtonRefresh(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.toButton()
    ) {
        Icon(
            modifier = Modifier.padding(end = 3.dp),
            painter = painterResource(R.drawable.baseline_create_24),
            contentDescription = stringResource(R.string.button_refresh)
        )
        Text(text = stringResource(R.string.button_refresh))
    }
}

@Composable
fun ButtonArchive(
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick, modifier = Modifier.toButton()
    ) {
        Icon(
            modifier = Modifier.padding(end = 3.dp),
            painter = painterResource(R.drawable.baseline_archive_24),
            contentDescription = stringResource(R.string.button_archive)
        )
        Text(text = stringResource(R.string.button_archive))
    }
}

@Composable
fun ButtonArchiveNew(
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick, modifier = Modifier.toButton()
    ) {
        Icon(
            modifier = Modifier.padding(end = 3.dp),
            painter = painterResource(R.drawable.baseline_archive_24),
            contentDescription = stringResource(R.string.button_archive)
        )
        Text(text = stringResource(R.string.button_archive))
    }
}


@Composable
fun ButtonDelete(
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.toButton(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.error)
    ) {
        Icon(
            modifier = Modifier.padding(end = 3.dp),
            painter = painterResource(R.drawable.baseline_delete_24),
            contentDescription = stringResource(R.string.button_delete)
        )
        Text(text = stringResource(R.string.button_delete))
    }
}

@Composable
fun ButtonStandart(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @StringRes intRes: Int
) {
    Button(
        onClick = onClick, modifier = modifier.toButton()
    ) {
        Text(text = stringResource(intRes))
    }
}

@Composable
fun ButtonCustom(
    modifier: Modifier = Modifier,
    @StringRes intRes: Int,
    drawableRes: Int? = null,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick, modifier = modifier.toButton()
    ) {
        drawableRes?.let {
            Icon(
                modifier = Modifier.padding(end = 3.dp),
                painter = painterResource(it),
                contentDescription = stringResource(intRes)
            )
        }
        Text(text = stringResource(intRes))
    }
}

@Composable
fun GradientMaterialButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF009966), Color(0xFF00A63E)),
        start = Offset(0f, 0f),
        end = Offset(300f, 0f)
    )
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(14.dp),
        enabled = enabled,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(brush = gradient)
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            color = Color.White,
            style = text_14,
        )
    }
}

@Composable
fun GradientButton(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    text: String,
    @DrawableRes iconRes: Int? = null,
    enable: Boolean,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    onClick: () -> Unit,
) {
    val gradient = Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = Offset(300f, 0f)
    )
    val alpha = if (enable) 0.5f else 1f

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(brush = gradient, alpha = alpha)
            .then(
                if (enable) Modifier else Modifier.clickable(onClick = onClick)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            iconRes?.let {
                Icon(
                    painterResource(it),
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Text(
                modifier = Modifier.padding(paddingValues),
                text = text,
                color = Color.White,
                style = text_14
            )
        }
    }
}

@Composable
fun CloseButton(
    modifier: Modifier = Modifier,
    @StringRes text: Int,
    @DrawableRes iconRes: Int? = null,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, gray_4, RoundedCornerShape(14.dp))
            .background(white)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            iconRes?.let {
                Icon(
                    painterResource(it),
                    contentDescription = null,
                    tint = black
                )
            }
            Text(
                text = stringResource(text),
                color = black,
                style = text_14
            )
        }
    }
}

@Composable
fun BigBorderButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    @StringRes text: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, gray_4, RoundedCornerShape(14.dp))
            .background(white)
            .clickable(onClick = onClick)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            icon?.let {
                Icon(
                    painterResource(icon),
                    contentDescription = null,
                    tint = black,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = stringResource(text),
                color = black,
                style = text_14
            )
        }
    }
}

@Composable
fun BorderShowAllButton(
    modifier: Modifier = Modifier,
    listSize: Int,
    textColor: Color,
    borderColor: Color,
    isShowMore: Boolean,
    onClick: () -> Unit
) {
    val (icon, text) = if (isShowMore)
        R.drawable.icon_keyboard_arrow_up to stringResource(R.string.button_wrap)
    else R.drawable.icon_keyboard_arrow_down to
            stringResource(R.string.animal_card_screen_show_product_all).format(listSize)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, color = borderColor),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painterResource(icon),
                contentDescription = null,
                tint = textColor
            )
            Spacer(Modifier.padding(horizontal = 8.dp))
            Text(
                text = text,
                style = text_14,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
            )
        }
    }
}


@Composable
fun ButtonForGroupButtons(
    modifier: Modifier = Modifier,
    @StringRes text: Int,
    backgroundColor: Color,
    textColor: Color,
    shadowElevation: Dp,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp),
        shadowElevation = shadowElevation
    ) {
        Text(
            text = stringResource(text),
            style = text_12,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable(
                    indication = null, // ❗ отключаем ВСЕ эффекты
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onClick()
                }
                .padding(vertical = 8.dp, horizontal = 10.dp)
        )
    }
}

@Composable
fun OutlineIconButtonNew(
    enabled: Boolean,
    isEntry: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            2.dp,
            if (enabled) green_3 else grey
        ),
        enabled = enabled,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                if (isEntry) Icons.Default.Add else Icons.Default.Edit,
                contentDescription = null,
                tint = if (enabled) green_2 else grey,
                modifier = Modifier.size(16.dp)
            )
            Text(
                stringResource(
                    if (isEntry) R.string.button_text_add_title
                    else R.string.button_text_edit_title
                ),
                style = text_14,
                color = if (enabled) green_2 else grey
            )
        }
    }
}