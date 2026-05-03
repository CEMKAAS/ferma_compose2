package com.zaroslikov.domain.models.enums

enum class TypeEgg(val code: Int) {
    CHICKENS(1),
    GEESE(2),
    QUAILS(3),
    TURKEYS(4),
    DUCKS(5);

    companion object {
        fun fromCode(code: Int): TypeEgg = TypeEgg.entries.first { it.code == code }
    }
}