package com.mgacreative.globaltrade.core.pdf.util

actual object FileSaver {
    actual fun savePdf(fileName: String, data: ByteArray) {
        // No-op for JS
    }
}
