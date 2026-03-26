package com.mgacreative.globaltrade.core.domain.consultancy

import kotlinx.serialization.Serializable

@Serializable
data class Consultant(
    val id: String = "",
    val name: String = "",
    val title: String = "",
    val expertise: String = "",
    val bio: String = "",
    val photoUrl: String? = null,
    val email: String = "",
    val phone: String = "",
    val whatsapp: String = "",
    val displayOrder: Int = 0
)
