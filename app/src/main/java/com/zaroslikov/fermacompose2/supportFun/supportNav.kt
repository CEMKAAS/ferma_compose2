package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.fermacompose2.ui.finance.FinanceDestination
import com.zaroslikov.fermacompose2.ui.project.mainScreen.Destination
import com.zaroslikov.fermacompose2.ui.sections.HomeDestination

fun Destination.toNav(itemPT: Long): String {
    return when (this) {
        Destination.MAGAZINE -> "${HomeDestination.route}/${itemPT}"
        Destination.WAREHOUSE -> "${HomeDestination.route}/${itemPT}"
        Destination.FINANCE -> "${FinanceDestination.route}/${itemPT}"
    }
}

fun Destination.toRoute(itemPT: Long): String {
    return when (this) {
        Destination.MAGAZINE -> HomeDestination.route
        Destination.WAREHOUSE -> HomeDestination.route
        Destination.FINANCE -> FinanceDestination.route
    }
}