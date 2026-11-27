package com.zaroslikov.domain.models.enums

enum class WarehouseCard(val code: String) {
    CALENDAR("calendar"),
    PRODUCTION("production"),
    FEED("feed"),
    GOODS("goods"),
    RECYCLING("recycling"),
    TASKS("tasks"),
    NOTES("notes");

    companion object {
        val defaultOrder: List<WarehouseCard> =
            listOf(CALENDAR, PRODUCTION, RECYCLING, GOODS, FEED, NOTES, TASKS)
        private val byCode = entries.associateBy(WarehouseCard::code)

        fun fromCodes(codes: List<String>?): List<WarehouseCard> =
            codes?.mapNotNull(byCode::get)?.distinct() ?: defaultOrder
    }
}
