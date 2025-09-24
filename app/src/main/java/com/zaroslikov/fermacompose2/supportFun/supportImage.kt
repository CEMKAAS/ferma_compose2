package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.fermacompose2.R

fun Category.toDrawRes(): Int {
    return when (this) {
        Category.ADD -> R.drawable.baseline_add_circle_outline_24
        Category.EXPENSES -> R.drawable.baseline_add_shopping_cart_24
        Category.SALE -> R.drawable.baseline_add_card_24
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