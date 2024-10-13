package com.zaroslikov.fermacompose2

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import com.zaroslikov.fermacompose2.ui.Banner
import com.zaroslikov.fermacompose2.ui.theme.FermaCompose2Theme
import java.util.Calendar
import java.util.Date


class MainActivity : ComponentActivity() {

    private var appOpenAd: AppOpenAd? = null
    private var isAdShownOnColdStart = false

    private var interstitialAd: InterstitialAd? = null
    private var interstitialAdLoader: InterstitialAdLoader? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                InventoryApp(modifier = Modifier)

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
                override fun onAdShown() {
                    // Called when ad is shown.
                }

                override fun onAdFailedToShow(adError: AdError) {
                    // Called when an InterstitialAd failed to show.
                    // Clean resources after Ad dismissed
                    destroyInterstitialAd()

                    // Now you can preload the next interstitial ad.
                    loadInterstitialAd()
                }

                override fun onAdDismissed() {
                    // Called when ad is dismissed.
                    // Clean resources after Ad dismissed
                    destroyInterstitialAd()

                    // Now you can preload the next interstitial ad.
                    loadInterstitialAd()
                }

                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                }

                override fun onAdImpression(impressionData: ImpressionData?) {
                    // Called when an impression is recorded for an ad.
                }
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
        override fun onAdShown() {
            // Called when ad is shown.
        }

        override fun onAdFailedToShow(adError: AdError) {
            // Called when ad failed to show.
            clearAppOpenAd()
            loadAppOpenAd()
        }

        override fun onAdDismissed() {
            // Called when ad is dismissed.
            // Clean resources after dismiss and preload new ad.
            clearAppOpenAd()
            loadAppOpenAd()
        }

        override fun onAdClicked() {
            // Called when a click is recorded for an ad.
        }

        override fun onAdImpression(impressionData: ImpressionData?) {
            // Called when an impression is recorded for an ad.
            // Get Impression Level Revenue Data in argument.
        }
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

            override fun onAdFailedToLoad(adRequestError: AdRequestError) {
                // Ad failed to load with AdRequestError.
                // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
            }
        }
        appOpenAdLoader.setAdLoadListener(appOpenAdLoadListener)

        val AD_UNIT_ID = "R-M-12224806-2"
        val adRequestConfiguration = AdRequestConfiguration.Builder(AD_UNIT_ID).build()
        appOpenAdLoader.loadAd(adRequestConfiguration)
        appOpenAdLoader.loadAd(adRequestConfiguration)
    }

    override fun onStart() {
        super.onStart()
        val manager = getSystemService(ALARM_SERVICE) as AlarmManager
        val dat = Date()
        val cal_now = Calendar.getInstance()
        cal_now.time = dat
        val cal_alarm = Calendar.getInstance()
        cal_alarm.time = dat
        cal_alarm[Calendar.HOUR_OF_DAY] = 20
        cal_alarm[Calendar.MINUTE] = 0
        cal_alarm[Calendar.SECOND] = 0
        if (cal_alarm.before(cal_now)) {
            cal_alarm.add(Calendar.DATE, 1)
        }
        val myIntent = Intent(this@MainActivity, AlarmReceiver::class.java)
        manager[AlarmManager.RTC_WAKEUP, cal_alarm.timeInMillis] =
            PendingIntent.getBroadcast(this@MainActivity, 0, myIntent, PendingIntent.FLAG_IMMUTABLE)
    }


}
