package com.mgacreative.globaltrade

import kotlinx.browser.window

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

// @JsFun notasyonu Kotlin/Wasm için en güvenli JS interop yöntemidir
@JsFun("() => Number(Date.now())")
external fun dateNow(): Double

actual fun getNowMillis(): Long = dateNow().toLong()

actual fun openUrl(url: String) {
    window.open(url, "_blank")
}
