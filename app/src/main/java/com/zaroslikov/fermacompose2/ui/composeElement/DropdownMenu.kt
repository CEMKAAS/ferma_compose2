@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.composeElement

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.supportFun.PairData
import com.zaroslikov.fermacompose2.supportFun.TripleData

@Composable
fun DropdownMenuIconProductSuffix(
    setSuffix: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val piecesSuffix = stringResource(id = R.string.suffix_pieces)
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
fun ExposedDropdownMenuProduct(
    title: String,
    setTitle: (String) -> Unit,
    titleList: List<String>,
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
                    .toOutlinedText()
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
                    .toOutlinedText()
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
                    .toOutlinedText()
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
