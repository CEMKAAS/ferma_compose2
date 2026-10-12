package com.zaroslikov.crm.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaroslikov.crm.R
import com.zaroslikov.crm.ui.theme.black_2
import com.zaroslikov.crm.ui.theme.gray_6
import com.zaroslikov.crm.ui.theme.gray_7
import com.zaroslikov.crm.ui.theme.green_6
import com.zaroslikov.crm.ui.theme.green_shamrock
import com.zaroslikov.crm.ui.theme.textBold_16
import com.zaroslikov.crm.ui.theme.textBold_18
import com.zaroslikov.crm.ui.theme.textBold_20
import com.zaroslikov.crm.ui.theme.text_12
import com.zaroslikov.crm.ui.theme.text_14
import com.zaroslikov.crm.ui.theme.white

private val CardShape = RoundedCornerShape(14.dp)

/** Белая карточка в стиле `CardFieldNew` из :app. */
@Composable
fun CrmCard(
    modifier: Modifier = Modifier,
    containerColor: Color = white,
    padding: PaddingValues = PaddingValues(16.dp),
    elevation: Dp = 5.dp,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = CardDefaults.cardColors(containerColor = containerColor)
    val cardElevation = CardDefaults.cardElevation(defaultElevation = elevation)
    val body: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            verticalArrangement = verticalArrangement
        ) {
            content()
        }
    }
    if (onClick != null)
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = CardShape,
            colors = colors,
            elevation = cardElevation,
            content = body
        )
    else
        Card(
            modifier = modifier,
            shape = CardShape,
            colors = colors,
            elevation = cardElevation,
            content = body
        )
}

/** Градиентная карточка со свечением, порт `BigColorCard` из :app. */
@Composable
fun CrmBigCard(
    glowColor: Color,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(20.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    borderStroke: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                shadowElevation = 20f
                shape = CardShape
                clip = false
                ambientShadowColor = glowColor.copy(alpha = 0.8f)
                spotShadowColor = glowColor.copy(alpha = 0.8f)
            }
            .then(if (borderStroke != null) Modifier.border(borderStroke, CardShape) else Modifier)
            .drawBehind {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = colors,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    ),
                    cornerRadius = CornerRadius(14.dp.toPx())
                )
            }
            .then(
                if (onClick != null) Modifier
                    .clip(CardShape)
                    .clickable { onClick() }
                else Modifier
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier.padding(padding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = verticalArrangement
        ) {
            content()
        }
    }
}

/** Иконка в тонированном круге. */
@Composable
fun CrmIconCircle(
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = color,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

/** KPI-плитка: иконка, подпись, значение и необязательная подпись снизу. */
@Composable
fun CrmKpiTile(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    caption: String? = null,
    onClick: (() -> Unit)? = null
) {
    CrmCard(
        modifier = modifier,
        padding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        onClick = onClick
    ) {
        CrmIconCircle(icon = icon, color = color, size = 36.dp)
        Text(
            text = label,
            style = text_12,
            color = gray_7,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            style = textBold_20,
            color = black_2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (caption != null)
            Text(
                text = caption,
                style = text_12,
                color = gray_7,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
    }
}

/** Заголовок секции с необязательным действием справа (по умолчанию «Все»). */
@Composable
fun CrmSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = textBold_18,
            color = black_2,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (onAction != null)
            TextButton(onClick = onAction) {
                Text(
                    text = actionText ?: stringResource(R.string.crm_all),
                    style = text_14,
                    color = green_shamrock
                )
            }
    }
}

/** Пустое состояние: иконка, заголовок, текст и необязательная кнопка. */
@Composable
fun CrmEmptyState(
    icon: ImageVector,
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(gray_6),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = gray_7,
                modifier = Modifier.size(36.dp)
            )
        }
        Text(text = title, style = textBold_16, color = black_2, textAlign = TextAlign.Center)
        Text(text = text, style = text_14, color = gray_7, textAlign = TextAlign.Center)
        if (buttonText != null && onButtonClick != null)
            Button(
                onClick = onButtonClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = green_6, contentColor = white)
            ) {
                Text(text = buttonText, style = textBold_16)
            }
    }
}
