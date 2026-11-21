package com.zaroslikov.crm

import com.zaroslikov.crm.domain.model.DomainCrmBackup
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CrmBirthdayBackupTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun restoresOldBackupWithoutBirthday() {
        val backup = json.decodeFromString<DomainCrmBackup>(backupJson())

        assertEquals("Клиент", backup.clients.single().name)
        assertNull(birthdayInExport(backup))
    }

    @Test
    fun preservesBirthdayWhenRestoringAndExporting() {
        val backup = json.decodeFromString<DomainCrmBackup>(backupJson(",\"birthDate\":\"2000-02-29\""))

        assertEquals("2000-02-29", birthdayInExport(backup))
    }

    private fun birthdayInExport(backup: DomainCrmBackup): String? =
        json.parseToJsonElement(json.encodeToString(backup)).jsonObject
            .getValue("clients").jsonArray.single().jsonObject["birthDate"]?.jsonPrimitive?.content

    private fun backupJson(extraFields: String = ""): String = """
        {"clients": [{"id": 3, "idPT": 7, "name": "Клиент", "salesKey": "Клиент",
            "createdDate": "2025-01-01"$extraFields}]}
    """.trimIndent()
}
