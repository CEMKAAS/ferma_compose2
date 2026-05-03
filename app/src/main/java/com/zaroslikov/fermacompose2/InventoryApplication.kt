package com.zaroslikov.fermacompose2


import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.yandex.mobile.ads.common.YandexAds
import dagger.hilt.android.HiltAndroidApp
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import javax.inject.Inject

@HiltAndroidApp
class InventoryApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        val config =
            AppMetricaConfig.newConfigBuilder("7bc20e66-fc56-4002-ac33-4cc15dd28213").build()
        AppMetrica.activate(this, config)
        YandexAds.initialize(this) {}
//        container = AppDataContainer(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

