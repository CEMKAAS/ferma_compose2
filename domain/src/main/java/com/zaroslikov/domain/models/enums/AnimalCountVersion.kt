package com.zaroslikov.domain.models.enums

enum class AnimalCountVersion(val code: Int) {
    SALE(0),
    EXPENSES(1),
    KILL(2),
    WRITE_OFF(3),
    ADD(4),
    INCUBATOR(5);

    companion object {
        fun fromCode(code: Int): AnimalCountVersion =
            AnimalCountVersion.entries.first { it.code == code }
    }
}