package com.mgacreative.globaltrade.core.domain.auth

import com.mgacreative.globaltrade.core.network.ApiConfig
import com.mgacreative.globaltrade.core.error.AppResult
import com.mgacreative.globaltrade.core.error.safeCall
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class AppConfig(
    val contactEmail: String = "destek@globaltrade.local",
    val faqs: List<FAQItem> = emptyList()
)

class SettingsService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { 
                ignoreUnknownKeys = true 
                coerceInputValues = true
                isLenient = true
            })
        }
    }

    suspend fun getContactEmail(): AppResult<String> {
        return safeCall {
            val response = client.get(ApiConfig.SETTINGS_URL)
            if (response.status == HttpStatusCode.OK) {
                val config = response.body<AppConfig>()
                config.contactEmail
            } else {
                "destek@globaltrade.local"
            }
        }
    }

    suspend fun updateContactEmail(email: String): AppResult<Unit> {
        return safeCall {
            val response = client.patch("${ApiConfig.SETTINGS_URL}?contactEmail=$email")
            if (response.status != HttpStatusCode.OK) throw Exception("API Hatası: ${response.status}")
        }
    }

    suspend fun getHelpCenterFAQs(): AppResult<List<FAQItem>> {
        return safeCall {
            val response = client.get(ApiConfig.SETTINGS_URL)
            if (response.status == HttpStatusCode.OK) {
                val config = response.body<AppConfig>()
                config.faqs
            } else {
                emptyList()
            }
        }
    }

    suspend fun updateHelpCenterFAQs(faqs: List<FAQItem>): AppResult<Unit> {
        return safeCall {
            val response = client.post(ApiConfig.SETTINGS_URL) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("faqs" to faqs))
            }
            if (response.status != HttpStatusCode.OK) throw Exception("API Hatası: ${response.status}")
        }
    }
}
