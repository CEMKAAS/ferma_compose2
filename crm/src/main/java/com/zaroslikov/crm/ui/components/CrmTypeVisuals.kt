package com.zaroslikov.crm.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.zaroslikov.crm.domain.model.CrmClientActivity
import com.zaroslikov.crm.domain.model.CrmContactType
import com.zaroslikov.crm.domain.model.CrmInteractionType
import com.zaroslikov.crm.ui.theme.blue_4
import com.zaroslikov.crm.ui.theme.gray_7
import com.zaroslikov.crm.ui.theme.green_6
import com.zaroslikov.crm.ui.theme.green_shamrock
import com.zaroslikov.crm.ui.theme.orang_9
import com.zaroslikov.crm.ui.theme.purple

// Единые иконки и цвета типов CRM — одинаковые на всех экранах

fun CrmContactType.icon(): ImageVector = when (this) {
    CrmContactType.PHONE -> Icons.Rounded.Phone
    CrmContactType.EMAIL -> Icons.Rounded.Email
    CrmContactType.TELEGRAM -> Icons.AutoMirrored.Rounded.Send
    CrmContactType.WHATSAPP -> Icons.AutoMirrored.Rounded.Chat
    CrmContactType.VK -> Icons.Rounded.Public
    CrmContactType.ADDRESS -> Icons.Rounded.Place
    CrmContactType.OTHER -> Icons.Rounded.MoreHoriz
}

fun CrmInteractionType.icon(): ImageVector = when (this) {
    CrmInteractionType.CALL -> Icons.Rounded.Call
    CrmInteractionType.MEETING -> Icons.Rounded.Groups
    CrmInteractionType.MESSAGE -> Icons.Rounded.Sms
    CrmInteractionType.NOTE -> Icons.Rounded.EditNote
}

fun CrmInteractionType.color(): Color = when (this) {
    CrmInteractionType.CALL -> green_6
    CrmInteractionType.MEETING -> purple
    CrmInteractionType.MESSAGE -> blue_4
    CrmInteractionType.NOTE -> orang_9
}

/** Цвет бейджа активности клиента; текст — `toResId()` из `util`. */
fun CrmClientActivity.color(): Color = when (this) {
    CrmClientActivity.NEW -> blue_4
    CrmClientActivity.ACTIVE -> green_shamrock
    CrmClientActivity.INACTIVE -> orang_9
    CrmClientActivity.NO_SALES -> gray_7
}
