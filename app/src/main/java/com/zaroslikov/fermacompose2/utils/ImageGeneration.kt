package com.zaroslikov.fermacompose2.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import com.zaroslikov.fermacompose2.BuildConfig
import java.io.File
import java.io.FileOutputStream


interface ImageGeneration {
    fun generateImage(
        bitmap: ImageBitmap
    )
}

class ImageGenerationImpl(private val context: Context) : ImageGeneration {
    override fun generateImage(
        bitmap: ImageBitmap,
    ) {
        val androidBitmap = bitmap.asAndroidBitmap()

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "qr_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Farma")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver

        val uri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        ) ?: return

        resolver.openOutputStream(uri)?.use { out ->
            androidBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)

        resolver.update(uri, values, null, null)
    }
}