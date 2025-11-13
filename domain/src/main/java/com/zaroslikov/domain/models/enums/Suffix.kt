package com.zaroslikov.domain.models.enums

enum class Suffix(val code: Int) {
    PIECES(1),
    HEADS(2),
    UNITS(3),
    GRAM(4),
    KILOGRAM(5),
    TONS(6),
    LITERS(7),
    CUBIC_METERS(8),
    MILLIMETERS(9),
    CENTIMETERS(10),
    METERS(11),
    RUBLE(12),
    GRAM_DAY(13),
    KILOGRAM_DAY(14),
    TONS_DAY(15);

    companion object {
        fun fromCode(code: Int): Suffix = entries.first { it.code == code }
    }
}
