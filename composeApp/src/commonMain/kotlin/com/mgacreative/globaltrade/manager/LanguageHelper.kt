package com.mgacreative.globaltrade.manager

expect suspend fun changeAppLanguage(languageCode: String)
expect suspend fun getCurrentAppLanguage(): String?
expect fun syncPlatformLocale(languageCode: String)
