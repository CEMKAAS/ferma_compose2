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

package com.zaroslikov.fermacompose2.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesDestination
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesEditDestination
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesEditProduct
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesEntryDestination
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesEntryProduct
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesScreen
import com.zaroslikov.fermacompose2.ui.finance.FinanceCategoryDestination
import com.zaroslikov.fermacompose2.ui.finance.FinanceCategoryScreen
import com.zaroslikov.fermacompose2.ui.finance.FinanceDestination
import com.zaroslikov.fermacompose2.ui.finance.FinanceIncomeExpensesDestination
import com.zaroslikov.fermacompose2.ui.finance.FinanceIncomeExpensesScreen
import com.zaroslikov.fermacompose2.ui.finance.FinanceScreen
import com.zaroslikov.fermacompose2.ui.home.AddEditDestination
import com.zaroslikov.fermacompose2.ui.home.AddEditProduct
import com.zaroslikov.fermacompose2.ui.home.AddEntryDestination
import com.zaroslikov.fermacompose2.ui.home.AddEntryProduct
import com.zaroslikov.fermacompose2.ui.home.AddScreen
import com.zaroslikov.fermacompose2.ui.home.HomeDestination
import com.zaroslikov.fermacompose2.ui.sale.SaleDestination
import com.zaroslikov.fermacompose2.ui.sale.SaleEditDestination
import com.zaroslikov.fermacompose2.ui.sale.SaleEditProduct
import com.zaroslikov.fermacompose2.ui.sale.SaleEntryDestination
import com.zaroslikov.fermacompose2.ui.sale.SaleEntryProduct
import com.zaroslikov.fermacompose2.ui.sale.SaleScreen
import com.zaroslikov.fermacompose2.ui.start.StartDestination
import com.zaroslikov.fermacompose2.ui.start.StartScreen
import com.zaroslikov.fermacompose2.ui.start.add.AddProject
import com.zaroslikov.fermacompose2.ui.start.add.ChoiseProject
import com.zaroslikov.fermacompose2.ui.start.add.ChoiseProjectDestination
import com.zaroslikov.fermacompose2.ui.start.add.ProjectAddDestination
import com.zaroslikov.fermacompose2.ui.start.add.incubator.AddIncubator
import com.zaroslikov.fermacompose2.ui.start.add.incubator.AddIncubatorDestination
import com.zaroslikov.fermacompose2.ui.start.add.incubator.AddIncubatorTwo
import com.zaroslikov.fermacompose2.ui.start.add.incubator.AddIncubatorTwoDestination
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseDestination
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseScreen
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffDestination
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffEditDestination
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffEditProduct
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffEntryDestination
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffEntryProduct
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffScreen


/**
 * Provides Navigation graph for the application.
 */
@Composable
fun InventoryNavHost(
    navController: NavHostController,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = StartDestination.route,
        modifier = modifier,
    ) {

        composable(route = StartDestination.route) {
            StartScreen(navController = navController,//TODO переделать на адд ADD
                navigateToItemUpdate = {
                    navController.navigate("${HomeDestination.route}/${it}")
                })
        }

        composable(route = ChoiseProjectDestination.route) {
            ChoiseProject(navigateBack = { navController.popBackStack() },
                navigateProject = { navController.navigate(it) })
        }


        composable(route = ProjectAddDestination.route) {
            AddProject(navController = navController,//TODO переделать на старт
                navigateBack = { navController.popBackStack() })
        }

        composable(route = AddIncubatorDestination.route) {
            AddIncubator(
                navigateBack = { navController.popBackStack() },
                navigateContinue = { navController.navigate(
                    "${AddIncubatorTwoDestination.route}/${it}}"
                )})
        }

        composable(
            route = AddIncubatorTwoDestination.routeWithArgs,
            arguments = listOf(navArgument(AddIncubatorTwoDestination.itemIdArg) {
                type = NavType.StringArrayType
            })
        ) {
            AddIncubatorTwo(
                navigateBack = { navController.popBackStack() },
                navigateContinue = { navController.navigate(
                    StartDestination.route
                )})
        }


        composable(
            route = WarehouseDestination.routeWithArgs,
            arguments = listOf(navArgument(WarehouseDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            WarehouseScreen(
                navigateToStart = { navController.navigate(StartDestination.route) },
                navigateToModalSheet = { navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}") },
                navigateToItemUpdate = {},
                navigateToItemAdd = {},
                drawerState = drawerState
            )
        }
        composable(
            route = FinanceDestination.routeWithArgs,
            arguments = listOf(navArgument(FinanceDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            FinanceScreen(
                drawerState = drawerState,
                navigateToStart = {
                    navController.navigate(StartDestination.route)
                },
                navigateToModalSheet = {
                    navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}")
                },
                navigateToCategory = {
                    navController.navigate(
                        "${FinanceCategoryDestination.route}/${it.idPT}/${it.category}/${it.incomeBoolean}"
                    )
                },
                navigateToIncomeExpenses = {
                    navController.navigate(
                        "${FinanceIncomeExpensesDestination.route}/${it.idPT}/${it.incomeBoolean}"
                    )
                }
            )
        }

        composable(
            route = FinanceCategoryDestination.routeWithArgs,
            arguments = listOf(
                navArgument(FinanceCategoryDestination.itemIdArg) {
                    type = NavType.IntType
                },
                navArgument(FinanceCategoryDestination.itemIdArgTwo) {
                    type = NavType.StringType
                }, navArgument(FinanceCategoryDestination.itemIdArgThree) {
                    type = NavType.BoolType
                }

            )
        ) {
            FinanceCategoryScreen(
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = FinanceIncomeExpensesDestination.routeWithArgs,
            arguments = listOf(
                navArgument(FinanceIncomeExpensesDestination.itemIdArg) {
                    type = NavType.IntType
                },
                navArgument(FinanceIncomeExpensesDestination.itemIdArgTwo) {
                    type = NavType.BoolType
                }
            )
        ) {
            FinanceIncomeExpensesScreen(navigateBack = { navController.popBackStack() })
        }


        //Add
        composable(
            route = HomeDestination.routeWithArgs,
            arguments = listOf(navArgument(HomeDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            AddScreen(
                drawerState = drawerState,
                navigateToStart = {
                    navController.navigate(StartDestination.route)
                },
                navigateToModalSheet = {
                    navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}")
                },
                navigateToItemAdd = {
                    navController.navigate(
                        "${AddEntryDestination.route}/${it}"
                    )
                },
                navigateToItemUpdate = {
                    navController.navigate(
                        "${AddEditDestination.route}/${it.id}/${it.idPT}"
                    )
                }
            )
        }
        composable(
            route = AddEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(AddEntryDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            AddEntryProduct(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = AddEditDestination.routeWithArgs,
            arguments = listOf(
                navArgument(AddEditDestination.itemIdArg) {
                    type = NavType.IntType
                },
                navArgument(AddEditDestination.itemIdArgTwo) {
                    type = NavType.IntType
                }

            )
        ) {
            AddEditProduct(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        //Sale
        composable(
            route = SaleDestination.routeWithArgs,
            arguments = listOf(navArgument(SaleDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            SaleScreen(
                drawerState = drawerState,
                navigateToStart = {
                    navController.navigate(StartDestination.route)
                },
                navigateToModalSheet = {
                    navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}")
                },
                navigateToItem = {
                    navController.navigate(
                        "${SaleEntryDestination.route}/${it}"
                    )
                },
                navigateToItemUpdate = {
                    navController.navigate(
                        "${SaleEditDestination.route}/${it.id}/${it.idPT}"
                    )
                }
            )
        }
        composable(
            route = SaleEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(SaleEntryDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            SaleEntryProduct(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = SaleEditDestination.routeWithArgs,
            arguments = listOf(
                navArgument(SaleEditDestination.itemIdArg) {
                    type = NavType.IntType
                },
                navArgument(SaleEditDestination.itemIdArgTwo) {
                    type = NavType.IntType
                }

            )
        ) {
            SaleEditProduct(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }


        //Expenses
        composable(
            route = ExpensesDestination.routeWithArgs,
            arguments = listOf(navArgument(ExpensesDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            ExpensesScreen(
                drawerState = drawerState,
                navigateToStart = {
                    navController.navigate(StartDestination.route)
                },
                navigateToModalSheet = {
                    navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}")
                },
                navigateToItem = {
                    navController.navigate(
                        "${ExpensesEntryDestination.route}/${it}"
                    )
                },
                navigateToItemUpdate = {
                    navController.navigate(
                        "${ExpensesEditDestination.route}/${it.id}/${it.idPT}"
                    )
                }
            )
        }
        composable(
            route = ExpensesEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(ExpensesEntryDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            ExpensesEntryProduct(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = ExpensesEditDestination.routeWithArgs,
            arguments = listOf(
                navArgument(ExpensesEditDestination.itemIdArg) {
                    type = NavType.IntType
                },
                navArgument(ExpensesEditDestination.itemIdArgTwo) {
                    type = NavType.IntType
                }

            )
        ) {
            ExpensesEditProduct(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        //WriteOff
        composable(
            route = WriteOffDestination.routeWithArgs,
            arguments = listOf(navArgument(WriteOffDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            WriteOffScreen(
                drawerState = drawerState,
                navigateToStart = {
                    navController.navigate(StartDestination.route)
                },
                navigateToModalSheet = {
                    navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}")
                },
                navigateToItem = {
                    navController.navigate(
                        "${WriteOffEntryDestination.route}/${it}"
                    )
                },
                navigateToItemUpdate = {
                    navController.navigate(
                        "${WriteOffEditDestination.route}/${it.id}/${it.idPT}"
                    )
                }
            )
        }
        composable(
            route = WriteOffEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(WriteOffEntryDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            WriteOffEntryProduct(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = WriteOffEditDestination.routeWithArgs,
            arguments = listOf(
                navArgument(WriteOffEditDestination.itemIdArg) {
                    type = NavType.IntType
                },
                navArgument(WriteOffEditDestination.itemIdArgTwo) {
                    type = NavType.IntType
                }
            )
        ) {
            WriteOffEditProduct(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
