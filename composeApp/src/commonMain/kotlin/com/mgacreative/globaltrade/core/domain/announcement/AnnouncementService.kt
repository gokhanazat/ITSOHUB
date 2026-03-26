package com.mgacreative.globaltrade.core.domain.announcement

import com.mgacreative.globaltrade.core.network.ApiConfig
import com.mgacreative.globaltrade.core.error.AppResult
import com.mgacreative.globaltrade.core.error.safeCall
import com.mgacreative.globaltrade.getNowMillis
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class AnnouncementService {
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

    suspend fun getAllAnnouncements(): AppResult<List<Announcement>> {
        val result = safeCall {
            val response = client.get(ApiConfig.ANNOUNCEMENTS_URL)
            if (response.status == HttpStatusCode.OK) {
                val list = response.body<List<Announcement>>()
                println("getAllAnnouncements SUCCESS: parsed ${list.size} items")
                list.sortedByDescending { it.createdAt }
            } else {
                println("getAllAnnouncements FAIL: ${response.status}")
                emptyList()
            }
        }
        if (result is AppResult.Error) {
            println("getAllAnnouncements EXCEPTION: ${result.error}")
        }
        return result
    }

    suspend fun getActiveAnnouncements(): AppResult<List<Announcement>> {
        val allResult = getAllAnnouncements()
        return if (allResult.isSuccess) {
            AppResult.Success(allResult.getOrNull()?.filter { it.isActive } ?: emptyList())
        } else {
            allResult
        }
    }

    suspend fun saveAnnouncement(announcement: Announcement): AppResult<Unit> {
        return safeCall {
            // EÄŸer id boÅŸsa yeni bir tane Ã¼retelim
            val finalAnnouncement = if (announcement.id.isBlank()) {
                announcement.copy(
                    id = "ann_${getNowMillis()}",
                    createdAt = getNowMillis()
                )
            } else {
                announcement
            }

            val response = client.post(ApiConfig.ANNOUNCEMENTS_URL) {
                contentType(ContentType.Application.Json)
                setBody(finalAnnouncement)
            }
            if (response.status != HttpStatusCode.OK) {
                throw Exception("API Hatası: ${response.status}")
            }
        }
    }

    suspend fun deleteAnnouncement(id: String): AppResult<Unit> {
        return safeCall {
            val response = client.delete("${ApiConfig.ANNOUNCEMENTS_URL}?id=$id")
            if (response.status != HttpStatusCode.OK) {
                throw Exception("API Hatası: ${response.status}")
            }
        }
    }

    suspend fun toggleAnnouncementStatus(id: String, active: Boolean): AppResult<Unit> {
        return safeCall {
            val response = client.patch("${ApiConfig.ANNOUNCEMENTS_URL}?id=$id&active=$active")
            if (response.status != HttpStatusCode.OK) {
                throw Exception("API Hatası: ${response.status}")
            }
        }
    }
}
