/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaroslikov.fermacompose2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.ui.finance.Fin
import com.zaroslikov.fermacompose2.ui.finance.FinTit
import com.zaroslikov.fermacompose2.ui.finance.IncomeExpensesDetails
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseData
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface ItemDao {

    @Query("SELECT * from Project")
    fun getAllProject(): Flow<List<ProjectTable>>

    @Query("SELECT * from MyFerma WHERE id = :id")
    fun getItem(id: Int): Flow<AddTable>

    @Query("SELECT * from MyFerma Where idPT=:id ORDER BY id DESC")
    fun getAllItems(id: Int): Flow<List<AddTable>>

    @Query("SELECT * from MyFerma Where id=:id")
    fun getItemAdd(id: Int): Flow<AddTable>

    @Query("SELECT MyFerma.Title from MyFerma Where idPT=:id group by MyFerma.Title ")
    fun getItemsTitleAddList(id: Int): Flow<List<String>>

    @Query("SELECT MyFerma.category from MyFerma Where idPT=:id group by MyFerma.category ")
    fun getItemsCategoryAddList(id: Int): Flow<List<String>>

    @Query("SELECT MyFerma.animal from MyFerma Where idPT=:id group by MyFerma.animal ")
    fun getItemsAnimalAddList(id: Int): Flow<List<String>>


    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: AddTable)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProject(projectTable: ProjectTable)

    @Update
    suspend fun update(item: AddTable)

    @Delete
    suspend fun delete(item: AddTable)

    //Sale
    @Query("SELECT * from MyFermaSale Where idPT=:id ORDER BY id DESC")
    fun getAllSaleItems(id: Int): Flow<List<SaleTable>>

    @Query("SELECT * from MyFermaSale Where id=:id")
    fun getItemSale(id: Int): Flow<SaleTable>

    @Query(
        "SELECT Title from (SELECT MyFermaSale.Title From MyFermaSale Where idPT=:id " +
                "UNION All " +
                " SELECT MyFerma.Title from MyFerma Where idPT=:id) combined_table group by Title"
    )
    fun getItemsTitleSaleList(id: Int): Flow<List<String>>

    @Query("SELECT MyFermaSale.category from MyFermaSale Where idPT=:id group by MyFermaSale.category")
    fun getItemsCategorySaleList(id: Int): Flow<List<String>>

    @Query("SELECT MyFermaSale.buyer from MyFermaSale Where idPT=:id group by MyFermaSale.buyer")
    fun getItemsBuyerSaleList(id: Int): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSale(item: SaleTable)
    @Update
    suspend fun updateSale(item: SaleTable)

    @Delete
    suspend fun deleteSale(item: SaleTable)

    //Expenses
    @Query("SELECT * from MyFermaEXPENSES Where idPT=:id ORDER BY id DESC")
    fun getAllExpensesItems(id: Int): Flow<List<ExpensesTable>>

    @Query("SELECT * from MyFermaEXPENSES Where id=:id")
    fun getItemExpenses(id: Int): Flow<ExpensesTable>

    @Query("SELECT MyFermaEXPENSES.Title from MyFermaEXPENSES Where idPT=:id group by MyFermaEXPENSES.Title")
    fun getItemsTitleExpensesList(id: Int): Flow<List<String>>

    @Query("SELECT MyFermaEXPENSES.category from MyFermaEXPENSES Where idPT=:id group by MyFermaEXPENSES.category")
    fun getItemsCategoryExpensesList(id: Int): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpenses(item: ExpensesTable)
    @Update
    suspend fun updateExpenses(item: ExpensesTable)

    @Delete
    suspend fun deleteExpenses(item: ExpensesTable)

    //WriteOff
    @Query("SELECT * from MyFermaWRITEOFF Where idPT=:id ORDER BY id DESC")
    fun getAllWriteOffItems(id: Int): Flow<List<WriteOffTable>>

    @Query("SELECT * from MyFermaWRITEOFF Where id=:id")
    fun getItemWriteOff(id: Int): Flow<WriteOffTable>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWriteOff(item: WriteOffTable)
    @Update
    suspend fun updateWriteOff(item: WriteOffTable)
    @Delete
    suspend fun deleteWriteOff(item: WriteOffTable)


    //Finance
    @Query("SELECT COALESCE(SUM(MyFermaSale.priceAll), 0.0) - COALESCE(SUM(MyFermaEXPENSES.priceAll), 0.0) AS ResultCount FROM MyFermaSale LEFT JOIN MyFermaEXPENSES ON MyFermaSale.idPT = MyFermaEXPENSES.idPT WHERE MyFermaSale.idPT =:id")
    fun getCurrentBalance(id: Int): Flow<Double>

    @Query("SELECT COALESCE(SUM(MyFermaSale.priceAll), 0.0) AS ResultCount FROM MyFermaSale WHERE MyFermaSale.idPT =:id")
    fun getIncome(id: Int): Flow<Double>

    @Query("SELECT COALESCE(SUM(MyFermaEXPENSES.priceAll), 0.0) AS ResultCount FROM MyFermaEXPENSES WHERE MyFermaEXPENSES.idPT =:id")
    fun getExpenses(id: Int): Flow<Double>

    @Query("SELECT MyFermaSale.category, COALESCE(SUM(MyFermaSale.priceAll), 0.0) AS priceAll FROM MyFermaSale Where idPT=:id and mount=:mount and year=:year group by MyFermaSale.category ORDER BY MyFermaSale.priceAll DESC")
    fun getCategoryIncomeCurrentMonth(id: Int, mount: Int, year:Int): Flow<List<Fin>>

    @Query("SELECT MyFermaEXPENSES.category, COALESCE(SUM(MyFermaEXPENSES.priceAll), 0.0) AS priceAll FROM MyFermaEXPENSES Where idPT=:id and mount=:mount and year=:year group by MyFermaEXPENSES.category ORDER BY MyFermaEXPENSES.priceAll DESC")
    fun getCategoryExpensesCurrentMonth(id: Int, mount: Int, year:Int): Flow<List<Fin>>

    @Query(
        "SELECT title, count, suffix, priceAll, day, mount, year " +
                "from (SELECT MyFermaSale.title, MyFermaSale.count, MyFermaSale.suffix, MyFermaSale.priceAll, MyFermaSale.day, MyFermaSale.mount, MyFermaSale.year From MyFermaSale" +
                " Where idPT=:id and mount=:mount and year=:year " +
                "UNION All " +
                "SELECT MyFermaEXPENSES.title, MyFermaEXPENSES.count, MyFermaEXPENSES.suffix, -MyFermaEXPENSES.priceAll AS minusPriceAll, MyFermaEXPENSES.day, MyFermaEXPENSES.mount, MyFermaEXPENSES.year from MyFermaEXPENSES" +
                " Where idPT=:id and mount=:mount and year=:year)" +
                " combined_table"
    )
    fun getIncomeExpensesCurrentMonth(id: Int, mount: Int, year:Int): Flow<List<IncomeExpensesDetails>>

    @Query("SELECT MyFermaSale.title, COALESCE(SUM(MyFermaSale.priceAll), 0.0) AS priceAll FROM MyFermaSale WHERE MyFermaSale.idPT =:id group by MyFermaSale.title")
    fun getIncomeAllList(id: Int): Flow<List<FinTit>>
    @Query("SELECT MyFermaEXPENSES.title, COALESCE(SUM(MyFermaEXPENSES.priceAll), 0.0) AS priceAll FROM MyFermaEXPENSES WHERE MyFermaEXPENSES.idPT =:id group by MyFermaEXPENSES.title")
    fun getExpensesAllList(id: Int): Flow<List<FinTit>>
    @Query("SELECT MyFermaSale.category, COALESCE(SUM(MyFermaSale.priceAll), 0.0) AS priceAll FROM MyFermaSale WHERE MyFermaSale.idPT =:id group by MyFermaSale.category")
    fun getIncomeCategoryAllList(id: Int): Flow<List<Fin>>
    @Query("SELECT MyFermaEXPENSES.category, COALESCE(SUM(MyFermaEXPENSES.priceAll), 0.0) AS priceAll FROM MyFermaEXPENSES WHERE MyFermaEXPENSES.idPT =:id group by MyFermaEXPENSES.category")
    fun getExpensesCategoryAllList(id: Int): Flow<List<Fin>>
    @Query("SELECT MyFermaSale.title, COALESCE(SUM(MyFermaSale.priceAll), 0.0) AS priceAll FROM MyFermaSale Where idPT=:id and mount=:mount and year=:year and category=:category group by MyFermaSale.title ORDER BY MyFermaSale.priceAll DESC")
    fun getProductListCategoryIncomeCurrentMonth(id: Int, mount: Int, year:Int, category: String): Flow<List<FinTit>>
    @Query("SELECT MyFermaEXPENSES.title, COALESCE(SUM(MyFermaEXPENSES.priceAll), 0.0) AS priceAll FROM MyFermaEXPENSES Where idPT=:id and mount=:mount and year=:year and category=:category group by MyFermaEXPENSES.title ORDER BY MyFermaEXPENSES.priceAll DESC")
    fun getProductLisCategoryExpensesCurrentMonth(id: Int, mount: Int, year:Int, category: String): Flow<List<FinTit>>

    @Query("SELECT Title, suffix, " +
            "       SUM(AddCount) - COALESCE(SUM(SaleCount), 0) - COALESCE(SUM(WriteOffCount), 0) AS ResultCount" +
            " FROM (" +
            "    SELECT Title,suffix, SUM(Count) AS AddCount, 0 AS SaleCount, 0 AS WriteOffCount" +
            "    FROM MyFerma" +
            "    WHERE idPT = :id" +
            "    GROUP BY Title" +
            "    UNION ALL" +
            "    SELECT Title, suffix, 0 AS AddCount, SUM(Count) AS SaleCount, 0 AS WriteOffCount" +
            "    FROM MyFermaSale" +
            "    WHERE idPT = :id" +
            "    GROUP BY Title" +
            "    UNION ALL" +
            "    SELECT Title, suffix, 0 AS AddCount, 0 AS SaleCount, SUM(Count) AS WriteOffCount" +
            "    FROM MyFermaWRITEOFF" +
            "    WHERE idPT = :id" +
            "    GROUP BY Title" +
            ")" +
            "GROUP BY Title ORDER BY ResultCount DESC")
    fun getCurrentBalanceWarehouse(id: Int): Flow<List<WarehouseData>>

}
