package com.mgacreative.globaltrade.core.domain.sector

import com.mgacreative.globaltrade.core.util.IntToBooleanSerializer
import kotlinx.serialization.Serializable

@Serializable
data class Sector(
    val id: String = "",
    val name: String = "",
    val groupNo: String = "",
    @Serializable(with = IntToBooleanSerializer::class)
    val isActive: Boolean = true
)
