package com.zaroslikov.domain.models.enums

enum class Category (val id: Int, val drawerRes: Int, val titleRes: Int) {
    ADD(
        id = 0,
        drawerRes = R.drawable.baseline_add_circle_outline_24,
        titleRes = R.string.add_screen_title
    ),
    EXPENSES(
        id = 1,
        drawerRes = R.drawable.baseline_add_shopping_cart_24,
        titleRes = R.string.expenses_screen_title
    ),
    SALE(
        id = 2,
        drawerRes = R.drawable.baseline_add_card_24,
        titleRes = R.string.sale_screen_title
    );

    fun toDrawer(): Int = drawerRes
    fun toResInt(): Int = titleRes

    companion object {
        fun fromId(id: Int): com.zaroslikov.fermacompose2.ui.composeElement.Category =
            entries.firstOrNull { it.id == id } ?: ADD
    }
}