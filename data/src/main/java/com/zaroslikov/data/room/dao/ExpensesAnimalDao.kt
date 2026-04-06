package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.table.ferma.ExpensesAnimalTable
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpensesAnimalDao {

    @Query("SELECT * from expensesanimaltable")
    fun getAllExpensesAnimalTableForExport(): Flow<List<ExpensesAnimalTable>>

    @Upsert
    suspend fun insertAllExpensesAnimalTable(data: List<ExpensesAnimalTable>)

    @Query("DELETE FROM expensesanimaltable")
    suspend fun deleteAllExpensesAnimalTable()

    @Transaction
    suspend fun clearAndInsertExpensesAnimalTableForImport(expensesAnimalTable: List<ExpensesAnimalTable>) {
        deleteAllExpensesAnimalTable()
        insertAllExpensesAnimalTable(expensesAnimalTable)
    }

    @Query("SELECT idAnimal FROM ExpensesAnimalTable WHERE idExpenses=:id")
    suspend fun getItemExpensesAnimal(id: Long): List<Long>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertExpensesAnimal(item: ExpensesAnimalTable)

    @Update
    suspend fun updateExpensesAnimal(item: ExpensesAnimalTable)

    @Delete
    suspend fun deleteExpensesAnimal(item: ExpensesAnimalTable)

    @Query("DELETE FROM ExpensesAnimalTable WHERE idExpenses=:id")
    suspend fun deleteExpensesAnimalById(id: Long)
}