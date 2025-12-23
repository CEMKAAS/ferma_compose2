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
import com.zaroslikov.fermacompose2.ui.add.ProjectAddDestination
import com.zaroslikov.fermacompose2.ui.project.mainScreen.MainProjectScreen
import com.zaroslikov.fermacompose2.ui.project.mainScreen.MainProjectsDestination
import com.zaroslikov.fermacompose2.ui.start.StartScreen.StartDestination
import com.zaroslikov.fermacompose2.ui.start.StartScreen.StartScreen
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseEditDestination
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseEditScreen

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
//                        "${HomeDestination.route}/${it}"
                        "${MainProjectsDestination.route}/${it}"
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
        composable(
            route = MainProjectsDestination.routeWithArgs,
            arguments = listOf(navArgument(MainProjectsDestination.itemIdArg) {
                type = NavType.LongType
            })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments!!.getLong(MainProjectsDestination.itemIdArg)
            MainProjectScreen(itemId)
        }
        composable(route = ProjectAddDestination.route) {
            AddProject(
                navigateBack = { navController.popBackStack() },
                navigateToStart = { navController.navigate(StartDestination.route) },
            )
        }

        /* composable(route = WarehouseEditDestination.route) {
             WarehouseEditScreen(
                 navigateBack = { navController.popBackStack() },
                 navigateUp = { navController.navigateUp() },
                 navigateToStart = { navController.navigate(StartDestination.route) })
         }*/

        composable(
            route = WarehouseEditDestination.routeWithArgs,
            arguments = listOf(navArgument(WarehouseEditDestination.itemIdArg) {
                type = NavType.LongType
            })
        ) {
            WarehouseEditScreen(
                navigateBack = navController::popBackStack)

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



*/
        //==================== Finance ====================
        /*   composable(
               route = FinanceDestination.routeWithArgs,
               arguments = listOf(navArgument(FinanceDestination.itemIdArg) {
                   type = NavType.IntType
               })
           ) {
               FinanceScreen(
                   navigateToStart = { navController.navigate(StartDestination.route) },
                   navigateToModalSheet = { navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}") },
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
                   },
                   navigateToFinanceMonth = {
                       navController.navigate(
                           navNull(
                               FinanceMonthDestination.route,
                               FinanceMonthDestination.itemIdPT,
                               it.toString()
                           )
                       )
                   }
               )
           }
           composable(
               route = FinanceMonthDestination.routeWithArgs,
               arguments = listOf(navArgument(FinanceMonthDestination.itemIdPT) {
                   type = NavType.LongType
               })
           ) {
               FinanceMonthScreen(
                   navigateBack = { navController.popBackStack() },
                   *//*  navigateToCategory = {
                navController.navigate( "${FinanceCategoryDestination.route}/${it.idPT}/${it.category}/${it.incomeBoolean}/${it.dateBegin}/${it.dateEnd}"
               }*//*
            )
        }*/

        /*composable(
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
        }*/
        /*composable(
            route = FinanceIncomeExpensesDestination.routeWithArgs,
            arguments = listOf(navArgument(FinanceIncomeExpensesDestination.itemIdArg) {
                type = NavType.LongType
            }, navArgument(FinanceIncomeExpensesDestination.itemIdArgTwo) {
                type = NavType.BoolType
            })
        ) {
            FinanceIncomeExpensesScreen(
                navigateBack = { navController.popBackStack() },
                navigationToAnalysis = {
                    navController.navigate(
                        "${FinanceAnalysisDestination.route}/${it.first}/${it.second}"
                    )
                },
            )
        }
        composable(
            route = FinanceAnalysisDestination.routeWithArgs,
            arguments = listOf(navArgument(FinanceAnalysisDestination.itemIdArg) {
                type = NavType.LongType
            }, navArgument(FinanceAnalysisDestination.itemIdArgTwo) {
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
                            itemTwo = it.second.toString()
                        )
                    )
                }
            )*/
        /* AddScreen(
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
         )*/
        /*   }
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
               *//*SaleScreen(drawerState = drawerState, navigateToStart = {
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
            })*//*
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
        *//*composable(
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
        }*//*

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
        *//*composable(
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
        }*//*
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
            NoteScreen(navigateToStart = {
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
        // Animal
        composable(
            route = AnimalDestination.routeWithArgs,
            arguments = listOf(navArgument(AnimalDestination.itemIdArg) {
                type = NavType.LongType
            })
        ) {
            AnimalScreen(
                *//*   drawerState = drawerState, navigateToStart = {
                       navController.navigate(StartDestination.route)
                   }, navigateToModalSheet = {
                       navController.navigate("${it.routeDrawer}/${it.idProjectDrawer}")
                   }, , navigateToItemCard = {
                       navController.navigate(
                           navNull(
                               route = AnimalCardDestination.route,
                               itemOne = it.first.toString(),
                               itemTwo = it.second.toString()
                           )
                       )
                   }, isFirstStart = isFirstStart*//*
                *//*  navigateToItemAdd = {
                      navController.navigate(
                          navNull(
                              AnimalEntryDestination.route,
                              AnimalEntryDestination.itemIdPT,
                              it.toString()
                          )
                      )
                  }*//*
            )
        }

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
                            route = AnimalEntryDestination.route,
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
        }*/

        /*
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
