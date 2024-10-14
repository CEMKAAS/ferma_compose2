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

import androidx.room.Query
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalSizeTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.data.animal.AnimalWeightTable
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.ui.animal.AnimalIndicatorsVM
import com.zaroslikov.fermacompose2.ui.animal.AnimalTitSuff
import com.zaroslikov.fermacompose2.ui.finance.AnalysisSaleBuyerAllTime
import com.zaroslikov.fermacompose2.ui.finance.Fin
import com.zaroslikov.fermacompose2.ui.finance.FinanceAnalysisViewModel
import com.zaroslikov.fermacompose2.ui.finance.IncomeExpensesDetails
import com.zaroslikov.fermacompose2.ui.warehouse.WarehouseData
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface ItemsRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllItemsStream(id: Int): Flow<List<AddTable>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: Int): Flow<AddTable?>

    fun getIncubatorListArh(type: String): Flow<List<ProjectTable>>

    fun getIncubatorListArh2(type: String): Flow<Int>

    fun getAllProject(): Flow<List<ProjectTable>>
    fun getAllProjectArh(): Flow<List<ProjectTable>>

    fun getAllProjectAct(): Flow<List<ProjectTable>>
    fun getProject(id: Int): Flow<ProjectTable>
    suspend fun updateProject(item: ProjectTable)
    suspend fun deleteProject(item: ProjectTable)

    fun getLastProject(): Flow<Int>

    fun getCountRowIncubator(): Flow<Int>
    fun getCountRowProject(): Flow<Int>

    fun getProjectListAct(): Flow<List<ProjectTable>>
    fun getIncubatorList(id: Int): Flow<List<Incubator>>

    suspend fun getIncubatorList2(id: Int): List<Incubator>
    fun getIncubator(id: Int): Flow<Incubator>
    fun getIncubatorEditDay(id: Int, day:Int): Flow<Incubator>

    fun getItemAdd(id: Int): Flow<AddTable>

    fun getItemsTitleAddList(id: Int): Flow<List<String>>

    fun getItemsCategoryAddList(id: Int): Flow<List<String>>
    fun getItemsAnimalAddList(id: Int): Flow<List<Pair<String,String>>>

    suspend fun insertProject(projectTable: ProjectTable)
    suspend fun insertProjectLong(projectTable: ProjectTable): Long

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: AddTable)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: AddTable)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: AddTable)


    fun getAllSaleItems(id: Int): Flow<List<SaleTable>>
    fun getItemSale(id: Int): Flow<SaleTable>
    fun getItemsTitleSaleList(id: Int): Flow<List<String>>
    fun getItemsCategorySaleList(id: Int): Flow<List<String>>
    fun getItemsBuyerSaleList(id: Int): Flow<List<String>>
    suspend fun insertSale(item: SaleTable)
    suspend fun updateSale(item: SaleTable)
    suspend fun deleteSale(item: SaleTable)

    //Expenses
    fun getAllExpensesItems(id: Int): Flow<List<ExpensesTable>>

    fun getItemExpenses(id: Int): Flow<ExpensesTable>

    fun getItemsTitleExpensesList(id: Int): Flow<List<String>>

    fun getItemsCategoryExpensesList(id: Int): Flow<List<String>>

    suspend fun insertExpenses(item: ExpensesTable)

    suspend fun updateExpenses(item: ExpensesTable)

    suspend fun deleteExpenses(item: ExpensesTable)

    //WriteOff
    fun getAllWriteOffItems(id: Int): Flow<List<WriteOffTable>>
    fun getItemWriteOff(id: Int): Flow<WriteOffTable>

    suspend fun insertWriteOff(item: WriteOffTable)
    suspend fun updateWriteOff(item: WriteOffTable)
    suspend fun deleteWriteOff(item: WriteOffTable)

    //Finance
    fun getCurrentBalance(id: Int): Flow<Double>
    fun getIncome(id: Int): Flow<Double>
    fun getExpenses(id: Int): Flow<Double>
    fun getOwnNeed(id: Int): Flow<Double>
    fun getScrap(id: Int): Flow<Double>

    fun getIncomeMountFin(id: Int, mount: Int, year: Int): Flow<Double>
    fun getExpensesMountFin(id: Int, mount: Int, year: Int): Flow<Double>


    fun getIncomeMount(id: Int, dateBegin: String, dateEnd: String): Flow<Double>
    fun getExpensesMount(id: Int, dateBegin: String, dateEnd: String): Flow<Double>
    fun getOwnNeedMonth(id: Int, dateBegin:String, dateEnd: String): Flow<Double>
    fun getScrapMonth(id: Int, dateBegin:String, dateEnd: String): Flow<Double>

    fun getCategoryIncomeCurrentMonth(id: Int, dateBegin: String, dateEnd: String): Flow<List<Fin>>
    fun getCategoryExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<Fin>>

    fun getIncomeExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<IncomeExpensesDetails>>

//    fun getIncomeMount(id: Int, mount: Int, year: Int): Flow<Double>
//    fun getExpensesMount(id: Int, mount: Int, year: Int): Flow<Double>
//
//    fun getCategoryIncomeCurrentMonth(id: Int, mount: Int, year: Int): Flow<List<Fin>>
//    fun getCategoryExpensesCurrentMonth(id: Int, mount: Int, year: Int): Flow<List<Fin>>
//    fun getIncomeExpensesCurrentMonth(
//        id: Int,
//        mount: Int,
//        year: Int
//    ): Flow<List<IncomeExpensesDetails>>


    fun getIncomeAllList(id: Int): Flow<List<Fin>>
    fun getExpensesAllList(id: Int): Flow<List<Fin>>
    fun getIncomeCategoryAllList(id: Int): Flow<List<Fin>>
    fun getExpensesCategoryAllList(id: Int): Flow<List<Fin>>
    fun getProductListCategoryIncomeCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String,
        category: String
    ): Flow<List<Fin>>

    fun getProductLisCategoryExpensesCurrentMonth(
        id: Int,
        dateBegin: String,
        dateEnd: String,
        category: String
    ): Flow<List<Fin>>

    fun getCurrentBalanceWarehouse(id: Int): Flow<List<WarehouseData>>


    // Analysis
    fun getAnalysisAddAllTime(id: Int, name: String): Flow<Fin>
    fun getAnalysisSaleAllTime(id: Int, name: String): Flow<Fin>
    fun getAnalysisWriteOffAllTime(id: Int, name: String): Flow<Fin>
    fun getAnalysisWriteOffOwnNeedsAllTime(id: Int, name: String): Flow<Fin>
    fun getAnalysisWriteOffScrapAllTime(id: Int, name: String): Flow<Fin>
    fun getAnalysisSaleSoldAllTime(id: Int, name: String): Flow<Double>
    fun getAnalysisWriteOffOwnNeedsMoneyAllTime(id: Int, name: String): Flow<Double>
    fun getAnalysisWriteOffScrapMoneyAllTime(id: Int, name: String): Flow<Double>
    fun getAnalysisAddAverageValueAllTime(id: Int, name: String): Flow<Fin>
    fun getAnalysisAddAnimalAllTime(id: Int, name: String): Flow<List<AnimalTitSuff>>
    fun getAnalysisSaleBuyerAllTime(id: Int, name: String): Flow<List<AnalysisSaleBuyerAllTime>>

    //AnalisisRange
    fun getAnalysisAddAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    fun getAnalysisSaleAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    fun getAnalysisWriteOffAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    fun getAnalysisWriteOffOwnNeedsAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    fun getAnalysisWriteOffScrapAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    fun getAnalysisSaleSoldAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisWriteOffOwnNeedsMoneyAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisWriteOffScrapMoneyAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    fun getAnalysisAddAverageValueAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Fin>

    fun getAnalysisAddAnimalAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnimalTitSuff>>

    fun getAnalysisSaleBuyerAllTimeRange(
        id: Int,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>>


    suspend fun insertIncubator(item: Incubator)
    suspend fun updateIncubator(item: Incubator)
    fun getAllAnimal(id: Int): Flow<List<AnimalTable>>
    fun getAnimal(id: Int): Flow<AnimalTable>
    fun getTypeAnimal(id: Int): Flow<List<String>>


    suspend fun insertAnimalTable(animalTable: AnimalTable): Long

    suspend fun insertAnimalCountTable(animalCountTable: AnimalCountTable)

    suspend fun insertAnimalSizeTable(animalSizeTable: AnimalSizeTable)

    suspend fun insertAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable)

    suspend fun insertAnimalWeightTable(animalWeightTable: AnimalWeightTable)

    suspend fun updateAnimalCountTable(animalCountTable: AnimalCountTable)
    suspend fun updateAnimalSizeTable(animalSizeTable: AnimalSizeTable)
    suspend fun updateAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable)
    suspend fun updateAnimalWeightTable(animalWeightTable: AnimalWeightTable)


    suspend fun deleteAnimalCountTable(animalCountTable: AnimalCountTable)
    suspend fun deleteAnimalSizeTable(animalSizeTable: AnimalSizeTable)
    suspend fun deleteAnimalVaccinationTable(animalVaccinationTable: AnimalVaccinationTable)
    suspend fun deleteAnimalWeightTable(animalWeightTable: AnimalWeightTable)

    suspend fun updateAnimalTable(animalTable: AnimalTable)
    suspend fun deleteAnimalTable(animalTable: AnimalTable)

    fun getCountAnimalLimit(id: Int): Flow<List<AnimalCountTable>>
    fun getSizeAnimalLimit(id: Int): Flow<List<AnimalSizeTable>>
    fun getVaccinationtAnimalLimit(id: Int): Flow<List<AnimalVaccinationTable>>
    fun getWeightAnimalLimit(id: Int): Flow<List<AnimalWeightTable>>

    fun getCountAnimal(id: Int): Flow<List<AnimalIndicatorsVM>>
    fun getSizeAnimal(id: Int): Flow<List<AnimalIndicatorsVM>>
    fun getVaccinationtAnimal(id: Int): Flow<List<AnimalVaccinationTable>>
    fun getWeightAnimal(id: Int): Flow<List<AnimalIndicatorsVM>>
    fun getProductAnimal(name: String): Flow<List<AnimalTitSuff>>
}
