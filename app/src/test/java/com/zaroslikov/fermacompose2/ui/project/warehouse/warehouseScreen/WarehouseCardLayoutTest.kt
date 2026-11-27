package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import com.zaroslikov.domain.models.enums.WarehouseCard
import com.zaroslikov.domain.models.enums.WarehouseCard.CALENDAR
import com.zaroslikov.domain.models.enums.WarehouseCard.FEED
import com.zaroslikov.domain.models.enums.WarehouseCard.GOODS
import com.zaroslikov.domain.models.enums.WarehouseCard.NOTES
import com.zaroslikov.domain.models.enums.WarehouseCard.PRODUCTION
import com.zaroslikov.domain.models.enums.WarehouseCard.RECYCLING
import com.zaroslikov.domain.models.enums.WarehouseCard.TASKS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WarehouseCardLayoutTest {
    @Test
    fun defaultSettingsKeepOriginalColumns() {
        val layout = WarehouseCardLayout.fromCodes(null)
        assertTrue(layout.hasCalendar)
        assertEquals(
            listOf(
                WarehouseCardColumns(),
                WarehouseCardColumns(listOf(PRODUCTION, GOODS, NOTES), listOf(RECYCLING, FEED, TASKS)),
            ),
            layout.sections,
        )
        assertEquals(WarehouseCard.defaultOrder, layout.cards)
    }

    @Test
    fun legacySettingsAlternateColumnsOnBothSidesOfCalendarAndIgnoreDuplicates() {
        val layout = legacy(FEED, CALENDAR, GOODS, NOTES, TASKS, NOTES)
        assertEquals(
            listOf(WarehouseCardColumns(listOf(FEED)), WarehouseCardColumns(listOf(GOODS, TASKS), listOf(NOTES))),
            layout.sections,
        )
    }

    @Test
    fun movingAcrossColumnsDoesNotChangeNeighborsColumnOrRelativeOrder() {
        val layout = legacy(FEED, PRODUCTION, GOODS, TASKS, RECYCLING, NOTES)
        val moved = layout.move(TASKS, WarehouseCardDrop.Column(section = 0, column = 0, index = 1))
        assertEquals(WarehouseCardColumns(listOf(FEED, TASKS, GOODS, RECYCLING), listOf(PRODUCTION, NOTES)), moved.sections.single())
        assertEquals(moved, WarehouseCardLayout.fromCodes(moved.toCodes()))
        assertEquals(moved.cards, WarehouseCard.fromCodes(moved.toCodes()))
    }

    @Test
    fun compactCardsCanStackInOneColumnAndKeepTheirPositionsAfterReload() {
        val layout = legacy(FEED, PRODUCTION, GOODS, TASKS, RECYCLING, NOTES)
        val moved = layout.move(TASKS, WarehouseCardDrop.Column(section = 0, column = 1, index = 3))
        assertEquals(WarehouseCardColumns(listOf(FEED, GOODS, RECYCLING), listOf(PRODUCTION, NOTES, TASKS)), moved.sections.single())
        assertEquals(moved, WarehouseCardLayout.fromCodes(moved.toCodes()))
    }

    @Test
    fun calendarUsesAnIndependentInsertionIndexForEachColumn() {
        val layout = legacy(CALENDAR, FEED, PRODUCTION, GOODS, TASKS, RECYCLING, NOTES)
        val moved = layout.move(CALENDAR, WarehouseCardDrop.Column(section = 1, column = 1, index = 2, otherIndex = 1))
        assertEquals(
            listOf(
                WarehouseCardColumns(listOf(FEED), listOf(PRODUCTION, TASKS)),
                WarehouseCardColumns(listOf(GOODS, RECYCLING), listOf(NOTES)),
            ),
            moved.sections,
        )
        assertEquals(moved, WarehouseCardLayout.fromCodes(moved.toCodes()))
        assertEquals(layout, moved.move(CALENDAR, WarehouseCardDrop.Column(section = 0, column = 0, index = 0)))
    }

    @Test
    fun droppingOnCalendarCrossesTheDividerInTheSameColumn() {
        val layout = legacy(PRODUCTION, FEED, CALENDAR, GOODS, NOTES)
        val moved = layout.move(NOTES, WarehouseCardDrop.Calendar(below = false))
        assertEquals(
            listOf(WarehouseCardColumns(listOf(PRODUCTION), listOf(FEED, NOTES)), WarehouseCardColumns(listOf(GOODS))),
            moved.sections,
        )
    }

    @Test
    fun hidingCalendarJoinsColumnsAndRestoringAppendsCalendar() {
        val layout = legacy(FEED, PRODUCTION, CALENDAR, GOODS, TASKS, RECYCLING, NOTES)
        val hidden = layout.hide(CALENDAR)
        assertFalse(hidden.hasCalendar)
        assertEquals(WarehouseCardColumns(listOf(FEED, GOODS, RECYCLING), listOf(PRODUCTION, TASKS, NOTES)), hidden.sections.single())
        val restored = hidden.restore(CALENDAR)
        assertEquals(listOf(hidden.sections.single(), WarehouseCardColumns()), restored.sections)
        assertEquals(CALENDAR, restored.cards.last())
    }

    @Test
    fun emptyColumnAndCardVisibilityArePreservedOnReload() {
        val layout = legacy(PRODUCTION, TASKS).hide(PRODUCTION)
        assertEquals(WarehouseCardColumns(right = listOf(TASKS)), layout.sections.single())
        assertEquals(layout, WarehouseCardLayout.fromCodes(layout.toCodes()))
        assertEquals(WarehouseCardLayout.fromCodes(emptyList()), layout.hide(TASKS))

        val leftOnly = legacy(PRODUCTION, TASKS).move(TASKS, WarehouseCardDrop.Column(0, 0, 1))
        assertEquals(WarehouseCardColumns(listOf(PRODUCTION, TASKS)), leftOnly.sections.single())
        assertEquals(leftOnly, WarehouseCardLayout.fromCodes(leftOnly.toCodes()))
    }

    @Test
    fun adjacentActionsUseColumnOrderAndCanCrossCalendar() {
        val layout = legacy(FEED, PRODUCTION, CALENDAR, GOODS, TASKS, RECYCLING, NOTES)
        assertEquals(WarehouseCardDrop.Calendar(below = false), layout.adjacentDrop(TASKS, -1))
        assertEquals(WarehouseCardDrop.Column(section = 1, column = 1, index = 2), layout.adjacentDrop(TASKS, 1))
        assertNull(layout.adjacentDrop(NOTES, 1))
        assertEquals(layout, layout.move(FEED, WarehouseCardDrop.Column(section = 0, column = 0, index = 1)))
        assertEquals(layout, layout.move(FEED, WarehouseCardDrop.Column(section = 3, column = 0, index = 0)))
    }

    private fun legacy(vararg cards: WarehouseCard): WarehouseCardLayout =
        WarehouseCardLayout.fromCodes(cards.map(WarehouseCard::code))
}
