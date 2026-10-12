package com.zaroslikov.crm.ui.theme

import androidx.compose.ui.graphics.Color

// Палитра повторяет Color.kt из :app — модуль не может на него зависеть

val white = Color(0xFFFFFFFF)
val black_2 = Color(0xFF101828)
val marengo = Color(0xFF4A5565)
val gray_7 = Color(0xFF6A7282)
val gray_6 = Color(0xFFF3F4F6)
val grey_2 = Color(0xFFE5E7EB)
val gray_8 = Color(0xFFD1D5DC)

val green_6 = Color(0xFF00C950)
val green_7 = Color(0xFF05DF72)
val green_shamrock = Color(0xFF009966)
val price_green = Color(0xFF00A63E)
val green_8 = Color(0xFFDCFCE7)

val blue_10 = Color(0xFF1D293D)
val blue_11 = Color(0xFF385070)
val blue_12 = Color(0xFF0F172B)
val blue_4 = Color(0xFF2B7FFF)
val blue_13 = Color(0xFFDBEAFE)

val red_7 = Color(0xFFEC003F)
val error_base = Color(0xFFE7000B)
val red_light = Color(0xFFFFE4E6)

val orang_9 = Color(0xFFFE9A00)
val orang_8 = Color(0xFFFEF3C6)

val purple = Color(0xFF7C3AED)
val purple_light = Color(0xFFEDE9FE)

/** Цвета аватаров клиентов: выбираются детерминированно по имени. */
val crmAvatarPalette = listOf(
    green_shamrock,
    blue_4,
    purple,
    orang_9,
    red_7,
    Color(0xFF0891B2),
    Color(0xFFDB2777),
    blue_11
)
