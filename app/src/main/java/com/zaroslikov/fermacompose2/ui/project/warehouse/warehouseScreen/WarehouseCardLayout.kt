package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import com.zaroslikov.domain.models.enums.WarehouseCard

internal data class WarehouseCardColumns(
    val left: List<WarehouseCard> = emptyList(),
    val right: List<WarehouseCard> = emptyList(),
) {
    operator fun get(column: Int): List<WarehouseCard> = when (column) {
        0 -> left
        1 -> right
        else -> error("Неизвестная колонка: $column")
    }

    fun replacing(column: Int, cards: List<WarehouseCard>): WarehouseCardColumns =
        if (column == 0) copy(left = cards) else copy(right = cards)

    fun take(leftIndex: Int, rightIndex: Int): WarehouseCardColumns =
        WarehouseCardColumns(left.take(leftIndex), right.take(rightIndex))

    fun drop(leftIndex: Int, rightIndex: Int): WarehouseCardColumns =
        WarehouseCardColumns(left.drop(leftIndex), right.drop(rightIndex))

    operator fun plus(other: WarehouseCardColumns): WarehouseCardColumns =
        WarehouseCardColumns(left + other.left, right + other.right)

    fun inReadingOrder(): List<WarehouseCard> = buildList {
        for (index in 0 until maxOf(left.size, right.size)) {
            left.getOrNull(index)?.let { add(it) }
            right.getOrNull(index)?.let { add(it) }
        }
    }
}

internal sealed interface WarehouseCardDrop {
    // Индексы указывают на позиции вставки до удаления исходной карточки.
    data class Column(
        val section: Int,
        val column: Int,
        val index: Int,
        val otherIndex: Int = index,
    ) : WarehouseCardDrop

    data class Calendar(val below: Boolean) : WarehouseCardDrop
}

@ConsistentCopyVisibility
internal data class WarehouseCardLayout private constructor(
    val sections: List<WarehouseCardColumns>,
) {
    val hasCalendar: Boolean get() = sections.size == 2

    val cards: List<WarehouseCard>
        get() = if (hasCalendar) {
            sections[0].inReadingOrder() + WarehouseCard.CALENDAR + sections[1].inReadingOrder()
        } else {
            sections.single().inReadingOrder()
        }

    fun toCodes(): List<String> = buildList {
        addAll(cards.map(WarehouseCard::code))
        add(COLUMNS_MARKER)
        for (section in sections) {
            addAll(section.right.map { RIGHT_PREFIX + it.code })
        }
    }

    fun hide(card: WarehouseCard): WarehouseCardLayout {
        if (card == WarehouseCard.CALENDAR) {
            return if (hasCalendar) WarehouseCardLayout(listOf(sections[0] + sections[1])) else this
        }
        val source = locationOf(card) ?: return this
        return editColumn(source) { it.removeAt(source.index) }
    }

    fun restore(card: WarehouseCard): WarehouseCardLayout {
        if (card in cards) return this
        if (card == WarehouseCard.CALENDAR) {
            return WarehouseCardLayout(sections + WarehouseCardColumns())
        }
        val last = sections.last()
        val column = if (last.left.size <= last.right.size) 0 else 1
        val target = WarehouseCardDrop.Column(sections.lastIndex, column, last[column].size)
        return editColumn(target) { it.add(card) }
    }

    fun move(card: WarehouseCard, target: WarehouseCardDrop): WarehouseCardLayout {
        if (card == WarehouseCard.CALENDAR) {
            return if (target is WarehouseCardDrop.Column && hasCalendar) moveCalendar(target) else this
        }
        val source = locationOf(card) ?: return this
        val destination = when (target) {
            is WarehouseCardDrop.Column -> target
            is WarehouseCardDrop.Calendar -> {
                if (!hasCalendar) return this
                val section = if (target.below) 1 else 0
                val index = if (target.below) 0 else sections[section][source.column].size
                WarehouseCardDrop.Column(section, source.column, index)
            }
        }
        val columns = sections.getOrNull(destination.section) ?: return this
        if (destination.column !in 0..1 || destination.index !in 0..columns[destination.column].size) return this
        val sameColumn = destination.section == source.section && destination.column == source.column
        val index = if (sameColumn && destination.index > source.index) destination.index - 1 else destination.index
        if (sameColumn && index == source.index) return this
        return hide(card).editColumn(destination) { it.add(index, card) }
    }

    fun adjacentDrop(card: WarehouseCard, direction: Int): WarehouseCardDrop? {
        if (direction != -1 && direction != 1) return null
        if (card == WarehouseCard.CALENDAR) {
            if (!hasCalendar) return null
            val section = if (direction < 0) 0 else 1
            val columns = sections[section]
            if (columns.left.isEmpty() && columns.right.isEmpty()) return null
            val leftIndex = if (direction < 0) (columns.left.size - 1).coerceAtLeast(0) else 1
            val rightIndex = if (direction < 0) (columns.right.size - 1).coerceAtLeast(0) else 1
            return WarehouseCardDrop.Column(section, 0, leftIndex, rightIndex)
        }
        val source = locationOf(card) ?: return null
        val size = sections[source.section][source.column].size
        if (source.index + direction in 0 until size) {
            val index = if (direction < 0) source.index - 1 else source.index + 2
            return WarehouseCardDrop.Column(source.section, source.column, index)
        }
        return when {
            hasCalendar && source.section == 0 && direction > 0 -> WarehouseCardDrop.Calendar(below = true)
            hasCalendar && source.section == 1 && direction < 0 -> WarehouseCardDrop.Calendar(below = false)
            else -> null
        }
    }

    private fun moveCalendar(target: WarehouseCardDrop.Column): WarehouseCardLayout {
        val columns = sections.getOrNull(target.section) ?: return this
        if (target.column !in 0..1) return this
        val leftIndex = (if (target.column == 0) target.index else target.otherIndex).coerceIn(0, columns.left.size)
        val rightIndex = (if (target.column == 1) target.index else target.otherIndex).coerceIn(0, columns.right.size)
        val before = columns.take(leftIndex, rightIndex)
        val after = columns.drop(leftIndex, rightIndex)
        return if (target.section == 0) {
            WarehouseCardLayout(listOf(before, after + sections[1]))
        } else {
            WarehouseCardLayout(listOf(sections[0] + before, after))
        }
    }

    private fun locationOf(card: WarehouseCard): WarehouseCardDrop.Column? {
        for ((section, columns) in sections.withIndex()) {
            for (column in 0..1) {
                val index = columns[column].indexOf(card)
                if (index >= 0) return WarehouseCardDrop.Column(section, column, index)
            }
        }
        return null
    }

    private fun editColumn(
        location: WarehouseCardDrop.Column,
        edit: (MutableList<WarehouseCard>) -> Unit,
    ): WarehouseCardLayout {
        val updated = sections.toMutableList()
        val columns = updated[location.section]
        val cards = columns[location.column].toMutableList().also(edit)
        updated[location.section] = columns.replacing(location.column, cards)
        return WarehouseCardLayout(updated)
    }

    companion object {
        // Коды карточек сохраняются отдельно, чтобы прежние версии могли прочитать их порядок.
        private const val COLUMNS_MARKER = "@warehouse-columns"
        private const val RIGHT_PREFIX = "@warehouse-right:"

        fun fromCodes(codes: List<String>?): WarehouseCardLayout {
            val cards = WarehouseCard.fromCodes(codes)
            val hasColumns = codes?.contains(COLUMNS_MARKER) == true
            val rightCodes = codes.orEmpty().filter { it.startsWith(RIGHT_PREFIX) }
                .map { it.removePrefix(RIGHT_PREFIX) }.toSet()

            fun columns(section: List<WarehouseCard>): WarehouseCardColumns {
                val (left, right) = if (hasColumns) {
                    section.partition { it.code !in rightCodes }
                } else {
                    section.filterIndexed { index, _ -> index % 2 == 0 } to
                        section.filterIndexed { index, _ -> index % 2 == 1 }
                }
                return WarehouseCardColumns(left, right)
            }

            val calendar = cards.indexOf(WarehouseCard.CALENDAR)
            return if (calendar < 0) {
                WarehouseCardLayout(listOf(columns(cards)))
            } else {
                WarehouseCardLayout(listOf(columns(cards.take(calendar)), columns(cards.drop(calendar + 1))))
            }
        }
    }
}
