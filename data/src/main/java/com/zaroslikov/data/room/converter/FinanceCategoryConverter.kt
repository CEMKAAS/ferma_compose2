package com.zaroslikov.data.room.converter

import androidx.room.TypeConverter
import com.zaroslikov.domain.models.enums.FinanceCategory

class FinanceCategoryConverter {
    @TypeConverter
    fun fromFinanceCategory(category: FinanceCategory): Int =
        if (category == FinanceCategory.RECYCLING) RECYCLING_ID else category.ordinal

    @TypeConverter
    fun toFinanceCategory(id: Int): FinanceCategory = financeCategoryFromId(id)
}

/** 5 в запросах уже занят под поступления (читается как PROFIT), переработка — 6. */
const val RECYCLING_ID = 6

fun financeCategoryFromId(id: Int): FinanceCategory {
    return when (id) {
        0 -> FinanceCategory.SALE
        1 -> FinanceCategory.EXPENSES
        2 -> FinanceCategory.OWN_NEED
        3 -> FinanceCategory.SCRAP
        RECYCLING_ID -> FinanceCategory.RECYCLING
        else -> FinanceCategory.PROFIT
    }
}


/*ADD(
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
}*/