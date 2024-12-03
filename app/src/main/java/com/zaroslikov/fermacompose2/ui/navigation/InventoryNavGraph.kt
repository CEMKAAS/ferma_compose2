package com.zaroslikov.fermacompose2.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zaroslikov.fermacompose2.ui.animal.AnimalCardDestination
import com.zaroslikov.fermacompose2.ui.animal.AnimalCardProduct
import com.zaroslikov.fermacompose2.ui.animal.AnimalDestination
import com.zaroslikov.fermacompose2.ui.animal.AnimalEditDestination
import com.zaroslikov.fermacompose2.ui.animal.AnimalEditProduct
import com.zaroslikov.fermacompose2.ui.animal.AnimalEntryDestination
import com.zaroslikov.fermacompose2.ui.animal.AnimalEntryProduct
import com.zaroslikov.fermacompose2.ui.animal.AnimalIndicatorsDestination
import com.zaroslikov.fermacompose2.ui.animal.AnimalIndicatorsScreen
import com.zaroslikov.fermacompose2.ui.animal.AnimalScreen
import com.zaroslikov.fermacompose2.ui.arhiv.FinanceArhivDestination
import com.zaroslikov.fermacompose2.ui.arhiv.FinanceArhivScreen
import com.zaroslikov.fermacompose2.ui.arhiv.IncubatorArhivDestination
import com.zaroslikov.fermacompose2.ui.arhiv.IncubatorArhivScreen
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesDestination
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesEditDestination
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesEditProduct
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesEntryDestination
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesEntryProduct
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesScreen
import com.zaroslikov.fermacompose2.ui.finance.FinanceAnalysisDestination
import com.zaroslikov.fermacompose2.ui.finance.FinanceAnalysisProduct
import com.zaroslikov.fermacompose2.ui.finance.FinanceCategoryDestination
import com.zaroslikov.fermacompose2.ui.finance.FinanceCategoryScreen
import com.zaroslikov.fermacompose2.ui.finance.FinanceDestination
import com.zaroslikov.fermacompose2.ui.finance.FinanceIncomeExpensesDestination
import com.zaroslikov.fermacompose2.ui.finance.FinanceIncomeExpensesScreen
import com.zaroslikov.fermacompose2.ui.finance.FinanceMountDestination
import com.zaroslikov.fermacompose2.ui.finance.FinanceMountScreen
import com.zaroslikov.fermacompose2.ui.finance.FinanceScreen
import com.zaroslikov.fermacompose2.ui.home.AddEditDestination
import com.zaroslikov.fermacompose2.ui.home.AddEditProduct
import com.zaroslikov.fermacompose2.ui.home.AddEntryDestination
import com.zaroslikov.fermacompose2.ui.home.AddEntryProduct
import com.zaroslikov.fermacompose2.ui.home.AddScreen
import com.zaroslikov.fermacompose2.ui.home.HomeDestination
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorEditDayScreen
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorEditDayScreenDestination
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorOvoscopDestination
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorOvoscopScreen
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditDestination
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditScreen
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorScreen
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorScreenDestination
import com.zaroslikov.fermacompose2.ui.note.NoteDestination
import com.zaroslikov.fermacompose2.ui.note.NoteEditDestination
import com.zaroslikov.fermacompose2.ui.note.NoteEditProduct
import com.zaroslikov.fermacompose2.ui.note.NoteEntryDestination
import com.zaroslikov.fermacompose2.ui.note.NoteEntryProduct
import com.zaroslikov.fermacompose2.ui.note.NoteScreen
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
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseDestination
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseEditDestination
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseEditScreen
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseScreen
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffDestination
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffEditDestination
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffEditProduct
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffEntryDestination
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffEntryProduct
import com.zaroslikov.fermacompose2.ui.writeOff.WriteOffScreen

@Composable
fun InventoryNavHost(
    navController: NavHostController,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = StartDestination.route,
        modifier = modifier
    ) {

        composable(route = StartDestination.route) {
            StartScreen(
                navController = navController,//TODO переделать на адд ADD
                navigateToItemProject = {
                    navController.navigate("${WarehouseDestination.route}/${it}")
                },
                navigateToItemIncubator = {
                    navController.navigate("${IncubatorScreenDestination.route}/${it}")
                },
                navigateToItemIncubatorArh = {
                    navController.navigate("${IncubatorArhivDestination.route}/${it}")
                },
                navigateToItemProjectArh = {
                    navController.navigate("${FinanceArhivDestination.route}/${it}")
                },
                modifier = modifier
            )

        }

        composable(route = ChoiseProjectDestination.route) {
            ChoiseProject(navigateBack = { navController.popBackStack() },
                navigateProject = { navController.navigate(it) })
        }


        composable(route = ProjectAddDestination.route) {
            AddProject(
                navigateBack = { navController.popBackStack() },
                navigateToStart = { navController.navigate(StartDestination.route) }
            )
        }

        composable(route = AddIncubatorDestination.route) {
            AddIncubator(
                navigateBack = { navController.popBackStack() },
                navigateContinue = { navController.navigate(StartDestination.route) }
            )
        }

        composable(
            route = IncubatorScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(IncubatorScreenDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            IncubatorScreen(navigateBack = { navController.popBackStack() }, navigateDayEdit = {
                navController.navigate(
                    "${IncubatorEditDayScreenDestination.route}/${it.first}/${it.second}"
                )
            }, navigateProjectEdit = {
                navController.navigate(
                    "${IncubatorProjectEditDestination.route}/${it}"
                )
            }, navigateOvos = {
                navController.navigate(
                    "${IncubatorOvoscopDestination.route}/${it.first}/${it.second}"
                )
            }, navigateStart = {
                navController.navigate(StartDestination.route)
            })
        }

        composable(
            route = IncubatorOvoscopDestination.routeWithArgs,
            arguments = listOf(navArgument(IncubatorOvoscopDestination.itemIdArg) {
                type = NavType.IntType
            }, navArgument(IncubatorOvoscopDestination.itemIdArgTwo) {
                type = NavType.StringType
            })
        ) {
            IncubatorOvoscopScreen(navigateBack = {
                navController.popBackStack()
            }, onNavigateUp = { navController.navigateUp() })
        }


        composable(
            route = IncubatorProjectEditDestination.routeWithArgs,
            arguments = listOf(navArgument(IncubatorProjectEditDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            IncubatorProjectEditScreen(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateStart = { navController.navigate(StartDestination.route) })
        }

        composable(
            route = IncubatorEditDayScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(IncubatorEditDayScreenDestination.itemIdArg) {
                type = NavType.IntType
            }, navArgument(IncubatorEditDayScreenDestination.itemIdArgTwo) {
                type = NavType.IntType
            })
        ) {
            IncubatorEditDayScreen(navigateBack = {
                navController.popBackStack()
            }, onNavigateUp = { navController.navigateUp() })
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
                navigateToEdit = { navController.navigate("${WarehouseEditDestination.route}/${it}") },
                navigationToAnalysis = { navController.navigate("${FinanceAnalysisDestination.route}/${it.idProject}/${it.name}") },
                drawerState = drawerState
            )
        }

        composable(
            route = WarehouseEditDestination.routeWithArgs,
            arguments = listOf(navArgument(WarehouseEditDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            WarehouseEditScreen(
                navigateBack = { navController.popBackStack() },
                navigateUp = { navController.navigateUp() },
                navigateToStart = { navController.navigate(StartDestination.route) })

        }

        composable(
            route = FinanceDestination.routeWithArgs,
            arguments = listOf(navArgument(FinanceDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            FinanceScreen(drawerState = drawerState, navigateToStart = {
                navController.navigate(StartDestination.route)
            }, navigateToModalSheet = {
                navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}")
            }, navigateToIncomeExpenses = {
                navController.navigate(
                    "${FinanceIncomeExpensesDestination.route}/${it.idPT}/${it.incomeBoolean}"
                )
            }, navigateToFinaceMount = {
                navController.navigate(
                    "${FinanceMountDestination.route}/${it}"
                )
            })
        }

        composable(
            route = FinanceMountDestination.routeWithArgs,
            arguments = listOf(navArgument(FinanceMountDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            FinanceMountScreen(navigateBack = { navController.popBackStack() },
                navigateToCategory = {
                    navController.navigate(
                        "${FinanceCategoryDestination.route}/${it.idPT}/${it.category}/${it.incomeBoolean}/${it.dateBegin}/${it.dateEnd}"
                    )
                })
        }

        composable(
            route = FinanceCategoryDestination.routeWithArgs,
            arguments = listOf(
                navArgument(FinanceCategoryDestination.itemIdArg) {
                    type = NavType.IntType
                },
                navArgument(FinanceCategoryDestination.itemIdArgTwo) {
                    type = NavType.StringType
                },
                navArgument(FinanceCategoryDestination.itemIdArgThree) {
                    type = NavType.BoolType
                },
                navArgument(FinanceCategoryDestination.itemIdArgFour) {
                    type = NavType.StringType
                },
                navArgument(FinanceCategoryDestination.itemIdArgFive) {
                    type = NavType.StringType
                })
        ) {
            FinanceCategoryScreen(
                navigateBack = { navController.popBackStack() },
                navigationToAnalysis = { navController.navigate("${FinanceAnalysisDestination.route}/${it.idProject}/${it.name}") },
            )
        }

        composable(
            route = FinanceIncomeExpensesDestination.routeWithArgs,
            arguments = listOf(navArgument(FinanceIncomeExpensesDestination.itemIdArg) {
                type = NavType.IntType
            }, navArgument(FinanceIncomeExpensesDestination.itemIdArgTwo) {
                type = NavType.BoolType
            })
        ) {
            FinanceIncomeExpensesScreen(
                navigateBack = { navController.popBackStack() },
                navigationToAnalysis = { navController.navigate("${FinanceAnalysisDestination.route}/${it.idProject}/${it.name}") },
            )

        }

        composable(
            route = FinanceAnalysisDestination.routeWithArgs,
            arguments = listOf(navArgument(FinanceAnalysisDestination.itemIdArg) {
                type = NavType.IntType
            }, navArgument(FinanceAnalysisDestination.itemIdArgTwo) {
                type = NavType.StringType
            })
        ) {
            FinanceAnalysisProduct(navigateBack = { navController.popBackStack() })
        }


        //Add
        composable(
            route = HomeDestination.routeWithArgs,
            arguments = listOf(navArgument(HomeDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            AddScreen(drawerState = drawerState, navigateToStart = {
                navController.navigate(StartDestination.route)
            }, navigateToModalSheet = {
                navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}")
            }, navigateToItemAdd = {
                navController.navigate(
                    "${AddEntryDestination.route}/${it}"
                )
            }, navigateToItemUpdate = {
                navController.navigate(
                    "${AddEditDestination.route}/${it.id}/${it.idPT}"
                )
            })
        }
        composable(
            route = AddEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(AddEntryDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            AddEntryProduct(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        composable(route = AddEditDestination.routeWithArgs,
            arguments = listOf(navArgument(AddEditDestination.itemIdArg) {
                type = NavType.IntType
            }, navArgument(AddEditDestination.itemIdArgTwo) {
                type = NavType.IntType
            }

            )) {
            AddEditProduct(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        //Sale
        composable(
            route = SaleDestination.routeWithArgs,
            arguments = listOf(navArgument(SaleDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            SaleScreen(drawerState = drawerState, navigateToStart = {
                navController.navigate(StartDestination.route)
            }, navigateToModalSheet = {
                navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}")
            }, navigateToItem = {
                navController.navigate(
                    "${SaleEntryDestination.route}/${it}"
                )
            }, navigateToItemUpdate = {
                navController.navigate(
                    "${SaleEditDestination.route}/${it.id}/${it.idPT}"
                )
            })
        }
        composable(
            route = SaleEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(SaleEntryDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            SaleEntryProduct(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        composable(route = SaleEditDestination.routeWithArgs,
            arguments = listOf(navArgument(SaleEditDestination.itemIdArg) {
                type = NavType.IntType
            }, navArgument(SaleEditDestination.itemIdArgTwo) {
                type = NavType.IntType
            }

            )) {
            SaleEditProduct(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }


        //Expenses
        composable(
            route = ExpensesDestination.routeWithArgs,
            arguments = listOf(navArgument(ExpensesDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            ExpensesScreen(drawerState = drawerState, navigateToStart = {
                navController.navigate(StartDestination.route)
            }, navigateToModalSheet = {
                navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}")
            }, navigateToItem = {
                navController.navigate(
                    "${ExpensesEntryDestination.route}/${it}"
                )
            }, navigateToItemUpdate = {
                navController.navigate(
                    "${ExpensesEditDestination.route}/${it.id}/${it.idPT}"
                )
            })
        }
        composable(
            route = ExpensesEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(ExpensesEntryDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            ExpensesEntryProduct(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        composable(route = ExpensesEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ExpensesEditDestination.itemIdArg) {
                type = NavType.IntType
            }, navArgument(ExpensesEditDestination.itemIdArgTwo) {
                type = NavType.IntType
            }

            )) {
            ExpensesEditProduct(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        //WriteOff
        composable(
            route = WriteOffDestination.routeWithArgs,
            arguments = listOf(navArgument(WriteOffDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            WriteOffScreen(drawerState = drawerState, navigateToStart = {
                navController.navigate(StartDestination.route)
            }, navigateToModalSheet = {
                navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}")
            }, navigateToItem = {
                navController.navigate(
                    "${WriteOffEntryDestination.route}/${it}"
                )
            }, navigateToItemUpdate = {
                navController.navigate(
                    "${WriteOffEditDestination.route}/${it.id}/${it.idPT}"
                )
            })
        }
        composable(
            route = WriteOffEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(WriteOffEntryDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            WriteOffEntryProduct(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        composable(
            route = WriteOffEditDestination.routeWithArgs,
            arguments = listOf(navArgument(WriteOffEditDestination.itemIdArg) {
                type = NavType.IntType
            }, navArgument(WriteOffEditDestination.itemIdArgTwo) {
                type = NavType.IntType
            })
        ) {
            WriteOffEditProduct(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        composable(
            route = AnimalDestination.routeWithArgs,
            arguments = listOf(navArgument(AnimalDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            AnimalScreen(drawerState = drawerState, navigateToStart = {
                navController.navigate(StartDestination.route)
            }, navigateToModalSheet = {
                navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}")
            }, navigateToItemAdd = {
                navController.navigate(
                    "${AnimalEntryDestination.route}/${it}"
                )
            }, navigateToItemCard = {
                navController.navigate(
                    "${AnimalCardDestination.route}/${it}"
                )
            })
        }

        composable(
            route = AnimalEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(AnimalEntryDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            AnimalEntryProduct(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        composable(
            route = AnimalCardDestination.routeWithArgs,
            arguments = listOf(navArgument(AnimalCardDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            AnimalCardProduct(navigateBack = { navController.popBackStack() },
                onNavigateSetting = { navController.navigate("${AnimalEditDestination.route}/${it}") },
                onNavigateIndicators = { navController.navigate("${AnimalIndicatorsDestination.route}/${it.id}/${it.table}") })
        }

        composable(route = AnimalIndicatorsDestination.routeWithArgs,
            arguments = listOf(
                navArgument(AnimalIndicatorsDestination.itemIdArg) {
                    type = NavType.IntType
                },
                navArgument(AnimalIndicatorsDestination.itemIdArgTwo) {
                    type = NavType.StringType
                }

            )) {
            AnimalIndicatorsScreen(
                navigateBack = { navController.popBackStack() })
        }

        composable(route = AnimalEditDestination.routeWithArgs,
            arguments = listOf(
                navArgument(AnimalEditDestination.itemIdArg) {
                    type = NavType.IntType
                }
            )) {
            AnimalEditProduct(
                navigateBack = { navController.popBackStack() },
                navigateEdit = { navController.navigateUp() },
                navigateDelete = { navController.navigate("${AnimalDestination.route}/${it}") }
            )
        }



        composable(route = FinanceArhivDestination.routeWithArgs,
            arguments = listOf(
                navArgument(FinanceArhivDestination.itemIdArg) {
                    type = NavType.IntType
                }
            )) {
            FinanceArhivScreen(
                navigateToBack = { navController.popBackStack() },
                navigateToStart = { navController.navigate(StartDestination.route) },
                navigateToIncomeExpenses = {
                    navController.navigate(
                        "${FinanceIncomeExpensesDestination.route}/${it.idPT}/${it.incomeBoolean}"
                    )
                }
            )
        }

        composable(route = IncubatorArhivDestination.routeWithArgs,
            arguments = listOf(
                navArgument(IncubatorProjectEditDestination.itemIdArg) {
                    type = NavType.IntType
                }
            )) {
            IncubatorArhivScreen(
                navigateBack = { navController.popBackStack() },
                navigateStart = { navController.navigateUp() }
            )
        }

        //Note
        composable(
            route = NoteDestination.routeWithArgs,
            arguments = listOf(navArgument(HomeDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            NoteScreen(drawerState = drawerState, navigateToStart = {
                navController.navigate(StartDestination.route)
            }, navigateToModalSheet = {
                navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}")
            }, navigateToItemAdd = {
                navController.navigate(
                    "${NoteEntryDestination.route}/${it}"
                )
            }, navigateToItemUpdate = {
                navController.navigate(
                    "${NoteEditDestination.route}/${it}"
                )
            })
        }
        composable(
            route = NoteEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(NoteEntryDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            NoteEntryProduct(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        composable(route = NoteEditDestination.routeWithArgs,
            arguments = listOf(navArgument(NoteEditDestination.itemIdArg) {
                type = NavType.IntType
            })) {
            NoteEditProduct(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }
    }
}
