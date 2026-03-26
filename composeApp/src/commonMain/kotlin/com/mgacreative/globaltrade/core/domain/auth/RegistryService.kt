package com.mgacreative.globaltrade.core.domain.auth

import com.mgacreative.globaltrade.getNowMillis
import com.mgacreative.globaltrade.core.network.ApiConfig
import com.mgacreative.globaltrade.core.error.AppResult
import com.mgacreative.globaltrade.core.error.safeCall
import com.mgacreative.globaltrade.core.util.IntToBooleanSerializer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RegistryEntry(
    val id: String = "",
    val number: String = "",
    val ownerName: String = "",
    @Serializable(with = IntToBooleanSerializer::class)
    val active: Boolean = true,
    val createdAt: Long = 0L
)

class RegistryService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { 
                ignoreUnknownKeys = true 
                coerceInputValues = true
                encodeDefaults = true
                isLenient = true
            })
        }
    }

    suspend fun isValidRegistryNumber(number: String): AppResult<Boolean> {
        return safeCall {
            val response: HttpResponse = client.get("${ApiConfig.REGISTRY_URL}?number=$number")
            if (response.status != HttpStatusCode.OK) return@safeCall false
            
            val bodyText = response.bodyAsText().trim()
            
            // 1. Doğrudan "true" dönüyorsa onay ver
            if (bodyText == "true") return@safeCall true
            
            // 2. Eğer JSON listesi veya objesi dönüyorsa içeriği kontrol et
            val json = Json { ignoreUnknownKeys = true }
            try {
                // Liste olarak dene
                if (bodyText.startsWith("[")) {
                    val list = json.decodeFromString<List<RegistryEntry>>(bodyText)
                    list.any { it.number == number && it.active }
                } else if (bodyText.startsWith("{")) {
                    // Tekil obje olarak dene
                    val entry = json.decodeFromString<RegistryEntry>(bodyText)
                    entry.number == number && entry.active
                } else {
                    false
                }
            } catch (e: Exception) {
                // Eğer parse edilemiyorsa ama status OK ise ve "false" değilse
                bodyText != "false" && bodyText.isNotEmpty()
            }
        }
    }

    suspend fun addRegistryNumber(number: String, ownerName: String): AppResult<Unit> {
        return safeCall {
            val entry = RegistryEntry(
                id = "reg_${getNowMillis()}",
                number = number.trim(),
                ownerName = ownerName.trim(),
                active = true,
                createdAt = getNowMillis()
            )
            val response: HttpResponse = client.post(ApiConfig.REGISTRY_URL) {
                contentType(ContentType.Application.Json)
                setBody(entry)
            }
            if (response.status != HttpStatusCode.OK) throw Exception("API Hatası: ${response.status}")
        }
    }

    suspend fun setRegistryStatus(number: String, active: Boolean): AppResult<Unit> {
        return safeCall {
            client.patch("${ApiConfig.REGISTRY_URL}?number=$number&active=$active")
        }
    }

    suspend fun deleteRegistryEntry(id: String): AppResult<Unit> {
        return safeCall {
            client.delete("${ApiConfig.REGISTRY_URL}?id=$id")
        }
    }

    suspend fun getAllRegistryEntries(): AppResult<List<RegistryEntry>> {
        return safeCall {
            val response: HttpResponse = client.get(ApiConfig.REGISTRY_URL)
            response.body()
        }
    }
}
