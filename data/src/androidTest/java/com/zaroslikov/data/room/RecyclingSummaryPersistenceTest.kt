package com.zaroslikov.data.room

import androidx.room.Room
import androidx.room.withTransaction
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.data.room.database.AppDatabase
import com.zaroslikov.data.room.database.migration.MIGRATION_19_20
import com.zaroslikov.data.room.repository.RecyclingRepositoryImpl
import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainRecycling
import com.zaroslikov.domain.models.table.DomainRecyclingProduct
import com.zaroslikov.domain.models.table.DomainRecyclingSummary
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecyclingSummaryPersistenceTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun disposalKeepsReadyProductsAndResumedBatchCompletesInItsOwnCycle() = runBlocking {
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build().useDatabase { db ->
            val repository = RecyclingRepositoryImpl(db.recyclingDao())
            db.projectDao().insertProject(project(1))
            db.projectDao().insertProject(project(2))
            assertEquals(DomainRecyclingSummary(), repository.observeSummary(1).first())
            val partial = repository.insertRecycling(
                batch(1), listOf(product(1, "Готовое"), product(1, "Будущее", 2)),
                listOf(ingredient(1)), NOW
            )
            val full = repository.insertRecycling(
                batch(1), listOf(product(1, "Будущее", 2)), listOf(ingredient(1)), NOW
            )
            repository.insertRecycling(batch(2), listOf(product(2, "Другой проект", 2)), emptyList(), NOW)
            assertEquals(DomainRecyclingSummary(2, 2, 0, 0), repository.observeSummary(1).first())

            val readyAddId = repository.getRecyclingList(1).first().single { it.recycling.id == partial }
                .products.single { it.isCompleted }.addId
            assertNotNull(readyAddId)
            val disposed = repository.disposeRecycling(partial, "В утиль")
            assertEquals(1, disposed.size)
            assertTrue(repository.disposeRecycling(partial, "В утиль").isEmpty())
            assertEquals(1, repository.disposeRecycling(full, "В утиль").size)
            assertTrue(repository.disposeRecycling(full, "В утиль").isEmpty())
            assertEquals(DomainRecyclingSummary(0, 0, 0, 2), repository.observeSummary(1).first())
            val kept = repository.getRecyclingList(1).first().single()
            assertEquals(readyAddId, kept.products.single().addId)
            assertTrue(db.addDao().getAllAddTableForExport().first().any { it.id == readyAddId })
            val writeOffs = db.writeOffDao().getAllWriteOffTableForExport().first()
            assertEquals(2, writeOffs.size)
            assertTrue(writeOffs.all { it.recyclingId == null && !it.status && it.note == "В утиль" })

            repository.updateRecycling(kept.recycling, kept.products + product(1, "Новый цикл", 2), emptyList(), NOW)
            val resumed = DomainRecyclingSummary(1, 1, 0, 2)
            val completed = DomainRecyclingSummary(0, 0, 1, 2)
            val observedResumption = CompletableDeferred<Unit>()
            val observed = async(start = CoroutineStart.UNDISPATCHED) {
                withTimeout(5_000) {
                    repository.observeSummary(1)
                        .onEach { if (it == resumed) observedResumption.complete(Unit) }
                        .first { it == completed }
                }
            }
            withTimeout(5_000) { observedResumption.await() }
            repository.completeDueRecycling("2026-11-22T00:00")
            assertEquals(completed, observed.await())
            assertEquals(DomainRecyclingSummary(0, 0, 1, 0), repository.observeSummary(2).first())
            assertFalse(repository.getAllRecyclingInterruptionsForExport().first().single {
                it.recyclingId == partial
            }.endedCurrentCycle)

            val batchDeletedNormally = repository.insertRecycling(
                batch(1), listOf(product(1, "Удаляется", 2)), emptyList(), NOW
            )
            val productDeletedNormally = repository.getRecyclingList(1).first()
                .single { it.recycling.id == batchDeletedNormally }.products.single()
            repository.deleteRecyclingProduct(productDeletedNormally.id, "Обычное удаление")
            assertEquals(completed, repository.observeSummary(1).first())

            val ready = repository.getRecyclingList(1).first().single()
            repository.updateRecycling(ready.recycling, ready.products + product(1, "Ещё один цикл", 3), emptyList(), NOW)
            repository.disposeRecycling(partial, "В утиль")
            val history = repository.getAllRecyclingInterruptionsForExport().first()
            assertEquals(3, history.map { it.id }.distinct().size)
            assertEquals(2, history.count { it.recyclingId == partial })
            assertEquals(1, history.count { it.recyclingId == partial && it.endedCurrentCycle })
            assertEquals(DomainRecyclingSummary(0, 0, 0, 3), repository.observeSummary(1).first())
            repository.deleteRecycling(partial)
            assertTrue(repository.getAllRecyclingInterruptionsForExport().first().all { it.recyclingId == null })
            assertEquals(DomainRecyclingSummary(0, 0, 0, 3), repository.observeSummary(1).first())

            db.projectDao().deleteProject(project(1))
            assertTrue(repository.getAllRecyclingInterruptionsForExport().first().isEmpty())
            assertEquals(DomainRecyclingSummary(0, 0, 1, 0), repository.observeSummary(2).first())
        }
    }

    @Test
    fun migrationKeepsVersion19RowsAndBackupImportRestoresInterruptionLinks() = runBlocking {
        val databaseName = "recycling-migration-test"
        context.deleteDatabase(databaseName)
        try {
            val id = Room.databaseBuilder(context, AppDatabase::class.java, databaseName).build().useDatabase { db ->
                db.projectDao().insertProject(project(1))
                val repository = RecyclingRepositoryImpl(db.recyclingDao())
                val id = repository.insertRecycling(
                    batch(1), listOf(product(1, "Готовое"), product(1, "Будущее", 3)),
                    listOf(ingredient(1)), NOW
                )
                // В схеме 19 отсутствует только новая таблица. Остальные таблицы и данные сохраняем.
                val sqlite = db.openHelper.writableDatabase
                sqlite.execSQL("DROP TABLE recycling_interruption_table")
                sqlite.version = 19
                id
            }

            Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
                .addMigrations(MIGRATION_19_20).build().useDatabase { db ->
                    val repository = RecyclingRepositoryImpl(db.recyclingDao())
                    assertEquals(DomainRecyclingSummary(1, 1, 0, 0), repository.observeSummary(1).first())
                    val batchBeforeDisposal = repository.getRecyclingList(1).first().single()
                    assertEquals(id, batchBeforeDisposal.recycling.id)
                    assertEquals(1, batchBeforeDisposal.ingredients.size)
                    assertEquals(1, db.addDao().getAllAddTableForExport().first().size)

                    repository.disposeRecycling(id, "В утиль")
                    val (batches, products, events) = db.withTransaction {
                        Triple(
                            repository.getAllRecyclingForExport().first(),
                            repository.getAllRecyclingProductsForExport().first(),
                            repository.getAllRecyclingInterruptionsForExport().first()
                        )
                    }
                    assertEquals(id, events.single().recyclingId)
                    assertTrue(events.single().endedCurrentCycle)

                    // Старый файл восстанавливает партию и готовую продукцию без событий.
                    repository.clearAndInsertRecyclingForImport(batches, products)
                    assertEquals(DomainRecyclingSummary(0, 0, 1, 0), repository.observeSummary(1).first())
                    assertTrue(repository.getAllRecyclingInterruptionsForExport().first().isEmpty())

                    repository.clearAndInsertRecyclingForImport(batches, products, events)
                    assertEquals(DomainRecyclingSummary(0, 0, 0, 1), repository.observeSummary(1).first())
                    assertEquals(events, repository.getAllRecyclingInterruptionsForExport().first())
                    assertTrue(repository.getRecyclingList(1).first().single().ingredients.isEmpty())
                }
        } finally {
            context.deleteDatabase(databaseName)
        }
    }

    private suspend fun <T> AppDatabase.useDatabase(block: suspend (AppDatabase) -> T): T = try {
        block(this)
    } finally {
        close()
    }

    private fun project(id: Long) = ProjectTable(
        id = id, title = "Ферма $id", date = "", dateEnd = "", mode = true, archive = false,
        imagePath = null, currentIcon = null
    )

    private fun batch(projectId: Long) = DomainRecycling(day = 21, month = 11, year = 2026, idPT = projectId)

    private fun product(projectId: Long, title: String, readyDayOffset: Int? = null) = DomainRecyclingProduct(
        title = title, count = 1.0, countSuffix = Suffix.KILOGRAM, idPT = projectId,
        readyDay = readyDayOffset?.let { 20 + it }, readyMonth = readyDayOffset?.let { 11 },
        readyYear = readyDayOffset?.let { 2026 }
    )

    private fun ingredient(projectId: Long) = DomainWriteOffTable(
        title = "Сырьё", count = 1.0, countSuffix = Suffix.KILOGRAM, productOrigin = null,
        day = 21, month = 11, year = 2026, status = true, note = "", idPT = projectId
    )

    private companion object {
        const val NOW = "2026-11-21T00:00"
    }
}
