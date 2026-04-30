package com.zaroslikov.fermacompose2

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.yandex.mobile.ads.appopenad.AppOpenAd
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadResult
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.YandexAds
import com.yandex.mobile.ads.compose.rememberAppOpenAdLoader
/*import com.yandex.mobile.ads.appopenad.AppOpenAd
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
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader*/
//import com.zaroslikov.fermacompose2.data.water.WorkManagerWaterRepository
//import com.zaroslikov.fermacompose2.ui.add.incubator.AlertDialogExample
import com.zaroslikov.fermacompose2.ui.theme.FermaCompose2Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private var TAG = "MainActivity"
        const val REQUEST_CODE_NOTIFICATION_PERMISSIONS = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent?.action
        val projectId = intent?.getLongExtra("itemIdPT", -1L) ?: -1L

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) getNotificationPermissions()


        setContent {
            FermaCompose2Theme {
                YandexAds.initialize(this) {}
                InventoryApp(action = action, projectId = projectId)
            }
        }
    }
}

@Composable
fun SplashScreen(activity: Activity) {
    var appOpenAd by remember { mutableStateOf<AppOpenAd?>(null) }

    val loader = rememberAppOpenAdLoader()
    val adUnitId = stringResource(R.string.yandex_first_launch_ads)
    // Загружаем сразу при входе в composable
    LaunchedEffect(Unit) {

        val adRequest = AdRequest.Builder(adUnitId).build()
        when (val result = loader.loadAd(adRequest)) {
            is AppOpenAdLoadResult.Success -> appOpenAd = result.ad
            is AppOpenAdLoadResult.Failure -> Log.e("YandexAds", result.error.description)
        }
    }

    LaunchedEffect(appOpenAd) {
        appOpenAd?.show(activity)
    }
}