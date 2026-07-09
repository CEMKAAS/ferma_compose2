package com.zaroslikov.fermacompose2.ui.elements.bottomSheet

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.ui.elements.сompositions.card.InfoPatternCard

//TODO Сократить
@Composable
fun QrCodeBottomSheet(
    colors: List<Color>,
    triple: Triple<DomainTemplateTable, Bitmap, Bitmap>?,
    onCreateQrCodeClick: (ImageBitmap) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val miniGraphicsLayer = rememberGraphicsLayer()
    val standardGraphicsLayer = rememberGraphicsLayer()
    val fullGraphicsLayer = rememberGraphicsLayer()

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

    BaseBottomSheet(
        title = stringResource(R.string.template_qr_code_bottom_sheet_title),
        onDismissRequest = onDismissRequest,
        contentBottom = {
            ButtonPanel(
                colors = colors,
                onDismissRequest = onDismissRequest,
                onCreateQrCodeClick = {
                    coroutineScope.launch {
                        bitmap =
                            when (QrCodeType.entries.first { it.ordinal == pagerState.currentPage }) {
                                QrCodeType.MINI -> miniGraphicsLayer.toImageBitmap()
                                QrCodeType.STANDARD -> standardGraphicsLayer.toImageBitmap()
                                QrCodeType.FULL -> fullGraphicsLayer.toImageBitmap()
                            }.addPadding(context, 20)
                        launcher.launch("qr_code_${triple?.first?.nameTemplate}.png")
                    }
                },
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
                            triple?.third
                        )

                    QrCodeType.STANDARD -> StandardQrCodeCard(
                        modifier = Modifier.drawWithContent {
                            standardGraphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(standardGraphicsLayer)
                        }, triple?.second, triple?.first
                    )

                    QrCodeType.FULL -> FullQrCodeCard(
                        modifier = Modifier.drawWithContent {
                            fullGraphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(fullGraphicsLayer)
                        }, triple?.second, triple?.first
                    )
                }
            }
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
                note = first.note
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
    if (bitmap != null)
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.size(190.dp)
        )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        HorizontalDivider(color = green_8, modifier = Modifier.fillMaxWidth(0.75f))
        if (first != null)
            Text(
                first.nameTemplate,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp,
                style = text_16
            )
    }
}

@Composable
private fun MiniQrCodeCard(
    modifier: Modifier = Modifier,
    bitmap: Bitmap?
) {
    BaseQrCodeCard(
        modifier = modifier
    ) {
        if (bitmap != null)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(190.dp)
            )
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
            modifier = Modifier.weight(1f)
        )
        GradientButton(
            text = stringResource(R.string.button_create_png),
            onClick = onCreateQrCodeClick,
            prefixIconRes = R.drawable.outline_download_24,
            modifier = Modifier.weight(1f),
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

enum class QrCodeType {
    MINI, STANDARD, FULL
}

fun ImageBitmap.addPadding(
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