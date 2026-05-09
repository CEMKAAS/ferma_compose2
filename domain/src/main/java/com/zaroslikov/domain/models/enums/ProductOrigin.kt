package com.zaroslikov.domain.models.enums

enum class ProductOrigin(val code: Int) {
    ADD(0),
    EXPENSES(1),
    SALE(2);

    companion object {
        fun fromCode(code: Int): ProductOrigin =
            ProductOrigin.entries.first { it.code == code }
    }
}