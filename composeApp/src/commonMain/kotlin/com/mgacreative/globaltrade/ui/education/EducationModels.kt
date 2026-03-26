package com.mgacreative.globaltrade.ui.education

import com.mgacreative.globaltrade.core.util.IntToBooleanSerializer
import kotlinx.serialization.Serializable

@Serializable
data class Education(
    val id: String = "",
    val title: String = "",
    val topic: String = "",
    val instructor: String = "",
    val contentText: String = "",
    val videoUrl: String = "",
    val examLink: String = "",
    val contentUrl: String? = null, // Link için yeni alan
    val createdAt: Long = 0L
)
