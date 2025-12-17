package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.project.mainScreen.Destination
import com.zaroslikov.fermacompose2.ui.project.mainScreen.Destination.*
import com.zaroslikov.fermacompose2.ui.sections.add.list_screen.Page

fun Category.toDrawRes(): Int {
    return when (this) {
        Category.ADD -> R.drawable.icon_add_product
        Category.EXPENSES -> R.drawable.icon_sale
        Category.SALE -> R.drawable.icon_expenses
    }
}

fun AnimalCountVersion.toDrawRes(): Int {
    return when (this) {
        AnimalCountVersion.SALE -> R.drawable.baseline_add_card_24
        AnimalCountVersion.EXPENSES -> R.drawable.baseline_add_shopping_cart_24
        AnimalCountVersion.KILL -> R.drawable.icons8__meat60
        AnimalCountVersion.WRITE_OFF -> R.drawable.baseline_edit_note_24
        AnimalCountVersion.ADD -> R.drawable.baseline_add_circle_outline_24
        AnimalCountVersion.INCUBATOR -> R.drawable.baseline_pets_24
    }
}

fun Page.toDrawRes(): Int {
    return when (this) {
        Page.SALE -> R.drawable.icon_expenses
        Page.ADD -> R.drawable.icon_add_product
        Page.EXPENSES -> R.drawable.icon_sale
        Page.WRITE_OFF -> R.drawable.baseline_edit_note_24
        Page.ANIMAL -> R.drawable.baseline_pets_24
    }
}

fun Destination.toDrawRes(): Int {
    return when (this) {
        MAGAZINE -> R.drawable.baseline_edit_document_24
        WAREHOUSE -> R.drawable.baseline_warehouse_24
        FINANCE -> R.drawable.baseline_currency_ruble_24
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
        FinanceCategory.SALE -> R.drawable.icon_expenses
        FinanceCategory.EXPENSES -> R.drawable.icon_sale
        FinanceCategory.OWN_NEED -> R.drawable.outline_savings_24
        FinanceCategory.SCRAP -> R.drawable.baseline_delete_24
        FinanceCategory.PROFIT -> R.drawable.icon_add_product
    }
}