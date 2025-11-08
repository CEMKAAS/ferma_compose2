package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.sections.add.list_screen.Page

fun infoTextKillAnimal(count: String, suffix: String): String {
    return "${count.toConvertZeroString().toFormatNumber()} " +
            suffix
}

fun Suffix.toResId(): Int {
    return when (this) {
        // Count
        Suffix.PIECES -> R.string.suffix_pieces
        Suffix.UNITS -> R.string.suffix_units
        Suffix.HEADS -> R.string.suffix_head
        // Weight
        Suffix.GRAM -> R.string.suffix_gram
        Suffix.KILOGRAM -> R.string.suffix_kilogram
        Suffix.TONS -> R.string.suffix_tons
        // Liters
        Suffix.LITERS -> R.string.suffix_liters
        Suffix.CUBIC_METERS -> R.string.suffix_cubic_meters
        // Size
        Suffix.METERS -> R.string.suffix_meters
        Suffix.MILLIMETERS -> R.string.suffix_millimeters
        Suffix.CENTIMETERS -> R.string.suffix_centimetre
        // Money
        Suffix.RUBLE -> R.string.currency_ruble
    }
}

fun Category.toResId(): Int {
    return when (this) {
        Category.ADD -> R.string.add_screen_title
        Category.EXPENSES -> R.string.expenses_screen_title
        Category.SALE -> R.string.sale_screen_title
    }
}

fun AnimalCountVersion.toResId(): Int {
    return when (this) {
        AnimalCountVersion.SALE -> R.string.button_sale
        AnimalCountVersion.EXPENSES -> R.string.button_expenses
        AnimalCountVersion.KILL -> R.string.button_kill
        AnimalCountVersion.WRITE_OFF -> R.string.button_write_off
        AnimalCountVersion.ADD -> R.string.button_add
        AnimalCountVersion.INCUBATOR -> R.string.button_add
    }
}

fun Page.toResId(): Int {
    return when (this) {
        Page.ADD -> R.string.add_screen_title2
        Page.SALE -> R.string.sale_screen_title2
        Page.EXPENSES -> R.string.expenses_screen_title2
        Page.WRITE_OFF -> R.string.write_off_screen_title2
        Page.ANIMAL -> R.string.animal_screen_title2
    }
}