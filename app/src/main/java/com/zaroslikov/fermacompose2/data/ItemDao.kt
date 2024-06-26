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
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.data.ferma.IncubatorAiring
import com.zaroslikov.fermacompose2.data.ferma.IncubatorDamp
import com.zaroslikov.fermacompose2.data.ferma.IncubatorOver
import com.zaroslikov.fermacompose2.data.ferma.IncubatorTemp
import com.zaroslikov.fermacompose2.ui.animal.AnimalIndicatorsVM
import com.zaroslikov.fermacompose2.ui.animal.AnimalTitSuff
import com.zaroslikov.fermacompose2.ui.finance.Fin
import com.zaroslikov.fermacompose2.ui.finance.FinTit
import com.zaroslikov.fermacompose2.ui.finance.IncomeExpensesDetails
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorUIList
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseData
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
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

    @Query("SELECT * from МyINCUBATORTEMP2 Where idPT=:id")
    fun getIncubatorTemp2(id: Int): Flow<IncubatorTemp>

    @Query("SELECT * from МyINCUBATORTEMPDAMP Where idPT=:id")
    fun getIncubatorDamp2(id: Int): Flow<IncubatorDamp>

    @Query("SELECT * from МyINCUBATOROVER Where idPT=:id")
    fun getIncubatorOver2(id: Int): Flow<IncubatorOver>

    @Query("SELECT * from МyINCUBATORAIRING Where idPT=:id")
    fun getIncubatorAiring2(id: Int): Flow<IncubatorAiring>


    @Query("SELECT * from МyINCUBATORTEMP2 Where idPT=:id")
    fun getIncubatorTemp(id: Int): Flow<IncubatorUIList>

    @Query("SELECT * from МyINCUBATORTEMPDAMP Where idPT=:id")
    fun getIncubatorDamp(id: Int): Flow<IncubatorUIList>

    @Query("SELECT * from МyINCUBATOROVER Where idPT=:id")
    fun getIncubatorOver(id: Int): Flow<IncubatorUIList>

    @Query("SELECT * from МyINCUBATORAIRING Where idPT=:id")
    fun getIncubatorAiring(id: Int): Flow<IncubatorUIList>


    @Query("SELECT * from MyFerma WHERE _id = :id")
    fun getItem(id: Int): Flow<AddTable>


    @Query("SELECT * from MyFerma Where idPT=:id ORDER BY _id DESC")
    fun getAllItems(id: Int): Flow<List<AddTable>>

    @Query("SELECT * from MyFerma Where _id=:id")
    fun getItemAdd(id: Int): Flow<AddTable>

    @Query("SELECT MyFerma.Title from MyFerma Where idPT=:id group by MyFerma.Title ")
    fun getItemsTitleAddList(id: Int): Flow<List<String>>

    @Query("SELECT MyFerma.category from MyFerma Where idPT=:id group by MyFerma.category ")
    fun getItemsCategoryAddList(id: Int): Flow<List<String>>

    @Query("SELECT name from AnimalTable Where idPT=:id")
    fun getItemsAnimalAddList(id: Int): Flow<List<String>>


    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: AddTable)

    @Update
    suspend fun update(item: AddTable)

    @Delete
    suspend fun delete(item: AddTable)

    //Sale
    @Query("SELECT * from MyFermaSale Where idPT=:id ORDER BY _id DESC")
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
    @Query("SELECT * from MyFermaEXPENSES Where idPT=:id ORDER BY _id DESC")
    fun getAllExpensesItems(id: Int): Flow<List<ExpensesTable>>

    @Query("SELECT * from MyFermaEXPENSES Where _id=:id")
    fun getItemExpenses(id: Int): Flow<ExpensesTable>

    @Query("SELECT MyFermaEXPENSES.titleEXPENSES from MyFermaEXPENSES Where idPT=:id group by MyFermaEXPENSES.titleEXPENSES")
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
    @Query("SELECT * from MyFermaWRITEOFF Where idPT=:id ORDER BY _id DESC")
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
    @Query("SELECT COALESCE(SUM(MyFermaSale.PRICE), 0.0) - COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS ResultCount FROM MyFermaSale LEFT JOIN MyFermaEXPENSES ON MyFermaSale.idPT = MyFermaEXPENSES.idPT WHERE MyFermaSale.idPT =:id")
    fun getCurrentBalance(id: Int): Flow<Double>

    @Query("SELECT COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS ResultCount FROM MyFermaSale WHERE MyFermaSale.idPT =:id")
    fun getIncome(id: Int): Flow<Double>

    @Query("SELECT COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS ResultCount FROM MyFermaEXPENSES WHERE MyFermaEXPENSES.idPT =:id")
    fun getExpenses(id: Int): Flow<Double>

    @Query("SELECT MyFermaSale.category, COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS priceAll FROM MyFermaSale Where idPT=:id and mount=:mount and year=:year group by MyFermaSale.category ORDER BY MyFermaSale.PRICE DESC")
    fun getCategoryIncomeCurrentMonth(id: Int, mount: Int, year: Int): Flow<List<Fin>>

    @Query("SELECT MyFermaEXPENSES.category, COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS priceAll FROM MyFermaEXPENSES Where idPT=:id and mount=:mount and year=:year group by MyFermaEXPENSES.category ORDER BY MyFermaEXPENSES.countEXPENSES DESC")
    fun getCategoryExpensesCurrentMonth(id: Int, mount: Int, year: Int): Flow<List<Fin>>

    @Query(
        "SELECT titleSale as title, discSale as count, suffix, PRICE as priceAll, day, mount, year " +
                "from (SELECT MyFermaSale.titleSale, MyFermaSale.discSale, MyFermaSale.suffix, MyFermaSale.PRICE, MyFermaSale.day, MyFermaSale.mount, MyFermaSale.year From MyFermaSale" +
                " Where idPT=:id and mount=:mount and year=:year " +
                "UNION All " +
                "SELECT MyFermaEXPENSES.titleEXPENSES, MyFermaEXPENSES.discEXPENSES, MyFermaEXPENSES.suffix, -MyFermaEXPENSES.countEXPENSES AS minusPriceAll, MyFermaEXPENSES.day, MyFermaEXPENSES.mount, MyFermaEXPENSES.year from MyFermaEXPENSES" +
                " Where idPT=:id and mount=:mount and year=:year)" +
                " combined_table"
    )
    fun getIncomeExpensesCurrentMonth(
        id: Int,
        mount: Int,
        year: Int
    ): Flow<List<IncomeExpensesDetails>>

    @Query("SELECT MyFermaSale.titleSale as Title, COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS priceAll FROM MyFermaSale WHERE MyFermaSale.idPT =:id group by MyFermaSale.titleSale")
    fun getIncomeAllList(id: Int): Flow<List<FinTit>>

    @Query("SELECT MyFermaEXPENSES.titleEXPENSES as Title, COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS priceAll FROM MyFermaEXPENSES WHERE MyFermaEXPENSES.idPT =:id group by MyFermaEXPENSES.titleEXPENSES")
    fun getExpensesAllList(id: Int): Flow<List<FinTit>>

    @Query("SELECT MyFermaSale.category as category, COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS priceAll FROM MyFermaSale WHERE MyFermaSale.idPT =:id group by MyFermaSale.category")
    fun getIncomeCategoryAllList(id: Int): Flow<List<Fin>>

    @Query("SELECT MyFermaEXPENSES.category as category, COALESCE(SUM(MyFermaEXPENSES.countEXPENSES), 0.0) AS priceAll FROM MyFermaEXPENSES WHERE MyFermaEXPENSES.idPT =:id group by MyFermaEXPENSES.category")
    fun getExpensesCategoryAllList(id: Int): Flow<List<Fin>>

    @Query("SELECT MyFermaSale.titleSale as Title, COALESCE(SUM(MyFermaSale.PRICE), 0.0) AS priceAll FROM MyFermaSale Where idPT=:id and mount=:mount and year=:year and category=:category group by MyFermaSale.titleSale ORDER BY MyFermaSale.PRICE DESC")
    fun getProductListCategoryIncomeCurrentMonth(
        id: Int,
        mount: Int,
        year: Int,
        category: String
    ): Flow<List<FinTit>>

    @Query("SELECT MyFermaEXPENSES.titleEXPENSES as Title, COALESCE(SUM(MyFermaEXPENSES.discEXPENSES), 0.0) AS priceAll FROM MyFermaEXPENSES Where idPT=:id and mount=:mount and year=:year and category=:category group by MyFermaEXPENSES.titleEXPENSES ORDER BY MyFermaEXPENSES.countEXPENSES DESC")
    fun getProductLisCategoryExpensesCurrentMonth(
        id: Int,
        mount: Int,
        year: Int,
        category: String
    ): Flow<List<FinTit>>

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
                "GROUP BY Title ORDER BY ResultCount DESC"
    )
    fun getCurrentBalanceWarehouse(id: Int): Flow<List<WarehouseData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncubatorTemp(item: IncubatorTemp)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncubatorDamp(item: IncubatorDamp)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncubatorAiring(item: IncubatorAiring)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncubatorOver(item: IncubatorOver)

    @Update
    suspend fun updateIncubatorTemp(item: IncubatorTemp)

    @Update
    suspend fun updateIncubatorDamp(item: IncubatorDamp)

    @Update
    suspend fun updateIncubatorAiring(item: IncubatorAiring)

    @Update
    suspend fun updateIncubatorOver(item: IncubatorOver)

    @Query("SELECT * from AnimalTable Where idPT=:id")
    fun getAllAnimal(id: Int): Flow<List<AnimalTable>>

    @Query("SELECT * from AnimalTable Where id=:id")
    fun getAnimal(id: Int): Flow<AnimalTable>

    @Query("SELECT type from AnimalTable Where idPT=:id")
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

    @Query("SELECT title as Title,COALESCE(SUM(disc), 0.0) AS priceAll, suffix from MyFerma Where animal=:name GROUP BY Title ORDER BY priceAll DESC")
    fun getProductAnimal(name:String):Flow<List<AnimalTitSuff>>

}
