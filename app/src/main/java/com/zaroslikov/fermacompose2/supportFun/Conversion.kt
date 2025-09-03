package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.fermacompose2.R


fun Double.convertSize(from: String, to: String): Double {

    val result = when (from) {
        "мм." -> when (to) {
            "мм." -> this
            "см." -> (this / 10)
            "м." -> (this / 1000)
            else -> this
        }

        "см." -> when (to) {
            "мм." -> this * 10
            "см." -> this
            "м." -> this / 100
            else -> this
        }

        "м." -> when (to) {
            "мм." -> this * 1000
            "см." -> this * 100
            "м." -> this
            else -> this
        }

        else -> this
    }
    return result
}

fun Double.convertWeight(from: String, to: String): Double {

    val result = when (from) {
        "г." -> when (to) {
            "г." -> this
            "кг." -> (this / 1000)
            "тн." -> (this / 1000000)
            else -> this
        }

        "кг." -> when (to) {
            "г." -> this*1000
            "кг." -> this
            "тн." -> (this / 1000)
            else -> this
        }

        "тн." -> when (to) {
            "г." -> (this * 1000000)
            "кг." -> (this * 1000)
            "тн." -> this
            else -> this
        }

        else -> this
    }
    return result
}


fun Double.convertVolume(from: String, to: String): Double {

    val result = when (from) {
        "м3." -> when (to) {
            "м3." -> this
            "л." -> this * 1000
            "мл." -> this * 1000000
            else -> this
        }

        "л." -> when (to) {
            "м3." -> this * 0.001
            "л." -> this
            "мл." -> this * 1000
            else -> this
        }

        "мл." -> when (to) {
            "м3." -> this * 0.000001
            "л." -> this * 0.001
            "мл." -> this
            else -> this
        }

        else -> this
    }
    return result
}

fun Category.toDrawRes() : Int{
    return when(this) {
        Category.ADD ->  R.drawable.baseline_add_circle_outline_24
        Category.EXPENSES -> R.drawable.baseline_add_shopping_cart_24
        Category.SALE -> R.drawable.baseline_add_card_24
    }
}

fun Category.toResInt(): Int{
    return when (this) {
        Category.ADD -> R.string.add_screen_title
        Category.EXPENSES -> R.string.expenses_screen_title
        Category.SALE  -> R.string.sale_screen_title
    }
}