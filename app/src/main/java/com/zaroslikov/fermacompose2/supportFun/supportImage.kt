package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.incubator_project.main_screen.DestinationIncubator
import com.zaroslikov.fermacompose2.ui.project.mainScreen.Destination
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.Page
import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.domain.models.enums.AppIcon

/**
 * Иконка группы птицы этого вида — её ставит и завершение закладки своего инкубатора,
 * и птенцы, пришедшие из приложения «Инкубатор». [AppIcon], а не `R.drawable`: в базу
 * уходит стабильный код, см. комментарий у [AppIcon].
 */
fun TypeEgg.toAnimalIcon(): AppIcon {
    return when (this) {
        TypeEgg.CHICKENS -> AppIcon.CHICK
        TypeEgg.GEESE -> AppIcon.GOOSE
        TypeEgg.QUAILS -> AppIcon.QUAIL
        TypeEgg.TURKEYS -> AppIcon.TURKEY
        TypeEgg.DUCKS -> AppIcon.DUCK
    }
}

/** Иконка системного вида в подсказках типа; у своего типа (UNKNOWN) иконки нет. */
fun AnimalSpecies.toAnimalIcon(): AppIcon? {
    return when (this) {
        AnimalSpecies.UNKNOWN -> null
        AnimalSpecies.RABBIT -> AppIcon.CRUELTY_FREE
        AnimalSpecies.PIG -> AppIcon.PIG
        AnimalSpecies.COW -> AppIcon.COW
        AnimalSpecies.SHEEP -> AppIcon.SHEEP
        AnimalSpecies.GOAT -> AppIcon.GOAT
        AnimalSpecies.HORSE -> AppIcon.HORSE
        AnimalSpecies.TURKEY -> AppIcon.TURKEY
        AnimalSpecies.CHICKEN -> AppIcon.CHICKEN
        AnimalSpecies.QUAIL -> AppIcon.QUAIL
        AnimalSpecies.DUCK -> AppIcon.DUCK
        AnimalSpecies.GOOSE -> AppIcon.GOOSE
        // Своих иконок нет — ближайшие похожие птицы
        AnimalSpecies.PHEASANT -> AppIcon.CHICKEN
        AnimalSpecies.MUSCOVY_DUCK -> AppIcon.DUCK
    }
}


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
        AnimalCountVersion.TRANSFER_IN, AnimalCountVersion.TRANSFER_OUT -> R.drawable.icon_group
        AnimalCountVersion.HERD_JOIN -> R.drawable.ic_herd
        AnimalCountVersion.HERD_LEAVE -> R.drawable.ic_herd_leave
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
        FinanceCategory.RECYCLING -> R.drawable.baseline_autorenew_24
    }
}

fun FinanceCategory.toTransactionDrawRes(): Int {
    return when (this) {
        FinanceCategory.SALE -> R.drawable.icon_sale
        FinanceCategory.EXPENSES -> R.drawable.icon_expenses
        FinanceCategory.OWN_NEED -> R.drawable.outline_savings_24
        FinanceCategory.SCRAP -> R.drawable.baseline_delete_24
        FinanceCategory.PROFIT -> R.drawable.icon_add_product
        FinanceCategory.RECYCLING -> R.drawable.baseline_autorenew_24
    }
}