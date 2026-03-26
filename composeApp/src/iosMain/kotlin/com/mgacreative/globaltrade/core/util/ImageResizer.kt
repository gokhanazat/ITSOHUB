package com.mgacreative.globaltrade.core.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
// Apple Native API'ları (Sadece iOS hedefinde derlenir)
// import platform.UIKit.* 
// import platform.Foundation.*
// import platform.CoreGraphics.*

actual object ImageResizer {
    actual suspend fun compressImage(
        bytes: ByteArray,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int
    ): ByteArray = withContext(Dispatchers.Default) {
        // iOS tarafına geçtiğinizde buraya CoreGraphics/UIKit tabanlı 
        // resim işleme kodu eklenecektir. Demo aşamasında orijinali dönüyoruz.
        bytes 
    }
}
