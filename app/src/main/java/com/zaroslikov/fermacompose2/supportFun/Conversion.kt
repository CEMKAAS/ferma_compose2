package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.R


fun Double.convertSize(from: Suffix, to: Suffix): Double {

    val result = when (from) {
        Suffix.MILLIMETERS -> when (to) {
            Suffix.MILLIMETERS -> this
            Suffix.CENTIMETERS -> (this / 10)
            Suffix.METERS -> (this / 1000)
            else -> this
        }

        Suffix.CENTIMETERS -> when (to) {
            Suffix.MILLIMETERS -> this * 10
            Suffix.CENTIMETERS -> this
            Suffix.METERS -> this / 100
            else -> this
        }

        Suffix.METERS -> when (to) {
            Suffix.MILLIMETERS -> this * 1000
            Suffix.CENTIMETERS -> this * 100
            Suffix.METERS -> this
            else -> this
        }

        else -> this
    }
    return result
}

fun Double.convertWeight(from: Suffix, to: Suffix): Double {

    val result = when (from) {
        Suffix.GRAM -> when (to) {
            Suffix.GRAM -> this
            Suffix.KILOGRAM -> (this / 1000)
            Suffix.TONS -> (this / 1000000)
            else -> this
        }

        Suffix.KILOGRAM -> when (to) {
            Suffix.GRAM -> this * 1000
            Suffix.KILOGRAM -> this
            Suffix.TONS -> (this / 1000)
            else -> this
        }

        Suffix.TONS -> when (to) {
            Suffix.GRAM -> (this * 1000000)
            Suffix.KILOGRAM -> (this * 1000)
            Suffix.TONS -> this
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



