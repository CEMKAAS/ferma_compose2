package com.zaroslikov.crm.data.room.database

import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.crm.data.room.repository.CrmBackupRepositoryImpl
import com.zaroslikov.crm.data.room.repository.CrmClientRepositoryImpl
import com.zaroslikov.crm.di.CrmDatabaseModule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrmMigrationTest {
    @Test
    fun migratesVersion1AndPreservesClientData() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().context
        val name = "crm_database"
        context.deleteDatabase(name)
        try {
            val path = context.getDatabasePath(name)
            path.parentFile?.mkdirs()
            SQLiteDatabase.openOrCreateDatabase(path, null).use(::createVersion1)

            val database = CrmDatabaseModule.provideCrmDatabase(context)
            try {
                val backupRepository = CrmBackupRepositoryImpl(database)
                val oldBackup = backupRepository.export()
                val client = oldBackup.clients.single()
                assertEquals("Имя клиента", client.name)
                assertEquals("Телефон", oldBackup.contacts.single().value)
                assertEquals("Звонок", oldBackup.interactions.single().note)
                assertEquals("Позвонить", oldBackup.tasks.single().title)
                assertNull(client.birthDate)

                val clientRepository = CrmClientRepositoryImpl(database, database.crmClientDao())
                clientRepository.updateClient(client.copy(birthDate = "2000-02-29"))
                assertEquals("2000-02-29", clientRepository.getClients(7).first().single().birthDate)
                clientRepository.setFavorite(client.id, true)
                assertEquals("2000-02-29", clientRepository.getClient(client.id).first()?.birthDate)
                val newBackup = backupRepository.export()
                backupRepository.import(newBackup, setOf(7))
                assertEquals(newBackup, backupRepository.export())
                clientRepository.updateClient(client.copy(birthDate = null))
                assertNull(clientRepository.getClient(1).first()?.birthDate)
            } finally {
                database.close()
            }
        } finally {
            context.deleteDatabase(name)
        }
    }

    // Схема, сгенерированная Room для CRM версии 1, до добавления birth_date.
    private fun createVersion1(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE crm_client_table (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                idPT INTEGER NOT NULL, name TEXT NOT NULL, sales_key TEXT NOT NULL, source TEXT NOT NULL,
                is_vip INTEGER NOT NULL DEFAULT 0, note TEXT NOT NULL DEFAULT '',
                is_favorite INTEGER NOT NULL DEFAULT 0, created_date TEXT NOT NULL,
                is_archived INTEGER NOT NULL DEFAULT 0)"""
        )
        db.execSQL("CREATE INDEX index_crm_client_table_idPT ON crm_client_table (idPT)")
        db.execSQL("CREATE UNIQUE INDEX index_crm_client_table_idPT_sales_key ON crm_client_table (idPT, sales_key)")
        db.execSQL(
            """CREATE TABLE crm_contact_table (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                client_id INTEGER NOT NULL, type TEXT NOT NULL, value TEXT NOT NULL,
                is_primary INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(client_id) REFERENCES crm_client_table(id) ON UPDATE NO ACTION ON DELETE CASCADE)"""
        )
        db.execSQL("CREATE INDEX index_crm_contact_table_client_id ON crm_contact_table (client_id)")
        db.execSQL(
            """CREATE TABLE crm_interaction_table (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                client_id INTEGER NOT NULL, type TEXT NOT NULL, date TEXT NOT NULL, note TEXT NOT NULL,
                FOREIGN KEY(client_id) REFERENCES crm_client_table(id) ON UPDATE NO ACTION ON DELETE CASCADE)"""
        )
        db.execSQL("CREATE INDEX index_crm_interaction_table_client_id ON crm_interaction_table (client_id)")
        db.execSQL(
            """CREATE TABLE crm_task_table (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                idPT INTEGER NOT NULL, client_id INTEGER, title TEXT NOT NULL, due_date TEXT NOT NULL,
                is_done INTEGER NOT NULL DEFAULT 0, note TEXT NOT NULL DEFAULT '',
                FOREIGN KEY(client_id) REFERENCES crm_client_table(id) ON UPDATE NO ACTION ON DELETE SET NULL)"""
        )
        db.execSQL("CREATE INDEX index_crm_task_table_idPT ON crm_task_table (idPT)")
        db.execSQL("CREATE INDEX index_crm_task_table_client_id ON crm_task_table (client_id)")
        db.execSQL("CREATE TABLE room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT)")
        db.execSQL("INSERT INTO room_master_table (id, identity_hash) VALUES (42, 'b055b8536d2b3e4916a160efb84d601f')")
        db.execSQL(
            """INSERT INTO crm_client_table (id, idPT, name, sales_key, source, created_date)
                VALUES (1, 7, 'Имя клиента', 'Ключ продаж', 'manual', '2025-01-01')"""
        )
        db.execSQL("INSERT INTO crm_contact_table (client_id, type, value) VALUES (1, 'phone', 'Телефон')")
        db.execSQL(
            """INSERT INTO crm_interaction_table (client_id, type, date, note)
                VALUES (1, 'call', '2025-01-03', 'Звонок')"""
        )
        db.execSQL(
            """INSERT INTO crm_task_table (idPT, client_id, title, due_date)
                VALUES (7, 1, 'Позвонить', '2025-01-05')"""
        )
        db.version = 1
    }
}
