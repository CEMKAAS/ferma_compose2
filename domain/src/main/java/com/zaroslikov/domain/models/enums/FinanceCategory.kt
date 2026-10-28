package com.zaroslikov.domain.models.enums

enum class FinanceCategory {
    SALE,
    EXPENSES,
    OWN_NEED,
    SCRAP,
    PROFIT,
    /** Сырьё, ушедшее в переработку (только в анализе продукции). */
    RECYCLING
}
