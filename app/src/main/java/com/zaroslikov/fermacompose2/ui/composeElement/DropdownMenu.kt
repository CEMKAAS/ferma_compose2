@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.PairData
import com.zaroslikov.fermacompose2.supportFun.TripleData

@Composable
fun DropdownMenuIconProductSuffix(
    setSuffix: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val piecesSuffix = stringResource(id = R.string.suffix_pieces)
    val gramSuffix = stringResource(id = R.string.suffix_gram)
    val kilogramSuffix = stringResource(id = R.string.suffix_kilogram)
    val tonsSuffix = stringResource(id = R.string.suffix_tons)
    val litersSuffix = stringResource(id = R.string.suffix_liters)
    val cubicMetersSuffix = stringResource(id = R.string.suffix_cubic_meters)
    val metersSuffix = stringResource(id = R.string.suffix_meters)

    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.content_description_show_menu)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
        ) {
            DropdownMenuItem(
                onClick = {
                    setSuffix(piecesSuffix)
                    expanded = !expanded
                },
                text = {
                    Text(text = piecesSuffix)
                }
            )
            DropdownMenuItem(
                onClick = {
                    setSuffix(gramSuffix)
                    expanded = !expanded
                },
                text = { Text(text = kilogramSuffix) }
            )
            DropdownMenuItem(
                onClick = {
                    setSuffix(kilogramSuffix)
                    expanded = !expanded
                },
                text = { Text(text = kilogramSuffix) }
            )
            DropdownMenuItem(
                onClick = {
                    setSuffix(tonsSuffix)
                    expanded = !expanded
                },
                text = { Text(text = tonsSuffix) }
            )
            DropdownMenuItem(
                onClick = {
                    setSuffix(litersSuffix)
                    expanded = !expanded
                },
                text = { Text(text = litersSuffix) }
            )
            DropdownMenuItem(
                onClick = {
                    setSuffix(cubicMetersSuffix)
                    expanded = !expanded
                },
                text = { Text(text = cubicMetersSuffix) }
            )
            DropdownMenuItem(
                onClick = {
                    setSuffix(metersSuffix)
                    expanded = !expanded
                },
                text = { Text(text = metersSuffix) }
            )
        }
    }
}

@Composable
fun DropdownMenuIconWeightSuffix(
    setSuffix: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val gramSuffix = stringResource(id = R.string.suffix_gram)
    val kilogramSuffix = stringResource(id = R.string.suffix_kilogram)
    val tonsSuffix = stringResource(id = R.string.suffix_tons)

    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.content_description_show_menu)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
        ) {
            DropdownMenuItem(
                onClick = {
                    setSuffix(gramSuffix)
                    expanded = !expanded
                },
                text = {
                    Text(text = gramSuffix)
                }
            )
            DropdownMenuItem(
                onClick = {
                    setSuffix(kilogramSuffix)
                    expanded = !expanded
                },
                text = { Text(text = kilogramSuffix) }
            )
            DropdownMenuItem(
                onClick = {
                    setSuffix(tonsSuffix)
                    expanded = !expanded
                },
                text = { Text(text = tonsSuffix) }
            )
        }
    }
}

@Composable
fun DropdownMenuIconHeightSuffix(
    setSuffix: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }


    val millimetersSuffix = stringResource(id = R.string.suffix_millimeters)
    val centimetersSuffix = stringResource(id = R.string.suffix_centimetre)
    val metersSuffix = stringResource(id = R.string.suffix_meters)

    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.content_description_show_menu)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
        ) {
            DropdownMenuItem(
                onClick = {
                    setSuffix(millimetersSuffix)
                    expanded = !expanded
                },
                text = {
                    Text(text = millimetersSuffix)
                }
            )
            DropdownMenuItem(
                onClick = {
                    setSuffix(centimetersSuffix)
                    expanded = !expanded
                },
                text = { Text(text = centimetersSuffix) }
            )
            DropdownMenuItem(
                onClick = {
                    setSuffix(metersSuffix)
                    expanded = !expanded
                },
                text = { Text(text = metersSuffix) }
            )
        }
    }
}

@Composable
fun DropdownMenuIconCountSuffix(
    setSuffix: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val piecesSuffix = stringResource(id = R.string.suffix_pieces)
    val headSuffix = stringResource(id = R.string.suffix_head)
    val unitsSuffix = stringResource(id = R.string.suffix_units)

    IconButton(onClick = { expanded = !expanded }) {
        Icon(
            Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.content_description_show_menu)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
        ) {
            DropdownMenuItem(
                onClick = {
                    setSuffix(piecesSuffix)
                    expanded = !expanded
                },
                text = {
                    Text(text = piecesSuffix)
                }
            )
            DropdownMenuItem(
                onClick = {
                    setSuffix(headSuffix)
                    expanded = !expanded
                },
                text = { Text(text = headSuffix) }
            )
            DropdownMenuItem(
                onClick = {
                    setSuffix(unitsSuffix)
                    expanded = !expanded
                },
                text = { Text(text = unitsSuffix) }
            )
        }
    }
}


@Composable
fun ExposedDropdownMenuProduct(
    title: String,
    setTitle: (String) -> Unit,
    titleList: List<String>,
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
                        .fillMaxWidth(), expanded
                )
            )
            val filteredOptions =
                titleList.filter { it.contains(title, ignoreCase = true) }

            if (filteredOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
//                            expanded = false
                    }
                ) {
                    filteredOptions.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item) },
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
fun ExposedDropdownMenuSex(
    title: String,
    setTitle: (String) -> Unit,
    titleList: List<String>,
    standardPadding: Boolean = true,
    isFilterUsed: Boolean = true,
    content: @Composable (Pair<Modifier, Boolean>) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
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

            val filteredOptions =
                if (isFilterUsed) titleList.filter {
                    it.contains(
                        title,
                        ignoreCase = true
                    )
                } else titleList

            if (filteredOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {}
                ) {
                    filteredOptions.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = item,
                                    fontWeight = if (index == selectedItemIndex) FontWeight.Bold else null
                                )
                            },
                            onClick = {
                                setTitle(item)
                                selectedItemIndex = index
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
fun ExposedDropdownMenuAnimals(
    title: String,
    selectedItemIndex: Int,
    setTitle: (Pair<Int, String>) -> Unit,
    animalList: List<TripleData>,
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
                onDismissRequest = { expanded = !expanded }
            ) {
                animalList.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "${item.second} - ${item.third}",
                                fontWeight = if (index == selectedItemIndex) FontWeight.Bold else null
                            )
                        },
                        onClick = {
                            setTitle(Pair(index, item.second))
                            expanded = !expanded
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ExposedDropdownMenuPair(
    title: String,
    selectedItemIndex: Int,
    setTitle: (Triple<Int, String, String>) -> Unit,
    list: List<PairData>,
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
            val filteredOptions =
                list.filter { it.first.contains(title, ignoreCase = true) }
            if (filteredOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = !expanded }
                ) {
                    list.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "${item.first} - ${item.second}",
                                    fontWeight = if (index == selectedItemIndex) FontWeight.Bold else null
                                )
                            },
                            onClick = {
                                setTitle(Triple(index, item.first, item.second))
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
fun GetDropDownMenu(version: Int, onClick: (String) -> Unit) {
    return when (version) {
        0 -> DropdownMenuIconWeightSuffix(setSuffix = { onClick(it) })
        1 -> DropdownMenuIconHeightSuffix(setSuffix = { onClick(it) })
        2 -> DropdownMenuIconCountSuffix(setSuffix = { onClick(it) })
        3 -> {}
        else -> DropdownMenuIconProductSuffix(setSuffix = { onClick(it) })
    }
}