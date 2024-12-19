package com.zaroslikov.fermacompose2

import android.Manifest
import android.content.pm.PackageManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ProcessLifecycleOwner
import com.yandex.mobile.ads.appopenad.AppOpenAd
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import com.zaroslikov.fermacompose2.data.water.WorkManagerWaterRepository
import com.zaroslikov.fermacompose2.ui.add.incubator.AlertDialogExample
import com.zaroslikov.fermacompose2.ui.theme.FermaCompose2Theme


class MainActivity : ComponentActivity() {

    companion object {
        private var TAG = "MainActivity"
        const val REQUEST_CODE_NOTIFICATION_PERMISSIONS = 11
    }

    private var appOpenAd: AppOpenAd? = null
    private var isAdShownOnColdStart = false

    private var interstitialAd: InterstitialAd? = null
    private var interstitialAdLoader: InterstitialAdLoader? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val startBoolean = isFirstLaunch(this)

        if (startBoolean) {
            WorkManagerWaterRepository(this).setupDailyReminder()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) getNotificationPermissions()
        }


        setContent {

            FermaCompose2Theme {
                MobileAds.initialize(this) {

                    loadAppOpenAd()
                    val processLifecycleObserver = DefaultProcessLifecycleObserver(
                        onProcessCaneForeground = ::showAppOpenAd
                    )
                    ProcessLifecycleOwner.get().lifecycle.addObserver(processLifecycleObserver)


                    //Межстра
//                    interstitialAdLoader = InterstitialAdLoader(this).apply {
//                        setAdLoadListener(object : InterstitialAdLoadListener {
//                            override fun onAdLoaded(ad: InterstitialAd) {
//                                interstitialAd = ad
//                                // The ad was loaded successfully. Now you can show loaded ad.
//                            }
//
//                            override fun onAdFailedToLoad(adRequestError: AdRequestError) {
//                                // Ad failed to load with AdRequestError.
//                                // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
//                            }
//                        })
//                    }
//                    loadInterstitialAd()
                }
                var openFirstDialog by rememberSaveable { mutableStateOf(startBoolean) }

                InventoryApp(
                    modifier = Modifier,
                    isFirstStart = openFirstDialog,
                    isFirstEnd = { openFirstDialog = false })

            }

        }
    }


    private fun getNotificationPermissions() {
        try {
            val hasAccessNotificationPolicyPermission =
                checkSelfPermission(Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED
            val hasPostNotificationsPermission =
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            when {
                !hasAccessNotificationPolicyPermission || !hasPostNotificationsPermission -> {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                            Manifest.permission.POST_NOTIFICATIONS
                        ), REQUEST_CODE_NOTIFICATION_PERMISSIONS
                    )
                }

                else -> Log.d(TAG, "Notification Permissions : previously granted successfully")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE_NOTIFICATION_PERMISSIONS -> {
                val hasAccessNotificationPolicyPermission =
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                val hasPostNotificationsPermission =
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                when {
                    !hasAccessNotificationPolicyPermission || !hasPostNotificationsPermission -> {
                        getNotificationPermissions()
                    }

                    else -> {
                        Log.d(TAG, "Notification Permissions : Granted successfully")
                    }
                }
            }
        }
    }

    //Реклама при клике
    private fun loadInterstitialAd() {
        val adRequestConfiguration =
            AdRequestConfiguration.Builder("R-M-12224806-2").build()
        interstitialAdLoader?.loadAd(adRequestConfiguration)
    }

    fun showAd() {
        interstitialAd?.apply {
            setAdEventListener(object : InterstitialAdEventListener {
                override fun onAdShown() {}
                override fun onAdFailedToShow(adError: AdError) {
                    destroyInterstitialAd()
                    loadInterstitialAd()
                }

                override fun onAdDismissed() {
                    destroyInterstitialAd()
                    loadInterstitialAd()
                }

                override fun onAdClicked() {}
                override fun onAdImpression(impressionData: ImpressionData?) {}
            })
            show(this@MainActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        interstitialAdLoader?.setAdLoadListener(null)
        interstitialAdLoader = null
        destroyInterstitialAd()
    }

    private fun destroyInterstitialAd() {
        interstitialAd?.setAdEventListener(null)
        interstitialAd = null
    }

    //Реклама при хапуске приложения
    private fun showAppOpenAd() {
        val appOpenAdEventListener = AdEventListener()
        appOpenAd?.setAdEventListener(appOpenAdEventListener)
        appOpenAd?.show(this@MainActivity)
    }

    private inner class AdEventListener : AppOpenAdEventListener {
        override fun onAdShown() {}
        override fun onAdFailedToShow(adError: AdError) {
            clearAppOpenAd()
            loadAppOpenAd()
        }

        override fun onAdDismissed() {
            clearAppOpenAd()
            loadAppOpenAd()
        }

        override fun onAdClicked() {}
        override fun onAdImpression(impressionData: ImpressionData?) {}
    }


    private fun clearAppOpenAd() {
        appOpenAd?.setAdEventListener(null)
        appOpenAd = null
    }

    private fun loadAppOpenAd() {
        val appOpenAdLoader = AppOpenAdLoader(application)
        val appOpenAdLoadListener = object : AppOpenAdLoadListener {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                // The ad was loaded successfully. Now you can show loaded ad.
                this@MainActivity.appOpenAd = appOpenAd
                if (!isAdShownOnColdStart) {
                    showAppOpenAd()
                    isAdShownOnColdStart = true
                }
            }

            override fun onAdFailedToLoad(adRequestError: AdRequestError) {}
        }
        appOpenAdLoader.setAdLoadListener(appOpenAdLoadListener)

        val AD_UNIT_ID = "R-M-12224806-2"
        val adRequestConfiguration = AdRequestConfiguration.Builder(AD_UNIT_ID).build()
        appOpenAdLoader.loadAd(adRequestConfiguration)
        appOpenAdLoader.loadAd(adRequestConfiguration)
    }

}

// Функция для проверки первого запуска приложения
fun isFirstLaunch(context: Context): Boolean {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)


    if (isFirstLaunch) {
        sharedPreferences.edit().putBoolean("is_first_launch", false).apply()
        sharedPreferences.edit().putString("app_notifications", "20:00").apply()
    }

    return isFirstLaunch
}

@Composable
fun AlterDialigStart(
    isFirstStart: Boolean,
    dialogTitle: String,
    dialogText: String,
    isFirstEndConfig: () -> Unit ={},
) {
    var openFirstDialog by rememberSaveable { mutableStateOf(isFirstStart) }

    if (openFirstDialog) {
        AlertDialog(
            icon = {
                Icon(Icons.Default.Info, contentDescription = "Example Icon")
            },
            title = {
                Text(text = dialogTitle)
            },
            text = {
                Text(text = dialogText, textAlign = TextAlign.Justify)
            },
            onDismissRequest = {
                openFirstDialog = false
            },
            confirmButton = {
                TextButton(onClick = {
                    isFirstEndConfig()
                    openFirstDialog = false
                }) { Text("Отлично!") }
            }
        )
    }
}

