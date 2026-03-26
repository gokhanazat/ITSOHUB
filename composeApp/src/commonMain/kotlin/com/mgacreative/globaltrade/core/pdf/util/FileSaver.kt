package com.mgacreative.globaltrade.core.pdf.util

expect object FileSaver {
    fun savePdf(fileName: String, data: ByteArray)
}
