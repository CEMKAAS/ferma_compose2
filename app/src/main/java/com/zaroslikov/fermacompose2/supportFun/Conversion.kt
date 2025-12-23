package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.dto.shared.DomainTitleCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainSettings

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

fun Double.conversation22(suffix: Suffix, baseSuffix: Suffix, settings: DomainSettings): Double {
    return when (baseSuffix) {
        Suffix.GRAM, Suffix.KILOGRAM, Suffix.TONS ->
            this.convertWeight(suffix, settings.weightSuffix)

        Suffix.MILLILITRES, Suffix.LITERS, Suffix.CUBIC_METERS ->
            this.convertVolume(suffix, settings.volumeSuffix)

        Suffix.MILLIMETERS, Suffix.CENTIMETERS, Suffix.METERS ->
            this.convertSize(suffix, settings.linearSuffix)

        Suffix.PIECES, Suffix.UNITS, Suffix.HEADS -> this

        else -> 0.0
    }
}

fun Double.conversation3(suffix: Suffix, settings: DomainSettings): Double {
    return when (suffix) {
        Suffix.GRAM, Suffix.KILOGRAM, Suffix.TONS ->
            this.convertWeight(suffix, settings.weightSuffix)

        Suffix.MILLILITRES, Suffix.LITERS, Suffix.CUBIC_METERS ->
            this.convertVolume(suffix, settings.volumeSuffix)

        Suffix.MILLIMETERS, Suffix.CENTIMETERS, Suffix.METERS ->
            this.convertSize(suffix, settings.linearSuffix)

        Suffix.PIECES, Suffix.UNITS, Suffix.HEADS -> this

        else -> 0.0
    }
}

fun Suffix.conversation4(settings: DomainSettings): Suffix {
    return when (this) {
        Suffix.GRAM, Suffix.KILOGRAM, Suffix.TONS ->
            settings.weightSuffix

        Suffix.MILLILITRES, Suffix.LITERS, Suffix.CUBIC_METERS ->
            settings.volumeSuffix

        Suffix.MILLIMETERS, Suffix.CENTIMETERS, Suffix.METERS ->
            settings.linearSuffix

        Suffix.PIECES, Suffix.UNITS, Suffix.HEADS -> this

        else -> this
    }
}

fun resolveSuffix(
    items: List<DomainTitleCountSuffix>,
    settings: DomainSettings
): Suffix =
    when (items.first().suffix) {
        Suffix.GRAM, Suffix.KILOGRAM, Suffix.TONS -> settings.weightSuffix
        Suffix.MILLILITRES, Suffix.LITERS, Suffix.CUBIC_METERS -> settings.volumeSuffix
        Suffix.MILLIMETERS, Suffix.CENTIMETERS, Suffix.METERS -> settings.linearSuffix
        else -> items.first().suffix
    }

fun Double.convertSize(from: Suffix, to: Suffix): Double {
    val result = when (from) {
        Suffix.MILLIMETERS -> when (to) {
            Suffix.MILLIMETERS -> this
            Suffix.CENTIMETERS -> (this / 10)
            Suffix.METERS -> (this / 1000)
            else -> 0.0
        }

        Suffix.CENTIMETERS -> when (to) {
            Suffix.MILLIMETERS ->
                this * 10

            Suffix.CENTIMETERS -> this
            Suffix.METERS -> this / 100
            else -> 0.0
        }

        Suffix.METERS -> when (to) {
            Suffix.MILLIMETERS -> this * 1000
            Suffix.CENTIMETERS -> this * 100
            Suffix.METERS -> this
            else -> 0.0
        }

        else -> 0.0
    }
    return result
}

fun Double.convertWeight(from: Suffix, to: Suffix): Double {

    val result = when (from) {
        Suffix.GRAM -> when (to) {
            Suffix.GRAM -> this
            Suffix.KILOGRAM -> (this / 1000)
            Suffix.TONS -> (this / 1000000)
            else -> 0.0
        }

        Suffix.KILOGRAM -> when (to) {
            Suffix.GRAM -> this * 1000
            Suffix.KILOGRAM -> this
            Suffix.TONS -> (this / 1000)
            else -> 0.0
        }

        Suffix.TONS -> when (to) {
            Suffix.GRAM -> (this * 1000000)
            Suffix.KILOGRAM -> (this * 1000)
            Suffix.TONS -> this
            else -> 0.0
        }

        else -> 0.0
    }
    return result
}


fun Double.convertVolume(from: Suffix, to: Suffix): Double {

    val result = when (from) {
        Suffix.CUBIC_METERS -> when (to) {
            Suffix.CUBIC_METERS -> this
            Suffix.LITERS -> this * 1000
            Suffix.MILLILITRES -> this * 1000000
            else -> 0.0
        }

        Suffix.LITERS -> when (to) {
            Suffix.CUBIC_METERS -> this * 0.001
            Suffix.LITERS -> this
            Suffix.MILLILITRES -> this * 1000
            else ->0.0
        }

        Suffix.MILLILITRES -> when (to) {
            Suffix.CUBIC_METERS -> this * 0.000001
            Suffix.LITERS -> this * 0.001
            Suffix.MILLILITRES -> this
            else -> 0.0
        }

        else -> 0.0
    }
    return result
}



