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

package com.zaroslikov.fermacompose2


import android.app.Application
import androidx.work.Configuration
import com.yandex.mobile.ads.common.MobileAds
import com.zaroslikov.fermacompose2.data.AppContainer
import com.zaroslikov.fermacompose2.data.AppDataContainer
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig

class InventoryApplication: Application(), Configuration.Provider  {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        val config =
            AppMetricaConfig.newConfigBuilder("7bc20e66-fc56-4002-ac33-4cc15dd28213").build()
        AppMetrica.activate(this, config)
        MobileAds.initialize(this) { }
        container = AppDataContainer(this)
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setMinimumLoggingLevel(android.util.Log.INFO)
        .build()
    }

