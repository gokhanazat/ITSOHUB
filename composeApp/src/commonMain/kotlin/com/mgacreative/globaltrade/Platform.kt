package com.mgacreative.globaltrade

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getNowMillis(): Long

expect fun openUrl(url: String)
