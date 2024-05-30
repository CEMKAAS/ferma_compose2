/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaroslikov.fermacompose2.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarFerma
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.sale.navigateId
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navigateToItemUpdate: (navigateId) -> Unit,
    navigateToItemAdd: (Int) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    viewModel: AddViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val idProject = viewModel.itemId

    val coroutineScope = rememberCoroutineScope()

    val showBottomSheetFilter = remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(
                scope = coroutineScope,
                navController = { },
                drawerState = drawerState,
                0,//ToDo 3
                "1"
            )
        },
    ) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBarFerma(
                    title = "Мои Товары",
                    scope = coroutineScope,
                    drawerState = drawerState,
                    showBottomFilter = showBottomSheetFilter, //todo на фильтр
                    filterSheet = true,
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigateToItemAdd(idProject) },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .padding(
                            end = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateEndPadding(LocalLayoutDirection.current)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.item_entry_title) // TODO Преименовать
                    )
                }
            },
        ) { innerPadding ->
            AddBody(
                itemList = homeUiState.itemList,
                onItemClick = navigateToItemUpdate,
                modifier = modifier.fillMaxSize(),
                contentPadding = innerPadding,
                showBottomFilter = showBottomSheetFilter
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBody(
    itemList: List<AddTable>,
    onItemClick: (navigateId) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showBottomFilter: MutableState<Boolean>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (itemList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_item_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {
            InventoryList(
                itemList = itemList,
                onItemClick = { onItemClick(navigateId(it.id, it.idPT)) },
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }

        if (showBottomFilter.value) {
//            FilterProductSheet(
//                showBottom = showBottomFilter
//            )
        }

    }
}

@Composable
private fun InventoryList(
    itemList: List<AddTable>,
    onItemClick: (AddTable) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = itemList, key = { it.id }) { item ->
           AddProductCard(addProduct = item,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onItemClick(item) })
        }
    }
}

@Composable
fun AddProductCard(
    addProduct: AddTable,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text(
                    text = addProduct.title,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(6.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                if (addProduct.category != "") {
                    Text(
                        text = "Категория: ${addProduct.category}",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 3.dp, horizontal = 6.dp)
                    )
                }
                if (addProduct.animal != "") {
                    Text(
                        text = "Животное: ${addProduct.animal}",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 3.dp, horizontal = 6.dp)
                    )
                }
                Text(
                    text = "Дата: ${addProduct.day}.${addProduct.mount}.${addProduct.year}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(vertical = 3.dp, horizontal = 6.dp)
                )
            }
            Text(
                text = "${addProduct.count} ${addProduct.suffix}",
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth(1f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }
    }
}


@Preview()
@Composable
fun Card() {
    AddProductCard(
        addProduct = AddTable(
            0,
            "Мясо Коровы",
            150.50,
            25,
            12,
            2025,
            "0",
            1,
            "кг",
            "Животноводство",
            "Борька"
        )
    )
}


data class navigateId(
    val id: Int,
    val idPT: Int

)

//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddProductSheetPP() {
//
//    var title by remember { mutableStateOf("") }
//    var count by rememberSaveable { mutableStateOf("") }
//    var category by rememberSaveable { mutableStateOf("") }
//
//    val coffeeDrinks = arrayOf("Americano", "Cappuccino", "Espresso", "Latte", "Mocha")
//    var expanded by remember { mutableStateOf(false) }
//
//
//    var suffix by remember { mutableStateOf("Ед.") }
//    var expandedSuf by remember { mutableStateOf(false) }
//
//
//    Column(modifier = Modifier.padding(5.dp, 5.dp)) {
//        Box {
//                ExposedDropdownMenuBox(
//                    expanded = expanded,
//                    onExpandedChange = {
//                        expanded = !expanded
//                    }
//                ) {
//                    OutlinedTextField(
//                        value = title,
//                        onValueChange = { title = it },
//                        label = { Text(text = "Товар") },
//                        supportingText = {
//                            Text("Выберите или укажите товар")
//                        },
//                        modifier = Modifier
//                            .menuAnchor()
//                            .fillMaxWidth()
//                    )
//
//                    val filteredOptions =
//                        coffeeDrinks.filter { it.contains(title, ignoreCase = true) }
//                    if (filteredOptions.isNotEmpty()) {
//                        ExposedDropdownMenu(
//                            expanded = expanded,
//                            onDismissRequest = {
//                                // We shouldn't hide the menu when the user enters/removes any character
//                            }
//                        ) {
//                            filteredOptions.forEach { item ->
//                                DropdownMenuItem(
//                                    text = { Text(text = item) },
//                                    onClick = {
//                                        title = item
//                                        expanded = false
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//    Box {
//        OutlinedTextField(
//            value = count,
//            onValueChange = { count = it },
//            label = { Text("Количество") },
//            modifier = Modifier.fillMaxWidth(),
//            supportingText = {
//                Text("Укажите кол-во товара, которое хотите сохранить на склад")
//            },
//            trailingIcon = {
//                IconButton(onClick = { expandedSuf = true }) {
//                    Icon(Icons.Default.MoreVert, contentDescription = "Показать меню")
//                }
//            },
//            suffix = {
//                Text(text = suffix)
//            },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
////            isError = () TODO
//        )
//        DropdownMenu(
//            expanded = expandedSuf,
//            onDismissRequest = { expandedSuf = false },
//            //todo чтобы был слева
//        ) {
//            DropdownMenuItem(
//                onClick = { suffix = "Шт." },
//                text = { Text("Шт.") }
//            )
//            DropdownMenuItem(
//                onClick = { suffix = "Кг." },
//                text = { Text("Кг.") }
//            )
//            DropdownMenuItem(
//                onClick = { suffix = "Л." },
//                text = { Text("Л.") }
//            )
//        }
//
//    }
//
//        OutlinedTextField(
//            value = category,
//            onValueChange = { category = it },
//            label = { Text("Категория") },
//            modifier = Modifier.fillMaxWidth(),
//            supportingText = {
//                Text("Укажите категорию в которую хотите отнести товар")
//            },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
////            isError = () TODO
//        )
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 10.dp),
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Button(onClick = {
//            }) {
//                Text(text = "Добавить")
//                //TODO Изображение
//            }
//        }
//    }
//}


//@Preview(showBackground = true)
//@Composable
//fun AddProductSheetPPRewie() {
//    AddProductSheetPP()
//}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddProductDetailsSheet(
//    showBottom: MutableState<Boolean>,
//    addTable: AddTable,
//    saveInRoomAdd: (AddTableInsert) -> Unit,
//    sheetState: SheetState
//) {
//
//    //Дата
//    var openDialog by remember { mutableStateOf(false) }
//    val datePickerState = rememberDatePickerState()
//
//    ModalBottomSheet(
//        onDismissRequest = { showBottom.value = false },
//        sheetState = sheetState
//    ) {
//
//        var title by rememberSaveable { mutableStateOf(addTable.title) }
//        var count by rememberSaveable { mutableStateOf(addTable.count.toString()) }
//        var date by rememberSaveable { mutableStateOf("${addTable.day}.${addTable.mount}.${addTable.year}") }
//
//        //запоминает состояние для BottomShee
//        if (openDialog) {
//            DatePickerDialogSample(datePickerState, date) { date1 ->
//                date = date1
//                openDialog = false
//            }
//        }
//
//
//        Column(modifier = Modifier.padding(5.dp, 5.dp)) {
//
//            OutlinedTextField(
//                value = title,
//                onValueChange = { title = it },
//                label = { Text("Товар") },
//                modifier = Modifier.fillMaxWidth(),
//                supportingText = {
//                    Text("Введите или выберите товар")
//                }
//            )
//
//            OutlinedTextField(
//                value = count,
//                onValueChange = { count = it },
//                label = { Text("Количество") },
//                modifier = Modifier.fillMaxWidth(),
//                supportingText = {
//                    Text("Укажите кол-во товара, которое хотите сохранить на склад")
//                },
//                suffix = { Text(text = "Шт.") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
////            isError = () TODO
//            )
//
//            OutlinedTextField(
//                value = date,
//                onValueChange = { date = it },
//                label = { Text("Дата") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { openDialog = true },
//                supportingText = {
//                    Text("Выберите дату")
//                },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
//
////            isError = () TODO
//            )
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 10.dp),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Button(onClick = {
//
//                }) {
//                    Text(text = "Добавить")
//                    //TODO Изображение
//                }
//            }
//        }
//    }
//}