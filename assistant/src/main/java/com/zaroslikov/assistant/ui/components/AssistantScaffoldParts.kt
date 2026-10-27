package com.zaroslikov.assistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaroslikov.assistant.R
import com.zaroslikov.assistant.ui.theme.assistant_accent
import com.zaroslikov.assistant.ui.theme.assistant_accent_light
import com.zaroslikov.assistant.ui.theme.black_2
import com.zaroslikov.assistant.ui.theme.error_base
import com.zaroslikov.assistant.ui.theme.gray_7
import com.zaroslikov.assistant.ui.theme.gray_8
import com.zaroslikov.assistant.ui.theme.green_6
import com.zaroslikov.assistant.ui.theme.marengo
import com.zaroslikov.assistant.ui.theme.textBold_16
import com.zaroslikov.assistant.ui.theme.textBold_18
import com.zaroslikov.assistant.ui.theme.textBold_20
import com.zaroslikov.assistant.ui.theme.text_12
import com.zaroslikov.assistant.ui.theme.text_14
import com.zaroslikov.assistant.ui.theme.text_16
import com.zaroslikov.assistant.ui.theme.white

private val CardShape = RoundedCornerShape(14.dp)

/**
 * Верхняя панель помощника: заголовок с подзаголовком по центру, «назад» слева,
 * кнопка меню справа. [menuContent] рисуется рядом с кнопкой — туда кладётся `DropdownMenu`,
 * чтобы он открывался под ней.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantTopBar(
    title: String,
    subtitle: String?,
    navigateBack: () -> Unit,
    onMenuClick: () -> Unit,
    menuContent: @Composable () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = textBold_20,
                    color = black_2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null)
                    Text(
                        text = subtitle,
                        style = text_12,
                        color = gray_7,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
            }
        },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.assistant_back),
                    tint = black_2
                )
            }
        },
        actions = {
            Box {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = stringResource(R.string.assistant_menu),
                        tint = black_2
                    )
                }
                menuContent()
            }
        },
        // Отступ под статус-бар уже даёт внешний Scaffold проекта — иначе он удваивается
        windowInsets = WindowInsets(0),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = white,
            scrolledContainerColor = white
        )
    )
}

/** Выпадающее меню помощника: автозапись без подтверждения, справка и очистка чата. */
@Composable
fun AssistantMenu(
    expanded: Boolean,
    autoConfirm: Boolean,
    onDismiss: () -> Unit,
    onAutoConfirmChange: (Boolean) -> Unit,
    onHelp: () -> Unit,
    onClear: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        containerColor = white
    ) {
        DropdownMenuItem(
            text = {
                Column(
                    modifier = Modifier.widthIn(max = 220.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.assistant_menu_auto_confirm),
                        style = text_14,
                        color = black_2
                    )
                    Text(
                        text = stringResource(R.string.assistant_menu_auto_confirm_sup),
                        style = text_12,
                        color = gray_7
                    )
                }
            },
            onClick = { onAutoConfirmChange(!autoConfirm) },
            trailingIcon = {
                Switch(
                    checked = autoConfirm,
                    onCheckedChange = onAutoConfirmChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = white,
                        checkedTrackColor = green_6,
                        uncheckedBorderColor = gray_8
                    )
                )
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.assistant_menu_help),
                    style = text_14,
                    color = black_2
                )
            },
            onClick = onHelp,
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                    contentDescription = null,
                    tint = marengo
                )
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.assistant_menu_clear),
                    style = text_14,
                    color = error_base
                )
            },
            onClick = onClear,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = null,
                    tint = error_base
                )
            }
        )
    }
}

/** Белая карточка в стиле `CardFieldNew` из :app (как `CrmCard`). */
@Composable
fun AssistantCard(
    modifier: Modifier = Modifier,
    containerColor: Color = white,
    padding: PaddingValues = PaddingValues(16.dp),
    elevation: Dp = 5.dp,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(10.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            verticalArrangement = verticalArrangement
        ) {
            content()
        }
    }
}

/** Круглый аватар помощника со «звёздочками» ИИ. */
@Composable
fun AssistantAvatar(modifier: Modifier = Modifier, size: Dp = 32.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(assistant_accent_light),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.AutoAwesome,
            contentDescription = null,
            tint = assistant_accent,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

/** Подтверждение необратимого действия; кнопка подтверждения — красная. */
@Composable
fun AssistantConfirmDialog(
    title: String,
    text: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = white,
        title = { Text(text = title, style = textBold_18, color = black_2) },
        text = { Text(text = text, style = text_16, color = gray_7) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText, style = textBold_16, color = error_base)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.assistant_cancel),
                    style = textBold_16,
                    color = marengo
                )
            }
        }
    )
}
