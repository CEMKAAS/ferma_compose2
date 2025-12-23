package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.fermacompose2.ui.finance.FinanceDestination
import com.zaroslikov.fermacompose2.ui.project.mainScreen.Destination
import com.zaroslikov.fermacompose2.ui.sections.HomeDestination
import com.zaroslikov.fermacompose2.ui.warehouse.warehouseScreen.WarehouseDestination

fun Destination.toNav(itemPT: Long): String {
    return when (this) {
        Destination.MAGAZINE -> "${HomeDestination.route}/${itemPT}"
        Destination.WAREHOUSE -> "${WarehouseDestination.route}/${itemPT}"
        Destination.FINANCE -> "${FinanceDestination.route}/${itemPT}"
    }
}

fun Destination.toRoute(itemPT: Long): String {
    return when (this) {
        Destination.MAGAZINE -> HomeDestination.route
        Destination.WAREHOUSE -> WarehouseDestination.route
        Destination.FINANCE -> FinanceDestination.route
    }
}