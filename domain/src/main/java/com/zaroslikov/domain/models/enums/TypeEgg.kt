package com.zaroslikov.domain.models.enums

enum class TypeEgg(val code: Int) {
    CHICKENS(0),
    GEESE(1),
    QUAILS(2),
    TURKEYS(4),
    DUCKS(3);

    companion object {
        fun fromCode(code: Int): TypeEgg = TypeEgg.entries.first { it.code == code }
    }
}