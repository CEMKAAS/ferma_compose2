package com.zaroslikov.fermacompose2.ui.elements.TextField

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.animal.AnimalForAddDomain
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory
import com.zaroslikov.domain.models.enums.FilterDate
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.grey_3
import com.zaroslikov.fermacompose2.supportFun.toColorList
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.IconDone
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.violet_1


@Composable
fun DropdownMenuEdit(
    color: Color = grey_3,
    onActiveClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null,
    onArchiveClick: (() -> Unit)? = null,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = !expanded }, modifier = Modifier.size(18.dp)) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More"
        )

        BaseDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val items = buildList {
                onActiveClick?.let {
                    add(MenuItemData("Активировать закладку", R.drawable.outline_edit_square_24, color, it))
                }
                onEditClick?.let {
                    add(MenuItemData("Редактировать", R.drawable.outline_edit_square_24, color, it))
                }
                onArchiveClick?.let {
                    add(MenuItemData("Архивировать", R.drawable.baseline_archive_24, color, it))
                }
                add(MenuItemData("Удалить", R.drawable.icon_trash, error_base, onDeleteClick))
            }

            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item.title,
                            style = text_14,
                            color = if (item.color == error_base) error_base else Color.Unspecified
                        )
                    },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            painter = painterResource(item.icon),
                            contentDescription = null,
                            tint = color
                        )
                    },
                    onClick = {
                        item.onClick()
                        expanded = false
                    }
                )
            }
        }
    }
}

private data class MenuItemData(
    val title: String,
    @param:DrawableRes val icon: Int,
    val color: Color,
    val onClick: () -> Unit
)


@Composable
fun ExposedDropdownMenuCategoryBuyer(
    title: String,
    setTitle: (String) -> Unit,
    titleList: List<String>,
    enableDropMenu: Boolean = true,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
) {
    BaseExposedDropdownMenu2(
        title = title,
        labelSelector = { it },
        list = titleList,
        content = content
    ) { index, item, closeMenu ->
        val trailingIcon: @Composable (() -> Unit)? = if (item == title) {
            { Icon(Icons.Default.Done, contentDescription = null) }
        } else null
        DropdownMenuItem(
            trailingIcon = trailingIcon,
            text = { Text(text = item) },
            onClick = {
                setTitle(item)
                closeMenu()
            }
        )
    }
}


@Composable
fun ExposedDropdownMenuFilterDate(
    title: FilterDate,
    setTitle: (FilterDate) -> Unit,
    titleList: List<FilterDate>,
    enableDropMenu: Boolean = true,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
) {
    BaseExposedDropdownMenu2(
        title = stringResource(title.toResId()),
        labelSelector = { "" },
        list = titleList,
        content = content
    ) { index, item, closeMenu ->
        val trailingIcon: @Composable (() -> Unit)? = if (item == title) {
            { Icon(Icons.Default.Done, contentDescription = null) }
        } else null
        DropdownMenuItem(
            trailingIcon = trailingIcon,
            text = { Text(text = stringResource(item.toResId())) },
            onClick = {
                setTitle(item)
                closeMenu()
            }
        )
    }
}

@Composable
fun ExposedDropdownMenuProduct(
    title: String,
    setTitle: (Pair<String, Suffix>) -> Unit,
    titleList: List<TitleAndSuffixDomain>,
    enableDropMenu: Boolean = true,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
) {
    BaseExposedDropdownMenu2(
        title = title,
        labelSelector = { it.title },
        list = titleList,
        content = content
    ) { index, item, closeMenu ->
        val trailingIcon: @Composable (() -> Unit)? = if (item.title == title) {
            { Icon(Icons.Default.Done, contentDescription = null) }
        } else null
        DropdownMenuItem(
            text = {
                Text(
                    text = "${item.title}, ${stringResource(item.suffix.toResId())}",
                )
            },
            trailingIcon = trailingIcon,
            onClick = {
                setTitle(item.title to item.suffix)
                closeMenu()
            }
        )
    }
}

@Composable
fun ExposedDropdownMenuSuffix(
    suffix: Suffix,
    setSuffix: (Suffix) -> Unit,
    suffixList: List<Suffix>,
    enableDropMenu: Boolean = true,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
) {
    BaseExposedDropdownMenu(
        list = suffixList,
        content = content
    ) { index, item, closeMenu ->
        val trailingIcon: @Composable (() -> Unit)? = if (item == suffix) {
            { Icon(Icons.Default.Done, contentDescription = null) }
        } else null
        DropdownMenuItem(
            text = { Text(text = stringResource(item.toResId())) },
            trailingIcon = trailingIcon,
            onClick = {
                setSuffix(item)
                closeMenu()
            }
        )
    }
}

@Composable
fun <T> ExposedDropdownMenuEnum(
    valueList: List<T>,
    enabled: Boolean = true,
    dropdownMenuItem: @Composable (Int, T, () -> Unit) -> Unit,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
) {
    BaseExposedDropdownMenu(
        list = valueList,
        content = content,
        enabled = enabled,
        dropdownMenuItem = dropdownMenuItem
    )
}

@Composable
fun ExposedDropdownMenuStatusWriteOff(
    status: Boolean,
    setStatus: (Pair<Boolean, Int>) -> Unit,
    statusList: List<Triple<Int, Int, Boolean>>,
    enableDropMenu: Boolean = true,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
) {
    BaseExposedDropdownMenu(
        list = statusList,
        content = content
    ) { index, item, closeMenu ->
        val trailingIcon: @Composable (() -> Unit)? = if (item.third == status) {
            { IconDone() }
        } else null
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    painterResource(item.first),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp), tint = if (index == 0) violet_1 else error_base
                )
            },
            text = { Text(text = stringResource(item.second)) },
            trailingIcon = trailingIcon,
            onClick = {
                setStatus(item.third to index)
                closeMenu()
            }
        )
    }
}

@Composable
fun ExposedDropdownMenuSex(
    sex: Boolean,
    setSex: (Boolean) -> Unit,
    setList: List<Triple<Boolean, Int, String>>,
    standardPadding: Boolean = true,
    isFilterUsed: Boolean = true,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
) {
    BaseExposedDropdownMenu(
        list = setList,
        content = content
    ) { index, item, closeMenu ->
        val trailingIcon: @Composable (() -> Unit)? = if (sex == item.first) {
            { Icon(Icons.Default.Done, contentDescription = null) }
        } else null
        DropdownMenuItem(
            leadingIcon = { Icon(painterResource(item.second), contentDescription = null) },
            text = { Text(text = item.third) },
            trailingIcon = trailingIcon,
            onClick = {
                setSex(item.first)
//                            setSex(item)
//                            selectedItemIndex = index
                closeMenu()
            }
        )
    }
}

@Composable
fun ExposedDropdownMenuAnimals(
    selectedItemIndex: Long,
    setTitle: (Pair<Long, String>) -> Unit,
    animalList: List<AnimalForAddDomain>,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
) {
    BaseExposedDropdownMenu(
        list = animalList,
        content = content
    ) { index, item, closeMenu ->
        val trailingIcon: @Composable (() -> Unit)? = if (item.first == selectedItemIndex) {
            { Icon(Icons.Default.Done, contentDescription = null) }
        } else null
        DropdownMenuItem(
            trailingIcon = trailingIcon,
            text = { Text(text = "${item.second} - ${item.third}") },
            onClick = {
                setTitle(Pair(item.first, item.second))
                closeMenu()
            }
        )
    }
}

@Composable
fun ExposedDropdownMenuPair(
    title: String,
    setTitle: (DomainTitleSuffixCategory) -> Unit,
    list: List<DomainTitleSuffixCategory>,
    enableDropMenu: Boolean = true,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
) {
    BaseExposedDropdownMenu2(
        title = title,
        labelSelector = { it.title },
        list = list,
        content = content
    ) { index, item, closeMenu ->
        val trailingIcon: @Composable (() -> Unit)? = if (item.title == title) {
            { IconDone() }
        } else null
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    painterResource(item.category.toDrawRes()),
                    null,
                    tint = item.category.toColorList()
                )
            },
            text = {
                Text(
                    text = "${item.title}, ${stringResource(item.suffix.toResId())} " +
                            "- ${stringResource(item.category.toResId())}",
                )
            },
            trailingIcon = trailingIcon,
            onClick = {
                setTitle(item)
                closeMenu()
            }
        )
    }
}

@Composable
fun ExposedDropdownMenuIcon(
    selectedItemIndex: Int,
    setTitle: (Triple<Int, Int, Int>) -> Unit = {},
    list: List<Triple<Int, String, Int>>,
    expanded: MutableState<Boolean>,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
) {
    BaseExposedDropdownMenu(
        list = list,
        content = content
    ) { index, item, closeMenu ->
        val trailingIcon: @Composable (() -> Unit)? = if (index == selectedItemIndex) {
            { Icon(Icons.Default.Done, contentDescription = null) }
        } else null
        DropdownMenuItem(
            leadingIcon = { Icon(painterResource(item.first), item.second) },
            text = { Text(text = item.second) },
            trailingIcon = trailingIcon,
            onClick = {
                setTitle(Triple(index, item.first, item.third))
                expanded.value = false
            }
        )
    }
}

@Composable
fun GetDropDownMenu(version: DropdownMenu, onClick: (Suffix) -> Unit) {
    val suffixWeightList = listOf(
        Suffix.GRAM,
        Suffix.KILOGRAM,
        Suffix.TONS
    )
    val suffixList = listOf(
        Suffix.PIECES,
        Suffix.GRAM,
        Suffix.KILOGRAM,
        Suffix.TONS,
        Suffix.LITERS,
        Suffix.CUBIC_METERS,
        Suffix.METERS
    )
    val suffixSizeList = listOf(
        Suffix.MILLIMETERS,
        Suffix.CENTIMETERS,
        Suffix.METERS
    )
    val suffixCountList = listOf(
        Suffix.PIECES,
        Suffix.HEADS,
        Suffix.UNITS
    )
    return when (version) {
        DropdownMenu.WEIGHT -> DropdownMenuIconProductSuffix(
            setSuffix = { onClick(it) },
            suffixList = suffixWeightList
        )

        DropdownMenu.HEIGHT -> DropdownMenuIconProductSuffix(
            setSuffix = { onClick(it) },
            suffixList = suffixSizeList
        )

        DropdownMenu.COUNT -> DropdownMenuIconProductSuffix(
            setSuffix = { onClick(it) },
            suffixList = suffixCountList
        )

        DropdownMenu.ALL -> DropdownMenuIconProductSuffix(
            setSuffix = { onClick(it) },
            suffixList = suffixList
        )
    }
}

@Composable
fun DropdownMenuIconProductSuffix(
    suffixList: List<Suffix>,
    setSuffix: (Suffix) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            painterResource(R.drawable.icon_more_vert),
            contentDescription = stringResource(R.string.content_description_show_menu)
        )
        BaseDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
        ) {
            suffixList.forEach { suffix ->
                DropdownMenuItem(
                    onClick = {
                        setSuffix(suffix)
                        expanded = !expanded
                    },
                    text = {
                        Text(text = stringResource(suffix.toResId()))
                    }
                )
            }
        }
    }
}

enum class DropdownMenu {
    WEIGHT, HEIGHT, COUNT, ALL
}