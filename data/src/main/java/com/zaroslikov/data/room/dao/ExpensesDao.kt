package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.dto.add.TitleAndSuffixDto
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDto
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDto
import com.zaroslikov.data.room.table.ferma.ExpensesTable
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpensesDao {
    @Query("SELECT * FROM expenses_table " +
            "Where idPT=:id ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day) ) DESC, _id DESC")
    fun getAllExpensesItems(id: Int): Flow<List<ExpensesTable>>

    @Query("SELECT * FROM expenses_table WHERE _id=:id")
    fun getItemExpenses(id: Int): Flow<ExpensesTable>

    @Query("SELECT * FROM expenses_table WHERE animal_count_id=:id")
    fun getItemExpensesIdAnimalCount(id: Int): Flow<ExpensesTable>

    @Query("SELECT * FROM expenses_table WHERE animal_vaccination_id=:id")
    fun getItemExpensesForVaccination(id: Long): Flow<ExpensesTable>

    @Query(
        "SELECT title, SUM(count) AS count," +
                " SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END) AS price," +
                " count_suffix AS suffix" +
                " FROM expenses_table" +
                " WHERE idPT=:id" +
                " GROUP BY title" +
                " ORDER BY price DESC"
    )
    fun getBrieflyItemExpenses(id: Int): Flow<List<BrieflyExpensesDto>>

    @Query("SELECT * FROM expenses_table WHERE idPT=:id AND title =:name ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC")
    fun getBrieflyDetailsItemExpenses(id: Long, name: String): Flow<List<ExpensesTable>>


    @Query(
        "SELECT title AS first, count_suffix AS second FROM expenses_table" +
                " WHERE idPT=:id AND animalId IS NULL AND animal_vaccination_id IS NULL AND animal_count_id IS NULL " +
                " GROUP BY title" +
                " ORDER BY MAX(_id) DESC"
    )
    fun getItemsTitleExpensesList(id: Int): Flow<List<TitleAndSuffixDto>>

    @Query("SELECT category FROM expenses_table WHERE idPT=:id GROUP BY category")
    fun getItemsCategoryExpensesList(id: Int): Flow<List<String>>


    @Query(
        "SELECT a.id, a.name as name, a.foodDay as foodDay, a.food_day_suffix as foodDaySuffix, t.count as countAnimal," +
                " case when e._id NOT NULL Then e._id  else 0 end as idExpensesAnimal," +
                " case when e.idAnimal NOT NULL  Then 1 else 0 end as ps," +
                " case when  e.percentExpenses NOT NULL Then e.percentExpenses else 0 end as presentException " +
                " from animal_table a JOIN (" +
                "    SELECT idAnimal, count" +
                "    FROM animalcounttable" +
                "    WHERE id IN (" +
                "        SELECT MAX(id)" +
                "        FROM animalcounttable " +
                "    GROUP by idAnimal)" +
                ") t ON a.id = t.idAnimal Left Join ExpensesAnimalTable e On e.idAnimal = a.id and e.idExpenses =:idExpenses Where a.idPT=:id ORDER By ps Desc"
    )
    suspend fun getItemsAnimalExpensesList2(id: Int, idExpenses: Long): Flow<List<AnimalExpensesDto>>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertExpenses(item: ExpensesTable): Long

    @Update
    suspend fun updateExpenses(item: ExpensesTable)

    @Delete
    suspend fun deleteExpenses(item: ExpensesTable)

}
