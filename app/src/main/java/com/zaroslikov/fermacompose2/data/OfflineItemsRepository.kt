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
import kotlinx.coroutines.flow.Flow

class OfflineItemsRepository(private val itemDao: ItemDao) : ItemsRepository {
    override fun getAllItemsStream(id: Int): Flow<List<AddTable>> = itemDao.getAllItems(id)

    override fun getItemStream(id: Int): Flow<AddTable?> = itemDao.getItem(id)
    override fun getAllProject(): Flow<List<ProjectTable>> = itemDao.getAllProject()
    override fun getItemAdd(id: Int): Flow<AddTable> =itemDao.getItemAdd(id)
    override fun getItemsTitleAddList(id: Int): Flow<List<String>>  = itemDao.getItemsTitleAddList(id)
    override fun getItemsCategoryAddList(id: Int): Flow<List<String>> = itemDao.getItemsCategoryAddList(id)
    override fun getItemsAnimalAddList(id: Int): Flow<List<String>> = itemDao.getItemsAnimalAddList(id)

    override suspend fun insertProject(projectTable: ProjectTable) = itemDao.insertProject(projectTable)

    override suspend fun insertItem(item: AddTable) = itemDao.insert(item)

    override suspend fun deleteItem(item: AddTable) = itemDao.delete(item)

    override suspend fun updateItem(item: AddTable) = itemDao.update(item)
    override fun getAllSaleItems(id: Int): Flow<List<SaleTable>>  = itemDao.getAllSaleItems(id)

    override fun getItemSale(id: Int): Flow<SaleTable> = itemDao.getItemSale(id)

    override fun getItemsTitleSaleList(id: Int): Flow<List<String>> = itemDao.getItemsTitleSaleList(id)

    override fun getItemsCategorySaleList(id: Int): Flow<List<String>> = itemDao.getItemsCategorySaleList(id)

    override fun getItemsAnimalSaleList(id: Int): Flow<List<String>> = itemDao.getItemsAnimalSaleList(id)

    override fun getItemsBuyerSaleList(id: Int): Flow<List<String>> = itemDao.getItemsBuyerSaleList(id)
    override suspend fun insertSale(item: SaleTable) = itemDao.insertSale(item)

    override suspend fun updateSale(item: SaleTable)= itemDao.updateSale(item)

    override suspend fun deleteSale(item: SaleTable) = itemDao.deleteSale(item)
    override fun getAllExpensesItems(id: Int): Flow<List<ExpensesTable>> = itemDao.getAllExpensesItems(id)

    override fun getItemExpenses(id: Int): Flow<ExpensesTable> = itemDao.getItemExpenses(id)

    override fun getItemsTitleExpensesList(id: Int): Flow<List<String>> = itemDao.getItemsTitleExpensesList(id)

    override fun getItemsCategoryExpensesList(id: Int): Flow<List<String>> = itemDao.getItemsCategoryExpensesList(id)

    override suspend fun insertExpenses(item: ExpensesTable) = itemDao.insertExpenses(item)
    override suspend fun updateExpenses(item: ExpensesTable) = itemDao.updateExpenses(item)
    override suspend fun deleteExpenses(item: ExpensesTable) = itemDao.deleteExpenses(item)
}
