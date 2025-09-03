package com.zaroslikov.domain.models.enums

enum class Category {
    ADD,
    EXPENSES,
    SALE;
}



/*enum class Category {
    ADD, EXPENSES, SALE;

    fun toDrawer(): Int {
        return when (this) {
            ADD -> R.drawable.baseline_add_circle_outline_24
            EXPENSES -> R.drawable.baseline_add_shopping_cart_24
            SALE -> R.drawable.baseline_add_card_24
        }
    }

    fun toResInt(): Int {
        return when (this) {
            ADD -> R.string.add_screen_title
            EXPENSES -> R.string.expenses_screen_title
            SALE -> R.string.sale_screen_title
        }
    }
}*/