package com.mgacreative.globaltrade.manager

actual suspend fun changeAppLanguage(languageCode: String) {
    LanguagePreferenceManager.appContext?.let { context ->
        LanguagePreferenceManager.setLanguage(context, languageCode)
    }
}

actual suspend fun getCurrentAppLanguage(): String? {
    return LanguagePreferenceManager.appContext?.let { context ->
        LanguagePreferenceManager.getLanguage(context)
    }
}

actual fun syncPlatformLocale(languageCode: String) {
    // Android'de navigator.language override'a gerek yoktur, sistem dilleri otomatik yönetilir.
}
