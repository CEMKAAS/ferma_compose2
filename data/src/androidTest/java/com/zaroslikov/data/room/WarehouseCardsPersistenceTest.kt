package com.zaroslikov.data.room

import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.data.room.database.AppDatabase
import com.zaroslikov.data.room.database.migration.MIGRATION_18_19
import com.zaroslikov.data.room.repository.SettingsRepositoryImpl
import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.WarehouseCard
import com.zaroslikov.domain.models.table.DomainSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WarehouseCardsPersistenceTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun migrationKeepsExistingSettingsAndUsesDefaultOrder() {
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .callback(object : SupportSQLiteOpenHelper.Callback(18) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE settings_table (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            currency_suffix INTEGER NOT NULL,
                            weight_suffix INTEGER NOT NULL,
                            volume_suffix INTEGER NOT NULL,
                            linear_suffix INTEGER NOT NULL,
                            idPT INTEGER NOT NULL
                        )
                        """.trimIndent()
                    )
                    db.execSQL("INSERT INTO settings_table VALUES (1, 13, 5, 8, 11, 42)")
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
            })
            .build()

        FrameworkSQLiteOpenHelperFactory().create(config).use { helper ->
            val db = helper.writableDatabase
            MIGRATION_18_19.migrate(db)

            db.query("SELECT currency_suffix, weight_suffix, warehouse_card_codes FROM settings_table WHERE idPT = 42")
                .use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals(Suffix.RUBLE.code, cursor.getInt(0))
                    assertEquals(Suffix.KILOGRAM.code, cursor.getInt(1))
                    assertNull(cursor.getString(2))
                    assertEquals(WarehouseCard.defaultOrder, WarehouseCard.fromCodes(null))
                }
        }
    }

    @Test
    fun layoutsAreIndependentAndSurviveSettingsChangesAndReopening() = runBlocking {
        val databaseName = "warehouse-cards-persistence-test"
        context.deleteDatabase(databaseName)
        try {
            val customOrder = listOf(WarehouseCard.NOTES, WarehouseCard.CALENDAR)
            val customCodes = customOrder.map { it.code }
            val exported = withDatabase(databaseName) { db ->
                val repository = SettingsRepositoryImpl(db.settingsDao())
                db.projectDao().insertProject(project(1))
                db.projectDao().insertProject(project(2))
                repository.insertSettings(DomainSettings(idPT = 1))
                repository.insertSettings(DomainSettings(idPT = 2))

                val originalSettings = repository.getSettings(1).first()
                repository.updateWarehouseCards(1, customOrder)
                assertNull(repository.getSettings(2).first().warehouseCardCodes)
                repository.updateWarehouseCards(2, emptyList())
                repository.updateSettings(originalSettings.copy(currencySuffix = Suffix.EURO, weightSuffix = Suffix.GRAM))

                val settings = repository.getAllSettingsTableForExport().first().sortedBy { it.idPT }
                assertEquals(
                    originalSettings.copy(currencySuffix = Suffix.EURO, weightSuffix = Suffix.GRAM, warehouseCardCodes = customCodes),
                    settings[0],
                )
                assertEquals(emptyList<String>(), settings[1].warehouseCardCodes)
                settings
            }

            withDatabase(databaseName) { db ->
                val repository = SettingsRepositoryImpl(db.settingsDao())
                assertEquals(exported, repository.getAllSettingsTableForExport().first().sortedBy { it.idPT })

                repository.clearAndInsertSettingsTableForImport(exported.map { it.copy(warehouseCardCodes = null) })
                assertNull(repository.getSettings(1).first().warehouseCardCodes)
                repository.clearAndInsertSettingsTableForImport(exported)

                assertEquals(customOrder, WarehouseCard.fromCodes(repository.getSettings(1).first().warehouseCardCodes))
                assertEquals(emptyList<WarehouseCard>(), WarehouseCard.fromCodes(repository.getSettings(2).first().warehouseCardCodes))
            }
        } finally {
            context.deleteDatabase(databaseName)
        }
    }

    private suspend fun <T> withDatabase(name: String, block: suspend (AppDatabase) -> T): T {
        val database = Room.databaseBuilder(context, AppDatabase::class.java, name).build()
        try {
            return block(database)
        } finally {
            database.close()
        }
    }

    private fun project(id: Long) = ProjectTable(
        id = id,
        title = "Ферма $id",
        date = "",
        dateEnd = "",
        mode = true,
        archive = false,
        imagePath = null,
        currentIcon = null,
    )
}
