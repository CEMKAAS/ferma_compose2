package com.zaroslikov.todo.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Agriculture
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.EggAlt
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.TodoListIcon
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.white

// Код значка списка → картинка. Только здесь: в БД и бэкапе лежит код (`TodoListIcon.code`)

fun TodoListIcon.imageVector(): ImageVector = when (this) {
    TodoListIcon.LIST -> Icons.AutoMirrored.Rounded.List
    TodoListIcon.ANIMALS -> Icons.Rounded.Pets
    TodoListIcon.PLANTS -> Icons.Rounded.Eco
    TodoListIcon.FLOWERS -> Icons.Rounded.LocalFlorist
    TodoListIcon.MACHINERY -> Icons.Rounded.Agriculture
    TodoListIcon.EGGS -> Icons.Rounded.EggAlt
    TodoListIcon.SHOPPING -> Icons.Rounded.ShoppingCart
    TodoListIcon.REPAIR -> Icons.Rounded.Build
    TodoListIcon.HOME -> Icons.Rounded.Home
    TodoListIcon.WATER -> Icons.Rounded.WaterDrop
    TodoListIcon.MONEY -> Icons.Rounded.Payments
    TodoListIcon.CALENDAR -> Icons.Rounded.Event
}

@StringRes
fun TodoListIcon.titleRes(): Int = when (this) {
    TodoListIcon.LIST -> R.string.todo_list_icon_list
    TodoListIcon.ANIMALS -> R.string.todo_list_icon_animals
    TodoListIcon.PLANTS -> R.string.todo_list_icon_plants
    TodoListIcon.FLOWERS -> R.string.todo_list_icon_flowers
    TodoListIcon.MACHINERY -> R.string.todo_list_icon_machinery
    TodoListIcon.EGGS -> R.string.todo_list_icon_eggs
    TodoListIcon.SHOPPING -> R.string.todo_list_icon_shopping
    TodoListIcon.REPAIR -> R.string.todo_list_icon_repair
    TodoListIcon.HOME -> R.string.todo_list_icon_home
    TodoListIcon.WATER -> R.string.todo_list_icon_water
    TodoListIcon.MONEY -> R.string.todo_list_icon_money
    TodoListIcon.CALENDAR -> R.string.todo_list_icon_calendar
}

/** Значок списка: белая картинка в круге цвета списка; [iconSize] — размер картинки. */
@Composable
fun TodoListBadge(
    icon: TodoListIcon,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 30.dp,
    iconSize: Dp = 18.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon.imageVector(), contentDescription = null, tint = white, modifier = Modifier.size(iconSize))
    }
}

/** Название списка в отдельной строке метаданных задачи. */
@Composable
internal fun TodoTaskListLabel(title: String, icon: TodoListIcon, style: TextStyle) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon.imageVector(),
            contentDescription = null,
            tint = gray_7,
            modifier = Modifier.size(14.dp)
        )
        Text(text = title, style = style, color = gray_7, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
