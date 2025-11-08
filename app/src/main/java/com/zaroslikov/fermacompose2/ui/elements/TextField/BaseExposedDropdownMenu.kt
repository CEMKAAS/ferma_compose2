@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.elements.TextField

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuAnchorType.Companion.PrimaryEditable
import androidx.compose.material3.ExposedDropdownMenuAnchorType.Companion.PrimaryNotEditable
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.gray_5
import com.zaroslikov.fermacompose2.white


@Composable
fun <T> BaseExposedDropdownMenu(
    type: ExposedDropdownMenuAnchorType = PrimaryNotEditable,
    list: List<T>,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
    dropdownMenuItem: @Composable (Int, T, () -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            content(
                Pair(
                    Modifier
                        .menuAnchor(type = type)
                        .fillMaxWidth(),
                    expanded
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                shape = RoundedCornerShape(14.dp),
                containerColor = white
            ) {
                list.forEachIndexed { index, item ->
                    dropdownMenuItem(index, item) {
                        expanded = false
                    }
                    if (index != list.lastIndex)
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = gray_5
                        )
                }
            }
        }
    }
}

@Composable
fun <T> BaseExposedDropdownMenu2(
    type: ExposedDropdownMenuAnchorType = PrimaryEditable,
    title: String,
    labelSelector: (T) -> String,
    list: List<T>,
    enableDropMenu: Boolean = true,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
    dropdownMenuItem: @Composable (Int, T, () -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showAllOnOpen by remember { mutableStateOf(false) } // показать весь список при первом открытии
    var lastTitle by remember { mutableStateOf(title) }

    // Если пользователь меняет текст, при открытом меню снова включаем фильтрацию
    LaunchedEffect(title) {
        if (title != lastTitle) {
            lastTitle = title
            if (expanded) showAllOnOpen = false
        }
    }

    Box {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                if (!enableDropMenu) return@ExposedDropdownMenuBox
                val opening = !expanded
                expanded = !expanded
                if (opening) showAllOnOpen = title.isNotBlank()
            }
        ) {
            content(
                Pair(
                    Modifier
                        .menuAnchor(type = type)
                        .fillMaxWidth(),
                    expanded
                )
            )
            val options = if (showAllOnOpen) list
            else list.filter { labelSelector(it).contains(title, ignoreCase = true) }
            if (list.isNotEmpty())
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                        showAllOnOpen = false
                    },
                    shape = RoundedCornerShape(14.dp),
                    containerColor = white
                ) {
                    options.forEachIndexed { index, item ->
                        dropdownMenuItem(index, item) {
                            expanded = false
                            showAllOnOpen = false
                        }
                        if (index != list.lastIndex)
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = gray_5
                            )
                    }
                }
        }
    }
}

@Composable
fun BaseDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(14.dp),
        containerColor = white
    ) {
        content()
    }
}
