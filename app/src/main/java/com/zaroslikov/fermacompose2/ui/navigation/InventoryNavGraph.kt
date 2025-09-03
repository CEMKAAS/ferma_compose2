package com.zaroslikov.fermacompose2.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zaroslikov.fermacompose2.ui.add.AddProject
import com.zaroslikov.fermacompose2.ui.add.ChoiseProjectDestination
import com.zaroslikov.fermacompose2.ui.add.ProjectAddDestination
//import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardDestination
//import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardProduct
import com.zaroslikov.fermacompose2.ui.animal.list_screen.AnimalDestination
import com.zaroslikov.fermacompose2.ui.animal.entry.AnimalEntryDestination
import com.zaroslikov.fermacompose2.ui.animal.entry.AnimalEntryProduct
//import com.zaroslikov.fermacompose2.ui.animal.AnimalIndicatorsDestination
//import com.zaroslikov.fermacompose2.ui.animal.AnimalIndicatorsScreen
import com.zaroslikov.fermacompose2.ui.animal.list_screen.AnimalScreen
//import com.zaroslikov.fermacompose2.ui.arhiv.FinanceArhivDestination
//import com.zaroslikov.fermacompose2.ui.arhiv.FinanceArhivScreen
//import com.zaroslikov.fermacompose2.ui.arhiv.IncubatorArhivDestination
//import com.zaroslikov.fermacompose2.ui.arhiv.IncubatorArhivScreen
import com.zaroslikov.fermacompose2.ui.sections.expenses.list_screen.ExpensesDestination
import com.zaroslikov.fermacompose2.ui.sections.expenses.entry.ExpensesEntryDestination
import com.zaroslikov.fermacompose2.ui.sections.expenses.entry.ExpensesEntryProduct
import com.zaroslikov.fermacompose2.ui.sections.expenses.list_screen.ExpensesScreen
//import com.zaroslikov.fermacompose2.ui.finance.FinanceAnalysisDestination
//import com.zaroslikov.fermacompose2.ui.finance.FinanceAnalysisProduct
//import com.zaroslikov.fermacompose2.ui.finance.FinanceCategoryDestination
//import com.zaroslikov.fermacompose2.ui.finance.FinanceCategoryScreen
//import com.zaroslikov.fermacompose2.ui.finance.FinanceDestination
//import com.zaroslikov.fermacompose2.ui.finance.FinanceIncomeExpensesDestination
//import com.zaroslikov.fermacompose2.ui.finance.FinanceIncomeExpensesScreen
//import com.zaroslikov.fermacompose2.ui.finance.FinanceMountDestination
//import com.zaroslikov.fermacompose2.ui.finance.FinanceMountScreen
//import com.zaroslikov.fermacompose2.ui.finance.FinanceScreen
import com.zaroslikov.fermacompose2.ui.sections.add.entry.AddEntryDestination
import com.zaroslikov.fermacompose2.ui.sections.add.entry.AddEntryProduct
import com.zaroslikov.fermacompose2.ui.sections.add.list_screen.AddScreen
import com.zaroslikov.fermacompose2.ui.sections.add.list_screen.HomeDestination
//import com.zaroslikov.fermacompose2.ui.incubator.IncubatorEditDayScreen
//import com.zaroslikov.fermacompose2.ui.incubator.IncubatorEditDayScreenDestination
//import com.zaroslikov.fermacompose2.ui.incubator.IncubatorOvoscopDestination
//import com.zaroslikov.fermacompose2.ui.incubator.IncubatorOvoscopScreen
//import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditDestination
//import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditScreen
//import com.zaroslikov.fermacompose2.ui.incubator.IncubatorScreen
//import com.zaroslikov.fermacompose2.ui.incubator.IncubatorScreenDestination
//import com.zaroslikov.fermacompose2.ui.new_year.NewYearAnalysis
//import com.zaroslikov.fermacompose2.ui.new_year.NewYearDestination
import com.zaroslikov.fermacompose2.ui.sections.note.list_screen.NoteDestination
//import com.zaroslikov.fermacompose2.ui.note.entry.NoteEditDestination
//import com.zaroslikov.fermacompose2.ui.note.entry.NoteEditProduct
import com.zaroslikov.fermacompose2.ui.sections.note.entry.NoteEntryDestination
import com.zaroslikov.fermacompose2.ui.sections.note.entry.NoteEntryProduct
import com.zaroslikov.fermacompose2.ui.sections.note.list_screen.NoteScreen
import com.zaroslikov.fermacompose2.ui.sections.sale.list_screen.SaleDestination
import com.zaroslikov.fermacompose2.ui.sections.sale.entry.SaleEntryDestination
import com.zaroslikov.fermacompose2.ui.sections.sale.entry.SaleEntryProduct
import com.zaroslikov.fermacompose2.ui.sections.sale.list_screen.SaleScreen
import com.zaroslikov.fermacompose2.ui.start.StartDestination
import com.zaroslikov.fermacompose2.ui.start.StartScreen
//import com.zaroslikov.fermacompose2.ui.add.AddProject
//import com.zaroslikov.fermacompose2.ui.add.ChoiseProject
//import com.zaroslikov.fermacompose2.ui.add.ChoiseProjectDestination
//import com.zaroslikov.fermacompose2.ui.add.ProjectAddDestination
//import com.zaroslikov.fermacompose2.ui.add.incubator.AddIncubator
//import com.zaroslikov.fermacompose2.ui.add.incubator.AddIncubatorDestination
//import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseDestination
//import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseEditDestination
//import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseEditScreen
//import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseScreen
import com.zaroslikov.fermacompose2.ui.sections.writeOff.list_screen.WriteOffDestination
import com.zaroslikov.fermacompose2.ui.sections.writeOff.entry.WriteOffEntryDestination
import com.zaroslikov.fermacompose2.ui.sections.writeOff.entry.WriteOffEntryProduct
import com.zaroslikov.fermacompose2.ui.sections.writeOff.list_screen.WriteOffScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryNavHost(
    navController: NavHostController,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    isFirstStart: Boolean,
    isFirstEnd: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination =
//            if (isFirstStart) ChoiseProjectDestination.route else
            StartDestination.route,
        modifier = modifier
    ) {

        composable(route = StartDestination.route) {
            StartScreen(
                navController = navController,//TODO переделать на адд ADD
                navigateToItemProject = {
                    navController.navigate(
//                        "${WarehouseDestination.route}/${it}"
                        "${HomeDestination.route}/${it}"
                    )
                },
                navigateToItemIncubator = {
//                    navController.navigate("${IncubatorScreenDestination.route}/${it}")
                },
                navigateToItemIncubatorArh = {
//                    navController.navigate("${IncubatorArhivDestination.route}/${it}")
                },
                navigateToItemProjectArh = {
//                    navController.navigate("${FinanceArhivDestination.route}/${it}")
                },
                navigationToNewYear = {
//                    navController.navigate("${NewYearDestination.route}/${it.first}/${it.second}")
                },
                modifier = modifier,
                isFirstStart = isFirstStart,
                isFirstEnd = isFirstEnd
            )
        }

        /* composable(route = ChoiseProjectDestination.route) {
             ChoiseProject(
                 navigateBack = { navController.popBackStack() },
                 navigateProject = { navController.navigate(it) },
                 isFirstStart = isFirstStart,
                 isFirstEnd = isFirstEnd
             )
         }
         */

        composable(route = ProjectAddDestination.route) {
            AddProject(
                navigateBack = { navController.popBackStack() },
                navigateToStart = { navController.navigate(StartDestination.route) },
                isFirstStart = isFirstStart
            )
        }
        /*
                composable(route = AddIncubatorDestination.route) {
                    AddIncubator(
                        navigateBack = { navController.popBackStack() },
                        navigateContinue = { navController.navigate(StartDestination.route) },
                        isFirstStart = isFirstStart
                    )
                }

                composable(
                    route = IncubatorScreenDestination.routeWithArgs,
                    arguments = listOf(navArgument(IncubatorScreenDestination.itemIdArg) {
                        type = NavType.IntType
                    })
                ) {
                    IncubatorScreen(
                        navigateBack = { navController.popBackStack() },
                        navigateDayEdit = {
                            navController.navigate(
                                "${IncubatorEditDayScreenDestination.route}/${it.first}/${it.second}"
                            )
                        },
                        navigateProjectEdit = {
                            navController.navigate(
                                "${IncubatorProjectEditDestination.route}/${it}"
                            )
                        },
                        navigateOvos = {
                            navController.navigate(
                                "${IncubatorOvoscopDestination.route}/${it.first}/${it.second}"
                            )
                        },
                        navigateStart = {
                            navController.navigate(StartDestination.route)
                        }
                    )
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
                    IncubatorProjectEditScreen(
                        navigateBack = { navController.popBackStack() },
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
                */
//        composable(
//            route = WarehouseDestination.routeWithArgs,
//            arguments = listOf(navArgument(WarehouseDestination.itemIdArg) {
//                type = NavType.IntType
//            })
//        ) {
//            WarehouseScreen(
//                navigateToStart = { navController.navigate(StartDestination.route) },
//                navigateToModalSheet = { navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}") },
//                navigateToEdit = { navController.navigate("${WarehouseEditDestination.route}/${it}") },
//                navigationToAnalysis = { navController.navigate("${FinanceAnalysisDestination.route}/${it.idProject}/${it.name}") },
//                navigationToNewYear = { navController.navigate("${NewYearDestination.route}/${it.first}/${it.second}") },
//                drawerState = drawerState,
////                isFirstStart = isFirstStart,
////                isFirstEnd = isFirstEnd
//            )
//        }

        /* composable(
             route = NewYearDestination.routeWithArgs,
             arguments = listOf(
                 navArgument(NewYearDestination.itemIdArg) {
                     type = NavType.BoolType
                 },
                 navArgument(NewYearDestination.itemIdArgTwo) {
                     type = NavType.IntType
                 },
             )
         ) {
             NewYearAnalysis(
                 navigateBack = { navController.popBackStack() }
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
             FinanceScreen(
                 drawerState = drawerState,
                 navigateToStart = { navController.navigate(StartDestination.route) },
                 navigateToModalSheet = { navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}") },
                 navigateToIncomeExpenses = {
                     navController.navigate(
                         "${FinanceIncomeExpensesDestination.route}/${it.first}/${it.second}"
                     )
                 },
                 navigateToFinaceMount = {
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
             FinanceMountScreen(
                 navigateBack = { navController.popBackStack() },
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
                 navigationToAnalysis = { navController.navigate("${FinanceAnalysisDestination.route}/${it.first}/${it.second}") },
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
 */

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
                        navNull(
                            AddEntryDestination.route,
                            AddEntryDestination.itemIdPT,
                            it.toString()
                        )
                    )
                },
                navigateToItemUpdate = {
                    navController.navigate(
                        navNull(
                            AddEntryDestination.route,
                            AddEntryDestination.itemIdPT,
                            it.first.toString(),
                            AddEntryDestination.itemId,
                            it.second.toString()
                        )
                    )
                },
                navigationToAnalysis = {
//                    navController.navigate(
//                        nav(
//                            FinanceAnalysisDestination.route,
//                            it.first.toString(),
//                            it.second
//                        )
//                    )
//                    "${FinanceAnalysisDestination.route}/${it.idProject}/${it.name}")
                },
            )
        }
        composable(
            route = AddEntryDestination.routeWithArgs,
            arguments = listOf(
                navArgument(AddEntryDestination.itemIdPT) {
                    type = NavType.IntType
                },
                navArgument(AddEntryDestination.itemId) {
                    type = NavType.IntType
                    defaultValue = -1
                })
        ) {
            AddEntryProduct(navigateBack = { navController.popBackStack() })
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
            }, navigateToItemAdd = {
                navController.navigate(
                    navNull(
                        route = SaleEntryDestination.route,
                        itemOne = it.toString()
                    )
                )
            }, navigateToItemUpdate = {
                navController.navigate(
                    navNull(
                        route = SaleEntryDestination.route,
                        itemOne = it.first.toString(),
                        itemTwo = it.second.toString()
                    )
                )
            })
        }
        composable(
            route = SaleEntryDestination.routeWithArgs,
            arguments = listOf(
                navArgument(SaleEntryDestination.itemIdPT) {
                    type = NavType.IntType
                },
                navArgument(SaleEntryDestination.itemId) {
                    type = NavType.IntType
                    defaultValue = -1
                })
        ) {
            SaleEntryProduct(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        //Expenses
        composable(
            route = ExpensesDestination.routeWithArgs,
            arguments = listOf(navArgument(ExpensesDestination.itemIdArg) {
                type = NavType.LongType
            })
        ) {
            ExpensesScreen(
                drawerState = drawerState, navigateToStart = {
                    navController.navigate(StartDestination.route)
                }, navigateToModalSheet = {
                    navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}")
                }, navigateToItemAdd = {
                    navController.navigate(
                        navNull(
                            route = ExpensesEntryDestination.route,
                            itemOne = it.toString()
                        )
                    )
                },
                navigateToItemUpdate = {
                    navController.navigate(
                        navNull(
                            route = ExpensesEntryDestination.route,
                            itemOne = it.first.toString(),
                            itemTwo = it.second.toString()
                        )
                    )
                }
            )
        }

        composable(
            route = ExpensesEntryDestination.routeWithArgs,
            arguments = listOf(
                navArgument(ExpensesEntryDestination.itemIdPT) {
                    type = NavType.IntType
                },
                navArgument(ExpensesEntryDestination.itemId) {
                    type = NavType.IntType
                    defaultValue = -1
                })
        ) {
            ExpensesEntryProduct(
                navigateBack = { navController.popBackStack() })
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
            }, navigateToItemAdd = {
                navController.navigate(
                    navNull(
                        WriteOffEntryDestination.route,
                        WriteOffEntryDestination.itemIdPT,
                        it.toString()
                    )
                )
            }, navigateToItemUpdate = {
                navController.navigate(
                    navNull(
                        WriteOffEntryDestination.route,
                        WriteOffEntryDestination.itemIdPT,
                        it.first.toString(),
                        WriteOffEntryDestination.itemId,
                        it.second.toString()
                    )
                )
            })
        }
        composable(
            route = WriteOffEntryDestination.routeWithArgs,
            arguments = listOf(
                navArgument(WriteOffEntryDestination.itemIdPT) {
                    type = NavType.IntType
                },
                navArgument(WriteOffEntryDestination.itemId) {
                    type = NavType.IntType
                    defaultValue = -1
                })
        ) {
            WriteOffEntryProduct(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
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
                    navNull(
                        route = NoteEntryDestination.route,
                        itemOne = it.first.toString(),
                        itemTwo = it.second.toString()
                    )
                )
            })
        }
        composable(
            route = NoteEntryDestination.routeWithArgs,
            arguments = listOf(
                navArgument(NoteEntryDestination.itemIdPT) {
                    type = NavType.LongType
                },
                navArgument(NoteEntryDestination.itemId) {
                    type = NavType.LongType
                    defaultValue = -1
                }
            )
        ) {
            NoteEntryProduct(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        /* composable(
             route = AnimalDestination.routeWithArgs,
             arguments = listOf(navArgument(AnimalDestination.itemIdArg) {
                 type = NavType.IntType
             })
         ) {
             AnimalScreen(
                 drawerState = drawerState, navigateToStart = {
                     navController.navigate(StartDestination.route)
                 }, navigateToModalSheet = {
                     navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}")
                 }, navigateToItemAdd = {
                     navController.navigate(
                         navNull(
                             AnimalEntryDestination.route,
                             AnimalEntryDestination.itemIdPT,
                             it.toString()
                         )
                     )
                 }, navigateToItemCard = {
                     navController.navigate(
                         navNull(
                             route = AnimalCardDestination.route,
                             itemOne = it.first.toString(),
                             itemTwo = it.second.toString()
                         )
                     )
                 }, isFirstStart = isFirstStart
             )
         }*/

        composable(
            route = AnimalEntryDestination.routeWithArgs,
            arguments = listOf(
                navArgument(AnimalEntryDestination.itemIdPT) {
                    type = NavType.LongType
                },
                navArgument(AnimalEntryDestination.itemId) {
                    type = NavType.LongType
                    defaultValue = -1
                })
        ) {
            AnimalEntryProduct(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        /*composable(
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
                            route = AnimalEntryDestination.route,
                            itemOne = it.first.toString(),
                            itemTwo = it.second.toString()
                        )
                    )
                },
                onNavigateIndicators = { navController.navigate("${AnimalIndicatorsDestination.route}/${it.first}/${it.second}/${it.third}") })
        }

        composable(
            route = AnimalIndicatorsDestination.routeWithArgs,
            arguments = listOf(
                navArgument(AnimalIndicatorsDestination.itemIdArg) {
                    type = NavType.IntType
                },
                navArgument(AnimalIndicatorsDestination.itemIdArgTwo) {
                    type = NavType.IntType
                },
                navArgument(AnimalIndicatorsDestination.itemIdArgTree) {
                    type = NavType.LongType
                }
            )) {
            AnimalIndicatorsScreen(
                navigateBack = { navController.popBackStack() },
                navigateSection = { navController.navigate(it) }
            )
        }
*/
        /*composable(
            route = AnimalEditDestination.routeWithArgs,
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
        }*/


        /* composable(
             route = FinanceArhivDestination.routeWithArgs,
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

         composable(
             route = IncubatorArhivDestination.routeWithArgs,
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
 */
    }
}
