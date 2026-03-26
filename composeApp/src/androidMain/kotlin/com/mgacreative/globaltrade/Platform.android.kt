package com.mgacreative.globaltrade

import android.content.Intent
import android.net.Uri
import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun getNowMillis(): Long = System.currentTimeMillis()

actual fun openUrl(url: String) {
    val context = com.mgacreative.globaltrade.manager.LanguagePreferenceManager.appContext ?: return
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
