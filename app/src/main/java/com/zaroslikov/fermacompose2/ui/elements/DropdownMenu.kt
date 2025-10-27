@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType.Companion.PrimaryEditable
import androidx.compose.material3.ExposedDropdownMenuAnchorType.Companion.PrimaryNotEditable
import androidx.compose.material3.ExposedDropdownMenuAnchorType.Companion.SecondaryEditable
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.animal.AnimalForAddDomain
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixCategory
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.gray_5
import com.zaroslikov.fermacompose2.supportFun.toDrawRes
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.white


@Composable
fun DropdownMenuIconProductSuffix(
    setSuffix: (Suffix) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val suffixList = listOf(
        Suffix.PIECES,
        Suffix.GRAM,
        Suffix.KILOGRAM,
        Suffix.TONS,
        Suffix.LITERS,
        Suffix.CUBIC_METERS,
        Suffix.METERS
    )

    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            painterResource(R.drawable.icon_more_vert),
            contentDescription = stringResource(R.string.content_description_show_menu)
        )
        DropdownMenu(
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

@Composable
fun DropdownMenuEdit(
    onEditClick: () -> Unit,
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
            onDismissRequest = { expanded = !expanded },
        ) {
            DropdownMenuItem(
                onClick = {
                    onEditClick()
                    expanded = !expanded
                },
                text = {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(R.drawable.icon_edit),
                            contentDescription = null,
                            tint = Color(0xFF155DFC)
                        )
                        Text(text = "Редактировать")
                    }
                }
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = gray_5
            )
            DropdownMenuItem(
                onClick = {
                    onDeleteClick()
                    expanded = !expanded
                },
                text = {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(R.drawable.icon_trash),
                            contentDescription = null,
                            tint = Color(0xFFEC003F)
                        )
                        Text(text = "Удалить")
                    }
                }
            )
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

@Composable
fun DropdownMenuIconWeightSuffix(
    setSuffix: (Suffix) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val suffixWeightList = listOf(
        Suffix.GRAM,
        Suffix.KILOGRAM,
        Suffix.TONS
    )

    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            painterResource(R.drawable.icon_more_vert),
            contentDescription = stringResource(R.string.content_description_show_menu)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
        ) {
            suffixWeightList.forEach { suffix ->
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

@Composable
fun DropdownMenuIconHeightSuffix(
    setSuffix: (Suffix) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val suffixSizeList = listOf(
        Suffix.MILLIMETERS,
        Suffix.CENTIMETERS,
        Suffix.METERS
    )

    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            painterResource(R.drawable.icon_more_vert),
            contentDescription = stringResource(R.string.content_description_show_menu)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
        ) {
            suffixSizeList.forEach { suffix ->
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

@Composable
fun DropdownMenuIconCountSuffix(
    setSuffix: (Suffix) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val suffixCountList = listOf(
        Suffix.PIECES,
        Suffix.HEADS,
        Suffix.UNITS
    )

    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            painterResource(R.drawable.icon_more_vert),
            contentDescription = stringResource(R.string.content_description_show_menu)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
        ) {
            suffixCountList.forEach { suffix ->
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


@Composable
fun ExposedDropdownMenuProduct(
    title: String,
    setTitle: (String) -> Unit,
    titleList: List<String>,
    enableDropMenu: Boolean = true,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
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
                if (opening) {
                    showAllOnOpen = title.isNotBlank()
                }
            }
        ) {
            content(
                Pair(
                    Modifier
                        .menuAnchor(PrimaryEditable)
                        .fillMaxWidth(), expanded
                )
            )
            val filteredOptions =
                if (showAllOnOpen) titleList
                else titleList.filter { it.contains(title, ignoreCase = true) }


            if (filteredOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                        showAllOnOpen = false
                    },
                    shape = RoundedCornerShape(14.dp),
                    containerColor = white
                ) {
                    filteredOptions.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                setTitle(item)
                                expanded = !expanded
                            }
                        )
                        if (index != filteredOptions.lastIndex)
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = gray_5
                            )
                    }
                }
            }
        }
    }
}

@Composable
fun ExposedDropdownMenuProduct2(
    title: String,
    setTitle: (Pair<String, Suffix>) -> Unit,
    titleList: List<TitleAndSuffixDomain>,
    enableDropMenu: Boolean = true,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
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
                if (opening) {
                    showAllOnOpen = title.isNotBlank()
                }
            }
        ) {
            content(
                Pair(
                    Modifier
                        .menuAnchor(type = PrimaryEditable)
                        .fillMaxWidth(), expanded
                )
            )

            val options = if (showAllOnOpen) titleList
            else titleList.filter { it.title.contains(title, ignoreCase = true) }


            if (titleList.isNotEmpty()) {
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
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "${item.title}, ${stringResource(item.suffix.toResId())}",
                                    fontWeight = if (item.title == title) FontWeight.Bold else null
                                )
                            },

                            onClick = {
                                setTitle(item.title to item.suffix)
                                expanded = false
                                showAllOnOpen = false
                            }
                        )
                        if (index != options.lastIndex)
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = gray_5
                            )
                    }
                }
            }
        }
    }
}


@Composable
fun ExposedDropdownMenuSuffix(
    suffix: Suffix,
    setSuffix: (Suffix) -> Unit,
    enableDropMenu: Boolean = true,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    val suffixList = listOf(
        Suffix.PIECES,
        Suffix.GRAM,
        Suffix.KILOGRAM,
        Suffix.TONS,
        Suffix.LITERS,
        Suffix.CUBIC_METERS,
        Suffix.METERS
    )

    Box {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            content(
                Pair(
                    Modifier
                        .menuAnchor(type = PrimaryNotEditable)
                        .fillMaxWidth(), expanded
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                shape = RoundedCornerShape(14.dp),
                containerColor = white
            ) {
                suffixList.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(item.toResId()),
                                fontWeight = if (item == suffix) FontWeight.Bold else null
                            )
                        },
                        onClick = {
                            setSuffix(item)
                            expanded = false
                        }
                    )
                    if (index != suffixList.lastIndex)
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
fun ExposedDropdownMenuSex(
    sex: Boolean,
    setSex: (Boolean) -> Unit,
    setList: List<Triple<Boolean, Int, String>>,
    standardPadding: Boolean = true,
    isFilterUsed: Boolean = true,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            content(
                Pair(
                    Modifier
                        .menuAnchor()
                        .then(if (standardPadding) Modifier.toOutlinedText() else Modifier.fillMaxWidth()),
                    expanded
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {}
            ) {
                setList.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        trailingIcon = {
                            Icon(
                                painter = painterResource(item.second),
                                contentDescription = null
                            )
                        },
                        text = {
                            Text(
                                text = item.third,
                                fontWeight = if (sex == item.first) FontWeight.Bold else null
                            )
                        },
                        onClick = {
                            setSex(item.first)
//                            setSex(item)
//                            selectedItemIndex = index
                            expanded = !expanded
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun ExposedDropdownMenuAnimals(
    selectedItemIndex: Long,
    setTitle: (Pair<Long, String>) -> Unit,
    animalList: List<AnimalForAddDomain>,
    content: @Composable (Modifier) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            content(
                Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = !expanded },
                shape = RoundedCornerShape(14.dp),
                containerColor = white
            ) {
                animalList.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "${item.second} - ${item.third}",
                                fontWeight = if (item.first == selectedItemIndex) FontWeight.Bold else null
                            )
                        },
                        onClick = {
                            setTitle(Pair(item.first, item.second))
                            expanded = !expanded
                        }
                    )
                    if (index != animalList.lastIndex)
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
fun ExposedDropdownMenuPair(
    title: String,
    setTitle: (DomainTitleSuffixCategory) -> Unit,
    list: List<DomainTitleSuffixCategory>,
    enableDropMenu: Boolean = true,
    content: @Composable (Modifier) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showAllOnOpen by remember { mutableStateOf(false) }
    var lastTitle by remember { mutableStateOf(title) }

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
                if (opening) {
                    showAllOnOpen = title.isNotBlank()
                }
            }
        ) {
            content(
                Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            val options = if (showAllOnOpen) {
                list
            } else {
                list.filter { it.title.contains(title, ignoreCase = true) }
            }

            if (list.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                        showAllOnOpen = false
                    }
                ) {
                    options.forEach { item ->
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(item.category.toDrawRes()),
                                    contentDescription = null
                                )
                            },
                            text = {
                                Text(
                                    text = "${item.title}, ${item.suffix} - ${stringResource(item.category.toResId())}",
                                    fontWeight = if (item.title == title) FontWeight.Bold else null
                                )
                            },
                            onClick = {
                                setTitle(item)
                                expanded = !expanded
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExposedDropdownMenuIcon(
    selectedItemIndex: Int,
    setTitle: (Triple<Int, Int, Int>) -> Unit = {},
    list: List<Triple<Int, String, Int>>,
    expanded: MutableState<Boolean>,
    content: @Composable (Modifier) -> Unit,
) {

    Box {
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = {
                expanded.value
            }
        ) {
            content(
                Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                list.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    painter = painterResource(item.first),
                                    contentDescription = item.second
                                )
                                Text(
                                    text = item.second,
                                    fontWeight = if (index == selectedItemIndex) FontWeight.Bold else null
                                )
                            }
                        },
                        onClick = {
                            setTitle(Triple(index, item.first, item.third))
                            expanded.value = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun GetDropDownMenu(version: DropdownMenu, onClick: (Suffix) -> Unit) {
    return when (version) {
        DropdownMenu.WEIGHT -> DropdownMenuIconWeightSuffix(setSuffix = { onClick(it) })
        DropdownMenu.HEIGHT -> DropdownMenuIconHeightSuffix(setSuffix = { onClick(it) })
        DropdownMenu.COUNT -> DropdownMenuIconCountSuffix(setSuffix = { onClick(it) })
        DropdownMenu.ALL -> DropdownMenuIconProductSuffix(setSuffix = { onClick(it) })
    }
}

enum class DropdownMenu {
    WEIGHT, HEIGHT, COUNT, ALL
}