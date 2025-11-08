package com.zaroslikov.fermacompose2.supportFun

import androidx.compose.ui.graphics.Color
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.green_shamrock
import com.zaroslikov.fermacompose2.orang_1
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.ui.elements.CountColorCard
import com.zaroslikov.fermacompose2.ui.sections.add.list_screen.Page
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_2

fun Category.toColor(): Color {
    return when (this) {
        Category.ADD -> green_shamrock
        Category.EXPENSES -> orang_1
        Category.SALE -> blue_1
    }
}

fun Page.toColor(): Color {
    return when (this) {
        Page.ADD -> green_shamrock
        Page.EXPENSES -> orang_1
        Page.SALE -> blue_1
        Page.WRITE_OFF -> violet_1
        Page.ANIMAL -> violet_2
    }
}

fun Suffix.toColor(): Color {
    return when (this) {
        Suffix.PIECES -> Color(0xFFFFC107)   // Amber 500 — базовый
        Suffix.HEADS -> Color(0xFFFFB300)    // Amber 600 — чуть теплее
        Suffix.UNITS -> Color(0xFFFFA000)    // Amber 700 — насыщеннее

        Suffix.GRAM -> Color(0xFFA5D6A7)     // Light green 300 — лёгкий
        Suffix.KILOGRAM -> Color(0xFF66BB6A) // Green 400 — стандартный
        Suffix.TONS -> Color(0xFF2E7D32)     // Green 800 — глубокий и "тяжёлый"

        // --- Объём ---
        Suffix.LITERS -> Color(0xFF64B5F6)   // Blue 300 — мягкий
        Suffix.CUBIC_METERS -> Color(0xFF1565C0) // Blue 800 — глубокая вода

        // --- Длина ---
        Suffix.MILLIMETERS -> Color(0xFFB39DDB)  // Deep Purple 300 — лёгкий
        Suffix.CENTIMETERS -> Color(0xFF7E57C2)  // Deep Purple 400
        Suffix.METERS -> Color(0xFF512DA8)       // Deep Purple 700 — насыщенный

        // --- Валюта ---
        Suffix.RUBLE -> price_green
    }
}
