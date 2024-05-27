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
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
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
    fun getItemsAdd(id: Int): Flow<AddTable>



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
}
