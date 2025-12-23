package com.zaroslikov.domain.models.enums

enum class Suffix(val code: Int) {
    PIECES(1),
    HEADS(2),
    UNITS(3),
    GRAM(4),
    KILOGRAM(5),
    TONS(6),
    MILLILITRES(7),
    LITERS(8),
    CUBIC_METERS(9),
    MILLIMETERS(10),
    CENTIMETERS(11),
    METERS(12),
    RUBLE(13),
    TENGE(14),
    DOLLAR(15),
    EURO(16),
    GRAM_DAY(17),
    KILOGRAM_DAY(18),
    TONS_DAY(19);

    companion object {
        fun fromCode(code: Int): Suffix = entries.first { it.code == code }
    }
}
