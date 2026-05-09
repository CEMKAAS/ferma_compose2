package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.incubator_project.main_screen.DestinationIncubator
import com.zaroslikov.fermacompose2.ui.project.mainScreen.Destination
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.Page


fun ProductOrigin.toDrawRes(): Int {
    return when (this) {
        ProductOrigin.ADD -> R.drawable.icon_add_product
        ProductOrigin.EXPENSES -> R.drawable.icon_expenses
        ProductOrigin.SALE -> R.drawable.icon_sale
    }
}

fun ProductOrigin.toCardDrawRes(): Int {
    return when (this) {
        ProductOrigin.ADD -> R.drawable.icon_add_product
        ProductOrigin.EXPENSES -> R.drawable.icon_expenses
        ProductOrigin.SALE -> R.drawable.icon_unknow
    }
}

fun AnimalCountVersion.toDrawRes(): Int {
    return when (this) {
        AnimalCountVersion.SALE -> R.drawable.icon_sale
        AnimalCountVersion.EXPENSES -> R.drawable.icon_expenses
        AnimalCountVersion.KILL -> R.drawable.icons8__meat60
        AnimalCountVersion.WRITE_OFF -> R.drawable.baseline_edit_note_24
        AnimalCountVersion.ADD -> R.drawable.baseline_add_circle_outline_24
        AnimalCountVersion.INCUBATOR -> R.drawable.outline_egg_24
    }
}

fun Page.toDrawRes(): Int {
    return when (this) {
        Page.SALE -> R.drawable.icon_sale
        Page.ADD -> R.drawable.icon_add_product
        Page.EXPENSES -> R.drawable.icon_expenses
        Page.WRITE_OFF -> R.drawable.baseline_edit_note_24
        Page.ANIMAL -> R.drawable.baseline_pets_24
    }
}

fun Destination.toDrawRes(): Int {
    return when (this) {
        Destination.MAGAZINE -> R.drawable.outline_menu_book_24
        Destination.WAREHOUSE -> R.drawable.baseline_warehouse_24
        Destination.FINANCE -> R.drawable.icon_money
    }
}

fun DestinationIncubator.toDrawRes(): Int {
    return when (this) {
        DestinationIncubator.JOURNAL -> R.drawable.outline_book_5_24
        DestinationIncubator.BOOKMARK -> R.drawable.baseline_edit_document_24
        DestinationIncubator.FINANCE -> R.drawable.icon_money
    }
}

fun FinanceCategory.toDrawRes(): Int {
    return when (this) {
        FinanceCategory.SALE -> R.drawable.icon_arrow_up
        FinanceCategory.EXPENSES -> R.drawable.icon_arrow_down
        FinanceCategory.OWN_NEED -> R.drawable.outline_savings_24
        FinanceCategory.SCRAP -> R.drawable.baseline_delete_24
        FinanceCategory.PROFIT -> R.drawable.baseline_currency_ruble_24
    }
}

fun FinanceCategory.toTransactionDrawRes(): Int {
    return when (this) {
        FinanceCategory.SALE -> R.drawable.icon_sale
        FinanceCategory.EXPENSES -> R.drawable.icon_expenses
        FinanceCategory.OWN_NEED -> R.drawable.outline_savings_24
        FinanceCategory.SCRAP -> R.drawable.baseline_delete_24
        FinanceCategory.PROFIT -> R.drawable.icon_add_product
    }
}