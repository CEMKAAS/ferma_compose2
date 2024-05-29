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
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
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
}
