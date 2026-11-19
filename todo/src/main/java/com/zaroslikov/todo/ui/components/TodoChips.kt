package com.zaroslikov.todo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.gray_8
import com.zaroslikov.todo.ui.theme.green_6
import com.zaroslikov.todo.ui.theme.green_8
import com.zaroslikov.todo.ui.theme.green_shamrock
import com.zaroslikov.todo.ui.theme.marengo
import com.zaroslikov.todo.ui.theme.textBold_12
import com.zaroslikov.todo.ui.theme.text_14
import com.zaroslikov.todo.ui.theme.todoColorPalette
import com.zaroslikov.todo.ui.theme.white

/** Выбираемый чип; [fillWithColor] заполняет плашку цветом тега вместо цветной точки. */
@Composable
fun TodoChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    color: Color? = null,
    fillWithColor: Boolean = false
) {
    val tagBackground = color?.takeIf { fillWithColor }
    val tagForeground = tagBackground?.let { white }
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = text, style = text_14) },
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        leadingIcon = when {
            tagBackground != null && selected -> {
                { Icon(imageVector = Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
            }
            leadingIcon != null -> {
                { Icon(imageVector = leadingIcon, contentDescription = null, modifier = Modifier.size(18.dp)) }
            }
            color != null && tagBackground == null -> {
                { TodoColorDot(color = color, size = 10.dp) }
            }
            else -> null
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = tagBackground ?: white,
            labelColor = tagForeground ?: marengo,
            iconColor = tagForeground ?: marengo,
            selectedContainerColor = tagBackground ?: green_8,
            selectedLabelColor = tagForeground ?: green_shamrock,
            selectedLeadingIconColor = tagForeground ?: green_shamrock
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = tagBackground ?: gray_8,
            selectedBorderColor = tagBackground ?: green_6
        )
    )
}

/** Маленькая плашка (приоритет, тег, статус и т.п.). */
@Composable
fun TodoBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = textBold_12,
        color = color,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

/** Цветная точка (цвет списка, статуса, тега). */
@Composable
fun TodoColorDot(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 12.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

/** Выбор цвета из [todoColorPalette]; [selectedIndex] и результат [onSelect] — индекс палитры. */
@Composable
fun TodoColorPickerRow(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Вся палитра в одну строку и на телефоне 360dp: кружки поменьше, промежутки — что останется
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        todoColorPalette.forEachIndexed { index, color ->
            val selected = index == selectedIndex
            val description = stringResource(R.string.todo_color_option, index + 1)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (selected) 3.dp else 0.dp,
                        color = if (selected) black_2.copy(alpha = 0.35f) else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable(role = Role.RadioButton) { onSelect(index) }
                    .semantics {
                        this.selected = selected
                        contentDescription = description
                    },
                contentAlignment = Alignment.Center
            ) {
                if (selected)
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = white,
                        modifier = Modifier.size(18.dp)
                    )
            }
        }
    }
}
