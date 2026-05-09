package com.zaroslikov.fermacompose2.supportFun

import androidx.compose.ui.graphics.Color
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.domain.models.enums.supportUi.TypeProduct
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_10
import com.zaroslikov.fermacompose2.blue_11
import com.zaroslikov.fermacompose2.blue_12
import com.zaroslikov.fermacompose2.blue_13
import com.zaroslikov.fermacompose2.blue_17
import com.zaroslikov.fermacompose2.blue_2
import com.zaroslikov.fermacompose2.blue_21
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_4
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.green_5
import com.zaroslikov.fermacompose2.green_6
import com.zaroslikov.fermacompose2.green_7
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.green_g_2
import com.zaroslikov.fermacompose2.green_g_4
import com.zaroslikov.fermacompose2.green_g_5
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_1
import com.zaroslikov.fermacompose2.orang_10
import com.zaroslikov.fermacompose2.orang_15
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_3
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_8
import com.zaroslikov.fermacompose2.orang_9
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.red_11
import com.zaroslikov.fermacompose2.red_12
import com.zaroslikov.fermacompose2.red_15
import com.zaroslikov.fermacompose2.red_18
import com.zaroslikov.fermacompose2.red_3
import com.zaroslikov.fermacompose2.red_4
import com.zaroslikov.fermacompose2.red_5
import com.zaroslikov.fermacompose2.red_6
import com.zaroslikov.fermacompose2.red_7
import com.zaroslikov.fermacompose2.ui.incubator_project.main_screen.DestinationIncubator
import com.zaroslikov.fermacompose2.ui.project.mainScreen.Destination
import com.zaroslikov.fermacompose2.ui.project.mainScreen.Destination.*
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.Page
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_2
import com.zaroslikov.fermacompose2.violet_3
import com.zaroslikov.fermacompose2.violet_4
import com.zaroslikov.fermacompose2.violet_5
import com.zaroslikov.fermacompose2.violet_7

fun ProductOrigin.toColorList(): Color {
    return when (this) {
        ProductOrigin.ADD -> green_shamrock
        ProductOrigin.EXPENSES -> orang_1
        ProductOrigin.SALE -> blue_1
    }
}

fun Page.toColorList(): Color {
    return when (this) {
        Page.ADD -> green_shamrock
        Page.EXPENSES -> orang_1
        Page.SALE -> blue_1
        Page.WRITE_OFF -> violet_1
        Page.ANIMAL -> animal_1
    }
}

fun AnimalCountVersion.toColorList(): List<Color> {
    return when (this) {
        AnimalCountVersion.SALE -> listOf(blue_1, blue_2)
        AnimalCountVersion.EXPENSES -> listOf(orang_2, orang_1)
        AnimalCountVersion.KILL -> listOf(red_4, red_5)
        AnimalCountVersion.WRITE_OFF -> listOf(violet_1, violet_2)
        AnimalCountVersion.ADD -> listOf(Color(0xFF00A63E), Color(0xFF009966))
        AnimalCountVersion.INCUBATOR -> listOf(orang_2, orang_1)
    }
}

fun Suffix.toColorList(): Color {
    return when (this) {
        Suffix.PIECES -> Color(0xFFFFC107)   // Amber 500 — базовый
        Suffix.HEADS -> Color(0xFFFFB300)    // Amber 600 — чуть теплее
        Suffix.UNITS -> Color(0xFFFFA000)    // Amber 700 — насыщеннее

        Suffix.GRAM -> Color(0xFF80CBC4)
        Suffix.KILOGRAM -> Color(0xFF26A69A)
        Suffix.TONS -> Color(0xFF00695C)   // Green 800 — глубокий и "тяжёлый"

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
        else -> gray_6
    }
}

fun Destination.toColorList(): List<Color> {
    return when (this) {
        MAGAZINE -> listOf(green_5, green_shamrock)
        WAREHOUSE -> listOf(blue_21, blue_4)
        FINANCE -> listOf(blue_11, blue_12)
    }
}

fun DestinationIncubator.toColorList(): List<Color> {
    return when (this) {
        DestinationIncubator.JOURNAL -> listOf(blue_21, blue_4)
        DestinationIncubator.BOOKMARK -> listOf(orang_9, orang_15)
        DestinationIncubator.FINANCE -> listOf(green_6, green_shamrock)
    }
}


fun ProductOrigin.toColorFinance(): Color {
    return when (this) {
        ProductOrigin.ADD -> blue_3
        ProductOrigin.EXPENSES -> orang_3
        ProductOrigin.SALE -> blue_3
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


/*fun TypeProduct.toColorList(): List<Color> {
    return when (this) {
        TypeProduct.ANIMAL -> listOf(animal_1, animal_2)
        TypeProduct.FOOD -> listOf(orang_8, orang_9 )
        TypeProduct.VACCINATION -> listOf(violet_4, violet_2)
        TypeProduct.KILL -> listOf(red_4, red_5)
    }
}*/

fun TypeProduct.toColorList(): List<Color> {
    return when (this) {
        TypeProduct.ANIMAL -> listOf( green_g_4, animal_1)
        TypeProduct.FOOD -> listOf(orang_8, orang_9)
        TypeProduct.VACCINATION -> listOf(violet_5, violet_4)
        TypeProduct.KILL -> listOf( red_15, red_4)
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

fun TypeEgg.toColor(): List<Color> {
    return when (this) {
        TypeEgg.CHICKENS -> listOf(orang_10, orang_15)
        TypeEgg.GEESE -> listOf(red_6, red_18)
        TypeEgg.QUAILS -> listOf(blue_17, blue_4)
        TypeEgg.TURKEYS -> listOf(violet_7, violet_4)
        TypeEgg.DUCKS -> listOf(green_7, green_6)
    }
}

fun TypeEgg.toCardColor(): Pair<Color, Color> {
    return when (this) {
        TypeEgg.CHICKENS -> orang_4 to orang_10
        TypeEgg.GEESE -> red_3 to red_5
        TypeEgg.QUAILS -> blue_3 to blue_17
        TypeEgg.TURKEYS -> violet_3 to violet_7
        TypeEgg.DUCKS -> green_g_2 to green_g_5
    }
}
