package com.zaroslikov.fermacompose2


import android.app.Application
import androidx.work.Configuration
import com.yandex.mobile.ads.common.MobileAds
import com.zaroslikov.fermacompose2.data.AppContainer
import com.zaroslikov.fermacompose2.data.AppDataContainer
import dagger.hilt.android.HiltAndroidApp
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig

@HiltAndroidApp
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

