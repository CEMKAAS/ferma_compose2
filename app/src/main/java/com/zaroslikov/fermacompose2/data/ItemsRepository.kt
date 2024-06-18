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

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.data.incubator.IncubatorAiring
import com.zaroslikov.fermacompose2.data.incubator.IncubatorDamp
import com.zaroslikov.fermacompose2.data.incubator.IncubatorOver
import com.zaroslikov.fermacompose2.data.incubator.IncubatorTemp
import com.zaroslikov.fermacompose2.ui.finance.Fin
import com.zaroslikov.fermacompose2.ui.finance.FinTit
import com.zaroslikov.fermacompose2.ui.finance.IncomeExpensesDetails
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorUIList
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

    fun getAllProject(): Flow<List<ProjectTable>>
    fun getProject(id: Int): Flow<ProjectTable>
    suspend fun updateProject(item: ProjectTable)
    fun getLastProject(): Flow<Int>
    fun getIncubatorTemp(id: Int): Flow<IncubatorUIList>

    fun getIncubatorDamp(id: Int): Flow<IncubatorUIList>

    fun getIncubatorOver(id: Int): Flow<IncubatorUIList>

    fun getIncubatorAiring(id: Int): Flow<IncubatorUIList>

    fun getItemAdd(id: Int): Flow<AddTable>

    fun getItemsTitleAddList(id: Int): Flow<List<String>>

    fun getItemsCategoryAddList(id: Int): Flow<List<String>>
    fun getItemsAnimalAddList(id: Int): Flow<List<String>>

    suspend fun insertProject(projectTable: ProjectTable)

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
    fun getCategoryIncomeCurrentMonth(id: Int, mount: Int, year:Int): Flow<List<Fin>>
    fun getCategoryExpensesCurrentMonth(id: Int, mount: Int, year:Int): Flow<List<Fin>>
    fun getIncomeExpensesCurrentMonth(id: Int, mount: Int, year:Int): Flow<List<IncomeExpensesDetails>>


    fun getIncomeAllList(id: Int): Flow<List<FinTit>>
    fun getExpensesAllList(id: Int): Flow<List<FinTit>>
    fun getIncomeCategoryAllList(id: Int): Flow<List<Fin>>
    fun getExpensesCategoryAllList(id: Int): Flow<List<Fin>>
    fun getProductListCategoryIncomeCurrentMonth(id: Int, mount: Int, year:Int, category: String): Flow<List<FinTit>>
    fun getProductLisCategoryExpensesCurrentMonth(id: Int, mount: Int, year:Int, category: String): Flow<List<FinTit>>

    fun getCurrentBalanceWarehouse(id: Int): Flow<List<WarehouseData>>


    suspend fun insertIncubatorTemp(item: IncubatorTemp)


    suspend fun insertIncubatorDamp(item: IncubatorDamp)


    suspend fun insertIncubatorAiring(item: IncubatorAiring)



    suspend fun insertIncubatorOver(item: IncubatorOver)
    suspend fun updateIncubatorTemp(item: IncubatorTemp)
    suspend fun updateIncubatorDamp(item: IncubatorDamp)
    suspend fun updateIncubatorAiring(item: IncubatorAiring)
    suspend fun updateIncubatorOver(item: IncubatorOver)

}
