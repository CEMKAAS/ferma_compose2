package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.FilterDate
import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.incubator_project.main_screen.DestinationIncubator
import com.zaroslikov.fermacompose2.ui.incubator_project.main_screen.DestinationIncubator.*
import com.zaroslikov.fermacompose2.ui.project.mainScreen.Destination
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.Page

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
        Suffix.MILLILITRES -> R.string.suffix_millilitres
        Suffix.LITERS -> R.string.suffix_liters
        Suffix.CUBIC_METERS -> R.string.suffix_cubic_meters
        // Size
        Suffix.METERS -> R.string.suffix_meters
        Suffix.MILLIMETERS -> R.string.suffix_millimeters
        Suffix.CENTIMETERS -> R.string.suffix_centimetre
        // Money
        Suffix.RUBLE -> R.string.currency_ruble
        Suffix.TENGE -> R.string.currency_tenge
        Suffix.DOLLAR -> R.string.currency_dollar
        Suffix.EURO -> R.string.currency_euro
        Suffix.GRAM_DAY -> R.string.suffix_gram_day
        Suffix.KILOGRAM_DAY -> R.string.suffix_kilogram_day
        Suffix.TONS_DAY -> R.string.suffix_tons_day
        Suffix.KILOGRAM_TO_CUBIC_METERS -> R.string.suffix_cubic_kilogram_to_meters
        Suffix.KILOGRAM_TO_LITERS -> R.string.suffix_kilogram_to_liters
    }
}

fun Suffix.toFullResId(): Int {
    return when (this) {
        Suffix.RUBLE -> R.string.currency_full_ruble
        Suffix.TENGE -> R.string.currency_full_tenge
        Suffix.DOLLAR -> R.string.currency_full_dollar
        Suffix.EURO -> R.string.currency_full_euro

        // Weight
        Suffix.GRAM -> R.string.suffix_full_gram
        Suffix.KILOGRAM -> R.string.suffix_full_kilogram
        Suffix.TONS -> R.string.suffix_full_tons

        // Liters
        Suffix.MILLILITRES -> R.string.suffix_full_millilitres
        Suffix.LITERS -> R.string.suffix_full_liters
        Suffix.CUBIC_METERS -> R.string.suffix_full_cubic_meters

        // Size
        Suffix.METERS -> R.string.suffix_full_meters
        Suffix.MILLIMETERS -> R.string.suffix_full_millimeters
        Suffix.CENTIMETERS -> R.string.suffix_full_centimetre
        else -> R.string.is_empty
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

fun Destination.toResId(): Int {
    return when (this) {
        Destination.MAGAZINE -> R.string.bottom_bar_magazine
        Destination.WAREHOUSE -> R.string.bottom_bar_warehouse
        Destination.FINANCE -> R.string.bottom_bar_finance
    }
}

fun DestinationIncubator.toResId(): Int {
    return when (this) {
        JOURNAL -> R.string.bottom_bar_incubator
        BOOKMARK -> R.string.bottom_bar_bookmark
        FINANCE -> R.string.bottom_bar_finance
    }
}

fun FinanceCategory.toResId(): Int {
    return when (this) {
        FinanceCategory.SALE -> R.string.finance_category_income_total
        FinanceCategory.EXPENSES -> R.string.finance_category_expenses_total
        FinanceCategory.OWN_NEED -> R.string.finance_category_onw_need_total
        FinanceCategory.SCRAP -> R.string.finance_category_scrap_total
        FinanceCategory.PROFIT -> /*R.string.finance_category_total_profit*/ R.string.support_text_current_balance
    }
}

fun FinanceCategory.toTitleResId(): Int {
    return when (this) {
        FinanceCategory.SALE -> R.string.card_income
        FinanceCategory.EXPENSES -> R.string.card_expenditure
        FinanceCategory.OWN_NEED -> R.string.card_own_need
        FinanceCategory.SCRAP -> R.string.card_scrap
        FinanceCategory.PROFIT -> R.string.card_profit
    }
}

fun FinanceCategory.toTitleSecondResId(): Int {
    return when (this) {
        FinanceCategory.SALE -> R.string.finance_income
        FinanceCategory.EXPENSES -> R.string.finance_expenses
        FinanceCategory.OWN_NEED -> R.string.card_own_need
        FinanceCategory.SCRAP -> R.string.card_scrap
        FinanceCategory.PROFIT -> R.string.finance_total
    }
}

fun FilterDate.toResId(): Int {
    return when (this) {
        FilterDate.TODAY -> R.string.filter_date_today
        FilterDate.WEEK -> R.string.filter_date_week
        FilterDate.MONTH -> R.string.filter_date_month
        FilterDate.YEAR -> R.string.filter_date_year
        FilterDate.ALL_TIME -> R.string.filter_date_all_time
        FilterDate.PERIOD -> R.string.filter_date_period
    }
}

fun TypeEgg.toResId(): Int {
    return when (this) {
        TypeEgg.CHICKENS -> R.string.type_egg_chickens
        TypeEgg.GEESE -> R.string.type_egg_geese
        TypeEgg.QUAILS -> R.string.type_egg_quails
        TypeEgg.TURKEYS -> R.string.type_egg_turkeys
        TypeEgg.DUCKS -> R.string.type_egg_ducks
    }
}
