package com.mgacreative.globaltrade.core.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class FAQItem(val question: String, val answer: String)
