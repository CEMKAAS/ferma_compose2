package com.zaroslikov.fermacompose2

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yandex.mobile.ads.appopenad.AppOpenAd
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadResult
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.compose.rememberAppOpenAdLoader
import com.zaroslikov.fermacompose2.ui.elements.CircularProgressWitchText
import com.zaroslikov.fermacompose2.ui.theme.FermaCompose2Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val action = intent?.action
        val projectId = intent?.getLongExtra("itemIdPT", -1L) ?: -1L
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.WHITE,
                darkScrim = android.graphics.Color.WHITE
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.WHITE,
                darkScrim = android.graphics.Color.WHITE
            )
        )
        setContent {
            FermaCompose2Theme {
                InventoryApp(action = action, projectId = projectId)
            }
        }
    }
}

@Composable
fun SplashScreen(
    innerPadding: PaddingValues,
    activity: Activity,
    onFinished: () -> Unit
) {

    var appOpenAd by remember { mutableStateOf<AppOpenAd?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val loader = rememberAppOpenAdLoader()
    val adUnitId = stringResource(R.string.yandex_first_launch_ads)

    LaunchedEffect(Unit) {
        val adRequest = AdRequest.Builder(adUnitId).build()

        when (val result = loader.loadAd(adRequest)) {
            is AppOpenAdLoadResult.Success -> {
                appOpenAd = result.ad
            }

            is AppOpenAdLoadResult.Failure -> {
                Log.e("YandexAds", result.error.description)
            }
        }

        isLoading = false
    }
    Box(
        modifier = Modifier.padding(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding()
        )
    ) {
        LaunchedEffect(isLoading, appOpenAd) {

            if (!isLoading) {

                appOpenAd?.apply {

                    setAdEventListener(object : AppOpenAdEventListener {

                        override fun onAdShown() {
                            Log.i("YandexAds", "Ad shown")
                        }

                        override fun onAdDismissed() {
                            Log.i("YandexAds", "Ad dismissed")

                            onFinished()
                        }

                        override fun onAdFailedToShow(error: AdError) {

                            Log.e(
                                "YandexAds",
                                "Show error: ${error.description}"
                            )

                            onFinished()
                        }

                        override fun onAdClicked() {}

                        override fun onAdImpression(impressionData: ImpressionData?) {}
                    })

                    show(activity)
                } ?: run {
                    onFinished()
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressWitchText(
                intRes = R.string.ads_support_text_load_ads
            )
        }
    }
}