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

import android.content.Context
import com.zaroslikov.fermacompose2.data.Alarm.AlarmMangerRepository
import com.zaroslikov.fermacompose2.data.Alarm.AlarmRepository
import com.zaroslikov.fermacompose2.data.water.WaterRepository
import com.zaroslikov.fermacompose2.data.water.WorkManagerWaterRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val itemsRepository: ItemsRepository
    val waterRepository : WaterRepository
    val alarmRepositiry: AlarmRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(InventoryDatabase.getDatabase(context).itemDao())
    }

    override val waterRepository = WorkManagerWaterRepository(context)

    override val alarmRepositiry: AlarmRepository = AlarmMangerRepository(context)
}

