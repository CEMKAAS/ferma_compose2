package com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.main

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_1
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_13
import com.zaroslikov.fermacompose2.blue_14
import com.zaroslikov.fermacompose2.blue_20
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.blue_9
import com.zaroslikov.fermacompose2.error_base
import com.zaroslikov.fermacompose2.green_11
import com.zaroslikov.fermacompose2.green_9
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_2
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.price_green
import com.zaroslikov.fermacompose2.price_green_2
import com.zaroslikov.fermacompose2.red_14
import com.zaroslikov.fermacompose2.supportFun.toConvertZero
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BigButton
import com.zaroslikov.fermacompose2.ui.elements.BigColorCard
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CardFieldNew
import com.zaroslikov.fermacompose2.ui.elements.IconTransaction2
import com.zaroslikov.fermacompose2.ui.elements.InfoCard
import com.zaroslikov.fermacompose2.ui.elements.TextField.OutlinedNumberNew
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.text_20
import com.zaroslikov.fermacompose2.ui.elements.text_24
import com.zaroslikov.fermacompose2.ui.elements.сompositions.CircleShape
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard
import com.zaroslikov.fermacompose2.violet_13
import com.zaroslikov.fermacompose2.violet_14
import com.zaroslikov.fermacompose2.violet_3
import com.zaroslikov.fermacompose2.white

@Composable
fun OvoscopBottomSheet(
    /* currentDay: Int,*/
    isLantern: Boolean,
    ovoscopyState: OvoskopyState,
    onIntent: (BookmarkIntent) -> Unit
) {
    val hintList = listOf(
        R.string.bookmark_screen_hint_remember_1,
        R.string.bookmark_screen_hint_remember_2,
        R.string.bookmark_screen_hint_remember_3,
        R.string.bookmark_screen_hint_remember_4
    )
    val hintListTwo = listOf(
        R.string.bookmark_screen_hint_ovoscopy_1,
        R.string.bookmark_screen_hint_ovoscopy_2,
        R.string.bookmark_screen_hint_ovoscopy_3,
        R.string.bookmark_screen_hint_ovoscopy_4,
        R.string.bookmark_screen_hint_ovoscopy_5,
        R.string.bookmark_screen_hint_ovoscopy_6
    )
    BaseBottomSheet(
        isScroll = true,
        title = "Овоскопировние",
        onDismissRequest = {
            onIntent(BookmarkIntent.OpenOvoscopBottomSheetClick(false))
        },
        contentBottom = {
            BottomPanel(
                isLantern = isLantern,
                onLanternClick = { onIntent(BookmarkIntent.LanternClick) },
                onCompleteClick = { onIntent(BookmarkIntent.CompleteOvoscopClick(true)) }
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            BigColorCard(
                glowColor = violet_14,
                colors = listOf(violet_13, violet_14),
                padding = PaddingValues(20.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconTransaction2(
                            sizeCard = 52.dp,
                            icon = R.drawable.ic_ovos2,
                            iconColor = white,
                            boxColor = white.copy(alpha = 0.2f)
                        )
                        Text(
                            ovoscopyState.titleText,
                            style = text_20,
                            color = white
                        )
                    }
                    Text(
                        ovoscopyState.titleSupText,
                        style = text_14,
                        color = white.copy(alpha = 0.8f)
                    )
                }
            }
            CardFieldNew(
                padding = PaddingValues()
            ) {
                Column(
                    modifier = Modifier.height(350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = ovoscopyState.image),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Column(
                    modifier = Modifier
                        .background(violet_3)
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(
                        stringResource(R.string.bookmark_screen_ovoscopy_paint_support),
                        style = text_14,
                        color = black_2,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        WarningCard(
            colorBackground = orang_4,
            colorBorder = orang_5,
            colorIcon = orang_2,
            colorIconBackground = Color(0xFFFEF3C6),
            colorTitle = Color(0xFF7B3306),
            colorText = orang_6,
            icon = R.drawable.icon_warning,
            title = R.string.entry_bookmark_warning_title,
            text = R.string.bookmark_screen_warning_text
        )
        InfoCard(
            colorBackground = blue_3,
            colorBorder = blue_9,
            colorIcon = blue_1,
            colorIconBackground = blue_13,
            colorTitle = blue_14,
            icon = R.drawable.outline_book_5_24,
            title = R.string.bookmark_screen_hint_ovoscopy_title
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                hintListTwo.forEach {
                    LineWithMarker(blue_20, it)
                }
            }
        }
        InfoCard(
            colorBackground = Color(0xFFF0FDF4),
            colorBorder = Color(0xFFB9F8CF),
            colorIcon = price_green,
            colorIconBackground = Color(0xFFDCFCE7),
            colorTitle = Color(0xFF008236),
            icon = R.drawable.outline_info_24,
            title = R.string.bookmark_screen_hint_remember_title,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                hintList.forEach {
                    LineWithMarker(green_9, it)
                }
            }
        }
    }
}

@Composable
private fun LineWithMarker(
    markerColor: Color,
    @StringRes text: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircleShape(color = markerColor, 6.dp)
        Text(stringResource(text), style = text_14, color = black_1)
    }
}


@Composable
private fun BottomPanel(
    isLantern: Boolean,
    onLanternClick: () -> Unit,
    onCompleteClick: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    Log.i("bookmark", "isLantern: $isLantern")

    val (backgroundColor, borderColor, text) = if (isLantern) Triple(
        listOf(orang_4, orang_4),
        orang_5,
        R.string.bookmark_screen_off
    ) else Triple(
        listOf(white, white),
        grey_2,
        R.string.bookmark_screen_lantern
    )
    toggleFlashlight(context, isLantern)

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        BigButton(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            backgroundColors = backgroundColor,
            borderStroke = BorderStroke(1.dp, borderColor),
            text = text,
            icon = if (isLantern) R.drawable.outline_flashlight_off_24 else R.drawable.outline_flashlight_on_24,
            onClick = onLanternClick
        )
        BigButton(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            backgroundColors = listOf(violet_13, violet_14),
            text = R.string.button_сomplete,
            icon = R.drawable.icon_check,
            textColor = white,
            onClick = { onCompleteClick(true) }
        )
    }
}

private fun toggleFlashlight(context: Context, enabled: Boolean) {
    val cameraManager =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
        cameraManager.getCameraCharacteristics(id)
            .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
    }
    Log.i("bookmark", "toggleFlashlight cameraId: $cameraId $enabled ")
    cameraId?.let {
        cameraManager.setTorchMode(it, enabled)
    }
}

@Composable
fun OvoscopEndBottomSheet(
    currentEgg: Int,
    rejectedEgg: String,
    enabled: Boolean,
    onIntent: (BookmarkIntent) -> Unit
) {
    val suffix = stringResource(R.string.bookmark_screen_eggs)
    BaseBottomSheet(
        skipPartiallyExpanded = false,
        title = stringResource(R.string.bookmark_screen_ovoscopy_results),
        onDismissRequest = { onIntent(BookmarkIntent.CompleteOvoscopClick(false)) },
        contentBottom = {
            BottomPanel(
                enabled = enabled,
                onCancelClick = { onIntent(BookmarkIntent.CompleteOvoscopClick(false)) },
                onSaveClick = { onIntent(BookmarkIntent.SaveEggClick) }
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            BorderCard(
                borderColor = green_11,
                containerColor = price_green_2,
                padding = PaddingValues(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.bookmark_screen_ovoscopy_was),
                            style = text_14,
                            color = marengo
                        )
                        Text("$currentEgg $suffix", style = text_14, color = black_2)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.bookmark_screen_ovoscopy_rejected),
                            style = text_14,
                            color = error_base
                        )
                        Text(
                            "${rejectedEgg.toConvertZero()} $suffix",
                            style = text_14,
                            color = red_14
                        )
                    }
                    HorizontalDivider(thickness = 1.dp, color = grey_2)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.bookmark_screen_ovoscopy_left),
                            style = text_16,
                            color = black_2
                        )
                        Text(
                            "${currentEgg - rejectedEgg.toConvertZero()} $suffix",
                            style = text_24,
                            color = green_9
                        )
                    }
                }
            }
            OutlinedNumberNew(
                value = rejectedEgg,
                onValueChange = { onIntent(BookmarkIntent.RejectedEggChanged(it)) },
                intRes = R.string.bookmark_screen_rejected_egg,
                intResSup = R.string.entry_bookmark_support_count_egg,
                intResError = R.string.entry_bookmark_error_reject_count,
                isError = !enabled,
                drawableRes = R.drawable.outline_cancel_24
            )
        }
    }
}

@Composable
private fun BottomPanel(
    enabled: Boolean,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        BigButton(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            borderStroke = BorderStroke(1.dp, grey_2),
            text = R.string.button_cancel,
            onClick = onCancelClick
        )
        BigButton(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            backgroundColors = listOf(
                Color(0xFF00A63E),
                Color(0xFF009966)
            ) /*listOf(violet_13, violet_14)*/,
            text = R.string.button_save,
            textColor = white,
            enabled = enabled,
            onClick = onSaveClick
        )
    }
}