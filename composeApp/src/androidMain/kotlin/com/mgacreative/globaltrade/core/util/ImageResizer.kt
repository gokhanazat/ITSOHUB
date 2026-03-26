package com.mgacreative.globaltrade.core.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual object ImageResizer {
    actual suspend fun compressImage(
        bytes: ByteArray,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int
    ): ByteArray = withContext(Dispatchers.Default) {
        try {
            // 1. Orijinal resmi yükle
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true 
            }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

            // 2. Ölçekleme oranını hesapla
            var inSampleSize = 1
            if (options.outHeight > maxHeight || options.outWidth > maxWidth) {
                val halfHeight = options.outHeight / 2
                val halfWidth = options.outWidth / 2
                while (halfHeight / inSampleSize >= maxHeight && halfWidth / inSampleSize >= maxWidth) {
                    inSampleSize *= 2
                }
            }

            // 3. Resmi bellek dostu olarak yükle
            val scaleOptions = BitmapFactory.Options().apply {
                inSampleSize = inSampleSize
            }
            val originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, scaleOptions)
                ?: return@withContext bytes

            // 4. Kesin boyutlandırma (maxWidth/maxHeight sınırına çekme)
            val scale = Math.min(
                maxWidth.toFloat() / originalBitmap.width,
                maxHeight.toFloat() / originalBitmap.height
            )
            
            val resizedBitmap = if (scale < 1f) {
                val matrix = Matrix().apply { postScale(scale, scale) }
                Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
            } else {
                originalBitmap
            }

            // 5. WebP formatında sıkıştır
            val outputStream = ByteArrayOutputStream()
            val format = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                @Suppress("DEPRECATION")
                Bitmap.CompressFormat.WEBP
            }
            
            resizedBitmap.compress(format, quality, outputStream)
            
            val result = outputStream.toByteArray()
            
            // Bellek temizliği
            if (resizedBitmap != originalBitmap) resizedBitmap.recycle()
            originalBitmap.recycle()
            
            result
        } catch (e: Exception) {
            e.printStackTrace()
            bytes // Hata durumunda orijinali dön
        }
    }
}
