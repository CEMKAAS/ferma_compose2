package com.zaroslikov.data.room

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.data.room.database.AppDatabase
import com.zaroslikov.data.room.repository.ExpensesRepositoryImpl
import com.zaroslikov.data.room.repository.WriteOffRepositoryImpl
import com.zaroslikov.data.room.table.project.ProjectTable
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WarehouseFeedPersistenceTest {
    @Test
    fun writeOffUpdatesObservedStockOnlyOnceInItsProject() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        try {
            val expenses = ExpensesRepositoryImpl(database.expensesDao())
            val writeOff = WriteOffRepositoryImpl(database.writeOffDao())
            database.projectDao().insertProject(project(1))
            database.projectDao().insertProject(project(2))
            val firstPurchase = expenses.insertExpenses(feed(1))
            val secondPurchase = expenses.insertExpenses(feed(2))
            val form = DomainWriteOffTable(
                title = "Корм",
                count = 2.5,
                countSuffix = Suffix.KILOGRAM,
                productOrigin = null,
                day = 26,
                month = 11,
                year = 2026,
                status = false,
                note = "",
                idPT = 1,
            )

            val initial = CompletableDeferred<Unit>()
            val emissions = async(start = CoroutineStart.UNDISPATCHED) {
                expenses.getCurrentFoodWarehouse(1)
                    .distinctUntilChanged()
                    .onEach { initial.complete(Unit) }
                    .take(2)
                    .toList()
            }
            withTimeout(5_000) { initial.await() }
            assertFalse(expenses.writeOffFood(firstPurchase, form.copy(idPT = 2)))
            assertTrue(expenses.writeOffFood(firstPurchase, form))
            assertFalse(expenses.writeOffFood(firstPurchase, form))

            val (before, after) = withTimeout(5_000) { emissions.await() }
            assertEquals(listOf(firstPurchase), before.map { it.id })
            assertTrue(after.isEmpty())
            assertEquals(listOf(secondPurchase), expenses.getCurrentFoodWarehouse(2).first().map { it.id })
            assertEquals(listOf(form.count), writeOff.getAllWriteOffItems(1).first().map { it.count })
            assertTrue(writeOff.getAllWriteOffItems(2).first().isEmpty())
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

    private fun feed(projectId: Long) = DomainExpensesTable(
        title = "Корм",
        count = 3.0,
        countSuffix = Suffix.KILOGRAM,
        day = 26,
        month = 11,
        year = 2026,
        category = null,
        isFood = true,
        isShowFood = true,
        idPT = projectId,
    )
}
