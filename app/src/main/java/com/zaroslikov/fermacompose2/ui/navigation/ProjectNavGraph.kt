package com.zaroslikov.fermacompose2.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zaroslikov.fermacompose2.ui.finance.FinanceDestination
import com.zaroslikov.fermacompose2.ui.finance.FinanceScreen
import com.zaroslikov.fermacompose2.ui.project.finance.analysis.FinanceAnalysisDestination
import com.zaroslikov.fermacompose2.ui.project.finance.analysis.FinanceAnalysisProduct
import com.zaroslikov.fermacompose2.ui.project.finance.category.FinanceCategoryScreen2
import com.zaroslikov.fermacompose2.ui.project.finance.category.FinanceIncomeExpensesDestination
import com.zaroslikov.fermacompose2.ui.project.sections.HomeDestination
import com.zaroslikov.fermacompose2.ui.project.sections.SectionWorkspaceScreen
import com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard.AnimalCardDestination
import com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard.AnimalCardProduct
import com.zaroslikov.fermacompose2.ui.project.sections.animal.edit.AnimalEditDestination
import com.zaroslikov.fermacompose2.ui.project.sections.animal.edit.AnimalEditProduct
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.AnimalCountDestination
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count.AnimalCountScreen
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.size.AnimalSizeDestination
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.size.AnimalSizeScreen
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.vaccination.AnimalVaccinationDestination
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.vaccination.AnimalVaccinationScreen
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.weight.AnimalWeightDestination
import com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.weight.AnimalWeightScreen
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalDestination
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalListScreen
import com.zaroslikov.fermacompose2.ui.project.sections.note.list_screen.NoteDestination
import com.zaroslikov.fermacompose2.ui.project.sections.note.list_screen.NoteScreen
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseEditDestination
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseEditScreen
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseDestination
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseScreen
import com.zaroslikov.fermacompose2.ui.start.first.FirstDestination


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectNavHost(
    navController: NavHostController,
    itemPT: Long,
    modifier: Modifier = Modifier,
    rootNavController: NavController
) {
    NavHost(
        navController = navController,
        startDestination = "${WarehouseDestination.route}/${itemPT}",
        modifier = modifier
    ) {
        composable(
            route = WarehouseDestination.routeWithArgs,
            arguments = listOf(navArgument(WarehouseDestination.itemIdArg) {
                type = NavType.LongType
            })
        ) {
            WarehouseScreen(
                navigateToStart = { rootNavController.navigate(FirstDestination.route) },
                navigateToEdit = { rootNavController.navigate("${WarehouseEditDestination.route}/${it}") },
                navigateToNote = { navController.navigate("${NoteDestination.route}/${it}") },
                navigationToAnalysis = {
                    navController.navigate(
                        nav(
                            FinanceAnalysisDestination.route,
                            it.first.toString(),
                            it.second,
                            it.third.toString()
                        )
                    )
                },
                navigationToNewYear = {}
            )
        }
        composable(
            route = WarehouseEditDestination.routeWithArgs,
            arguments = listOf(navArgument(WarehouseEditDestination.itemIdArg) {
                type = NavType.LongType
            })
        ) {
            WarehouseEditScreen(
                navigateBack = navController::popBackStack
            )
        }

        composable(
            route = FinanceDestination.routeWithArgs,
            arguments = listOf(navArgument(FinanceDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            FinanceScreen(
                navigateToIncomeExpenses = {
                    navController.navigate(
                        navNull(
                            FinanceIncomeExpensesDestination.route,
                            FinanceIncomeExpensesDestination.itemIdArg,
                            it.first.toString(),
                            FinanceIncomeExpensesDestination.itemIdArgTwo,
                            it.second.toString()
                        )
                    )
                }
            )
        }

        composable(
            route = FinanceIncomeExpensesDestination.routeWithArgs,
            arguments = listOf(navArgument(FinanceIncomeExpensesDestination.itemIdArg) {
                type = NavType.LongType
            }, navArgument(FinanceIncomeExpensesDestination.itemIdArgTwo) {
                type = NavType.StringType
            })
        ) {
            FinanceCategoryScreen2(navigateBack = { navController.popBackStack() })
        }
        composable(
            route = FinanceAnalysisDestination.routeWithArgs,
            arguments = listOf(
                navArgument(FinanceAnalysisDestination.itemIdArg) {
                    type = NavType.LongType
                }, navArgument(FinanceAnalysisDestination.itemIdArgTwo) {
                    type = NavType.StringType
                },
                navArgument(FinanceAnalysisDestination.itemIdArgTree) {
                    type = NavType.StringType
                })
        ) {
            FinanceAnalysisProduct(navigateBack = { navController.popBackStack() })
        }

        // Add
        composable(
            route = HomeDestination.routeWithArgs,
            arguments = listOf(navArgument(HomeDestination.itemIdArg) {
                type = NavType.LongType
            })
        ) {
            SectionWorkspaceScreen(
                navigateToItemCard = {
                    navController.navigate(
                        navNull(
                            route = AnimalCardDestination.route,
                            itemOne = it.first.toString(),
                            itemTwo = it.second.toString(),
                        )
                    )
                },
                navigationToAnalysis = {
                    navController.navigate(
                        nav(
                            FinanceAnalysisDestination.route,
                            it.first.toString(),
                            it.second,
                            it.third.toString()
                        )
                    )
                }
            )
        }

        //Note
        composable(
            route = NoteDestination.routeWithArgs,
            arguments = listOf(navArgument(NoteDestination.itemIdArg) {
                type = NavType.LongType
            })
        ) {
            NoteScreen(navigateBack = navController::popBackStack)
        }

        // Animal
        composable(
            route = AnimalDestination.routeWithArgs,
            arguments = listOf(navArgument(AnimalDestination.itemIdArg) {
                type = NavType.LongType
            })
        ) {
            AnimalListScreen()
        }

        composable(
            route = AnimalEditDestination.routeWithArgs,
            arguments = listOf(
                navArgument(AnimalEditDestination.itemIdPT) {
                    type = NavType.LongType
                },
                navArgument(AnimalEditDestination.itemId) {
                    type = NavType.LongType
                    defaultValue = -1
                })
        ) {
            AnimalEditProduct(
                navigateBack = { navController.popBackStack() })
        }

        composable(
            route = AnimalCardDestination.routeWithArgs,
            arguments = listOf(navArgument(AnimalCardDestination.itemIdPT) {
                type = NavType.LongType
            }, navArgument(AnimalCardDestination.itemId) {
                type = NavType.LongType
            })
        ) {
            AnimalCardProduct(
                navigateBack = { navController.popBackStack() },
                onNavigateSetting = {
                    navController.navigate(
                        navNull(
                            route = AnimalEditDestination.route,
                            itemOne = it.first.toString(),
                            itemTwo = it.second.toString()
                        )
                    )
                },
                onNavigateSize = {
                    navController.navigate(
                        navNull(
                            route = AnimalSizeDestination.route,
                            itemOne = it.first.toString(),
                            itemTwo = it.second.toString()
                        )
                    )
                },
                onNavigateWeight = {
                    navController.navigate(
                        navNull(
                            route = AnimalWeightDestination.route,
                            itemOne = it.first.toString(),
                            itemTwo = it.second.toString()
                        )
                    )
                },
                onNavigateCount = {
                    navController.navigate(
                        navNull(
                            route = AnimalCountDestination.route,
                            itemOne = it.first.toString(),
                            itemTwo = it.second.toString()
                        )
                    )
                },
                onNavigateVaccination = {
                    navController.navigate(
                        navNull(
                            route = AnimalVaccinationDestination.route,
                            itemOne = it.first.toString(),
                            itemTwo = it.second.toString()
                        )
                    )
                }
            )
        }

        composable(
            route = AnimalSizeDestination.routeWithArgs,
            arguments = listOf(navArgument(AnimalSizeDestination.itemIdPT) {
                type = NavType.LongType
            }, navArgument(AnimalSizeDestination.itemId) {
                type = NavType.LongType
            })
        ) {
            AnimalSizeScreen(
                navigateBack = { navController.popBackStack() },
            )
        }
        composable(
            route = AnimalWeightDestination.routeWithArgs,
            arguments = listOf(navArgument(AnimalWeightDestination.itemIdPT) {
                type = NavType.LongType
            }, navArgument(AnimalWeightDestination.itemId) {
                type = NavType.LongType
            })
        ) {
            AnimalWeightScreen(
                navigateBack = { navController.popBackStack() },
            )
        }
        composable(
            route = AnimalVaccinationDestination.routeWithArgs,
            arguments = listOf(navArgument(AnimalVaccinationDestination.itemIdPT) {
                type = NavType.LongType
            }, navArgument(AnimalVaccinationDestination.itemId) {
                type = NavType.LongType
            })
        ) {
            AnimalVaccinationScreen(
                navigateBack = { navController.popBackStack() },
            )
        }
        composable(
            route = AnimalCountDestination.routeWithArgs,
            arguments = listOf(navArgument(AnimalCountDestination.itemIdPT) {
                type = NavType.LongType
            }, navArgument(AnimalCountDestination.itemId) {
                type = NavType.LongType
            })
        ) {
            AnimalCountScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}

