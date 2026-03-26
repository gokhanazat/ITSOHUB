package com.mgacreative.globaltrade.core.domain.consultancy

import com.mgacreative.globaltrade.core.network.ApiConfig
import com.mgacreative.globaltrade.core.error.AppResult
import com.mgacreative.globaltrade.core.error.AppError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ConsultantService {
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

    suspend fun getConsultants(): AppResult<List<Consultant>> {
        return try {
            val response: HttpResponse = client.get(ApiConfig.CONSULTANTS_URL)
            if (response.status == HttpStatusCode.OK) {
                val list: List<Consultant> = response.body()
                AppResult.Success(list.sortedBy { it.displayOrder })
            } else {
                AppResult.Success(emptyList())
            }
        } catch (e: Exception) {
            AppResult.Error(AppError.Unknown(e))
        }
    }

    suspend fun addConsultant(consultant: Consultant): AppResult<Unit> {
        return try {
            val response: HttpResponse = client.post(ApiConfig.CONSULTANTS_URL) {
                contentType(ContentType.Application.Json)
                setBody(consultant)
            }
            if (response.status == HttpStatusCode.OK) AppResult.Success(Unit)
            else AppResult.Error(AppError.Unknown(Exception("API HatasÄ±: ${response.status}")))
        } catch (e: Exception) {
            AppResult.Error(AppError.Unknown(e))
        }
    }

    suspend fun updateConsultant(consultant: Consultant): AppResult<Unit> {
        return addConsultant(consultant) // Same endpoint for save/update usually
    }

    suspend fun deleteConsultant(id: String): AppResult<Unit> {
        return try {
            val response: HttpResponse = client.delete("${ApiConfig.CONSULTANTS_URL}?id=$id")
            if (response.status == HttpStatusCode.OK) AppResult.Success(Unit)
            else AppResult.Error(AppError.Unknown(Exception("API HatasÄ±: ${response.status}")))
        } catch (e: Exception) {
            AppResult.Error(AppError.Unknown(e))
        }
    }
}
