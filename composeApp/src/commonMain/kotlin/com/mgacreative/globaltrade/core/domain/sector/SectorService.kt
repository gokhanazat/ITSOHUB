package com.mgacreative.globaltrade.core.domain.sector

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

class SectorService {
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

    suspend fun getSectors(): AppResult<List<Sector>> {
        val res = safeCall {
            val response = client.get(ApiConfig.SECTORS_URL)
            if (response.status == HttpStatusCode.OK) {
                val list = response.body<List<Sector>>()
                println("getSectors SUCCESS: parsed ${list.size} items")
                list
            } else {
                println("getSectors FAIL: ${response.status}")
                emptyList()
            }
        }
        if (res is AppResult.Error) {
            println("getSectors EXCEPTION: ${res.error}")
        }
        return res
    }

    suspend fun saveSector(sector: Sector): AppResult<Unit> = safeCall {
        val finalId = if (sector.id.isBlank()) "sec_${getNowMillis()}" else sector.id
        
        val updatedSector = sector.copy(
            id = finalId,
            name = sector.name.trim(),
            groupNo = sector.groupNo.trim(),
            isActive = true
        )
        
        val response = client.post(ApiConfig.SECTORS_URL) {
            contentType(ContentType.Application.Json)
            setBody(updatedSector)
        }
        
        if (response.status != HttpStatusCode.OK) {
            throw Exception("API Kaydetme Hatası: ${response.status}")
        }
    }

    suspend fun deleteSector(id: String): AppResult<Unit> = safeCall {
        val response = client.delete("${ApiConfig.SECTORS_URL}?id=$id")
        if (response.status != HttpStatusCode.OK) {
            throw Exception("API Silme HatasÄ±: ${response.status}")
        }
    }
}
