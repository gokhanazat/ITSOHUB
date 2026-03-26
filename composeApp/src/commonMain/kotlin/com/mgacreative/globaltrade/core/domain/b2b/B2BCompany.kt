package com.mgacreative.globaltrade.core.domain.b2b

import kotlinx.serialization.Serializable
import com.mgacreative.globaltrade.core.util.IntToBooleanSerializer

/**
 * Domain model representing a company specifically for B2B matching and scoring.
 */
@Serializable
data class B2BCompany(
    val id: String = "",
    val name: String = "",
    val sector: String = "",
    val subSectors: List<String> = emptyList(),
    val country: String = "",
    @Serializable(with = IntToBooleanSerializer::class)
    val isVerified: Boolean = false,
    val yearsInMarket: Int = 0,
    val exportVolume: Double = 0.0,
    val targetMarkets: List<String> = emptyList(),
    val certifications: List<String> = emptyList(),
    @Serializable(with = IntToBooleanSerializer::class)
    val hasLogo: Boolean = false,
    @Serializable(with = IntToBooleanSerializer::class)
    val hasDescription: Boolean = false,
    @Serializable(with = IntToBooleanSerializer::class)
    val hasContactInfo: Boolean = false,
    val platformActivityScore: Double = 0.0,
    val logoUrl: String? = null,
    val phone: String = "",
    val gsm: String = "",
    val email: String = "",
    val authorizedPerson: String = "",
    val description: String = ""
)
