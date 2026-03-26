package com.mgacreative.globaltrade.manager

import kotlinx.browser.window
import kotlinx.browser.localStorage
import com.mgacreative.globaltrade.overrideNavigatorLanguage

actual suspend fun changeAppLanguage(languageCode: String) {
    localStorage.setItem("app_language", languageCode)
    // WASM/JS tarafında Compose Resources'ın yeni dili yüklemesi için sayfayı yenilemek en güvenli yoldur.
    window.location.reload()
}

actual suspend fun getCurrentAppLanguage(): String? {
    return localStorage.getItem("app_language") ?: "tr"
}

actual fun syncPlatformLocale(languageCode: String) {
    com.mgacreative.globaltrade.overrideNavigatorLanguage(languageCode)
}
