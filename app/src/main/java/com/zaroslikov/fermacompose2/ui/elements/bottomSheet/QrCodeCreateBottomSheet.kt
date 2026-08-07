package com.zaroslikov.fermacompose2.ui.elements.bottomSheet

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.Bitmap
import com.zaroslikov.domain.models.table.template.DomainTemplateTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.black_2
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_8
import com.zaroslikov.fermacompose2.supportFun.toResId
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.CloseButton
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.text_12
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.elements.сompositions.GroupButtonWitchAnimate
import kotlinx.coroutines.launch
import androidx.core.graphics.createBitmap
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.compose.RewardedAdLoaderState
import com.yandex.mobile.ads.compose.rememberRewardedAdLoader
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoadResult
import com.zaroslikov.domain.models.enums.TemplateType
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_16
import com.zaroslikov.fermacompose2.orang_4
import com.zaroslikov.fermacompose2.orang_5
import com.zaroslikov.fermacompose2.orang_6
import com.zaroslikov.fermacompose2.orang_8
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.supportFun.toastShort
import com.zaroslikov.fermacompose2.ui.elements.сompositions.card.InfoPatternCard
import com.zaroslikov.fermacompose2.ui.project.finance.category.WarningCard
import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.QrCodeData
import com.zaroslikov.fermacompose2.white
import io.appmetrica.analytics.AppMetrica


//TODO Сократить
@Composable
fun QrCodeCreateBottomSheet(
    colors: List<Color>,
    qrCodeData: QrCodeData?,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val activity = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentTitleLocation by rememberSaveable { mutableStateOf(TitleLocation.NO) }
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val miniGraphicsLayer = rememberGraphicsLayer()
    val standardGraphicsLayer = rememberGraphicsLayer()
    val fullGraphicsLayer = rememberGraphicsLayer()
    val text = stringResource(R.string.yandex_rewarded_ads)

    // Состояния для рекламы
    var rewardedAd by remember { mutableStateOf<RewardedAd?>(null) }
    var isAdReady by remember { mutableStateOf(false) }
    var isAdLoading by remember { mutableStateOf(false) }
    var showAdTrigger by remember { mutableStateOf(false) } // Триггер для показа рекламы

    val loader = rememberRewardedAdLoader()

    val pages = QrCodeType.entries
    val pagerState = rememberPagerState(
        pageCount = { pages.size },
        initialPage = 1
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/png")
    ) { uri ->
        uri?.let {
            val androidBitmap = bitmap?.asAndroidBitmap() ?: return@let
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                androidBitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
            }
        }
    }

    // Загрузка рекламы при старте
    LaunchedEffect(Unit) {
        isAdLoading = true
        loadNewAd(
            text, loader
        ) { ad ->
            rewardedAd = ad
            isAdReady = true
            isAdLoading = false
        }
    }

    // Показ рекламы при активации триггера
    LaunchedEffect(showAdTrigger) {
        if (showAdTrigger && rewardedAd != null && isAdReady) {
            rewardedAd?.apply {
                setAdEventListener(object : RewardedAdEventListener {
                    override fun onAdShown() {
                        // Реклама показана
                    }

                    override fun onAdFailedToShow(adError: AdError) {
                        coroutineScope.launch {
                            loadNewAd(text, loader) { ad ->
                                rewardedAd = ad
                                isAdReady = true
                                isAdLoading = false
                            }
                        }

                    }

                    override fun onAdDismissed() {
                        // Реклама закрыта - загружаем новую для следующих показов
                        isAdReady = false
                        isAdLoading = true
                        coroutineScope.launch {
                            loadNewAd(text, loader) { ad ->
                                rewardedAd = ad
                                isAdReady = true
                                isAdLoading = false
                            }
                        }
                        // Сбрасываем триггер
                        showAdTrigger = false
                    }

                    override fun onAdClicked() {
                        // Клик по рекламе
                    }

                    override fun onAdImpression(impressionData: ImpressionData?) {
                        // Импрессия зафиксирована
                    }

                    override fun onRewarded(reward: Reward) {
                        // Награда получена - создаем QR код
                        coroutineScope.launch {
                            val currentQrCodeType =
                                QrCodeType.entries.first { it.ordinal == pagerState.currentPage }
                            bitmap =
                                when (currentQrCodeType) {
                                    QrCodeType.MINI -> miniGraphicsLayer.toImageBitmap()
                                    QrCodeType.STANDARD -> standardGraphicsLayer.toImageBitmap()
                                    QrCodeType.FULL -> fullGraphicsLayer.toImageBitmap()
                                }.addPadding(context, 20)
                            launcher.launch("qr_code_${qrCodeData?.template?.nameTemplate}.png")
                            yandexMetric(currentQrCodeType, currentTitleLocation)
                        }
                    }
                })
                // Показываем рекламу
                show(activity as Activity)
            }
        }
    }

    BaseBottomSheet(
        title = stringResource(R.string.template_qr_code_bottom_sheet_title),
        onDismissRequest = onDismissRequest,
        contentBottom = {
            ButtonPanel(
                colors = colors,
                onDismissRequest = onDismissRequest,
                onCreateQrCodeClick = {
                    // Показываем рекламу перед созданием QR кода
                    if (isAdReady && rewardedAd != null) {
                        // Реклама готова - показываем
                        showAdTrigger = true
                    } else if (isAdLoading) {
                        toastShort(context, "Загрузка")
                    } else {
                        toastShort(context, "Отсутсвует подключение к интернету")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TabsWhichSlider(
                selectedIndex = pagerState.currentPage,
                onChoiceQrCodeTypeClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(it.ordinal)
                    }
                }
            )
            HorizontalPager(
                state = pagerState,
                pageSpacing = 10.dp
            ) { pageIndex ->
                when (pages[pageIndex]) {
                    QrCodeType.MINI ->
                        MiniQrCodeCard(
                            modifier = Modifier.drawWithContent {
                                miniGraphicsLayer.record {
                                    this@drawWithContent.drawContent()
                                }
                                drawLayer(miniGraphicsLayer)
                            },
                            color = colors.first(),
                            title = qrCodeData?.template?.nameTemplate ?: "",
                            bitmap = qrCodeData?.qrCodeWithLogoBitmap,
                            currentTitleLocation = currentTitleLocation,
                            isMultiProjectTemplate = qrCodeData?.template?.isMultiProjectTemplate
                                ?: false,
                            onClick = { currentTitleLocation = it },
                        )

                    QrCodeType.STANDARD -> StandardQrCodeCard(
                        modifier = Modifier.drawWithContent {
                            standardGraphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(standardGraphicsLayer)
                        }, qrCodeData?.qrCodeBitmap, qrCodeData?.template
                    )

                    QrCodeType.FULL -> FullQrCodeCard(
                        modifier = Modifier.drawWithContent {
                            fullGraphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(fullGraphicsLayer)
                        }, qrCodeData?.qrCodeBitmap, qrCodeData?.template
                    )
                }
            }
            qrCodeData?.template?.templateType?.let {
                WarningCard(
                    colorBackground = ghostly_white,
                    colorBorder = grey_2,
                    colorIcon = dark,
                    colorIconBackground = gray_6,
                    colorText = marengo,
                    icon = R.drawable.outline_lock_24,
                    text = when (it) {
                        TemplateType.SALE -> R.string.template_qr_code_bottom_sheet_security_support_sale
                        TemplateType.EXPENSES -> R.string.template_qr_code_bottom_sheet_security_support_expenses
                        else -> R.string.template_qr_code_bottom_sheet_security_support
                    }
                )
            }
            WarningCard(
                colorBackground = orang_4,
                colorBorder = orang_8,
                colorIcon = orang_6,
                colorIconBackground = orang_5,
                colorText = orang_16,
                icon = R.drawable.baseline_favorite_24,
                text = R.string.template_qr_code_bottom_sheet_ads_support
            )
        }
    }
}


@Composable
private fun FullQrCodeCard(
    modifier: Modifier,
    bitmap: Bitmap?,
    first: DomainTemplateTable?
) {
    BaseQrCodeCard(modifier) {
        Sd(bitmap, first)
        if (first != null)
            InfoPatternCard(
                title = first.title,
                count = first.count?.formatNumber(),
                countSuffix = first.countSuffix,
                category = first.category,
                note = first.note?.ifBlank { null },
                buyer = first.buyer,
                animalName = first.animalName?.ifBlank { null },
                price = first.price?.takeIf { it != 0.0 }?.formatNumber(),
                priceAll = first.priceAll?.takeIf { it != 0.0 }?.formatNumber(),
                priceSuffix = first.priceSuffix,
                writeOffStatus = first.writeOffStatus
            )
    }
}

@Composable
private fun StandardQrCodeCard(
    modifier: Modifier,
    bitmap: Bitmap?,
    first: DomainTemplateTable?
) {
    BaseQrCodeCard(modifier) {
        Sd(bitmap, first)
    }
}

@Composable
private fun Sd(bitmap: Bitmap?, first: DomainTemplateTable?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_log),
            contentDescription = null,
            modifier = Modifier
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(10.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(10.dp))
                .size(48.dp)
                .border(
                    1.dp,
                    color = ghostly_white,
                    shape = RoundedCornerShape(10.dp)
                )
        )
        Text(
            text = stringResource(R.string.app_name),
            style = text_12,
            lineHeight = 16.sp,
            color = black_2,
            fontWeight = FontWeight.SemiBold
        )
        HorizontalDivider(color = green_8, modifier = Modifier.fillMaxWidth(0.75f))
    }
    QrCodeImage(bitmap = bitmap)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        HorizontalDivider(color = green_8, modifier = Modifier.fillMaxWidth(0.75f))
        if (first != null)
            NameTemplateText(
                title = first.nameTemplate,
                isMultiProjectTemplate = first.isMultiProjectTemplate
            )
    }
}

@Composable
private fun MiniQrCodeCard(
    modifier: Modifier = Modifier,
    title: String,
    bitmap: Bitmap?,
    color: Color,
    currentTitleLocation: TitleLocation,
    isMultiProjectTemplate: Boolean,
    onClick: (TitleLocation) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GroupButton(
            color = color,
            currentTitleLocation
        ) { onClick(it) }
        BaseQrCodeCard(
            modifier = modifier
        ) {
            when (currentTitleLocation) {
                TitleLocation.LEFT,
                TitleLocation.RIGHT -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentTitleLocation == TitleLocation.LEFT)
                            NameTemplateText(
                                modifier = Modifier.weight(1f),
                                title = title,
                                isMultiProjectTemplate = isMultiProjectTemplate
                            )
                        QrCodeImage(
                            modifier = Modifier.weight(1f),
                            bitmap = bitmap
                        )
                        if (currentTitleLocation == TitleLocation.RIGHT)
                            NameTemplateText(
                                modifier = Modifier.weight(1f),
                                title = title,
                                isMultiProjectTemplate = isMultiProjectTemplate
                            )
                    }
                }

                TitleLocation.UNDER -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QrCodeImage(bitmap = bitmap)
                    NameTemplateText(title = title, isMultiProjectTemplate = isMultiProjectTemplate)
                }

                TitleLocation.NO -> QrCodeImage(bitmap = bitmap)
            }
        }
    }
}


@Composable
private fun BaseQrCodeCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    BorderCard(
        modifier = modifier,
        borderColor = green_8
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}


@Composable
private fun ButtonPanel(
    colors: List<Color>,
    onDismissRequest: () -> Unit,
    onCreateQrCodeClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CloseButton(
            text = R.string.button_cancel,
            onClick = onDismissRequest,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        GradientButton(
            text = stringResource(R.string.button_create_png),
            onClick = onCreateQrCodeClick,
            prefixIconRes = R.drawable.outline_download_24,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            colors = colors
        )
    }
}

@Composable
private fun TabsWhichSlider(
    selectedIndex: Int,
    onChoiceQrCodeTypeClick: (QrCodeType) -> Unit
) {
    GroupButtonWitchAnimate(
        modifier = Modifier.fillMaxWidth(),
        itemCount = QrCodeType.entries.size,
        selectedIndex = selectedIndex,
        paddingValues = PaddingValues(4.dp)
    ) {
        QrCodeType.entries.forEach { qrCodeType ->
            val textColor by animateColorAsState(
                if (selectedIndex == qrCodeType.ordinal) black_2 else gray_7,
                label = ""
            )
            Text(
                text = stringResource(qrCodeType.toResId()),
                style = text_14,
                color = textColor,
                fontWeight = if (selectedIndex == qrCodeType.ordinal) FontWeight.Medium else FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        indication = null, // ❗ отключаем ВСЕ эффекты
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onChoiceQrCodeTypeClick(qrCodeType)
                    }
                    .padding(paddingValues = PaddingValues(6.dp))
            )
        }
    }
}


@Composable
private fun GroupButton(
    color: Color,
    currentTitleLocation: TitleLocation,
    onClick: (TitleLocation) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TitleLocation.entries.forEach { titleLocation ->
            val (containerColor, borderColor, textButton) =
                if (currentTitleLocation == titleLocation) Triple(color, color, white)
                else Triple(white, grey_2, gray_7)

            BorderCard(
                modifier = Modifier.weight(1f),
                containerColor = containerColor,
                padding = PaddingValues(6.dp),
                shape = RoundedCornerShape(14.dp),
                borderColor = borderColor,
                onClick = { onClick(titleLocation) }
            ) {
                Text(
                    stringResource(titleLocation.toResId()),
                    style = text_12,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 16.sp,
                    color = textButton,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun QrCodeImage(
    modifier: Modifier = Modifier,
    bitmap: Bitmap?
) {
    if (bitmap != null)
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = modifier
                .size(190.dp)
        )
}

@Composable
private fun NameTemplateText(
    modifier: Modifier = Modifier,
    title: String,
    isMultiProjectTemplate: Boolean
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            title,
            fontWeight = FontWeight.Bold,
            lineHeight = 24.sp,
            style = text_16,
            textAlign = TextAlign.Center
        )
        if (isMultiProjectTemplate)
            Icon(
                painterResource(R.drawable.outline_language_24),
                contentDescription = null,
                tint = grey,
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 4.dp)
            )
    }
}

private suspend fun loadNewAd(
    text: String,
    loader: RewardedAdLoaderState,
    onResult: (RewardedAd?) -> Unit
) {
    val adRequest = AdRequest.Builder(text).build()
    when (val result = loader.loadAd(adRequest)) {
        is RewardedAdLoadResult.Success -> onResult(result.ad)
        is RewardedAdLoadResult.Failure -> onResult(null)
    }
}

enum class QrCodeType {
    MINI, STANDARD, FULL
}

enum class TitleLocation {
    LEFT, RIGHT, UNDER, NO
}

private fun yandexMetric(
    currentQrCodeType: QrCodeType,
    currentTitleLocation: TitleLocation,
) {
    val eventParameters: MutableMap<String, Any> = HashMap()
    eventParameters["QR-код"] = currentQrCodeType
    if (currentQrCodeType == QrCodeType.MINI)
        eventParameters["Расположение"] = currentTitleLocation
    AppMetrica.reportEvent("Cоздание QR-кода", eventParameters)
}

private fun ImageBitmap.addPadding(
    context: Context,
    paddingDp: Int
): ImageBitmap {
    val padding = (paddingDp * context.resources.displayMetrics.density).toInt()

    val hardwareBitmap = asAndroidBitmap()
    val src = hardwareBitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, false)
    val result = createBitmap(src.width + padding * 2, src.height + padding * 2)

    val canvas = Canvas(result)
    canvas.drawColor(android.graphics.Color.WHITE)
    canvas.drawBitmap(src, padding.toFloat(), padding.toFloat(), null)

    return result.asImageBitmap()
}