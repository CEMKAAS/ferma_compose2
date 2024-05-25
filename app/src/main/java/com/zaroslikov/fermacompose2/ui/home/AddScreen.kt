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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.TopAppBarFerma
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.ui.navigation.NavigationDestination
import com.zaroslikov.fermacompose2.ui.AppViewModelProvider
import com.zaroslikov.fermacompose2.ui.start.DrawerSheet
import kotlinx.coroutines.launch

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navigateToItemEntry: () -> Unit,
    navigateToItemUpdate: (Int) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    viewModel: AddViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val coroutineScope = rememberCoroutineScope()

    val showBottomSheet = remember { mutableStateOf(false) }
    val showBottomDetails = remember { mutableStateOf(false) }
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
                    showBottomFilter = showBottomSheet, //todo на фильтр
                    filterSheet = true,
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
//                        showBottomSheet.value = true Todo Разблокировать
                        coroutineScope.launch {
                            viewModel.saveItem()
                        }
                    },
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
                showBottom = showBottomSheet,
                showBottomFilter = showBottomSheetFilter,
                showBottomDetails = showBottomDetails
            )
        }
    }
}


@Composable
private fun AddBody(
    itemList: List<AddTable>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showBottom: MutableState<Boolean>,
    showBottomFilter: MutableState<Boolean>,
    showBottomDetails: MutableState<Boolean>,
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
                onItemClick = { onItemClick(it.id) },
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }

//        if (showBottomFilter.value) {
//            FilterProductSheet(
//                showBottom = showBottomFilter
//            )
//        }

        if (showBottom.value) {
            AddProductSheet(
                showBottom = showBottom,
//                insertAddTable = insertAddTable,
//                insertAddTable2 = insertAddTable2,
//                view = view
            )
        }

        if (showBottomDetails.value) {
//            AddProductDetailsSheet(
//                showBottom = showBottom,
////                insertAddTable = insertAddTable,
////                insertAddTable2 = insertAddTable2,
////                view = view
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

            Column {
                Text(
                    text = addProduct.title,
                    modifier = Modifier
                        .fillMaxWidth(0.16f)
                        .padding(6.dp),
                    fontWeight = FontWeight.SemiBold,
                )

                Text(
                    text = "${addProduct.day}.${addProduct.mount}.${addProduct.year}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(6.dp)
                )
            }

            Text(
                text = "${addProduct.count} шт.",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .padding(6.dp),
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductSheet(
    showBottom: MutableState<Boolean>,
//    insertAddTable: (AddTableInsert) -> Unit,
//    insertAddTable2: () -> Unit,
//    view: AddDetails
) {
    //запоминает состояние для BottomShee

    ModalBottomSheet(onDismissRequest = { showBottom.value = false }) {
        var title by rememberSaveable { mutableStateOf("") }
        var count by rememberSaveable { mutableStateOf("") }

        Column(modifier = Modifier.padding(5.dp, 5.dp)) {
            Text(text = "Cейчас на складе: ${"Яйца - 50 шт."}", fontSize = 20.sp)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Товар") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    Text("Введите или выберите товар")
                }
            )

            OutlinedTextField(
                value = count,
                onValueChange = { count = it },
                label = { Text("Количество") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    Text("Укажите кол-во товара, которое хотите сохранить на склад")
                },
                suffix = { Text(text = "Шт.") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            isError = () TODO
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
//                    val calendar = Calendar.getInstance()
//                    insertAddTable(
//                        AddTableInsert(
//                            id = 0,
//                            title = title,
//                            count = count.toDouble(),
//                            calendar[Calendar.DAY_OF_MONTH],
//                            (calendar[Calendar.MONTH] + 1),
//                            calendar[Calendar.YEAR],
//                            priceAll = "0"
//                        )
//                    )
//                    view.sd
//                    insertAddTable2()
                }) {
                    Text(text = "Добавить")
                    //TODO Изображение
                }
            }
        }
    }
}
