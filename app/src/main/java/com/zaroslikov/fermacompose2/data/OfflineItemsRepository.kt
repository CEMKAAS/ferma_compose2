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

class OfflineItemsRepository(private val itemDao: ItemDao) : ItemsRepository {
    override fun getAllItemsStream(id: Int): Flow<List<AddTable>> = itemDao.getAllItems(id)

    override fun getItemStream(id: Int): Flow<AddTable?> = itemDao.getItem(id)
    override fun getAllProject(): Flow<List<ProjectTable>> = itemDao.getAllProject()
    override fun getProject(id: Int): Flow<ProjectTable> = itemDao.getProject(id)
    override suspend fun updateProject(item: ProjectTable) = itemDao.updateProject(item)

    override fun getLastProject(): Flow<Int> = itemDao.getLastProject()

    override fun getIncubatorTemp(id: Int): Flow<IncubatorUIList> = itemDao.getIncubatorTemp(id)
    override fun getIncubatorDamp(id: Int): Flow<IncubatorUIList> = itemDao.getIncubatorDamp(id)
    override fun getIncubatorOver(id: Int): Flow<IncubatorUIList> = itemDao.getIncubatorOver(id)
    override fun getIncubatorAiring(id: Int): Flow<IncubatorUIList> = itemDao.getIncubatorAiring(id)


    override fun getItemAdd(id: Int): Flow<AddTable> = itemDao.getItemAdd(id)
    override fun getItemsTitleAddList(id: Int): Flow<List<String>> =
        itemDao.getItemsTitleAddList(id)

    override fun getItemsCategoryAddList(id: Int): Flow<List<String>> =
        itemDao.getItemsCategoryAddList(id)

    override fun getItemsAnimalAddList(id: Int): Flow<List<String>> =
        itemDao.getItemsAnimalAddList(id)

    override suspend fun insertProject(projectTable: ProjectTable) =
        itemDao.insertProject(projectTable)

    override suspend fun insertItem(item: AddTable) = itemDao.insert(item)

    override suspend fun deleteItem(item: AddTable) = itemDao.delete(item)

    override suspend fun updateItem(item: AddTable) = itemDao.update(item)

    //Sale
    override fun getAllSaleItems(id: Int): Flow<List<SaleTable>> = itemDao.getAllSaleItems(id)

    override fun getItemSale(id: Int): Flow<SaleTable> = itemDao.getItemSale(id)

    override fun getItemsTitleSaleList(id: Int): Flow<List<String>> =
        itemDao.getItemsTitleSaleList(id)

    override fun getItemsCategorySaleList(id: Int): Flow<List<String>> =
        itemDao.getItemsCategorySaleList(id)

    override fun getItemsBuyerSaleList(id: Int): Flow<List<String>> =
        itemDao.getItemsBuyerSaleList(id)

    override suspend fun insertSale(item: SaleTable) = itemDao.insertSale(item)

    override suspend fun updateSale(item: SaleTable) = itemDao.updateSale(item)

    override suspend fun deleteSale(item: SaleTable) = itemDao.deleteSale(item)

    //Expenses
    override fun getAllExpensesItems(id: Int): Flow<List<ExpensesTable>> =
        itemDao.getAllExpensesItems(id)

    override fun getItemExpenses(id: Int): Flow<ExpensesTable> = itemDao.getItemExpenses(id)

    override fun getItemsTitleExpensesList(id: Int): Flow<List<String>> =
        itemDao.getItemsTitleExpensesList(id)

    override fun getItemsCategoryExpensesList(id: Int): Flow<List<String>> =
        itemDao.getItemsCategoryExpensesList(id)

    override suspend fun insertExpenses(item: ExpensesTable) = itemDao.insertExpenses(item)
    override suspend fun updateExpenses(item: ExpensesTable) = itemDao.updateExpenses(item)
    override suspend fun deleteExpenses(item: ExpensesTable) = itemDao.deleteExpenses(item)

    //WriteOff
    override fun getAllWriteOffItems(id: Int): Flow<List<WriteOffTable>> =
        itemDao.getAllWriteOffItems(id)

    override fun getItemWriteOff(id: Int): Flow<WriteOffTable> = itemDao.getItemWriteOff(id)

    override suspend fun insertWriteOff(item: WriteOffTable) = itemDao.insertWriteOff(item)
    override suspend fun updateWriteOff(item: WriteOffTable) = itemDao.updateWriteOff(item)
    override suspend fun deleteWriteOff(item: WriteOffTable) = itemDao.deleteWriteOff(item)

    //Finance
    override fun getCurrentBalance(id: Int): Flow<Double> = itemDao.getCurrentBalance(id)
    override fun getIncome(id: Int): Flow<Double> = itemDao.getIncome(id)
    override fun getExpenses(id: Int): Flow<Double> = itemDao.getExpenses(id)
    override fun getCategoryIncomeCurrentMonth(id: Int, mount: Int, year: Int): Flow<List<Fin>> =
        itemDao.getCategoryIncomeCurrentMonth(id, mount, year)

    override fun getCategoryExpensesCurrentMonth(id: Int, mount: Int, year: Int): Flow<List<Fin>> =
        itemDao.getCategoryExpensesCurrentMonth(id, mount, year)

    override fun getIncomeExpensesCurrentMonth(
        id: Int,
        mount: Int,
        year: Int
    ): Flow<List<IncomeExpensesDetails>> = itemDao.getIncomeExpensesCurrentMonth(id, mount, year)


    //FinanceTap
    override fun getIncomeAllList(id: Int): Flow<List<FinTit>> = itemDao.getIncomeAllList(id)
    override fun getExpensesAllList(id: Int): Flow<List<FinTit>> = itemDao.getExpensesAllList(id)
    override fun getIncomeCategoryAllList(id: Int): Flow<List<Fin>> =
        itemDao.getIncomeCategoryAllList(id)

    override fun getExpensesCategoryAllList(id: Int): Flow<List<Fin>> =
        itemDao.getExpensesCategoryAllList(id)

    override fun getProductListCategoryIncomeCurrentMonth(
        id: Int,
        mount: Int,
        year: Int,
        category: String
    ): Flow<List<FinTit>> =
        itemDao.getProductListCategoryIncomeCurrentMonth(id, mount, year, category)

    override fun getProductLisCategoryExpensesCurrentMonth(
        id: Int,
        mount: Int,
        year: Int,
        category: String
    ): Flow<List<FinTit>> =
        itemDao.getProductLisCategoryExpensesCurrentMonth(id, mount, year, category)

    override fun getCurrentBalanceWarehouse(id: Int): Flow<List<WarehouseData>> =
        itemDao.getCurrentBalanceWarehouse(id)

    override suspend fun insertIncubatorTemp(item: IncubatorTemp) =
        itemDao.insertIncubatorTemp(item)

    override suspend fun insertIncubatorDamp(item: IncubatorDamp) =
        itemDao.insertIncubatorDamp(item)

    override suspend fun insertIncubatorAiring(item: IncubatorAiring) =
        itemDao.insertIncubatorAiring(item)

    override suspend fun insertIncubatorOver(item: IncubatorOver) =
        itemDao.insertIncubatorOver(item)

    override suspend fun updateIncubatorTemp(item: IncubatorTemp) = itemDao.updateIncubatorTemp(item)

    override suspend fun updateIncubatorDamp(item: IncubatorDamp) = itemDao.updateIncubatorDamp(item)

    override suspend fun updateIncubatorAiring(item: IncubatorAiring) = itemDao.updateIncubatorAiring(item)
    override suspend fun updateIncubatorOver(item: IncubatorOver) = itemDao.updateIncubatorOver(item)
}
