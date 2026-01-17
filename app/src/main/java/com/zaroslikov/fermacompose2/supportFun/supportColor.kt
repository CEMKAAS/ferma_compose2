package com.zaroslikov.fermacompose2.supportFun

import androidx.compose.ui.graphics.Color
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_10
import com.zaroslikov.fermacompose2.blue_11
import com.zaroslikov.fermacompose2.blue_12
import com.zaroslikov.fermacompose2.blue_13
import com.zaroslikov.fermacompose2.blue_2
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.green_5
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_1
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_3
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_8
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_12
import com.zaroslikov.fermacompose2.red_4
import com.zaroslikov.fermacompose2.red_5
import com.zaroslikov.fermacompose2.red_7
import com.zaroslikov.fermacompose2.ui.incubator_project.main_screen.DestinationIncubator
import com.zaroslikov.fermacompose2.ui.project.mainScreen.Destination
import com.zaroslikov.fermacompose2.ui.project.mainScreen.Destination.*
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.Page
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_2
import com.zaroslikov.fermacompose2.white

fun Category.toColorList(): Color {
    return when (this) {
        Category.ADD -> green_shamrock
        Category.EXPENSES -> orang_1
        Category.SALE -> blue_1
    }
}

fun Page.toColorList(): Color {
    return when (this) {
        Page.ADD -> green_shamrock
        Page.EXPENSES -> orang_1
        Page.SALE -> blue_1
        Page.WRITE_OFF -> violet_1
        Page.ANIMAL -> violet_2
    }
}

fun AnimalCountVersion.toColorList(): List<Color> {
    return when (this) {
        AnimalCountVersion.SALE -> listOf(blue_1, blue_2)
        AnimalCountVersion.EXPENSES -> listOf(orang_2, orang_1)
        AnimalCountVersion.KILL -> listOf(red_4, red_5)
        AnimalCountVersion.WRITE_OFF -> listOf(violet_1, violet_2)
        AnimalCountVersion.ADD -> listOf(Color(0xFF00A63E), Color(0xFF009966))
        AnimalCountVersion.INCUBATOR -> listOf(Color(0xFF009966), Color(0xFF00A63E))
    }
}

fun Suffix.toColorList(): Color {
    return when (this) {
        Suffix.PIECES -> Color(0xFFFFC107)   // Amber 500 — базовый
        Suffix.HEADS -> Color(0xFFFFB300)    // Amber 600 — чуть теплее
        Suffix.UNITS -> Color(0xFFFFA000)    // Amber 700 — насыщеннее

        Suffix.GRAM -> Color(0xFFA5D6A7)     // Light green 300 — лёгкий
        Suffix.KILOGRAM -> Color(0xFF66BB6A) // Green 400 — стандартный
        Suffix.TONS -> Color(0xFF2E7D32)     // Green 800 — глубокий и "тяжёлый"

        // --- Объём ---
        Suffix.MILLILITRES -> Color(0xFF64B5F6)
        Suffix.LITERS -> Color(0xFF64B5F6)   // Blue 300 — мягкий
        Suffix.CUBIC_METERS -> Color(0xFF1565C0) // Blue 800 — глубокая вода

        // --- Длина ---
        Suffix.MILLIMETERS -> Color(0xFFB39DDB)  // Deep Purple 300 — лёгкий
        Suffix.CENTIMETERS -> Color(0xFF7E57C2)  // Deep Purple 400
        Suffix.METERS -> Color(0xFF512DA8)       // Deep Purple 700 — насыщенный

        // --- Валюта ---

        Suffix.GRAM_DAY -> Color(0xFFA5D6A7)
        Suffix.KILOGRAM_DAY -> Color(0xFF66BB6A)
        Suffix.TONS_DAY -> Color(0xFF2E7D32)

        Suffix.DOLLAR, Suffix.EURO, Suffix.TENGE, Suffix.RUBLE -> price_green
    }
}

fun Destination.toColorList(): List<Color> {
    return when (this) {
        MAGAZINE -> listOf(green_5, green_shamrock)
        WAREHOUSE -> listOf(green_6, price_green)
        FINANCE -> listOf(white, blue_2)
    }
}

fun DestinationIncubator.toColorList(): List<Color> {
    return when (this) {
        DestinationIncubator.REVIEW -> listOf(green_5, green_shamrock)
        DestinationIncubator.BOOKMARK -> listOf(green_6, price_green)
        DestinationIncubator.FINANCE -> listOf(white, blue_2)
    }
}


fun Category.toColorFinance(): Color {
    return when (this) {
        Category.ADD -> blue_3
        Category.EXPENSES -> orang_3
        Category.SALE -> blue_3
    }
}

fun FinanceCategory.toColorList(): List<Color> {
    return when (this) {
        FinanceCategory.SALE -> listOf(green_6, green_shamrock)
        FinanceCategory.EXPENSES -> listOf(Color(0xFFFB2C36), red_7)
        FinanceCategory.OWN_NEED -> listOf(Color(0xFFFE9A00), Color(0xFFFF6900))
        FinanceCategory.SCRAP -> listOf(Color(0xFF4A5565), Color(0xFF364153))
        FinanceCategory.PROFIT -> /*listOf(blue_1, animal_2)*/ listOf(blue_10, blue_11, blue_12)
    }
}

/*fun FinanceCategory.toColor(): Color {
    return when (this) {
        FinanceCategory.SALE -> blue_1
        FinanceCategory.EXPENSES -> orang_1
        FinanceCategory.OWN_NEED -> violet_1
        FinanceCategory.SCRAP -> violet_1
        FinanceCategory.PROFIT -> TODO()
    }
}

fun FinanceCategory.toColorFinance(): Color {
    return when (this) {
        FinanceCategory.SALE -> blue_3
        FinanceCategory.EXPENSES -> orang_3
        FinanceCategory.OWN_NEED -> violet_3
        FinanceCategory.SCRAP -> violet_3
        FinanceCategory.PROFIT -> TODO()
    }
}*/

fun FinanceCategory.toColorIconSecond(): Color {
    return when (this) {
        FinanceCategory.SALE -> price_green
        FinanceCategory.EXPENSES -> error_base
        FinanceCategory.OWN_NEED -> orang_2
        FinanceCategory.SCRAP -> marengo
        FinanceCategory.PROFIT -> blue_1
    }
}

fun FinanceCategory.toColorIconBorderSecond(): Color {
    return when (this) {
        FinanceCategory.SALE -> price_green_2
        FinanceCategory.EXPENSES -> red_11
        FinanceCategory.OWN_NEED -> orang_4
        FinanceCategory.SCRAP -> gray_6
        FinanceCategory.PROFIT -> blue_3
    }
}

fun FinanceCategory.toBorderCard(): Color {
    return when (this) {
        FinanceCategory.SALE -> green_8
        FinanceCategory.EXPENSES -> red_12
        FinanceCategory.OWN_NEED -> orang_8
        FinanceCategory.SCRAP -> grey_2
        FinanceCategory.PROFIT -> blue_13
    }
}
