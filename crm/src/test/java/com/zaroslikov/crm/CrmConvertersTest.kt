package com.zaroslikov.crm

import com.zaroslikov.crm.data.room.converter.CrmConverters
import com.zaroslikov.crm.domain.model.CrmClientSource
import com.zaroslikov.crm.domain.model.CrmContactType
import com.zaroslikov.crm.domain.model.CrmInteractionType
import com.zaroslikov.crm.domain.model.DomainCrmBackup
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmContact
import com.zaroslikov.crm.domain.model.DomainCrmInteraction
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CrmConvertersTest {

    private val converters = CrmConverters()

    @Test
    fun enums_roundTripThroughCode() {
        CrmClientSource.entries.forEach {
            assertEquals(it, converters.toClientSource(converters.fromClientSource(it)))
        }
        CrmContactType.entries.forEach {
            assertEquals(it, converters.toContactType(converters.fromContactType(it)))
        }
        CrmInteractionType.entries.forEach {
            assertEquals(it, converters.toInteractionType(converters.fromInteractionType(it)))
        }
    }

    @Test
    fun codes_areStable() {
        // Коды лежат в БД и бэкапах — менять их нельзя
        assertEquals("sales", converters.fromClientSource(CrmClientSource.SALES))
        assertEquals("manual", converters.fromClientSource(CrmClientSource.MANUAL))
        assertEquals(
            listOf("phone", "email", "telegram", "whatsapp", "vk", "address", "other"),
            CrmContactType.entries.map { converters.fromContactType(it) }
        )
        assertEquals(
            listOf("call", "meeting", "message", "note"),
            CrmInteractionType.entries.map { converters.fromInteractionType(it) }
        )
    }

    @Test
    fun unknownCodes_fallBackToSafeDefaults() {
        assertEquals(CrmClientSource.MANUAL, converters.toClientSource("???"))
        assertEquals(CrmContactType.OTHER, converters.toContactType("fax"))
        assertEquals(CrmInteractionType.NOTE, converters.toInteractionType(""))
    }

    @Test
    fun backup_serializesEnumsByCode() {
        val backup = DomainCrmBackup(
            clients = listOf(
                DomainCrmClient(
                    id = 1,
                    idPT = 7,
                    name = "Иван",
                    salesKey = "Иван",
                    source = CrmClientSource.SALES,
                    createdDate = "2026-01-01"
                )
            ),
            contacts = listOf(DomainCrmContact(clientId = 1, type = CrmContactType.WHATSAPP, value = "+7900"))
        )
        val json = Json.encodeToString(DomainCrmBackup.serializer(), backup)
        assertTrue(json.contains("\"source\":\"sales\""))
        assertTrue(json.contains("\"type\":\"whatsapp\""))
        assertEquals(backup, Json.decodeFromString(DomainCrmBackup.serializer(), json))
    }

    @Test
    fun backup_unknownEnumCodes_doNotAbortImport() {
        // Бэкап из новой версии с типами, которых эта версия не знает
        val json = """{
            "clients":[{"id":1,"idPT":7,"name":"Иван","salesKey":"Иван","source":"marketplace","createdDate":"2026-01-01"}],
            "contacts":[{"id":2,"clientId":1,"type":"signal","value":"x"}],
            "interactions":[{"id":3,"clientId":1,"type":"video","date":"2026-01-02","note":""}]
        }"""
        val backup = Json.decodeFromString(DomainCrmBackup.serializer(), json)
        assertEquals(CrmClientSource.MANUAL, backup.clients.single().source)
        assertEquals(CrmContactType.OTHER, backup.contacts.single().type)
        assertEquals(CrmInteractionType.NOTE, backup.interactions.single().type)
    }

    @Test
    fun backup_enumsRoundTripForAllCodes() {
        val backup = DomainCrmBackup(
            clients = CrmClientSource.entries.mapIndexed { i, source ->
                DomainCrmClient(id = i + 1L, idPT = 1, name = "К$i", salesKey = "К$i", source = source, createdDate = "2026-01-01")
            },
            contacts = CrmContactType.entries.map { DomainCrmContact(clientId = 1, type = it, value = it.code) },
            interactions = CrmInteractionType.entries.map {
                DomainCrmInteraction(clientId = 1, type = it, date = "2026-01-01", note = "")
            }
        )
        val json = Json.encodeToString(DomainCrmBackup.serializer(), backup)
        assertEquals(backup, Json.decodeFromString(DomainCrmBackup.serializer(), json))
    }

    @Test
    fun backup_readsEmptyObject() {
        assertEquals(DomainCrmBackup(), Json.decodeFromString(DomainCrmBackup.serializer(), "{}"))
    }
}
