package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.fermacompose2.ui.finance.FinanceDestination
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.main.BookmarkDestination
import com.zaroslikov.fermacompose2.ui.incubator_project.main_screen.DestinationIncubator
import com.zaroslikov.fermacompose2.ui.project.mainScreen.Destination
import com.zaroslikov.fermacompose2.ui.project.sections.HomeDestination
import com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen.WarehouseDestination

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


fun DestinationIncubator.toNav(itemPT: Long): String {
    return when (this) {
        DestinationIncubator.REVIEW -> "${HomeDestination.route}/${itemPT}"
        DestinationIncubator.BOOKMARK -> "${BookmarkDestination.route}/${itemPT}"
        DestinationIncubator.FINANCE -> "${FinanceDestination.route}/${itemPT}"
    }
}
