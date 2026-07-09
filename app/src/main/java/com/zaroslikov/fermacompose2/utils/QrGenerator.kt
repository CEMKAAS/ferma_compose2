package com.zaroslikov.fermacompose2.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.DrawableRes
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.zaroslikov.fermacompose2.R


interface QrGenerator {
    fun generateWhichLogo(
        content: String,
        size: Int = 512,
        @DrawableRes logoResId: Int = R.drawable.ic_log
    ): Bitmap

    fun generate(
        content: String,
        size: Int = 512
    ): Bitmap

}

class QrGeneratorImpl(context: Context) : QrGenerator {
    private val resources = context.resources
    override fun generateWhichLogo(
        content: String,
        size: Int,
        logoResId: Int  // Добавляем параметр с иконкой
    ): Bitmap {
        // 1. Настройки с высоким уровнем коррекции ошибок
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H, // Ключевой момент!
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )

        // 2. Генерируем QR-код с учётом подсказок
        val matrix = MultiFormatWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            size,
            size,
            hints // Передаём настройки
        )

        // 3. Создаём пустой Bitmap для QR-кода
        val qrBitmap = createBitmap(size, size)

        // 4. Рисуем QR-код пиксель за пикселем
        for (x in 0 until size) {
            for (y in 0 until size) {
                qrBitmap[x, y] = if (matrix[x, y]) Color.BLACK else Color.WHITE
            }
        }

        // 6. Создаём новый Bitmap для результата и Canvas для рисования
        val resultBitmap = createBitmap(size, size)
        val canvas = Canvas(resultBitmap)

        // 7. Рисуем QR-код
        canvas.drawBitmap(qrBitmap, 0f, 0f, null)

        // 8. Рассчитываем размер и позицию логотипа (20% от размера QR-кода)
        val logoSize = (size * 0.2).toInt() // 20% от ширины
        val left = (size - logoSize) / 2f
        val top = (size - logoSize) / 2f

        // 9. Рисуем белый фон под логотипом (опционально, чтобы улучшить читаемость)
        val paint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRect(left, top, left + logoSize, top + logoSize, paint)

        val drawable = resources.getDrawable(logoResId)
            ?: error("Logo not found")

        val scaledLogo = createBitmap(logoSize, logoSize)

        val logoCanvas = Canvas(scaledLogo)

        drawable.setBounds(0, 0, logoSize, logoSize)
        drawable.draw(logoCanvas)

        canvas.drawBitmap(
            scaledLogo,
            left,
            top,
            null
        )

        // 11. Освобождаем промежуточные ресурсы (по желанию)
        qrBitmap.recycle()

        return resultBitmap
    }

    override fun generate(
        content: String,
        size: Int
    ): Bitmap {
        val matrix = MultiFormatWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            size,
            size
        )

        val qrBitmap = createBitmap(size, size)
        for (x in 0 until size) {
            for (y in 0 until size) {
                qrBitmap[x, y] = if (matrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return qrBitmap
    }
}