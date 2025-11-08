package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.sections.add.list_screen.Page

fun Category.toDrawRes(): Int {
    return when (this) {
        Category.ADD -> R.drawable.icon_add_product
        Category.EXPENSES -> R.drawable.icon_sale
        Category.SALE -> R.drawable.icon_expenses
    }
}

fun AnimalCountVersion.toDrawRes(): Int {
    return when (this) {
        AnimalCountVersion.SALE -> R.drawable.baseline_add_card_24
        AnimalCountVersion.EXPENSES -> R.drawable.baseline_add_shopping_cart_24
        AnimalCountVersion.KILL -> R.drawable.icons8__meat60
        AnimalCountVersion.WRITE_OFF -> R.drawable.baseline_edit_note_24
        AnimalCountVersion.ADD -> R.drawable.baseline_add_circle_outline_24
        AnimalCountVersion.INCUBATOR -> R.drawable.baseline_pets_24
    }
}

fun Page.toDrawRes():Int{
    return when(this){
        Page.SALE -> R.drawable.icon_expenses
        Page.ADD -> R.drawable.icon_add_product
        Page.EXPENSES -> R.drawable.icon_sale
        Page.WRITE_OFF -> R.drawable.baseline_edit_note_24
        Page.ANIMAL -> R.drawable.baseline_pets_24
    }
}