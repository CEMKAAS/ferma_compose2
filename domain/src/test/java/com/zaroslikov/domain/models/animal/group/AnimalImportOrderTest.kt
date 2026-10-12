package com.zaroslikov.domain.models.animal.group

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.table.DomainAnimalHerd
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AnimalImportOrderTest {
    private fun a(id: Long, group: Boolean = false, herdId: Long? = null, idPT: Long = 0, name: String = "") =
        DomainAnimalTable(id = id, group = group, herdId = herdId, idPT = idPT, name = name)

    private fun h(id: Long, idPT: Long = 0) = DomainAnimalHerd(id = id, name = "Стадо $id", idPT = idPT)

    private fun List<DomainAnimalTable>.hid(id: Long) = first { it.id == id }.herdId

    @Test fun duplicateIdsKeepFirst() {
        val r = listOf(a(1, name = "first"), a(2), a(1, name = "second", herdId = 5))
            .normalizedForImport(listOf(h(5)))
        assertEquals(listOf(1L, 2L), r.map { it.id })
        assertEquals("first", r.first { it.id == 1L }.name)
        assertNull(r.hid(1))
    }

    @Test fun validHerdKept() =
        assertEquals(5L, listOf(a(1, herdId = 5, idPT = 10)).normalizedForImport(listOf(h(5, idPT = 10))).hid(1))

    @Test fun impersonalGroupInHerdKept() =
        assertEquals(5L, listOf(a(1, group = true, herdId = 5)).normalizedForImport(listOf(h(5))).hid(1))

    @Test fun herdInOtherProjectReset() =
        assertNull(listOf(a(1, herdId = 5, idPT = 10)).normalizedForImport(listOf(h(5, idPT = 20))).hid(1))

    @Test fun danglingHerdReset() = assertNull(listOf(a(1, herdId = 99)).normalizedForImport(emptyList()).hid(1))

    @Test fun oldBackupUnchanged() {
        val list = listOf(a(1), a(2, group = true), a(3))
        assertEquals(list, list.normalizedForImport(emptyList()))
    }

    @Test fun duplicateHerdsKeepFirst() {
        val r = listOf(h(1).copy(name = "first"), h(1).copy(name = "second"), h(2)).normalizedHerdsForImport(setOf(0L))
        assertEquals(listOf("first", "Стадо 2"), r.map { it.name })
    }

    @Test fun herdOfMissingProjectDropped() =
        assertEquals(listOf(1L), listOf(h(1, idPT = 10), h(2, idPT = 20)).normalizedHerdsForImport(setOf(10L)).map { it.id })

    // region Родословная

    @Test fun validPedigreeKept() {
        val child = a(3).copy(motherId = 1, fatherId = 2, motherName = "Маруся", fatherName = "Борис")
        val r = listOf(a(1), a(2), child).normalizedForImport(emptyList()).first { it.id == 3L }
        assertEquals(child, r)
    }

    @Test fun danglingParentsResetNamesKept() {
        val child = a(3).copy(motherId = 98, fatherId = 99, motherName = "Маруся", fatherName = "Чужой")
        val r = listOf(child).normalizedForImport(emptyList()).single()
        assertNull(r.motherId)
        assertNull(r.fatherId)
        assertEquals("Маруся", r.motherName)
        assertEquals("Чужой", r.fatherName)
    }

    @Test fun selfParentReset() {
        val r = listOf(a(1).copy(motherId = 1, fatherId = 1, fatherName = "Сам")).normalizedForImport(emptyList()).single()
        assertNull(r.motherId)
        assertNull(r.fatherId)
        assertEquals("Сам", r.fatherName)
    }

    // endregion
}
