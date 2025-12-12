package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.enums.Suffix

fun Double.conversation(suffix: Suffix, baseSuffix: Suffix): Double {
    return when (baseSuffix) {
        Suffix.GRAM, Suffix.KILOGRAM, Suffix.TONS -> this.convertWeight(suffix, baseSuffix)
        Suffix.MILLILITRES, Suffix.LITERS, Suffix.CUBIC_METERS -> this.convertVolume(
            suffix,
            baseSuffix
        )

        Suffix.MILLIMETERS, Suffix.CENTIMETERS, Suffix.METERS -> this.convertSize(
            suffix,
            baseSuffix
        )

        else -> 0.0
    }
}

fun Double.conversation2(suffix: Suffix, baseSuffix: Suffix, settingsSuffix: Suffix): Double {
    return when (baseSuffix) {
        Suffix.GRAM, Suffix.KILOGRAM, Suffix.TONS -> this.convertWeight(suffix, settingsSuffix)
        Suffix.MILLILITRES, Suffix.LITERS, Suffix.CUBIC_METERS -> this.convertVolume(
            suffix,
            settingsSuffix
        )

        Suffix.MILLIMETERS, Suffix.CENTIMETERS, Suffix.METERS -> this.convertSize(
            suffix,
            settingsSuffix
        )

        Suffix.PIECES, Suffix.UNITS, Suffix.HEADS -> this

        else -> 0.0
    }
}


fun Double.convertSize(from: Suffix, to: Suffix): Double {
    val result = when (from) {
        Suffix.MILLIMETERS -> when (to) {
            Suffix.MILLIMETERS -> this
            Suffix.CENTIMETERS -> (this / 10)
            Suffix.METERS -> (this / 1000)
            else -> this
        }

        Suffix.CENTIMETERS -> when (to) {
            Suffix.MILLIMETERS ->
                this * 10

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


fun Double.convertVolume(from: Suffix, to: Suffix): Double {

    val result = when (from) {
        Suffix.CUBIC_METERS -> when (to) {
            Suffix.CUBIC_METERS -> this
            Suffix.LITERS -> this * 1000
            Suffix.MILLILITRES -> this * 1000000
            else -> this
        }

        Suffix.LITERS -> when (to) {
            Suffix.CUBIC_METERS -> this * 0.001
            Suffix.LITERS -> this
            Suffix.MILLILITRES -> this * 1000
            else -> this
        }

        Suffix.MILLILITRES -> when (to) {
            Suffix.CUBIC_METERS -> this * 0.000001
            Suffix.LITERS -> this * 0.001
            Suffix.MILLILITRES -> this
            else -> this
        }

        else -> this
    }
    return result
}



