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
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalSizeTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.data.animal.AnimalWeightTable
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesAnimalTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.data.ferma.NoteTable
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.ui.animal.AnimalIndicatorsVM
import com.zaroslikov.fermacompose2.ui.animal.AnimalTitSuff
import com.zaroslikov.fermacompose2.ui.expenses.AnimalExpensesList
import com.zaroslikov.fermacompose2.ui.expenses.AnimalExpensesList2
import com.zaroslikov.fermacompose2.ui.finance.AnalysisSaleBuyerAllTime

import com.zaroslikov.fermacompose2.ui.finance.Fin
import com.zaroslikov.fermacompose2.ui.finance.FinanceAnalysisViewModel
import com.zaroslikov.fermacompose2.ui.finance.IncomeExpensesDetails
import com.zaroslikov.fermacompose2.ui.home.PairString
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseData
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

@Dao
interface ItemDao {

    @Query("SELECT * from МyINCUBATOR")
    fun getAllProject(): Flow<List<ProjectTable>>

    @Query("SELECT * from МyINCUBATOR Where ARHIVE = 1")
    fun getAllProjectArh(): Flow<List<ProjectTable>>

    @Query("SELECT * from МyINCUBATOR Where ARHIVE = 0")
    fun getAllProjectAct(): Flow<List<ProjectTable>>

    @Query("SELECT * from МyINCUBATOR Where _id=:id")
    fun getProject(id: Int): Flow<ProjectTable>

    @Query("SELECT * from MyIncubator Where idPT =:idPT")
    suspend fun getIncubatorListArh4(idPT: Int): List<Incubator>

    @Query("SELECT * from МyINCUBATOR Where TYPE =:type and mode = 0 and ARHIVE = 1")
    suspend fun getIncubatorListArh6(type: String): List<ProjectTable>

    @Query("SELECT * from МyINCUBATOR Where mode = 1 and ARHIVE = 0")
    fun getProjectListAct(): Flow<List<ProjectTable>>

    @Query("SELECT COUNT(*) AS row_count from МyINCUBATOR Where mode = 0")
    fun getCountRowIncubator(): Flow<Int>

    @Query("SELECT COUNT(*) AS row_count from МyINCUBATOR Where mode = 1")
    fun getCountRowProject(): Flow<Int>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProject(projectTable: ProjectTable)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProjectLong(projectTable: ProjectTable): Long

    @Update
    suspend fun updateProject(item: ProjectTable)

    @Delete
    suspend fun deleteProject(item: ProjectTable)

    @Query("SELECT _id from МyINCUBATOR ORDER BY _id DESC Limit 1")
    fun getLastProject(): Flow<Int>

    @Query("SELECT * from MyIncubator Where idPT=:id")
    fun getIncubatorList(id: Int): Flow<List<Incubator>>

    @Query("SELECT * from MyIncubator Where idPT=:id")
    suspend fun getIncubatorList2(id: Int): List<Incubator>

    @Query("SELECT * from MyIncubator Where idPT=:id")
    fun getIncubator(id: Int): Flow<Incubator>

    @Query("SELECT * from MyIncubator Where idPT=:id and day=:day")
    fun getIncubatorEditDay(id: Int, day: Int): Flow<Incubator>

    @Query("SELECT * from MyFerma WHERE _id = :id")
    fun getItem(id: Int): Flow<AddTable>

    @Query("SELECT * from MyFerma Where idPT=:id ORDER BY DATE(printf('%04d-%02d-%02d', year, mount, day) ) DESC")
    fun getAllItems(id: Int): Flow<List<AddTable>>

    @Query("SELECT * from MyFerma Where _id=:id")
    fun getItemAdd(id: Int): Flow<AddTable>

    @Query("SELECT MyFerma.Title from MyFerma Where idPT=:id group by MyFerma.Title ")
    fun getItemsTitleAddList(id: Int): Flow<List<String>>

    @Query(
        "SELECT Title as name, 'Продукция' as type from MyFerma Where idPT=:id group by MyFerma.Title  " +
                " UNION ALL" +
                " SELECT titleEXPENSES as name, 'Купленый товар' as type from MyFermaEXPENSES Where idPT=:id and showWarehouse = 1 group by titleEXPENSES "
    )
    fun getItemsWriteoffList(id: Int): Flow<List<PairString>>

    @Query("SELECT MyFerma.category from MyFerma Where idPT=:id group by MyFerma.category ")
    fun getItemsCategoryAddList(id: Int): Flow<List<String>>

    @Query("SELECT name, type from AnimalTable Where idPT=:id")
    fun getItemsAnimalAddList(id: Int): Flow<List<PairString>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: AddTable)

    @Update
    suspend fun update(item: AddTable)

    @Delete
    suspend fun delete(item: AddTable)

    //Sale
    @Query("SELECT * from MyFermaSale Where idPT=:id ORDER BY DATE(printf('%04d-%02d-%02d', year, mount, day) ) DESC")
    fun getAllSaleItems(id: Int): Flow<List<SaleTable>>

    @Query("SELECT * from MyFermaSale Where _id=:id")
    fun getItemSale(id: Int): Flow<SaleTable>

    @Query(
        "SELECT titleSale from (SELECT MyFermaSale.titleSale From MyFermaSale Where idPT=:id " +
                "UNION All " +
                " SELECT MyFerma.Title from MyFerma Where idPT=:id) combined_table group by titleSale"
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
    @Query("SELECT * from MyFermaEXPENSES Where idPT=:id ORDER BY DATE(printf('%04d-%02d-%02d', year, mount, day) ) DESC")
    fun getAllExpensesItems(id: Int): Flow<List<ExpensesTable>>

    @Query("SELECT * from MyFermaEXPENSES Where _id=:id")
    fun getItemExpenses(id: Int): Flow<ExpensesTable>

    @Query("SELECT idAnimal from ExpensesAnimalTable Where idExpenses=:id")
    suspend fun getItemExpensesAnimal(id: Int): List<Long>

    @Query("SELECT MyFermaEXPENSES.titleEXPENSES from MyFermaEXPENSES Where idPT=:id group by MyFermaEXPENSES.titleEXPENSES")
    fun getItemsTitleExpensesList(id: Int): Flow<List<String>>

    @Query("SELECT MyFermaEXPENSES.category from MyFermaEXPENSES Where idPT=:id group by MyFermaEXPENSES.category")
    fun getItemsCategoryExpensesList(id: Int): Flow<List<String>>

    @Query(
        "SELECT a.id, a.name as name, a.foodDay as foodDay, t.count as countAnimal from AnimalTable a JOIN (" +
                "    SELECT idAnimal, count" +
                "    FROM animalcounttable" +
                "    WHERE id IN (" +
                "        SELECT MAX(id)" +
                "        FROM animalcounttable " +
                "    GROUP by idAnimal)" +
                ") t ON a.id = t.idAnimal Where a.idPT=:id"
    )
    fun getItemsAnimalExpensesList(id: Int): Flow<List<AnimalExpensesList>>

//    @Query(
//        "SELECT a.id, a.name as name, a.foodDay as foodDay, t.count as countAnimal, (Select case when e.idAnimal = a.id and e.idExpenses =:idExpenses Then 1 else 0 end as ps From ExpensesAnimalTable e) as ps" +
//                " from AnimalTable a JOIN (" +
//                "    SELECT idAnimal, count" +
//                "    FROM animalcounttable" +
//                "    WHERE id IN (" +
//                "        SELECT MAX(id)" +
//                "        FROM animalcounttable " +
//                "    GROUP by idAnimal)" +
//                ") t ON a.id = t.idAnimal Join ExpensesAnimalTable e On e.idAnimal = a.id Where a.idPT=:id GROUP BY a.id"
//    )

    @Query(
        "SELECT a.id, a.name as name, a.foodDay as foodDay, t.count as countAnimal,  case when e._id NOT NULL Then e._id  else 0 end as idExpensesAnimal, case when e.idAnimal NOT NULL  Then 1 else 0 end as ps,   case when  e.percentExpenses NOT NULL Then e.percentExpenses else 0 end as presentException " +
                " from AnimalTable a JOIN (" +
                "    SELECT idAnimal, count" +
                "    FROM animalcounttable" +
                "    WHERE id IN (" +
                "        SELECT MAX(id)" +
                "        FROM animalcounttable " +
                "    GROUP by idAnimal)" +
                ") t ON a.id = t.idAnimal Left Join ExpensesAnimalTable e On e.idAnimal = a.id and e.idExpenses =:idExpenses Where a.idPT=:id ORDER By ps Desc"
    )
    suspend fun getItemsAnimalExpensesList2(id: Int, idExpenses: Long): List<AnimalExpensesList2>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpenses(item: ExpensesTable): Long

    @Update
    suspend fun updateExpenses(item: ExpensesTable)

    @Delete
    suspend fun deleteExpenses(item: ExpensesTable)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpensesAnimal(item: ExpensesAnimalTable)

    @Update
    suspend fun updateExpensesAnimal(item: ExpensesAnimalTable)

    @Delete
    suspend fun deleteExpensesAnimal(item: ExpensesAnimalTable)

    //WriteOff
    @Query("SELECT * from MyFermaWRITEOFF Where idPT=:id ORDER BY DATE(printf('%04d-%02d-%02d', year, mount, day) ) DESC")
    fun getAllWriteOffItems(id: Int): Flow<List<WriteOffTable>>

    @Query("SELECT * from MyFermaWRITEOFF Where _id=:id")
    fun getItemWriteOff(id: Int): Flow<WriteOffTable>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWriteOff(item: WriteOffTable)

    @Update
    suspend fun updateWriteOff(item: WriteOffTable)

    @Delete
    suspend fun deleteWriteOff(item: WriteOffTable)


    //Finance
    @Query(
        "SELECT COALESCE((SELECT SUM(PRICE) FROM MyFermaSale WHERE idPT =:id), 0) " +
                "- COALESCE((SELECT SUM(countEXPENSES) FROM MyFermaEXPENSES WHERE idPT=:id), 0) " +
                "- COALESCE((SELECT SUM(price) FROM AnimalTable WHERE idPT=:id), 0) AS PriceDifference;"
    )
    fun getCurrentBalance(id: Int): Flow<Double>

    @Query("SELECT COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS ResultCount FROM MyFermaSale WHERE MyFermaSale.idPT =:id")
    fun getIncome(id: Int): Flow<Double>

    @Query(
        "SELECT COALESCE((SELECT SUM(countEXPENSES) FROM MyFermaEXPENSES WHERE idPT=:id), 0) " +
                "+ COALESCE((SELECT SUM(price) FROM AnimalTable WHERE idPT=:id), 0) AS ResultCount"
    )
    fun getExpenses(id: Int): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0.0) AS ResultCount FROM MyFermaWRITEOFF WHERE MyFermaWRITEOFF.idPT =:id and statusWRITEOFF=0")
    fun getOwnNeed(id: Int): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0.0) AS ResultCount FROM MyFermaWRITEOFF WHERE MyFermaWRITEOFF.idPT =:id and statusWRITEOFF=1")
    fun getScrap(id: Int): Flow<Double>

    @Query("SELECT COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS ResultCount FROM MyFermaSale WHERE MyFermaSale.idPT =:id and mount =:mount and year =:year")
    fun getIncomeMountFin(id: Int, mount: Int, year: Int): Flow<Double>

//    @Query("SELECT COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS ResultCount FROM MyFermaEXPENSES WHERE MyFermaEXPENSES.idPT =:id and mount =:mount and year =:year")
//    fun getExpensesMountFin(id: Int, mount: Int, year: Int): Flow<Double>

    @Query(
        "SELECT COALESCE((SELECT SUM(countEXPENSES) FROM MyFermaEXPENSES WHERE idPT =:id and mount =:mount and year =:year), 0)" +
                "+ COALESCE((SELECT SUM(price) FROM AnimalTable WHERE idPT=:id and strftime('%Y-%m'," +
                "                substr(data, 7, 4) || '-' ||" +
                "                substr(data, 4, 2) || '-' ||" +
                "                substr(data, 1, 2)) =:yearMonth), 0) AS ResultCount"
    )
    fun getExpensesMountFin(id: Int, mount: Int, year: Int, yearMonth: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS ResultCount FROM MyFermaSale WHERE MyFermaSale.idPT =:id AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getIncomeMount(id: Int, dateBegin: String, dateEnd: String): Flow<Double>

    @Query(
        "SELECT COALESCE((SELECT SUM(countEXPENSES) FROM MyFermaEXPENSES WHERE idPT =:id AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0.0)" +
                " +  COALESCE((SELECT SUM(price) FROM AnimalTable WHERE idPT =:id AND DATE(printf('%04d-%02d-%02d',  substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0.0) AS ResultCount "
    )
    fun getExpensesMount(id: Int, dateBegin: String, dateEnd: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0.0) AS ResultCount FROM MyFermaWRITEOFF WHERE MyFermaWRITEOFF.idPT =:id AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) and statusWRITEOFF=0")
    fun getOwnNeedMonth(id: Int, dateBegin: String, dateEnd: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0.0) AS ResultCount FROM MyFermaWRITEOFF WHERE MyFermaWRITEOFF.idPT =:id AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) and statusWRITEOFF=1")
    fun getScrapMonth(id: Int, dateBegin: String, dateEnd: String): Flow<Double>

    @Query("SELECT MyFermaSale.category as title, COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS priceAll FROM MyFermaSale Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) group by MyFermaSale.category ORDER BY MyFermaSale.PRICE DESC")
    fun getCategoryIncomeCurrentMonth(id: Int, dateBegin: String, dateEnd: String): Flow<List<Fin>>

    @Query(
        "SELECT category as title, COALESCE(SUM(countEXPENSES), 0.0) AS priceAll FROM MyFermaEXPENSES Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) group by category " +
                " UNION All " +
                "SELECT ' Мои Животные ' as title, COALESCE(SUM(price), 0.0) AS priceAll FROM AnimalTable Where idPT=:id AND DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
//    ORDER BY countEXPENSES DESC
    fun getCategoryExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>>


//    @Query("SELECT COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS ResultCount FROM MyFermaSale WHERE MyFermaSale.idPT =:id and mount=:mount and year=:year")
//    fun getIncomeMount(id: Int, mount: Int, year: Int): Flow<Double>
//
//    @Query("SELECT COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS ResultCount FROM MyFermaEXPENSES WHERE MyFermaEXPENSES.idPT =:id and mount=:mount and year=:year")
//    fun getExpensesMount(id: Int, mount: Int, year: Int): Flow<Double>
//
//    @Query("SELECT MyFermaSale.category as title, COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS priceAll FROM MyFermaSale Where idPT=:id and mount=:mount and year=:year group by MyFermaSale.category ORDER BY MyFermaSale.PRICE DESC")
//    fun getCategoryIncomeCurrentMonth(id: Int, mount: Int, year: Int): Flow<List<Fin>>
//
//    @Query("SELECT MyFermaEXPENSES.category as title, COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS priceAll FROM MyFermaEXPENSES Where idPT=:id and mount=:mount and year=:year group by MyFermaEXPENSES.category ORDER BY MyFermaEXPENSES.countEXPENSES DESC")
//    fun getCategoryExpensesCurrentMonth(id: Int, mount: Int, year: Int): Flow<List<Fin>>

    @Query(
        "SELECT titleSale as title, discSale as count, suffix, PRICE as priceAll, printf('%02d.%02d.%04d', day, month, year) as date " +
                "from (SELECT titleSale, discSale, suffix, PRICE, day, mount as month, year From MyFermaSale" +
                " Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
                " UNION All " +
                " SELECT titleEXPENSES,discEXPENSES, suffix, -countEXPENSES AS minusPriceAll, day, mount as month, year from MyFermaEXPENSES" +
                " Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
                " UNION All " +
                " SELECT name, (Select Count From AnimalCountTable Where idAnimal = a.id ORDER BY count DESC LIMIT 1 ), 'Шт.', -price AS minusPriceAll,substr(data, 1, 2) AS day,  substr(data, 4, 2) AS month,  substr(data, 7, 4) AS year from AnimalTable a " +
                " Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)) " +
                " combined_table ORDER BY date DESC"
    )
    fun getIncomeExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<IncomeExpensesDetails>>

    @Query("SELECT MyFermaSale.titleSale as title, COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS priceAll FROM MyFermaSale WHERE MyFermaSale.idPT =:id group by MyFermaSale.titleSale")
    fun getIncomeAllList(id: Int): Flow<List<Fin>>

    @Query("SELECT MyFermaEXPENSES.titleEXPENSES as title, COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS priceAll FROM MyFermaEXPENSES WHERE MyFermaEXPENSES.idPT =:id group by title")
    fun getExpensesAllList(id: Int): Flow<List<Fin>>

    @Query("SELECT name as title, COALESCE(SUM(price), 0.0) AS priceAll FROM AnimalTable WHERE idPT =:id group by name")
    fun getExpensesAnimalAllList(id: Int): Flow<List<Fin>>

    @Query("SELECT MyFermaSale.category as title, COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS priceAll FROM MyFermaSale WHERE MyFermaSale.idPT =:id group by MyFermaSale.category")
    fun getIncomeCategoryAllList(id: Int): Flow<List<Fin>>

    @Query(
        "SELECT category as title, COALESCE(SUM(countEXPENSES), 0.0) AS priceAll FROM MyFermaEXPENSES WHERE idPT =:id group by category" +
                " UNION ALL " +
                "SELECT 'Мои Животные' as title, COALESCE(SUM(price), 0.0) AS priceAll FROM AnimalTable WHERE idPT =:id"
    )
    fun getExpensesCategoryAllList(id: Int): Flow<List<Fin>>

    @Query("SELECT MyFermaSale.titleSale as title, COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS priceAll FROM MyFermaSale Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) and category=:category group by MyFermaSale.titleSale ORDER BY MyFermaSale.PRICE DESC")
    fun getProductListCategoryIncomeCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String,
        category: String
    ): Flow<List<Fin>>

    @Query("SELECT MyFermaEXPENSES.titleEXPENSES as title, COALESCE(SUM(MyFermaEXPENSES.discEXPENSES), 0.0) AS priceAll FROM MyFermaEXPENSES Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) and category=:category group by MyFermaEXPENSES.titleEXPENSES ORDER BY MyFermaEXPENSES.countEXPENSES DESC")
    fun getProductLisCategoryExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String,
        category: String
    ): Flow<List<Fin>>

    @Query("SELECT name as title, COALESCE(SUM(price), 0.0) AS priceAll FROM AnimalTable Where idPT=:id AND DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) group by name ORDER BY price DESC")
    fun getProductLisCategoryExpensesAnimalCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>>


    @Query(
        "SELECT Title, suffix, " +
                "       SUM(AddCount) - COALESCE(SUM(SaleCount), 0) - COALESCE(SUM(WriteOffCount), 0) AS ResultCount" +
                " FROM (" +
                "    SELECT Title,suffix, SUM(disc) AS AddCount, 0 AS SaleCount, 0 AS WriteOffCount" +
                "    FROM MyFerma" +
                "    WHERE idPT = :id" +
                "    GROUP BY Title" +
                "    UNION ALL" +
                "    SELECT titleSale, suffix, 0 AS AddCount, SUM(discSale) AS SaleCount, 0 AS WriteOffCount" +
                "    FROM MyFermaSale" +
                "    WHERE idPT = :id" +
                "    GROUP BY titleSale" +
                "    UNION ALL" +
                "    SELECT titleWRITEOFF, suffix, 0 AS AddCount, 0 AS SaleCount, SUM(discWRITEOFF) AS WriteOffCount" +
                "    FROM MyFermaWRITEOFF" +
                "    WHERE idPT = :id" +
                "    GROUP BY titleWRITEOFF" +
                ")" +
                "  GROUP BY Title HAVING ResultCount > 0 ORDER BY ResultCount DESC "
    )
    fun getCurrentBalanceWarehouse(id: Int): Flow<List<WarehouseData>>


    @Query(
        "SELECT * From myfermaexpenses Where idPT =:id and showFood = 1"
    )
    fun getCurrentFoodWarehouse(id: Int): Flow<List<ExpensesTable>>

    @Query(
        "SELECT titleEXPENSES as Title, suffix, " +
                "      SUM(ExpensesCount) - COALESCE(SUM(WriteOffCount), 0) AS ResultCount" +
                " FROM (" +
                "    SELECT titleEXPENSES,suffix, SUM(countEXPENSES) AS ExpensesCount, 0 AS WriteOffCount" +
                "    FROM MyFermaEXPENSES" +
                "    WHERE idPT = :id and showWarehouse = 1 and showFood != 1" +
                "    GROUP BY titleEXPENSES" +
                "    UNION ALL" +
                "    SELECT titleWRITEOFF, suffix, 0 AS ExpensesCoun, SUM(discWRITEOFF) AS WriteOffCount" +
                "    FROM MyFermaWRITEOFF" +
                "    WHERE idPT = :id" +
                "    GROUP BY titleWRITEOFF" +
                ")" +
                " GROUP BY titleEXPENSES HAVING ResultCount > 0 ORDER BY ResultCount DESC"
    )
    fun getCurrentExpensesWarehouse(id: Int): Flow<List<WarehouseData>>


    @Query(
        "SELECT Title, suffix, " +
                "       SUM(AddCount) - COALESCE(SUM(SaleCount), 0) - COALESCE(SUM(WriteOffCount), 0) AS ResultCount" +
                " FROM (" +
                "    SELECT Title,suffix, SUM(disc) AS AddCount, 0 AS SaleCount, 0 AS WriteOffCount" +
                "    FROM MyFerma" +
                "    WHERE Title = :name" +
                "    GROUP BY Title" +
                "    UNION ALL" +
                "    SELECT titleSale, suffix, 0 AS AddCount, SUM(discSale) AS SaleCount, 0 AS WriteOffCount" +
                "    FROM MyFermaSale" +
                "    WHERE titleSale = :name" +
                "    GROUP BY titleSale" +
                "    UNION ALL" +
                "    SELECT titleWRITEOFF, suffix, 0 AS AddCount, 0 AS SaleCount, SUM(discWRITEOFF) AS WriteOffCount" +
                "    FROM MyFermaWRITEOFF" +
                "    WHERE titleWRITEOFF = :name" +
                "    GROUP BY titleWRITEOFF" +
                ")" +
                "  GROUP BY Title HAVING ResultCount > 0 ORDER BY ResultCount DESC "
    )
    fun getCurrentBalanceProduct(name: String): Flow<Double>

    @Query(
        "SELECT titleEXPENSES as Title, suffix, " +
                "      SUM(ExpensesCount) - COALESCE(SUM(WriteOffCount), 0) AS ResultCount" +
                " FROM (" +
                "    SELECT titleEXPENSES,suffix, SUM(countEXPENSES) AS ExpensesCount, 0 AS WriteOffCount" +
                "    FROM MyFermaEXPENSES" +
                "    WHERE idPT = :name and showWarehouse = 1 and showFood != 1" +
                "    GROUP BY titleEXPENSES" +
                "    UNION ALL" +
                "    SELECT titleWRITEOFF, suffix, 0 AS ExpensesCoun, SUM(discWRITEOFF) AS WriteOffCount" +
                "    FROM MyFermaWRITEOFF" +
                "    WHERE idPT = :name" +
                "    GROUP BY titleWRITEOFF" +
                ")" +
                " GROUP BY titleEXPENSES HAVING ResultCount > 0 ORDER BY ResultCount DESC"
    )
    fun getCurrentExpensesProduct(name: String): Flow<Double>


    // Analysis
    @Query("SELECT suffix as title, COALESCE(SUM(disc), 0) AS priceAll from MyFerma Where idPT=:id and title=:name")
    fun getAnalysisAddAllTime(id: Int, name: String): Flow<Fin>

    @Query("SELECT suffix as title, COALESCE(SUM(discSale), 0) AS priceAll from MyFermaSale Where idPT=:id and titleSale=:name")
    fun getAnalysisSaleAllTime(id: Int, name: String): Flow<Fin>

    @Query("SELECT suffix as title, COALESCE(SUM(discWRITEOFF), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name")
    fun getAnalysisWriteOffAllTime(id: Int, name: String): Flow<Fin>

    @Query("SELECT suffix as title, COALESCE(SUM(discWRITEOFF), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=0")
    fun getAnalysisWriteOffOwnNeedsAllTime(id: Int, name: String): Flow<Fin>

    @Query("SELECT suffix as title, COALESCE(SUM(discWRITEOFF), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=1")
    fun getAnalysisWriteOffScrapAllTime(id: Int, name: String): Flow<Fin>

    @Query("SELECT COALESCE(SUM(price), 0) AS priceAll from MyFermaSale Where idPT=:id and titleSale=:name")
    fun getAnalysisSaleSoldAllTime(id: Int, name: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=0")
    fun getAnalysisWriteOffOwnNeedsMoneyAllTime(id: Int, name: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=1")
    fun getAnalysisWriteOffScrapMoneyAllTime(id: Int, name: String): Flow<Double>

    @Query("SELECT suffix as title, CASE WHEN COALESCE(SUM(disc), 0) = 0 THEN 0 ELSE COALESCE(SUM(disc), 0) / 365 END AS priceAll from MyFerma Where idPT=:id and title=:name")
    fun getAnalysisAddAverageValueAllTime(id: Int, name: String): Flow<Fin>

    @Query("SELECT animal as title, COALESCE(SUM(disc),0) AS priceAll, suffix from MyFerma Where idPT=:id and title=:name GROUP BY animal ORDER BY priceAll DESC")
    fun getAnalysisAddAnimalAllTime(id: Int, name: String): Flow<List<AnimalTitSuff>>

    @Query("SELECT Buyer As buyer, COALESCE(SUM(PRICE),0) AS resultPrice, COALESCE(SUM(discSale),0) AS resultCount, suffix from MyFermaSale Where idPT=:id  and titleSale =:name  GROUP BY buyer ORDER BY ResultPrice DESC")
    fun getAnalysisSaleBuyerAllTime(id: Int, name: String): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query(
        "SELECT at.name As title, COALESCE((SUM((e.countEXPENSES*ea.percentExpenses)/100)/SUM(a.disc)),0) AS priceAll from MyFerma a" +
                " left Join AnimalTable at On at.name = a.animal" +
                " left Join ExpensesAnimalTable ea On ea.idAnimal = at.id" +
                " left Join MyFermaEXPENSES e on ea.idExpenses = e._id" +
                " Where a.title =:name and a.idPT=:id and at.name IS NOT NULL GROUP BY a.animal ORDER BY priceAll DESC"
    )
    fun getAnalysisCostPriceAllTime(id: Int, name: String): Flow<List<Fin>>

    //AnalisisRange
    @Query("SELECT suffix as title, COALESCE(SUM(disc), 0) AS priceAll from MyFerma Where idPT=:id and title=:name AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisAddAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    @Query("SELECT suffix as title, COALESCE(SUM(discSale), 0) AS priceAll from MyFermaSale Where idPT=:id and titleSale=:name AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisSaleAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    @Query("SELECT suffix as title, COALESCE(SUM(discWRITEOFF), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    @Query("SELECT suffix as title, COALESCE(SUM(discWRITEOFF), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=0 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffOwnNeedsAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    @Query("SELECT suffix as title, COALESCE(SUM(discWRITEOFF), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=1 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffScrapAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    @Query("SELECT COALESCE(SUM(price), 0) AS priceAll from MyFermaSale Where idPT=:id and titleSale=:name AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisSaleSoldAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=0 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffOwnNeedsMoneyAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and titleWRITEOFF=:name and statusWRITEOFF=1 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffScrapMoneyAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT suffix as title, CASE WHEN COALESCE(SUM(disc), 0) = 0 THEN 0 ELSE COALESCE(SUM(disc), 0) / 365 END AS priceAll from MyFerma Where idPT=:id and title=:name AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisAddAverageValueAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    @Query("SELECT animal as title, COALESCE(SUM(disc),0) AS priceAll, suffix from MyFerma Where idPT=:id and title=:name AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY animal ORDER BY priceAll DESC ")
    fun getAnalysisAddAnimalAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnimalTitSuff>>

    @Query("SELECT Buyer As buyer, COALESCE(SUM(PRICE),0) AS resultPrice, COALESCE(SUM(discSale),0) AS resultCount, suffix from MyFermaSale Where idPT=:id  and titleSale =:name AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY buyer ORDER BY ResultPrice DESC")
    fun getAnalysisSaleBuyerAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query(
        "SELECT at.name As title, COALESCE((SUM((e.countEXPENSES*ea.percentExpenses)/100)/SUM(a.disc)),0) AS priceAll, '₽' AS suffix from MyFerma a" +
                " left Join AnimalTable at On at.name = a.animal" +
                " left Join ExpensesAnimalTable ea On ea.idAnimal = at.id" +
                " left Join MyFermaEXPENSES e on ea.idExpenses = e._id" +
                " Where a.idPT=:id  and a.title =:name AND DATE(printf('%04d-%02d-%02d', a.year, a.mount, a.day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY a.animal ORDER BY priceAll DESC"
    )
    fun getAnalysisCostPriceAllTimeRange(
        id: Int, name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncubator(item: Incubator)

    @Update
    suspend fun updateIncubator(item: Incubator)

    //Animal
    @Query("SELECT * from AnimalTable Where idPT=:id")
    fun getAllAnimal(id: Int): Flow<List<AnimalTable>>

    @Query("SELECT * from AnimalTable Where id=:id")
    fun getAnimal(id: Int): Flow<AnimalTable>

    @Query("SELECT type from AnimalTable Where idPT=:id GROUP BY type")
    fun getTypeAnimal(id: Int): Flow<List<String>>


    @Insert
    suspend fun insertAnimalTable(animalTable: AnimalTable): Long

    @Insert
    suspend fun insertAnimalCountTable(animalCountTable: AnimalCountTable)

    @Insert
    suspend fun insertAnimalSizeTable(animalSizeTable: AnimalSizeTable)

    @Insert
    suspend fun insertAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable)

    @Insert
    suspend fun insertAnimalWeightTable(animalWeightTable: AnimalWeightTable)

    @Update
    suspend fun updateAnimalTable(animalTable: AnimalTable)

    @Delete
    suspend fun deleteAnimalTable(animalTable: AnimalTable)

    @Update
    suspend fun updateAnimalCountTable(animalCountTable: AnimalCountTable)

    @Update
    suspend fun updateAnimalSizeTable(animalSizeTable: AnimalSizeTable)

    @Update
    suspend fun updateAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable)

    @Update
    suspend fun updateAnimalWeightTable(animalWeightTable: AnimalWeightTable)

    @Delete
    suspend fun deleteAnimalCountTable(animalCountTable: AnimalCountTable)

    @Delete
    suspend fun deleteAnimalSizeTable(animalSizeTable: AnimalSizeTable)

    @Delete
    suspend fun deleteAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable)

    @Delete
    suspend fun deleteAnimalWeightTable(animalWeightTable: AnimalWeightTable)


    @Query("SELECT * from AnimalCountTable Where idAnimal=:id ORDER BY id DESC LIMIT 3")
    fun getCountAnimalLimit(id: Int): Flow<List<AnimalCountTable>>

    @Query("SELECT * from AnimalSizeTable Where idAnimal=:id ORDER BY id DESC LIMIT 3")
    fun getSizeAnimalLimit(id: Int): Flow<List<AnimalSizeTable>>

    @Query("SELECT * from AnimalVaccinationTable Where idAnimal=:id ORDER BY id DESC LIMIT 3")
    fun getVaccinationtAnimalLimit(id: Int): Flow<List<AnimalVaccinationTable>>

    @Query("SELECT * from AnimalWeightTable Where idAnimal=:id ORDER BY id DESC LIMIT 3")
    fun getWeightAnimalLimit(id: Int): Flow<List<AnimalWeightTable>>

    @Query("SELECT id, count as weight, date, idAnimal from AnimalCountTable Where idAnimal=:id ORDER BY id DESC")
    fun getCountAnimal(id: Int): Flow<List<AnimalIndicatorsVM>>

    @Query("SELECT id, size as weight, date, idAnimal from AnimalSizeTable Where idAnimal=:id ORDER BY id DESC")
    fun getSizeAnimal(id: Int): Flow<List<AnimalIndicatorsVM>>

    @Query("SELECT * from AnimalVaccinationTable Where idAnimal=:id ORDER BY id DESC")
    fun getVaccinationtAnimal(id: Int): Flow<List<AnimalVaccinationTable>>

    @Query("SELECT * from AnimalWeightTable Where idAnimal=:id ORDER BY id DESC")
    fun getWeightAnimal(id: Int): Flow<List<AnimalIndicatorsVM>>

    @Query("SELECT title,COALESCE(SUM(disc), 0.0) AS priceAll, suffix from MyFerma Where animal=:name GROUP BY Title ORDER BY priceAll DESC")
    fun getProductAnimal(name: String): Flow<List<AnimalTitSuff>>

    /*
     *
     * Note
     *
     */
    @Query("SELECT * from NoteFerma Where idPT=:id ORDER BY strftime('%Y-%m-%d', substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) DESC")
    fun getAllNote(id: Int): Flow<List<NoteTable>>

    @Query("SELECT * from NoteFerma Where _id=:id")
    fun getNote(id: Int): Flow<NoteTable>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(item: NoteTable)

    @Update
    suspend fun updateNote(item: NoteTable)

    @Delete
    suspend fun deleteNote(item: NoteTable)

    // NewYear Project
    @Query("SELECT COALESCE(SUM(PRICE), 0) AS priceAll from MyFermaSale Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisSaleNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT COALESCE((SELECT SUM(countEXPENSES) FROM MyFermaEXPENSES WHERE idPT=:id and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0) +" +
                " COALESCE((SELECT SUM(price) FROM AnimalTable WHERE idPT=:id and DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0) " +
                "AS PriceDifference"
    )
    fun getAnalysisExpensesNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and statusWRITEOFF=0 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffOwnNeedsNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF Where idPT=:id and statusWRITEOFF=1 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffScrapNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT  COALESCE(SUM(t.count), 0) as countAnimal" +
                " from AnimalTable a JOIN (" +
                "    SELECT idAnimal, count" +
                "    FROM animalcounttable" +
                "    WHERE id IN (" +
                "        SELECT MAX(id)" +
                "        FROM animalcounttable " +
                "    GROUP by idAnimal)" +
                ") t ON a.id = t.idAnimal Where a.idPT=:id and DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisCountAnimalNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    @Query("SELECT Buyer As buyer, COALESCE(SUM(PRICE),0) AS resultPrice, COALESCE(SUM(discSale),0) AS resultCount, suffix from MyFermaSale Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY buyer ORDER BY resultCount DESC Limit 3")
    fun getAnalysisSaleBuyerNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query("SELECT title As buyer, COALESCE(SUM(disc),0) AS resultPrice, 0 AS resultCount, suffix from MyFerma Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY title ORDER BY resultPrice DESC Limit 3")
    fun getAnalysisAddProductNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query("SELECT titleSale As buyer, COALESCE(SUM(PRICE),0) AS resultPrice, COALESCE(SUM(discSale),0) AS resultCount, suffix from MyFermaSale Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY titleSale ORDER BY resultPrice DESC Limit 3")
    fun getAnalysisSaleProductNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query("SELECT titleEXPENSES As buyer, COALESCE(SUM(countEXPENSES), 0) AS resultPrice,  COALESCE(SUM(discEXPENSES),0) AS resultCount, suffix from MyFermaEXPENSES Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY buyer ORDER BY resultPrice DESC Limit 3")
    fun getAnalysisExpensesProductNewYearProject(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>


    // NewYear
    @Query("SELECT COALESCE(SUM(PRICE), 0) AS priceAll from MyFermaSale Where DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisSaleNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT COALESCE((SELECT SUM(countEXPENSES) FROM MyFermaEXPENSES WHERE DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0) +" +
                " COALESCE((SELECT SUM(price) FROM AnimalTable WHERE DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)), 0) " +
                "AS PriceDifference"
    )
    fun getAnalysisExpensesNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF Where statusWRITEOFF=0 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffOwnNeedsNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT COALESCE(SUM(priceAll), 0) AS priceAll from MyFermaWRITEOFF Where statusWRITEOFF=1 AND DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getAnalysisWriteOffScrapNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query("SELECT Buyer As buyer, COALESCE(SUM(PRICE),0) AS resultPrice, COALESCE(SUM(discSale),0) AS resultCount, suffix from MyFermaSale Where DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY buyer ORDER BY resultCount DESC Limit 3")
    fun getAnalysisSaleBuyerNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query("SELECT title As buyer, COALESCE(SUM(disc),0) AS resultPrice, 0 AS resultCount, suffix from MyFerma Where DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY title ORDER BY resultPrice DESC Limit 3")
    fun getAnalysisAddProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query("SELECT titleSale As buyer, COALESCE(SUM(PRICE),0) AS resultPrice, COALESCE(SUM(discSale),0) AS resultCount, suffix from MyFermaSale Where DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY titleSale ORDER BY resultPrice DESC Limit 3")
    fun getAnalysisSaleProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query("SELECT titleEXPENSES As buyer, COALESCE(SUM(countEXPENSES), 0) AS resultPrice,  COALESCE(SUM(discEXPENSES),0) AS resultCount, suffix from MyFermaEXPENSES Where DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY buyer ORDER BY resultPrice DESC Limit 3")
    fun getAnalysisExpensesProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>

    @Query(
        "SELECT  COALESCE(SUM(t.count), 0) as countAnimal" +
                " from AnimalTable a JOIN (" +
                "    SELECT idAnimal, count" +
                "    FROM animalcounttable" +
                "    WHERE id IN (" +
                "        SELECT MAX(id)" +
                "        FROM animalcounttable " +
                "    GROUP by idAnimal)" +
                ") t ON a.id = t.idAnimal Where DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisCountAnimalNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    @Query("SELECT COUNT(*) from МyINCUBATOR Where mode = 0 and DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getIncubatorCountNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    @Query("SELECT COALESCE(SUM(EGGALL), 0) AS resultPrice from МyINCUBATOR Where mode = 0 and DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getEggInIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    @Query("SELECT COALESCE(SUM(EGGALLEND), 0) AS resultPrice from МyINCUBATOR Where mode = 0 and DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getChikenInIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    @Query("SELECT count(DISTINCT TYPE) from МyINCUBATOR Where mode = 0 and DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)")
    fun getTypeIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<String>

    @Query("SELECT name as title, COALESCE((SELECT SUM(PRICE) FROM MyFermaSale WHERE DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY idPT ), 0) +" +
            " COALESCE((SELECT SUM(priceAll) FROM MyFermaWRITEOFF WHERE DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY idPT), 0) -" +
            " COALESCE((SELECT SUM(countEXPENSES) FROM MyFermaEXPENSES WHERE DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)  GROUP BY idPT), 0) - " +
            " COALESCE((SELECT SUM(price) FROM AnimalTable WHERE DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY idPT), 0) - " +
            " COALESCE((SELECT SUM(priceAll) FROM MyFermaWRITEOFF WHERE DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY idPT), 0)" +
            " AS priceAll FROM МyINCUBATOR Where mode = 1 and DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)  GROUP BY name ")
    fun getBestProjectNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

//    @Query("SELECT i.name as title, COALESCE(SUM(pd1.price), 0) - COALESCE(SUM(pd_sub.price), 0) AS priceAll " +
//            " FROM МyINCUBATOR i" +
//            " LEFT JOIN" +
//            " (SELECT idPT, SUM(PRICE) as price FROM MyFermaSale WHERE DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
//            " UNION ALL" +
//            " SELECT idPT, SUM(priceAll) as price  FROM MyFermaWRITEOFF WHERE statusWRITEOFF = 0 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)) pd1" +
//            " ON i._id = pd1.idPT" +
//            " LEFT JOIN" +
//            " (SELECT idPT, SUM(countEXPENSES) as price  FROM MyFermaEXPENSES WHERE DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
//            " UNION ALL" +
//            " SELECT idPT, SUM(price) as price  FROM AnimalTable WHERE DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
//            " UNION ALL" +
//            " SELECT idPT, SUM(priceAll) as price  FROM MyFermaWRITEOFF WHERE statusWRITEOFF = 1 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
//            ") pd_sub ON i._id = pd_sub.idPT" +
//            " Where i.mode = 1 and DATE(printf('%04d-%02d-%02d', substr(i.data, 7, 4), substr(i.data, 4, 2), substr(i.data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)  GROUP BY i._id,i.NAME ORDER BY priceAll DESC LIMIT 1 ")
//    fun getBestProjectNewYear(
//        dateBegin: String,
//        dateEnd: String
//    ): Flow<Fin>

//    @Query(
//        "SELECT idPT, COALESCE(SUM(SaleCount), 0) + COALESCE(SUM(WriteOffCount0), 0) - COALESCE(SUM(ExpensesCount), 0) - COALESCE(SUM(WriteOffCount0), 0) - COALESCE(SUM(AnimalCount), 0)  AS ResultCount" +
//                " FROM (" +
//                "    SELECT idPT, SUM(PRICE) AS SaleCount, 0 AS ExpensesCount, 0 AS WriteOffCount0, 0 AS WriteOffCount1,  0 AS AnimalCount" +
//                "    FROM MyFermaSale s" +
//                "    Where DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
//                "    GROUP BY idPT" +
//                "    Join  +
//                "    SELECT  idPT, 0 AS SaleCount, SUM(countEXPENSES) AS ExpensesCount, 0 AS WriteOffCount0, 0 AS WriteOffCount1, 0 AS AnimalCount" +
//                "    FROM MyFermaEXPENSES e" +
//                "    Where DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
//                "    GROUP BY idPT" +
//                "    UNION ALL" +
//                "    SELECT  idPT, 0 AS SaleCount, 0 AS ExpensesCount, SUM(priceAll) AS WriteOffCount0, 0 AS WriteOffCount1, 0 AS AnimalCount" +
//                "    FROM MyFermaWRITEOFF w0" +
//                "    where statusWRITEOFF = 0 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
//                "    GROUP BY idPT " +
//                "    UNION ALL" +
//                "    SELECT  idPT,  0 AS SaleCount, 0 AS ExpensesCount, 0 AS WriteOffCount, SUM(priceAll) AS WriteOffCount1, 0 AS AnimalCount" +
//                "    FROM MyFermaWRITEOFF w1" +
//                "    where statusWRITEOFF = 1 and DATE(printf('%04d-%02d-%02d', year, mount, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
//                "    GROUP BY idPT " +
//                "    UNION ALL" +
//                "    SELECT  idPT, 0 AS SaleCount, 0 AS ExpensesCount, 0 AS WriteOffCount, 0 AS WriteOffCount1, SUM(price) AS AnimalCount" +
//                "    FROM AnimalTable a" +
//                "    where DATE(printf('%04d-%02d-%02d', substr(data, 7, 4), substr(data, 4, 2), substr(data, 1, 2))) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) " +
//                "    GROUP BY idPT " +
//                ")" +
//                " ORDER BY ResultCount DESC ")
//    fun getBestProjectNewYear(
//        dateBegin: String,
//        dateEnd: String
//    ): Flow<Fin>


}
