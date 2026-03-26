package com.mgacreative.globaltrade.core.util

actual object ImageResizer {
    /**
     * Web (Wasm) tarafında sıkıştırma işlemi şimdilik ham veriyi döner.
     */
    actual suspend fun compressImage(bytes: ByteArray, maxWidth: Int, maxHeight: Int, quality: Int): ByteArray {
        return bytes
    }
}
