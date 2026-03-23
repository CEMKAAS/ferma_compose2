package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.table.ferma.ExpensesAnimalTable

@Dao
interface ExpensesAnimalDao {
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