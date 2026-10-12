package com.zaroslikov.crm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.crm.ui.theme.crmAvatarPalette
import com.zaroslikov.crm.ui.theme.gray_8
import com.zaroslikov.crm.ui.theme.green_6
import com.zaroslikov.crm.ui.theme.green_8
import com.zaroslikov.crm.ui.theme.green_shamrock
import com.zaroslikov.crm.ui.theme.marengo
import com.zaroslikov.crm.ui.theme.textBold_12
import com.zaroslikov.crm.ui.theme.textBold_16
import com.zaroslikov.crm.ui.theme.text_14
import com.zaroslikov.crm.ui.theme.white

/** Выбираемый чип фильтра. */
@Composable
fun CrmChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = text, style = text_14) },
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        leadingIcon = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null, modifier = Modifier.size(18.dp)) }
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = white,
            labelColor = marengo,
            iconColor = marengo,
            selectedContainerColor = green_8,
            selectedLabelColor = green_shamrock,
            selectedLeadingIconColor = green_shamrock
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = gray_8,
            selectedBorderColor = green_6
        )
    )
}

/** Маленькая плашка-статус (активность клиента, VIP и т.п.). */
@Composable
fun CrmBadge(
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

/** Аватар с инициалами; цвет стабилен для одного и того же имени. */
@Composable
fun CrmAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(crmAvatarPalette[crmAvatarColorIndex(name, crmAvatarPalette.size)]),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = crmInitials(name),
            style = textBold_16.copy(fontSize = (size.value * 0.38f).sp),
            color = white
        )
    }
}

/** «Иван Петров» → «ИП», «ферма» → «Ф», пустое имя → «?». */
internal fun crmInitials(name: String): String {
    val words = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    if (words.isEmpty()) return "?"
    return words.take(2).joinToString("") { it.first().uppercase() }
}

internal fun crmAvatarColorIndex(name: String, paletteSize: Int): Int =
    Math.floorMod(name.trim().lowercase().hashCode(), paletteSize)
