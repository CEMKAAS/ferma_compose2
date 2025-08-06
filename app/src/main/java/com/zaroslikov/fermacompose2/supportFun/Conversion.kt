package com.zaroslikov.fermacompose2.supportFun


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