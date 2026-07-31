@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.elements.bottomSheet

import android.Manifest
import android.R.attr.left
import android.R.attr.top
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ghostly_white
import com.zaroslikov.fermacompose2.gray_6
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.grey
import com.zaroslikov.fermacompose2.grey_2
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.BorderButton
import com.zaroslikov.fermacompose2.ui.elements.BorderCard
import com.zaroslikov.fermacompose2.ui.elements.icon.IconBorder
import com.zaroslikov.fermacompose2.ui.elements.icon.IconCircleButton
import com.zaroslikov.fermacompose2.ui.elements.textBold_20
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.white
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.delay

@Composable
fun QrScannerScreen(
    onDismissRequest: () -> Unit,
    onQrDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity
    var torchEnabled by remember { mutableStateOf(false) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.CAMERA
    )
    BaseBottomSheet(
        modifier = Modifier.then(
            if (hasCameraPermission) Modifier.fillMaxSize() else Modifier
        ),
        paddingValues = PaddingValues(),
        isScroll = !hasCameraPermission,
        contentBottom = if (!hasCameraPermission) {
            {
                BorderButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.button_close)
                ) { onDismissRequest() }
            }
        } else null,
        onDismissRequest = {
            torchEnabled = false
            onDismissRequest() }
    ) {
        AppMetrica.reportEvent("Открытие камеры для сканирование QR-кода")
        var detected by remember { mutableStateOf(false) }


        var cameraControl by remember { mutableStateOf<CameraControl?>(null) }


        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasCameraPermission = granted

            if (!granted) {
                val permanentlyDenied =
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.CAMERA
                    )

                if (permanentlyDenied) openCameraSettings(context)

            }

        }

        LaunchedEffect(detected) {
            if (detected) {
                delay(700)
                detected = false
            }
        }

        if (hasCameraPermission)
            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                CameraPreview(
                    onQrDetected = {
                        if (!detected) {
                            torchEnabled = false
                            detected = true
                            vibrateShort(context)
                            onQrDetected(it)
                        }
                    },
                    onCameraReady = {
                        cameraControl = it
                    }
                )


                ScannerOverlay(
                    modifier = Modifier.fillMaxSize(),
                    detected = detected
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = stringResource(R.string.qr_code_scanner_bottom_camera_qr),
                        color = white,
                        style = textBold_20,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 30.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconCircleButton(
                            iconRes = R.drawable.baseline_clear_24
                        ) {
                            onDismissRequest()
                        }
                        IconCircleButton(
                            iconRes = if (torchEnabled) R.drawable.outline_flashlight_off_24
                            else R.drawable.outline_flashlight_on_24
                        ) {
                            torchEnabled = !torchEnabled
                            cameraControl?.enableTorch(torchEnabled)
                        }
                    }
                }
            }
        else BorderCard(
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_medium)),
            containerColor = ghostly_white,
            borderColor = grey_2
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconBorder(
                    iconRes = R.drawable.outline_no_photography_24,
                    iconColor = grey,
                    iconSize = 32.dp,
                    borderColor = grey_2,
                    containerColor = gray_6,
                )
                Text(
                    stringResource(R.string.qr_code_scanner_bottom_no_camera),
                    style = text_14,
                    color = gray_7,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )
                BorderButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.button_request_permission)
                ) {
                    if (hasCameraPermission) return@BorderButton

                    if (showRationale) {
                        launcher.launch(Manifest.permission.CAMERA)
                    } else {
                        launcher.launch(Manifest.permission.CAMERA)
                    }
                }
            }
        }
    }
}


fun openCameraSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    context.startActivity(intent)
}

@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    detected: Boolean
) {
    val borderColor by animateColorAsState(
        targetValue = if (detected) {
            Color(0xFF4CAF50)
        } else {
            Color.White
        },
        label = ""
    )

    Canvas(modifier = modifier) {

        val overlayColor = Color.Black.copy(alpha = 0.55f)

        // Размер окна (72% ширины)
        val windowSize = size.width * 0.72f

        val left = (size.width - windowSize) / 2f

        // Смещаем окно немного ниже центра,
        // чтобы оно выглядело естественнее
        val top = (size.height - windowSize) / 2f

        val rect = Rect(
            left,
            top,
            left + windowSize,
            top + windowSize
        )

        // Затемнение вокруг окна
        clipPath(
            Path().apply {
                addRect(Rect(Offset.Zero, size))
                addRoundRect(
                    RoundRect(
                        rect,
                        CornerRadius(40.dp.toPx())
                    )
                )
                fillType = PathFillType.EvenOdd
            }
        ) {
            drawRect(overlayColor)
        }

        // Зеленая подсветка после успешного сканирования
        if (detected) {
            drawRoundRect(
                color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                topLeft = Offset(left, top),
                size = Size(windowSize, windowSize),
                cornerRadius = CornerRadius(40.dp.toPx())
            )
        }

        // Основная рамка
        drawRoundRect(
            color = borderColor,
            topLeft = Offset(left, top),
            size = Size(windowSize, windowSize),
            cornerRadius = CornerRadius(40.dp.toPx()),
            style = Stroke(width = 4.dp.toPx())
        )
    }
}


@Composable
fun CameraPreview(
    onQrDetected: (String) -> Unit,
    onCameraReady: (CameraControl) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var scanned by remember { mutableStateOf(false) }

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                val scanner = BarcodeScanning.getClient(
                    BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build()
                )

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                analysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context)
                ) { imageProxy ->
                    val mediaImage = imageProxy.image

                    if (scanned) {
                        imageProxy.close()
                        return@setAnalyzer
                    }

                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )

                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                val value = barcodes.firstOrNull()?.rawValue
                                if (value != null) {
                                    scanned = true
                                    onQrDetected(value)
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
                onCameraReady(camera.cameraControl)
            } catch (e: Exception) {
                Log.e("Camera", "Bind failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}


fun vibrateShort(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibrator = context.getSystemService(VibratorManager::class.java)
        vibrator.defaultVibrator.vibrate(
            VibrationEffect.createOneShot(
                40,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        @Suppress("DEPRECATION")
        val vibrator =
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    40,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(40)
        }
    }
}
